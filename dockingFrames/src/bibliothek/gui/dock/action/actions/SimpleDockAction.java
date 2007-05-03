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

package bibliothek.gui.dock.action.actions;

import javax.swing.Icon;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DockAction;

/**
 * A simple implementation of {@link DockAction}. This action uses for
 * all associated {@link Dockable Dockables} the same settings.
 * @author Benjamin Sigg
 */
public abstract class SimpleDockAction extends AbstractStandardDockAction {
	/** Icon shown if the action is enabled */
	private Icon icon;
	
	/** Icon shown if the action is not enabled */
	private Icon disabledIcon;
    
	/** Text of the action */
    private String text;
    
    /** Tooltip for buttons showing this action */
    private String tooltip;
    
    /** Whether this action can be triggered or not */
    private boolean enabled = true;
    
    public Icon getIcon( Dockable dockable ) {
        return icon;
    }

    public String getText( Dockable dockable ) {
        return text;
    }
    
    /**
     * Gets the text that is shown for this action.
     * @return The text of this action, may be <code>null</code>
     * @see #setText(String)
     */
    public String getText() {
        return text;
    }
    
    /**
     * Sets the <code>text</code> which is shown for this action.
     * @param text The text to show, or <code>null</code>
     */
    public void setText( String text ) {
        this.text = text;
        fireActionTextChanged( getBindeds() );
    }

    public String getTooltipText( Dockable dockable ) {
        return tooltip;
    }
    
    /**
     * Gets the toopltip-text that is shown for this action.
     * @return The tooltip
     * @see #setTooltipText(String)
     */
    public String getTooltipText() {
        return tooltip;
    }
    
    /**
     * Sets the tooltip-text which is shown for this action.
     * @param tooltip The tooltip for this action
     */
    public void setTooltipText( String tooltip ) {
        this.tooltip = tooltip;
        fireActionTooltipTextChanged( getBindeds() );
    }
    
    public boolean isEnabled( Dockable dockable ) {
        return enabled;
    }
    
    /**
     * Gets the enabled-state for this action. Only an action that
     * is enabled can be triggered.
     * @return <code>true</code> if this action can be triggered,
     * <code>false</code> otherwise
     * @see #setEnabled(boolean)
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Sets the enabled-state of this action. This action can be triggered
     * only if it is enabled.
     * @param enabled The state
     */
    public void setEnabled( boolean enabled ) {
        if( this.enabled != enabled ){
            this.enabled = enabled;
            fireActionEnabledChanged( getBindeds() );
        }
    }
    
    /**
     * Gets the default-icon that is shown for this action.
     * @return The icon, may be <code>null</code>
     * @see #setIcon(Icon)
     */
    public Icon getIcon(){
        return icon;
    }
    
    /**
     * Sets the default-<code>icon</code> for this action. This icon
     * will be shown when no other icon fits the current states of
     * the action.
     * @param icon The icon, can be <code>null</code>
     */
    public void setIcon( Icon icon ) {
        this.icon = icon;
        fireActionIconChanged( getBindeds() );
    }
    
    /**
     * Gets the icon that is shown when this action is not enabled.
     * @return The disabled-icon, may be <code>null</code>
     * @see #setDisabledIcon(Icon)
     * @see #isEnabled()
     */
    public Icon getDisabledIcon() {
        return disabledIcon;
    }
    
    public Icon getDisabledIcon( Dockable dockable ){
    	return disabledIcon;
    }
    
    /**
     * Sets an icon that will be shown when this action is not enabled.
     * @param disabledIcon The disabled-icon, can be <code>null</code>
     * @see #setEnabled(boolean)
     */
    public void setDisabledIcon( Icon disabledIcon ) {
		this.disabledIcon = disabledIcon;
    	fireActionDisabledIconChanged( getBindeds() );	
    }
}
