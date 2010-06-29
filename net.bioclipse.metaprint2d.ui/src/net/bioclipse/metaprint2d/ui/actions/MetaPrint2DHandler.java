/* *****************************************************************************
 * Copyright (c) 2009 Ola Spjuth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 ******************************************************************************/
package net.bioclipse.metaprint2d.ui.actions;

 import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import net.sf.metaprint2d.MetaPrintResult;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.jchempaint.editor.JChemPaintEditor;
import net.bioclipse.cdk.jchempaint.view.JChemPaintView;
import net.bioclipse.cdk.ui.sdfeditor.editor.MoleculesEditor;
import net.bioclipse.cdk.ui.sdfeditor.editor.MultiPageMoleculesEditorPart;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.metaprint2d.Metaprinter;
import net.bioclipse.metaprint2d.ui.Activator;
import net.bioclipse.metaprint2d.ui.MetaPrintGenerator;
import net.bioclipse.metaprint2d.ui.Metaprint2DConstants;
import net.bioclipse.metaprint2d.ui.business.IMetaPrint2DManager;
import net.bioclipse.metaprint2d.ui.model.MetaPrint2DCalculation;
import net.bioclipse.metaprint2d.ui.views.MetaPrint2DReportView;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;

public class MetaPrint2DHandler extends AbstractHandler {

    private static final Object TOGGLE_COMMAND_ID = "net.bioclipse.metaprint2d.toggleCommand";
    private static boolean shouldAutorun=false;

    Logger logger = Logger.getLogger( MetaPrint2DHandler.class );
    
    public boolean shouldAutorun(){return shouldAutorun;};

	public Object execute(ExecutionEvent event) throws ExecutionException {

    	//Make sure we are called from a supported editor
        IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
        .getActivePage().getActiveEditor();

        if (event.getCommand().getId().equals( TOGGLE_COMMAND_ID )){
            shouldAutorun = !shouldAutorun;
            if (!shouldAutorun)
                return null;
        }
        
        MetaPrintGenerator.setVisible(true);

        if (part instanceof net.bioclipse.cdk.ui.sdfeditor.editor.MultiPageMoleculesEditorPart) {
            return executeInMolTableEditor(part);
        }
        else if ( part instanceof JChemPaintEditor ) {
            JChemPaintEditor jcp=(JChemPaintEditor)part;
            handleChangedListeners(jcp);
            return executeInJCP(jcp);
        }


        //Not supported, just finish (should not happen)
        logger.error("M2D called from unsupported editor: " + part);
		return null;

	} 
	
        
	public void handleChangedListeners( JChemPaintEditor jcp ) {

      //First, try to look up in map
      IPropertyChangeListener jcplistener = Activator.getEditorListenerMap().get( jcp);
      
      //If not found in map, create a new
      if (jcplistener==null){

          jcplistener = new IPropertyChangeListener() {
              public void propertyChange( PropertyChangeEvent event ) {

                  if(event.getProperty().equals( JChemPaintEditor.
                                                 STRUCUTRE_CHANGED_EVENT )) {

                      // editor model has changed
                      // do stuff...
                      logger.debug
                      ("M2D reacting: JCP editor model has changed");
                      
                      IEditorPart editor=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
                      if (editor==null)
                          return;
                      
                      JChemPaintEditor activejcp = null;

                      //Check if JCP is active editor
                      if ( editor instanceof JChemPaintEditor ){
                          activejcp = (JChemPaintEditor) editor;
                      }
                      
                      //Check if Moltable editor is active, if so, extract jcp if visible
                      else if ( editor instanceof MultiPageMoleculesEditorPart ) {

                          IContextService contextService = (IContextService) PlatformUI.getWorkbench().
                          getService(IContextService.class);

                          for (Object cs : contextService.getActiveContextIds()){
                              if (MultiPageMoleculesEditorPart.JCP_CONTEXT.equals( cs )){
                                  Object obj = ((MultiPageMoleculesEditorPart)
                                          editor)
                                          .getAdapter(JChemPaintEditor.class);
                                  if (obj!= null){
                                      activejcp=(JChemPaintEditor)obj;
                                  }
                              }
                          }
                      }
                          
                      if ( activejcp!=null ) {
                          JChemPaintEditor jcp = (JChemPaintEditor) event.getSource();

                          //Return if this event is not triggered for the active editor
                          if (jcp != activejcp)
                              return;

                          logger.debug("listenr found AC: " + jcp.getCDKMolecule().getAtomContainer());
                          
                          if (shouldAutorun){
                              try {
                                executeInJCP( jcp );
                            } catch ( ExecutionException e ) {
                            }
                          }
                          else if (jcp.getCDKMolecule().getAtomContainer().getProperty(
                                                                                  Metaprint2DConstants.METAPRINT_RESULT_PROPERTY )
                                                                                  !=null){
                              jcp.getCDKMolecule().getAtomContainer().setProperty(
                                                                                  Metaprint2DConstants.METAPRINT_RESULT_PROPERTY, "");
                              logger.debug("Cleared jcp m2d prop");
                              jcp.update();    
                          }

                      }
                  }
              }
          };

          jcp.addPropertyChangedListener(jcplistener);
          Activator.getEditorListenerMap().put( jcp, jcplistener );
      }
    }


