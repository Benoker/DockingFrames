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
package bibliothek.gui.dock.themes.basic;

import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.span.SpanCallback;
import bibliothek.gui.dock.station.span.SpanFactory;
import bibliothek.gui.dock.station.span.SpanMode;
import bibliothek.gui.dock.station.span.SpanUsage;

/**
 * This factory creates {@link Span}s that always have a size of <code>0</code>.
 * @author Benjamin Sigg
 */
public class NoSpanFactory implements SpanFactory{
	public Span create( final SpanCallback callback ){
		return new Span(){
			private int size = 0;
			
			public void set( SpanMode mode ){
				// ignore	
			}
			
			public void mutate( SpanMode mode ){
				// ignore
			}
			
			public int getSize(){
				if( callback.getUsage() == SpanUsage.HIDING ){
					return 5;
				}
				return size;
			}
			
			public void configureSize( SpanMode mode, int size ){
				if( mode == SpanMode.OFF ){
					this.size = size;
					callback.resized();
				}
			}
		};
	}
}
