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
package bibliothek.gui.dock.themes.basic.action;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitle.Orientation;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.container.Triple;

/**
 * A class containing all properties and methods needed to handle a button-component
 * that shows the contents of a {@link DockAction}.<br>
 * A model is normally instantiated by a {@link JComponent} which uses <code>this</code>
 * as argument for the constructor of the model. The component can use a subclass
 * of the model to override {@link #changed()}, which is invoked every time when
 * a property of this model changes. The model will add some listeners to
 * the button and update its properties when necessary.
 * @author Benjamin Sigg
 */
public class BasicButtonModel {
    /** whether this model is selected or not */
    private boolean selected = false;
    
    /** the icon shown for this model */
    private Icon icon;
    /** the icon for this model when the model is selected */
    private Icon iconSelected;
    /** the icon for this model if the model is not enabled */
    private Icon iconDisabled;
    /** the icon for this model if the model is not enabled, but selected */
    private Icon iconSelectedDisabled;
    
    /** automatically created icon used when this model is not enabled */
    private Icon autoIconDisabled;
    /** automatically created icon used when this model is not enabled, but selected */
    private Icon autoIconSelectedDisabled;
    
    /** whether the mouse is inside the button or not */
    private boolean mouseInside = false;
    /** whether the first button of the mouse is currently pressed or not */
    private boolean mousePressed = false;
    
    /** the graphical representation of this model */
    private JComponent owner;
    
    /** the orientation of the view */
    private Orientation orientation = Orientation.FREE_HORIZONTAL;
    
    /** a callback used when the user clicked on the view */
    private BasicTrigger trigger;
    
    /** the algorithm that should be used to paint the background of a component */
    private BackgroundPaint background;
    
    /** the source of {@link #background} */
    private BackgroundComponent backgroundComponent;
    
    /** listeners that were added to this model */
    private List<BasicButtonModelListener> listeners = new ArrayList<BasicButtonModelListener>();
    
    /**
     * Creates a new model.
     * @param owner the view of this model
     * @param trigger the callback used when the user clicks on the view
     */
    public BasicButtonModel( JComponent owner, BasicTrigger trigger ){
        this( owner, trigger, true );
    }

    /**
     * Creates a new model.
     * @param owner the view of this model
     * @param trigger the callback used when the user clicks on the view
     * @param createListener whether to create and add a {@link MouseListener} and
     * a {@link MouseMotionListener} to <code>owner</code>. If this argument
     * is <code>false</code>, then the client is responsible to update all
     * properties of this model.
     */
    public BasicButtonModel( JComponent owner, BasicTrigger trigger, boolean createListener ){
        this.owner = owner;
        this.trigger = trigger;
        
        if( createListener ){
            Listener listener = new Listener();
            owner.addMouseListener( listener );
            owner.addMouseMotionListener( listener );
        }
        
        List<Triple<KeyStroke, String, Action>> actions = listActions();
        if( actions != null ){
            InputMap inputMap = owner.getInputMap();
            ActionMap actionMap = owner.getActionMap();
            
            for( Triple<KeyStroke, String, Action> action : actions ){
                inputMap.put( action.getA(), action.getB() );
                actionMap.put( action.getB(), action.getC() );
            }
            
            owner.addFocusListener( new FocusAdapter(){
                @Override
                public void focusLost( FocusEvent e ) {
                    setMousePressed( false );
                }
            });
        }
    }
    
    /**
     * Gets a list of {@link KeyStroke}s, String keys and {@link Action}s which
     * are to be applied to the {@link #getOwner() owner} of this model. 
     * @return the list of actions
     */
    protected List<Triple<KeyStroke, String, Action>> listActions(){
        List<Triple<KeyStroke, String, Action>> actions = new ArrayList<Triple<KeyStroke,String,Action>>();
        
        Triple<KeyStroke, String, Action> select = new Triple<KeyStroke, String, Action>();
        select.setA( KeyStroke.getKeyStroke( KeyEvent.VK_SPACE, 0, false ) );
        select.setB( "button_model_select" );
        select.setC( new AbstractAction(){
            public void actionPerformed(java.awt.event.ActionEvent e){
                setMousePressed( true );
            }
        });
        actions.add( select );
        
        Triple<KeyStroke, String, Action> trigger = new Triple<KeyStroke, String, Action>();
        trigger.setA( KeyStroke.getKeyStroke( KeyEvent.VK_SPACE, 0, true ) );
        trigger.setB( "button_model_trigger" );
        trigger.setC( new AbstractAction(){
            public void actionPerformed(java.awt.event.ActionEvent e){
                if( mousePressed ){
                    setMousePressed( false );
                    if( isEnabled() ){
                        trigger();
                    }
                }
            }
        });
        actions.add( trigger );
        
        return actions;
    }
    
