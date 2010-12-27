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
package bibliothek.gui.dock.util;

import java.awt.Component;
import java.awt.Point;

import javax.swing.event.MouseInputListener;

import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockElementRepresentative;

/**
 * A very simplistic implementation of {@link DockElementRepresentative}, it just adds
 * a listener to a {@link Component}.
 * @author Benjamin Sigg
 */
public class SimpleDockElementRepresentative implements DockElementRepresentative{
	private Component component;
	private DockElement element;
	
	/**
	 * Creates a new {@link SimpleDockElementRepresentative}.
	 * @param element the element which is represented by <code>this</code>
	 * @param component the component to which <code>this</code> adds listeners
	 */
	public SimpleDockElementRepresentative( DockElement element, Component component ){
		if( element == null ){
			throw new IllegalArgumentException( "element must not be null" );
		}
		if( component == null ){
			throw new IllegalArgumentException( "component must not be null" );
		}
		
		this.element = element;
		this.component = component;
	}
	
	public void addMouseInputListener( MouseInputListener listener ){
		component.addMouseListener( listener );
		component.addMouseMotionListener( listener );
	}

	public Component getComponent(){
		return component;
	}

	public DockElement getElement(){
		return element;
	}

	public Point getPopupLocation( Point click, boolean popupTrigger ){
		if( popupTrigger ){
			return click;
		}
		return null;
	}

	public boolean isUsedAsTitle(){
		return false;
	}
	
	public boolean shouldFocus(){
    	return true;
    }
	
	public boolean shouldTransfersFocus(){
		return true;
	}

	public void removeMouseInputListener( MouseInputListener listener ){
		component.removeMouseListener( listener );
		component.removeMouseMotionListener( listener );
	}
}
