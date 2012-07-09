/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.wizard;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.split.Leaf;
import bibliothek.gui.dock.station.split.SplitDockAccess;

/**
 * This leaf has a modified {@link #getPut(int, int, double, double, Dockable)} method
 * that takes into account, how the {@link WizardSplitDockStation} changes its size when
 * {@link Span}s are present. 
 * @author Benjamin Sigg
 */
public class WizardLeaf extends Leaf{
	public WizardLeaf( SplitDockAccess access, long id ){
		super( access, id );
	}
	
//	@Override
//	public PutInfo getPut( int x, int y, double factorW, double factorH, Dockable drop ){
//		DockableDisplayer displayer = getDisplayer();
//		if( displayer == null ){
//			return null;
//		}
//		
//		Rectangle bounds = removeTitle( getBounds() );
//		Rectangle boundsMinusSpan = getAccess().getSpanStrategy().modifyBounds( bounds, this );
//
//		boolean centered = false;
//		PutInfo result = null;
//		
//        if( isTitlePut( bounds, x, y )){
//        	centered = true;
//        	result = getAccess().validatePutInfo( new PutInfo( this, PutInfo.Put.TITLE, drop, true ));
//        }
//        
//        if( result != null ){
//        	return result;
//        }
//		
//        if( isCenterPut( boundsMinusSpan, x, y )){
//        	centered = true;
//            result = getAccess().validatePutInfo( new PutInfo( this, PutInfo.Put.CENTER, drop, true ));
//        }
//        
//        if( result != null ){
//        	return result;
//        }
//        
//		
//		result = createSidePut( boundsMinusSpan, x, y, drop, false );
//		
//        if( result != null )
//            return result;
//        
//        return getAccess().validatePutInfo( new PutInfo( this, PutInfo.Put.CENTER, drop, centered ));
//	}
}
