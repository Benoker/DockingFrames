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

package bibliothek.gui.dock.toolbar.expand;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * The {@link ExpandableStateController} is a helper class intended for
 * {@link ExpandableToolbarItem}s, it finds the first parent of a
 * {@link ExpandableToolbarItem} which is acknowledged by the current
 * {@link ExpandableToolbarItemStrategy} and changes the {@link ExpandedState}
 * of the item to the {@link ExpandedState} of the parent.
 * 
 * @author Benjamin Sigg
 */
public class ExpandableStateController {
	/** the observed item */
	private final ExpandableToolbarItem item;

	/** the currently observed controller */
	private DockController controller;

	/** the current strategy */
	private final PropertyValue<ExpandableToolbarItemStrategy> strategy = new PropertyValue<ExpandableToolbarItemStrategy>( ExpandableToolbarItemStrategy.STRATEGY ){
		@Override
		protected void valueChanged( ExpandableToolbarItemStrategy oldValue, ExpandableToolbarItemStrategy newValue ){
			if( oldValue != null ) {
				oldValue.removeExpandedListener( strategyListener );
			}
			if( (newValue != null) && (controller != null) ) {
				newValue.addExpandedListener( strategyListener );
			}
			refresh();
		}
	};

	private final ExpandableToolbarItemStrategyListener strategyListener = new ExpandableToolbarItemStrategyListener(){
		@Override
		public void stretched( Dockable item ){
			if( DockUtilities.isAncestor( item, getItem() ) ) {
				refresh();
			}
		}

		@Override
		public void shrunk( Dockable item ){
			if( DockUtilities.isAncestor( item, getItem() ) ) {
				refresh();
			}
		}

		@Override
		public void expanded( Dockable item ){
			if( DockUtilities.isAncestor( item, getItem() ) ) {
				refresh();
			}
		}

		@Override
		public void enablementChanged( Dockable item, ExpandedState state, boolean enabled ){
			// ignore
		}
	};

	/**
	 * Creates a new controller.
	 * 
	 * @param item
	 *            the item to observe
	 */
	public ExpandableStateController( ExpandableToolbarItem item ){
		if( item == null ){
			throw new IllegalArgumentException( "item must not be null" );
		}
		this.item = item;

		item.addDockHierarchyListener( new DockHierarchyListener(){
			@Override
			public void hierarchyChanged( DockHierarchyEvent event ){
				refresh();
			}

			@Override
			public void controllerChanged( DockHierarchyEvent event ){
				controller = getItem().getController();
				strategy.setProperties( controller );
			}
		} );
		strategy.setProperties( getItem().getController() );
		refresh();
	}

	/**
	 * Gets the item which is observed by this controller.
	 * 
	 * @return the observed item, not <code>null</code>
	 */
	public ExpandableToolbarItem getItem(){
		return item;
	}

	/**
	 * Searches the first parent of {@link #getItem() the item} which is
	 * acknowledged by the current {@link ExpandableToolbarItemStrategy} and
	 * updates the {@link ExpandedState} of the item such that it has the same
	 * state as its parent.
	 */
	public void refresh(){
		if( item != null ){
			DockStation station = item.getDockParent();
			if( station != null ){
				Dockable current = station.asDockable();
				final ExpandableToolbarItemStrategy strategy = this.strategy.getValue();
				if( strategy != null ) {
					while( current != null ) {
						final ExpandedState state = strategy.getState( current );
						if( state != null ) {
							item.setExpandedState( state );
							return;
						}
		
						station = current.getDockParent();
						if( station != null ) {
							current = station.asDockable();
						}
						else {
							current = null;
						}
					}
				}
			}
		}
	}
}
