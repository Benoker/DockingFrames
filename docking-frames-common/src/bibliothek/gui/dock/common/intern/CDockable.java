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
package bibliothek.gui.dock.common.intern;

import java.awt.Component;
import java.awt.Dimension;

import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.FontMap;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.event.CDockableLocationListener;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.event.CDockableStateListener;
import bibliothek.gui.dock.common.event.CDoubleClickListener;
import bibliothek.gui.dock.common.event.CFocusListener;
import bibliothek.gui.dock.common.event.CKeyboardListener;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import bibliothek.gui.dock.common.intern.action.CloseActionSource;
import bibliothek.gui.dock.common.layout.RequestDimension;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.util.FrameworkOnly;

/**
 * A basic element representing some {@link java.awt.Component} and a wrapper
 * around a {@link Dockable}.<br>
 * <b>Note:</b> This interface is not intended to be implemented by clients. 
 * Clients should either extend the class {@link AbstractCDockable} or use
 * one of {@link DefaultSingleCDockable} or {@link DefaultMultipleCDockable}.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public interface CDockable {
	/**
	 * Key for an action of {@link #getAction(String)}. The action behind this
	 * key should call {@link #setExtendedMode(ExtendedMode)}
	 * with an argument of {@link ExtendedMode#MINIMIZED}.
	 */
	public static final String ACTION_KEY_MINIMIZE = "cdockable.minimize";

	/**
	 * Key for an action of {@link #getAction(String)}. The action behind this
	 * key should call {@link #setExtendedMode(ExtendedMode)}
	 * with an argument of {@link ExtendedMode#MAXIMIZED}.
	 */
	public static final String ACTION_KEY_MAXIMIZE = "cdockable.maximize";

	/**
	 * Key for an action of {@link #getAction(String)}. The action behind this
	 * key should call {@link #setExtendedMode(ExtendedMode)}
	 * with an argument of {@link ExtendedMode#NORMALIZED}.
	 */
	public static final String ACTION_KEY_NORMALIZE = "cdockable.normalize";

	/**
	 * Key for an action of {@link #getAction(String)}. The action behind this
     * key should call {@link #setExtendedMode(ExtendedMode)}
     * with an argument of {@link ExtendedMode#EXTERNALIZED}.
     */
	public static final String ACTION_KEY_EXTERNALIZE = "cdockable.externalize";
	
	/**
	 * Key for an action of {@link #getAction(String)}. The action behind this
	 * key should call {@link #setExtendedMode(ExtendedMode)} with
	 * an argument of {@link ExtendedMode#NORMALIZED}.
	 */
	public static final String ACTION_KEY_UNEXTERNALIZE = "cdockable.unexternalize";
	
	/**
	 * Key for an action of {@link #getAction(String)}. The action behind this
	 * key should call {@link #setExtendedMode(ExtendedMode)} with
	 * an argument of {@link ExtendedMode#EXTERNALIZED}.
	 */
	public static final String ACTION_KEY_UNMAXIMIZE_EXTERNALIZED = "cdockable.unmaximize_externalized";

	/**
	 * Key for an action of {@link #getAction(String)}. The action behind this
	 * key should call {@link #setVisible(boolean)} with the argument
	 * <code>false</code>.
     */
	public static final String ACTION_KEY_CLOSE = "cdockable.close";
	
	/**
	 * Key for an action of {@link #getAction(String)}. The action behind
	 * this key should toggle {@link CDockable#setMinimizedHold(boolean)}.
     */
	public static final String ACTION_KEY_MINIMIZE_HOLD = "cdockable.hold";
	
	/**
	 * Adds a state listener to this dockable, the listener will be informed of
	 * changes of this dockable.
	 * @param listener the new listener
	 */
	public void addCDockableStateListener( CDockableStateListener listener );
	
    /**
     * Adds a property listener to this dockable, the listener will be informed of
     * changes of this dockable.
     * @param listener the new listener
     */
	public void addCDockablePropertyListener( CDockablePropertyListener listener );
	
	/**
	 * Adds a listener to this dockable which gets informed if the location or
	 * the visibility changes. 
	 * @param listener the new listener
	 */
	public void addCDockableLocationListener( CDockableLocationListener listener );
	
	/**
	 * Removes a state listener from this dockable.
	 * @param listener the listener to remove
	 */
	public void removeCDockableStateListener( CDockableStateListener listener );

	/**
     * Removes a property listener from this dockable.
     * @param listener the listener to remove
     */
	public void removeCDockablePropertyListener( CDockablePropertyListener listener );
	
	/**
	 * Removes <code>listener</code> from this dockable
	 * @param listener the listener to remove
	 */
	public void removeCDockableLocationListener( CDockableLocationListener listener );
	
	/**
	 * Adds a focus listener to this dockable. The focus listener gets informed
	 * when this dockable gains or loses the focus.
	 * @param listener the new listener
	 */
	public void addFocusListener( CFocusListener listener );
	
	/**
	 * Removes a focus listener from this dockable.
	 * @param listener the listener to remove
	 */
	public void removeFocusListener( CFocusListener listener );
	
	/**
	 * Adds a keyboard listener to this dockable. The listener gets informed
	 * when a key is pressed or released on this dockable.
	 * @param listener the new listener
	 */
	public void addKeyboardListener( CKeyboardListener listener );
	
    /**
     * Removes a listener from this dockable.
     * @param listener the listener to remove
     */
	public void removeKeyboardListener( CKeyboardListener listener );
	
	/**
	 * Adds a new listener to this dockable. The listener gets informed
	 * when the mouse is clicked twice on this dockable.
	 * @param listener the new listener
	 */
	public void addDoubleClickListener( CDoubleClickListener listener );
	
	/**
	 * Removes a listener from this dockable.
	 * @param listener the listener to remove
	 */
	public void removeDoubleClickListener( CDoubleClickListener listener );
	
	/**
	 * Adds a veto-listener to this dockable, the listener will be informed before this
	 * dockable gets closed and can issue a veto. The listener will receive a
	 * {@link CVetoClosingEvent} which contains only this {@link CDockable} (even if
	 * other dockables are closed at the same time).<br>
	 * {@link CVetoClosingListener}s added to the {@link CControl} are invoked before listeners that
	 * are added to a {@link CDockable}.
	 * @param listener the new listener
	 * @see CControl#addVetoClosingListener(CVetoClosingListener)
	 */
	public void addVetoClosingListener( CVetoClosingListener listener );
	
	/**
	 * Removes <code>listener</code> from this <code>CDockable</code>.
	 * @param listener the listener to remove
	 */
	public void removeVetoClosingListener( CVetoClosingListener listener );
	
	/**
	 * Tells whether this <code>CDockable</code> can be minimized by the user.
	 * @return <code>true</code> if this element can be minimized
	 */
	public boolean isMinimizable();
	
	/**
	 * Tells whether this <code>CDockable</code> can be maximized by the user.
	 * @return <code>true</code> if this element can be maximized
	 */
	public boolean isMaximizable();
	
	/**
	 * Tells whether this <code>CDockable</code> can be externalized by the user.
	 * @return <code>true</code> if this element can be externalized
	 */
	public boolean isExternalizable();
	
	/**
	 * Tells whether this <code>CDockable</code> can be combined with another
	 * <code>Dockable</code> to create a stack.
	 * @return <code>true</code> if this element can be combined with
	 * another <code>Dockable</code>, normally <code>true</code> should be the answer.
	 */
	public boolean isStackable();
	
	/**
	 * Tells whether this <code>CDockable</code> can be closed by the user. A close-button
	 * has to be provided by the <code>CDockable</code> itself. The best way to do that is
	 * to instantiate a {@link CloseActionSource} and include this source
	 * in the array that is returned by {@link CommonDockable#getSources()}.
	 * @return <code>true</code> if this element can be closed
	 */
	public boolean isCloseable();
	
	/**
	 * Tells whether the height of this <code>CDockable</code> should remain the same when
	 * its parent changes the size. This has only effect if the parent can
	 * choose the size of its children. A lock is no guarantee for staying
	 * with the same size, the user still can resize this <code>CDockable</code>.
	 * @return <code>true</code> if the height of this <code>CDockable</code> should remain
	 * the same during resize events of the parent.
	 */
	public boolean isResizeLockedVertically();
	
	/**
     * Tells whether the width of this <code>CDockable</code> should remain the same when
     * its parent changes the size. This has only effect if the parent can
     * choose the size of its children. A lock is no guarantee for staying
     * with the same size, the user still can resize this <code>CDockable</code>.
     * @return <code>true</code> if the width of this <code>CDockable</code> should remain
     * the same during resize events of the parent.
     */
	public boolean isResizeLockedHorizontally();
	
	/**
	 * Gets the preferred size of this {@link CDockable}. The preferred size
	 * will be used to resize this <code>CDockable</code> when 
	 * {@link CControl#handleResizeRequests()} is called. There are no guarantees
	 * that the request can be granted, or will be handled at all.<br>
	 * Calling this method should delete the request, so calling this method
	 * twice should have the effect, that the second time <code>null</code> is
	 * returned.
	 * @return the next requested size or <code>null</code>
	 */
	public RequestDimension getAndClearResizeRequest();
	
	/**
	 * Shows or hides this <code>CDockable</code>. If this <code>CDockable</code> is not visible and
	 * is made visible, then the framework tries to set its location at the last known position.<br>
	 * Subclasses should call {@link CControlAccess#show(CDockable)} or {@link CControlAccess#hide(CDockable)}.
	 * @param visible the new visibility state
	 * @see #isVisible()
	 * @throws IllegalStateException if this dockable can't be made visible
	 */
	public void setVisible( boolean visible );
	
	/**
	 * Tells whether this <code>CDockable</code> is currently visible or not. Visibility
	 * means that this <code>CDockable</code> is in the tree structure of DockingFrames and has a parent. Being
	 * in the structure does not imply being visible on the screen: If some
	 * <code>JFrame</code> is not shown, or some <code>DockStation</code> not
	 * properly added to a parent component, then a visible <code>CDockable</code> can
	 * be invisible for the user. For {@link CDockable}s that are also root-{@link CStation} this method will 
	 * always return <code>true</code>.
	 * Subclasses should return the result of {@link CControlAccess#isVisible(CDockable)}.
	 * @return <code>true</code> if this <code>CDockable</code> can be accessed by the user
	 * through a graphical user interface.
	 * @see #hasParent()
	 */
	public boolean isVisible();
	
	/**
	 * Tells whether this {@link CDockable} has a root station as parent. Having a root station as parent
	 * implies {@link #isVisible()}. For root-{@link CStation} this method will return <code>false</code> if they
	 * do not have a parent, they are not considered to be parents of themselves.<br>
	 * Subclasses should return the result of {@link CControlAccess#hasParent(CDockable)}.
	 * @return <code>true</code> if this dockable has a parent and is in the tree
	 */
	public boolean hasParent();
	
	/**
	 * Tells whether this <code>CDockable</code> is currently visible to the user. A <code>CDockable</code>
	 * which is not {@link #isVisible() visible}, is not <code>dockable visible</code> either. The method
	 * does not take into account that a frame may be positioned such that it is not entierly visible on the
	 * screen. Neither does the method take into account, that a frame may be minimized.
	 * @return <code>true</code> if the user should be able to see this item
	 */
	public boolean isDockableVisible();
	
	/**
	 * Sets the location of this <code>CDockable</code>. If this <code>CDockable</code> is visible, than
	 * this method will take immediately effect. Otherwise the location will be
	 * stored in a cache and read as soon as this <code>CDockable</code> is made visible.<br>
	 * Note that the location can only be seen as a hint, the framework tries
	 * to fit the location as good as possible, but there are no guarantees.<br>
	 * Subclasses should call {@link CControlAccess#getLocationManager()} and 
	 * {@link CLocationModeManager#setLocation(bibliothek.gui.Dockable, CLocation)}.
	 * @param location the new location, <code>null</code> is possible, but
	 * will not move the <code>CDockable</code> immediately
	 * @see #getBaseLocation()
	 */
	public void setLocation( CLocation location );
	
	/**
	 * Gets the location of this <code>CDockable</code>. If this <code>CDockable</code> is visible, then
	 * a location will always be returned. Otherwise a location will only
	 * be returned if it just was set using {@link #setLocation(CLocation)}.
	 * @return the location or <code>null</code>
	 */
	public CLocation getBaseLocation();
	
	/**
	 * Tries to find out at which location this {@link CDockable} may appear if it would be made visible. The default
	 * implementation of this method has severe limitations:
	 * <ul> 
	 * 	<li>Random placements are not considered and the result will be <code>null</code></li>
	 * 	<li>Placeholders are not considered, if this {@link CDockable} is associated with a placeholder, then the
	 * 	placeholder information will be completely lost.</li>
	 *  <li>This {@link CDockable} must not be visible and it must be registered at a {@link CControl}.</li>
	 * </ul>
	 * To be more precise: the location returned by this method may not be the actual location where this dockable
	 * appears because this method does not consider all circumstances.<br>
	 * Clients can make use of this method in two ways:
	 * <ul>
	 * 	<li>They can find out whether the dockable has a location, or will be placed randomly (in which case the result
	 * 	of this method is <code>null</code>)</li>
	 *  <li>They can override this method in which case the {@link CDockable} will appear at the exact location that
	 *  is returned by this method ignoring any other settings.</li>
	 * </ul>
	 * 
	 * @param noBackwardsTransformation if <code>true</code>, then this method should not convert any {@link DockableProperty}
	 * back to a {@link CLocation}, instead it should return <code>null</code> if such a conversion would be necessary. This
	 * way the method does return <code>null</code> in any case where information (e.g. placeholders) could be lost
	 * due to the limitations of {@link CLocation}
	 * 
	 * @return The expected location of this invisible {@link CDockable}, this may either be the location that was set
	 * by calling {@link #setLocation(CLocation)}, the last location of this dockable when it was visible, the
	 * {@link CControl#getDefaultLocation() default location} of the <code>CControl</code> or the default location for the
	 * {@link ExtendedMode#NORMALIZED normalized extended mode}. A value of <code>null</code> is returned if this 
	 * {@link CDockable} would appear at a random location, is not registered at a {@link CControl} or is already visible.
	 */
	public CLocation getAutoBaseLocation( boolean noBackwardsTransformation );
	
    /**
     * Sets how and where this <code>CDockable</code> should be shown. Conflicts with
     * {@link #isExternalizable()}, {@link #isMaximizable()} and {@link #isMinimizable()}
     * will just be ignored. Implementations should call {@link CLocationModeManager#setMode(Dockable, ExtendedMode)}.<br>
     * If this dockable is not visible, then it will be made visible in order to apply the <code>extendedMode</code>.
     * @param extendedMode the size and location
     */
    public void setExtendedMode( ExtendedMode extendedMode );
	
    /**
     * Gets the size and location of this <code>CDockable</code>. Implementations should
     * return {@link CLocationModeManager#getMode(Dockable)}.
     * @return the size and location or <code>null</code> if this <code>CDockable</code>
     * is not part of an {@link CControl}. May be <code>null</code> if this dockable is not visible.
     */
    public ExtendedMode getExtendedMode();
    
    /**
     * Sets the parent of this <code>CDockable</code>. This method can be called by the client
     * or indirectly through {@link #setLocation(CLocation)}.
     * @param area the new parent or <code>null</code>
     */
    public void setWorkingArea( CStation<?> area );
    
    /**
     * Gets the parent of this <code>CDockable</code>, this should be the same as
     * set by the last call of {@link #setWorkingArea(CStation)}.
     * @return the parent or <code>null</code>
     */
    public CStation<?> getWorkingArea();
    
    /**
     * Sets the size of this <code>CDockable</code> when this <code>CDockable</code> is minimized and
     * on a popup window.
     * @param size the size
     */
    public void setMinimizedSize( Dimension size );
    
    /**
     * Gets the size which is used when this <code>CDockable</code> is minimzed and
     * on a popup window. If a value below 0 is set, then the default size
     * is used.
     * @return the size
     */
    public Dimension getMinimizedSize();
    
    /**
     * Sets whether this <code>CDockable</code> should remain visible when minimized
     * and without focus.
     * @param hold whether to remain visible
     */
    public void setMinimizedHold( boolean hold );
    
    /**
     * Tells whether this <code>CDockable</code> remains visible when minimized and 
     * without focus.
     * @return <code>true</code> if this remains visible, <code>false</code>
     * otherwise 
     */
    public boolean isMinimizedHold();
    
    /**
     * Sets whether the user can switch the {@link #isMinimizedHold()} property by clicking
     * on a button that is presented by the {@link FlapDockStation}.
     * @param switchable whether the user is able to switch the hold property
     */
    public void setMinimizedHoldSwitchable( boolean switchable );
    
    /**
     * Tells whether the {@link #isMinimizedHold()} property can be changed by the user
     * by clicking a button that is displayed on the {@link FlapDockStation}.
     * @return <code>true</code> if the user is able to switch the property, <code>false</code>
     * otherwise
     */
    public boolean isMinimizedHoldSwitchable();
    
    /**
     * Tells whether this <code>CDockable</code> shows its title or not. Note that some
     * {@link DockTheme}s might override this setting.
     * @return <code>true</code> if the title is shown, <code>false</code>
     * otherwise.
     */
    public boolean isTitleShown();
    
    /**
     * Tells whether a single tab should be shown for this <code>CDockable</code>. Some
     * {@link DockTheme}s might ignore this setting.
     * @return <code>true</code> if a single tab should be shown,
     * <code>false</code> if not
     */
    public boolean isSingleTabShown();
    
    /**
     * Gets the {@link Component} which should receive the focus once this <code>CDockable</code> is focused.
     * @return the element which should receive the focus, can be <code>null</code>
     */
    public Component getFocusComponent();
    
	/**
	 * Gets the intern representation of this <code>CDockable</code>.
	 * @return the intern representation.
	 */
	public CommonDockable intern();
	
	/**
	 * Gets <code>this</code> or an object representing <code>this</code> as
	 * {@link CStation}.  
	 * @return this as station or <code>null</code>
	 */
	public CStation<?> asStation();
	
	/**
	 * Sets the {@link CControl} which is responsible for this <code>CDockable</code>. Subclasses
	 * must call {@link CControlAccess#link(CDockable, CDockableAccess)} to grant
	 * the <code>CControl</code> access to the internal properties of this
	 * {@link CDockable}. <code>link</code> can also be used to revoke access.
	 * @param control the new control or <code>null</code>
	 */
	@FrameworkOnly
	public void setControlAccess( CControlAccess control );
	
	/**
	 * Gets the control which is responsible for this dockable. Clients
	 * should not use this method unless they know exactly what they are doing.
	 * @return the control
	 */
	@FrameworkOnly
	public CControlAccess getControlAccess();
	
	/**
	 * Gets the control which is responsible for this dockable. This property is set as long as this 
	 * {@link CDockable} is registered at a {@link CControl}.
	 * @return the control in whose realm this dockable is used, can be <code>null</code>
	 */
	public CControl getControl();
	
	/**
	 * Gets an action which is not added to the title by this {@link CDockable}
	 * put by another module.
	 * @param key the name of the action
	 * @return an action or <code>null</code>
	 */
	public CAction getAction( String key );
	
	/**
	 * Gets a mutable map of colors. Clients can put colors into this map, and
	 * the colors will be presented on the screen by various effects.
	 * @return the map, this has always to be the same object
	 */
	public ColorMap getColors();
	
	/**
	 * Gets a mutable map of fonts. Clients can put fonts into this map, and
	 * the fonts will be presented on the screen through various effects.
	 * @return the map, this has always to be the same object
	 */
	public FontMap getFonts();
}
