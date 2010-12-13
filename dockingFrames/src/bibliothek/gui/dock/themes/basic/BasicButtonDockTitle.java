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

package bibliothek.gui.dock.themes.basic;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.themes.font.TitleFont;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.font.DockFont;
import bibliothek.util.Condition;

/**
 * This title changes its border whenever the active-state changes.
 * @author Benjamin Sigg
 */
@ColorCodes({ "title.flap.active", 
	"title.flap.active.text",
	"title.flap.inactive",
	"title.flap.inactive.text",
	"title.flap.selected",
	"title.flap.selected.text" })
public class BasicButtonDockTitle extends AbstractDockTitle {
	/** whether the mouse is currently pressed or not */
	private boolean mousePressed = false;
	
	/** whether this button is selected on its owner or not */
	private boolean selected = false;
	
	/** when and how to show icons and text */
	private FlapDockStation.ButtonContent behavior;
	
	/** the color used for the background when active */
	private TitleColor activeColor = new BasicTitleColor( "title.flap.active", null );
	/** the color used for the foreground when active */
	private TitleColor activeTextColor = new BasicTitleColor( "title.flap.active.text", null );
	/** the color used for background when inactive */
	private TitleColor inactiveColor = new BasicTitleColor( "title.flap.inactive", null );
	/** the color used for foreground when inactive */
	private TitleColor inactiveTextColor = new BasicTitleColor( "title.flap.inactive.text", null );
	/** the color used for background when selected */
	private TitleColor selectedColor = new BasicTitleColor( "title.flap.selected", null );
	/** the color used for foreground when selected */
	private TitleColor selectedTextColor = new BasicTitleColor( "title.flap.selected.text", null );
	
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
        changeBorder();
        
        addMouseInputListener( new MouseInputAdapter(){
        	@Override
        	public void mousePressed( MouseEvent e ){
        		mousePressed = (e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK ) != 0;
        		changeBorder();
        	}
        	
        	@Override
        	public void mouseReleased( MouseEvent e ){
        		mousePressed = (e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK ) != 0;
        		changeBorder();
        	}
        });
        
        addColor( activeColor );
        addColor( activeTextColor );
        addColor( inactiveColor );
        addColor( inactiveTextColor );
        addColor( selectedColor );
        addColor( selectedTextColor );
        
        addConditionalFont( DockFont.ID_FLAP_BUTTON_ACTIVE, TitleFont.KIND_FLAP_BUTTON_FONT, 
                new Condition(){
            public boolean getState() {
                return isActive();
            }
        }, null );

        addConditionalFont( DockFont.ID_FLAP_BUTTON_SELECTED, TitleFont.KIND_FLAP_BUTTON_FONT, 
                new Condition(){
            public boolean getState() {
                return isSelected();
            }
        }, null );

