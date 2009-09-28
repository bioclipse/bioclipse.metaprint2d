/*******************************************************************************
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

package net.bioclipse.metaprint2d.ui.prefs;

import net.bioclipse.metaprint2d.ui.Activator;

import org.apache.log4j.Logger;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;



/**
 * Initialize UI preferences. Don't forget plugin_customization.ini also
 * initializes some preferences.
 * @author ola
 *
 */
public class MetaprintPreferenceInitializer extends AbstractPreferenceInitializer {

    private static final Logger logger = Logger.getLogger(MetaprintPreferenceInitializer.class);

    @Override
    public void initializeDefaultPreferences() {
    	
    	IPreferenceStore store=Activator.getDefault().getPreferenceStore();
    	store.setDefault(MetaprintPrefs.OPENBABEL_PATH, "/usr/bin/babel");
    	store.setDefault(MetaprintPrefs.METAPRINT_ATOMTYPING, MetaprintPrefs.CDK_ATOMTYPING);

      store.setDefault(MetaprintPrefs.CIRCLE_RADIUS, "4");
      store.setDefault(MetaprintPrefs.RENDER_SOLID_CIRCLES, false);
      store.setDefault(MetaprintPrefs.RENDER_MISSING_GREY, false);

    	
//        IEclipsePreferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
//        node.put(MetaprintPrefs.OPENBABEL_PATH, "/usr/bin/babel");
//        node.put(MetaprintPrefs.METAPRINT_ATOMTYPING, MetaprintPrefs.CDK_ATOMTYPING);

        logger.info("Metaprint2d default preferences initialized");
    }
}
