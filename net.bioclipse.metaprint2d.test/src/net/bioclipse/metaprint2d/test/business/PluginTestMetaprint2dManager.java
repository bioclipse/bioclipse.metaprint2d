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
package net.bioclipse.metaprint2d.test.business;

 import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.metaprint2d.MetaPrintResult;
import net.sf.metaprint2d.mol2.Atom;
import net.sf.metaprint2d.mol2.Molecule;
import net.bioclipse.cdk.business.Activator;
import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.CDKMolecule;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdkdebug.business.CDKDebugManager;
import net.bioclipse.cdkdebug.business.ICDKDebugManager;
import net.bioclipse.core.MockIFile;
import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.metaprint2d.ui.Metaprint2DConstants;
import net.bioclipse.metaprint2d.ui.business.IMetaPrint2DManager;
import net.bioclipse.metaprint2d.ui.business.MetaPrint2DManager;
import net.bioclipse.metaprint2d.ui.cdk.CDK2MetaprintConverter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.MoleculeFactory;

public class PluginTestMetaprint2dManager {

	ICDKManager cdk=Activator.getDefault().getJavaCDKManager();
	CDKDebugManager cdkdebug=new CDKDebugManager();
	IMetaPrint2DManager m2d = new MetaPrint2DManager(cdk);


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
//      DB Dog:(66/744;0.2057309540150995),(252/8022;0.07285284616241505),(176/5479;0.07449720984959983),(252/8022;0.07285284616241505),(66/744;0.2057309540150995),(0/37;0.0),(26/487;0.12381493293721875),(131/5197;0.05845843960713832),(16/239;0.15525683254695985),(65/222;0.6790300939237109),(11/123;0.2074035633973361),(47/109;1.0),(0/1;0.0),(3/23;0.30249768732654947),INFO - Opening data file: /Users/ola/Workspaces/bioclipse2_1/net.bioclipse.metaprint2d/data/metab2008.1-all.bin

      
      m2d.setDataBase("all");
      currentdb=m2d.getActiveDatabase();
      assertEquals( "ALL", currentdb );
      res = m2d.calculate( mol );
      assertEquals( 14, res.size() );
      System.out.print("DB All: ");
      for (MetaPrintResult r : res){
        System.out.print(r + ",");
      }
//      DB All:(66/744;0.2057309540150995),(252/8022;0.07285284616241505),(176/5479;0.07449720984959983),(252/8022;0.07285284616241505),(66/744;0.2057309540150995),(0/37;0.0),(26/487;0.12381493293721875),(131/5197;0.05845843960713832),(16/239;0.15525683254695985),(65/222;0.6790300939237109),(11/123;0.2074035633973361),(47/109;1.0),(0/1;0.0),(3/23;0.30249768732654947),INFO - Opening data file: /Users/ola/Workspaces/bioclipse2_1/net.bioclipse.metaprint2d/data/metab2008.1-rat.bin

      m2d.setDataBase("rat");
      currentdb=m2d.getActiveDatabase();
      assertEquals( "RAT", currentdb );
      res = m2d.calculate( mol );
      assertEquals( 14, res.size() );
      System.out.print("DB Rat: ");
      for (MetaPrintResult r : res){
        System.out.print(r + ",");
      }
//      DB RAT:(36/461;0.2532044961546046),(139/4130;0.10912759556827355),(96/2859;0.10887468599955485),(139/4130;0.10912759556827355),(36/461;0.2532044961546046),(0/28;0.0),(15/255;0.19073083778966132),(64/2697;0.076942955697127),(3/134;0.07259158751696065),(33/107;1.0000000000000002),(4/68;0.19073083778966132),(18/59;0.9892141756548537),(0/0;0.0),(0/8;0.0),INFO - Opening data file: /Users/ola/Workspaces/bioclipse2_1/net.bioclipse.metaprint2d/data/metab2008.1-human.bin

