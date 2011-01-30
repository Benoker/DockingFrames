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
package bibliothek.gui.dock.dockable;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * An event sent to the {@link DockableStateListener}.<br>
 * Note: if the hierarchy changed, the flag {@link #FLAG_VISIBILITY} will be set correctly. All the other 
 * flags are not set.
 * @author Benjamin Sigg
 */
public class DockableStateEvent {
	/** Indicates that the internal location of the {@link Dockable} on its {@link DockStation} changed. */
	public static final int FLAG_LOCATION_CHANGED = 1;

	/** Indicates that the internal location of one of the anchestors of the {@link Dockable} changed */
	public static final int FLAG_PARENT_LOCATION_CHANGED = 2;

	/** Indicates that the {@link Dockable} was either made visible or invisible to the user */
	public static final int FLAG_VISIBILITY = 4;

	/** Indicates that the {@link Dockable} is or was selected on its parent */
	public static final int FLAG_SELECTION = 8;
	
	/** Indicates that the selection state of a parent of the {@link Dockable} changed */
	public static final int FLAG_PARENT_SELECTION = 16;

	/** Indicates that the parent of the {@link Dockable} has changed */
	public static final int FLAG_HIERARCHY = 32;
	
	/** the source of the event */
	private Dockable dockable;
	
	/** exact changes */
	private int flags;
	
	/**
	 * Creates a new event.
	 * @param dockable the dockables that is affected
	 * @param flags all the details about the changes
	 */
	public DockableStateEvent( Dockable dockable, int flags ){
		this.dockable = dockable;
		this.flags = flags;
	}

	/**
	 * Gets the element which is affected
	 * @return the element, not <code>null</code>
	 */
	public Dockable getDockable(){
		return dockable;
	}

	/**
	 * Gets all the changes composed into one bit-array.
	 * @return all the changes
	 */
	public int getFlags(){
		return flags;
	}

	/**
	 * Tells whether the selection state of the {@link #getDockable() dockable} on its parent
	 * changed.
	 * @return whether the selection state changed
	 */
	public boolean didSelectionChange(){
		return (flags & FLAG_SELECTION) == FLAG_SELECTION;
	}
	
	/**
	 * Tells whether the selection state of a parent of the {@link #getDockable() dockable} changed.
	 * @return whether the selection state changed
	 */
	public boolean didParentSelectionChange(){
		return (flags & FLAG_PARENT_SELECTION) == FLAG_PARENT_SELECTION;
	}

	/**
	 * Tells whether the position of the {@link #getDockable() dockable} on its parent changed. This means
	 * that {@link DockStation#getDockableProperty(Dockable, Dockable)} would return another result
	 * than before.
	 * @return whether the position changed
	 */
	public boolean didLocationChange(){
		return (flags & FLAG_LOCATION_CHANGED) == FLAG_LOCATION_CHANGED;
	}

	/**
	 * Tells whether the position of one of the anchestors of {@link #getDockable() dockable} changed. This
	 * event is cased if an event with {@link #didLocationChange()} was found for one of the anchestors.
	 * @return whether an anchestor changed its position
	 */
	public boolean didParentLocationChange(){
		return (flags & FLAG_PARENT_LOCATION_CHANGED) == FLAG_PARENT_LOCATION_CHANGED;
	}

	/**
	 * Tells whether the visibility of the {@link #getDockable() dockable} changed.
	 * @return whether the visibility changed
	 * @see Dockable#isDockableVisible()
	 */
	public boolean didVisibilityChange(){
		return (flags & FLAG_VISIBILITY) == FLAG_VISIBILITY;
	}
	
	/**
	 * Tells that the parent of the {@link #getDockable() dockable} has changed.
	 * @return whether the hierarchy changed
	 */
	public boolean didHierarchyChange(){
		return (flags & FLAG_HIERARCHY) == FLAG_HIERARCHY;
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder( getClass().getSimpleName() );
		builder.append( "[" );
		boolean comma = false;
		if( didHierarchyChange() ){
			builder.append( "HIERARCHY" );
			comma = true;
		}
		if( didParentLocationChange() ){
			if( comma ){
				builder.append( ", " );
			}
			builder.append( "PARENT LOCATION" );
		}
		if( didLocationChange() ){
			if( comma ){
				builder.append( ", " );
			}
			builder.append( "LOCATION" );
		}
		if( didSelectionChange() ){
			if( comma ){
				builder.append( ", " );
			}
			builder.append( "SELECTION" );
		}
		if( didVisibilityChange() ){
			if( comma ){
				builder.append( ", " );
			}
			builder.append( "VISIBILITY" );
		}
		builder.append( "]" );
		return builder.toString();
	}
}
