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

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.mode.CLocationMode;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.perspective.CControlPerspective;
import bibliothek.gui.dock.common.perspective.CGridPerspective;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.PlaceholderMapping;
import bibliothek.gui.dock.station.stack.StackDockProperty;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.ClientOnly;
import bibliothek.util.Path;

/**
 * When a {@link Dockable} is about to be moved to a new position, then this {@link DockableGrouping} searches for 
 * a unique identifier, called a "placeholder", which indicates where the {@link Dockable} should be placed.<br>
 * The {@link #getPlaceholder() placeholder} is placed at the location of the {@link Dockable}, for which 
 * {@link #hierarchyChanged(Dockable)} was called last. Basically the placeholder always follows the last {@link Dockable}
 * that was moved around.<br>
 * This object tries to ensure that for each {@link ExtendedMode} there is no more than one placeholder set. Meaning
 * there is only one location marked for each mode.<br>
 * Clients may use the {@link CControlPerspective}, and methods like 
 * {@link CGridPerspective#gridPlaceholder(double, double, double, double, bibliothek.gui.dock.common.perspective.CDockablePerspective...) CGridPerspective.gridPlaceholder}, 
 * to set the initial location of the placeholder.
 * @author Benjamin Sigg
 */
@ClientOnly
// note: classname is written in the tutorial, if renaming this class, also update tutorial!
public class PlaceholderGrouping implements DockableGrouping{
	private CControl control;
	private Path placeholder;
	
	/**
	 * Creates a new grouping.
	 * @param control The realm in which this object acts, used to access things like the root {@link DockStation}s
	 * @param placeholder a unique identifier with which the location of a group of {@link Dockable}s is marked
	 */
	public PlaceholderGrouping( CControl control, Path placeholder ){
		if( control == null ){
			throw new IllegalArgumentException( "control must not be null" );
		}
		if( placeholder == null ){
			throw new IllegalArgumentException( "placeholder must not be null" );
		}
		
		this.control = control;
		this.placeholder = placeholder;
	}
	
	/**
	 * Gets the placeholder that is searched by this {@link PlaceholderGrouping}, and that is placed wherever 
	 * a {@link Dockable} is moved when {@link #hierarchyChanged(Dockable)} was called.
	 * @return the placeholder, not <code>null</code>
	 */
	public Path getPlaceholder() {
		return placeholder;
	}

	/**
	 * Placeholder that marks the last position where this {@link PlaceholderGrouping} did store something. The default implementation
	 * always returns {@link #getPlaceholder() placeholder + "last"}, but subclasses may change the behavior.
	 * @return the last location where something interesting happened
	 */
	protected Path getLastPlaceholder(){
		return placeholder.append( "last" );
	}
	
	public Location getStoredLocation( Dockable dockable, CLocationMode mode, Location history ) {
		return history;
	}

	public Location getValidatedLocation( Dockable dockable, CLocationMode mode, Location validatedHistory ) {
		if( validatedHistory != null ){
			Location result = findLocationFor( dockable, mode, validatedHistory );
			if( result != validatedHistory ){
				return result;
			}
		}
		Location result = findLocationFor( dockable, mode );
		if( result == null ){
			result = validatedHistory;
		}
		return result;
	}
	
	public ExtendedMode getInitialMode( Dockable dockable ) {
		ExtendedMode mode = getInitialMode( dockable, true );
		if( mode == null ){
			mode = getInitialMode( dockable, false );
		}
		return mode;
	}
	
	private ExtendedMode getInitialMode( Dockable dockable, boolean validateMode ){
		Path lastPlaceholder = getLastPlaceholder();
		
		for( CLocationMode mode : control.getLocationManager().modes() ){
			for( String id : mode.getRepresentationIds() ){
				DockStation station = mode.getRepresentation( id );
				if( station.getPlaceholderMapping().hasPlaceholder( lastPlaceholder )){
					boolean valid = true;
					
					if( validateMode ){
						Dockable child = station.getPlaceholderMapping().getDockableAt( lastPlaceholder );
						if( child != null ){
							valid = mode.isCurrentMode( child );
						}
					}
					
					if( valid ){
						return mode.getExtendedMode();
					}
				}
			}
		}
		
		return null;
	}

	/**
	 * Searches a location for <code>dockable</code>.
	 * @param dockable the element whose new location is searched
	 * @param mode the target mode of <code>dockable</code>
	 * @return the new location, or <code>null</code> if no location can be calculated
	 */
	protected Location findLocationFor( Dockable dockable, CLocationMode mode ) {
		for( String root : mode.getRepresentationIds() ){
			DockStation station = mode.getRepresentation( root );
			if( station.getPlaceholderMapping().hasPlaceholder( placeholder )){
				DockableProperty location = getLocation( station );
				if( location != null ){
					return new Location( mode.getUniqueIdentifier(), root, location, false );
				}
			}
		}
		return null;
	}
	
