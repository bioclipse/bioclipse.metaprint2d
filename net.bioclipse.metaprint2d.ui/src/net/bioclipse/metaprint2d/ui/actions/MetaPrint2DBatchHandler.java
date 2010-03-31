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
package net.bioclipse.metaprint2d.ui.actions;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.metaprint2d.ui.Activator;
import net.bioclipse.metaprint2d.ui.business.IMetaPrint2DManager;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

public class MetaPrint2DBatchHandler extends AbstractHandler{

//	private ISelection selection;
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ISelection selection=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (selection==null) {
			showError("Selection is empty");
			return null;
		}

		if (!(selection instanceof IStructuredSelection)){
			showError("Selection does not contain an SDF or CML file.");
			return null;
		}

		IStructuredSelection ssel = (IStructuredSelection) selection;

		IMetaPrint2DManager m2d=Activator.getDefault().getMetaPrint2DManager();

		List<String> errors=new ArrayList<String>();
		
		for (Object obj : ssel.toList()){
			if (obj instanceof IFile) {
				IFile file = (IFile) obj;
				try {
					m2d.calculate(file, true);
				} catch (Exception e) {
					e.printStackTrace();
					errors.add(" Error calculating file: '" + file + "': " + e.getMessage());
				}
			}
		}
		
		if (errors.size()>0){
			String emsg="Metaprint2D calculation experienced the following errors:\n";
			for (String er : errors){
				emsg=emsg+"\n - " + er;
			}
			showError(emsg);
		}
		
		return null;
		
	}

	private void showError(String emsg) {

		MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
				"Error", emsg);
		
	}

//	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
//		this.selection=selection;
//	}
	

}
