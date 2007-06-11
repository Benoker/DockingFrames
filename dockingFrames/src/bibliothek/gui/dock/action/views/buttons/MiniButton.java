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

package bibliothek.gui.dock.action.views.buttons;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A small {@link Component} which has the behavior of a button. The 
 * user can trigger an action if he clicks onto a minibutton.<br>
 * Every minibutton has a {@link MiniButtonHandler} which reads Icon, tooltips,
 * etc. from a {@link DockAction} and sets those values to the button.
 * @author Benjamin Sigg
 */
public class MiniButton extends JComponent {
    /** the handler of this button */
	private MiniButtonHandler<? extends DockAction, ? extends MiniButton> handler;
    
    /** the standard-border of this button */
    private Border normalBorder;
    /** the border if the mouse is over this button */
    private Border mouseOverBorder;
    /** the border if the mouse is pressed */
    private Border mousePressedBorder;
    
    /** whether this button is selected (in case the {@link #action} is something to select) */
    private boolean selected;
    /** the icon shown on this button */
    private Icon icon;
    /** disabled version of <code>icon</code> */
    private Icon autoDisabledIcon;
    /** the disabled icon */
    private Icon disabledIcon;
    
    /** the {@link MouseListener} of this button */
    private Listener listener;
    
    /**
     * Creates a new button
     */
    public MiniButton(){
    	setListener( new Listener() );
        
        mousePressedBorder = BorderFactory.createBevelBorder( BevelBorder.LOWERED );
        mouseOverBorder = BorderFactory.createBevelBorder( BevelBorder.RAISED );
    }
    
    /**
     * Sets the listener which recieves all mouse-events.
     * @param listener the listener, not <code>null</code>
     */
    protected void setListener( Listener listener ){
    	if( listener == null )
    		throw new IllegalArgumentException( "Listener must not be null" );
    	
    	if( this.listener != null ){
    		removeMouseListener( this.listener );
    		removeMouseMotionListener( this.listener );
    	}
    	
    	this.listener = listener;
    	
        addMouseListener( listener );
        addMouseMotionListener( listener );
    }
    
    /**
     * Sets the handler that will set the contents of this button. Client code
     * normally doesn't need this method.
     * @param handler the new handler
     */
    public void setHandler( MiniButtonHandler<? extends DockAction, ? extends MiniButton> handler ){
		if( this.handler != null )
			throw new IllegalStateException( "handler already set" );
		
		if( handler == null )
			throw new IllegalArgumentException( "handler must not be null" );
		
		if( handler.getButton() != this )
			throw new IllegalArgumentException( "handler does nothing know about this button" );
    	
    	this.handler = handler;
	}
    
    /**
     * Binds this button to its action.
     */
    public void bind(){
    	handler.bind();
    }
    
    /**
     * Unbinds this button from its action. The button will no longer receive
     * any events from the action.
     */
    public void unbind(){
    	handler.unbind();
    }
    
    /**
     * Gets the action of this button.
     * @return the action
     */
    public DockAction getAction(){
    	return handler.getAction();
    }
    
    @Override
    public void setEnabled( boolean enabled ){
    	super.setEnabled( enabled );
        listener.updateBorder();
    	repaint();
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
        listener.updateBorder();
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
        listener.updateBorder();
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
        listener.updateBorder();
    }
    
    /**
     * Gets the dockable which is affected by the {@link #getAction() action}
     * of this button.
     * @return the affected  dockable
     */
    public Dockable getDockable() {
        return handler.getDockable();
    }

    /**
     * Changes the selected-state of this button. This state is needed to
     * determine which border to show. Note that this method will be called
     * when the {@link #getAction() action} of this button changes its
     * selected-state. Clients normally don't call this method.
     * @param selected the state
     */
    public void setSelected( boolean selected ) {
        this.selected = selected;
        listener.updateBorder();
        repaint();
    }
    
