/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.path;

import bibliothek.gui.dock.extension.css.CssNode;
import bibliothek.gui.dock.extension.css.CssNodeListener;

/**
 * This node offers no features other than a unmodifiable name.
 * @author Benjamin Sigg
 */
public class NamedCssNode implements CssNode{
	private String name;
	
	/**
	 * Creates a new node.
	 * @param name the name of this node, must not be <code>null</code>
	 */
	public NamedCssNode( String name ){
		if( name == null ){
			throw new IllegalArgumentException( "name must not be null" );
		}
		this.name = name;
	}
	
	@Override
	public String getName(){
		return name;
	}

	@Override
	public String getIdentifier(){
		return null;
	}

	@Override
	public boolean hasClass( String className ){
		return false;
	}

	@Override
	public boolean hasPseudoClass( String className ){
		return false;
	}

	@Override
	public String getProperty( String key ){
		return null;
	}

	@Override
	public void addNodeListener( CssNodeListener listener ){
		// ignore
	}

	@Override
	public void removeNodeListener( CssNodeListener listener ){
		// ignore
	}
}
