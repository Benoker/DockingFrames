/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.station.span;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * A {@link SpanMode} is a default configuration telling a {@link Span} how it is used. The
 * class offers enumeration like constants, and the default {@link DockStation}s will only
 * use these constants. Custom {@link DockStation}s however may define their own {@link SpanMode}s.
 * @author Benjamin Sigg
 */
public class SpanMode {
	/** Used to disabled a {@link Span}, the span has size <code>0</code> and thus is not visible */
	public static final SpanMode OFF = new SpanMode( "dock.off", 0 );
	
	/** Opens a {@link Span} just a little bit, enough for the user to see there is something happening */
	public static final SpanMode TEASING = new SpanMode( "dock.teasing", 10 );
	
	/** 
	 * Fully opens a {@link Span}, the user sees how the layout will change when he drops a {@link Dockable} 
	 * at its current position.
	 */
	public static final SpanMode OPEN = new SpanMode( "dock.open", 50 );
	
	/** a unique identifier for this mode */
	private String id;
	/** the default size of this mode */
	private int size;
	
	/**
	 * Creates a new mode
	 * @param id the unique identifier of this mode
	 * @param size the preferred default size of this mode
	 */
	public SpanMode( String id, int size ){
		if( id == null ){
			throw new IllegalArgumentException( "id must not be null" );
		}
		if( size < 0 ){
			throw new IllegalArgumentException( "size must be at least 0" );
		}
		
		this.id = id;
		this.size = size;
	}
	
	/**
	 * Gets the default size in pixels.
	 * @return the default size, at least 0
	 */
	public int getSize(){
		return size;
	}
	
	@Override
	public int hashCode(){
		return id.hashCode();
	}
	
	@Override
	public boolean equals( Object obj ){
		if( obj == null || obj.getClass() != getClass() ){
			return false;
		}
		if( obj == this ){
			return true;
		}
		return ((SpanMode)obj).id.equals( id );
	}
}
