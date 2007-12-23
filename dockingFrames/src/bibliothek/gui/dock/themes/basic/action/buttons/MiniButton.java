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

package bibliothek.gui.dock.themes.basic.action.buttons;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;

/**
 * A small {@link Component} used as view of a {@link BasicButtonModel}.
 * A MiniButton just changes its border when the states of its model changes.
 * @author Benjamin Sigg
 */
public class MiniButton<M extends BasicButtonModel> extends JComponent {
    /** the standard-border of this button */
    private Border normalBorder;
    /** the border if the mouse is over this button */
    private Border mouseOverBorder;
    /** the border if the mouse is pressed */
    private Border mousePressedBorder;
    
    /** border used when the model is in selected state */
    private Border normalSelectedBorder;
    /** border used when the mouse is over this button and the button is selected */
    private Border mouseOverSelectedBorder;
    /** border used when the mouse is pressed and the button is selected */
    private Border mousePressedSelectedBorder;
    
    /** the model storing the properties for this button */
    private M model;
    
    /**
     * Creates a new button
     * @param model the model for this button
     */
    public MiniButton( M model ){
        this.model = model;
        
        mousePressedBorder = BorderFactory.createBevelBorder( BevelBorder.LOWERED );
        mouseOverBorder = BorderFactory.createBevelBorder( BevelBorder.RAISED );
        
        normalSelectedBorder = mousePressedBorder;
        mouseOverSelectedBorder = mousePressedBorder;
        mousePressedSelectedBorder = mouseOverBorder;
    }

    /**
     * Sets the model of this button. The model contains all properties which
     * are necessary to show this button.
     * @param model the model
     */
    protected void setModel( M model ) {
        this.model = model;
    }
    
    /**
     * Gets the model which is used to store the properties of this button.
     * @return the model
     */
    public M getModel() {
        return model;
    }
    
    /**
     * Gets the border which is used when the mouse is over this button, but
     * not pressed.
     * @return the border, may be <code>null</code>
     */
    public Border getMouseOverBorder() {
        return mouseOverBorder;
    }

    /**
     * Sets the border which is shown when the mouse is over this button,
     * but not pressed.
     * @param mouseOverBorder the border, can be <code>null</code>
     */
    public void setMouseOverBorder( Border mouseOverBorder ) {
        this.mouseOverBorder = mouseOverBorder;
        updateBorder();
    }
    
    /**
     * Gets the border which is shown when the mouse is pressed and over
     * this button. 
     * @return the border, may be <code>null</code>
     * @see #setMousePressedBorder(Border)
     */
    public Border getMousePressedBorder() {
        return mousePressedBorder;
    }

    /**
     * Sets the border which is shown when the mouse is pressed and over
     * this button. The border is also shown if this button is selected.
     * @param mousePressedBorder the border, can be <code>null</code>
     */
    public void setMousePressedBorder( Border mousePressedBorder ) {
        this.mousePressedBorder = mousePressedBorder;
        updateBorder();
    }

    /**
     * Gets the default-border.
     * @return the border, may be <code>null</code>
     */
    public Border getNormalBorder() {
        return normalBorder;
    }

    /**
     * Sets the default-border. The border is always shown when nothing
     * special happens.
     * @param normalBorder the border, can be <code>null</code>
     */
    public void setNormalBorder( Border normalBorder ) {
        this.normalBorder = normalBorder;
        updateBorder();
    }
    
    /**
     * Gets the border which is used when this button is selected.
     * @return the selected-border
     */
    public Border getNormalSelectedBorder() {
        return normalSelectedBorder;
    }
    
    /**
     * Sets the border which is used when this button is selected.
     * @param normalSelectedBorder the selected border
     */
    public void setNormalSelectedBorder( Border normalSelectedBorder ) {
        this.normalSelectedBorder = normalSelectedBorder;
        updateBorder();
    }
    
