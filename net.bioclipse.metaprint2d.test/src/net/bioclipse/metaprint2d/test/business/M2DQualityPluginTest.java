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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.sf.metaprint2d.MetaPrintResult;
import net.bioclipse.cdk.business.Activator;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.ui.sdfeditor.business.IMoleculeTableManager;
import net.bioclipse.cdk.ui.sdfeditor.business.SDFIndexEditorModel;
import net.bioclipse.cdkdebug.business.ICDKDebugManager;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;
import net.bioclipse.metaprint2d.ui.MetaPrint2DProperty;
import net.bioclipse.metaprint2d.ui.business.IMetaPrint2DManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;


public class M2DQualityPluginTest {

    private static final String METAPRINT2D_PROPERTY = "MetaPrint2D";

    private static final String SDF_WITH_VERIFIED_VALUES = "/testFiles/m2d_ref_232.sdf";
    private static final String SDF_WITH_NO_VALUES = "/testFiles/m2d_232.sdf";

    ICDKManager cdk=Activator.getDefault().getJavaCDKManager();
    ICDKDebugManager cdkdebug=net.bioclipse.cdkdebug.Activator.getDefault().getJavaManager();
    IMetaPrint2DManager m2d = net.bioclipse.metaprint2d.ui.Activator.getDefault().getMetaPrint2DManager();

    SDFIndexEditorModel moleculesmodel;

    /**
     * This test reads an SDF and calculates properties for all molecules one by 
     * one and compares to an SDF with verified values.
     *  
     * @throws BioclipseException
     * @throws InvocationTargetException
     * @throws URISyntaxException
     * @throws MalformedURLException
     * @throws IOException
     * @throws CoreException
     * @throws CDKException
     * @throws InterruptedException 
     */
    @Test
    public void QUalityTest232mols() throws BioclipseException, 
                                    InvocationTargetException, 
                                    URISyntaxException, 
                                    MalformedURLException, 
                                    IOException, 
                                    CoreException, 
                                    CDKException, 
                                    InterruptedException{

        m2d.setDataBase("all");

        //====================
        // Read and parse file with correct results into memory
        //====================

        IMoleculeTableManager moltable = net.bioclipse.cdk.ui.sdfeditor.
                               Activator.getDefault().getMoleculeTableManager();

        IFile sdfile=createSDFileInWorkspace();
        
        moleculesmodel=null;

        BioclipseJob<SDFIndexEditorModel> job1 = 
            moltable.createSDFIndex(sdfile, 
                        new BioclipseJobUpdateHook<SDFIndexEditorModel>("job") {

                @Override
                public void completeReturn( SDFIndexEditorModel object ) {
                    moleculesmodel = object;
                }
            } );

        job1.join();
        assertNotNull( moleculesmodel );
        System.out.println("Number of mols: " + moleculesmodel
                                                .getNumberOfMolecules());
        assertEquals( 232, moleculesmodel.getNumberOfMolecules() );
//        assertNotNull("Could not read molecule 0",
//                      moleculesmodel.getMoleculeAt( 0 ) );

        //We need to define that we want to read extra properties as well
        List<String> extraProps=new ArrayList<String>();
        extraProps.add( METAPRINT2D_PROPERTY );

        BioclipseJob<Void> job = moltable.
        parseProperties( moleculesmodel, 
                         extraProps, 
                         new BioclipseJobUpdateHook<Void>(
                         "Parsing SDFile for m2d props"));

        //Wait for job to finish
        job.join();

        //Verify we have m2d property for all
        for (int i=0; i<moleculesmodel.getNumberOfMolecules(); i++){
            
            //Verify we can get cdkmolecule
            ICDKMolecule mol=moleculesmodel.getMoleculeAt( i );
            assertNotNull("Could not get ICDKMolecule at index " + i, mol);

            //Get property
            MetaPrint2DProperty readm2d = moleculesmodel.getPropertyFor(
                                                      i, METAPRINT2D_PROPERTY );
            assertNotNull("M2D prop for mol: " + i + " is null", readm2d );

            List<MetaPrintResult> correctResultList = readm2d.getResults();
            assertNotNull( correctResultList );
            
            System.out.println("Processing molecule: " + i);
            
            List<MetaPrintResult> newResultList = m2d.calculate( mol );
            assertNotNull( newResultList );
            
            //Compare old and new
            assertEquals("Stored and calculated results are not of same size",
                         correctResultList.size(), newResultList.size() );
            for (int j=0; j<correctResultList.size();j++){
                MetaPrintResult cres = correctResultList.get( j );
                MetaPrintResult nres = newResultList.get( j );
                assertEquals( cres.getAtomNumber(), nres.getAtomNumber() );
                assertEquals( cres.getSubstrateCount(), nres.getSubstrateCount());
                assertEquals( cres.getReactionCentreCount(), nres.getReactionCentreCount() );
                assertEquals( cres.getNormalisedRatio(), nres.getNormalisedRatio() );
            }
            
        }


    }

    private IFile createSDFileInWorkspace() throws URISyntaxException, MalformedURLException, IOException, CoreException {

        //Get WS root
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        //Create the project
        IProject project = root.getProject("bogus");
        project.create(null);
        project.open(null);

        InputStream instr = getClass().getResourceAsStream( SDF_WITH_VERIFIED_VALUES );
        
        //Create folders
        IPath projectPath = project.getFullPath();
        IPath filePath = projectPath.append("232.sdf");
        IFile file = root.getFile(filePath);
        file.create(instr,true,null);
        
        return file;
    }


}
