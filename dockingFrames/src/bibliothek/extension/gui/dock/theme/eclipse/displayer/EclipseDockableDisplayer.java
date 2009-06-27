/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse.displayer;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector.TitleBar;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTabPane;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.title.DockTitle;

/**
 * @author Janni Kovacs
 */
public class EclipseDockableDisplayer extends EclipseTabPane implements DockableDisplayer {
	private DockStation station;
	
	private Dockable dockable;
	private DockTitle title;
	private Location location;
	
	private List<DockableDisplayerListener> listeners = new ArrayList<DockableDisplayerListener>();
	private TitleBarObserver observer;
	
	public EclipseDockableDisplayer(EclipseTheme theme, DockStation station, Dockable dockable) {
		super(theme, station);
		
		observer = new TitleBarObserver( dockable, TitleBar.ECLIPSE ){
			@Override
			protected void invalidated(){
				for( DockableDisplayerListener listener : displayerListeners() ){
					listener.discard( EclipseDockableDisplayer.this );
				}
			}
		};
		
		this.station = station;
		setDockable(dockable);
		
		getComponent().setFocusCycleRoot( true );
	}

	@Override
	public Dimension getMinimumSize(){
		if( dockable == null )
			return new Dimension( 10, 10 );
		else
			return dockable.getComponent().getMinimumSize();
	}
	
	@Override
	public Dimension getPreferredSize(){
		if( dockable == null )
			return new Dimension( 10, 10 );
		else
			return dockable.getComponent().getMinimumSize();
	}
	
	public void addDockableDisplayerListener( DockableDisplayerListener listener ){
		listeners.add( listener );	
	}
	
	public void removeDockableDisplayerListener( DockableDisplayerListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Gets all {@link DockableDisplayerListener} known to this displayer.
	 * @return the list of listeners
	 */
	protected DockableDisplayerListener[] displayerListeners(){
		return listeners.toArray( new DockableDisplayerListener[ listeners.size() ] );
	}
	
	public void setDockable( Dockable dockable ){
		if (getDockable() != null) {
			removeAll();
		}
		this.dockable = dockable;
		if( dockable != null ){
			addTab( dockable.getTitleText(), dockable.getTitleIcon(), dockable.getComponent(), dockable );
		}
		if( observer != null ){
			observer.setDockable( dockable );
		}
		revalidate();
	}
	
	@Override
	public void setController( DockController controller ){
		super.setController( controller );
		if( observer != null ){
			observer.setController( controller );
		}
	}

	public boolean titleContains( int x, int y ){
		Point point = new Point( x, y );
		for( int i = 0, n = getTabCount(); i<n; i++ ){
			Rectangle bounds = getBoundsAt( i );
			if( bounds.contains( point ))
				return true;
		}
		return false;
	}
	
	public Insets getDockableInsets() {
	    return getContentInsets();
	}

	public Dockable getDockable(){
		return dockable;
	}

	public DockStation getStation(){
		return station;
	}

	public DockTitle getTitle(){
		return title;
	}

	public Location getTitleLocation(){
		return location;
	}

	public void setStation( DockStation station ){
		this.station = station;
	}

	public void setTitle( DockTitle title ){
		this.title = title;
	}

	public void setTitleLocation( Location location ){
		this.location = location;
	}
}
