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
package bibliothek.extension.gui.dock.station;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.split.SplitDockPathProperty;
import bibliothek.gui.dock.station.split.SplitDockPathProperty.Location;
import bibliothek.gui.dock.station.split.SplitNode;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.station.support.Enforcement;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.themes.basic.BasicCombiner;
import bibliothek.util.ClientOnly;

/**
 * This is an alternative implementation of a {@link Combiner}, normally not used by this framework. This
 * {@link Combiner} creates a {@link SplitDockStation} if the parent station is a {@link ScreenDockStation}.
 * @author Benjamin Sigg
 */
@ClientOnly
public class SplitCombiner extends BasicCombiner{
	public CombinerTarget prepare( CombinerSource source, Enforcement force ){
		if( source.isMouseOverTitle() || !(source.getParent() instanceof ScreenDockStation) ){
			return super.prepare( source, force );
		}
		
		Dimension size = source.getSize();
		Point position = source.getMousePosition();
		if( size == null || position == null ){
			return super.prepare( source, force );
		}
		if( isCentered(  size.width, size.height, position.x, position.y )){
			return super.prepare( source, force );
		}
		
		boolean topLeft = SplitNode.above( 0, size.height, size.width, 0, position.x, position.y );
		boolean topRight = SplitNode.above( 0, 0, size.width, size.height, position.x, position.y );
		
		Location put;
		if( topLeft && topRight ){
			put = Location.TOP;
		}
		else if( topLeft && !topRight ){
			put = Location.LEFT;
		}
		else if( !topLeft && topRight ){
			put = Location.RIGHT;
		}
		else{
			put = Location.BOTTOM;
		}
		
		Dimension newSize = source.getNew().getComponent().getSize();
		
		double oldSpace;
		double newSpace;
		double space;
		
		if( put == Location.LEFT || put == Location.RIGHT ){
			oldSpace = size.width;
			newSpace = newSize.width;
		}
		else{
			oldSpace = size.height;
			newSpace = newSize.height;
		}
		
		if( newSpace < 10 ){
			newSpace = 10;
		}
		if( newSpace + 10 > oldSpace ){
			newSpace = oldSpace - 10;
		}
		
		if( newSpace < 10 ){
			space = 0.5;
		}
		else{
			space = newSpace / oldSpace;
		}
		
		return new Target( source.getParent(), put, space );
	}
	
	/**
	 * Tells whether the position <code>x/y</code> is centered in <code>width/height</code>.
	 * @param width the width of some area
	 * @param height the height of some area
	 * @param x x-coordinate of a point 
	 * @param y y-coordinate of a point
	 * @return <code>true</code> if <code>x/y</code> is centered
	 */
	protected boolean isCentered( int width, int height, int x, int y ){
		if( x < width / 3 || x > width * 2 / 3 )
			return false;
		
		if( y < height / 3 || y > height * 2 / 3 )
			return false;
		
		return true;
	}

	public Dockable combine( CombinerSource source, CombinerTarget target ){
		if( target instanceof Target ){
			DockStation parent = source.getParent();
			PlaceholderMap placeholders = source.getPlaceholders();
			
			SplitDockStation split = new SplitDockStation(){
				@Override
				protected ListeningDockAction createFullScreenAction(){
					return null;
				}
			};
			split.setController( parent.getController() );
			split.updateTheme();
			
	        if( placeholders != null ){
	        	split.setPlaceholders( placeholders );
	        }
	        
	        split.drop( source.getOld() );
	        
	        Target splitTarget = (Target)target;
	        
	        SplitDockPathProperty location = new SplitDockPathProperty();
	        location.add( splitTarget.side, splitTarget.space, -1 );
	        
	        split.drop( source.getNew(), location );
	        return split;
		}
		else{
			return super.combine( source, target );
		}
	}

	/**
	 * Internal {@link CombinerTarget} of {@link SplitCombiner}, draws markings
	 * at one side of a {@link Dockable}.
	 * @author Benjamin Sigg
	 */
	private static class Target implements CombinerTarget{
		private DockStation station;
		private SplitDockPathProperty.Location side;
		private double space;
		
		/**
		 * Creates a new target.
		 * @param station the station associated with this target
		 * @param side the side at which the element will be added
		 * @param space the size of the new element
		 */
		public Target( DockStation station, SplitDockPathProperty.Location side, double space ){
			this.station = station;
			this.side = side;
			this.space = space;
		}
		
		public void paint( Graphics g, Component component, StationPaint paint, Rectangle stationBounds, Rectangle dockableBounds ){
			Rectangle destination = new Rectangle( dockableBounds );
			switch( side ){
				case TOP:
					destination.height = (int)(destination.height * space);
					break;
				case BOTTOM:
					destination.height = (int)(destination.height * space);
					destination.y += dockableBounds.height - destination.height;
					break;
				case LEFT:
					destination.width = (int)(destination.width * space);
					break;
				case RIGHT:
					destination.width = (int)(destination.width * space);
					destination.x += dockableBounds.width - destination.width;
			}
			
			paint.drawInsertion( g, station, dockableBounds, destination );
		}
		
		public DisplayerCombinerTarget getDisplayerCombination(){
			return null;
		}
	}
}
