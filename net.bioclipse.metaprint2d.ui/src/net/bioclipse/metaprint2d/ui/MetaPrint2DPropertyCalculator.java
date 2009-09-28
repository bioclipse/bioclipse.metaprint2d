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
package net.bioclipse.metaprint2d.ui;

 import java.util.List;

import org.apache.log4j.Logger;

import net.sf.metaprint2d.MetaPrintResult;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.ui.sdfeditor.business.IPropertyCalculator;
import net.bioclipse.cdk.ui.sdfeditor.business.MoleculeTableManager;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.metaprint2d.ui.business.IMetaPrint2DManager;

/**
 * @author ola
 */
public class MetaPrint2DPropertyCalculator 
                         implements IPropertyCalculator<MetaPrint2DProperty> {

    Logger logger = Logger.getLogger( MetaPrint2DPropertyCalculator.class );

    public MetaPrint2DProperty calculate( ICDKMolecule molecule ) {

        IMetaPrint2DManager m2d = net.bioclipse.metaprint2d.ui.Activator.getDefault().getMetaPrint2DManager();
        try {
            List<MetaPrintResult> m2dres = m2d.calculate(molecule);
            MetaPrint2DProperty prop=new MetaPrint2DProperty(m2dres);
            assert prop!=null;
            return prop;
        } catch ( Exception e ) {
            LogUtils.handleException( e, logger, Activator.PLUGIN_ID);
        }
        return null;
    }

    public String getPropertyName() {
        return "MetaPrint2D";
    }

    public MetaPrint2DProperty parse( String value ) {

        return new MetaPrint2DProperty(value);
    }

    public String toString( Object value ) {

        return ((MetaPrint2DProperty)value).toString();
    }

}
