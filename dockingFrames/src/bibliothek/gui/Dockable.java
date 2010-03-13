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
package bibliothek.gui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.HierarchyDockActionSource;
import bibliothek.gui.dock.displayer.DockableDisplayerHints;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A Dockable is a window which is put onto a {@link DockStation}. The user
 * can grab a Dockable and drag it to another station.<br>
 * A Dockable has some properties:
 * <ul>
 *  <li>An icon which is displayed on the title</li>
 *  <li>A title-text which is displayed on the title</li>
 *  <li>A {@link DockStation} which is the parent of the Dockable</li>
 *  <li>A {@link DockController} which is responsible to allow the user to
 *  drag the Dockable.</li>
 *  <li>A {@link Component} which represents the Dockable</li>
 *  <li>A {@link DockActionSource} which provides some {@link DockAction DockActions}. 
 *  Each of the action can be triggered by the user, and can execute any it likes.</li>
 * </ul>
 * 
 * @author Benjamin Sigg
 */
public interface Dockable extends DockElement, DockElementRepresentative, PlaceholderListItem{
    /**
     * Sets the parent property. This Dockable is shown as direct child of
     * <code>station</code>.<br>
     * Note: this method has to fire a {@link bibliothek.gui.dock.event.DockHierarchyEvent}.<br>
     * Note: when using a {@link bibliothek.gui.dock.dockable.DockHierarchyObserver}, invoke
     * {@link bibliothek.gui.dock.dockable.DockHierarchyObserver#update()} after the
     * property has changed, and do not fire a {@link bibliothek.gui.dock.event.DockHierarchyEvent} 
     * here.
     * @param station the parent, may be <code>null</code> if this 
     * Dockable is not visible at all.
     */
    public void setDockParent( DockStation station );
    
    /**
     * Gets the current parent, which is the last argument of {@link #setDockParent(DockStation)}.
     * @return the parent property, can be <code>null</code>
     */
    public DockStation getDockParent();
    
    /**
     * Sets the controller in whose realm this Dockable is. A value of <code>null</code>
     * means that this {@link Dockable} is not managed by a controller.<br>
     * Note: this method has to inform all {@link DockHierarchyListener}s about the change.<br>
     * Note: when using a {@link bibliothek.gui.dock.dockable.DockHierarchyObserver}, invoke
     * {@link bibliothek.gui.dock.dockable.DockHierarchyObserver#controllerChanged(DockController)}
     * @param controller the owner, may be <code>null</code>
     */
    public void setController( DockController controller );
    
    /**
     * Gets the current controller, the argument of the last call of
     * {@link #setController(DockController)}.
     * @return the controller, can be <code>null</code>
     */
    public DockController getController();
    
    /**
     * Adds a listener to this Dockable. The listener has to be informed if
     * a property of this Dockable changes.
     * @param listener the new listener
     */
    public void addDockableListener( DockableListener listener );
    
    /**
     * Removes a listener from this Dockable.
     * @param listener the listener to remove
     */
    public void removeDockableListener( DockableListener listener );
    
    /**
     * Adds a hierarchy-listener to this Dockable. The listener has to be
     * informed whenever the path to this Dockable has been changed.<br>
     * Subclasses might use the {@link bibliothek.gui.dock.dockable.DockHierarchyObserver}
     * to implement this feature in an easy way. Subclasses then only have
     * to call {@link bibliothek.gui.dock.dockable.DockHierarchyObserver#update()}
     * whenever the {@link #setDockParent(DockStation) parent} of this Dockable
     * changes.<br>
     * Note: when using a {@link bibliothek.gui.dock.dockable.DockHierarchyObserver},
     * forward the call directly to {@link bibliothek.gui.dock.dockable.DockHierarchyObserver#addDockHierarchyListener(DockHierarchyListener)}
     * @param listener the new listener
     */
    public void addDockHierarchyListener( DockHierarchyListener listener );
    
    /**
     * Removes a hierarchy-listener from this Dockable.<br>
     * Note: when using a {@link bibliothek.gui.dock.dockable.DockHierarchyObserver},
     * forward the call directly to {@link bibliothek.gui.dock.dockable.DockHierarchyObserver#removeDockHierarchyListener(DockHierarchyListener)}
     * @param listener the listener to remove
     * @see #addDockableListener(DockableListener)
     */
    public void removeDockHierarchyListener( DockHierarchyListener listener );
    
    /**
     * Adds a {@link MouseInputListener} to the component of this Dockable.
     * A Dockable has to decide by itself which {@link Component Components}
     * should be observer, but generally all free areas should be covered.
     * It's also possible just to ignore the listener, but that's not the
     * preferred behavior.
     * @param listener the mouse listener
     */
    public void addMouseInputListener( MouseInputListener listener );
    
    /**
     * Removes a listener that was earlier added to this Dockable. 
     * @param listener The listener to remove
     */
    public void removeMouseInputListener( MouseInputListener listener );
    
    /**
     * Tells whether <code>station</code> is an accepted parent for this 
     * Dockable or not. The user is not able to drag a Dockable to a station
     * which is not accepted.
     * @param station a possible parent
     * @return whether <code>station</code> could be a parent or not
     */
    public boolean accept( DockStation station );
    
