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
package net.bioclipse.metaprint2d.ui.prefs;

import net.bioclipse.metaprint2d.ui.Activator;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class MetaprintPrefs extends FieldEditorPreferencePage implements
IWorkbenchPreferencePage {

    public static final String SIMILARITY_OPERATOR_DEFAULT = "DEFAULT";
    public static final String SIMILARITY_OPERATOR_STRICT = "STRICT";
    public static final String SIMILARITY_OPERATOR_LOOSE = "LOOSE";

    public static final String OPENBABEL_PATH = "openbabel_path";
	
    public static final String METAPRINT_ATOMTYPING = "metaprintAtomTyping";
    public static final String CDK_ATOMTYPING = "cdk";
    public static final String OPENBABEL_ATOMTYPING = "openbabel";

    public static final String RENDER_SOLID_CIRCLES = "RenderSolidCircles";
    public static final String CIRCLE_RADIUS = "CircleRadius";
    public static final String RENDER_MISSING_GREY = "MissingGray";

    private IWorkbench workbench;

//    RefreshableFileFieldEditor obFileEditor;
//    Combo cboAtomType;

    
    public MetaprintPrefs() {
      super("Metaprint", GRID);
      
      IPreferenceStore store = Activator.getDefault().getPreferenceStore();
      setPreferenceStore(store);
    }

    @Override
    protected void createFieldEditors() {
        
        BooleanFieldEditor solidCircles= new BooleanFieldEditor(
                                           MetaprintPrefs.RENDER_SOLID_CIRCLES, 
                                           "Solid circles", 
                                           getFieldEditorParent());
        addField(solidCircles);

//        BooleanFieldEditor missingGray= new BooleanFieldEditor(
//                                             MetaprintPrefs.RENDER_MISSING_GREY, 
//                                             "No data rendered as grey", 
//                                             getFieldEditorParent());
//        addField(missingGray);

        IntegerFieldEditor circleRadius = new IntegerFieldEditor(
                                           MetaprintPrefs.CIRCLE_RADIUS, 
                                           "Circle radius (1-10)", 
                                           getFieldEditorParent());
        
        addField(circleRadius);
        
/*
    	Label lblAtomTyping=new Label(getFieldEditorParent(),SWT.NULL);
    	lblAtomTyping.setText("Atom typing: ");
    	lblAtomTyping.setLayoutData(new GridData());
    	
    	cboAtomType=new Combo(getFieldEditorParent(),SWT.NULL);
    	cboAtomType.add(MetaprintPrefs.CDK_ATOMTYPING,0);
//    	cboAtomType.add(MetaprintPrefs.OPENBABEL_ATOMTYPING,1);
    	GridData gd=new GridData(GridData.FILL_HORIZONTAL);
    	gd.horizontalSpan=2;
    	cboAtomType.setLayoutData(gd);
    	
    	obFileEditor=new RefreshableFileFieldEditor(MetaprintPrefs.OPENBABEL_PATH, 
    			"Path to ObenBabel: ", true, getFieldEditorParent());
    	obFileEditor.setValidateStrategy(FileFieldEditor.VALIDATE_ON_KEY_STROKE);
    	
    	cboAtomType.addSelectionListener(new SelectionAdapter(){
    		
			public void widgetSelected(SelectionEvent e) {
				if (cboAtomType.getSelectionIndex()==0){
					getPreferenceStore().setValue(METAPRINT_ATOMTYPING, CDK_ATOMTYPING);
					obFileEditor.setEnabled(false, getFieldEditorParent());
				}
				if (cboAtomType.getSelectionIndex()==1){
					getPreferenceStore().setValue(METAPRINT_ATOMTYPING, OPENBABEL_ATOMTYPING);
					obFileEditor.setEnabled(true, getFieldEditorParent());
					}
				obFileEditor.refreshValidState();
				checkState();
				Display.getDefault().syncExec(new Runnable(){

					public void run() {
						updateApplyButton();
						getContainer().updateButtons();
					}
					
				});
			}
    	});
    	
    	
    	addField(obFileEditor);

    	//Initialize GUI after prefs
    	if (getPreferenceStore().getString(METAPRINT_ATOMTYPING)
    			.equals(CDK_ATOMTYPING)){
    		obFileEditor.setEnabled(false, getFieldEditorParent());
    		cboAtomType.select(0);
    	}
    	else if (getPreferenceStore().getString(METAPRINT_ATOMTYPING)
    			.equals(OPENBABEL_ATOMTYPING)){
    		obFileEditor.setEnabled(true, getFieldEditorParent());
    		cboAtomType.select(1);
    	}
    	
		obFileEditor.refreshValidState();
    	checkState();
    	
    	*/
    	
    }

    public void init(IWorkbench workbench) {
      this.workbench = workbench;
    }
    
//    @Override
//    protected void performDefaults() {
//
//    	//We need to set our pref
//		getPreferenceStore().setValue(METAPRINT_ATOMTYPING, 
//				getPreferenceStore().getDefaultString(METAPRINT_ATOMTYPING));
//
//		
//    getPreferenceStore().setValue(METAPRINT_ATOMTYPING, 
//                   getPreferenceStore().getDefaultString(METAPRINT_ATOMTYPING));
//
//    
//    getPreferenceStore().setValue(CIRCLE_RADIUS, 
//                          getPreferenceStore().getDefaultString(CIRCLE_RADIUS));
//
//    getPreferenceStore().setValue(RENDER_SOLID_CIRCLES, 
//                   getPreferenceStore().getDefaultString(RENDER_SOLID_CIRCLES));
//
//		//Default UI setup
////		obFileEditor.setEnabled(false, getFieldEditorParent());
////		cboAtomType.select(0);
////
////		obFileEditor.refreshValidState();
//		checkState();
//
//    }

  }