        addConditionalFont( DockFont.ID_FLAP_BUTTON_INACTIVE, TitleFont.KIND_FLAP_BUTTON_FONT, 
                new Condition(){
            public boolean getState() {
                return !isActive();
            }
        }, null );
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
            selected = active;
            updateLayout();
        }
    }
    
    @Override
    public void changed( DockTitleEvent event ) {
        super.setActive( event.isActive() );
        selected = event.isActive() || event.isPreferred();
        updateLayout();
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
     * Whether this button is selected or not.
     * @return <code>true</code> if selected, <code>false</code> otherwise
     */
    public boolean isSelected() {
		return selected;
	}
    
    /**
     * Updates various elements of this title such that the current state
     * is met.
     */
    protected void updateLayout(){
        changeBorder();
        updateColors();
        updateFonts();
    }
    
    /**
     * Exchanges the current border.
     */
    protected void changeBorder(){
    	if( selected && mousePressed ){
    		setBorder( ThemeManager.BORDER_MODIFIER + ".title.button.selected.pressed", BorderFactory.createBevelBorder( BevelBorder.RAISED ));
    	}
    	else if( selected ){
    		setBorder( ThemeManager.BORDER_MODIFIER + ".title.button.selected", BorderFactory.createBevelBorder( BevelBorder.LOWERED ));
    	}
    	else if( mousePressed ){
    		setBorder( ThemeManager.BORDER_MODIFIER + ".title.button.pressed", BorderFactory.createBevelBorder( BevelBorder.LOWERED ));
    	}
    	else{
    		setBorder( ThemeManager.BORDER_MODIFIER + ".title.button", BorderFactory.createBevelBorder( BevelBorder.RAISED ));
    	}
    }
    
    /**
     * Updates the colors of this title.
     */
    protected void updateColors(){
    	if( isActive() ){
    		setBackground( activeColor.color() );
    		setForeground( activeTextColor.color() );
    	}
    	else if( selected ){
    		setBackground( selectedColor.color() );
    		setForeground( selectedTextColor.color() );
    	}
    	else{
    		setBackground( inactiveColor.color() );
    		setForeground( inactiveTextColor.color() );
    	}
    }

    /**
     * Gets the color that is used as foreground if the title is focused.
     * @return the color, might be <code>null</code>
     */
    public Color getActiveTextColor(){
    	return activeTextColor.color();
    }
    
    /**
     * Sets the color that is used as foreground if the title is focused.
     * @param color the new color, <code>null</code> to reset the property
     */
    public void setActiveTextColor( Color color ){
    	activeTextColor.setValue( color );
    }
    

    /**
     * Gets the color that is used as background if the title is focused.
     * @return the color, might be <code>null</code>
     */
    public Color getActiveColor(){
    	return activeColor.color();
    }
    
    /**
     * Sets the color that is used as background if the title is focused.
     * @param color the new color, <code>null</code> to reset the property
     */
    public void setActiveColor( Color color ){
    	activeColor.setValue( color );
    }
    
    /**
     * Gets the color that is used as foreground if the title is selected.
     * @return the color, might be <code>null</code>
     */
    public Color getSelectedTextColor(){
    	return selectedTextColor.color();
    }
    
    /**
     * Sets the color that is used as foreground if the title is selected.
     * @param color the new color, <code>null</code> to reset the property
     */
    public void setSelectedTextColor( Color color ){
    	selectedTextColor.setValue( color );
    }
    

    /**
     * Gets the color that is used as background if the title is selected.
     * @return the color, might be <code>null</code>
     */
    public Color getSelectedColor(){
    	return selectedColor.color();
    }
    
    /**
     * Sets the color that is used as background if the title is selected.
     * @param color the new color, <code>null</code> to reset the property
     */
    public void setSelectedColor( Color color ){
    	selectedColor.setValue( color );
    }
    
    /**
     * Gets the color that is used as foreground
     * @return the color, might be <code>null</code>
     */
    public Color getInactiveTextColor(){
    	return inactiveTextColor.color();
    }
    
    /**
     * Sets the color that is used as foreground
     * @param color the new color, <code>null</code> to reset the property
     */
    public void setInactiveTextColor( Color color ){
    	inactiveTextColor.setValue( color );
    }
    

    /**
     * Gets the color that is used as background
     * @return the color, might be <code>null</code>
     */
    public Color getInactiveColor(){
    	return inactiveColor.color();
    }
    
    /**
     * Sets the color that is used as background
     * @param color the new color, <code>null</code> to reset the property
     */
    public void setInactiveColor( Color color ){
    	inactiveColor.setValue( color );
    }
    
    /**
     * A implementation of {@link TitleColor} that calls <code>repaint</code>
     * when the color changes.
     * @author Benjamin Sigg
     */
    private class BasicTitleColor extends TitleColor{
        /**
         * Creates a new color
         * @param id the id of the color
         * @param backup a backup color
         */
        public BasicTitleColor( String id, Color backup ){
            super( id, TitleColor.KIND_FLAP_BUTTON_COLOR, BasicButtonDockTitle.this, backup );
        }
        
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            updateColors();
        }
    }
}
