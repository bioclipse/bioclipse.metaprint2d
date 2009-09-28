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

 import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;

import com.sun.corba.se.impl.oa.poa.AOMEntry;

import net.bioclipse.metaprint2d.ui.prefs.MetaprintPrefs;
import net.sf.metaprint2d.MetaPrintResult;


public class MetaPrint2DHelper {

    public static List<MetaPrintResult> getResultFromProperty(String propString){

        List<MetaPrintResult> reslist=new ArrayList<MetaPrintResult>();
        
        
//      > <Metaprint2D NOR>
//      [(0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0),
    //        (0/9;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/1;0.0),
    //        (0/0;0.0), (16/44;1.0)]
        Pattern metaPrint = Pattern.compile(
        "\\(([^/]+?)/([^;]+?);([^)]+?)\\),?");
        Matcher matcher = metaPrint.matcher( propString );

        int atomno=0;  //Must be one per mol, start at 0
        while(matcher.find()) {
            
            int rc = java.lang.Integer.valueOf( matcher.group(1) );
            int sc = java.lang.Integer.valueOf( matcher.group(2) );
//            int norm = java.lang.Double.valueOf( matcher.group(3) );
            if (rc>0){
                MetaPrintResult mr= new MetaPrintResult(atomno,rc,sc);
                reslist.add( mr );
            }
            atomno++;

        }        
        return reslist;
    }
    
    public static Map<Integer, Double> getNormResultsFromProperty(String propString){

//        Atom no -> Norm Value
        Map<Integer, Double> reslist=new HashMap<Integer, Double>();
        
        
//      > <Metaprint2D NOR>
//      [(0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0),
    //        (0/9;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/1;0.0),
    //        (0/0;0.0), (16/44;1.0)]
        Pattern metaPrint = Pattern.compile(
        "\\(([^/]+?)/([^;]+?);([^)]+?)\\),?");
        Matcher matcher = metaPrint.matcher( propString );

        int atomno=0; //Must be one per mol, start at 0
        while(matcher.find()) {
            Double norm = java.lang.Double.valueOf( matcher.group(3) );
            
            if (norm>0){
                reslist.put( atomno, norm );
            }
            atomno++;
            
        }        
        return reslist;
    }
    
    public static Map<Integer, MetaPrintResult> getMetaprintResultsFromProperty(String propString){

//      Atom no -> Norm Value
      Map<Integer, MetaPrintResult> reslist=new HashMap<Integer, MetaPrintResult>();
      
      
//    > <Metaprint2D NOR>
//    [(0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0),
  //        (0/9;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/1;0.0),
  //        (0/0;0.0), (16/44;1.0)]
      Pattern metaPrint = Pattern.compile(
      "\\(([^/]+?)/([^;]+?);([^)]+?)\\),?");
      Matcher matcher = metaPrint.matcher( propString );

      int atomno=0; //Must be one per mol, start at 0
      while(matcher.find()) {
          int rcnt = java.lang.Integer.valueOf( matcher.group(1) );
          int scnt = java.lang.Integer.valueOf( matcher.group(2) );
          Double norm = java.lang.Double.valueOf( matcher.group(3) );
          
          MetaPrintResult mres=new MetaPrintResult(atomno, rcnt, scnt);
          mres.setNormalizedOccurenceRatio( norm );

          reslist.put( atomno, mres);
          atomno++;
          
      }        
      return reslist;
  }
  
    
    public static Color getColorByNormValue(Double normval){

        if (normval==null) return null;
        
        if (normval>=0.15 && normval<0.33){
            return Color.GREEN;
        }
        else if (normval>=0.33 && normval<0.66){
            return Color.ORANGE;
        }
        else if (normval>=0.66){
            return Color.RED;
        }
        
        return null;
    }

    public static Color getColorByNormValue( MetaPrintResult mres ) {

        IPreferenceStore store=Activator.getDefault().getPreferenceStore();
        boolean renderMissingGrey = 
                          store.getBoolean(MetaprintPrefs.RENDER_MISSING_GREY );
        
        if (renderMissingGrey){
            if (mres.getSubstrateCount()==0)
                return Color.LIGHT_GRAY;
        }else{
            if (mres.getSubstrateCount()==0)
                return null;
        }

        double normval = mres.getNormalisedRatio();
        
        if (normval>=0.15 && normval<0.33){
            return Color.GREEN;
        }
        else if (normval>=0.33 && normval<0.66){
            return Color.ORANGE;
        }
        else if (normval>=0.66){
            return Color.RED;
        }
        
        return null;

    }

}
