/*******************************************************************************
 * Copyright (c) 2009 Ola Spjuth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 ******************************************************************************/
package net.bioclipse.metaprint2d.ui;

import java.util.HashMap;
import java.util.Map;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.jchempaint.editor.JChemPaintEditor;
import net.bioclipse.cdk.ui.sdfeditor.editor.MultiPageMoleculesEditorPart;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.metaprint2d.ui.actions.MetaPrint2DHandler;
import net.bioclipse.metaprint2d.ui.business.IMetaPrint2DManager;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.contexts.ContextManagerEvent;
import org.eclipse.core.commands.contexts.IContextManagerListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IPartListener, IContextManagerListener {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.bioclipse.metaprint2d.ui";

	// The shared instance
	private static Activator plugin;

    private ServiceTracker finderTracker;

    private static final Logger logger = Logger.getLogger(Activator.class);

    private static Map<JChemPaintEditor, IPropertyChangeListener> editorListenerMap;
    
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
        finderTracker = new ServiceTracker( context, 
                IMetaPrint2DManager.class.getName(), 
                null );
        finderTracker.open();

		UIJob job = new UIJob("InitCommandsWorkaround") {
		    
		    public IStatus runInUIThread(IProgressMonitor monitor) {
		 
		        ICommandService commandService = (ICommandService) PlatformUI
		            .getWorkbench().getActiveWorkbenchWindow().getService(
		                ICommandService.class);
		        Command command = commandService.getCommand("net.bioclipse.dropdown.radio");
		        command.isEnabled();
		        
		        command = commandService.getCommand("net.bioclipse.dropdown.radio.op");
		        command.isEnabled();
		        
		        if (PlatformUI.getWorkbench()==null) return Status.OK_STATUS;
		        if (PlatformUI.getWorkbench().getActiveWorkbenchWindow()==null) return Status.OK_STATUS;

		        logger.debug("Initializing M2D part and context listeners");
		        editorListenerMap=new HashMap<JChemPaintEditor, IPropertyChangeListener>();
		        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(Activator.getDefault());

		        IContextService contextService = (IContextService)PlatformUI
		                    .getWorkbench().getService(IContextService.class);
		        contextService.addContextManagerListener( Activator.getDefault() );

		        
		        return new Status(IStatus.OK,
		            PLUGIN_ID,
		            "Init commands workaround performed succesfully");
		    }
		 
		};
		job.schedule();
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;

//    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener(Activator.getDefault());
//
//    IContextService contextService = (IContextService)PlatformUI
//                .getWorkbench().getService(IContextService.class);
//    contextService.removeContextManagerListener( Activator.getDefault() );

		
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	
	public IMetaPrint2DManager getMetaPrint2DManager() {

		IMetaPrint2DManager manager = null;
        try {
            manager = (IMetaPrint2DManager) finderTracker.waitForService(1000*10);
        } catch (InterruptedException e) {
            logger.warn("Exception occurred while attempting to get the Metaprint2DManager" + e);
            LogUtils.debugTrace(logger, e);
        }
        if(manager == null) {
            throw new IllegalStateException("Could not get the metaprint2D manager");
        }
        return manager;
	}

    public static Map<JChemPaintEditor, IPropertyChangeListener> getEditorListenerMap() {
        return editorListenerMap;
    }

    /*
     * Below are for removing editor listeners
     */
    
    public void partActivated( IWorkbenchPart part ) {
        fireAutoM2DifTurnedOn();
    }

    public void partBroughtToTop( IWorkbenchPart part ) {
    }

    public void partClosed( IWorkbenchPart part ) {
        if ( part instanceof JChemPaintEditor ) {
            JChemPaintEditor jcp=(JChemPaintEditor)part;
//            System.out.println("M2D Activator says: AN EDITOR WAS CLOSED");
            IPropertyChangeListener listener = editorListenerMap.get( jcp );
            if (listener!=null)
                jcp.removePropertyChangedListener( listener );
            editorListenerMap.remove( part );
        }
    }

    public void partDeactivated( IWorkbenchPart part ) {
    }

    public void partOpened( IWorkbenchPart part ) {
    }

    public void contextManagerChanged( ContextManagerEvent contextManagerEvent ) {

        if (PlatformUI.getWorkbench()==null) return;
        if (PlatformUI.getWorkbench().getActiveWorkbenchWindow()==null) return;
        if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()==null) return;
        if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()==null) return;

        //This code is only to cath the case when switched to JCP page in moltable
        IContextService contextService = (IContextService)PlatformUI
        .getWorkbench().getService(IContextService.class);
        if (contextService.getActiveContextIds().contains( 
                                MultiPageMoleculesEditorPart.JCP_CONTEXT )){
            
            //MolTableEditor switched to tab JCP
//            System.out.println("JCP context activated");
            IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                                             .getActivePage().getActiveEditor();
            if (editor!=null){
                if ( editor instanceof MultiPageMoleculesEditorPart ) {
                    //We have JCP page open in moltable
                    fireAutoM2DifTurnedOn();
                }
            }
        }
        
    }

    public void fireAutoM2DifTurnedOn() {
        

        final IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

        if (editor!=null){

            UIJob job=new UIJob("Running M2D"){

                @Override
                public IStatus runInUIThread( IProgressMonitor monitor ) {

                    MetaPrint2DHandler h = new MetaPrint2DHandler();
                    try {
                        if (h.shouldAutorun()){
                            System.out.println("Fired automatic m2d calculation from Activator");

                            if ( editor instanceof MultiPageMoleculesEditorPart ) {
                                h.executeInMolTableEditor( editor );
                            }
                            else if ( editor instanceof JChemPaintEditor ) {
                                JChemPaintEditor jcp=(JChemPaintEditor)editor;
                                
                                //Remove explicit hydrogens
//                                ICDKMolecule cmol = jcp.getCDKMolecule();
//                                net.bioclipse.cdk.business.Activator.getDefault().getJavaCDKManager().removeExplicitHydrogens( cmol );
                                
                                h.handleChangedListeners(jcp);
                                h.executeInJCP( jcp );
                            }

                        }
                    } catch ( Exception e ) {
//                        LogUtils.handleException( e, logger, PLUGIN_ID);
                    }

                    return Status.OK_STATUS;
                }
                
            };
            job.setUser( false );
            job.schedule(500);
        }

    }        
}
