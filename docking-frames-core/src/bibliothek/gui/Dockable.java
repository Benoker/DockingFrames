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

import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.HierarchyDockActionSource;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.displayer.DockableDisplayerHints;
import bibliothek.gui.dock.dockable.AbstractDockable;
import bibliothek.gui.dock.dockable.DockableStateListener;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A <code>Dockable</code> is a window which can be dragged around by the user. <code>Dockable</code>s
 * need to have a {@link DockStation} as parent, otherwise they are not visible.<br>
 * Several properties are associated with a <code>Dockable</code>:
 * <ul>
 *  <li>A set of {@link DockTitle}s is managed by the parent of this <code>Dockable</code>. The icon and the title-text
 *  of this <code>Dockable</code> might be painted on that title.</li>
 *  <li>A set of {@link DockAction}s is stored in a {@link DockActionSource}. These actions are displayed
 *  on the title(s) and allow users to execute operations that belong to this <code>Dockable</code></li>
 *  <li>A {@link Component} which represents the contents of this <code>Dockable</code></li>
 * </ul>
 * This interface is not intended to be implemented by clients, although they can if some very special behavior is required. 
 * Normally clients are better of using the {@link DefaultDockable} or extending {@link AbstractDockable}.  
 * @author Benjamin Sigg
 */
public interface Dockable extends DockElement, DockElementRepresentative, PlaceholderListItem<Dockable>{
    /**
     * Sets the parent property. This Dockable is shown as direct child of
     * <code>station</code>.<br>
     * Note: this method has to fire a {@link bibliothek.gui.dock.event.DockHierarchyEvent}.<br>
     * Note: when using a {@link bibliothek.gui.dock.dockable.DockHierarchyObserver}, invoke
     * {@link bibliothek.gui.dock.dockable.DockHierarchyObserver#update()} after the
     * property has changed, it will automatically fire a {@link DockHierarchyEvent} if necessary.
     * @param station the parent, may be <code>null</code> if this <code>Dockable</code> is not visible at all.
     */
    public void setDockParent( DockStation station );
    
    /**
     * Gets the current parent, which is the latest argument of {@link #setDockParent(DockStation)}.
     * @return the parent property, can be <code>null</code>
     */
    public DockStation getDockParent();
    
