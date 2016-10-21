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
package bibliothek.gui.dock.control.focus;

import java.awt.Component;
import java.awt.Window;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.event.FocusVetoListener;
import bibliothek.gui.dock.event.FocusVetoListener.FocusVeto;

/**
 * The default implementation of a {@link FocusRequest}, the kind of 
 * request clients usually will use.
 * @author Benjamin Sigg
 */
public class DefaultFocusRequest implements FocusRequest {
	/** the element whose child gets the focus, may be <code>null</code> */
	private DockElementRepresentative source;
	/** the component which get the focus, can be <code>null</code> */
	private Component component;
	/** whether to force focus transfer always */
	private boolean force;
	/** whether to ensure that the focus really was transferred */
	private boolean ensureFocusSet;
	/** like {@link #ensureFocusSet}, but also ensuring that a child of the focused {@link Dockable} gained the focus */
	private boolean ensureDockableFocused;
	/** whether to execute this request even if the involved {@link Component}s are not visible */
	private boolean hardRequest;
	
	/** whether the {@link FocusVetoListener} approved of this request */
	private FocusVeto veto;
	
	/** the controller in whose realm this request is called */
	private DockController controller;
	
    /**
     * Creates a new request for setting the focused {@link Dockable}.
     * @param source the item to focus, may be <code>null</code>
     * @param force <code>true</code> if this request must ensure that all properties are correct, <code>false</code> if some
     * optimizations are allowed. Clients normally can set this argument to <code>false</code>.
     */
	public DefaultFocusRequest( DockElementRepresentative source, boolean force ){
		this( source, null, force );
	}
	
    /**
     * Creates a new request for setting the focused {@link Dockable}.
     * @param source the item to focus, may be <code>null</code>
     * @param component the {@link Component} which triggered this request for example because the user clicked with the mouse on it. 
     * This request can assume that the focus will automatically be transferred to <code>component</code> by the Swing framework itself.
     * Can be <code>null</code>, in which case this request decides on its own which {@link Component} to focus. This request may or may
     * not do sanity checks concerning <code>component</code>. An invalid argument will silently be ignored and treated 
     * as if it would be <code>null</code>.
     * @param force <code>true</code> if this request must ensure that all properties are correct, <code>false</code> if some
     * optimizations are allowed. Clients normally can set this argument to <code>false</code>.
     */
	public DefaultFocusRequest( DockElementRepresentative source, Component component, boolean force ){
		this( source, component, force, true, false );
	}
	
    /**
     * Creates a new request for setting the focused {@link Dockable}.
     * @param source the item to focus, may be <code>null</code>
     * @param component the {@link Component} which triggered this request for example because the user clicked with the mouse on it. 
     * This request can assume that the focus will automatically be transferred to <code>component</code> by the Swing framework itself.
     * Can be <code>null</code>, in which case this request decides on its own which {@link Component} to focus. This request may or may
     * not do sanity checks concerning <code>component</code>. An invalid argument will silently be ignored and treated 
     * as if it would be <code>null</code>.
     * @param force <code>true</code> if this request must ensure that all properties are correct, <code>false</code> if some
     * optimizations are allowed. Clients normally can set this argument to <code>false</code>.
     * @param ensureFocusSet if <code>true</code>, then this request should make sure that either <code>source</code>
     * itself or one of its {@link DockElementRepresentative} is the focus owner 
     * @param ensureDockableFocused  if <code>true</code>, then this method should make sure that <code>source</code>
     * is the focus owner. This parameter is stronger that <code>ensureFocusSet</code>
     */
	public DefaultFocusRequest( DockElementRepresentative source, Component component, boolean force, boolean ensureFocusSet, boolean ensureDockableFocused ){
		this( source, component, force, ensureFocusSet, ensureDockableFocused, false );
	}
	
    /**
     * Creates a new request for setting the focused {@link Dockable}.
     * @param source the item to focus, may be <code>null</code>
     * @param component the {@link Component} which triggered this request for example because the user clicked with the mouse on it. 
     * This request can assume that the focus will automatically be transferred to <code>component</code> by the Swing framework itself.
     * Can be <code>null</code>, in which case this request decides on its own which {@link Component} to focus. This request may or may
     * not do sanity checks concerning <code>component</code>. An invalid argument will silently be ignored and treated 
     * as if it would be <code>null</code>.
     * @param force <code>true</code> if this request must ensure that all properties are correct, <code>false</code> if some
     * optimizations are allowed. Clients normally can set this argument to <code>false</code>.
     * @param ensureFocusSet if <code>true</code>, then this request should make sure that either <code>source</code>
     * itself or one of its {@link DockElementRepresentative} is the focus owner 
     * @param ensureDockableFocused  if <code>true</code>, then this method should make sure that <code>source</code>
     * is the focus owner. This parameter is stronger that <code>ensureFocusSet</code>
     * @param hardRequest whether this request should be executed even if the involved {@link Component}s are not
     * visible.
     */
	public DefaultFocusRequest( DockElementRepresentative source, Component component, boolean force, boolean ensureFocusSet, boolean ensureDockableFocused, boolean hardRequest ){
		this.source = source;
		this.component = component;
		this.force = force;
		this.ensureFocusSet = ensureFocusSet;
		this.ensureDockableFocused = ensureDockableFocused;
		this.hardRequest = hardRequest;
	}
	
	public DockElementRepresentative getSource(){
		return source;
	}
	
	public Component getComponent(){
		return component;
	}
	
	public int getDelay(){
		return 0;
	}
	
	public boolean isHardRequest(){
		return hardRequest;
	}
	
	public boolean acceptable( Component component ){
		Dockable dockable = getDockable();
		if( ensureDockableFocused && dockable != null ){
			return SwingUtilities.isDescendingFrom( component, dockable.getComponent() );
		}
		return true;
	}
	
	public boolean validate( FocusController controller ){
		this.controller = controller.getController();
		if( force ){
			return true;
		}
		return controller.getFocusedDockable() != getDockable();
	}
	
	private Dockable getDockable(){
		if( source == null ){
			return null;
		}
		return source.getElement().asDockable();
	}
	
	public void veto( FocusVeto veto ){
		this.veto = veto;	
	}
	
	/**
	 * Tells whether the {@link FocusVetoListener}s approved of this request.
	 * @return the veto, may be <code>null</code>
	 */
	public FocusVeto getVeto(){
		return veto;
	}
	
	public FocusRequest grant( Component component ){
		Dockable dockable = getDockable();
		if( dockable != null ){
			if( ensureFocusSet || ensureDockableFocused ){
				return new EnsuringFocusRequest( dockable, ensureDockableFocused, component );
			}
		}
		if( component == null && controller != null ){
			Window root = controller.getRootWindowProvider().searchWindow();
			if( root != null ){
				// if another dockable gains the focus, then it is going to do that 
				// before this request gets even processed. So this is a backup
				// executed to ensure that the application does not lose focus
				return new RepeatingFocusRequest( null, root, isHardRequest() );
			}
		}
		
		return null;
	}
}
