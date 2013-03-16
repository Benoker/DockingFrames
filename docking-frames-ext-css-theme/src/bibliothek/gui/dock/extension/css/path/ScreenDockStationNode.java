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
package bibliothek.gui.dock.extension.css.path;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.extension.css.CssNode;
import bibliothek.gui.dock.extension.css.doc.CssDocKey;
import bibliothek.gui.dock.extension.css.doc.CssDocPathNode;
import bibliothek.gui.dock.extension.css.doc.CssDocText;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.station.screen.ScreenDockStationListener;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;

/**
 * This {@link CssNode} describes the child of a {@link ScreenDockStation}.
 * @author Benjamin Sigg
 */
@CssDocPathNode(
		name=@CssDocKey(key=ScreenDockStationNode.NAME),
		description=@CssDocText(text="Relation between a ScreenDockStation and its child(ren)"),
		properties={
			@CssDocKey(key="x", description=@CssDocText(text="x coordinate of the child (in pixel)")),
			@CssDocKey(key="y", description=@CssDocText(text="y coordinate of the child (in pixel)")),
			@CssDocKey(key="width", description=@CssDocText(text="width of the child (in pixel)")),
			@CssDocKey(key="height", description=@CssDocText(text="height of the child (in pixel)"))},
		pseudoClasses={
			@CssDocKey(key="fullscreen", description=@CssDocText(text="Applied if the child is in fullscreen mode")),
			@CssDocKey(key="selected", description=@CssDocText(text="Applied if the child is selected"))})
public class ScreenDockStationNode extends AbstractCssNode {
	/** The name of this node */
	public static final String NAME = "screen-child";
	
	/** the station to observe */
	private ScreenDockStation station;
	
	/** the dockable whose location has to be monitored */
	private Dockable dockable;
	
	/** the current location of {@link #dockable} */
	private ScreenDockProperty location;
	
	private ScreenDockStationListener fullscreenListener = new ScreenDockStationListener(){
		@Override
		public void windowRegistering( ScreenDockStation station, Dockable dockable, ScreenDockWindow window ){
			// ignore	
		}
		
		@Override
		public void windowDeregistering( ScreenDockStation station, Dockable dockable, ScreenDockWindow window ){
			// ignore
		}
		
		@Override
		public void fullscreenChanged( ScreenDockStation station, Dockable dockable ){
			if( ScreenDockStationNode.this.dockable == dockable ){
				location = null;
				fireNodeChanged();
			}
		}
	};
	
	private DockStationListener boundaryListener = new DockStationAdapter(){
		@Override
		public void dockablesRepositioned( DockStation station, Dockable[] dockables ){
			for( Dockable item : dockables ){
				if( dockable == item ){
					location = null;
					fireNodeChanged();
					break;
				}
			}
		}
		@Override
		public void dockableSelected( DockStation station, Dockable oldSelection, Dockable newSelection ){
			if( oldSelection == dockable || newSelection == dockable ){
				fireNodeChanged();
			}
		}
	};
	
	/**
	 * Creates a new path.
	 * @param station the station which should be observed
	 * @param dockable the element whose boundaries are monitored
	 */
	public ScreenDockStationNode( ScreenDockStation station, Dockable dockable ){
		if( station == null ){
			throw new IllegalArgumentException( "station must not be null" );
		}
		if( dockable != null ){
			throw new IllegalArgumentException( "dockable must not be null" );
		}
		this.station = station;
		this.dockable = dockable;
	}
	
	@Override
	public String getName(){
		return NAME;
	}
	
	@Override
	public String getProperty( String key ){
		ScreenDockProperty location = getLocation();
		if( location == null ){
			return null;
		}
		if( "x".equals( key )){
			return String.valueOf( location.getX() );
		}
		if( "y".equals( key )){
			return String.valueOf( location.getY() );
		}
		if( "width".equals( key )){
			return String.valueOf( location.getWidth() );
		}
		if( "height".equals( key )){
			return String.valueOf( location.getHeight() );
		}
		return null;
	}
	
	@Override
	public boolean hasPseudoClass( String className ){
		if( "fullscreen".equals( className )){
			ScreenDockProperty location = getLocation();
			if( location == null ){
				return false;
			}
			return location.isFullscreen();
		}
		if( "selected".equals( className )){
			return station.getFrontDockable() == dockable;
		}
		return false;
	}
	
	private ScreenDockProperty getLocation(){
		if( isBound() ){
			if( location == null ){
				location = station.getLocation( dockable, dockable );
			}
			return location;
		}
		else{
			return station.getLocation( dockable, dockable );
		}
	}
	
	@Override
	protected void bind(){
		station.addScreenDockStationListener( fullscreenListener );
		station.addDockStationListener( boundaryListener );
	}
	
	@Override
	protected void unbind(){
		station.removeScreenDockStationListener( fullscreenListener );
		station.removeDockStationListener( boundaryListener );
	}
}
