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

package bibliothek.extension.gui.dock.theme.bubble;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

/**
 * A <code>BubbleColorAnimation</code> has the ability to convert one or many color-pairs smoothly from source
 * to destination color. It basically is a map storing {@link String}-{@link Color} pairs. 
 * Clients have to call {@link #putColor(String, Color)} to start an animation. They
 * can call {@link #getColor(String)} any time to get the current intermediate color. Adding a {@link #addTask(Runnable) task}
 * will allow a client to be informed whenever the colors change.<br>
 * The animation itself takes {@link #setDuration(int) duration} milliseconds.  
 * @author Benjamin Sigg
 */
public class BubbleColorAnimation {
	/** How long a transformation takes */
    private int duration = 1000;
    
    /** The color pairs that can be animated */
    private Map<String, Entry> colors = new HashMap<String, Entry>();
    /** The timer that triggers steps of the animation */
    private Timer timer;
    /** The current time in milliseconds*/
    private long time = 0;
    
    /** The tasks that are executed at every step of the animation */
    private List<Runnable> tasks = new ArrayList<Runnable>();
    
    /**
     * Creates a new animation.
     */
    public BubbleColorAnimation(){
    	timer = new Timer( 25, new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                pulse();
            }
        });
    	timer.setCoalesce( true );
    }
    
    /**
     * Sets a color-pair. The color <code>destination</code> is shown
     * after maximal {@link #getDuration() duration} milliseconds. This method
     * does not start an animation, it just modifies an existing transition.
     * @param key the key of the pair
     * @param source where the animation starts
     * @param destination the destination of the animation
     */
    public void putColors( String key, Color source, Color destination ){
        Entry entry = colors.get( key );
        if( entry == null ){
            entry = new Entry();
            colors.put( key, entry );
        }
        entry.setColors( source, destination );
    }
    
    /**
     * If there is already a color stored under <code>key</code>, then a new animation
     * is started that smoothly changes the color <code>key</code> from its current value
     * to <code>color</code>. If no color is stored, then <code>color</code> is just set
     * without starting an animation.
     * @param key the key of the pair
     * @param color the destination of the animation, can be <code>null</code>
     */
    public void putColor( String key, Color color ){
        Entry entry = colors.get( key );
        if( entry == null ){
            entry = new Entry();
            entry.age = 0;
            entry.source = color;
            colors.put( key, entry );
        }
        else{
            entry.setDestination( color );
            start();
        }
    }
    
    /**
     * Gets the current color of the pair <code>key</code>.
     * @param key the key of the pair
     * @return the color of the pair or <code>null</code>
     */
    public Color getColor( String key ){
        Entry entry = colors.get( key );
        if( entry == null )
            return null;
        
        return entry.getColor();
    }
    
    /**
     * Adds a task to this animation. The task will be executed whenever the
     * animation makes a new step.
     * @param runnable the task to execute
     */
    public void addTask( Runnable runnable ){
        tasks.add( runnable );
    }
    
    /**
     * Removes a task which was earlier added to this animation.
     * @param runnable the task to remove
     */
    public void removeTask( Runnable runnable ){
    	tasks.remove( runnable );
    }
    
    /**
     * Sets the length of one transformation.
     * @param duration the duration in milliseconds
     */
    public void setDuration( int duration ){
		this.duration = duration;
	}
    
    /**
     * Gets the length of one transformation.
     * @return the duration in milliseconds
     */
    public int getDuration(){
		return duration;
	}
    
    /**
     * Stops the animation immediately, possibly leaving the animation
     * in an unfinished state.
     *
     */
    public void stop(){
        timer.stop();
    }
    
    /**
     * Immediately puts all colors to their final state and stops the animation.
     */
    public void kick(){
        if( timer.isRunning() ){
            stop();
            for( Entry entry : colors.values() )
                entry.kick();
            
            for( Runnable task : tasks )
                task.run();
        }
    }
    
    /**
     * Starts the animation if it is not yet running.
     */
    protected void start(){
        if( !timer.isRunning() ){
            time = System.currentTimeMillis();
            timer.start();
        }
    }
    
    /**
     * Called when the animation has to perform another step.
     */
    protected void pulse(){
        boolean run = false;
        long current = System.currentTimeMillis();
        int delta = (int)( current - time );
        time = current;
        
        for( Entry entry : colors.values() )
            run = entry.step( delta ) | run;
        
        if( !run )
            timer.stop();
        
        for( Runnable task : tasks )
            task.run();
    }
    
    /**
     * One pair of colors.
     * @author Benjamin Sigg
     */
    private class Entry{
    	/** The color which is abandoned by the animation */
        private Color source;
        /** The color to which the animation runs */
        private Color destination;
        /** Whether {@link #destination} was set */
        private boolean destinationSet = false;
        
        /** Replacement of {@link #source} for special circumstances */
        private Color intermediate;
        /** The age of the current transition from source to destination */
        private int age;
        
        /**
         * Gets the current color represented by this pair.
         * @return the color
         */
        public Color getColor(){
            if( age <= 0 )
                return source;
            
            if( age >= duration )
                return destination;
            
            Color source = intermediate == null ? this.source : intermediate;
            
            if( source == null ){
            	if( age >= duration/2 ){
            		return destination;
            	}
            	return null;
            }
            
            if( destination == null ){
            	if( age <= duration/2 ){
            		return source;
            	}
            	return null;
            }
            
            double s = (duration - age) / (double)duration;
            double d = age / (double)duration;
            return new Color(
                    Math.max( 0, Math.min( 255, (int)(s * source.getRed() + d * destination.getRed()))),
                    Math.max( 0, Math.min( 255, (int)(s * source.getGreen() + d * destination.getGreen()))),
                    Math.max( 0, Math.min( 255, (int)(s * source.getBlue() + d * destination.getBlue()))));
        }
        
        /**
         * Makes another step of the animation towards {@link #destination}.
         * @param delta the time passed since the last call in milliseconds
         * @return <code>true</code> if the animation is still running, <code>false</code>
         * if the animation is finished
         */
        public boolean step( int delta ){
            if( !destinationSet ){
            	return false;
            }
            
            age += delta;
            if( age >= duration ){
                age = 0;
                source = destination;
                destination = null;
                destinationSet = false;
                intermediate = null;
                return false;
            }
            
            return true;
        }
        
        /**
         * Immediately finishes this animation.
         */
        public void kick(){
            age = 0;
            if( destinationSet ){
                source = destination;
                destinationSet = false;
            }
            destination = null;
            intermediate = null;
        }
        
        /**
         * Replaces source and destination color immediately
         * @param source the new source
         * @param destination the new destination
         */
        public void setColors( Color source, Color destination ){
        	if( age == 0 ){
                this.source = destination;
            }
            else{
                this.source = source;
                this.destination = destination;
            }
        }
        
        /**
         * Sets all properties such that an animation from the current color
         * to <code>color</code> can happen.
         * @param color the new destination
         */
        public void setDestination( Color color ){
        	destinationSet = true;
        	if( destination == null || !destination.equals( color )){
        		if( age == 0 ){
	                destination = color;
	                intermediate = null;
	            }
	            else if( source != null && source.equals( color ) ){
	                intermediate = getColor();
	            	source = destination;
	                destination = color;
	                age = 1;
	            }
	            else{
	                intermediate = getColor();
	                source = null;
	                destination = color;
	                age = 1;
	            }
        	}
        }
    }
}
