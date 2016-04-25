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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.basic.BasicDockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.util.Colors;

/**
 * A title which changes its colors smoothly when selected or deselected.
 * @author Benjamin Sigg
 */
public class SmoothDefaultTitle extends BasicDockTitle{
	private final int ACTIVE_STATE = 0;
	private final int INACTIVE_STATE = 1;
	private final int DISABLED_STATE = 2;
	
    /** The current state of the transition */
    private int[] current = null;
    
    /** a trigger for the animation */
    private SmoothChanger changer = new SmoothChanger( 3 ){
    	@Override
    	protected int destination() {
    		if( isDisabled() ){
    			return DISABLED_STATE;
    		}
    		else if( isActive() ){
    			return ACTIVE_STATE;
    		}
    		else{
    			return INACTIVE_STATE;
    		}
    	}
    	
        @Override
        protected void repaint( int[] current ) {
            SmoothDefaultTitle.this.current = current;
            updateForegroundColor();
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
    protected void setDisabled( boolean disabled ){
    	super.setDisabled( disabled );
    	
    	if( changer != null )
    		changer.trigger();
    }
    
    @Override
    protected void updateColors() {
    	super.updateColors();
    	updateForegroundColor();
    }
    
    /**
     * Updates the foreground color.
     */
    protected void updateForegroundColor(){
        boolean done = false;
        
        if( changer != null && current != null ){
            int duration = getDuration();
            
            if( (isActive() && current[ACTIVE_STATE] != duration) || (!isActive() && current[ACTIVE_STATE] != 0 )){
                double ratio = current[ACTIVE_STATE] / (double)duration;
                
                setForeground( Colors.between( getInactiveTextColor(), getActiveTextColor(), ratio ));
                done = true;
            }
            
            if( !done ){
                if( isActive() ){
                    setForeground( getActiveTextColor() );
                }
                else{
                    setForeground( getInactiveTextColor() );
                }
            }
        }
    }
    
    @Override
    protected void paintBackground( Graphics g, JComponent component ) {
        if( changer.isRunning() && current != null ){
            Color left = get( getActiveLeftColor(), getInactiveLeftColor(), getDisabledLeftColor() );
            Color right = get( getActiveRightColor(), getInactiveRightColor(), getDisabledRightColor() );
            
            GradientPaint gradient = getGradient( left, right, component );
            Graphics2D g2 = (Graphics2D)g;
            
            g2.setPaint( gradient );
            g2.fillRect( 0, 0, component.getWidth(), component.getHeight() );
        }
        else
            super.paintBackground( g, component );
    }
    
    private Color get( Color active, Color inactive, Color disabled ){
    	return Colors.between( active, current[ACTIVE_STATE], inactive, current[INACTIVE_STATE], disabled, current[DISABLED_STATE] );
    }
}
