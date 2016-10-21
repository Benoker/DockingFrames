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

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link DefaultSpanFactory} creates a most simple {@link Span} which does not
 * use any kind of animation.
 * @author Benjamin Sigg
 */
public class DefaultSpanFactory implements SpanFactory {
	public Span create( SpanCallback callback ){
		return new DefaultSpan( callback );
	}

	/**
	 * This most simple {@link Span} does not use any kind of animation
	 * @author Benjamin Sigg
	 */
	private static class DefaultSpan implements Span{
		private SpanCallback callback;
		private Map<SpanMode, Integer> sizes = new HashMap<SpanMode, Integer>( 2 );
		private int size = 0;
		
		public DefaultSpan( SpanCallback callback ){
			this.callback = callback;
		}
		
		public void mutate( SpanMode mode ){
			set( mode );
		}

		public void set( SpanMode mode ){
			Integer next = sizes.get( mode );
			if( next == null ){
				this.size = mode.getSize();
			}
			else{
				this.size = next.intValue();
			}
			callback.resized();
		}

		public void configureSize( SpanMode mode, int size ){
			sizes.put( mode, size );
		}

		public int getSize(){
			return size;
		}
	}
}
