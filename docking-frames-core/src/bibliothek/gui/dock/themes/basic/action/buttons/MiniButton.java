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
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModelAdapter;
import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.util.AbstractPaintableComponent;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * A small {@link Component} used as view of a {@link BasicButtonModel}.
 * A MiniButton just changes its border when the states of its model changes.
 * @param <M> the type of model used to describe the state of this button
 * @author Benjamin Sigg
 */
@ColorCodes({
	"action.button.text",
	"action.button.text.disabled"
})
public class MiniButton<M extends BasicButtonModel> extends JComponent {
	/** Identifier for the {@link ThemeManager} of the {@link BorderModifier} which is used for the normal state. */
	public static final String BORDER_KEY_NORMAL = ThemeManager.BORDER_MODIFIER + ".action.miniButton.normal";
	/** Identifier for the {@link ThemeManager} of the {@link BorderModifier} which is used for the selected state. */
	public static final String BORDER_KEY_NORMAL_SELECTED = ThemeManager.BORDER_MODIFIER + ".action.miniButton.normal.selected";
	/** Identifier for the {@link ThemeManager} of the {@link BorderModifier} which is used for the mouse hover state. */
	public static final String BORDER_KEY_MOUSE_OVER = ThemeManager.BORDER_MODIFIER + ".action.miniButton.mouseOver";
	/** Identifier for the {@link ThemeManager} of the {@link BorderModifier} which is used for the selected mouse hover state. */
	public static final String BORDER_KEY_MOUSE_OVER_SELECTED = ThemeManager.BORDER_MODIFIER + ".action.miniButton.mouseOver.selected";
	/** Identifier for the {@link ThemeManager} of the {@link BorderModifier} which is used for the mouse pressed state. */
	public static final String BORDER_KEY_MOUSE_PRESSED = ThemeManager.BORDER_MODIFIER + ".action.miniButton.mousePressed";
	/** Identifier for the {@link ThemeManager} of the {@link BorderModifier} which is used for the selected mouse pressed state. */
	public static final String BORDER_KEY_MOUSE_PRESSED_SELECTED = ThemeManager.BORDER_MODIFIER + ".action.miniButton.mousePressed.selected";
	
	/** the icon and text on this button */
	private MiniButtonContent content;
	
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
    
    /** Identifier for the {@link ThemeManager} of the {@link BorderModifier} which is used for the normal state. */
    private String borderKeyNormal = BORDER_KEY_NORMAL;
    /** Identifier for the {@link ThemeManager} of the {@link BorderModifier} which is used for the mouse hover state. */
    private String borderKeyMouseOver = BORDER_KEY_MOUSE_OVER;
    /** Identifier for the {@link ThemeManager} of the {@link BorderModifier} which is used for the mouse pressed state. */
    private String borderKeyMousePressed = BORDER_KEY_MOUSE_PRESSED;
    /** Identifier for the {@link ThemeManager} of the {@link BorderModifier} which is used for the selected state. */
    private String borderKeyNormalSelected = BORDER_KEY_NORMAL_SELECTED;
    /** Identifier for the {@link ThemeManager} of the {@link BorderModifier} which is used for the selected mouse hover state. */
    private String borderKeyMouseOverSelected = BORDER_KEY_MOUSE_OVER_SELECTED;
    /** Identifier for the {@link ThemeManager} of the {@link BorderModifier} which is used for the selected mouse pressed state. */
    private String borderKeyMousePressedSelected = BORDER_KEY_MOUSE_PRESSED_SELECTED;
    
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
    	
