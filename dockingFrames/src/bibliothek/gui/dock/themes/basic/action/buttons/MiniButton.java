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

package bibliothek.gui.dock.themes.basic.action.buttons;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModelAdapter;
import bibliothek.gui.dock.util.AbstractPaintableComponent;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A small {@link Component} used as view of a {@link BasicButtonModel}.
 * A MiniButton just changes its border when the states of its model changes.
 * @param <M> the type of model used to describe the state of this button
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
    
    /** a listener to {@link #model} */
    private BasicButtonModelAdapter listener = new BasicButtonModelAdapter(){
    	@Override
    	public void mouseInside( BasicButtonModel model, boolean mouseInside ){
    		updateBorder();
    	}
    	@Override
    	public void mousePressed( BasicButtonModel model, boolean mousePressed ){
    		if( mousePressed ){
    			requestFocusInWindow();
    		}
    		updateBorder();
    	}
    	@Override
    	public void enabledStateChanged( BasicButtonModel model, boolean enabled ){
    		updateBorder();
    	}
    	@Override
    	public void selectedStateChanged( BasicButtonModel model, boolean selected ){
    		updateBorder();
    	}
    };
    
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
        
        setFocusable( true );
        
        addFocusListener( new FocusAdapter(){
            @Override
            public void focusGained( FocusEvent e ) {
                repaint();
            }
            @Override
            public void focusLost( FocusEvent e ) {
                repaint();
            }
        });
    }
    
    /**
     * Sets the model of this button. The model contains all properties which
     * are necessary to paint this button.
     * @param model the model
     */
    protected void setModel( M model ) {
    	if( this.model != null )
    		this.model.removeListener( listener );
    	
        this.model = model;
        
        if( this.model != null )
        	this.model.addListener( listener );
        
        updateBorder();
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
        BackgroundPaint paint = model.getBackground();
        BackgroundComponent component = model.getBackgroundComponent();
        	
        AbstractPaintableComponent paintable = new AbstractPaintableComponent( component, this, paint ){
			protected void background( Graphics g ){
				// ignore
			}
			
			protected void foreground( Graphics g ){
				doPaintForeground( g );
			}
			
			@Override
			protected void border( Graphics g ){
				doPaintBorder( g );	
			}
			
			@Override
			protected void children( Graphics g ){
				// ignore	
			}
			
			@Override
			protected void overlay( Graphics g ){
				// ignore
			}
			
			public boolean isSolid(){
				return false;
			}
			
			public boolean isTransparent(){
				return false;
			}
		};
        paintable.paint( g );
    }
    
    private void doPaintForeground( Graphics g ){
    	// icon
        Icon icon = model.getPaintIcon();
        if( icon != null ){
            paintIcon( icon, g );
        }
        
        // focus
        if( isFocusOwner() && isFocusable() && isEnabled() ){
            paintFocus( g );
        }
    }
    
    private void doPaintBorder( Graphics g ){
    	// border
        Border border = getBorder();
        if( border != null )
            border.paintBorder( this, g, 0, 0, getWidth(), getHeight() );	
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
    
    /**
     * Paints markings on this button when this button is the focus owner.
     * @param g the graphics context
     */
    protected void paintFocus( Graphics g ){
        g.setColor( getForeground() );
        Insets insets = getMaxBorderInsets();
        
        int x = insets.left;
        int y = insets.right;
        int w = getWidth() - insets.left - insets.right;
        int h = getHeight() - insets.top - insets.bottom;
    
        h--;
        w--;
        
        g.drawLine( x, y,   x+2, y   );
        g.drawLine( x, y+1, x+1, y+1 );
        g.drawLine( x, y+2, x,   y+2 );
        
        g.drawLine( x+w, y,   x+w-2, y   );
        g.drawLine( x+w, y+1, x+w-1, y+1 );
        g.drawLine( x+w, y+2, x+w,   y+2 );
        
        g.drawLine( x+w, y+h,   x+w-2, y+h   );
        g.drawLine( x+w, y+h-1, x+w-1, y+h-1 );
        g.drawLine( x+w, y+h-2, x+w,   y+h-2 );
        
        g.drawLine( x, y+h,   x+2, y+h   );
        g.drawLine( x, y+h-1, x+1, y+h-1 );
        g.drawLine( x, y+h-2, x,   y+h-2 );
    }
    
    @Override
    @Todo(compatibility=Compatibility.BREAK_MINOR, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_0,
    		description="dont hardcode the minimum size")
    public Dimension getPreferredSize() {
    	if( isPreferredSizeSet() )
    		return super.getPreferredSize();
    	
    	Insets max = getMaxBorderInsets();
    	Dimension size = model.getMaxIconSize();
        
        size.width = Math.max( size.width, 16 );
        size.height = Math.max( size.height, 16 );
        
        size.width += max.left + max.right + 2;
        size.height += max.top + max.bottom + 2;
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
