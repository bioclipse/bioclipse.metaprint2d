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

 import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import net.bioclipse.cdk.domain.CDKMolecule;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.ui.sdfeditor.editor.IRenderer2DConfigurator;
import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.metaprint2d.Metaprinter;
import net.bioclipse.metaprint2d.ui.Activator;
import net.bioclipse.metaprint2d.ui.MetaPrint2DHelper;
import net.bioclipse.metaprint2d.ui.Metaprint2DConstants;
import net.bioclipse.metaprint2d.ui.Metaprint2DPropertyColorer;
import net.bioclipse.metaprint2d.ui.business.IMetaPrint2DManager;
import net.sf.metaprint2d.MetaPrintResult;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.RendererModel.ColorHash;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.AtomColorer;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.AtomRadius;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.ColorByType;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.CompactAtom;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.CompactShape;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.KekuleStructure;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.ShowExplicitHydrogens;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.BackgroundColor;
import org.openscience.cdk.renderer.generators.ExtendedAtomGenerator.ShowAtomTypeNames;
import org.openscience.cdk.renderer.generators.ExtendedAtomGenerator.ShowImplicitHydrogens;

public class MetaPrint2DAtomColorConfigurator implements IRenderer2DConfigurator{

    //The bondcolor in M2d results
    Color bondcolor=new Color(33,33,33);

    /**
     * Add tooltip read from M2D property
     */
    public void configure(RendererModel model, IAtomContainer ac) {

        //Get the managers via OSGI
        IMetaPrint2DManager m2d = Activator.getDefault().getMetaPrint2DManager();

        //Calculate M2D and store as property
        ICDKMolecule cdkmol=new CDKMolecule(ac);
        try {
            m2d.calculate( cdkmol, true );
            if (!(ac.equals( cdkmol.getAtomContainer() ))){
                ac=cdkmol.getAtomContainer();
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            return;
        }
        
        //Read M2D property from ac
        String acprop=(String)ac.getProperty( Metaprint2DConstants.METAPRINT_RESULT_PROPERTY );
        
        if (acprop==null || acprop.length()<=0){
            System.out.println("No M2D property found for molecule. No M2D tooltip to have.");
            return;
        }
        
        List<MetaPrintResult> reslist = MetaPrint2DHelper.getResultFromProperty( acprop );

        //Store tooltips Atom -> String
        HashMap<IAtom, String> currentToolTip=new HashMap<IAtom, String>();

        //Start by coloring all grey
//        for (int i=0; i< ac.getAtomCount(); i++){
//            ac.getAtom( i ).setProperty( Metaprint2DConstants.COLOR_PROPERTY, Metaprinter.BLACK_COLOR );
//        }

        //Add tooltip for atoms with result
        for (MetaPrintResult res : reslist){

            StringWriter sw=new StringWriter();
            PrintWriter pw=new PrintWriter(sw);
            pw.printf( "%d/%d %1.2f" , res.getReactionCentreCount(), res.getSubstrateCount(), res.getNormalisedRatio());
            String tt=sw.getBuffer().toString();
            currentToolTip.put( ac.getAtom( res.getAtomNumber() ), tt );

        }

        //Configure JCP
        IAtomColorer c = new IAtomColorer() {
			@Override
			public Color getAtomColor(IAtom atom, Color defaultColor) {
				return Color.BLACK;
			}
			@Override
			public Color getAtomColor(IAtom atom) {
				return Color.BLACK;
			}
		};

        model.set(AtomColorer.class,c);
        model.set(ColorByType.class,true);

        model.set( CompactAtom.class ,true );
        model.set( ShowAtomTypeNames.class, false );
        model.set( ShowImplicitHydrogens.class, false);
        model.set( ShowExplicitHydrogens.class, false);
        model.setToolTipTextMap( currentToolTip );
               model.set( AtomRadius.class , 0.0 );
        //       model.setCompactShape(RenderingParameters.AtomShape.OVAL);

        //Update drawing
        model.fireChange();

    }


}
