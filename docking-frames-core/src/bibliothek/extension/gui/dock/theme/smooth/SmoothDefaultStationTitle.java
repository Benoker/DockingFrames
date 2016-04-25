/**
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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.basic.BasicStationTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.util.Colors;

/**
 * A station-title which smoothly changes its color from active to passive.
 * @author Benjamin Sigg
 *
 */
public class SmoothDefaultStationTitle extends BasicStationTitle{
	private final int ACTIVE_STATE = 0;
	private final int INACTIVE_STATE = 1;
	private final int DISABLED_STATE = 2;
	
    /** the counter, tells where transition between active and passive stands. */
    private int[] current = null;
    
    /**
     * Source for pulses for this title.
     */
    private SmoothChanger changer = new SmoothChanger( 3 ){
    	@Override
    	protected int destination() {
    		if( isDisabled() ){
    			return DISABLED_STATE;
    		}
    		else if( isActive() )
    			return ACTIVE_STATE;
    		else
    			return INACTIVE_STATE;
    	}
    	
        @Override
        protected void repaint( int[] current ) {
            SmoothDefaultStationTitle.this.current = current;
            SmoothDefaultStationTitle.this.updateColors();
        }
    };
    
    @Override
    public void setActive( boolean active ) {
        super.setActive(active);
        
        if( changer != null )
            changer.trigger();
    }
    
    @Override
    protected void setDisabled( boolean disabled ){
    	super.setDisabled( disabled );
    	
    	if( changer != null ){
    		changer.trigger();
    	}
    }
    
    /**
     * Constructs a new station title
     * @param dockable the owner of this title
     * @param origin the version which was used to create this title
     */
    public SmoothDefaultStationTitle( Dockable dockable, DockTitleVersion origin ) {
        super(dockable, origin);
    }
    
    /**
     * Gets the duration of one transition from active to passive
     * @return the duration
     */
    public int getDuration(){
        return changer.getDuration();
    }
    
    /**
     * Sets the duration of one transition from active to passive, or
     * in the other direction.
     * @param duration the duration
     */
    public void setDuration( int duration ){
        changer.setDuration( duration );
    }
        
    @Override
    protected void updateColors() {
        if( changer != null && changer.isRunning() && current != null ){
        	setForeground( get( getActiveTextColor(), getInactiveTextColor(), getInactiveTextColor() ));
            setBackground( get( getActiveColor(), getInactiveColor(), getDisabledColor() ));
            
            repaint();
        }
        else{
        	super.updateColors();
        }
    }
    
    private Color get( Color active, Color inactive, Color disabled ){
    	return Colors.between( active, current[ACTIVE_STATE], inactive, current[INACTIVE_STATE], disabled, current[DISABLED_STATE] );
    }
}
