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

package bibliothek.gui.dock.station.toolbar.group;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ComponentListener;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.toolbar.title.ColumnDockActionSource;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.toolbar.expand.ExpandableToolbarItemStrategyListener;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * This is a helper class enabling support for {@link ExpandedState} of the children of a {@link ToolbarGroupDockStation}.
 * This class provides all the tasks that may be useful:
 * <ul>
 * 	<li>It makes sure that during drag and drop operations the dragged dockable changes its state if necessary</li>
 *  <li>It provides a {@link ColumnDockActionSource} to allow implementation of a {@link DockTitle} showing
 *  actions for the different columns.</li>
 * </ul> 
 * @author Benjamin Sigg
 */
public class ToolbarGroupExpander {
	private Actions actions;
	private ToolbarGroupDockStation station;
	private DockController controller;

	private ColumnHandler columnHandler = new ColumnHandler();
	private StrategyHandler strategyHandler = new StrategyHandler();

	private PropertyValue<ExpandableToolbarItemStrategy> strategy = new PropertyValue<ExpandableToolbarItemStrategy>( ExpandableToolbarItemStrategy.STRATEGY ){
		@Override
		protected void valueChanged( ExpandableToolbarItemStrategy oldValue, ExpandableToolbarItemStrategy newValue ){
			if( oldValue != null ) {
				oldValue.removeExpandedListener( strategyHandler );
			}
			if( newValue != null ) {
				newValue.addExpandedListener( strategyHandler );
				columnHandler.validateAll();
			}
		}
	};

	/**
	 * Creates a new expander
	 * @param station the station for which this expander will work
	 */
	public ToolbarGroupExpander( ToolbarGroupDockStation station ){
		this.station = station;
		setController( station.getController() );
		station.getColumnModel().addListener( new ToolbarColumnModelListener<Dockable,StationChildHandle>(){
			@Override
			public void removed( ToolbarColumnModel<Dockable,StationChildHandle> model, ToolbarColumn<Dockable,StationChildHandle> column, int index ){
				column.removeListener( columnHandler );
			}

			@Override
			public void inserted( ToolbarColumnModel<Dockable,StationChildHandle> model, ToolbarColumn<Dockable,StationChildHandle> column, int index ){
				column.addListener( columnHandler );
			}
		} );
	}

	/**
	 * Sets the {@link DockController} in whose realm this expander works
	 * @param controller the controller
	 */
	public void setController( DockController controller ){
		if( this.controller != controller ) {
			if( actions != null ) {
				actions.setModel( null );
				actions.destroy();
				actions = null;
			}
			this.controller = controller;
			strategy.setProperties( controller );
			if( controller != null ) {
				actions = new Actions( controller );
				actions.setModel( station.getColumnModel() );
			}
		}
	}

	/**
	 * Gets a {@link ColumnDockActionSource} which allows users to change the {@link ExpandedState} of an entire
	 * column of {@link Dockable}s.
	 * @return the source
	 */
	public ColumnDockActionSource getActions(){
		return actions;
	}

	private ExpandableToolbarItemStrategy getStrategy(){
		return strategy.getValue();
	}

	private class StrategyHandler implements ExpandableToolbarItemStrategyListener {
		@Override
		public void enablementChanged( Dockable item, ExpandedState state, boolean enabled ){
			ToolbarColumn<Dockable,StationChildHandle> column = station.getColumnModel().getColumn( item );
			if( column != null ) {
				columnHandler.validate( column );
			}
		}

		@Override
		public void expanded( Dockable item ){
			// ignore
		}

		@Override
		public void shrunk( Dockable item ){
			// ignore			
		}

		@Override
		public void stretched( Dockable item ){
			// ignore
		}
	}

