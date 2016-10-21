/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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

package bibliothek.gui.dock;

import java.awt.Component;
import java.awt.Point;

import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DockRelocator;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A {@link DockElementRepresentative} is a {@link Component} that represents
 * a {@link DockElement}. For example a {@link DockTitle} would be a representation
 * of a {@link Dockable}. Or a small image in an overview-view might represent
 * any {@link DockElement}.<br>
 * If a <code>DockElementRepresentative</code> is 
 * {@link DockController#addRepresentative(DockElementRepresentative) added}
 * to a {@link DockController} then it may gain new abilities. Clicking on the
 * representative might open the {@link Dockable}s popup menu, dragging it will
 * drag the {@link Dockable}. However, events on the representative are
 * not automatically transferred to its <code>DockElement</code>, that step
 * is always performed by the module that observes the representative.
 * 
 * @author Benjamin Sigg
 */
public interface DockElementRepresentative {
    /**
     * Gets the {@link Component} which represents {@link #getElement() the element}.
     * The result of this method must not change.
     * @return the component, never <code>null</code>
     */
    public Component getComponent();
    
    /**
     * Gets the element which is represented by <code>this</code>.
     * The result of this method must not change.
     * @return the element, never <code>null</code>
     */
    public DockElement getElement();
    
    /**
     * Tells whether this {@link DockElementRepresentative} is used as title.<br>
     * Some modules grant more rights to titles than to non-titles, i.e. 
     * a {@link DockRelocator} can allow drag &amp; drop only for titles.<br>
     * Normally a {@link Dockable} should have only one element that is a title,
     * so if in doubt return <code>false</code>.
     * @return <code>true</code> if this representative should be seen as title
     */
    public boolean isUsedAsTitle();
    
    /**
     * Tells whether a click onto this component should transfer the focus either to this component or to the
     * {@link Dockable}.
     * @return <code>true</code> if clicking this component should influence focus
     */
    public boolean shouldFocus();
    
    /**
     * Tells whether a click onto this component should transfer the focus to the {@link Dockable} or not. If this
     * object is a {@link Dockable}, then a result of <code>true</code> might change the currently focused item
     * of itself. This property is ignored if {@link #shouldFocus()} returns <code>false</code>.
     * @return whether to change the focused component or not
     */
    public boolean shouldTransfersFocus();
    
    /**
     * Adds a listener to this representative, it is not defined what to do
     * with that listener, but most subclasses would just add the listener
     * to their {@link #getComponent() component}. It is valid to do nothing.
     * @param listener the new listener
     */
    public void addMouseInputListener( MouseInputListener listener );
    
    /**
     * Removes a listener from this representative.
     * @param listener the listener to remove
     */
    public void removeMouseInputListener( MouseInputListener listener );
    
    /**
     * Tells whether a popup menu should be opened when the user clicks
     * at <code>click</code> with the mouse. If yes, then the top left edge
     * of the popup should be returned, otherwise <code>null</code> should be
     * returned.
     * @param click the location where the user clicked with the mouse
     * @param popupTrigger whether the invocation is the systems popup trigger
     * or not. Many implementations of this method will return <code>click</code>
     * in case of <code>true</code>, and <code>null</code> in case of <code>false</code>
     * @return the preferred location of a popup or <code>null</code> if no
     * popup-menu should be opened
     */
    public Point getPopupLocation( Point click, boolean popupTrigger );
}
