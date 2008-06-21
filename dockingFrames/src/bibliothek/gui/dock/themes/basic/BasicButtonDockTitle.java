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

package bibliothek.gui.dock.themes.basic;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * This title changes its border whenever the active-state changes.
 * @author Benjamin Sigg
 */
public class BasicButtonDockTitle extends AbstractDockTitle {
	/** whether the mouse is currently pressed or not */
	private boolean mousePressed = false;
	
	/** the argument of the last call of {@link #changeBorder(boolean)} */
	private boolean selected = false;
	
	/** when and how to show icons and text */
	private FlapDockStation.ButtonContent behavior;
	
    /**
     * Constructs a new title
     * @param dockable the {@link Dockable} for which this title is created
     * @param origin the version which was used to create this title
     */
    public BasicButtonDockTitle( Dockable dockable, DockTitleVersion origin ) {
        super();
        
        behavior = FlapDockStation.ButtonContent.THEME_DEPENDENT;
        if( origin != null )
            behavior = origin.getController().getProperties().get( FlapDockStation.BUTTON_CONTENT );
        
        init( dockable, origin, behavior.showActions( true ) );
        setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ));
        
        addMouseInputListener( new MouseInputAdapter(){
        	@Override
        	public void mousePressed( MouseEvent e ){
        		mousePressed = (e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK ) != 0;
        		changeBorder( selected );
        	}
        	
        	@Override
        	public void mouseReleased( MouseEvent e ){
        		mousePressed = (e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK ) != 0;
        		changeBorder( selected );
        	}
        });
    }
    
    @Override
    protected void updateIcon() {
        String text = getDockable().getTitleText();
        if( behavior.showIcon( text != null && text.length() > 0, true ) )
            super.updateIcon();
        else
            setIcon( null );
    }
    
    @Override
    protected void updateText() {
        if( behavior.showText( getDockable().getTitleIcon() != null, true ) )
            super.updateText();
        else
            setText( "" );     
    }
    
    @Override
    public void setActive( boolean active ) {
        if( active != isActive() ){
            super.setActive(active);
            changeBorder( active );
        }
    }
    
    @Override
    public void changed( DockTitleEvent event ) {
        super.setActive( event.isActive() );
        changeBorder( event.isActive() || event.isPreferred() );
    }
    
    @Override
    public Point getPopupLocation( Point click, boolean popupTrigger ){
        if( popupTrigger )
            return click;
        
    	return null;
    }
    
    /**
     * Tells whether the mouse is currently pressed or not.
     * @return <code>true</code> if the mouse is pressed
     */
    protected boolean isMousePressed(){
		return mousePressed;
	}
    
    /**
     * Exchanges the current border.
     * @param selected whether the title is selected (active) or not
     */
    protected void changeBorder( boolean selected ){
    	this.selected = selected;
    	
        if( selected ^ mousePressed )
            setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ));
        else
            setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ));
    }
}
