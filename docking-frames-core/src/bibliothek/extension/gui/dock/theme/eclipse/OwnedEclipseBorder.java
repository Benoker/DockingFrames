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
package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Component;
import java.awt.Graphics;

import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.BorderedComponent;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;

/**
 * This border paints round edges at the edges which touch
 * the side of the tabs. It uses a {@link BorderedComponent} to
 * decide which side the tabs are at.
 * @author Benjamin Sigg
 */
public class OwnedEclipseBorder extends EclipseBorder{
	private BorderedComponent owner;
	
	/**
	 * Creates a new border.
	 * @param owner the component which paints this border
	 * @param controller to read properties about the environment
	 * @param fillEdges whether to fill the edges when painting
	 */
	public OwnedEclipseBorder( BorderedComponent owner, DockController controller, boolean fillEdges ){
		super( controller, fillEdges );
		if( owner == null )
			throw new IllegalArgumentException( "owner must not be null" );
		this.owner = owner;
	}
	
	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ){
		TabPlacement placement = owner.getDockTabPlacement();
		if( placement != null ){
			switch( placement ){
				case TOP_OF_DOCKABLE:
					setRoundEdges( TOP_LEFT | TOP_RIGHT );
					break;
				case BOTTOM_OF_DOCKABLE:
					setRoundEdges( BOTTOM_LEFT | BOTTOM_RIGHT );
					break;
				case LEFT_OF_DOCKABLE:
					setRoundEdges( BOTTOM_LEFT | TOP_LEFT );
					break;
				case RIGHT_OF_DOCKABLE:
					setRoundEdges( BOTTOM_RIGHT | TOP_RIGHT );
					break;
			}
		}
		
		super.paintBorder( c, g, x, y, width, height );
	}
}
