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
package bibliothek.gui.dock.control.focus;

import java.util.LinkedList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * The default implementation of {@link FocusHistory} adds listeners to a 
 * {@link DockController} to keep the history up to date. Only {@link Dockable}s that
 * are actually registered at the controller are reported in the history
 * @author Benjamin Sigg
 */
public class DefaultFocusHistory implements FocusHistory{
	/** the controller which is observed */
	private DockController controller;
	
	/** the actual history */
	private List<Dockable> history = new LinkedList<Dockable>();
	
	/** the listeners that are added to {@link #controller} */
	private Listener listener = new Listener();
	
	public void setController( DockController controller ){
		if( this.controller != null ){
			this.controller.getRegister().removeDockRegisterListener( listener );
			this.controller.getFocusController().removeDockableFocusListener( listener );
		}
		history.clear();
		this.controller = controller;
		if( this.controller != null ){
			this.controller.getRegister().addDockRegisterListener( listener );
			this.controller.getFocusController().addDockableFocusListener( listener );
		}
	}
	
	public Dockable[] getHistory(){
		Dockable[] result = new Dockable[ history.size() ];
		int index = result.length-1;
		for( Dockable item : history ){
			result[ index-- ] = item;
		}
		return result;
	}
	
	public Dockable getNewestOn( DockStation... parents ){
		for( Dockable item : history ){
			for( DockStation station : parents ){
				if( DockUtilities.isAncestor( station, item )){
					return item;
				}
			}
		}
		return null;
	}
	
	/**
	 * A set of listeners that are needed to find out which {@link Dockable} currently has the
	 * focus, and which {@link Dockable}s are to be removed.
	 * @author Benjamin Sigg
	 */
	private class Listener extends DockRegisterAdapter implements DockableFocusListener{
		public void dockableUnregistered( DockController controller, Dockable dockable ){
			history.remove( dockable );
		}
		
		public void dockableFocused( DockableFocusEvent event ){
			Dockable owner = event.getNewFocusOwner();
			if( owner != null ){
				history.remove( owner );
				history.add( 0, owner );
			}
		}
	}
}