      m2d.setDataBase("Human");
      currentdb=m2d.getActiveDatabase();
      assertEquals( "HUMAN", currentdb );
      res = m2d.calculate( mol );
      assertEquals( 14, res.size() );
      System.out.print("DB Human: ");
      for (MetaPrintResult r : res){
        System.out.print(r + ",");
      }
//      DB HUMAN:(35/397;0.17024233475201944),(74/3133;0.04561013460712988),(47/2006;0.045243579606009554),(74/3133;0.04561013460712988),(35/397;0.17024233475201944),(0/11;0.0),(11/263;0.08076570079979022),(45/2090;0.04157729747566408),(13/93;0.26992955135335556),(37/104;0.6870026525198938),(10/57;0.338777979431337),(29/56;0.9999999999999999),(0/1;0.0),(3/18;0.32183908045977005),INFO - Stopping org.springframework.osgi.extender bundle


  }

	
	 /*====================================================
   * Test Sybyl Atom Typing
    ====================================================*/

	
	@Test
	public void TestMetaprint2DAtomTyping() throws BioclipseException, InvocationTargetException, IOException, CoreException, CDKException{

		IAtomContainer ac = MoleculeFactory.makeBenzene();
		ICDKMolecule mol=new CDKMolecule(ac);
    	
		ICDKMolecule cdkMol=cdk.asCDKMolecule( mol);
		Molecule metamol=CDK2MetaprintConverter.getMetaprintMoleculeWithSybylTypesNew(cdkMol);

	    /*
		"C.3","C.2","C.ar","C.1","N.3", "N.2","N.1","O.3","O.2","S.3",
		"N.ar","P.3","H","Br","Cl", "F","I","S.2","N.pl3","LP",
		"Na","K","Ca","Li","Al", "Du","Si","N.am","S.O","S.O2",
	    "N.4","O.CO2","C.cat" */

	    //Assert all aromatic, as benzene, with 2 neighbours
	    for (Atom atom : metamol.getAtoms()){
		    assertEquals(2, atom.getAtomType());
//		    assertEquals(Constants.ATOM_TYPE_INDEX.get("C.ar"), atom.getAtomType());
		    assertEquals(2, atom.getNeighbours().size());
	    }
	    
	    //Test neighbours
	    assertNotNull(metamol.getAtom(0).getNeighbours().get(0));
	    assertNotNull(metamol.getAtom(0).getNeighbours().get(1));

	}

	/*====================================================
	 * Test MetaPrint2DCaclculations
	  ====================================================*/
	
	@Test
	public void TestCalculateBenzene() throws BioclipseException, InvocationTargetException, IOException, CoreException{

		IAtomContainer ac = MoleculeFactory.makeBenzene();
		ICDKMolecule mol=new CDKMolecule(ac);

		List<MetaPrintResult> ress = m2d.calculate(mol);
		assertEquals(6, ress.size());

		System.out.println("Result:");
		for (MetaPrintResult res : ress){
			System.out.println("  " + res);
		}

	}

	@Test
	public void TestCalculateMol() throws BioclipseException, InvocationTargetException, IOException, CoreException, URISyntaxException{

	    URI uri = getClass().getResource("/testFiles/atp.mol").toURI();
	    URL url=FileLocator.toFileURL(uri.toURL());
	    String path=url.getFile();
	    ICDKMolecule mol = cdk.loadMolecule( path);

	    //Calculate on already perceiveed sybyl types
	    cdkdebug.perceiveSybylAtomTypes(mol);
	    List<MetaPrintResult> res = m2d.calculate(mol);
	    assertEquals(31, res.size());

	    //Calculate on non-sybyl types
	    List<MetaPrintResult> res2 = m2d.calculate(mol);
	    assertEquals(31, res2.size());

	    String fullpath=FileLocator.toFileURL(getClass().getResource("/testFiles/atp.mol")).getFile();

	    //Calculate on path
	    Map<IMolecule, List<MetaPrintResult>> res3 = m2d.calculate(new MockIFile(fullpath));
	    assertEquals(1, res3.keySet().size());
	    List<MetaPrintResult> im = res3.get(res3.keySet().toArray()[0]);
	    assertEquals(31, im.size());

	    //Test that props are not stored
	    for (IAtom atom : mol.getAtomContainer().atoms()){
	        String m2dprop=(String) atom.getProperty(Metaprint2DConstants.METAPRINT_RESULT_PROPERTY);
	        assertNull(m2dprop);
	    }

	}
	
	@Test
	public void TestCalculateMolStorePropsInMemory() throws BioclipseException, InvocationTargetException, IOException, CoreException, URISyntaxException{

        URI uri = getClass().getResource("/testFiles/atp.mol").toURI();
        URL url=FileLocator.toFileURL(uri.toURL());
        String path=url.getFile();
        ICDKMolecule mol = cdk.loadMolecule( path);
//        ICDKMolecule mol = cdk.loadMolecule( path);

    	//Calculate M2D
    	List<MetaPrintResult> res2 = m2d.calculate(mol, true);
    	assertEquals(31, res2.size());
    	
    	//Test that props are stored
    	String m2dprop=(String) mol.getAtomContainer().getProperty(Metaprint2DConstants.METAPRINT_RESULT_PROPERTY);
    	assertNotNull(m2dprop);
    	System.out.println("M2d stored prop: " + m2dprop);

	}

	@Test
	public void TestCalculateMolFile() throws BioclipseException, InvocationTargetException, IOException, CoreException, URISyntaxException, CDKException{

        URI uri = getClass().getResource("/testFiles/atp.mol").toURI();
        URL url=FileLocator.toFileURL(uri.toURL());
        String path=url.getFile();

    	//Calculate M2D
    	Map<IMolecule, List<MetaPrintResult>> res2 = m2d.calculate(path);
    	@SuppressWarnings("unchecked")
    	List<MetaPrintResult> res=(List<MetaPrintResult>) res2.values().toArray()[0];
    	assertEquals(31, res.size());
    	ICDKMolecule mol=(ICDKMolecule) res2.keySet().toArray()[0];
    	
    	//Test that props are not stored
    	String m2dprop=(String) mol.getAtomContainer().getProperty(Metaprint2DConstants.METAPRINT_RESULT_PROPERTY);
    	assertNull(m2dprop);

	}

	@Test
	public void TestCMLFileStoreProps() throws BioclipseException, InvocationTargetException, IOException, CoreException, URISyntaxException, CDKException{

		//Read from molfile
        URI uri = getClass().getResource("/testFiles/0037.cml").toURI();
        URL url=FileLocator.toFileURL(uri.toURL());
        String path=url.getFile();
        
        ICDKMolecule origmol = cdk.loadMolecule(path);
        
    	String newFile="/Virtual/0037-WITH-PROPS.cml";
    	cdk.saveMolecule(origmol, newFile);

    	Map<IMolecule, List<MetaPrintResult>> res = m2d.calculate(newFile, true);
    	assertEquals(1, res.keySet().size());
    	
    	//Read it back from file
    	ICDKMolecule cdkmol=cdk.loadMolecule(newFile);
    	
    	//Validate result and properties stored
    	String m2dprop=(String) cdkmol.getAtomContainer().getProperty(Metaprint2DConstants.METAPRINT_RESULT_PROPERTY);
    	assertNotNull(m2dprop);
    	System.out.println("M2d stored prop for mol " + cdkmol.getName() +": " + m2dprop);
        
//        
//        
//
//    	//Calculate M2D
//    	Map<IMolecule, List<MetaPrintResult>> res2 = m2d.calculate(path, true);
//    	
//    	@SuppressWarnings("unchecked")
//    	List<MetaPrintResult> res=(List<MetaPrintResult>) res2.values().toArray()[0];
//    	assertEquals(31, res.size());
//    	ICDKMolecule mol=(ICDKMolecule) res2.keySet().toArray()[0];
//
//    	//Test that props are stored
//    	String m2dprop=(String) mol.getAtomContainer().getProperty(Metaprint2DConstants.METAPRINT_RESULT_PROPERTY);
//    	assertNotNull(m2dprop);
//    	System.out.println("M2d stored prop in AC: " + m2dprop);
//
//    	System.out.println("cml old:\n" + mol.getCML());
//
//    	//Store molecule to another file and validate properties can be read back
//    	String newFilename="/Virtual/atpWithM2Dprops.cml";
//    	cdk.saveMolecule(mol, newFilename, "cml", true);
//    	
//    	ICDKMolecule newmol = cdk.loadMolecule(newFilename);
//    	assertNotNull(cdk);
//    	
//    	System.out.println("cml new:\n" + newmol.getCML());
//    	
//    	//Test that props are stored
//    	m2dprop=(String) newmol.getAtomContainer().getProperty(Metaprint2DConstants.METAPRINT_RESULT_PROPERTY);
//    	assertNotNull(m2dprop);
//    	System.out.println("M2d stored prop in file read back: " + m2dprop);
//

	}

	@Test
	public void TestCalculateCMLFile() throws BioclipseException, InvocationTargetException, IOException, CoreException{

		String fullpath=FileLocator.toFileURL(getClass().getResource("/testFiles/0037.cml")).getFile();
		Map<IMolecule, List<MetaPrintResult>> ress = m2d.calculate(new MockIFile(fullpath));
		assertEquals(1, ress.keySet().size());
		IMolecule mol = ress.keySet().iterator().next();
		assertNotNull(ress.get(mol));
		
	}

	@Test
	public void TestCalculateOnSMILES() throws BioclipseException, InvocationTargetException, IOException, CoreException{

		List<MetaPrintResult> ress = m2d.calculate(cdk.fromSMILES("C1CCCCC1CC(CC)CCO"));
		assertEquals(13, ress.size());

		System.out.println("Result:");
		for (MetaPrintResult res : ress){
			System.out.println(" : " + res);
		}

	}

	
	@Test
	public void TestCalculateMultiple() throws BioclipseException, InvocationTargetException, IOException, CoreException{

		ICDKMolecule mol1=cdk.fromSMILES("C1CCCCC1CC(CC)C");
		ICDKMolecule mol2=cdk.fromSMILES("C1CCCCC1CC(CC)CCO");
		ICDKMolecule mol3=cdk.fromSMILES("C1CCCCC1CC(CCO)CCOCN(CCO)CCC");
		
		List<IMolecule> mols=new ArrayList<IMolecule>();
		mols.add(mol1);
		mols.add(mol2);
		mols.add(mol3);
		
		Map<IMolecule, List<MetaPrintResult>> ress = m2d.calculate(mols);
		
		assertEquals(3, ress.keySet().size());

		System.out.println("Result:");
		for (IMolecule mol : mols){
			System.out.println("Molecule: " + mol.toSMILES());
			for (MetaPrintResult res : ress.get(mol)){
				System.out.println("  " + res);
			}
		}
	}

	@Test
	public void TestCalculateFromSDFviaCDKLoading() throws URISyntaxException, MalformedURLException, IOException, BioclipseException, CoreException, InvocationTargetException{

		Stopwatch sw=new Stopwatch();
		sw.start();
//        URI uri = getClass().getResource("/testFiles/m2d_ref_232.sdf").toURI();
        URI uri = getClass().getResource("/testFiles/dbsmallconf.sdf").toURI();
        URL url=FileLocator.toFileURL(uri.toURL());
        String path=url.getFile();
        List<ICDKMolecule> mols = cdk.loadMolecules( path);
		sw.stop();
		System.out.println("** Loading 232 sdf with CDK took: " + sw.toString());

		sw=new Stopwatch();
		sw.start();
        //Calculate on already perceiveed sybyl types
    	Map<IMolecule, List<MetaPrintResult>> res = m2d.calculate(mols);
//    	assertEquals(232, res.keySet().size());
    	assertEquals(5, res.keySet().size());
		sw.stop();
		System.out.println("** Predicting M2D 232 sdf took: " + sw.toString());

	}

	@Test
	public void TestCalculateFromSDF() throws URISyntaxException, MalformedURLException, IOException, BioclipseException, CoreException, InvocationTargetException, CDKException{

	    URI uri = getClass().getResource("/testFiles/dbsmallconf.sdf").toURI();
	    URL url=FileLocator.toFileURL(uri.toURL());
	    String path=url.getFile();

	    Map<IMolecule, List<MetaPrintResult>> res = m2d.calculate(path,false);

	    assertEquals(5, res.keySet().size());

	}

	@Test
	public void TestCalculateFromSDFStoreProps() throws URISyntaxException, MalformedURLException, IOException, BioclipseException, CoreException, InvocationTargetException, CDKException{

		Stopwatch sw=new Stopwatch();
		sw.start();
        URI uri = getClass().getResource("/testFiles/dbsmallconf.sdf").toURI();
        URL url=FileLocator.toFileURL(uri.toURL());
        String path=url.getFile();
        
        //Copy file to Virtual, since we want to save it later
        List<ICDKMolecule> origMols = cdk.loadMolecules(path);
    	String newFile="/Virtual/dbsmallconf-WITH-PROPS.sdf";
    	cdk.saveMolecules(origMols, newFile, cdk.getFormat("SDFFormat"));

        //Calculate on already perceiveed sybyl types from virtual path
    	Map<IMolecule, List<MetaPrintResult>> res = m2d.calculate(newFile, true);
    	assertEquals(5, res.keySet().size());
    	
    	//Validate result and properties stored
    	for (IMolecule mol : res.keySet()){
    		List<MetaPrintResult> molresult=res.get(mol);
    		assertNotNull(molresult);
    		ICDKMolecule cdkmol=(ICDKMolecule)mol;
    		String m2dprop=(String) cdkmol.getAtomContainer().getProperty(Metaprint2DConstants.METAPRINT_RESULT_PROPERTY);
        	assertNotNull(m2dprop);
        	System.out.println("M2d stored prop for mol " + cdkmol.getName() +": " + m2dprop);
    	}

    	/*
    	List<IMolecule> mols=new ArrayList<IMolecule>();
    	mols.addAll(res.keySet());
    	
    	String newFile="/Virtual/dbsmallconf-WITH-PROPS.sdf";
    	
    	//Test write file
    	cdk.saveMolecules(mols, newFile, "sdf");
    	
    	List<ICDKMolecule> newmols = cdk.loadMolecules(newFile);
    	//Validate result and properties stored
    	for (ICDKMolecule cdkmol : newmols){
    		String m2dprop=(String) cdkmol.getAtomContainer().getProperty(Metaprint2DConstants.METAPRINT_RESULT_PROPERTY);
        	assertNotNull(m2dprop);
        	System.out.println("M2d read saved prop for mol " + cdkmol.getName() +": " + m2dprop);
    	}
    	*/
    	
    	
	}


	@Test
	public void TestMultipleSerializationSDF() throws BioclipseException, InvocationTargetException, IOException, CoreException, CDKException{

		ICDKMolecule mol1=cdk.fromSMILES("C1CCCCC1CC(CC)C");
		ICDKMolecule mol2=cdk.fromSMILES("C1CCCCC1CC(CC)CCO");
		ICDKMolecule mol3=cdk.fromSMILES("C1CCCCC1CC(CCO)CCOCN(CCO)CCC");
		
		List<IMolecule> mols=new ArrayList<IMolecule>();
		mols.add(mol1);
		mols.add(mol2);
		mols.add(mol3);
		
		Map<IMolecule, List<MetaPrintResult>> ress = m2d.calculate(mols, true);
		assertEquals(3, ress.keySet().size());
		
		List<IMolecule> mollist=new ArrayList<IMolecule>(ress.keySet());

//		Map<IMolecule, Map<Object,Object>> molmap=new HashMap<IMolecule, Map<Object,Object>>();
//		for (IMolecule mol : ress.keySet()){
//			molmap.put(mol, cdk10.create(mol).getAtomContainer().getProperties());
//		}
		
        IFile target=new MockIFile();
        cdk.saveMolecules(mollist, target, cdk.getFormat("SDFFormat"));
//        Metaprint2dCDKhelper.SaveMolecules(molmap, target, "sdf");
        
        BufferedReader reader=new BufferedReader(new InputStreamReader(target.getContents()));

        System.out.println("#############################################");
        String line=reader.readLine();
        while(line!=null){
        	System.out.println(line);
            line=reader.readLine();
        }
        System.out.println("#############################################");
        
        List<ICDKMolecule> readmols = cdk.loadMolecules(target);
        assertEquals(3, readmols.size());

    	System.out.println("** Reading back created SDFile: ");
        for (ICDKMolecule cdkmol : readmols){
        	System.out.println("  - SMILES: " + cdk.calculateSMILES(cdkmol));

        	assertNotNull(cdkmol.getAtomContainer().
           			getProperty(Metaprint2DConstants.METAPRINT_RESULT_PROPERTY));
           	System.out.println(cdkmol.getAtomContainer().
           			getProperty(Metaprint2DConstants.METAPRINT_RESULT_PROPERTY));
           	System.out.println(" --- ");
        }
        
        System.out.println("*************************");
        
		

	}
	
	@Test
	public void TestMultipleSerializationCML() throws BioclipseException, InvocationTargetException, IOException, CoreException, CDKException{

		ICDKMolecule mol1=cdk.fromSMILES("C1CCCCC1CC(CC)C");
		ICDKMolecule mol2=cdk.fromSMILES("C1CCCCC1CC(CC)CCO");
		ICDKMolecule mol3=cdk.fromSMILES("C1CCCCC1CC(CCO)CCOCN(CCO)CCC");
		
		List<IMolecule> mols=new ArrayList<IMolecule>();
		mols.add(mol1);
		mols.add(mol2);
		mols.add(mol3);
		
		Map<IMolecule, List<MetaPrintResult>> ress = m2d.calculate(mols, true);
		assertEquals(3, ress.keySet().size());
		
        String virtualPath="/Virtual/TestMultipleSerializationCML.cml";
        cdk.saveMolecules(mols, virtualPath, cdk.getFormat("CMLFormat"));
        
        //For debug output
        System.out.println("#############################################");
        IFile target=ResourcePathTransformer.getInstance().transform(virtualPath);
        assertNotNull(target);
        BufferedReader reader=new BufferedReader(new InputStreamReader(target.getContents()));

        String line=reader.readLine();
        while(line!=null){
        	System.out.println(line);
            line=reader.readLine();
        }
        System.out.println("#############################################");

    	System.out.println("** Reading back created CML File: ");
    	
    	List<ICDKMolecule> readmols = cdk.loadMolecules(virtualPath);
        assertEquals(3, readmols.size());

        for (ICDKMolecule cdkmol : readmols){
        	
//        	System.out.println("  - SMILES: " + cdk.calculateSMILES(cdkmol));

        	assertNotNull(cdkmol.getAtomContainer().
           			getProperty(Metaprint2DConstants.METAPRINT_RESULT_PROPERTY));
        }
        
        System.out.println("*************************");
        
		

	}	

	
	@Test
	public void TestSingleSerializationCML() throws BioclipseException, InvocationTargetException, IOException, CoreException, CDKException{

		ICDKMolecule mol1=cdk.fromSMILES("C1CCCCC1CC(CC)C");
		
		m2d.calculate(mol1, true);

        String virtualPath="/Virtual/TestSingleSerializationCML.cml";
        cdk.saveMolecule(mol1, virtualPath);
        
        //For debug output
        System.out.println("#############################################");
        IFile target=ResourcePathTransformer.getInstance().transform(virtualPath);
        assertNotNull(target);
        BufferedReader reader=new BufferedReader(new InputStreamReader(target.getContents()));

        String line=reader.readLine();
        while(line!=null){
        	System.out.println(line);
            line=reader.readLine();
        }
        System.out.println("#############################################");

    	System.out.println("** Reading back created CML File: ");
    	
      ICDKMolecule cdkmol = cdk.loadMolecule( virtualPath);
//    	ICDKMolecule cdkmol = cdk.loadMolecule(virtualPath);
    	assertNotNull(cdkmol);
    	assertNotNull(cdkmol.getAtomContainer());
    	assertNotNull(cdkmol.getAtomContainer().
    			getProperty(Metaprint2DConstants.METAPRINT_RESULT_PROPERTY));
        
        System.out.println("*************************");
        
		

	}	

	
	
	 @Test
	  public void TestCompareHydrogens() throws BioclipseException, InvocationTargetException, IOException, CoreException{

	    String withHydrogens=FileLocator.toFileURL(getClass().getResource("/testFiles/hydrogen2D.mol")).getFile();
      String withoutHydrogens=FileLocator.toFileURL(getClass().getResource("/testFiles/noHHydrogen2D.mol")).getFile();

      Map<IMolecule, List<MetaPrintResult>> withRes = m2d.calculate(new MockIFile(withHydrogens));
      System.out.println("With H: " + withRes);
//      Map<IMolecule, List<MetaPrintResult>> withoutRes = m2d.calculate(new MockIFile(withoutHydrogens));
//      System.out.println("Without H: " + withoutRes);

      assertEquals(1, withRes.keySet().size());
      IMolecule mol = withRes.keySet().iterator().next();
      List<MetaPrintResult> mrlist = withRes.get( mol );
      ICDKMolecule cdkmol = cdk.asCDKMolecule( mol);
      
      for (int i=0; i< cdkmol.getAtomContainer().getAtomCount();i++){
          IAtom atom=cdkmol.getAtomContainer().getAtom(i);
          if ("H".equals( atom.getSymbol() )){
              MetaPrintResult mr = mrlist.get( i );
              assertEquals( 0, mr.getSubstrateCount());
              assertEquals( 0, mr.getReactionCentreCount());
          }
      }

//      assertNotNull(withRes.get(mol));
//      assertEquals(1, withoutRes.keySet().size());
	    
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
