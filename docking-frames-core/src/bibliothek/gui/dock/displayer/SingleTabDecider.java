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
package bibliothek.gui.dock.displayer;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.event.SingleTabDeciderListener;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;

/**
 * Decides for {@link Dockable}s whether there should be a single tab
 * shown for them.
 * @author Benjamin Sigg
 */
public interface SingleTabDecider {
	/** this decider never shows a tab */
	public static SingleTabDecider NONE = new SingleTabDecider(){
		public boolean showSingleTab( DockStation station, Dockable dockable ){
			return false;
		}
		public void addSingleTabDeciderListener( SingleTabDeciderListener arg0 ){
			// ignore
		}
		public void removeSingleTabDeciderListener( SingleTabDeciderListener arg0 ){
			// ignore	
		}
	};
	
	/** this decider shows a tab, unless the element is a station itself or on a {@link StackDockStation} */
	public static SingleTabDecider ALLWAYS = new SingleTabDecider(){
		public boolean showSingleTab( DockStation station, Dockable dockable ){
			if( dockable.asDockStation() != null )
				return false;
			if( station instanceof StackDockStation )
				return false;
			return true;
		}
		public void addSingleTabDeciderListener( SingleTabDeciderListener arg0 ){
			// ignore
		}
		public void removeSingleTabDeciderListener( SingleTabDeciderListener arg0 ){
			// ignore	
		}
	};
	
	/** Key for the {@link DockProperties} */
	public static final PropertyKey<SingleTabDecider> SINGLE_TAB_DECIDER =
		new PropertyKey<SingleTabDecider>( "dock.single_tab_decider", 
				new ConstantPropertyFactory<SingleTabDecider>( NONE ), true );
	
	/**
	 * Decides whether to show a single tab for <code>dockable</code>
	 * @param station the parent of <code>dockable</code>
	 * @param dockable the element for which a single tab might be shown
	 * @return <code>true</code> if a tab should be shown, <code>false</code>
	 * if not
	 */
	public boolean showSingleTab( DockStation station, Dockable dockable );
	
	/**
	 * Adds a listener to this decider, the listener is to be informed if
	 * the single tab property of a {@link Dockable} changes.
	 * @param listener the new listener
	 */
	public void addSingleTabDeciderListener( SingleTabDeciderListener listener );
	
	/**
	 * Removes a listener from this decider.
	 * @param listener the listener to remove
	 */
	public void removeSingleTabDeciderListener( SingleTabDeciderListener listener );
}