    /**
     * Gets the selected-state of this button.
     * @return the state
     * @see #setSelected(boolean)
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * Sets the icon of this button. The icon is shown in the center of the button.
     * Note that this method is called when the {@link #getAction() action} of 
     * this button changes its icon. Clients normally don't call this method
     * directly. 
     * @param icon the icon to show
     */
    public void setIcon( Icon icon ) {
        this.icon = icon;
        this.autoDisabledIcon = null;
        
        revalidate();
        repaint();
    }
    
    /**
     * Sets the icon that will be used if this button is disabled.
     * @param disabledIcon the icon, can be <code>null</code>
     */
    public void setDisabledIcon( Icon disabledIcon ){
		this.disabledIcon = disabledIcon;
		repaint();
	}
    
    /**
     * Gets the current icon of this button.
     * @return the icon
     */
    public Icon getIcon() {
        return icon;
    }
    
    @Override
    public void paint( Graphics g ){
        // border
        Border border = getBorder();
        if( border != null )
            border.paintBorder( this, g, 0, 0, getWidth(), getHeight() );
        
        Icon icon = getPaintIcon( isEnabled() );
        if( icon != null ){
        	paintIcon( icon, g );
        }
    }
    
    /**
     * Gets the icon that will be painted in the center of this button
     * @param enabled whether the enabled or the disabled icon is requested.
     * @return the icon, might be <code>null</code>
     */
    protected Icon getPaintIcon( boolean enabled ){
    	if( enabled )
    		return icon;
    	else{
    		if( disabledIcon != null )
    			return disabledIcon;
    		
    		if( autoDisabledIcon == null ){
    			autoDisabledIcon = DockUtilities.disabledIcon( this, icon );
    		}
    			
    		return autoDisabledIcon;
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
    	Dimension size;
    	
        if( icon == null )
            size = new Dimension( 16, 16 );
        else
            size = new Dimension( icon.getIconWidth(), icon.getIconHeight() );
        
        size.width += max.left + max.right;
        size.height += max.top + max.bottom;
        return size;
    }
    
    /**
     * Invoked when an action has been dispatched.
     */
    protected void action(){
    	if( handler != null )
    		handler.triggered();
    }
    
    /**
     * Gets the maximal insets of this button
     * @return the insets
     */
	protected Insets getMaxBorderInsets(){
		Insets max = new Insets( 0, 0, 0, 0 );
		for( int i = 0; i < 3; i++ ){
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
     * Listens to a {@link MiniButton}, changes the border of the button
     * and triggers the action of the button, if the user clicks on the button.
     * @author Benjamin Sigg
     */
    protected class Listener extends MouseInputAdapter{
        /** Whether the mouse is pressed */
        protected boolean pressed = false;
        /** Whether the mouse is over the button */
        protected boolean over = false;
        
        @Override
        public void mousePressed( MouseEvent e ) {
            if( isEnabled() ){
                pressed = true;
                updateBorder();
            }
        }
        @Override
        public void mouseReleased( MouseEvent e ) {
            if( pressed ){
                int x = e.getX();
                int y = e.getY();
                
                if( x >= 0 && y >= 0 && x <= getWidth() && y <= getHeight() ){
                    if( isEnabled() ){
                        action();
                    }
                    over = true;
                }
                else{
                    over = false;
                }
                pressed = false;
                updateBorder();
            }
        }
        @Override
        public void mouseEntered( MouseEvent e ) {
            if( !pressed && isEnabled() ){
                over = true;
                updateBorder();
            }
        }
        @Override
        public void mouseExited( MouseEvent e ) {
            if( !pressed ){
                over = false;
                updateBorder();
            }
        }
        
        /**
         * Changes the current border. Uses various states to determine the
         * correct border.
         */
        public void updateBorder(){
            if( !isEnabled() ){
                setBorder( normalBorder );
            }
            else{
                if( pressed ){
                    if( isSelected() )
                        setBorder( mouseOverBorder );
                    else
                        setBorder( mousePressedBorder );
                }
                else if( over ){
                    if( isSelected() )
                        setBorder( mousePressedBorder );
                    else
                        setBorder( mouseOverBorder );
                }
                else{
                    setBorder( normalBorder );
                }
            }
        }
    }
}
