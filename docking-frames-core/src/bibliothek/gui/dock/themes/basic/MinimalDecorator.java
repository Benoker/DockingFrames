/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.themes.basic;

import java.awt.Component;
import java.awt.Insets;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.action.DockActionSource;

/**
 * Minimalistic implementation of a {@link BasicDockableDisplayerDecorator}. Just shows the
 * element which represents the selected {@link Dockable}.
 * @author Benjamin Sigg
 *
 */
public class MinimalDecorator implements BasicDockableDisplayerDecorator{
	private Component content;
	
	public Component getComponent(){
		return content;
	}

	public void setController( DockController controller ){
		// ignore
	}

	public void setDockable( Component content, Dockable dockable ){
		this.content = content;
	}
	
	public DockActionSource getActionSuggestion(){
		return null;
	}
	
	public Insets getDockableInsets(){
		return new Insets( 0, 0, 0, 0 );
	}
	
	public DockElementRepresentative getMoveableElement(){
		return null;
	}
	
	public void addDecoratorListener( BasicDockableDisplayerDecoratorListener listener ){
		// ignored
	}
	
	public void removeDecoratorListener( BasicDockableDisplayerDecoratorListener listener ){
		// ignored	
	}
}