	/**
	 * Called by {@link #getValidatedLocation(Dockable, CLocationMode, Location)}, this method tries to find a location on the
	 * root station designated by <code>validatedHistory</code>.
	 * @param dockable the element whose new location is searched
	 * @param mode the target mode of <code>dockable</code>
	 * @param validatedHistory the location where the unmodified algorithm would place <code>dockable</code>
	 * @return either a replacement for <code>validatedHistory</code>, or <code>validatedHistory</code>. Should not return <code>null</code>.
	 */
	protected Location findLocationFor( Dockable dockable, CLocationMode mode, Location validatedHistory ) {
		DockStation root = mode.getRepresentation( validatedHistory.getRoot() );
		
		if( root != null && root.getPlaceholderMapping().hasPlaceholder( placeholder )){
			DockableProperty location = getLocation( root );
			if( location != null ){
				return new Location( mode.getUniqueIdentifier(), validatedHistory.getRoot(), location, validatedHistory.isApplicationDefined() );
			}
		}
		return validatedHistory;
	}

	/**
	 * Searches the ideal location on <code>root</code> that matches {@link #getPlaceholder() the placeholder}. 
	 * @param root the station on which to search the placeholder
	 * @return the location of {@link #getPlaceholder() the placeholder}, or <code>null</code> if the placeholder was not found
	 */
	protected DockableProperty getLocation( DockStation root ) {
		DockableProperty result = root.getPlaceholderMapping().getLocationAt( placeholder );
		
		DockableProperty last = result;
		DockableProperty previous = null;
		
		DockStation parent = root;
		while( last != previous ){
			previous = last;
			
			Dockable child = parent.getPlaceholderMapping().getDockableAt( placeholder );
			if( child != null ){
				parent = child.asDockStation();
			}
			else{
				parent = null;
			}
			
			if( parent != null ){
				PlaceholderMapping mapping = parent.getPlaceholderMapping();
				if( mapping.hasPlaceholder( placeholder )){
					DockableProperty next = mapping.getLocationAt( placeholder );
					if( next != null ){
						while( last.getSuccessor() != null ){
							last = last.getSuccessor();
						}
						last.setSuccessor( next );
						last = next;
					}
				}
			}
		}
		
		while( last != null ){
			DockableProperty successor = last.getSuccessor();
			if( successor == null ){
				last.setSuccessor( new StackDockProperty( Integer.MAX_VALUE, placeholder ) );
			}
			last = successor;
		}
		
		return result;
	}

	public void hierarchyChanged( Dockable dockable ) {
		markLocation( dockable );
	}
	
	public void focusGained( Dockable dockable ) {
		markLocation( dockable );	
	}
	
	/**
	 * Makes sure that the placeholder marks the current location of <code>dockable</code>.
	 * @param dockable defines the location to mark
	 */
	protected void markLocation( Dockable dockable ){
		removePlaceholderInMode( dockable );
		removePlaceholderEverywhere();
		
		Path lastPlaceholder = getLastPlaceholder();
		DockStation parent = dockable.getDockParent();
		while( parent != null && dockable != null ){
			PlaceholderMapping mapping = parent.getPlaceholderMapping();
			mapping.addPlaceholder( dockable, placeholder );
			mapping.addPlaceholder( dockable, lastPlaceholder );
			dockable = parent.asDockable();
			if( dockable != null ){
				parent = dockable.getDockParent();
			}
		}
	}
	
	/**
	 * Removes the {@link #placeholder} from any {@link DockStation} that is not an ancestor of <code>dockable</code>,
	 * but is associated with the current {@link ExtendedMode} of <code>dockable</code>.
	 * @param dockable defines the mode to check, and defines that its parent stations are not to be touched
	 */
	private void removePlaceholderInMode( Dockable dockable ){
		CLocationMode mode = control.getLocationManager().getCurrentMode( dockable );
		if( mode == null ){
			return;
		}
		for( String id : mode.getRepresentationIds() ){
			DockStation station = mode.getRepresentation( id );
			if( !DockUtilities.isAncestor( station, dockable )){
				station.getPlaceholderMapping().removePlaceholder( placeholder );
			}
		}
	}
	
	/**
	 * Removes the {@link #getLastPlaceholder() last placeholder} everywhere.
	 */
	public void removePlaceholderEverywhere(){
		Path lastPlaceholder = getLastPlaceholder();
		
		for( CLocationMode mode : control.getLocationManager().modes() ){
			for( String id : mode.getRepresentationIds() ){
				DockStation station = mode.getRepresentation( id );
				station.getPlaceholderMapping().removePlaceholder( lastPlaceholder );
			}
		}
	}
}
