/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.station.toolbar.group;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.toolbar.layout.ToolbarGridLayoutManager;

/**
 * This implementation of {@link ToolbarGroupDividerStrategy} paints a line between each
 * column and each line, and a line around at the outside of the {@link Dockable}s as well.
 * @author Benjamin Sigg
 */
public class DefaultToolbarGroupDividierStrategy implements ToolbarGroupDividerStrategy{
	/** a factory creating new {@link DefaultToolbarGroupDividierStrategy}s */
	public static final ToolbarGroupDividerStrategyFactory FACTORY = new ToolbarGroupDividerStrategyFactory(){
		@Override
		public ToolbarGroupDividerStrategy create( ToolbarGroupDockStation station ){
			return new DefaultToolbarGroupDividierStrategy( station );
		}
	};
	
	/** the station for which this strategy works */
	private ToolbarGroupDockStation station;
	
	/**
	 * Creates a new strategy for <code>station</code>.
	 * @param station the station which will use this strategy, not <code>null</code>
	 */
	public DefaultToolbarGroupDividierStrategy( ToolbarGroupDockStation station ){
		this.station = station;
	}
	
	@Override
	public int getColumn( int index ){
		return 1;
	}

	@Override
	public int getLine( int column, int index ){
		if( index == 0 ){
			return 0;
		}
		else{
			return 1;
		}
	}

	@Override
	public void paint( Component parent, Graphics g, ToolbarGridLayoutManager<StationChildHandle> layoutManager ){
		g.setColor( parent.getForeground() );
		
		Orientation orientation = station.getOrientation();
		ToolbarColumnModel<Dockable,?> model = station.getColumnModel();
		for( int i = 0, n = model.getColumnCount(); i<n; i++ ){
			ToolbarColumn<Dockable,?> column = model.getColumn( i );
			for( int j = 0, m = column.getDockableCount(); j<m; j++ ){
				Rectangle bounds = layoutManager.getBounds( i, j );
				if( j == 0 ){
					switch( orientation ){
						case HORIZONTAL:
							g.drawRect( bounds.x, bounds.y-1, bounds.width, bounds.height+1 );
							break;
							
						case VERTICAL:
							g.drawRect( bounds.x-1, bounds.y, bounds.width+1, bounds.height );
							break;
					}
				}
				else{
					g.drawRect( bounds.x-1, bounds.y-1, bounds.width+1, bounds.height+1 );
				}
			}
		}
	}
}