    /**
     * Adds a listener to this model.
     * @param listener the new listener
     */
    public void addListener( BasicButtonModelListener listener ){
    	if( listener == null )
    		throw new NullPointerException( "listener must not be null" );
    	listeners.add( listener );
    }
    
    /**
     * Removes a listener from this model.
     * @param listener the listener to remove
     */
    public void removeListener( BasicButtonModelListener listener ){
    	listeners.remove( listener );
    }
    
    /**
     * Gets all the listeners that are known to this model.
     * @return the listeners
     */
    protected BasicButtonModelListener[] listeners(){
    	return listeners.toArray( new BasicButtonModelListener[ listeners.size() ] );
    }
    
    /**
     * Gets the view which paints the properties of this model.
     * @return the view
     */
    public JComponent getOwner() {
        return owner;
    }
    
    /**
     * Sets the algorithm which should be used to paint the background of the owner.
     * @param background the background algorithm, can be <code>null</code>
     * @param backgroundComponent the source of <code>background</code>. Must not be <code>null</code> if 
     * <code>background</code> is not <code>null</code>, must represents {@link #getOwner()} as {@link Component}.
     */
    public void setBackground( BackgroundPaint background, BackgroundComponent backgroundComponent ){
		if( this.background != background ){
			if( background != null ){
				if( backgroundComponent == null ){
					throw new IllegalArgumentException( "backgroundComponent must not be null" );
				}
				if( backgroundComponent.getComponent() != getOwner() ){
					throw new IllegalArgumentException( "backgroundComponent must exactly represent 'getOwner()'" );
				}
			}
			
			BackgroundPaint old = this.background;
			this.background = background;
			this.backgroundComponent = backgroundComponent;
			
			for( BasicButtonModelListener listener : listeners() ){
				listener.backgroundChanged( this, old, background );
			}
		}
	}
    
    /**
     * Gets the algorithm which should be used to paint the background of components.
     * @return the background, can be <code>null</code>
     */
    public BackgroundPaint getBackground(){
		return background;
	}
    
    /**
     * Gets the source of {@link #getBackground()}. 
     * @return the source, can be <code>null</code> if {@link #getBackground()} returns <code>null</code>
     */
    public BackgroundComponent getBackgroundComponent(){
		return backgroundComponent;
	}
    
    /**
     * Sets the icon which is normally shown on the view.
     * @param icon the new icon, can be <code>null</code>
     */
    public void setIcon( Icon icon ){
    	Icon oldIcon = this.icon;
        this.icon = icon;
        autoIconDisabled = null;
        for( BasicButtonModelListener listener : listeners() ){
        	listener.iconChanged( this, oldIcon, icon );
        }
        changed();
    }
    
    /**
     * Sets the icon which is shown on the view if this model
     * is {@link #isSelected() selected}.
     * @param icon the icon, can be <code>null</code>
     */
    public void setSelectedIcon( Icon icon ) {
    	Icon oldIcon = this.iconSelected;
        this.iconSelected = icon;
        autoIconSelectedDisabled = null;
        for( BasicButtonModelListener listener : listeners() ){
        	listener.selectedIconChanged( this, oldIcon, icon );
        }
        changed();
    }
    
    /**
     * Sets the icon which is shown on the view if this model
     * is not {@link #isEnabled() enabled}.
     * @param icon the icon, can be <code>null</code>
     */
    public void setDisabledIcon( Icon icon ) {
    	Icon oldIcon = this.iconDisabled;
        this.iconDisabled = icon;
        autoIconDisabled = null;
        for( BasicButtonModelListener listener : listeners() ){
        	listener.disabledIconChanged( this, oldIcon, icon );
        }
        changed();
    }
    
