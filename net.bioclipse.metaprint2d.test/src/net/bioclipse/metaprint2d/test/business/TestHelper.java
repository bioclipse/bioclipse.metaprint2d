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
package net.bioclipse.metaprint2d.test.business;

 import static org.junit.Assert.*;

import java.util.List;

import net.sf.metaprint2d.MetaPrintResult;
import net.bioclipse.metaprint2d.ui.MetaPrint2DHelper;

import org.junit.Test;


public class TestHelper {

    @Test
    public void testParseMetaprint2DProperty(){

        String propertyString="<Metaprint2D NOR>"+
        "[(0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0),"+
        "(0/9;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/0;0.0), (0/1;0.0),"+
        "(0/0;0.0), (16/44;1.0)]";
        
        List<MetaPrintResult> res = MetaPrint2DHelper.getResultFromProperty( propertyString );
        
        assertEquals( 1, res.size() );
        MetaPrintResult mr=res.get( 0 );
        assertEquals( 14, mr.getAtomNumber() );
        assertEquals( 16, mr.getReactionCentreCount() );
        assertEquals( 44, mr.getSubstrateCount() );

    }

}
