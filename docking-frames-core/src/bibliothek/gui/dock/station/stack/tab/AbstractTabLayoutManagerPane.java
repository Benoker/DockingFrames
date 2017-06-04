/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.station.stack.tab;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * Information about a {@link TabPane} that gets laid out by a {@link AbstractTabLayoutManager}.
 * This class implements  {@link TabPaneListener}, the listener is added and removed from the 
 * {@link TabPane} automatically.
 * @author Benjamin Sigg
 */
public class AbstractTabLayoutManagerPane implements TabPaneListener{
	/** the panel that is laid out */
	private TabPane pane;
	
	private PropertyValue<Boolean> useSmallMinimumSize = new PropertyValue<Boolean>( TabPane.USE_SMALL_MINIMUM_SIZE ) {
		@Override
		protected void valueChanged( Boolean oldValue, Boolean newValue ) {
			// nothing
		}
	};
	
	/**
	 * Creates a new info.
	 * @param pane the owner
	 */
	public AbstractTabLayoutManagerPane( TabPane pane ){
		if( pane == null )
			throw new IllegalStateException( "pane must not be null" );
		this.pane = pane;
	}
	
	/**
	 * Gets the owner of this info.
	 * @return the owner, not <code>null</code>
	 */
	public TabPane getPane(){
		return pane;
	}

	public void added( TabPane pane, Dockable dockable ){
		// ignore
	}

	public void infoComponentChanged( TabPane pane, LonelyTabPaneComponent oldInfo, LonelyTabPaneComponent newInfo ){
		// ignore
	}

	public void removed( TabPane pane, Dockable dockable ){
		// ignore
	}

	public void selectionChanged( TabPane pane ){
		// ignore
	}

	/**
	 * Called by the {@link AbstractTabLayoutManager} once this pane is no longer in use.
	 */
	public void uninstalled(){
		useSmallMinimumSize.setProperties( (DockController)null );
	}
	
	public void controllerChanged( TabPane pane, DockController controller ){
		useSmallMinimumSize.setProperties( controller );
	}
	
	protected boolean isUseSmallMinimumSize(){
		return useSmallMinimumSize.getValue();
	}
}