	/**
	 * Ensures that all the items of a column have the same {@link ExpandedState}.
	 */
	private class ColumnHandler implements ToolbarColumnListener<Dockable,StationChildHandle> {
		@Override
		public void inserted( ToolbarColumn<Dockable,StationChildHandle> column, StationChildHandle item, final Dockable dockable, int index ){
			final ExpandableToolbarItemStrategy strategy = getStrategy();
			if( strategy != null ) {
				int count = 0;
				int length = column.getDockableCount();
				ExpandedState state = null;
				while( count < length && state == null ) {
					if( count != index ) {
						state = strategy.getState( column.getDockable( count++ ) );
					}
					else {
						count++;
					}
				}
				if( state != null ) {
					if( controller != null ) {
						final ExpandedState newState = state;
						controller.getHierarchyLock().onRelease( new Runnable(){
							@Override
							public void run(){
								set( strategy, dockable, newState );
								actions.update( dockable );
							}
						} );
					}
					else {
						set( strategy, dockable, state );
					}
				}
			}
		}

		@Override
		public void removed( ToolbarColumn<Dockable,StationChildHandle> column, StationChildHandle item, Dockable dockable, int index ){
			// ignore
		}

		public void validateAll(){
			ToolbarColumnModel<Dockable,StationChildHandle> model = station.getColumnModel();
			for( int i = 0, n = model.getColumnCount(); i < n; i++ ) {
				validate( model.getColumn( i ) );
			}
		}

		/**
		 * Tries to ensure that all items in <code>column</code> have the same {@link ExpandedState}, namely the
		 * {@link ExpandedState} of the first item.
		 * @param column the column to validate
		 */
		public void validate( ToolbarColumn<Dockable,StationChildHandle> column ){
			ExpandableToolbarItemStrategy strategy = getStrategy();
			if( strategy != null ) {
				int index = 0;
				int length = column.getDockableCount();
				ExpandedState state = null;
				while( index < length && state == null ) {
					state = strategy.getState( column.getDockable( index++ ) );
				}
				while( index < length ) {
					Dockable item = column.getDockable( index++ );
					set( strategy, item, state );
				}
			}
		}

		private void set( ExpandableToolbarItemStrategy strategy, Dockable item, ExpandedState state ){
			if( strategy.isEnabled( item, state ) ) {
				strategy.setState( item, state );
				return;
			}
			ExpandedState smaller = state;
			while( smaller != smaller.smaller() ) {
				smaller = smaller.smaller();
				if( strategy.isEnabled( item, smaller ) ) {
					strategy.setState( item, smaller );
					return;
				}
			}
			ExpandedState larger = state;
			while( larger != larger.larger() ) {
				larger = larger.larger();
				if( strategy.isEnabled( item, larger ) ) {
					strategy.setState( item, larger );
					return;
				}
			}
		}
	}

	/**
	 * The actions that are shown over the columns.
	 */
	private class Actions extends ExpandToolbarGroupActions<StationChildHandle> {
		public Actions( DockController controller ){
			super( controller, station );
		}

		@Override
		protected void uninstallListener( StationChildHandle item, ComponentListener listener ){
			item.getDockable().getComponent().removeComponentListener( listener );
		}

		@Override
		protected void installListener( StationChildHandle item, ComponentListener listener ){
			item.getDockable().getComponent().addComponentListener( listener );
		}

		@Override
		protected Rectangle getBoundaries( StationChildHandle item ){
			Rectangle result = getBoundaries( item.getDockable().getComponent() );
			Rectangle title = null;
			if( item.getTitle() != null ) {
				title = getBoundaries( item.getTitle().getComponent() );
				if( result == null ){
					result = title;
				}
				else{
					result = result.union( title );
				}
			}
			return result;
		}

		private Rectangle getBoundaries( Component component ){
			Rectangle bounds = component.getBounds();
			if( SwingUtilities.isDescendingFrom( component, station.getComponent() ) ) {
				return SwingUtilities.convertRectangle( component.getParent(), bounds, station.getComponent() );
			}
			else {
				return null;
			}
		}
	}
}
