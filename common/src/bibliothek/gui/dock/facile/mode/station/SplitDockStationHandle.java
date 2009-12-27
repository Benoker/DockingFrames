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
package bibliothek.gui.dock.facile.mode.station;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.LocationModeEvent;
import bibliothek.gui.dock.facile.mode.LocationModeManager;
import bibliothek.gui.dock.facile.mode.MaximizedMode;
import bibliothek.gui.dock.facile.mode.MaximizedModeArea;
import bibliothek.gui.dock.facile.mode.NormalMode;
import bibliothek.gui.dock.facile.mode.NormalModeArea;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.split.SplitDockTree;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A link between {@link SplitDockStation}, {@link NormalModeArea} and
 * {@link MaximizedModeArea}.
 * @author Benjamin Sigg
 */
public class SplitDockStationHandle {
	/** unique id of this handle */
	private String id; 
	/** station managed by this handle */
	private SplitDockStation station;
	
	/** normal-mode */
	private Normal normal = new Normal();
	/** maximized-mode */
	private Maximal maximal = new Maximal();
	/** the mode which is accessing this handler */
	private MaximizedMode maximizedMode;
	
	/**
	 * Creates a new handle.
	 * @param id the unique id of this handle
	 * @param station the station to be managed
	 */
	public SplitDockStationHandle( String id, SplitDockStation station ){
		if( id == null )
			throw new IllegalArgumentException( "id must not be null" );
		if( station == null )
			throw new IllegalArgumentException( "station must not be null" );
		
		this.id = id;
		this.station = station;
	}
	
	/**
	 * Gets the station which is managed by this handle.
	 * @return the station
	 */
	public SplitDockStation getStation(){
		return station;
	}
	
	/**
	 * Returns this as {@link NormalModeArea}
	 * @return a representation of <code>this</code>
	 */
	public NormalModeArea asNormalModeArea(){
		return normal;
	}
	
	/**
	 * Returns this as {@link MaximizedModeArea}
	 * @return a representation of <code>this</code>
	 */
	public MaximizedModeArea asMaximziedModeArea(){
		return maximal;
	}
	
	/**
	 * Ensures that <code>dockable</code> is a child of this
	 * station.
	 * @param dockable
	 * @throws IllegalStateException if <code>dockable</code> already
	 * is a child of this station.
	 */
	public void dropAside( Dockable dockable ){
		if( dockable.getDockParent() == station )
			throw new IllegalStateException( "dockable already a child" );
		
		SplitDockTree tree = station.createTree();
		if( tree.getRoot() == null )
			tree.root( dockable );
		else{
			tree.root( tree.horizontal( tree.put( dockable ), tree.unroot() ) );
		}
		station.dropTree( tree, false );
	}
	
	private class Normal implements NormalModeArea{
		public boolean isNormalModeChild( Dockable dockable ){
			return isChild( dockable ) && station.getFullScreen() != dockable;
		}

		public DockableProperty getLocation( Dockable child ){
			return DockUtilities.getPropertyChain( station, child );
		}

		public String getUniqueId(){
			return id;
		}

		public boolean isChild( Dockable dockable ){
			return dockable.getDockParent() == station && !maximal.isChild( dockable );
		}

		public void setLocation( Dockable dockable, DockableProperty location, AffectedSet set ){
			if( maximizedMode != null ){
				maximizedMode.unmaximize( dockable, set );
			}
			
			maximal.setMaximized( null, set );
			set.add( dockable );
			
			if( dockable.getDockParent() == station ){
				if( location != null ){
					station.move( dockable, location );
				}
			}
			else{
				if( location != null ){
					if( !station.drop( dockable, location ))
						location = null;
				}
				if( location == null )
					station.drop( dockable );
			}
		}		
	}
	
	private class Maximal implements MaximizedModeArea{
		public void connect( MaximizedMode mode ){
			if( maximizedMode != null )
				throw new IllegalStateException( "handle already in use" );
			maximizedMode = mode;
		}
		
		public void prepareApply( Dockable dockable, AffectedSet affected ){
			LocationModeManager manager = maximizedMode.getManager();
			LocationMode normal = manager.getMode( NormalMode.IDENTIFIER );
			if( normal != null ){
				manager.alter( dockable, normal );
			}
		}
		
		public Runnable onApply( final LocationModeEvent event, final Dockable replacement ){
			if( event.getMode().getUniqueIdentifier().equals( NormalMode.IDENTIFIER )){
				Location location = event.getLocation();
				DockableProperty property = location == null ? null : location.getLocation();
				
				// try to set the mode prematurely
		        if( property != null ){
		        	Dockable dockable = event.getDockable();
		        	
		            if( property.getSuccessor() == null ){
		            	LocationModeManager manager = maximizedMode.getManager();
		            	
		            	LocationMode last = manager.getCurrentMode( dockable );
		            	LocationMode secondLast = manager.getPreviousMode( dockable );
		            	
		                if( last != null && secondLast != null ){
		                	if( NormalMode.IDENTIFIER.equals( secondLast.getUniqueIdentifier() ) &&
		                			MaximizedMode.IDENTIFIER.equals( last.getUniqueIdentifier() )){
		                    
		                		MaximizedModeArea area = maximizedMode.get( location.getRoot() );
		                		
		                        if( area == this ){
		                            area.setMaximized( null, event.getAffected() );
		                            event.done();
		                            return null;
		                        }
		                    }
		                }
		            }
		        }
		        
		        // unmaximize elements
		        // wherever it is, ensure that it does not leave a station in
		        // a dubious state
		        maximizedMode.unmaximize( event.getDockable(), event.getAffected() );

		        // ensure it does land on a parent without maximized children
		        if( location != null ){
		        	MaximizedModeArea area = maximizedMode.get( location.getRoot() );
		            if( area != null ){
		                maximizedMode.unmaximize( area, event.getAffected() );
		            }
		        }
		        
		        return null;
			}
			else{
				maximizedMode.unmaximize( getMaximized(), event.getAffected() );

		        return new Runnable() {
					public void run(){
						if( replacement != null && replacement.getDockParent() != null ){
							maximizedMode.maximize( Maximal.this, replacement, event.getAffected() );
						}		
					}
				};
			}
		}
		
		public String getUniqueId(){
			return id; 
		}
		
		public boolean isChild( Dockable dockable ){
			return getMaximized() == dockable;
		}
		
		public Dockable getMaximized(){
			return station.getFullScreen();
		}

		public void setMaximized( Dockable dockable, AffectedSet set ){
			if( dockable == null ){
				station.setFullScreen( null );
			}
			else if( dockable.getDockParent() == station ){
	            station.setFullScreen( dockable );
	        }
	        else{
	            if( dockable.getDockParent() != null )
	                dockable.getDockParent().drag( dockable );

	            dropAside( dockable );
	            station.setFullScreen( dockable );
	        }
			
			set.add( dockable );
		}
		
		public boolean isRepresenting( DockStation station ){
			return station == SplitDockStationHandle.this.station;
		}
	}
}
