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
package bibliothek.gui.dock.themes.basic;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.Enforcement;
import bibliothek.gui.dock.station.support.PlaceholderMap;

/**
 * This {@link DisplayerCombinerTarget} can be used by {@link DockableDisplayer}s that show a 
 * {@link StackDockComponent} to paint some tabs. This target will create a {@link StackDockStation}
 * if {@link #execute(CombinerSource)} is called.<br>
 * Clients should first create an instance of this target, then call {@link #isValid()} to check whether
 * the parameters were valid.  
 * @author Benjamin Sigg
 */
public class TabDisplayerCombinerTarget implements DisplayerCombinerTarget{
	/** the index where the new dockable would be inserted */
	private int index = -1;
	
	/** the item that created this target */
	private DockableDisplayer displayer;
	
	/** the stack component used to paint a single dockable */
	private StackDockComponent stack;
	
	/**
	 * Creates a new target, clients should call {@link #isValid()} to check whether the new target can
	 * be executed.
	 * @param displayer the owner of this target
	 * @param stack the stack that shows exactly one {@link Dockable}
	 * @param source information about the {@link Dockable} that is going to be dropped
	 * @param force whether this target should be forced to be valid in more cases
	 */
	public TabDisplayerCombinerTarget( DockableDisplayer displayer, StackDockComponent stack, CombinerSource source, Enforcement force ){
		this.displayer = displayer;
		this.stack = stack;
		Point mouse = source.getMousePosition();
		if( mouse != null && stack != null && stack.getTabCount() == 1 ){
			mouse = SwingUtilities.convertPoint( displayer.getDockable().getComponent(), mouse, stack.getComponent() );
			
			Rectangle bounds = stack.getBoundsAt( 0 );
			if( stack.getDockTabPlacement().isHorizontal() ){
				if( bounds.y <= mouse.y && bounds.y + bounds.height >= mouse.y ){
					if( bounds.x + bounds.width/2 < mouse.x ){
						index = 1;
					}
					else{
						index = 0;
					}
				}
				else if( force.getForce() > 0.9f ){
					index = 1;
				}
			}
			else{
				if( bounds.x <= mouse.x && bounds.x + bounds.width >= mouse.x ){
					if( bounds.y + bounds.height/2 < mouse.y ){
						index = 1;
					}
					else{
						index = 0;
					}
				}
				else if( force.getForce() > 0.9f ){
					index = 1;
				}
			}
		}
	}
	
	public boolean isValid(){
		return index >= 0;
	}
	
	/**
	 * Gets the location where the {@link Dockable} would be inserted
	 * @return -1 if this target is invalid, otherwise 0 or 1
	 */
	public int getIndex(){
		return index;
	}
	
	/**
	 * Gets the {@link Dockable} over which the new item would be dragged.
	 * @return the target {@link Dockable}, not <code>null</code>
	 */
	public Dockable getTarget(){
		return displayer.getDockable();
	}
	
	public Dockable execute( CombinerSource source ){
		DockStation parent = source.getParent();
		PlaceholderMap placeholders = source.getPlaceholders();
		
	    StackDockStation stack = new StackDockStation( parent.getTheme() );
        stack.setController( parent.getController() );
        if( placeholders != null ){
        	stack.setPlaceholders( placeholders );
        }
        
        if( index == 1 ){
	        stack.drop( source.getOld(), false );
	        stack.drop( source.getNew(), false );
        }
        else{
        	stack.drop( source.getNew(), false );
        	stack.drop( source.getOld(), false );
        }
        
        return stack;
	}

	public void paint( Graphics g, Component component, StationPaint paint, Rectangle stationBounds, Rectangle dockableBounds ){
		dockableBounds = new Rectangle( dockableBounds );
		
		Rectangle tab = stack.getBoundsAt( 0 );
		Point zero = new Point( 0, 0 );
		zero = SwingUtilities.convertPoint( stack.getComponent(), zero, component );
		tab.x += zero.x;
		tab.y += zero.y;
		
		int delta = 0;
		
		switch( stack.getDockTabPlacement() ){
			case TOP_OF_DOCKABLE:
				delta = tab.y + tab.height - dockableBounds.y;
				if( delta > 0 ){
					dockableBounds.height -= delta;
					dockableBounds.y += delta;
				}
				break;
			case BOTTOM_OF_DOCKABLE:
				delta = dockableBounds.y + dockableBounds.height - tab.y;
				if( delta > 0 ){
					dockableBounds.height -= delta;
				}
				break;
			case LEFT_OF_DOCKABLE:
				delta = tab.x + tab.width - dockableBounds.x;
				if( delta > 0 ){
					dockableBounds.width -= delta;
					dockableBounds.x += delta;
				}
				break;
			case RIGHT_OF_DOCKABLE:
				delta = dockableBounds.x + dockableBounds.width - tab.x;
				if( delta > 0 ){
					dockableBounds.width -= delta;
				}
				break;
		}
		
		paint.drawInsertion( g, displayer.getStation(), stationBounds, dockableBounds );
		
		
		if( stack.getDockTabPlacement().isHorizontal() ){
			if( index == 0 ){
				paint.drawInsertionLine( g, displayer.getStation(), tab.x, tab.y, tab.x, tab.y + tab.height );
			}
			else{
				paint.drawInsertionLine( g, displayer.getStation(), tab.x + tab.width, tab.y, tab.x + tab.width, tab.y + tab.height );	
			}
		}
		else{
			if( index == 0 ){
				paint.drawInsertionLine( g, displayer.getStation(), tab.x, tab.y, tab.x + tab.width, tab.y );
			}
			else{
				paint.drawInsertionLine( g, displayer.getStation(), tab.x, tab.y + tab.height, tab.x + tab.width, tab.y + tab.height );
			}
		}
	}
	
	public void paint( Graphics g, StationPaint paint ){
		Dockable dockable = displayer.getDockable();
		
		Point zeroDockable = new Point( 0, 0 );
		SwingUtilities.convertPoint( displayer.getComponent(), zeroDockable, dockable.getComponent() );
		
		paint.drawInsertion( g, displayer.getStation(), null, new Rectangle( zeroDockable, dockable.getComponent().getSize() ) );
	}
}
