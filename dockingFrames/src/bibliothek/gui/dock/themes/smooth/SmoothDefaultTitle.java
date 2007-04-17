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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import bibliothek.gui.Colors;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.title.DefaultDockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A title which changes its colors smoothly when selected or deselected.
 * @author Benjamin Sigg
 */
public class SmoothDefaultTitle extends DefaultDockTitle{
    /** The current state of the transition */
    private int current = 0;
    
    /** a trigger for the animation */
    private SmoothChanger changer = new SmoothChanger(){
        @Override
        protected boolean isActive() {
            return SmoothDefaultTitle.this.isActive();
        }
        
        @Override
        protected void repaint( int current ) {
            SmoothDefaultTitle.this.current = current;
            SmoothDefaultTitle.this.repaint();
        }
    };
    
    /**
     * Constructs a new title
     * @param dockable the owner of this title
     * @param origin the version which was used to create this title
     */
    public SmoothDefaultTitle( Dockable dockable, DockTitleVersion origin ) {
        super(dockable, origin);
    }
    
    /**
     * Gets the number of milliseconds needed for one transition from
     * active to passive.
     * @return the duration in milliseconds
     */
    public int getDuration() {
        return changer.getDuration();
    }
    
    /**
     * Sets the duration of the animation in milliseconds.
     * @param duration the duration
     */
    public void setDuration( int duration ) {
        changer.setDuration( duration );
    }
    
    @Override
    public void setActive( boolean active ) {
        super.setActive(active);
        
        if( changer != null )
            changer.trigger();
    }
    
    @Override
    protected void updateColors() {
    	super.updateColors();
    	
        if( changer != null ){
            int duration = getDuration();
            
            if( (isActive() && current != duration) || 
                (!isActive() && current != 0 )){
                
                double ratio = current / (double)duration;
                
                setForeground( Colors.between( getInactiveTextColor(), getActiveTextColor(), ratio ));
            }
        }
    }
    
    @Override
    protected void paintBackground( Graphics g, JComponent component ) {
        int duration = getDuration();
        
        if( (isActive() && current != duration) ||
            (!isActive() && current != 0 )){
            double ratio = current / (double)duration;
            
            Color left = Colors.between( getInactiveLeftColor(), getActiveLeftColor(), ratio );
            Color right = Colors.between( getInactiveRightColor(), getActiveRightColor(), ratio );
            
            GradientPaint gradient = getGradient( left, right, component );
            Graphics2D g2 = (Graphics2D)g;
            
            g2.setPaint( gradient );
            g2.fillRect( 0, 0, component.getWidth(), component.getHeight() );
        }
        else
            super.paintBackground( g, component );
    }
}
