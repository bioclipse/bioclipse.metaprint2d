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

 import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import net.sf.metaprint2d.MetaPrintResult;


public class MetaPrint2DProperty {

    private static final Logger logger = Logger.getLogger(MetaPrint2DProperty.class);

    List<MetaPrintResult> results;

    public MetaPrint2DProperty(List<MetaPrintResult> results) {

        this.results = results;
    }

    public MetaPrint2DProperty(String serializedForm) {
        parseString(serializedForm);
    }

    public List<MetaPrintResult> getResults() {
    
        return results;
    }
    
    public void setResults( List<MetaPrintResult> results ) {
    
        this.results = results;
    }
    
    @Override
    public String toString() {
    
        String ret="";
        DecimalFormat twoDForm = new DecimalFormat("#.##");

        for (MetaPrintResult res : getResults()){
            int atomno = res.getAtomNumber();
            int subcnt = res.getSubstrateCount();
            int rccnt = res.getReactionCentreCount();
            double rat = res.getNormalisedRatio();
            
            //Round ratio to 2 decimals
            String ratstr=twoDForm.format( rat );

            ret=ret+(atomno + "/" + subcnt + "/" + rccnt + "/" + ratstr + ","  );
        }
        ret=ret.substring( 0, ret.length()-1 );
        
        return ret;
    }
    
    private void parseString( String serializedForm ) {

        results=new ArrayList<MetaPrintResult>();
        double maxOR=0; //Store maximum occurence ratio in the process
        
        //split up entire string by ,
        StringTokenizer tk=new StringTokenizer(serializedForm, ",");
        while ( tk.hasMoreElements() ) {
            String resultstring = (String) tk.nextElement();

            //split up individual results by /
            StringTokenizer restk=new StringTokenizer(resultstring, "/");
            if (restk.countTokens()==4){
                String s_atomno=restk.nextToken();
                String s_subcnt=restk.nextToken();
                String s_rccnt=restk.nextToken();
//                String s_rat=restk.nextToken(); //Unused 4th token
                
                int atomno=Integer.parseInt( s_atomno );
                int subcnt=Integer.parseInt( s_subcnt );
                int rccnt=Integer.parseInt( s_rccnt );
                
                MetaPrintResult res=new MetaPrintResult(atomno, rccnt, subcnt);
                results.add( res );

                if (subcnt > 0) {
                    maxOR = Math.max(maxOR, rccnt / (double) subcnt);
                }

            }
            else{
                logger.debug("Entry: '" + resultstring +"' did not have 4 tokens.");
            }
            
        }
        
        //Set normalization ratio
        for (MetaPrintResult r : results) {
            r.setNormalizedOccurenceRatio( maxOR);
        }
        
    }

    
}
