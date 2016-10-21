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
package bibliothek.gui.dock.station.screen;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;

/**
 * This extension is used by {@link ScreenDockStation} and changes the functionality of some
 * methods.
 * @author Benjamin Sigg
 */
public interface ScreenDockStationExtension {
	/**
	 * Tells whether replacing <code>old</code> with <code>next</code> is possible on <code>station</code>.
	 * @param station the source of the call
	 * @param old a child of <code>station</code>
	 * @param next a possible replacement of <code>old</code>
	 * @return whether replacing <code>old</code> with <code>next</code> is allowed
	 */
	public boolean canReplace( ScreenDockStation station, Dockable old, Dockable next );
	
	/**
	 * Called by {@link ScreenDockStation#drop(Dockable, ScreenDockProperty, boolean)}. This method may modify
	 * the arguments in any way it likes.
	 * @param station the source of the event
	 * @param arguments the arguments of the method, can be modified
	 */
	public void drop( ScreenDockStation station, DropArguments arguments );

	/**
	 * Called after a (modified) call to {@link ScreenDockStation#drop(Dockable, ScreenDockProperty, boolean)}
	 * ended.
	 * @param station the source of the event
	 * @param arguments the arguments that were used by the method, should not be modified
	 * @param successful whether the call was successful
	 */
	public void dropped( ScreenDockStation station, DropArguments arguments, boolean successful );

	/**
	 * A class offering all the arguments of the method {@link ScreenDockStation#drop(Dockable, ScreenDockProperty, boolean)}
	 * @author Benjamin Sigg
	 */
	public static class DropArguments {
		private Dockable dockable;
		private ScreenDockProperty property;
		private boolean boundsIncludeWindow;
		private ScreenDockWindow window;

		/**
		 * Sets the dockable which is to be dropped.
		 * @param dockable the dockable, must not be <code>null</code>
		 */
		public void setDockable( Dockable dockable ){
			if( dockable == null ) {
				throw new IllegalArgumentException( "dockable must not be null" );
			}
			this.dockable = dockable;
		}

		/**
		 * Gets the element which is about to be dropped
		 * @return the element to drop
		 */
		public Dockable getDockable(){
			return dockable;
		}

		/**
		 * Sets the location at which the element is to be dropped.
		 * @param property the new location at which to drop the element, not <code>null</code>
		 */
		public void setProperty( ScreenDockProperty property ){
			if( property == null ) {
				throw new IllegalArgumentException( "property must not be null" );
			}
			this.property = property;
		}

		/**
		 * Gets the location at which the element will be dropped
		 * @return the location, not <code>null</code>
		 */
		public ScreenDockProperty getProperty(){
			return property;
		}

		/**
		 * Sets whether the boundaries of the location include the entire window.
		 * @param boundsIncludeWindow whether the entire window is included
		 * @see #isBoundsIncludeWindow()
		 */
		public void setBoundsIncludeWindow( boolean boundsIncludeWindow ){
			this.boundsIncludeWindow = boundsIncludeWindow;
		}

		/**
		 * If <code>true</code>, the bounds describe the size
		 * of the resulting window. Otherwise the size of the window will be a bit larger
		 * such that the title can be shown in the new space
		 * @return whether the bounds include the window
		 */
		public boolean isBoundsIncludeWindow(){
			return boundsIncludeWindow;
		}
		
		/**
		 * Sets the window with which the element will be merged
		 * @param window the merging window, can be <code>null</code>
		 */
		public void setWindow( ScreenDockWindow window ){
			this.window = window;
		}
		
		/**
		 * Gets the window with which the element will be merged
		 * @return the merging window
		 */
		public ScreenDockWindow getWindow(){
			return window;
		}
	}
}
