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
package bibliothek.gui.dock.themes;

import java.awt.Component;

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.focus.DockableSelection;
import bibliothek.gui.dock.focus.DockableSelectionListener;

/**
 * A {@link DockableSelection} that forwards any calls to the {@link DockableSelection} of the current
 * {@link DockTheme}.
 * @author Benjamin Sigg
 */
public class ThemeDockableSelection implements DockableSelection{
	private DockController controller;
	
	/** the {@link DockableSelection} that is currently open */
	private DockableSelection current;
	
	/** a listener added to {@link #current} */
	private DockableSelectionListener listener = new DockableSelectionListener(){
		public void selected( Dockable dockable ){
			closed();
		}
		
		public void considering( Dockable dockable ){
			// ignore
		}
		
		public void canceled(){
			closed();
		}
	};
	
	/**
	 * Creates a new object
	 * @param controller the controller whose {@link DockTheme} will be used
	 */
	public ThemeDockableSelection( DockController controller ){
		this.controller = controller;
	}
	
	private DockableSelection get(){
		if( current != null ){
			return current;
		}
		return controller.getTheme().getDockableSelection( controller );
	}
	
	private void closed(){
		if( current != null ){
			current.removeDockableSelectionListener( listener );
			current = null;
		}
	}
	
	public void addDockableSelectionListener( DockableSelectionListener listener ){
		get().addDockableSelectionListener( listener );	
	}

	public void close(){
		get().close();
		closed();
	}

	public Component getComponent(){
		return get().getComponent();
	}

	public boolean hasChoices( DockController controller ){
		return get().hasChoices( controller );
	}

	public void open( DockController controller ){
		if( current == null ){
			current = get();
			current.addDockableSelectionListener( listener );
		}
		current.open( controller );
	}

	public void removeDockableSelectionListener( DockableSelectionListener listener ){
		get().removeDockableSelectionListener( listener );
	}
}
