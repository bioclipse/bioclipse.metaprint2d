package net.bioclipse.metaprint2d.ds;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.openscience.cdk.interfaces.IAtom;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.ds.model.AbstractDSTest;
import net.bioclipse.ds.model.DSException;
import net.bioclipse.ds.model.ITestResult;
import net.bioclipse.ds.model.result.RGBMatch;
import net.bioclipse.metaprint2d.ui.Activator;
import net.bioclipse.metaprint2d.ui.business.IMetaPrint2DManager;
import net.sf.metaprint2d.MetaPrintResult;


public class MetaPrint2DMatcher extends AbstractDSTest{

    @Override
    protected List<? extends ITestResult> doRunTest( ICDKMolecule cdkmol,
                                                     IProgressMonitor monitor ) {

        IMetaPrint2DManager m2d=Activator.getDefault().getMetaPrint2DManager();
        try {
            
            //Calc m2d and store properties on mol
//            List<MetaPrintResult> m2dres = m2d.calculate( cdkmol);
            RGBMatch match=new RGBMatch( "MetaPrint2D",ITestResult.INFORMATIVE);
            
            
            for (IAtom atom : cdkmol.getAtomContainer().atoms()){
//                match.putAtomColor( atom, color );
            }
            
//            match.writeResultsAsProperties(cdkmol.getAtomContainer(), 
//                                           net.bioclipse.ds.cpdb.signatures.Activator.CPDB_RESULT_PROPERTY);

            
            
        } catch ( Exception e ) {
            return returnError( "Error running MetaPrint2D: " + e.getMessage(), e.getMessage() );
        }
        
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void initialize( IProgressMonitor monitor ) throws DSException {
        //TODO: Perhaps trigger M2D db loading if not triggered already
    }
    
}