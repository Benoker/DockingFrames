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
package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JPanel;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

/**
 * @author Janni Kovacs
 */
public class NoTitleDisplayer extends JPanel implements DockableDisplayer {
	private Dockable dockable;
	private DockController controller;
	private DockStation station;
	private DockTitle title;
	private Location location;
	
	public NoTitleDisplayer( DockStation station, Dockable dockable ){
		setBorder( new EclipseBorder() );
		setLayout( new GridLayout( 1, 1, 0, 0 ) );
		setOpaque( false );
		
		setStation( station );
		setDockable( dockable );
	}

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintBorder(g);
    }
	
	public Component getComponent(){
		return this;
	}

	public DockController getController(){
		return controller;
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

	public void setController( DockController controller ){
		this.controller = controller;
	}

	public void setDockable( Dockable dockable ){
		this.dockable = dockable;
		removeAll();
		if( dockable != null )
			add( dockable.getComponent() );
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

	public boolean titleContains( int x, int y ){
		return false;
	}
}
