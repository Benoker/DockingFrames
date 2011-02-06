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
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.station.support.PlaceholderMap;

/**
 * A simple implementation of {@link Combiner}, which merges two {@link Dockable Dockables}
 * by creating a {@link StackDockStation}, and putting the children onto this
 * station.
 * @author Benjamin Sigg
 */
public class BasicCombiner implements Combiner {
	public CombinerTarget prepare( final CombinerSource source, boolean force ){
		if( !force ){
			return null;
		}
		
		return new CombinerTarget(){
			public void paint( Graphics g, Component component, StationPaint paint, Rectangle stationBounds, Rectangle dockableBounds ){
				paint.drawInsertion( g, source.getParent(), stationBounds, dockableBounds );	
			}
		};
	}
	
	public Dockable combine( CombinerSource source, CombinerTarget target ){
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
