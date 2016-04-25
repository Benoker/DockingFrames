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

package bibliothek.extension.gui.dock.theme.smooth;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * A class which counts milliseconds from 0 to {@link #setDuration(int) duration}
 * or in the other direction. This class contains an array of states: one state is
 * counted upwards (the {@link #destination()}), all the other states are counted
 * downwards. Clients can use this class to smoothly switch between different states.<br>
 * This class works on the EDT, no new threads are created.
 * @author Benjamin Sigg
 *
 */
public abstract class SmoothChanger implements ActionListener{
    /** the time at the last pulse */
    private long last;
    
    /** the current location for each state, something between 0 and {@link #duration} */
    private int[] current;
    
    /** the duration of the change */
    private int duration;
    
    /** a timer which pulses this changer */
    private Timer timer;
    
    /**
     * Constructor, sets {@link #setDuration(int) duration} to 250 milliseconds.
     * @param states the number of states this changer can have
     */
    public SmoothChanger( int states ){
    	this( 250, states );
    }
    
    /**
     * Constructs a new changer.
     * @param duration the duration of one transition, should not be less than 1
     * @param states the number of states this changer can have, should
     * at least be 2
     */
    public SmoothChanger( int duration, int states ){
        this.duration = duration;
        timer = new Timer( 15, this );
        current = new int[ states ];
    }
    
    /**
     * The direction of the change. The counter of for the state
     * <code>destination()</code> will always rise, while the other
     * counters decent.
     * @return the favored state
     */
    protected abstract int destination();
    
    /**
     * Triggered during a transition when the counter has been changed
     * @param current for each state a number between 0 and {@link #getDuration()},
     * the state with the highest number is the best selected state
     */
    protected abstract void repaint( int[] current );
    
    /**
     * Sets the duration of the transition.
     * @param duration the duration
     * @throws IllegalStateException if the duration is less than 1
     */
    public void setDuration( int duration ) {
        if( duration < 1 )
            throw new IllegalArgumentException( "duration must be >= 1" );
        
        this.duration = duration;
        
        for( int i = 0; i < current.length; i++ ){
        	current[i] = Math.min( current[i], duration );
        }
        
        repaint( current );
    }
    
    /**
     * Tells whether this changer is currently active or not.
     * @return <code>true</code> if active, <code>false</code> if not
     */
    public boolean isRunning(){
    	return timer.isRunning();
    }
    
    /**
     * Gets the duration of a transition
     * @return the duration
     * @see #setDuration(int)
     */
    public int getDuration() {
        return duration;
    }
    
    /**
     * Starts a transition
     */
    public void trigger(){
        timer.start();
        last = System.currentTimeMillis();
    }
    
    public void actionPerformed( ActionEvent e ){
        long time = System.currentTimeMillis();
        int delta = (int)( time - last );
        last = time;
        
        int destination = destination();
        boolean incomplete = false;
        
        for( int i = 0; i < current.length; i++ ){
        	if( i == destination ){
        		current[i] = Math.min( current[i] + delta, duration );
        		incomplete = incomplete || current[i] < duration;
        	}
        	else{
        		current[i] = Math.max( current[i] - delta, 0 );
        		incomplete = incomplete || current[i] > 0;
        	}
        }
        
        if( !incomplete ){
        	timer.stop();
        }
                
        repaint( current );
    }
}