    /**
     * Tells whether this {@link Dockable} can be seen by the user. A {@link Dockable} at least needs
     * to be registered as root-station on a {@link DockController}, or be a child of a root-station
     * to be visible.<br>
     * Implementations will assume that {@link Component}s which are {@link Component#isDisplayable() displayable} are
     * are visible to the user.
     * @return <code>true</code> if the user can actually see this dockable, <code>false</code> 
     * otherwise
     * @deprecated replaced by {@link #isDockableShowing()}, this method will be removed in a future release
     */
    @Deprecated
    @Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_3, description="remove this method" )
    public boolean isDockableVisible();

    /**
     * Tells whether this {@link Dockable} can be seen by the user. A {@link Dockable} at least needs
     * to be registered as root-station on a {@link DockController}, or be a child of a root-station
     * to be visible.<br>
     * Implementations will assume that {@link Component}s which are {@link Component#isDisplayable() displayable} are
     * are visible to the user.
     * @return <code>true</code> if the user can actually see this dockable, <code>false</code> 
     * otherwise
     */
    public boolean isDockableShowing();
    
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
	 * Adds <code>listener</code> to this {@link Dockable}. The listener will be informed about
	 * various events concerning the position and visibility of this dockable.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addDockableStateListener( DockableStateListener listener );
	
	/**
	 * Removes <code>listener</code> from this element.
	 * @param listener the listener to remove
	 */
	public void removeDockableStateListener( DockableStateListener listener );
	
    /**
     * Adds a {@link MouseInputListener} to the {@link #getComponent() component} of this <code>Dockable</code>.
     * A <code>Dockable</code> has to decide by itself which {@link Component Components}
     * should be observer, but generally all free areas should be covered.
     * It's also possible just to ignore the listener, but that's not the
     * preferred behavior.
     * @param listener the mouse listener
     */
    public void addMouseInputListener( MouseInputListener listener );
    
    /**
     * Removes a listener that was earlier added to this <code>Dockable</code>. 
     * @param listener The listener to remove
     */
    public void removeMouseInputListener( MouseInputListener listener );
    
    /**
     * Tells whether <code>station</code> is an accepted parent for this 
     * <code>Dockable</code> or not. The user is not able to drag a <code>Dockable</code> to a station
     * which is not accepted.
     * @param station a possible parent
     * @return whether <code>station</code> could be a parent or not
     */
    public boolean accept( DockStation station );
    
    /**
     * Tells whether <code>base</code> could be the parent of a combination
     * between this <code>Dockable</code> and <code>neighbor</code>. The user is not able
     * to make a combination between this <code>Dockable</code> and <code>neighbor</code>
     * if this method does not accept the operation.
     * @param base the future parent of the combination
     * @param neighbor a <code>Dockable</code> whose parent will be the same parent as
     * the parent of this <code>Dockable</code>
     * @return <code>true</code> if the combination is allowed, <code>false</code>
     * otherwise
     */
    public boolean accept( DockStation base, Dockable neighbor );
    
    /**
     * Gets the {@link Component} which represents this <code>Dockable</code>. Note that
     * the component should be a {@link java.awt.Container#setFocusCycleRoot(boolean) focus cycle root}
     * @return the visible representation
     */
    public Component getComponent();
    
    /**
     * Gets the current title-text of this <code>Dockable</code>.
     * @return the text
     */
    public String getTitleText();
    
    /**
     * Gets a tooltip that is associated with this <code>Dockable</code> and
     * that should be shown on any {@link DockTitle}.
     * @return the tooltip, can be <code>null</code>
     */
    public String getTitleToolTip();
    
    /**
     * Gets the current icon of this <code>Dockable</code>.
     * @return the icon, may be <code>null</code>
     */
    public Icon getTitleIcon();
    
    /**
     * Invoked to get a graphical representation of a title for this <code>Dockable</code>. This method is
     * called either when a title first is required, or when this {@link Dockable}
     * invoked the {@link DockableListener#titleExchanged(Dockable, DockTitle)} method of its
     * current observers. <br>
     * This {@link Dockable} might decide to answer the request by calling
     * {@link DockTitleRequest#answer(DockTitle)}, any title, including <code>null</code> are
     * valid answers. If this {@link Dockable} does not answer the request the associated
     * {@link DockTitleFactory} (as described by {@link DockTitleVersion#getFactory()}) is
     * asked to answer the request.<br>
     * The requests {@link DockTitleRequest#getTarget() target} must be this {@link Dockable}.<br>
     * The normal behavior of this method is to do nothing.
     * @param request which title is required. If this Dockable does not have
     * a special rule for the given request it just ignores the call
     */
    public void requestDockTitle( DockTitleRequest request );
    
    /**
     * Invoked to get {@link DockableDisplayer} for this {@link Dockable}. This method may be called when
     * this {@link Dockable} is dropped onto a new {@link DockStation}, a theme was exchanged, or an existing
     * {@link DockableDisplayer} was discarded.<br>
     * The usual behavior of this method should be to do nothing.
     * @param request callback used to set a new {@link DockableDisplayer}
     */
    public void requestDisplayer( DisplayerRequest request );
    
    /**
     * Called by clients which want to show a title of this <code>Dockable</code>. The
     * method {@link DockTitle#bind()} will be called automatically by the 
     * controller.<br>
     * This method must at least inform all listeners, that <code>title</code>
     * was bound. However, the method {@link DockTitle#bind()} must not
     * be invoked by this method.<br>
     * <code>title</code> must be returned by {@link #listBoundTitles()}
     * unless {@link #unbind(DockTitle)} is called.<br>
     * @param title the title which will be show some things of this <code>Dockable</code>
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
     * @param title the title which will be no longer connected to this <code>Dockable</code>
     * @see #bind(DockTitle)
     * @throws IllegalArgumentException if the title is not known to this <code>Dockable</code>
     */
    public void unbind( DockTitle title );
    
    /**
     * Gets a list of all {@link DockTitle DockTitles} which are currently
     * bound to this <code>Dockable</code>. That are titles for which {@link #bind(DockTitle)}
     * was called, but not yet {@link #unbind(DockTitle)}.
     * @return the list of titles
     */
    public DockTitle[] listBoundTitles();
    
    /**
     * Gets a list of {@link DockAction}s which should be triggerable if
     * this <code>Dockable</code> is visible. The list contains only actions which are
     * directly bound to this <code>Dockable</code> (the actions which are not changed when
     * the parent-station of this <code>Dockable</code> is exchanged).
     * The list can be modified by this <code>Dockable</code> at every time, clients have
     * to react on these changes by adding a {@link DockActionSourceListener} to the result.
     * @return the source of actions, can be <code>null</code> if no actions
     * are available
     */
    public DockActionSource getLocalActionOffers();
    
    /**
     * Gets a list of all {@link bibliothek.gui.dock.action.DockAction}s which
     * might be triggered while this <code>Dockable</code> is visible. The list must contain
     * all actions which are related in any way to this <code>Dockable</code>. Subclasses
     * might use a {@link HierarchyDockActionSource} or the method 
     * {@link DockController#listOffers(Dockable)} to implement this functionality
     * @return the source containing all actions, never <code>null</code>
     */
    public DockActionSource getGlobalActionOffers();

    /**
     * Orders this <code>Dockable</code> to configure <code>hints</code> which will
     * be used by the parent component of this element. This <code>Dockable</code>
     * can store a reference to <code>hints</code> and use it to change the
     * hints whenever it is appropriate. This method will be called with <code>null</code>
     * if the link should be broken.
     * @param hints the hints to configure or <code>null</code> if the last
     * <code>hints</code> should no longer be configured by this element
     */
    public void configureDisplayerHints( DockableDisplayerHints hints );
}
