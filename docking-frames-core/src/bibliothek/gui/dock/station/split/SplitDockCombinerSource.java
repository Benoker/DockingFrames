/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.station.split;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.PlaceholderMap;

/**
 * A {@link CombinerSource} that is created by a {@link SplitDockStation}.
 * @author Benjamin Sigg
 */
public class SplitDockCombinerSource implements CombinerSource{
	/** information about the current position of the mouse and its effects */
	private PutInfo info;
	/** the existing child */
	private Dockable old;
	/** the creator of this source */
	private SplitDockStation station;
	/** position of the mouse in relation to {@link #station} */
	private Point mouse;
	
	/**
	 * Creates a new object
	 * @param info information about the current position of the mouse and of
	 * what is under the mouse
	 * @param station the creator of this source
	 * @param mouseOnStation position of the mouse in relation to <code>station</code>
	 * @throws IllegalArgumentException if the target not of <code>info</code> is not a {@link Leaf}
	 */
	public SplitDockCombinerSource( PutInfo info, SplitDockStation station, Point mouseOnStation ){
		if( !(info.getNode() instanceof Leaf) ){
			throw new IllegalArgumentException( "info is not available for a combination" );
		}
		
		this.info = info;
		this.station = station;
		this.mouse = mouseOnStation;
		old = getLeaf().getDockable();
		if( old == null ){
			throw new IllegalArgumentException( "old Dockable is null" );
		}
	}
	
	public Point getMousePosition(){
		if( this.mouse == null ){
			return null;
		}
		Point mouse = new Point( this.mouse );
		return SwingUtilities.convertPoint( station.getComponent(), mouse, getOld().getComponent() );
	}
	
	private Leaf getLeaf(){
		return (Leaf)info.getNode();
	}

	public Dockable getNew(){
		return info.getDockable();
	}

	public Dockable getOld(){
		return old;
	}
	
	public DockableDisplayer getOldDisplayer(){
		SplitNode node = info.getNode();
		if( node instanceof Leaf ){
			return ((Leaf)node).getDisplayer();
		}
		return null;
	}

	public DockStation getParent(){
		return station;
	}

	public PlaceholderMap getPlaceholders(){
		return getLeaf().getPlaceholderMap();
	}

	public Dimension getSize(){
		return getOld().getComponent().getSize();
	}

	public boolean isMouseOverTitle(){
		return info.getPut() == PutInfo.Put.TITLE;
	}
}
