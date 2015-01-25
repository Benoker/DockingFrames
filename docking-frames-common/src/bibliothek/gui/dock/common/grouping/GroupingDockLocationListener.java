/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2015 Benjamin Sigg
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
package bibliothek.gui.dock.common.grouping;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.util.FrameworkOnly;

/**
 * A {@link DockHierarchyListener} that calls {@link DockableGrouping#hierarchyChanged(Dockable)}.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class GroupingDockLocationListener extends DockRegisterAdapter implements DockableFocusListener {
	/** The control from which to read the current {@link CGroupingBehavior} */
	private CControl control;
	
	/** The listener added to all {@link Dockable}s */
	private Listener listener = new Listener();
	
	/**
	 * Creates a new listener, without adding it to <code>ccontrol</code>.
	 * @param control the control from which to read the current {@link CGroupingBehavior}
	 */
	public GroupingDockLocationListener( CControl control ){
		this.control = control;
	}
	
	public void dockableRegistered( DockController controller, Dockable dockable ) {
		dockable.addDockHierarchyListener( listener );
		update( dockable );
	}
	
	public void dockableUnregistered( DockController controller, Dockable dockable ) {
		dockable.removeDockHierarchyListener( listener );
	}
	
	private void update( Dockable dockable ){
		CGroupingBehavior groupingBehavior = control.getProperty( CControl.GROUPING_BEHAVIOR );
		DockableGrouping grouping = groupingBehavior.getGrouping( dockable );
		if( grouping != null ){
			grouping.hierarchyChanged( dockable );
		}		
	}
	

	public void dockableFocused( DockableFocusEvent event ) {
		Dockable dockable = event.getNewFocusOwner();
		
		if( dockable != null ){
			CGroupingBehavior groupingBehavior = control.getProperty( CControl.GROUPING_BEHAVIOR );
			DockableGrouping grouping = groupingBehavior.getGrouping( dockable );
			if( grouping != null ){
				grouping.focusGained( dockable );
			}
		}
	}
	
	/**
	 * A {@link DockHierarchyListener} that is added to all known {@link Dockable}s.
	 * @author Benjamin Sigg
	 */
	private class Listener implements DockHierarchyListener {
		public void hierarchyChanged( DockHierarchyEvent event ) {
			update( event.getDockable() );
		}

		public void controllerChanged( DockHierarchyEvent event ) {
			// ignored
		}
	}
}