    /**
     * Tells whether <code>base</code> could be the parent of a combination
     * between this Dockable and <code>neighbor</code>. The user is not able
     * to make a combination between this Dockable and <code>neighbor</code>
     * if this method does not accept the operation.
     * @param base the future parent of the combination
     * @param neighbor a Dockable whose parent will be the same parent as
     * the parent of this Dockable
     * @return <code>true</code> if the combination is allowed, <code>false</code>
     * otherwise
     */
    public boolean accept( DockStation base, Dockable neighbor );
    
    /**
     * Gets the {@link Component} which represents this Dockable. Note that
     * the component should be a 
     * {@link java.awt.Container#setFocusCycleRoot(boolean) focus cycle root}
     * @return the visible representation
     */
    public Component getComponent();
    
    /**
     * Gest the current title-text of this Dockable.
     * @return the text
     */
    public String getTitleText();
    
    /**
     * Gets a tooltip that is associated with this {@link Dockable} and
     * that should be shown on any {@link DockTitle}.
     * @return the tooltip, can be <code>null</code>
     */
    public String getTitleToolTip();
    
    /**
     * Gets the current icon of this Dockable.
     * @return the icon, may be <code>null</code>
     */
    public Icon getTitleIcon();
    
    /**
     * Invoked to get a graphical representation of a title for this Dockable. This method is
     * called either when a title first is required, or when this {@link Dockable}
     * invoked the {@link DockableListener#titleExchanged(Dockable, DockTitle)} method of its
     * current observers. <br>
     * This {@link Dockable} might decide to answer the request by calling
     * {@link DockTitleRequest#answer(DockTitle)}, any title, including <code>null</code> are
     * valid answers. If this {@link Dockable} does not answer the request the associated
     * {@link DockTitleFactory} (as described by {@link DockTitleVersion#getFactory()}) is
     * asked to answer the request.<br>
     * The requests {@link DockTitleRequest#getTarget() target} must be this {@link Dockable}.
     * @param request which title is required. If this Dockable does not have
     * a special rule for the given request it just ignores the call
     */
    public void requestDockTitle( DockTitleRequest request );
    
    /**
     * Called by clients which want to show a title of this Dockable. The
     * method {@link DockTitle#bind()} will be called automatically by the 
     * controller.<br>
     * This method must at least inform all listeners, that <code>title</code>
     * was bound. However, the method {@link DockTitle#bind()} must not
     * be invoked by this method.<br>
     * <code>title</code> must be returned by {@link #listBoundTitles()}
     * unless {@link #unbind(DockTitle)} is called.<br>
     * @param title the title which will be show some things of this Dockable
     * @see #unbind(DockTitle)
     * @throws IllegalArgumentException if the title is already bound
     */
    public void bind( DockTitle title );
    
    /**
     * Clients should call this method if a {@link DockTitle} is no longer
     * needed. The controller will call {@link DockTitle#unbind()} at an appropriate
     * time.<br>
     * This method must inform all listeners that <code>title</code>
     * is no longer bound. However, this method must not call
     * {@link DockTitle#unbind()}.<br>
     * <code>title</code> must no longer be returned when calling {@link #listBoundTitles()}
     * @param title the title which will be no longer connected to this
     * Dockable
     * @see #bind(DockTitle)
     * @throws IllegalArgumentException if the title is not known to this dockable
     */
    public void unbind( DockTitle title );
    
    /**
     * Gets a list of all {@link DockTitle DockTitles} which are currently
     * bound to this Dockable. That are titles for which {@link #bind(DockTitle)}
     * was called, but not yet {@link #unbind(DockTitle)}.
     * @return the list of titles
     */
    public DockTitle[] listBoundTitles();
    
    /**
     * Gets a list of {@link DockAction}s which should be triggerable if
     * this Dockable is visible. The list contains only actions which are
     * directly bound to this Dockable (the actions which are not changed when
     * the parent-station of this Dockable is exchanged).
     * The list can be modified by this Dockable at every time, clients have
     * to react on these changes by adding
     * a {@link DockActionSourceListener} to the result.
     * @return the source of actions, can be <code>null</code> if no actions
     * are available
     */
    public DockActionSource getLocalActionOffers();
    
    /**
     * Gets a list of all {@link bibliothek.gui.dock.action.DockAction}s which
     * might be triggered while this Dockable is visible. The list must contain
     * all actions which are related in any way to this Dockable. Subclasses
     * might use a {@link HierarchyDockActionSource} or the method 
     * {@link DockController#listOffers(Dockable)} to get this functionality
     * @return the source containing all actions, never <code>null</code>
     */
    public DockActionSource getGlobalActionOffers();

    /**
     * Orders this {@link Dockable} to configure <code>hints</code> which will
     * be used by the parent component of this element. This <code>Dockable</code>
     * can store a reference to <code>hints</code> and use it to change the
     * hints whenever it is appropriate. This method will be called with <code>null</code>
     * if the link should be broken.
     * @param hints the hints to configure or <code>null</code> if the last
     * <code>hints</code> should no longer be configured by this element
     */
    public void configureDisplayerHints( DockableDisplayerHints hints );
}
