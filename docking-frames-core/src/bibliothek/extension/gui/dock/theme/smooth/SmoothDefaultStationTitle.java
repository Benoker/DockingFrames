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
    /** the counter, tells where transition between active and passive stands. */
    private int current = 0;
    
    /**
     * Source for pulses for this title.
     */
    private SmoothChanger changer = new SmoothChanger( 2 ){
    	@Override
    	protected int destination() {
    		if( isActive() )
    			return 0;
    		else
    			return 1;
    	}
    	
        @Override
        protected void repaint( int[] current ) {
            SmoothDefaultStationTitle.this.current = current[0];
            SmoothDefaultStationTitle.this.updateColors();
        }
    };
    
    @Override
    public void setActive( boolean active ) {
        super.setActive(active);
        
        if( changer != null )
            changer.trigger();
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
        super.updateColors();
        
        if( changer != null ){
            int duration = getDuration();
            
            if( (isActive() && current != duration) || 
                (!isActive() && current != 0 )){
                
                double ratio = current / (double)duration;
                
                setForeground( Colors.between( getInactiveTextColor(), getActiveTextColor(), ratio ));
                setBackground( Colors.between( getInactiveColor(), getActiveColor(), ratio ) );
            }
        }
    }
}
