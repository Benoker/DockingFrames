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

import java.awt.event.*;
import java.util.List;

import javax.swing.*;

import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.util.container.Triple;

/**
 * An expanded {@link BasicButtonModel} that can handle the properties needed
 * to represent a {@link DropDownAction}.
 * @author Benjamin Sigg
 */
public abstract class BasicDropDownButtonModel extends BasicButtonModel{
    /** whether the mouse is currently over the dropdown area */
    private boolean overDropDown = false;
    
    /** whether the selected action is currently enabled */
    private boolean selectionEnabled = true;
    
    /** trigger used on this model */
    private BasicDropDownButtonTrigger trigger;
    
    /**
     * Creates a new model.
     * @param owner the view of this model
     * @param initializer a strategy to lazily initialize resources
     * @param trigger the callback used when the user clicks on the view
     */
    public BasicDropDownButtonModel( JComponent owner, BasicDropDownButtonTrigger trigger, BasicResourceInitializer initializer ) {
        this( owner, trigger, initializer, true );
    }
    
    /**
     * Creates a new model.
     * @param owner the view of this model
     * @param trigger the callback used when the user clicks on the view
     * @param initializer a strategy to lazily initialize resources
     * @param createListener whether the model should add a {@link MouseListener} and
     * a {@link MouseMotionListener} to the view or not.
     */
    public BasicDropDownButtonModel( JComponent owner, BasicDropDownButtonTrigger trigger, BasicResourceInitializer initializer, boolean createListener ) {
        super( owner, trigger, initializer, createListener );
        this.trigger = trigger;
        if( createListener ){
            Listener listener = new Listener();
            owner.addMouseMotionListener( listener );
        }
    }
    
    @Override
    protected List<Triple<KeyStroke, String, Action>> listActions() {
        List<Triple<KeyStroke, String, Action>> list = super.listActions();
        Triple<KeyStroke, String, Action> popup = new Triple<KeyStroke, String, Action>();
        
        popup.setA( KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, 0, true ) );
        popup.setB( "basic_drop_down_model_popup" );
        popup.setC( new AbstractAction(){
            public void actionPerformed( ActionEvent e ) {
                popupTriggered();
            }
        });
        list.add( popup );
        
        return list;
    }
    
    @Override
    protected void setMouseInside( boolean mouseInside ) {
        super.setMouseInside( mouseInside );
        if( !mouseInside )
            setMouseOverDropDown( false );
    }
    
    /**
     * Sets whether the mouse is currently over the dropdown area of the 
     * view or not. Clients should not call this method unless the handle all
     * mouse events.
     * @param overDropDown whether the mouse is over the dropdown area
     */
    protected void setMouseOverDropDown( boolean overDropDown ) {
        this.overDropDown = overDropDown;
        changed();
    }
    
    /**
     * Tells whether the mouse is currently over the dropdown area of
     * the view or not.
     * @return whether the mouse is over the dropdown area
     */
    public boolean isMouseOverDropDown() {
        return overDropDown;
    }
    
    /**
     * Sets whether the selected child-action of the represented {@link DropDownAction}
     * is currently enabled or not.
     * @param selectionEnabled whether the action is enabled
     */
    public void setSelectionEnabled( boolean selectionEnabled ) {
        this.selectionEnabled = selectionEnabled;
        changed();
    }
    
    /**
     * Tells whether the selected child-action of the represented
     * {@link DropDownAction} is currently enabled or not.
     * @return whether the action is enabled
     */
    public boolean isSelectionEnabled() {
        return selectionEnabled;
    }
    
    @Override
    public Icon getPaintIcon() {
        return super.getPaintIcon( isEnabled() && isSelectionEnabled() );
    }
    
    /**
     * Tells whether the location x/y is over the dropdown area of the view or not.
     * The coordinates are in system of the {@link #getOwner() owner's} coordinate-system.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return <code>true</code> if a click with the mouse should open the
     * selection-menu of the {@link DropDownAction}
     */
    protected abstract boolean inDropDownArea( int x, int y );
    
    /**
     * Triggers the drop down menu to open.
     */
    protected void popupTriggered(){
        trigger.popupTriggered();
    }
    
    /**
     * A listener ensuring that the {@link BasicDropDownButtonModel#isMouseOverDropDown() mouseOverDropDown}
     * property has always the correct value.
     * @author Benjamin Sigg
     */
    private class Listener extends MouseMotionAdapter{
        @Override
        public void mouseMoved( MouseEvent e ) {
            boolean over = inDropDownArea( e.getX(), e.getY() );
            if( over != overDropDown ){
                setMouseOverDropDown( over );
            }
        }
        
        @Override
        public void mouseDragged( MouseEvent e ) {
            boolean over = inDropDownArea( e.getX(), e.getY() );
            if( over != overDropDown ){
                setMouseOverDropDown( over );
            }        
        }
    }
}
