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
package net.bioclipse.metaprint2d.ui.cdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.sf.metaprint2d.Constants;
import net.sf.metaprint2d.mol2.Atom;
import net.sf.metaprint2d.mol2.Molecule;

public class CDK2MetaprintConverter {
	

	public static Molecule CDKMol2MetaprintMolecule(ICDKMolecule cdkmol){

			Molecule metamol=new Molecule();
			
	        IAtomContainer ac=cdkmol.getAtomContainer();

	        //Set up Map from CDK to Metaprint atoms
	        Map<IAtom, Atom> cdk2metaprintAtomMap=new HashMap<IAtom, Atom>();
	        for (int i=0; i< ac.getAtomCount();i++){

	        	//CDK IAtom
	        	IAtom cdkAtom=ac.getAtom(i);
	        	String type=cdkAtom.getAtomTypeName();

	        	//Metaprint atom
	        	Integer typeIndex=Constants.ATOM_TYPE_INDEX.get(type);
	        	Atom m2dAtom=new Atom(typeIndex);

	        	cdk2metaprintAtomMap.put(cdkAtom, m2dAtom);
	        	
	        }
	        
	    	//Add neighbours
	    	for (IAtom cdkAtom : cdk2metaprintAtomMap.keySet()){
	    		Atom m2dAtom=cdk2metaprintAtomMap.get(cdkAtom);
	    		
	    		//Get connected atoms list, add as neighbours
	    		List<IAtom> connList = ac.getConnectedAtomsList(cdkAtom);
	    		for (IAtom connAtom : connList){
	    			//Get m2d atom from map
	    			Atom m2dNeighbour=cdk2metaprintAtomMap.get(connAtom);
	    			m2dAtom.addNeighbour(m2dNeighbour);
	    		}

	    		//Add mols to metamol
	    		metamol.addAtom(m2dAtom);

	    	}
			
	    	return metamol;
	    	
		}
	
	public static Molecule getMetaprintMoleculeWithSybylTypes(ICDKMolecule cdkmol) throws CDKException{

		
		IAtomContainer ac=cdkmol.getAtomContainer();
			
		// Build metaprint molecule
		net.sf.metaprint2d.mol2.Molecule metamol = new Molecule();
		Map<IAtom,Atom> map = new HashMap<IAtom, Atom>();
		for (int i = 0; i < ac.getAtomCount(); i++) {
			IAtom cdkAtom = ac.getAtom(i);
			String atomTypeName=ac.getAtom(i).getAtomTypeName();
			Atom atom;
			if (atomTypeName == null || atomTypeName.equals("")) {
				atom = new Atom("Du");
			} else {
				atom = new Atom(atomTypeName);
			}
			metamol.addAtom(atom);
	    	map.put(cdkAtom,atom);

		}
		
	    //We need to set Neighbours after all metatoms has been created
		//Hence 2 loops
	    for (IAtom cdkAt : ac.atoms()) {
	    	Atom atom = map.get(cdkAt);
	    	for (IAtom an : ac.getConnectedAtomsList(cdkAt)) {
	    		atom.addNeighbour(map.get(an));
	    	}
	    }
		
		return metamol;

	}

}