    /**
     * Sets the icon which is shown on the view if this model is
     * not {@link #isEnabled() enabled}, but {@link #isSelected() selected}.
     * @param icon the icon, can be <code>null</code>
     */
    public void setSelectedDisabledIcon( Icon icon ) {
    	Icon oldIcon = this.iconSelectedDisabled;
        this.iconSelectedDisabled = icon;
        autoIconSelectedDisabled = null;
        for( BasicButtonModelListener listener : listeners() ){
        	listener.selectedDisabledIconChanged( this, oldIcon, icon );
        }
        changed();
    }
    
    /**
     * Sets the <code>selected</code> property. The view may be painted in
     * a different way dependent on this value.
     * @param selected the new value
     */
    public void setSelected( boolean selected ) {
    	if( this.selected != selected ){
	        this.selected = selected;
	        for( BasicButtonModelListener listener : listeners() ){
	        	listener.selectedStateChanged( this, selected );
	        }
	        changed();
    	}
    }
    
    /**
     * Tells whether this model is selected or not.
     * @return the property
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * Sets the <code>enabled</code> property of this model. A model will not
     * react on a mouse-click if it is not enabled.
     * @param enabled the value
     */
    public void setEnabled( boolean enabled ) {
        owner.setEnabled( enabled );
        if( !enabled ){
            setMousePressed( false );
        }
        
        for( BasicButtonModelListener listener : listeners() ){
        	listener.enabledStateChanged( this, enabled );
        }
        
        changed();
    }
    
    /**
     * Tells whether this model reacts on mouse-clicks or not.
     * @return the property
     */
    public boolean isEnabled() {
        return owner.isEnabled();
    }
    
    /**
     * Sets the text which should be used as tooltip. The text is directly
     * forwarded to the {@link #getOwner() owner} of this model using
     * {@link JComponent#setToolTipText(String) setToolTipText}.
     * @param tooltip the text, can be <code>null</code>
     */
    public void setToolTipText( String tooltip ){
        String old = owner.getToolTipText();
        
        for( BasicButtonModelListener listener : listeners() ){
        	listener.tooltipChanged( this, old, tooltip );
        }
        
        owner.setToolTipText( tooltip );
    }
    
    /**
     * Tells this model which orientation the {@link DockTitle} has, on which
     * the view of this model is displayed.
     * @param orientation the orientation, not <code>null</code>
     */
    public void setOrientation( Orientation orientation ) {
        if( orientation == null  )
            throw new IllegalArgumentException( "Orientation must not be null" );
        
        Orientation old = this.orientation;
        this.orientation = orientation;
        
        
        for( BasicButtonModelListener listener : listeners() ){
        	listener.orientationChanged( this, old, orientation );
        }
        
        changed();
    }
    
    /**
     * Gets the orientation of the {@link DockTitle} on which the view of
     * this model is displayed.
     * @return the orientation
     * @see #setOrientation(DockTitle.Orientation)
     */
    public Orientation getOrientation() {
        return orientation;
    }
    
    /**
     * Called whenever a property of the model has been changed. The 
     * default behavior is just to call {@link Component#repaint() repaint}
     * of the {@link #getOwner() owner}. Clients are encouraged to override
     * this method.
     */
    public void changed(){
        owner.repaint();
    }
    
    /**
     * Gets the maximum size the icons need.
     * @return the maximum size of all icons
     */
    public Dimension getMaxIconSize(){
        int w = 0;
        int h = 0;
        
        if( icon != null ){
            w = Math.max( w, icon.getIconWidth() );
            h = Math.max( h, icon.getIconHeight() );
        }
        
        if( iconSelected != null ){
            w = Math.max( w, iconSelected.getIconWidth() );
            h = Math.max( h, iconSelected.getIconHeight() );
        }
        
        if( iconDisabled != null ){
            w = Math.max( w, iconDisabled.getIconWidth() );
            h = Math.max( h, iconDisabled.getIconHeight() );
        }
        
        if( iconSelectedDisabled != null ){
            w = Math.max( w, iconSelectedDisabled.getIconWidth() );
            h = Math.max( h, iconSelectedDisabled.getIconHeight() );
        }
        
        return new Dimension( w, h );
    }
    
