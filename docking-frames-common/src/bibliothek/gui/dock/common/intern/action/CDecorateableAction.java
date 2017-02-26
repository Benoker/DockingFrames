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
package bibliothek.gui.dock.common.intern.action;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.core.CommonDecoratableDockAction;

/**
 * An action which has text, icons and other decorations.
 * @author Benjamin Sigg
 * @param <A> the kind of action managed by this {@link CAction}
 */
public class CDecorateableAction<A extends CommonDecoratableDockAction> extends CAction {
    /** the internal representation */
    private A action;
    
    /** whether the text of this action should be shown on buttons */
    private boolean showTextOnButtons = false;
    
    /** all the listener added to this action */
    private List<CDecorateableActionListener> listeners = new ArrayList<CDecorateableActionListener>();
    
    /**
     * Creates a new action.
     * @param action the internal representation, can be <code>null</code>
     * if {@link #init(CommonDecoratableDockAction)} is called later
     */
    public CDecorateableAction( A action ){
        super( null );
        if( action != null )
            init( action );
    }
    
    /**
     * Initializes this action, this method can be called only once.
     * @param action the internal representation
     */
    protected void init( A action ){
        super.init( action );
        this.action = action;
    }
    
    /**
     * Adds the observer <code>listener</code> to this action. The observer will be informed
     * when properties of this {@link CDecorateableAction} changed. 
     * @param listener the new observer
     */
    public void addDecorateableActionListener( CDecorateableActionListener listener ){
    	listeners.add( listener );
    }
    
    /**
     * Removes the observer <code>listener</code> from this action.
     * @param listener the listener to remove
     * @see #addDecorateableActionListener(CDecorateableActionListener)
     */
    public void removeDecorateableActionListener( CDecorateableActionListener listener ){
    	listeners.remove( listener );
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
     * Sets whether the text of this action should be shown if this action is shown as a button.
     * @param showTextOnButtons <code>true</code> if the text should be shown, <code>false</code> otherwise
     */
    public void setShowTextOnButtons( boolean showTextOnButtons ){
    	if( this.showTextOnButtons != showTextOnButtons ){
    		this.showTextOnButtons = showTextOnButtons;
    		for( CDecorateableActionListener listener : listeners.toArray( new CDecorateableActionListener[ listeners.size() ] )){
    			listener.showTextOnButtonsChanged( this );
    		}
    	}
	}
    
    /**
     * Tells whether the text of this action is shown on buttons.
     * @return <code>true</code> if the text is shown, <code>false</code> otherwise
     * @see #setShowTextOnButtons(boolean)
     */
    public boolean isShowTextOnButtons(){
		return showTextOnButtons;
	}
    
    /**
     * Sets a tooltip for this action. The tooltip is a long description of
     * this action.
     * @param tooltip the tooltip
     */
    public void setTooltip( String tooltip ){
        action.setTooltip( tooltip );
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
     * Sets the icon which is used if the mouse is hovering over a button that represents this action.
     * @param icon the icon or <code>null</code>
     */
    public void setHoverIcon( Icon icon ){
    	action.setIcon( ActionContentModifier.NONE_HOVER, icon );
    }
    
    /**
     * Gets the icon which is used if the mouse is hovering over a button that represents this action.
     * @return the icon or <code>null</code>
     */
    public Icon getHoverIcon(){
    	return action.getIcon( ActionContentModifier.NONE_HOVER );
    }
    
    /**
     * Sets the icon which is used if the mouse is pressed over a button that represents this action.
     * @param icon the icon or <code>null</code>
     */
    public void setPressedIcon( Icon icon ){
    	action.setIcon( ActionContentModifier.NONE_PRESSED, icon );
    }
    
    /**
     * Gets the icon which is used if the mouse is pressed over a button that represents this action.
     * @return the icon, can be <code>null</code>
     */
    public Icon getPressedIcon(){
    	return action.getIcon( ActionContentModifier.NONE_PRESSED );
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
     * Sets the icon which is used if the mouse is hovering over a button that represents this action and
     * if this action is disabled.
     * @param icon the icon or <code>null</code>
     */
    public void setDisabledHoverIcon( Icon icon ){
    	action.setIcon( ActionContentModifier.DISABLED_HOVER, icon );
    }
    
    /**
     * Gets the icon which is used if the mouse is hovering over a button that represents this action and
     * if this action is disabled.
     * @return the icon or <code>null</code>
     */
    public Icon getDisabledHoverIcon(){
    	return action.getIcon( ActionContentModifier.DISABLED_HOVER );
    }
    
    /**
     * Sets the icon which is used if the mouse is pressed over a button that represents this action and
     * if this action is disabled.
     * @param icon the icon or <code>null</code>
     */
    public void setDisabledPressedIcon( Icon icon ){
    	action.setIcon( ActionContentModifier.DISABLED_PRESSED, icon );
    }
    
    /**
     * Gets the icon which is used if the mouse is pressed over a button that represents this action and
     * if this action is disabled.
     * @return the icon, can be <code>null</code>
     */
    public Icon getDisabledPressedIcon(){
    	return action.getIcon( ActionContentModifier.DISABLED_PRESSED );
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
    
    /**
     * Tells this action that the {@link #setAccelerator(KeyStroke) accelerator} is global.
     * A global action is executed whenever the accelerator keys are typed anywhere in the scope of
     * DockingFrames (if for example the keys are typed on some random dialog, DockingFrames will not be 
     * informed about the event).
     * @param global should this action be listening to global key events
     */
    public void setAcceleratorIsGlobal( boolean global ){
    	action.setAcceleratorIsGlobal( global );
    }
    
    /**
     * Whether this action is listening to global key events.
     * @return <code>true</code> if global key events are supported
     */
    public boolean isAcceleratorGlobal(){
    	return action.isAcceleratorGlobal();
    }
    
    @Override
    public A intern(){
    	return action;
    }
}
