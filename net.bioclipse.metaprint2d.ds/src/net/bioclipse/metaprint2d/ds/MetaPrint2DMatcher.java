package net.bioclipse.metaprint2d.ds;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.openscience.cdk.interfaces.IAtom;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.ds.model.AbstractDSTest;
import net.bioclipse.ds.model.DSException;
import net.bioclipse.ds.model.ITestResult;
import net.bioclipse.ds.model.result.SimpleResult;
import net.bioclipse.metaprint2d.ui.Activator;
import net.bioclipse.metaprint2d.ui.MetaPrint2DHelper;
import net.bioclipse.metaprint2d.ui.business.IMetaPrint2DManager;
import net.sf.metaprint2d.MetaPrintResult;

/**
 * 
 * @author ola
 *
 */
public class MetaPrint2DMatcher extends AbstractDSTest{

    @Override
    protected List<? extends ITestResult> doRunTest( ICDKMolecule cdkmol,
                                                     IProgressMonitor monitor ) {

        List<SimpleResult> results=new ArrayList<SimpleResult>();

        IMetaPrint2DManager m2d=Activator.getDefault().getMetaPrint2DManager();
        try {
            
            //Calc m2d and store properties on mol
            List<MetaPrintResult> m2dres = m2d.calculate( cdkmol);
            
            MetaPrint2D_DS_Match match=new MetaPrint2D_DS_Match( "MetaPrint2D",ITestResult.INFORMATIVE);
            
            for (int i=0; i< cdkmol.getAtomContainer().getAtomCount(); i++){
            	IAtom atom=cdkmol.getAtomContainer().getAtom(i);
            	int atomno=cdkmol.getAtomContainer().getAtomNumber(atom);
            	MetaPrintResult mres=m2dres.get(i);
            	int mresint=MetaPrint2DHelper.getIntResultByNormValue(mres);
                match.putAtomResult( atomno, mresint );
            }
            
            results.add(match);
            
        } catch ( Exception e ) {
            return returnError( "Error running MetaPrint2D: " + e.getMessage(), e.getMessage() );
        }
        
        return results;
    }

    @Override
    public void initialize( IProgressMonitor monitor ) throws DSException {
        //TODO: Perhaps trigger M2D db loading if not triggered already
    }
    
}