/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Herve Guillaume, Benjamin Sigg
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

package bibliothek.gui.dock.station.screen;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;

import javax.swing.SwingUtilities;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.screen.window.AbstractScreenWindowShape;
import bibliothek.gui.dock.station.screen.window.ScreenWindowShape;
import bibliothek.gui.dock.station.screen.window.ScreenWindowShapeCallback;
import bibliothek.gui.dock.station.toolbar.layout.ToolbarGridLayoutManagerListener;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A {@link ScreenWindowShape} that cuts out the empty spaces around a {@link ToolbarGroupDockStation}.
 * @author Benjamin Sigg
 */
public class ToolbarScreenWindowShape extends AbstractScreenWindowShape {
	private ToolbarGroupDockStation station;
	private boolean reshapePending = false;
	private ToolbarGridLayoutManagerListener listener = new ToolbarGridLayoutManagerListener(){
		@Override
		public void didLayout( Container container ){
			reshape();
		}
	};
	
	@Override
	public void setCallback( ScreenWindowShapeCallback callback ){
		uninstall();
		super.setCallback( callback );
	}
	
	private void uninstall(){
		if( station != null ){
			station.getLayoutManager().removeListener( listener );
			station = null;
		}
	}
	
	private void install( ToolbarGroupDockStation station ){
		this.station = station;
		station.getLayoutManager().addListener( listener );
	}
	
	@Override
	protected Shape getShape(){
		if( station == null ){
			Dockable dockable = getCallback().getWindow().getDockable();
			if( dockable instanceof ToolbarGroupDockStation ){
				install( (ToolbarGroupDockStation)dockable );
			}
		}
		if( station == null ){
			return null;
		}
		
		Area area = new Area();
		
		addBorder( area );
		addChildren( area );
		addExpanded( area );
		
		return area;
	}
	
	private void addBorder( Area area ){
		ScreenDockWindow window = getCallback().getWindow();
		
		Insets insets = window.getDockableInsets();
		
		Dimension size = window.getWindowBounds().getSize();
			
		if( insets.top > 0 ){
			area.add( new Area( new Rectangle( 0, 0, size.width, insets.top )));
		}
		if( insets.bottom > 0 ){
			area.add( new Area( new Rectangle( 0, size.height - insets.bottom - 1, size.width, insets.bottom )));
		}
		if( insets.left > 0 ){
			area.add( new Area( new Rectangle( 0, 0, insets.left, size.height )));
		}
		if( insets.right > 0 ){
			area.add( new Area( new Rectangle( size.width - insets.right - 1, 0, insets.right, size.height )));
		}
	}
	
	private void addChildren( Area area ){
		ScreenDockWindow window = getCallback().getWindow();
		Insets insets = window.getDockableInsets();
		
		for( int i = 0, n = station.getDockableCount(); i < n; i++ ){
			Dockable dockable = station.getDockable( i );
			Point zero = new Point( 0, 0 );
			zero = SwingUtilities.convertPoint( dockable.getComponent(), zero, station.getComponent() );
			
			zero.x += insets.left;
			zero.y += insets.top;
			
			Dimension size = dockable.getComponent().getSize();
			
			area.add( new Area( new Rectangle( zero.x-1, zero.y-1, size.width+2, size.height+2 ) ));
			
			
			for( DockTitle title : dockable.listBoundTitles() ){
				if( SwingUtilities.isDescendingFrom( title.getComponent(), station.getComponent() )){
					Point titleZero = new Point( 0, 0 );
					titleZero = SwingUtilities.convertPoint( title.getComponent(), titleZero, dockable.getComponent() );
					
					titleZero.x += zero.x;
					titleZero.y += zero.y;
					
					size = title.getComponent().getSize();
					
					area.add( new Area( new Rectangle( titleZero.x-1, titleZero.y-1, size.width+2, size.height+2 )));
				}
			}
		}
	}
	
	private void addExpanded( Area area ){
		Rectangle dropGap = station.getDropGapBoundaries();
		if( dropGap != null ){
			ScreenDockWindow window = getCallback().getWindow();
			Insets insets = window.getDockableInsets();
			dropGap.x += insets.left;
			dropGap.y += insets.top;
			area.add( new Area( dropGap ) );
		}
	}
	
	@Override
	public void reshape(){
		reshapePending = true;
		EventQueue.invokeLater( new Runnable(){
			@Override
			public void run(){
				if( reshapePending ){
					reshapePending = false;
					ToolbarScreenWindowShape.super.reshape();
				}
			}
		});
	}
}
