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
package bibliothek.gui.dock.station.flap.layer;

import java.awt.Component;
import java.awt.Point;

import javax.swing.SwingUtilities;

import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.station.flap.FlapWindow;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Represents the "tabs" and the {@link DockTitle}s of a {@link FlapDockStation}.
 * @author Benjamin Sigg
 */
public class FlapOverrideDropLayer extends DefaultDropLayer{
	private FlapDockStation station;
	
	/**
	 * Creates a new layer
	 * @param station
	 */
	public FlapOverrideDropLayer( FlapDockStation station ){
		super( station );
		this.station = station;
		setPriority( LayerPriority.OVERRIDE_PRECISE );
	}
	
	@Override
	public boolean contains( int x, int y ){
		if( super.contains( x, y )){
			if( station.isOverButtons( x, y ) ){
				return true;
			}
			FlapWindow window = station.getFlapWindow();
			if( window != null && window.isWindowVisible() ){
				DockTitle title = window.getDockTitle();
				if( title != null ){
					Component titleComponent = title.getComponent();
					Point point = new Point( x, y );
	                SwingUtilities.convertPointFromScreen( point, titleComponent );
	                if( titleComponent.contains( point )){
	                	return true;
	                }
				}
			}
		}
		return false;
	}
}
