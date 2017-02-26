/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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

import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.util.FrameworkOnly;

/**
 * A {@link SharingStandardDockAction} is a {@link StandardDockAction} whose properties are
 * shared by all {@link Dockable}s and whose properties can be modified by the client.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public interface SharingStandardDockAction extends StandardDockAction{
    /**
     * Gets the text that is shown for this action.
     * @return The text of this action, may be <code>null</code>
     * @see #setText(String)
     */
    public String getText();
    
    /**
     * Sets the <code>text</code> which is shown for this action.
     * @param text The text to show, or <code>null</code>
     */
    public void setText( String text );
    
    /**
     * Sets the first part of the tooltip-text which is shown for this action.
     * @param tooltip The client defined part of the tooltip for this action
     */
    public void setTooltip( String tooltip );
    
    /**
     * Gets the first part of the tooltip-text that is shown for this action.
     * @return The client defined part of the tooltip
     * @see #setTooltip(String)
     */
    public String getTooltip();
    
    /**
     * Gets the text that should be shown as tooltip of this action. This text
     * contains the value of {@link #getTooltip()}, but also additional information
     * like the {@link #getAccelerator() accelerator}
     * @return the full tooltip text
     */
    public String getTooltipText();
    
    /**
     * Gets the enabled-state for this action. Only an action that
     * is enabled can be triggered.
     * @return <code>true</code> if this action can be triggered,
     * <code>false</code> otherwise
     * @see #setEnabled(boolean)
     */
    public boolean isEnabled();
    
    /**
     * Sets the enabled-state of this action. This action can be triggered
     * only if it is enabled.
     * @param enabled The state
     */
    public void setEnabled( boolean enabled );
    
    /**
     * Gets the default-icon that is shown for this action.
     * @return The icon, may be <code>null</code>
     * @see #setIcon(Icon)
     */
    public Icon getIcon();
    
    /**
     * Sets the default-<code>icon</code> for this action. This icon
     * will be shown when no other icon fits the current states of
     * the action.
     * @param icon The icon, can be <code>null</code>
     */
    public void setIcon( Icon icon );
    
    /**
     * Gets the icon that is shown when this action is not enabled.
     * @return The disabled-icon, may be <code>null</code>
     * @see #setDisabledIcon(Icon)
     * @see #isEnabled()
     */
    public Icon getDisabledIcon();

    /**
     * Sets an icon that will be shown when this action is not enabled.
     * @param disabledIcon The disabled-icon, can be <code>null</code>
     * @see #setEnabled(boolean)
     */
    public void setDisabledIcon( Icon disabledIcon );
    
    /**
     * Gets the icon that is used if the conditions of <code>modifier</code> are met.
     * @param modifier the key for the icon
     * @return the icon or <code>null</code>
     */
    public Icon getIcon( ActionContentModifier modifier );
    
    /**
     * Sets the icon which is to be used if the conditions of <code>modifier</code> are met.
     * @param modifier the key of the icon
     * @param icon the new icon or <code>null</code>
     */
    public void setIcon( ActionContentModifier modifier, Icon icon );
    
    /**
     * Sets the {@link Dockable} which is represented by this {@link DockAction}. Some views of
     * this {@link DockAction} will register themselves as {@link DockElementRepresentative} representing
     * <code>dockable</code>.
     * @param dockable the new representation, can be <code>null</code>
     */
    public void setDockableRepresentation( Dockable dockable );
    
    /**
     * Gets the {@link Dockable} which is represented by this {@link DockAction}.
     * @return the element, can be <code>null</code>
     * @see #getDockableRepresentation(Dockable)
     */
    public Dockable getDockableRepresentation();
    
    /**
     * Gets the type of {@link KeyEvent} that must happen to trigger this
     * action.
     * @return the type of event or <code>null</code>
     */
    public KeyStroke getAccelerator();
    
    /**
     * Sets the type of event that will trigger this action.
     * @param accelerator the type of event or <code>null</code>.
     */
    public void setAccelerator( KeyStroke accelerator );
    
    /**
     * Tells this action that the {@link #setAccelerator(KeyStroke) accelerator} is global.
     * A global action is executed whenever the accelerator keys are typed anywhere in the scope of
     * DockingFrames (if for example the keys are typed on some random dialog, DockingFrames will not be 
     * informed about the event).
     * @param global should this action be listening to global key events
     */
    public void setAcceleratorIsGlobal( boolean global );
    
    /**
     * Whether this action is listening to global key events.
     * @return <code>true</code> if global key events are supported
     */
    public boolean isAcceleratorGlobal();
}
