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
package net.bioclipse.metaprint2d;

import org.eclipse.core.runtime.jobs.Job;

public abstract class MetaPrint2DJob extends Job{
	
	public static final String METAPRINT_JOB_FAMIlY="MetaprintFamily";

	public static final String METAPRINT_JOB_DBTYPE="DB";
	public static final String METAPRINT_JOB_OPERATORTYPE="OPERATOR";

	public MetaPrint2DJob(String name) {
		super(name);
	}

	/**
	 * We need to be able to detect that this is a metaprint job
	 */
	@Override
	public boolean belongsTo(Object family) {
		if (family instanceof String) {
			String fam = (String) family;
			if (fam.equals(METAPRINT_JOB_FAMIlY)) return true;
		}
		
		return false;
	}
	
	public abstract String getType();

}
