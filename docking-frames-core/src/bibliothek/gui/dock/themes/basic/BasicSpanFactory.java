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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.Timer;

import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.span.SpanCallback;
import bibliothek.gui.dock.station.span.SpanFactory;
import bibliothek.gui.dock.station.span.SpanMode;

/**
 * The {@link BasicSpanFactory} uses a small animation to expand and to shrink its {@link Span}s.
 * @author Benjamin Sigg
 */
public class BasicSpanFactory implements SpanFactory{
	private int duration;
	private int minSpeed;
	private Timer timer;
	private Collection<BasicSpan> ticking = new HashSet<BasicSpan>(); 
	
	/**
	 * Creates a new factory
	 * @param duration how long the animation takes
	 * @param minSpeed the minimum speed, how many pixels must be shown/hidden on average within 1000 milliseconds.
	 */
	public BasicSpanFactory( int duration, int minSpeed ){
		setDuration( duration );
		setMinSpeed( minSpeed );
		timer = new Timer( 0, new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				tick();
			}
		} );
		timer.setRepeats( true );
		timer.setCoalesce( true );
		timer.setDelay( 20 );
		timer.setInitialDelay( 0 );
	}
	
	/**
	 * Sets how long an animation takes for changing the size of a {@link Span}. Changing this property
	 * affects all {@link Span}s that were ever created by this factory.
	 * @param duration how long the animation is in milliseconds, at least 0
	 */
	public void setDuration( int duration ){
		if( duration < 0 ){
			throw new IllegalArgumentException( "duration must be at least 0 milliseconds" );
		}
		this.duration = duration;
	}
	
	/**
	 * Sets the minimum speed to open/close spans. The minimum speed is the average amount of pixels
	 * in which the size has to change within 1000 milliseconds.
	 * @param minSpeed the minimum speed, can be 0
	 */
	public void setMinSpeed( int minSpeed ){
		this.minSpeed = minSpeed;
	}
	
	/**
	 * Gets the minimum speed of the animation.
	 * @return the minimum speed
	 * @see #setMinSpeed(int)
	 */
	public int getMinSpeed(){
		return minSpeed;
	}
	
	public Span create( SpanCallback callback ){
		return new BasicSpan( callback );
	}
	
	private synchronized void start( BasicSpan span ){
		if( ticking.isEmpty() ){
			ticking.add( span );
			timer.start();
		}
		else{
			ticking.add( span );
		}
	}
	
	private synchronized void stop( BasicSpan span ){
		ticking.remove( span );
		if( ticking.isEmpty() ){
			timer.stop();
		}
	}
	
	private synchronized void tick(){
		long now = System.nanoTime();
		for( BasicSpan span : ticking.toArray( new BasicSpan[ ticking.size() ] ) ){
			span.tick( now );
		}
	}
	
	private class BasicSpan implements Span {
		private SpanCallback callback;
		private Map<SpanMode, Integer> sizes = new HashMap<SpanMode, Integer>( 2 );
		private SpanMode currentMode;
		
		private int sizeStart;
		private int sizeTarget;
		private int animationDuration = -1;
		private long animationStart = -1;
		private int duration;
		
		public BasicSpan( SpanCallback callback ){
			this.callback = callback;
		}

		public void mutate( SpanMode mode ){
			sizeStart = getSize();
			sizeTarget = getSize( mode );
			
			this.duration = BasicSpanFactory.this.duration;
			
			if( sizeStart != sizeTarget ){
				if( minSpeed > 0 ){
					duration = Math.min( duration, 1000 * Math.abs( sizeStart - sizeTarget ) / minSpeed );
				}
				
				animationDuration = 0;
				animationStart = -1;
				start( this );
			}
		}

		public void set( SpanMode mode ){
			stop( this );
			animationDuration = -1;
			sizeTarget = getSize( mode );
			callback.resized();
		}

		private int getSize( SpanMode mode ){
			Integer size = sizes.get( mode );
			if( size == null ){
				return mode.getSize();
			}
			else{
				return size.intValue();
			}
		}
		
		public void configureSize( SpanMode mode, int size ){
			sizes.put( mode, size );
			if( mode == currentMode ){
				set( mode );
			}
		}
		
		public void tick( long now ){
			if( animationStart == -1 ){
				animationStart = now;
			}
			animationDuration = (int)((now - animationStart) / 1000000);
			if( animationDuration >= duration ){
				animationDuration = -1;
				stop( this );
			}
			callback.resized();
		}

		public int getSize(){
			if( animationDuration == -1 ){
				return sizeTarget;
			}
			double ratio = animationDuration / (double)duration;
			if( ratio > 1 ){
				ratio = 1;
			}
			
			ratio = 2*(1-ratio)*ratio + ratio*ratio;
			
			return (int)(sizeStart * (1-ratio) + sizeTarget * ratio);
		}
	}
}
