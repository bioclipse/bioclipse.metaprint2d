/* *****************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.metaprint2d.ui.business;

 import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.metaprint2d.MetaPrintCalculator;
import net.sf.metaprint2d.MetaPrintResult;
import net.sf.metaprint2d.mol2.Mol2Reader;
import net.sf.metaprint2d.mol2.Molecule;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdkdebug.business.ICDKDebugManager;
import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.metaprint2d.Metaprinter;
import net.bioclipse.metaprint2d.Species;
import net.bioclipse.metaprint2d.ui.Activator;
import net.bioclipse.metaprint2d.ui.Metaprint2DConstants;
import net.bioclipse.metaprint2d.ui.cdk.CDK2MetaprintConverter;
import net.bioclipse.metaprint2d.ui.model.MetaPrint2DCalculation;
import net.bioclipse.metaprint2d.ui.prefs.MetaprintPrefs;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.formats.CMLFormat;
import org.openscience.cdk.io.formats.IChemFormat;

public class MetaPrint2DManager implements IMetaPrint2DManager{

    private static final Logger logger = Logger.getLogger(MetaPrint2DManager.class);


    ICDKManager cdk;

    public Map<IEditorPart, MetaPrint2DCalculation> getCalculationMap() {
        return calculationMap;
    }

    public void setCalculationMap(
                                  Map<IEditorPart, MetaPrint2DCalculation> calculationMap) {
        this.calculationMap = calculationMap;
    }

    Map<IEditorPart, MetaPrint2DCalculation> calculationMap;


    public MetaPrint2DManager() {
        cdk=net.bioclipse.cdk.business.Activator.getDefault().getJavaCDKManager();
        calculationMap=new HashMap<IEditorPart, MetaPrint2DCalculation>();
    }

    public MetaPrint2DManager(ICDKManager cdkmanager) {
        cdk=cdkmanager;
        calculationMap=new HashMap<IEditorPart, MetaPrint2DCalculation>();
    }

    /**
     * Namespace
     */
    public String getManagerName() {
        return "metaprint2d";
    }
    
    /**
     * Calculate MetaPrint2D for an IMolecule
     */
    public List<MetaPrintResult> calculate(IMolecule molecule) throws BioclipseException, InvocationTargetException {

        ICDKMolecule cdkmol=cdk.asCDKMolecule( molecule);
        
        ICDKMolecule clonemol = cdk.clone( cdkmol );
        
        //Make sure we perform aromaticity detection before M2D
        ICDKMolecule aromaticcdkmol = (ICDKMolecule) cdk.perceiveAromaticity( clonemol );
//        ICDKMolecule noexplcdkmol = cdk.removeExplicitHydrogens( aromaticcdkmol );
//        cdk.removeExplicitHydrogens( cdkmol );
//        ICDKMolecule noimplcdkmol = (ICDKMolecule) cdk.addImplicitHydrogens( noexplcdkmol );
//        ICDKMolecule noimplcdkmol = (ICDKMolecule) cdk.addImplicitHydrogens( aromaticcdkmol );
//        System.out.println("--" + noimplcdkmol.getAtomContainer());
        return doCalculation(aromaticcdkmol);
    }

    @SuppressWarnings("unchecked")
    public List<MetaPrintResult> calculate(IMolecule molecule,
                                           boolean storeResults) throws BioclipseException,
                                           InvocationTargetException {

        List<IMolecule> lst=new ArrayList<IMolecule>();
        lst.add(molecule);
        Map<IMolecule, List<MetaPrintResult>> ret = calculate(lst, storeResults);
        if (ret!=null && ret.size()>0)
            return (List<MetaPrintResult>) ret.values().toArray()[0];
        else return null;
    }


    /**
     * Calculate MetaPrint2D for a File containing a molecule
     */
    public Map<IMolecule, List<MetaPrintResult>> calculate(IFile file) throws IOException, BioclipseException, CoreException, InvocationTargetException {

        List<ICDKMolecule> cdkmols = cdk.loadMolecules(file);
        return calculate(cdkmols, false);

    }

    public Map<IMolecule, List<MetaPrintResult>> calculate(IFile file,
                                                           boolean storeResults) throws IOException, BioclipseException,
                                                           CoreException, InvocationTargetException, CDKException {

        List<ICDKMolecule> cdkmols=cdk.loadMolecules(file);

        //Do calculation
        Map<IMolecule, List<MetaPrintResult>> ret = calculate(cdkmols, storeResults);

        //Store results
        if (storeResults){
            //If multiple mols
            if (cdkmols.size()==1)
                cdk.saveMolecule(cdkmols.get(0), file, true);
            if (cdkmols.size()>1){
                IChemFormat format=cdk.guessFormatFromExtension( file.getName() );
                cdk.saveMolecules(cdkmols, file, format);
            }
        }

        return ret;

    }



    /**
     * Calculate MetaPrint2D for a path to a molecular file
     * @throws CDKException 
     */
    public Map<IMolecule, List<MetaPrintResult>> calculate(String path) 
    throws IOException, BioclipseException, CoreException, InvocationTargetException, CDKException {

        return calculate(ResourcePathTransformer.getInstance().transform(
                                                                         path), false);
    }

    public Map<IMolecule, List<MetaPrintResult>> calculate(String path,
                                                           boolean storeResults) throws IOException, BioclipseException,
                                                           CoreException, InvocationTargetException, CDKException {

        return calculate(ResourcePathTransformer.getInstance().transform(
                                                                         path), true);
    }



    /**
     * Perform the actual MetaPrint2D calculation
     * @param cdkmol_in
     * @return 
     * @throws InvocationTargetException
     */
    private List<MetaPrintResult> doCalculation(ICDKMolecule cdkmol_in) throws InvocationTargetException{

        //Get Metaprint engine. Throws exception if error.
        MetaPrintCalculator metaprint = Metaprinter.getMetaprint();
        if (metaprint==null){
            logger.debug("Error loading metaprint database");
            throw new UnsupportedOperationException("Error loading metaprint database");
        }

        //        logger.debug("Calculating MetaPrint2D with DB: " + Metaprinter.getSpecies());

        //Depict atom types
        net.bioclipse.cdkdebug.Activator.getDefault()
                            .getJavaManager().perceiveSybylAtomTypes(cdkmol_in);

        //Convert to Metaprint Molecule
        Molecule metamol=null;
        try {
            metamol=CDK2MetaprintConverter
                              .getMetaprintMoleculeWithSybylTypesNew(cdkmol_in);
        } catch (CDKException e) {
            logger.error("Could not convert " +
                         "molecule with CDK. Reason: " + e.getMessage());
            throw new InvocationTargetException( e);
        }
        
        //Predict m2d
        List<MetaPrintResult> scores = metaprint.predictMetabolicSites(metamol);
        return scores;

    }

    public Molecule doSybylAtomTyping(ICDKMolecule cdkmol_in) throws InvocationTargetException {
        //Do sybyl atom typing
        //==============================
        String mol2path=null;
        Molecule metamol=null;

        //If ObenBabel selected for Atom Typing
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        if (store.getString(MetaprintPrefs.METAPRINT_ATOMTYPING).equals(MetaprintPrefs.OPENBABEL_ATOMTYPING)){

            mol2path = convertToMol2WithOpenBabel(cdkmol_in);

            try {
                Mol2Reader in = new Mol2Reader(new FileReader(mol2path));    
                metamol = in.read();
            } catch ( IOException e ) {
                logger.debug("Could not convert " +
                             "molecule with OpenBabel. Reason: " + e.getMessage());
                throw new UnsupportedOperationException("Could not convert " +
                                                        "molecule with OpenBabel. Reason: " + e.getMessage());
            }

        }

        //Else, use CDK to get Sybyl atom types
        else{
            try {
                ICDKMolecule cdkMol=cdk.asCDKMolecule( cdkmol_in);
                metamol=CDK2MetaprintConverter.getMetaprintMoleculeWithSybylTypesNew(cdkMol);
            } catch (CDKException e) {
                logger.error("Could not convert " +
                             "molecule with CDK. Reason: " + e.getMessage());
                throw new UnsupportedOperationException("Could not convert " +
                                                        "molecule with CDK. Reason: " + e.getMessage());
            } catch (BioclipseException e) {
                logger.error("Could not convert " +
                             "molecule with CDK. Reason: " + e.getMessage());
                throw new UnsupportedOperationException("Could not convert " +
                                                        "molecule with CDK. Reason: " + e.getMessage());
            }
        }

        //Atom typing done.
        return metamol;
    }
    
    /**
     * Do sybyl atom typing with CDK.
     * @param cdkmol_in
     * @return
     * @throws InvocationTargetException
     */
    public Molecule doCDKSybylAtomTyping(ICDKMolecule cdkmol_in) throws InvocationTargetException {

        Molecule metamol=null;

        try {
            ICDKMolecule cdkMol=cdk.asCDKMolecule( cdkmol_in);
            metamol=CDK2MetaprintConverter.getMetaprintMoleculeWithSybylTypesNew(cdkMol);
        } catch (CDKException e) {
            logger.error("Could not convert " +
                         "molecule with CDK. Reason: " + e.getMessage());
            throw new UnsupportedOperationException("Could not convert " +
                                                    "molecule with CDK. Reason: " + e.getMessage());
        } catch (BioclipseException e) {
            logger.error("Could not convert " +
                         "molecule with CDK. Reason: " + e.getMessage());
            throw new UnsupportedOperationException("Could not convert " +
                                                    "molecule with CDK. Reason: " + e.getMessage());
        }

        return metamol;
    }

    /**
     * If OpenBabel used for sybyl atom typing, make sure it has a valid path
     * or throw exception.
     */
    private void verifyOpenBabelPath() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        if (store.getString(MetaprintPrefs.METAPRINT_ATOMTYPING).
                equals(MetaprintPrefs.OPENBABEL_ATOMTYPING)){

            String openbabel_path=store.getString( MetaprintPrefs.OPENBABEL_PATH );
            if (openbabel_path==null){
                logger.debug( "Openbabel path not set. " +
                "Please set in Preferences > Metaprint2d" );
                throw new UnsupportedOperationException("Openbabel path not set. " +
                "Please set in Preferences > Metaprint2d");
            }

            File obfile=new File(openbabel_path);
            if (obfile.canRead()==false){
                logger.debug( "Cannot read openbabel at location: '" + 
                              openbabel_path + "'. Please set correct path in " +
                "Preferences > Metaprint2d" );
                throw new UnsupportedOperationException("Cannot read openbabel " +
                                                        "at location: '" + openbabel_path + "'. " +
                "Please set correct path in Preferences > Metaprint2d");
            }
        }
    }


    /**
     * Convert a ICDKMolecule to a mol2 file
     * @param cdkmol the molecule to convert
     * @return String with absolute path to mol2 file
     * @throws InvocationTargetException
     */
    private String convertToMol2WithOpenBabel(ICDKMolecule cdkmol) throws InvocationTargetException {

        String filename=null;
        try {
            File tmpfile=File.createTempFile( "m2d", "bioc" );
            filename=tmpfile.getAbsolutePath();
        } catch ( IOException e ) {
            System.out.println("Cant write metaprint temp file in default temp dir.");
            e.printStackTrace();
            throw new InvocationTargetException(e);
        }

        if (filename==null){
            System.out.println("Cant write metaprint temp file in default temp dir.");
            throw new IllegalArgumentException("Cant write metaprint temp file in default temp dir.");
        }

        try {
            cdk.saveMolecule(cdkmol, filename);
        } catch (BioclipseException e) {
            logger.debug("Error serializing molecule to MDL: " + e.getMessage());
            throw new InvocationTargetException(e);
        } catch (CoreException e) {
            System.out.println("Error writing file: " + filename + ". Reason: " + e.getMessage());
            throw new InvocationTargetException(e);
        }

        System.out.println("Wrote molfile: " + filename);

        //Convert to mol2. Update name by adding .mol2
        String mol2path=runBabelConvertMolToMol2(new File(filename));

        return mol2path;

    }

    /**
     * Convert the input file to mol2.
     * @param savefile file to be converted.
     * @return Path to new mol2 file
     */
    private String runBabelConvertMolToMol2( File savefile ) {

        IPreferenceStore store = PlatformUI.getPreferenceStore();
        String openbabel_path=store.getString( MetaprintPrefs.OPENBABEL_PATH );


        File obfile=new File(openbabel_path);
        if (obfile.canRead()==false){
            logger.debug("Cannot read openbabel at location: '" + openbabel_path + "'. Please set correct path in Preferences > Metaprint2d");
            throw new IllegalArgumentException( "Cannot read openbabel at location: '" + openbabel_path + "'. Please set correct path in Preferences > Metaprint2d" );
        }

        String command=openbabel_path+" -imol " + savefile.getAbsolutePath() + " -omol2 " + savefile.getAbsolutePath()+".mol2";

        try {
            Process ext_proc=Runtime.getRuntime().exec(command, null, null);

            //Get output of process and send to BioclipseConsole
            BufferedReader d = new BufferedReader(new InputStreamReader(ext_proc.getInputStream()));
            String str="";
            while ((str = d.readLine()) != null) {
                System.out.println("> " + str);
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        return savefile.getAbsolutePath()+".mol2";

    }

    public Map<IMolecule, List<MetaPrintResult>> calculate(List<? extends IMolecule> mols) throws BioclipseException, InvocationTargetException {

        return calculate(mols,false);
    }

    /**
     * Calculate M2D for a list of molecules and optionally store as properties 
     * in atomcontainer.
     */
    public Map<IMolecule, List<MetaPrintResult>> calculate(
                                                           List<? extends IMolecule> mols, boolean storeResults)
                                                           throws BioclipseException, InvocationTargetException {

        Map<IMolecule, List<MetaPrintResult>> results = 
            new HashMap<IMolecule, List<MetaPrintResult>>();

        for (IMolecule mol : mols){
            List<MetaPrintResult> res = calculate(mol);
            if (storeResults){
                ICDKMolecule cdkmol=cdk.asCDKMolecule( mol);
                cdkmol.getAtomContainer().setProperty(
                                 Metaprint2DConstants.METAPRINT_RESULT_PROPERTY,
                                 res.toString());

                results.put(cdkmol, res);
            }else{
                results.put(mol, res);
            }
        }

        return results;

    }
    
    public void clear(ICDKMolecule mol){
        mol.getAtomContainer().removeProperty( 
                                Metaprint2DConstants.METAPRINT_RESULT_PROPERTY);
    }
    public void clear(List<? extends ICDKMolecule> mols){
        for (ICDKMolecule mol : mols){
            mol.getAtomContainer().removeProperty( 
                                Metaprint2DConstants.METAPRINT_RESULT_PROPERTY);
        }

    }

    public String getActiveDatabase() {

        return Metaprinter.getSpecies().toString();
    }

    public void setDataBase( String database ) {

        if (database==null) throw new IllegalArgumentException("database cannot be null");

        if (    (database.equalsIgnoreCase( "all")) ||
                (database.equalsIgnoreCase( "dog")) ||    
                (database.equalsIgnoreCase( "rat")) ||
                (database.equalsIgnoreCase( "human"))
        ){

            //Valid string
            Metaprinter.setSpecies( database );
            return;
        }
        
        if (database==null) throw new IllegalArgumentException(database + 
               " is not a valid database (must be one of All, Dog, Rat, and Human). ");

    }




}
