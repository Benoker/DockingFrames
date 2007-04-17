/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.themes.smooth;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * A class which counts milliseconds from 0 to {@link #setDuration(int) duration}
 * or in the other direction. There is no new thread, objects are inserted
 * in the EventDispatcher-Thread.
 * @author Benjamin Sigg
 *
 */
public abstract class SmoothChanger implements ActionListener{
    /** the time at the last pulse */
    private long last;
    
    /** the current location, something between 0 and {@link #duration} */
    private int current;
    
    /** the duration of the change */
    private int duration;
    
    /** a timer which pulses this changer */
    private Timer timer;
    
    /**
     * Constructor, sets {@link #setDuration(int) duration} to 250 milliseconds.
     */
    public SmoothChanger(){
    	this( 250 );
    }
    
    /**
     * Constructs a new changer.
     * @param duration the duration of one transition, should not be less than 1
     */
    public SmoothChanger( int duration ){
        this.duration = duration;
        timer = new Timer( 15, this );
    }
    
    /**
     * The direction of the change. If active, then the counter of this changer
     * is increasing. Otherwise, the value of this changer is decreasing.
     * @return whether to in- or to decrease the counter
     */
    protected abstract boolean isActive();
    
    /**
     * Triggered during a transition when the counter has been changed
     * @param current the counter
     */
    protected abstract void repaint( int current );
    
    /**
     * Sets the duration of the transition.
     * @param duration the duration
     * @throws IllegalStateException if the duration is less than 1
     */
    public void setDuration( int duration ) {
        if( duration < 1 )
            throw new IllegalArgumentException( "duration must be >= 1" );
        
        this.duration = duration;
        if( isActive() )
            current = Math.min( current, duration );
        else
            current = Math.max( current, 0 );
        
        repaint( current );
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
        
        if( isActive() ){
            current += delta;
            
            if( current >= duration ){
                current = duration;
                timer.stop();
            }
        }
        else{
            current -= delta;
            
            if( current <= 0 ){
                current = 0;
                timer.stop();
            }
        }
        
        repaint( current );
    }
}