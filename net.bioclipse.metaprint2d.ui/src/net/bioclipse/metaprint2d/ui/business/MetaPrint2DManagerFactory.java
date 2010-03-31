/* *****************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/

package net.bioclipse.metaprint2d.ui.business;

import net.bioclipse.metaprint2d.ui.Activator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

/**
 * 
 * @author ola
 */
public class MetaPrint2DManagerFactory implements IExecutableExtension, 
                                              IExecutableExtensionFactory {

    private Object metaprint2DManager;
    
    public void setInitializationData(IConfigurationElement config,
            String propertyName, Object data) throws CoreException {
        
        metaprint2DManager = Activator.getDefault().getMetaPrint2DManager();
        if(metaprint2DManager==null) {
            metaprint2DManager = new Object();
        }
    }

    public Object create() throws CoreException {
        return metaprint2DManager;
    }
}
