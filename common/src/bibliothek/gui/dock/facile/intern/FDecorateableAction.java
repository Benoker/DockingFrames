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
package bibliothek.gui.dock.facile.intern;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import bibliothek.gui.dock.action.actions.SimpleDockAction;
import bibliothek.gui.dock.facile.action.FAction;

/**
 * An action which has text, icons and other decorations.
 * @author Benjamin Sigg
 */
public abstract class FDecorateableAction extends FAction {
    /** the internal representation */
    private SimpleDockAction action;
    
    /**
     * Creates a new action.
     * @param action the internal representation
     */
    public FDecorateableAction( SimpleDockAction action ){
        super( action );
        this.action = action;
    }
    
    /**
     * Sets the text of this action, the text will be visible when this
     * action is shown in a menu. The text is a small description telling the
     * user, for what this action is good for.
     * @param text the text
     */
    public void setText( String text ){
        action.setText( text );
    }
    
    /**
     * Gets the text of this action. 
     * @return the text
     */
    public String getText(){
        return action.getText();
    }
    
    /**
     * Sets a tooltip for this action. The tooltip is a long description of
     * this action.
     * @param tooltip the tooltip
     */
    public void setTooltip( String tooltip ){
        action.setTooltipText( tooltip );
    }
    
    /**
     * Gets the long description of this action.
     * @return the description
     */
    public String getTooltip(){
        return action.getTooltipText();
    }
    
    /**
     * Sets the icon of this action.
     * @param icon the icon, can be <code>null</code>
     */
    public void setIcon( Icon icon ){
        action.setIcon( icon );
    }
    
    /**
     * Gest the icon of this action.
     * @return the icon
     */
    public Icon getIcon(){
        return action.getIcon();
    }
    
    /**
     * Sets the icon of this action. The disabled-icon will only be visible
     * when this action is disabled.
     * @param icon the disabled icon
     * @see #setEnabled(boolean)
     */
    public void setDisabledIcon( Icon icon ){
        action.setDisabledIcon( icon );
    }
    
    /**
     * Gets the disabled icon. This icon is only visible when this action
     * is disabled.
     * @return the disabled icon
     */
    public Icon getDisabledIcon(){
        return action.getDisabledIcon();
    }
    
    /**
     * Enables or disables this action, a disabled action can't be triggered
     * by the user.
     * @param enabled <code>true</code> if this action should be triggerable
     * by the user.
     */
    public void setEnabled( boolean enabled ){
        action.setEnabled( enabled );
    }
    
    /**
     * Tells whether this action can be triggered by the user or not.
     * @return <code>true</code> if this action can be triggered
     */
    public boolean isEnabled(){
        return action.isEnabled();
    }
    
    /**
     * Sets the combination of keys that will trigger this action if the
     * user presses them.
     * @param accelerator the combination of keys, or <code>null</code>
     */
    public void setAccelerator( KeyStroke accelerator ){
        action.setAccelerator( accelerator );
    }
    
    /**
     * Gets the combination of keys that will trigger this action.
     * @return the combination or <code>null</code>
     */
    public KeyStroke getAccelerator(){
        return action.getAccelerator();
    }
}
