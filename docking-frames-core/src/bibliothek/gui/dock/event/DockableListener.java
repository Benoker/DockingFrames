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

package bibliothek.gui.dock.event;

import javax.swing.Icon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A listener which is added to a {@link Dockable}. These listeners are manly
 * used to update the titles of a {@link Dockable} whenever necessary.
 * @author Benjamin Sigg
 */
public interface DockableListener {
    /**
     * Will be invoked when a {@link DockTitle} was {@link Dockable#bind(DockTitle) bound}
     * to a {@link Dockable}.
     * @param dockable the <code>Dockable</code> whose title is set
     * @param title the new title
     */
    public void titleBound( Dockable dockable, DockTitle title );
    
    /**
     * Will be invoked when a {@link DockTitle} was {@link Dockable#unbind(DockTitle) unbound}
     * from a {@link Dockable}.
     * @param dockable the <code>Dockable</code> whose title was removed
     * @param title the remove title
     */
    public void titleUnbound( Dockable dockable, DockTitle title );
    
    /**
     * Invoked when the title of a {@link Dockable} has changed.
     * @param dockable the <code>Dockable</code> whose title is changed
     * @param oldTitle the title before the change
     * @param newTitle the title after the change
     */
    public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle );
    
    /**
     * Invoked when the title-icon of a {@link Dockable} has changed
     * @param dockable the <code>Dockable</code> whose title is changed
     * @param oldIcon the old icon, may be <code>null</code>
     * @param newIcon the new icon, may be <code>null</code>
     */
    public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon );
    
    /**
     * Called when the tooltip of a {@link Dockable} changed.
     * @param dockable the dockable whose tooltip changed
     * @param oldToolTip the old value
     * @param newToolTip the new value
     */
    public void titleToolTipChanged( Dockable dockable, String oldToolTip, String newToolTip );
    
    /**
     * Invoked when the properties of a {@link Dockable} have a so dramatically
     * changed, that the {@link DockTitle} <code>title</code> is no longer 
     * considered a good title and should be replaced. The one element which shows 
     * <code>title</code> should unbind it, and request a new title.
     * @param dockable the source of the event
     * @param title the title that should be discarded, can be <code>null</code>
     * to alert elements which do show the <code>null</code>-title
     */
    public void titleExchanged( Dockable dockable, DockTitle title );
}
