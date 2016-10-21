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

import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;

/**
 * Abstract {@link Border} managing basic properties that are required by various
 * {@link Border}s related to the {@link EclipseTheme}.
 * @author Benjamin Sigg
 */
public abstract class AbstractEclipseBorder implements Border{
	/** constant indicating the top left edge has to be painted round */
	public static final int TOP_LEFT = 1;
	/** constant indicating the top right edge has to be painted round */
	public static final int TOP_RIGHT = 2;
	/** constant indicating the bottom left edge has to be painted round */
	public static final int BOTTOM_LEFT = 4;
	/** constant indicating the bottom right edge has to be painted round */
	public static final int BOTTOM_RIGHT = 8;
	
	/** which edges to paint round */
	private int roundEdges;
	
	private boolean fillEdges;
	private DockController controller;
	
	/**
	 * Creates a new border
	 * @param controller the owner of this border
	 * @param fillEdges whether to paint over the edges
	 */
	public AbstractEclipseBorder( DockController controller, boolean fillEdges ){
		this( controller, fillEdges, TOP_LEFT | TOP_RIGHT );
	}
	
	/**
	 * Creates a new border
	 * @param controller the owner of this border
	 * @param fillEdges whether to paint over the edges
	 * @param edges the edges that are painted round, or-ed from {@link #TOP_LEFT},
	 * {@link #TOP_RIGHT}, {@link #BOTTOM_LEFT} and {@link #BOTTOM_RIGHT}
	 */
	public AbstractEclipseBorder( DockController controller, boolean fillEdges, int edges ){
		this.fillEdges = fillEdges;
	    this.controller = controller;
	    roundEdges = edges;
	}
	
	/**
	 * Sets which edges are painted round.
	 * @param roundEdges the edges to paint round
	 */
	public void setRoundEdges( int roundEdges ){
		this.roundEdges = roundEdges;
	}
	
	/**
	 * Tells which edges are painted round.
	 * @return the round edges
	 */
	public int getRoundEdges(){
		return roundEdges;
	}
	
	/**
	 * Sets whether the edges should be filled
	 * @param fillEdges whether to paint the edges
	 */
	public void setFillEdges( boolean fillEdges ){
		this.fillEdges = fillEdges;
	}
	
	/**
	 * Tells whether the edges should be painted.
	 * @return <code>true</code> if the edges are to be filled
	 */
	public boolean isFillEdges(){
		return fillEdges;
	}
	
	/**
	 * Gets the controller in whose realm this border paints
	 * @return the controller
	 */
	public DockController getController(){
		return controller;
	}
	
	public boolean isBorderOpaque() {
		return false;
	}
}
