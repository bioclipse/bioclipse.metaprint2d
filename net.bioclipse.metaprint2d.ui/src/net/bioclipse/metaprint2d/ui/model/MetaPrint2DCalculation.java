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
package net.bioclipse.metaprint2d.ui.model;

public class MetaPrint2DCalculation {

	String database;
	String operator;
	String calculationTime;
	String status;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public MetaPrint2DCalculation(String database, String operator,
			String calculationTime) {
		super();
		this.database = database;
		this.operator = operator;
		this.calculationTime = calculationTime;
	}

	public MetaPrint2DCalculation() {
	}

	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getCalculationTime() {
		return calculationTime;
	}
	public void setCalculationTime(String calculationTime) {
		this.calculationTime = calculationTime;
	}
	
}
