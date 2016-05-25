package net.bioclipse.metaprint2d.ui.business;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.atomtype.mapper.AtomTypeMapper;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomTypeException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

import net.bioclipse.cdk.business.Activator;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;

public class CDKHelper {

	private static final Logger logger = Logger.getLogger(CDKHelper.class);


	public static String perceiveSybylAtomTypes(IMolecule mol)
			throws InvocationTargetException {

		ICDKMolecule cdkmol = null;
		AtomTypeFactory factory = null;

		ICDKManager cdk = Activator.getDefault().getJavaCDKManager();
		URL owlURL = Platform.getBundle("org.openscience.cdk.io").getResource("/org/openscience/cdk/dict/data/sybyl-atom-types.owl");

		try {

			InputStream iStream = owlURL.openStream();
			cdkmol = cdk.asCDKMolecule(mol);

			factory = AtomTypeFactory.getInstance( iStream, "owl",
					DefaultChemObjectBuilder.getInstance());

		} catch (IOException e) {
			logger.error("Could not get sybyl-atom-types.owl file",e);
			e.printStackTrace();
			throw new InvocationTargetException(e);
		} 
		catch (BioclipseException e) {
			logger.error("Error converting cdk molecule" , e);
			e.printStackTrace();
			throw new InvocationTargetException(e);
		}


	IAtomContainer ac = cdkmol.getAtomContainer();
	CDKAtomTypeMatcher cdkMatcher 
	= CDKAtomTypeMatcher.getInstance(ac.getBuilder());
	AtomTypeMapper mapper 
	= AtomTypeMapper.getInstance(
			"org/openscience/cdk/dict/data/cdk-sybyl-mappings.owl" );

	IAtomType[] sybylTypes = new IAtomType[ac.getAtomCount()];

	int atomCounter = 0;
	for (IAtom atom : ac.atoms()) {
		IAtomType type;
		try {
			type = cdkMatcher.findMatchingAtomType(ac, atom);
		} 
		catch (CDKException e) {
			type = null;
		}
		if (type==null) {
			//                logger.debug("AT null for atom: " + atom);
			type = atom.getBuilder().newInstance(
					IAtomType.class, atom.getSymbol()
					);
			type.setAtomTypeName("X");
		}
		AtomTypeManipulator.configure(atom, type);
	}
	try {
            // TODO Check this
            Aromaticity aromaticity = new Aromaticity( ElectronDonation.cdk(), Cycles.cdkAromaticSet() );
            // AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms( mol
            // );
            aromaticity.apply( ac );
	} 
	catch (CDKException e) {
		logger.debug("Failed to perceive aromaticity: " + e.getMessage());
	}
	for (IAtom atom : ac.atoms()) {
		String mappedType = mapper.mapAtomType(atom.getAtomTypeName());
		if ("C.2".equals(mappedType)
				&& atom.getFlag(CDKConstants.ISAROMATIC)) {
			mappedType = "C.ar";
		} 
		else if ("N.pl3".equals(mappedType)
				&& atom.getFlag(CDKConstants.ISAROMATIC)) {
			mappedType = "N.ar";
		}
		try {
			sybylTypes[atomCounter] = factory.getAtomType(mappedType);
		} 
		catch (NoSuchAtomTypeException e) {
			// yes, setting null's here is important
			sybylTypes[atomCounter] = null; 
		}
		atomCounter++;
	}
	StringBuffer result = new StringBuffer();
	// now that full perception is finished, we can set atom type names:
	for (int i = 0; i < sybylTypes.length; i++) {
		if (sybylTypes[i] != null) {
			ac.getAtom(i).setAtomTypeName(sybylTypes[i].getAtomTypeName());
		} 
		else {
			ac.getAtom(i).setAtomTypeName("X");
		}

		result.append(i).append(':').append(ac.getAtom(i).getAtomTypeName())
		/*.append("\n")*/;

	}
	return result.toString();
}

}