    /**
     * Gets the border which is used when the mouse is over this button and
     * this button is selected.
     * @return the mouse-over-selected-border
     */
    public Border getMouseOverSelectedBorder() {
        return mouseOverSelectedBorder;
    }
    
    /**
     * Sets the border which is used when the mouse is over this button and
     * this button is selected.
     * @param mouseOverSelectedBorder the new border
     */
    public void setMouseOverSelectedBorder( Border mouseOverSelectedBorder ) {
        this.mouseOverSelectedBorder = mouseOverSelectedBorder;
        updateBorder();
    }
    
    /**
     * Gets the border which is used when the mouse is pressed and this button
     * is selected.
     * @return the border
     */
    public Border getMousePressedSelectedBorder() {
        return mousePressedSelectedBorder;
    }
    
    /**
     * Sets the border which is used when the mouse is pressed and this 
     * button is selected.
     * @param mousePressedSelectedBorder the new border
     */
    public void setMousePressedSelectedBorder( Border mousePressedSelectedBorder ) {
        this.mousePressedSelectedBorder = mousePressedSelectedBorder;
        updateBorder();
    }
    
    @Override
    public void paint( Graphics g ){
        // border
        Border border = getBorder();
        if( border != null )
            border.paintBorder( this, g, 0, 0, getWidth(), getHeight() );
        
        Icon icon = model.getPaintIcon();
        if( icon != null ){
        	paintIcon( icon, g );
        }
    }
    
    /**
     * Paints the <code>icon</code> in the center of this button.
     * @param icon the icon to paint
     * @param g the graphics context
     */
    protected void paintIcon( Icon icon, Graphics g ){
    	Insets max = getMaxBorderInsets();
    	
    	icon.paintIcon( this, g, 
                max.left + (getWidth()-max.left-max.right-icon.getIconWidth())/2, 
                max.top + (getHeight()-max.top-max.bottom-icon.getIconHeight())/2 );
    }
    
    @Override
    public Dimension getPreferredSize() {
    	if( isPreferredSizeSet() )
    		return super.getPreferredSize();
    	
    	Insets max = getMaxBorderInsets();
    	Dimension size = model.getMaxIconSize();
        
        size.width = Math.max( size.width, 16 );
        size.height = Math.max( size.height, 16 );
        
        size.width += max.left + max.right;
        size.height += max.top + max.bottom;
        return size;
    }
    
    /**
     * Gets the maximal insets of this button
     * @return the insets
     */
	protected Insets getMaxBorderInsets(){
		Insets max = new Insets( 0, 0, 0, 0 );
		for( int i = 0; i < 6; i++ ){
			Border border = null;
			switch( i ){
				case 0:
					border = getNormalBorder();
					break;
				case 1:
					border = getMouseOverBorder();
					break;
				case 2:
					border = getMousePressedBorder();
					break;
				case 3:
				    border = getNormalSelectedBorder();
				    break;
				case 4:
				    border = getMouseOverSelectedBorder();
				    break;
				case 5:
				    border = getMousePressedSelectedBorder();
				    break;
			}
			
			if( border != null ){
				Insets insets = border.getBorderInsets( this );
				max.left = Math.max( max.left, insets.left );
				max.right = Math.max( max.right, insets.right );
				max.top = Math.max( max.top, insets.top );
				max.bottom = Math.max( max.bottom, insets.bottom );
			}
		}
		return max;
	}
    
    /**
     * Changes the current border. Uses various states to determine the
     * correct border.
     */
    protected void updateBorder(){
        if( model.isEnabled() && model.isMousePressed() ){
            if( model.isSelected() )
                setBorder( getMousePressedSelectedBorder() );
            else
                setBorder( getMousePressedBorder() );
        }
        else if( model.isEnabled() && model.isMouseInside() ){
            if( model.isSelected() )
                setBorder( getMouseOverSelectedBorder() );
            else
                setBorder( getMouseOverBorder() );
        }
        else{
            if( model.isSelected() )
                setBorder( getNormalSelectedBorder() );
            else
                setBorder( getNormalBorder() );
        }
    }
}
