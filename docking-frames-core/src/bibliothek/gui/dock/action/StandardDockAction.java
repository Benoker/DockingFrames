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

package bibliothek.gui.dock.action;

import javax.swing.Icon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.event.StandardDockActionListener;

/**
 * A StandardDockAction is an object that is shown as graphical interface (like a button)
 * on some Components. The user can trigger an action, for example by pressing
 * a button.<br>
 * StandardDockActions are linked with one or many {@link Dockable Dockables}.<br>
 * Note: this interface allows that one action is used for many Dockables with
 * unspecified type. However, some implementations may have restrictions, read 
 * the documentation of those actions carefully.
 * 
 * @author Benjamin Sigg
 *
 */
public interface StandardDockAction extends DockAction {
	/**
     * Gets the Icon of this DockAction, when this DockAction is shown
     * together with <code>dockable</code>. This method must <i>ignore</i> the 
     * {@link ActionContentModifier#getBackup() backup property} of <code>modifier</code>.
     * @param dockable The {@link Dockable} for which the action-icon
     * should be chosen.
     * @param modifier how the icon will be used
     * @return The icon to show for this action when the action is associated
     * with <code>dockable</code>, or <code>null</code>.
     */
    public Icon getIcon( Dockable dockable, ActionContentModifier modifier );
    
    /**
     * Gets all the {@link ActionContentModifier}s for which {@link #getIcon(Dockable, ActionContentModifier)} would
     * return a value other than <code>null</code>.
     * @param dockable the {@link Dockable} for which the action-icons may be chosen.
     * @return the contexts in which an icon is present
     */
    public ActionContentModifier[] getIconContexts( Dockable dockable );

    /**
     * Gets the text of this DockAction, when this DockAction is
     * shown together with <code>dockable</code>.
     * @param dockable The {@link Dockable} for which the action-text 
     * should be chosen.
     * @return The text to show for this action when the action is
     * associated with <code>dockable</code>, or <code>null</code>.
     */
    public String getText( Dockable dockable );

    /**
     * Gets a tooltip for this DockAction, when this DockAction is
     * shown together with <code>dockable</code>.
     * @param dockable The {@link Dockable} for which the action-tooltip 
     * should be chosen.
     * @return The tooltip to show for this action when the action is
     * associated with <code>dockable</code>, or <code>null</code>.
     */
    public String getTooltipText( Dockable dockable );
    
    /**
     * Adds a listener to this DockAction. The listener should be triggered
     * whenever an icon, a text, a tooltip, or the selected/enabled state
     * changes.
     * @param listener The listener to add
     */
    public void addDockActionListener( StandardDockActionListener listener );
    
    /**
     * Removes a listener from this DockStation. Note that this can happen
     * at any time, even while this DockAction is sending an event.
     * @param listener The listener to remove
     */
    public void removeDockActionListener( StandardDockActionListener listener );
    
    /**
     * Tells whether this DockAction can be triggered together with
     * the <code>dockable</code>.
     * @param dockable The {@link Dockable} for which this action maybe
     * triggered.
     * @return <code>true</code> if the user should be able to trigger
     * this action, <code>false</code> otherwise
     */
    public boolean isEnabled( Dockable dockable );
    
    /**
     * Tells whether this {@link DockAction} represents a {@link Dockable}. If so, the framework
     * may register views for this action as {@link DockElementRepresentative}.
     * @param dockable The {@link Dockable} for which this action may be triggered
     * @return the {@link Dockable} which is represented by this action, can be <code>null</code>. 
     * Normally <code>dockable</code> should not be the result.  
     */
    public Dockable getDockableRepresentation( Dockable dockable );
}
