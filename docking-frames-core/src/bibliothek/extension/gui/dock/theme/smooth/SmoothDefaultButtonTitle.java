/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
import java.awt.Container;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.themes.basic.BasicButtonDockTitle;
import bibliothek.gui.dock.title.DockTitleEvent;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A title intended for the {@link FlapDockStation}, this title changes its color
 * smoothly.
 * @author Benjamin Sigg
 */
public class SmoothDefaultButtonTitle extends BasicButtonDockTitle {
	private final int ACTIVE_STATE = 0;
	private final int SELECTED_STATE = 1;
	private final int INACTIVE_STATE = 2;
	
	/** the current time for each state */
	private int[] current;
	
    /** a trigger for the animation */
    private SmoothChanger changer = new SmoothChanger( 3 ){
    	@Override
    	protected int destination() {
    		if( isActive() )
    			return ACTIVE_STATE;
    		else if( isSelected() )
    			return SELECTED_STATE;
    		else
    			return INACTIVE_STATE;
    	}
    	
        @Override
        protected void repaint( int[] current ) {
            SmoothDefaultButtonTitle.this.current = current.clone();
            updateColors();
        }
    };
    
	/**
	 * Creates a new title.
	 * @param dockable the element for which the title is shown
	 * @param origin the origin of this title
	 */
	public SmoothDefaultButtonTitle( Dockable dockable, DockTitleVersion origin ){
		super( dockable, origin );
	}
	

    @Override
    public void setActive( boolean active ) {
        super.setActive(active);
        
        if( changer != null )
            changer.trigger();
    }
    
    @Override
    public void changed(DockTitleEvent event) {
    	super.changed(event);
    	
        if( changer != null )
            changer.trigger();
    }
	
    @Override
    protected void updateColors() {
    	updateForegroundColor();
    	updateBackgroundColor();
    }
    
	/**
	 * Updates the color used in the foreground
	 */
	protected void updateForegroundColor(){
		if( changer != null && changer.isRunning() && current != null ){
			setForeground( triColor( foreground( getActiveTextColor() ), foreground( getSelectedTextColor() ), foreground( getInactiveTextColor() )));
		}
		else{
			if( isActive() )
				setForeground( getActiveTextColor() );
			else if( isSelected() )
				setForeground( getSelectedTextColor() );
			else
				setForeground( getInactiveTextColor() );
		}
	}


	/**
	 * Updates the color used in the background
	 */
	protected void updateBackgroundColor(){
		if( changer != null && changer.isRunning() && current != null ){
			setBackground( triColor( background( getActiveColor() ), background( getSelectedColor() ), background( getInactiveColor() )));
		}
		else{
			if( isActive() )
				setBackground( getActiveColor() );
			else if( isSelected() )
				setBackground( getSelectedColor() );
			else
				setBackground( getInactiveColor() );
		}
	}
	
	
	/**
	 * Creates a combination of colors according to the {@link #current}
	 * states.
	 * @param a the color for state 0
	 * @param b the color for state 0
	 * @param c the color for state 0
	 * @return the combination of all these colors
	 */
	private Color triColor( Color a, Color b, Color c ){
		int sum = 0;
		for( int count : current )
			sum += count;
		
		if( sum == 0 )
			return Color.BLACK;
		
		double factorA = current[0] / (double)sum;
		double factorB = current[1] / (double)sum;
		double factorC = current[2] / (double)sum;
		
		double red = factorA * a.getRed() + factorB * b.getRed() + factorC * c.getRed();
		double green = factorA * a.getGreen() + factorB * b.getGreen() + factorC * c.getGreen();
		double blue = factorA * a.getBlue() + factorB * b.getBlue() + factorC * c.getBlue();
		
		return new Color( 
				Math.max( 0, Math.min( 255, (int)red )),
				Math.max( 0, Math.min( 255, (int)green )),
				Math.max( 0, Math.min( 255, (int)blue )));
	}
	
	/**
	 * Tries to find the foreground color used on this title assuming that
	 * <code>set</code> was given to {@link #setForeground(Color)}.
	 * @param set the expected color, can be <code>null</code>
	 * @return the foreground color
	 */
	private Color foreground( Color set ){
		if( set != null )
			return set;
		
		Container parent = getParent();
		if( parent == null )
			return Color.BLACK;
		
		set = parent.getForeground();
		if( set != null )
			return set;
		
		return Color.BLACK;
	}
	
	/**
	 * Tries to find the background color used on this title assuming that
	 * <code>set</code> was given to {@link #setBackground(Color)}.
	 * @param set the expected color, can be <code>null</code>
	 * @return the background color
	 */
	private Color background( Color set ){
		if( set != null )
			return set;
		
		Container parent = getParent();
		if( parent == null )
			return Color.WHITE;
		
		set = parent.getBackground();
		if( set != null )
			return set;
		
		return Color.WHITE;
	}
}
