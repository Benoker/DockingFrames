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

package bibliothek.gui.dock.themes.basic;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.dockable.DefaultDockablePerspective;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.stack.StackDockPerspective;
import bibliothek.gui.dock.station.stack.StackDockProperty;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.station.support.Enforcement;
import bibliothek.gui.dock.station.support.PlaceholderList.Level;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;

/**
 * A simple implementation of {@link Combiner}, which merges two {@link Dockable Dockables}
 * by creating a {@link StackDockStation}, and putting the children onto this
 * station.
 * @author Benjamin Sigg
 */
public class BasicCombiner implements Combiner {
	public CombinerTarget prepare( final CombinerSource source, Enforcement force ){
		DockableDisplayer displayer = source.getOldDisplayer();
		if( displayer != null ){
			DisplayerCombinerTarget operation = displayer.prepareCombination( source, force );
			if( operation != null ){
				return new DisplayerTarget( operation );
			}
		}
		
		if( force.getForce() < 0.5f ){
			return null;
		}
		
		return new CombinerTarget(){
			public void paint( Graphics g, Component component, StationPaint paint, Rectangle stationBounds, Rectangle dockableBounds ){
				paint.drawInsertion( g, source.getParent(), stationBounds, dockableBounds );	
			}
			public DisplayerCombinerTarget getDisplayerCombination(){
				return null;
			}
		};
	}
	
	public Dockable combine( CombinerSource source, CombinerTarget target ){
		if( target instanceof DisplayerTarget ){
			return ((DisplayerTarget)target).execute( source );
		}
		else{
			DockStation parent = source.getParent();
			PlaceholderMap placeholders = source.getPlaceholders();
			
		    StackDockStation stack = new StackDockStation( parent.getTheme() );
	        stack.setController( parent.getController() );
	        if( placeholders != null ){
	        	stack.setPlaceholders( placeholders );
	        }
	        
	        stack.drop( source.getOld() );
	        stack.drop( source.getNew() );
	        
	        return stack;
		}
    }
	
	public void aside( final AsideRequest request ){
		PlaceholderMap placeholders = request.getLayout();
		StackDockPerspective stack = new StackDockPerspective();
		if( placeholders != null && stack.canRead( placeholders ) ){
			stack.setPlaceholders( placeholders );
		}
		if( stack.getItemCount() == 0 ){
			insert( stack, request.getLocation() );
		}
		
		int index = indexOf( stack, request.getLocation() );
		if( index == -1 ){
			index = stack.getDockableCount();
			if( index == 0 ){
				index = 1;
			}
		}
		else{
			index++;
		}
		index = Math.min( stack.getItemCount(), index );
		
		if( request.getPlaceholder() != null ){
			stack.insertPlaceholder( index, request.getPlaceholder(), Level.BASE );
		}
		
		request.answer( new StackDockProperty( index, request.getPlaceholder() ), stack.getPlaceholders() );
	}
	
	private void insert( StackDockPerspective stack, DockableProperty location ){
		final Path placeholder;
		
		if( location instanceof StackDockProperty ){
			placeholder = ((StackDockProperty)location).getPlaceholder();
		}
		else{
			placeholder = null;
		}
		
		if( placeholder == null ){
			stack.add( new DefaultDockablePerspective() );
		}
		else{
			stack.addPlaceholder( placeholder );
		}
	}
	
	private int indexOf( StackDockPerspective stack, DockableProperty location ){
		if( location instanceof StackDockProperty ){
			StackDockProperty property = (StackDockProperty)location;
			int index = stack.indexOf( property.getPlaceholder() );
			if( index != -1 ){
				return index;
			}
			return property.getIndex();
		}
		return -1;
	}
	
	private class DisplayerTarget implements CombinerTarget{
		private DisplayerCombinerTarget operation;
		
		/**
		 * Creates a new target
		 * @param operation the operation which is represented by this target
		 */
		public DisplayerTarget( DisplayerCombinerTarget operation ){
			this.operation = operation;
		}
		
		/**
		 * Executes and disposes the {@link #operation}.
		 * @param source the source to use for the execution
		 */
		public Dockable execute( CombinerSource source ){
			return operation.execute( source );
		}
		
		public void paint( Graphics g, Component component, StationPaint paint, Rectangle stationBounds, Rectangle dockableBounds ){
			operation.paint( g, component, paint, stationBounds, dockableBounds );
		}
		
		public DisplayerCombinerTarget getDisplayerCombination(){
			return operation;
		}
	}
}
