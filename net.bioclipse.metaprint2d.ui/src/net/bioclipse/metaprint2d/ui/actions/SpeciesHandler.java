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
package net.bioclipse.metaprint2d.ui.actions;

import java.util.Map;

import net.bioclipse.metaprint2d.Metaprinter;
import net.bioclipse.metaprint2d.ui.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;



public class SpeciesHandler extends AbstractHandler implements IElementUpdater {
    
    private static final String PARM_INFO = "net.bioclipse.dropdown.radio.info";
    private String fCurrentValue;
    
    public SpeciesHandler() {
    	
    	//Set default
    	fCurrentValue="All";
	}
    
    public Object execute(ExecutionEvent event) throws ExecutionException {
      String parm = event.getParameter(PARM_INFO);
      if (parm.equals(fCurrentValue)) {
        return null; // in theory, we're already in the correct state
      }
    
      // do whatever having "parm" active implies
      fCurrentValue = parm;
    
      Metaprinter.setSpecies(parm);
    
      // update our radio button states ... get the service from
      // a place that's most appropriate
      ICommandService service = (ICommandService) HandlerUtil
          .getActiveWorkbenchWindowChecked(event).getService(
              ICommandService.class);
      service.refreshElements(event.getCommand().getId(), null);
      
      //Clear any current M2D
      ClearMetaPrint2DHandler cha=new ClearMetaPrint2DHandler();
      cha.execute( null );
      
      Activator.getDefault().fireAutoM2DifTurnedOn();
      
      return null;
    }
    
    public void updateElement(UIElement element, Map parameters) {
      String parm = (String) parameters.get(PARM_INFO);
      if (parm != null) {
        if (fCurrentValue != null && fCurrentValue.equals(parm)) {
          element.setChecked(true);
          
          //Set this as species
          Metaprinter.setSpecies(parm);
          
        } else {
          element.setChecked(false);
        }
      }
    }
   }