    public Object executeInJCP(JChemPaintEditor jcp) throws ExecutionException {

        //Get the managers via OSGI
        IMetaPrint2DManager m2d = Activator.getDefault().getMetaPrint2DManager();
//        ICDKManager cdk = net.bioclipse.cdk.business.Activator.getDefault().getJavaCDKManager();

        //Time m2d execution
        Stopwatch watch=new Stopwatch();
        watch.start();
		
        
        //Remove explicit hydrogens
//        net.bioclipse.cdk.business.Activator.getDefault().getJavaCDKManager().removeExplicitHydrogens( cdkmol );
        net.bioclipse.cdk.jchempaint.Activator.getDefault().getJavaManager().removeExplicitHydrogens();
//        net.bioclipse.cdk.jchempaint.Activator.getDefault().getJavaManager().updateImplicitHydrogenCounts();
        

        ICDKMolecule cdkmol=jcp.getCDKMolecule();
//      logger.debug("Execute found AC: " + jcp.getCDKMolecule().getAtomContainer());


        //Get previous result from m2d in a map editor -> m2dcalculation
        //If this is first, create entry in map for this editor
        List<MetaPrintResult> scores;
        MetaPrint2DCalculation calc=null;
        if (m2d.getCalculationMap().containsKey(jcp)){
        	calc=m2d.getCalculationMap().get(jcp);
        }else{
        	calc=new MetaPrint2DCalculation();
        	m2d.getCalculationMap().put((IEditorPart) jcp, calc);
        }

        //===========
        //Predict m2d
        //===========
        try {
            scores = m2d.calculate(cdkmol,true);

            setTooltips(jcp, scores);

            calc.setStatus("OK");

        } catch (BioclipseException e) {
            e.printStackTrace();
	        calc.setStatus(e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
	        calc.setStatus(e.getMessage());
		}
        
        watch.stop();
        calc.setCalculationTime(watch.toString());
        calc.setDatabase(Metaprinter.getSpecies().name());
        calc.setOperator(Metaprinter.getOperator());

        //Turn on contributed generators
        jcp.getWidget().setUseExtensionGenerators( true );

        //manually update jcpeditor
        jcp.update();

        //Update linked views
        updateLinkedViews();
        
        return null;
	}


    private void updateLinkedViews() {

        //Manually refresh m2d report view
        IViewPart reportView = PlatformUI.getWorkbench().
        	                     getActiveWorkbenchWindow().getActivePage().
        	                     findView(MetaPrint2DReportView.VIEW_ID);
        if (reportView!=null)
            ((MetaPrint2DReportView)reportView).refresh();
                
        //Manually refresh 2D view as well
        IViewPart jcpview = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                .getActivePage().findView( JChemPaintView.VIEW_ID );
        if (jcpview!=null)
            ((JChemPaintView)jcpview).refresh();
    }
	


	/**
	 * M2d is executed from e.g. SDFEditor
	 * @param part a MultiPageMoleculesEditorPart
	 * @return null, always
	 * @throws ExecutionException if errors
	 */
	public Object executeInMolTableEditor(IEditorPart part) throws ExecutionException {

		MultiPageMoleculesEditorPart moltableTditor=(MultiPageMoleculesEditorPart)part;

		IContextService contextService = (IContextService) PlatformUI.getWorkbench().
		                                          getService(IContextService.class);
		if (contextService==null) return null;
		
		for (Object cs : contextService.getActiveContextIds()){
		    if (MultiPageMoleculesEditorPart.JCP_CONTEXT.equals( cs )){
		        //JCP is active
		        Object obj = moltableTditor.getAdapter(JChemPaintEditor.class);
		        if (obj!= null){
		            JChemPaintEditor jcp=(JChemPaintEditor)obj;
		            handleChangedListeners(jcp);
		            return executeInJCP( jcp );
		        }
		    }
		    
		}
		
		//Moltable is active
		MoleculesEditor molEditor=moltableTditor.getMoleculesPage();
		
		molEditor.setUseExtensionGenerators( true );

		//Add configurator that calculates property and customizes rendering
		molEditor.setRenderer2DConfigurator( new MetaPrint2DAtomColorConfigurator() );

		//Manually update the moltable
		molEditor.refresh();
		
    //Update linked views
    updateLinkedViews();
		
		return null;
	}

	
	 private void setTooltips(JChemPaintEditor jcpeditor, List<MetaPrintResult> scores) {
	     
      RendererModel model=jcpeditor.getWidget().getRenderer2DModel();            
      IAtomContainer ac = jcpeditor.getCDKMolecule().getAtomContainer();

      //Store tooltips Atom -> String
      HashMap<IAtom, String> currentToolTip=new HashMap<IAtom, String>();
      
      //Add tooltip for atoms with result
      for (MetaPrintResult res : scores){

          StringWriter sw=new StringWriter();
          PrintWriter pw=new PrintWriter(sw);
          pw.printf( "%d/%d %1.2f" , res.getReactionCentreCount(), res.getSubstrateCount(), res.getNormalisedRatio());
          String tt=sw.getBuffer().toString();
          currentToolTip.put( ac.getAtom( res.getAtomNumber() ), tt );

      }

      //Configure JCP
      model.setToolTipTextMap( currentToolTip );
    
}

	 /**
	  * Stopwatch to time M2D execution
	  * @author ola
	  *
	  */
    public class Stopwatch {
        private long start;
        private long stop;
        
        public void start() {
            start = System.currentTimeMillis(); // start timing
        }
        
        public void stop() {
            stop = System.currentTimeMillis(); // stop timing
        }
        
        public long elapsedTimeMillis() {
            return stop - start;
        }

        //return number of miliseconds
        public String toString() {
            return "" + elapsedTimeMillis() + " ms"; // print execution time
        }
    }


}
