/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.common.theme.color;

import java.awt.Color;

import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.util.FrameworkOnly;

/**
 * This {@link ColorBridge} uses a delegate and applies a set of {@link CColorBridge}s.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class ExtendedColorBridge implements ColorBridge{
	private ColorBridge bridge;
	private CColorBridge[] extensions;
	
	/**
	 * Creates a new bridge.
	 * @param bridge the delegate
	 * @param extensions the extensions that are asked first
	 */
	public ExtendedColorBridge( ColorBridge bridge, CColorBridge[] extensions ){
		this.bridge = bridge;
		this.extensions = extensions;
	}
	
	public void add( String id, DockColor uiValue ){
		for( CColorBridge bridge : extensions ){
			if( bridge.matches( id )){
				bridge.add( id, uiValue );
				return;
			}
		}
		bridge.add( id, uiValue );
	}

	public void remove( String id, DockColor uiValue ){
		for( CColorBridge bridge : extensions ){
			if( bridge.matches( id )){
				bridge.remove( id, uiValue );
				return;
			}
		}
		bridge.remove( id, uiValue );	
	}

	public void set( String id, Color value, DockColor uiValue ){
		for( CColorBridge bridge : extensions ){
			if( bridge.matches( id )){
				bridge.set( id, value, uiValue );
				return;
			}
		}
		bridge.set( id, value, uiValue );	
	}
}
