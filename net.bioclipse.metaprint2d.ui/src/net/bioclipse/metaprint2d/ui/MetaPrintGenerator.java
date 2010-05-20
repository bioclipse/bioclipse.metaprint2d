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
package net.bioclipse.metaprint2d.ui;

 import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.metaprint2d.MetaPrintResult;
import net.bioclipse.metaprint2d.ui.prefs.MetaprintPrefs;

import org.eclipse.jface.preference.IPreferenceStore;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.IRenderingVisitor;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.CompactAtom;
import org.openscience.cdk.renderer.generators.BasicBondGenerator.BondWidth;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;


public class MetaPrintGenerator implements IGenerator {

    public static class MetaPrintVisibleParameter extends
    AbstractGeneratorParameter<Boolean> {
        public Boolean getDefault() {
            return true;
        }
    }
    private IGeneratorParameter<Boolean> visibleGenerator= new MetaPrintVisibleParameter();

    
    public MetaPrintGenerator() {

    }
    
    private static final IRenderingElement emptyElement = 
        new IRenderingElement() {
        @Override
        public void accept( IRenderingVisitor v ) {
        }
    };
    
    /**
     * Set up the colored M2D circles based on calculated properties
     */
    public IRenderingElement generate( IAtomContainer ac,
                                       RendererModel model ) {

//        System.out.println("Generator found AC: " + ac);
        
        if (!visibleGenerator.getValue())
            return emptyElement;

        ElementGroup group = new ElementGroup();
        Object o = ac.getProperty( Metaprint2DConstants.METAPRINT_RESULT_PROPERTY );
        if (o==null) return group;
        
        //Read prefs for rendering params and compute real values
        IPreferenceStore store=Activator.getDefault().getPreferenceStore();
        boolean renderSolid=store.getBoolean(MetaprintPrefs.RENDER_SOLID_CIRCLES );
        int circleRadiusPref = store.getInt( MetaprintPrefs.CIRCLE_RADIUS );
        double circleRadius=(double)circleRadiusPref / 10;
        if (circleRadius<=0 || circleRadius >1)
            circleRadius=0.4;

        //OLD
//        Map<Integer, Double> mrlist = MetaPrint2DHelper
//            .getNormResultsFromProperty( (String)o );
        
        Map<Integer, MetaPrintResult> mresmap = MetaPrint2DHelper
            .getMetaprintResultsFromProperty( (String)o );


        for(int i = 0;i<ac.getAtomCount();i++) {  //Loop over all atoms
            for (Integer ii : mresmap.keySet()){   //Loop over list of atom indices with M2D results
                if (ii.intValue()==i){
                    IAtom atom = ac.getAtom( i );
                    
                    MetaPrintResult mres=mresmap.get( ii );

                    Color drawColor=MetaPrint2DHelper
                    .getColorByNormValue( mres );

                    if(drawColor != null){
                        if (model.getRenderingParameter( CompactAtom.class ).getValue()
                                        || renderSolid){
                            group.add( new OvalElement( atom.getPoint2d().x,
                                                        atom.getPoint2d().y,
                                                        circleRadius,true, drawColor ));
//                            group.add( new OvalElement( atom.getPoint2d().x,
//                                                        atom.getPoint2d().y,
//                                                        .4,true, drawColor ));
                        }
                        else{
                            
                            group.add( new OvalElement( atom.getPoint2d().x,
                                                        atom.getPoint2d().y,
                                                        circleRadius,false, drawColor ));
                            group.add( new OvalElement( atom.getPoint2d().x,
                                                        atom.getPoint2d().y,
                                                        circleRadius+0.002,false, drawColor ));
//                            group.add( new OvalElement( atom.getPoint2d().x,
//                                                        atom.getPoint2d().y,
//                                                        .4,false, drawColor ));
//                            group.add( new OvalElement( atom.getPoint2d().x,
//                                                        atom.getPoint2d().y,
//                                                        .402,false, drawColor ));
                        }
                    }
                }
            }
        }

        
        return group;
    }

    public List<IGeneratorParameter<?>> getParameters() {
        List<IGeneratorParameter<?>> lst=new ArrayList<IGeneratorParameter<?>>();
        lst.add(visibleGenerator);
        return lst;
    }
}
