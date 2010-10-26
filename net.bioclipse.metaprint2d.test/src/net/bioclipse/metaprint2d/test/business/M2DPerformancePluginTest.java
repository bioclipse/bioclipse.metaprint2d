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
package net.bioclipse.metaprint2d.test.business;

 import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import net.sf.metaprint2d.MetaPrintResult;
import net.bioclipse.cdk.business.Activator;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdkdebug.business.ICDKDebugManager;
import net.bioclipse.core.api.BioclipseException;
import net.bioclipse.core.api.domain.IMolecule;
import net.bioclipse.metaprint2d.ui.business.IMetaPrint2DManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;


public class M2DPerformancePluginTest {

    ICDKManager cdk=Activator.getDefault().getJavaCDKManager();
    ICDKDebugManager cdkdebug=net.bioclipse.cdkdebug.Activator.getDefault().getJavaManager();
    IMetaPrint2DManager m2d = net.bioclipse.metaprint2d.ui.Activator.getDefault().getMetaPrint2DManager();


    /*====================================================
     * Test Read All Data Files
      ====================================================*/
    @Test
    public void TestReadDatabases() throws BioclipseException, InvocationTargetException{

        //A simple molecule to test with
        ICDKMolecule mol=cdk.fromSMILES( "C1CCCCC1CCCCOCCC" );
        
        m2d.setDataBase("DOG");
        String currentdb=m2d.getActiveDatabase();
        assertEquals( "DOG", currentdb );
        List<MetaPrintResult> res = m2d.calculate( mol );
        assertEquals( 14, res.size() );
        System.out.print("DB Dog: ");
        for (MetaPrintResult r : res){
          System.out.print(r + ",");
        }
//        DB Dog:(66/744;0.2057309540150995),(252/8022;0.07285284616241505),(176/5479;0.07449720984959983),(252/8022;0.07285284616241505),(66/744;0.2057309540150995),(0/37;0.0),(26/487;0.12381493293721875),(131/5197;0.05845843960713832),(16/239;0.15525683254695985),(65/222;0.6790300939237109),(11/123;0.2074035633973361),(47/109;1.0),(0/1;0.0),(3/23;0.30249768732654947),INFO - Opening data file: /Users/ola/Workspaces/bioclipse2_1/net.bioclipse.metaprint2d/data/metab2008.1-all.bin

        
        m2d.setDataBase("all");
        currentdb=m2d.getActiveDatabase();
        assertEquals( "ALL", currentdb );
        res = m2d.calculate( mol );
        assertEquals( 14, res.size() );
        System.out.print("DB All: ");
        for (MetaPrintResult r : res){
          System.out.print(r + ",");
        }
//        DB All:(66/744;0.2057309540150995),(252/8022;0.07285284616241505),(176/5479;0.07449720984959983),(252/8022;0.07285284616241505),(66/744;0.2057309540150995),(0/37;0.0),(26/487;0.12381493293721875),(131/5197;0.05845843960713832),(16/239;0.15525683254695985),(65/222;0.6790300939237109),(11/123;0.2074035633973361),(47/109;1.0),(0/1;0.0),(3/23;0.30249768732654947),INFO - Opening data file: /Users/ola/Workspaces/bioclipse2_1/net.bioclipse.metaprint2d/data/metab2008.1-rat.bin

        m2d.setDataBase("rat");
        currentdb=m2d.getActiveDatabase();
        assertEquals( "RAT", currentdb );
        res = m2d.calculate( mol );
        assertEquals( 14, res.size() );
        System.out.print("DB Rat: ");
        for (MetaPrintResult r : res){
          System.out.print(r + ",");
        }
//        DB RAT:(36/461;0.2532044961546046),(139/4130;0.10912759556827355),(96/2859;0.10887468599955485),(139/4130;0.10912759556827355),(36/461;0.2532044961546046),(0/28;0.0),(15/255;0.19073083778966132),(64/2697;0.076942955697127),(3/134;0.07259158751696065),(33/107;1.0000000000000002),(4/68;0.19073083778966132),(18/59;0.9892141756548537),(0/0;0.0),(0/8;0.0),INFO - Opening data file: /Users/ola/Workspaces/bioclipse2_1/net.bioclipse.metaprint2d/data/metab2008.1-human.bin

        m2d.setDataBase("Human");
        currentdb=m2d.getActiveDatabase();
        assertEquals( "HUMAN", currentdb );
        res = m2d.calculate( mol );
        assertEquals( 14, res.size() );
        System.out.print("DB Human: ");
        for (MetaPrintResult r : res){
          System.out.print(r + ",");
        }
//        DB HUMAN:(35/397;0.17024233475201944),(74/3133;0.04561013460712988),(47/2006;0.045243579606009554),(74/3133;0.04561013460712988),(35/397;0.17024233475201944),(0/11;0.0),(11/263;0.08076570079979022),(45/2090;0.04157729747566408),(13/93;0.26992955135335556),(37/104;0.6870026525198938),(10/57;0.338777979431337),(29/56;0.9999999999999999),(0/1;0.0),(3/18;0.32183908045977005),INFO - Stopping org.springframework.osgi.extender bundle


    }

    @Test
    public void Test5molsSDF() throws URISyntaxException, MalformedURLException, IOException, BioclipseException, CoreException, InvocationTargetException, CDKException{

        m2d.setDataBase("all");

        URI uri = getClass().getResource("/testFiles/dbsmallconf.sdf").toURI();
        URL url=FileLocator.toFileURL(uri.toURL());
        String path=url.getFile();

        Map<IMolecule, List<MetaPrintResult>> res = m2d.calculate(path,false);

        assertEquals(5, res.keySet().size());

    }

    @Test
    public void Test232molsSDF() throws BioclipseException, InvocationTargetException, URISyntaxException, MalformedURLException, IOException, CoreException, CDKException{

        m2d.setDataBase("all");

        int numTimes=2;
        
        URI uri = getClass().getResource("/testFiles/m2d_ref_232.sdf").toURI();
        URL url=FileLocator.toFileURL(uri.toURL());
        String path=url.getFile();

//        int totalatoms=0;
//        List<ICDKMolecule> lst = cdk.loadMolecules( path );
//        for (ICDKMolecule mol : lst){
//            totalatoms=totalatoms+mol.getAtomContainer().getAtomCount();
//        }
//        
//        System.out.println("The file: " + path + " contained " + lst.size() 
//                           + " molecules with " + totalatoms + " atoms in total, " +
//                           		"which is " + totalatoms/lst.size() 
//                           		+ " atoms on average.");

        Stopwatch sw=new Stopwatch();
        sw.start();
        for (int i=0; i<numTimes;i++){
            Map<IMolecule, List<MetaPrintResult>> res = m2d.calculate(path,false);
        }
        sw.stop();

        System.out.println("** Loading and predicting M2D 232 from SDF " + numTimes + " times took: " 
                           + sw.toString() + " which is " 
                           + sw.elapsedTimeMillis()/(numTimes*232) 
                           + " ms per molecule");


    }


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
