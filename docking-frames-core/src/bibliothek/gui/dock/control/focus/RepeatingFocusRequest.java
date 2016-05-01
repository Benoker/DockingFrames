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
import java.awt.KeyboardFocusManager;

import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.event.FocusVetoListener.FocusVeto;

/**
 * Ensures that a specific {@link Component} is focused by calling 
 * {@link Component#requestFocus()} multiple times.
 * @author Benjamin Sigg
 */
public class RepeatingFocusRequest implements FocusRequest{
	private int delay;
	private int attempts;
	private DockElementRepresentative source;
	private Component component;
	private boolean hardRequest;
	
	/**
	 * Creates a new request
	 * @param source the element that gets focused, not <code>null</code>
	 * @param component the {@link Component} that actually gains the focus, not <code>null</code>
	 * @param hardRequest whether this request should be executed even if the application is invisible
	 */
	public RepeatingFocusRequest( DockElementRepresentative source, Component component, boolean hardRequest ){
		this( source, component, 10, 20, hardRequest );
	}
	
	/**
	 * Creates a new request
	 * @param source the element that gets focused, can be <code>null</code>
	 * @param component the {@link Component} that actually gains the focus, not <code>null</code>
	 * @param delay how long to wait until requesting the focus, at least 1
	 * @param attempts how often to try and gain the focus, at least 1
	 * @param hardRequest whether this request should be executed even if the application is invisible
	 */
	public RepeatingFocusRequest( DockElementRepresentative source, Component component, int delay, int attempts, boolean hardRequest ){
		if( component == null ){
			throw new IllegalArgumentException( "component must not be null" );
		}
		if( delay < 1 ){
			throw new IllegalArgumentException( "delay must be >= 1: " + delay );
		}
		if( attempts < 1 ){
			throw new IllegalArgumentException( "attempts must be >= 1: " + attempts );
		}
		
		this.source = source;
		this.component = component;
		this.delay = delay;
		this.attempts = attempts;
		this.hardRequest = hardRequest;
	}
	
	public boolean validate( FocusController controller ){
		return true;
	}
	
	public int getDelay(){
		return delay;
	}
	
	public DockElementRepresentative getSource(){
		return source;
	}
	
	public Component getComponent(){
		return component;
	}
	
	public boolean acceptable( Component component ){
		return this.component == component;
	}
	
	public boolean isHardRequest(){
		return hardRequest;
	}
	
	public void veto( FocusVeto veto ){
		// ignore
	}
	
	public FocusRequest grant( Component component ){
		attempts--;
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        if( manager.getPermanentFocusOwner() != component ){
            manager.clearGlobalFocusOwner();
            component.requestFocus();
        }
        if( attempts > 0 ){
        	return this;
        }
        return null;
	}
}