    /**
     * Gets the icon which should be painted on the view.
     * @return the icon to paint, can be <code>null</code>
     */
    public Icon getPaintIcon(){
        return getPaintIcon( isEnabled() );
    }
    
    /**
     * Gets the icon which should be painted on the view.
     * @param enabled whether the enabled or the disabled version of the
     * icon is requested.
     * @return the icon or <code>null</code>
     */
    public Icon getPaintIcon( boolean enabled ){
        if( enabled ){
            if( isSelected() && iconSelected != null )
                return iconSelected;
            
            return icon;
        }
        
        if( isSelected() ){
            if( iconSelectedDisabled != null )
                return iconSelectedDisabled;
            
            if( iconSelected != null )
                autoIconSelectedDisabled = DockUtilities.disabledIcon( owner, iconSelected );
            
            if( autoIconSelectedDisabled != null )
                return autoIconSelectedDisabled;
        }
        
        if( iconDisabled != null )
            return iconDisabled;
        
        if( icon != null )
            autoIconDisabled = DockUtilities.disabledIcon( owner, icon );
        
        if( autoIconDisabled != null )
            return autoIconDisabled;
        
        // no icon to show
        return null;
    }
    
    /**
     * Changes the <code>mouseInside</code> property. The property tells whether
     * the mouse is currently inside the border of the {@link #getOwner() owner}
     * or not. Clients should not call this method unless they handle all
     * mouse events.
     * @param mouseInside whether the mouse is inside
     */
    protected void setMouseInside( boolean mouseInside ) {
        if( this.mouseInside != mouseInside ){
	    	this.mouseInside = mouseInside;
	        
	        for( BasicButtonModelListener listener : listeners() ){
	        	listener.mouseInside( this, mouseInside );
	        }
	        
	        changed();
        }
    }
    
    /**
     * Tells whether the mouse currently is inside the {@link #getOwner() owner}
     * or not.
     * @return <code>true</code> if the mouse is inside
     */
    public boolean isMouseInside() {
        return mouseInside;
    }
    
    /**
     * Changes the <code>mousePressed</code> property. The property tells
     * whether the left mouse button is currently pressed or not. Clients
     * should not invoke this method unless they handle all mouse events.
     * @param mousePressed whether button 1 is pressed
     */
    protected void setMousePressed( boolean mousePressed ) {
        if( this.mousePressed != mousePressed ){
	    	this.mousePressed = mousePressed;
	        
	        for( BasicButtonModelListener listener : listeners() ){
	        	listener.mousePressed( this, mousePressed );
	        }
	        
	        changed();
        }
    }
    
    /**
     * Tells whether the left mouse button is currently pressed or not.
     * @return <code>true</code> if the button is pressed
     */
    public boolean isMousePressed() {
        return mousePressed;
    }
    
    /**
     * Called when the left mouse button has been pressed and released within
     * the {@link #getOwner() owner} and when this model is {@link #isEnabled() enabled}.
     */
    protected void trigger(){
    	if( trigger != null ){
    		trigger.triggered();
    	}
        
        for( BasicButtonModelListener listener : listeners() ){
        	listener.triggered();
        }
    }
    
    /**
     * A mouse listener observing the view of the enclosing model.
     * @author Benjamin Sigg
     */
    private class Listener extends MouseInputAdapter{
        @Override
        public void mouseEntered( MouseEvent e ) {
            setMouseInside( true );
        }
        @Override
        public void mouseExited( MouseEvent e ) {
            setMouseInside( false );
        }
        @Override
        public void mouseDragged( MouseEvent e ) {
            boolean inside = owner.contains( e.getX(), e.getY() );
            if( inside != mouseInside )
                setMouseInside( inside );
        }
        @Override
        public void mousePressed( MouseEvent e ) {
            if( !mousePressed && e.getButton() == MouseEvent.BUTTON1 ){
                setMousePressed( true );
            }
        }
        @Override
        public void mouseReleased( MouseEvent e ) {
            if( mousePressed && e.getButton() == MouseEvent.BUTTON1 ){
                boolean inside = owner.contains( e.getX(), e.getY() );
                if( inside && isEnabled() ){
                    trigger();
                }
                
                setMousePressed( false );
                if( mouseInside != inside )
                    setMouseInside( inside );
            }
        }
    }
}
