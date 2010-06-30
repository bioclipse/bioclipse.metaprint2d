package net.bioclipse.metaprint2d.ds;


import java.util.Map;

import net.bioclipse.ds.model.result.AtomResultMatch;

import org.openscience.cdk.renderer.generators.IGeneratorParameter;

/**
 * 
 * @author ola
 */
public class MetaPrint2D_DS_Match extends AtomResultMatch{
	
    
    public MetaPrint2D_DS_Match(String name, int resultStatus) {
		super(name, resultStatus);
	}

	@Override
	public Class<? extends IGeneratorParameter<Boolean>> getGeneratorVisibility() {
    	return (Class<? extends IGeneratorParameter<Boolean>>)MetaPrint2D_DS_Generator.Visibility.class;
    }

    @Override
    public Class<? extends IGeneratorParameter<Map<Integer, Integer>>> getGeneratorAtomMap() {
    	return (Class<? extends IGeneratorParameter<Map<Integer, Integer>>>)MetaPrint2D_DS_Generator.AtomMap.class;
    }
}
