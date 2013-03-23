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
package bibliothek.gui.dock.station.layer;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;

/**
 * A {@link LayerPriority} defines the basic ordering of {@link DockStationDropLayer}s. Each
 * {@link LayerPriority} has a custom {@link #getPriority() priority}, as higher this value is,
 * as higher the priority of the {@link DockStationDropLayer} is. The {@link #isReverse() reverse}
 * property tells whether the order should be reversed for {@link DockStationDropLayer}s with the
 * same {@link LayerPriority}.<br>
 * Several default {@link LayerPriority}s are defined, the framework itself uses only these
 * priorities. Clients may introduce additional priorities. The default priorities all lay within
 * a range of <code>0.1</code> to <code>0.9</code>. Clients introducing new layers can use code like
 * <code>new LayerPriority( LAYER.getPriority()+0.5f, false );</code> to place their new layers between existing
 * default layers. 
 * @author Benjamin Sigg
 */
public class LayerPriority implements Comparable<LayerPriority>{
	/**
	 * Describes the area where there are no stations, {@link Dockable}s dragged into this
	 * area normally start to float (i.e. become children of {@link ScreenDockStation}).
	 */
	public static final LayerPriority FALLBACK = new LayerPriority( 0.1f, false );
	
	/**
	 * Used for the area around a {@link DockStation}, the layer that is only accessed if
	 * all other layers are not used.
	 */
	public static final LayerPriority OUTSIDE_LOW = new LayerPriority( 0.2f, false );
	
	/**
	 * Used for {@link DockStation}s itself, this is the default priority for most layers.
	 */
	public static final LayerPriority BASE = new LayerPriority( 0.3f, false );
	
	/**
	 * Describes an area with special behavior more important than {@link #BASE}, but not an area
	 * where the user has a clear indicator of what will happen.This priority has the <code>reverse</code>
	 * flag set to <code>true</code>. 
	 */
	public static final LayerPriority OVERRIDE_GUESS = new LayerPriority( 0.4f, true );
	
	/**
	 * Describes an area with special behavior more important than {@link #BASE}, other than
	 * {@link #OVERRIDE_GUESS} this layer is very small but also has very clear indicators of
	 * what will happen.
	 */
	public static final LayerPriority OVERRIDE_PRECISE = new LayerPriority( 0.5f, false );

	/**
	 * Used for the area around a {@link DockStation}, the layer has a high priority and
	 * is accessed unless there are floating windows present.
	 */
	public static final LayerPriority OUTSIDE_HIGH = new LayerPriority( 0.6f, false );
	
	/**
	 * Describes a layer that is floating above a window, but still attached to that window. 
	 */
	public static final LayerPriority FLOAT_ANCHORED = new LayerPriority( 0.7f, false );
	
	/**
	 * Describes a layer that is floating freely above all other windows.
	 */
	public static final LayerPriority FLOAT_FREE = new LayerPriority( 0.8f, false );
	
	private LayerPriority sub;
	private boolean reverse;
	private float priority;
	
	/**
	 * Creates a new {@link LayerPriority}.
	 * @param priority the priority, a number at least 0, where a higher number means that
	 * the priority is more important.
	 * @param reverse whether the order of {@link DockStationDropLayer}s should be reversed
	 */
	public LayerPriority( float priority, boolean reverse ){
		this( priority, reverse, null );
	}
	
	/**
	 * Creates a new {@link LayerPriority}.
	 * @param priority the priority, a number at least 0, where a higher number means that
	 * the priority is more important.
	 * @param reverse whether the order of {@link DockStationDropLayer}s should be reversed
	 * @param sub further description of this priority
	 */
	public LayerPriority( float priority, boolean reverse, LayerPriority sub ){
		this.priority = priority;
		this.reverse = reverse;
		this.sub = sub;
	}
	
	/**
	 * Combines this {@link LayerPriority} with <code>sub</code>:
	 * <ul> 
	 * 	<li>The inner-layer priority is set to <code>sub</code> </li>
	 *  <li>The {@link #getPriority() priority} is to the priority of <code>this</code> </li> 
	 *  <li>The {@link #isReverse() reverse} property is set to the value of <code>this</code> </li> 
	 * </ul>
	 * @param sub the new sub layer
	 * @return the merged layer
	 */
	public LayerPriority merge( LayerPriority sub ){
		return new LayerPriority( getPriority(), isReverse(), sub );
	}
	
	/**
	 * Tells whether the order of {@link DockStationDropLayer}s should be reversed
	 * @return whether to reverse the order
	 */
	public boolean isReverse(){
		return reverse;
	}
	
	/**
	 * Gets the priority of this {@link LayerPriority}.
	 * @return the priority, a value at least 0, a higher number means that the priority
	 * is more important
	 */
	public float getPriority(){
		return priority;
	}
	
	public int compareTo( LayerPriority o ){
		if( o == this ){
			return 0;
		}
		
		int result = compareDirect( o );
		if( result != 0 ){
			return result;
		}
		
		if( sub == null && o.sub == null ){
			return 0;
		}
		if( sub == null && o.sub != null ){
			return 1;
		}
		if( sub != null && o.sub == null ){
			return -1;
		}
		
		return sub.compareTo( o.sub );
	}
	
	private int compareDirect( LayerPriority o ){
		if( getPriority() > o.getPriority() ){
			return -1;
		}
		if( getPriority() < o.getPriority() ){
			return 1;
		}
		if( !isReverse() && o.isReverse() ){
			return -1;
		}
		if( isReverse() && !o.isReverse() ){
			return 1;
		}
		return 0;		
	}
	
	@Override
	public String toString(){
		String base = "layer [priority=" + getPriority() + ", reverse=" + isReverse() + "]";
		if( sub != null ){
			base += " -> " + sub;
		}
		return base;
	}
}