    	@Override
    	public void borderChanged( BasicButtonModel model, String key, BorderModifier oldBorder, BorderModifier newBorder ){
    		updateBorder();
    	}
    };
    
    /**
     * Creates a new button
     * @param model the model for this button
     */
    public MiniButton( M model ){
        this.model = model;
        content = createButtonContent();
        content.setModel( model );
        
        setLayout( null );
        add( content );
        
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
        
        if( model != null ){
        	content.setForegroundColorId( "action.button.text", "action.button.text.disabled" );
        }
    }
    
    /**
     * Creates the content component of this button.
     * @return the component
     */
    protected MiniButtonContent createButtonContent(){
    	return new MiniButtonContent();
    }
    
    /**
     * Gets access to the content component.
     * @return the content component showing icon and text
     */
    protected MiniButtonContent getContent(){
		return content;
	}
    
    /**
     * Sets the model of this button. The model contains all properties which
     * are necessary to paint this button.
     * @param model the model
     */
    protected void setModel( M model ) {
    	if( this.model != null )
    		this.model.removeListener( listener );
    	
    	content.setModel( model );
        this.model = model;
        
        if( this.model != null ){
        	this.model.addListener( listener );
        	content.setForegroundColorId( "action.button.text", "action.button.text.disabled" );
        }
        
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
     * Gets the key for modifying the border which was set by {@link #setMouseOverBorder(Border)}.
     * @return the key, not <code>null</code> 
     */
    public String getBorderKeyMouseOver(){
		return borderKeyMouseOver;
	}
    
    /**
     * Gets the key for modifying the border which was set by {@link #setMouseOverBorder(Border)}.
     * @param borderKeyMouseOver the new key, not <code>null</code>
     */
    public void setBorderKeyMouseOver( String borderKeyMouseOver ){
    	if( borderKeyMouseOver == null ){
    		throw new IllegalArgumentException( "key must not be null" );
    	}
		this.borderKeyMouseOver = borderKeyMouseOver;
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
     * Gets the key for modifying the border which was set by {@link #setMousePressedBorder(Border)}.
     * @return the key, not <code>null</code> 
     */
    public String getBorderKeyMousePressed(){
		return borderKeyMousePressed;
	}
    
    /**
     * Gets the key for modifying the border which was set by {@link #setMousePressedBorder(Border)}.
     * @param borderKeyMousePressed the new key, not <code>null</code>
     */
    public void setBorderKeyMousePressed( String borderKeyMousePressed ){
    	if( borderKeyMousePressed == null ){
    		throw new IllegalArgumentException( "key must not be null" );
    	}
		this.borderKeyMousePressed = borderKeyMousePressed;
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
     * Gets the key for modifying the border which was set by {@link #setNormalBorder(Border)}.
     * @return the key, not <code>null</code> 
     */
    public String getBorderKeyNormal(){
		return borderKeyNormal;
	}
    
    /**
     * Sets the key for modifying the border which was set by {@link #setNormalBorder(Border)}.
     * @param borderKeyNormal the new key, not <code>null</code>
     */
    public void setBorderKeyNormal( String borderKeyNormal ){
    	if( borderKeyNormal == null ){
    		throw new IllegalArgumentException( "key must not be null" );
    	}
		this.borderKeyNormal = borderKeyNormal;
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
     * Gets the key for modifying the border which was set by {@link #setNormalSelectedBorder(Border)}.
     * @return the key, not <code>null</code> 
     */
    public String getBorderKeyNormalSelected(){
		return borderKeyNormalSelected;
	}
    
    /**
     * Gets the key for modifying the border which was set by {@link #setNormalSelectedBorder(Border)}.
     * @param borderKeyNormalSelected the new key, not <code>null</code>
     */
    public void setBorderKeyNormalSelected( String borderKeyNormalSelected ){
    	if( borderKeyNormalSelected == null ){
    		throw new IllegalArgumentException( "key must not be null" );
    	}
		this.borderKeyNormalSelected = borderKeyNormalSelected;
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
     * Gets the key for modifying the border which was set by {@link #setMouseOverSelectedBorder(Border)}.
     * @return the key, not <code>null</code> 
     */
    public String getBorderKeyMouseOverSelected(){
		return borderKeyMouseOverSelected;
	}
    
    /**
     * Gets the key for modifying the border which was set by {@link #setMouseOverSelectedBorder(Border)}.
     * @param borderKeyMouseOverSelected the new key, not <code>null</code>
     */
    public void setBorderKeyMouseOverSelected( String borderKeyMouseOverSelected ){
    	if( borderKeyMouseOverSelected == null ){
    		throw new IllegalArgumentException( "key must not be null" );
    	}
		this.borderKeyMouseOverSelected = borderKeyMouseOverSelected;
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
    
    /**
     * Gets the key for modifying the border which was set by {@link #setMousePressedSelectedBorder(Border)}.
     * @return the key, not <code>null</code> 
     */
    public String getBorderKeyMousePressedSelected(){
		return borderKeyMousePressedSelected;
	}
    
    /**
     * Gets the key for modifying the border which was set by {@link #setMousePressedSelectedBorder(Border)}.
     * @param borderKeyMousePressedSelected the new key, not <code>null</code>
     */
    public void setBorderKeyMousePressedSelected( String borderKeyMousePressedSelected ){
    	if( borderKeyMousePressedSelected == null ){
    		throw new IllegalArgumentException( "key must not be null" );
    	}
		this.borderKeyMousePressedSelected = borderKeyMousePressedSelected;
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
			
			public Transparency getTransparency(){
				return Transparency.DEFAULT;
			}
		};
        paintable.paint( g );
    }
    
    private void doPaintForeground( Graphics g ){
    	// icon
        paintContent( g );
        
        // focus
        if( isFocusOwner() && isFocusable() && isEnabled() ){
            paintFocus( g );
        }
    }
    
    /**
     * Paints the {@link #getContent() content component}.
     * @param g the graphics context that should be used to paint the content
     */
    protected void paintContent( Graphics g ){
    	paintChildren( g );
    }
    
    private void doPaintBorder( Graphics g ){
    	// border
        Border border = getBorder();
        if( border != null )
            border.paintBorder( this, g, 0, 0, getWidth(), getHeight() );	
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
    public Dimension getPreferredSize() {
    	if( isPreferredSizeSet() )
    		return super.getPreferredSize();
    	
    	Insets max = getMaxBorderInsets();
    	Dimension size = content.getPreferredSize();
    	
        size.width += max.left + max.right + 2;
        size.height += max.top + max.bottom + 2;
        return size;
    }
    
    @Override
    public void doLayout(){
	    Insets insets = getMaxBorderInsets();
	    content.setBounds( insets.left, insets.top, getWidth()-insets.right-insets.left, getHeight()-insets.top-insets.bottom );
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
					border = getBorder( getNormalBorder(), borderKeyNormal );
					break;
				case 1:
					border = getBorder( getMouseOverBorder(), borderKeyMouseOver );
					break;
				case 2:
					border = getBorder( getMousePressedBorder(), borderKeyMousePressed );
					break;
				case 3:
				    border = getBorder( getNormalSelectedBorder(), borderKeyNormalSelected );
				    break;
				case 4:
				    border = getBorder( getMouseOverSelectedBorder(), borderKeyMouseOverSelected );
				    break;
				case 5:
				    border = getBorder( getMousePressedSelectedBorder(), borderKeyMousePressedSelected );
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
                setBorder( getBorder( getMousePressedSelectedBorder(), borderKeyMousePressedSelected ) );
            else
                setBorder( getBorder( getMousePressedBorder(), borderKeyMousePressed ) );
        }
        else if( model.isEnabled() && model.isMouseInside() ){
            if( model.isSelected() )
                setBorder( getBorder( getMouseOverSelectedBorder(), borderKeyMouseOverSelected ) );
            else
                setBorder( getBorder( getMouseOverBorder(), borderKeyMouseOver ) );
        }
        else{
            if( model.isSelected() )
                setBorder( getBorder( getNormalSelectedBorder(), borderKeyNormalSelected ) );
            else
                setBorder( getBorder( getNormalBorder(), borderKeyNormal ) );
        }
    }
    
    private Border getBorder( Border defaultBorder, String key ){
    	BorderModifier modifier = model.getBorder( key );
    	if( modifier == null ){
    		return defaultBorder;
    	}
    	return modifier.modify( defaultBorder );
    }
}
