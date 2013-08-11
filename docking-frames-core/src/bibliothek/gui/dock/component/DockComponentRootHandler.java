/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.component;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.HashSet;
import java.util.Set;

import bibliothek.gui.DockController;

/**
 * A wrapper for a {@link DockComponentRoot}, implements all the features required to fully support
 * {@link DockComponentConfiguration}.
 * @author Benjamin Sigg
 */
public abstract class DockComponentRootHandler {
	/** the root represented by this */
	private DockComponentRoot root;
	
	/** configuration that was applied to <code>root</code> */
	private DockComponentConfiguration configuration;
	
	/** the controller on which this {@link DockComponentRootHandler} is registered */
	private DockController controller;
	
	/** {@link ContainerListener} added to all {@link Component}s */
	private Listener listener = new Listener();
	
	/** all the {@link Component}s that are known to this handler */
	private Set<Component> handledComponents = new HashSet<Component>();
	
	/**
	 * Creates a new handler
	 * @param root the root represented by <code>this</code>, not <code>null</code>
	 */
	public DockComponentRootHandler( DockComponentRoot root ){
		if( root == null ){
			throw new IllegalArgumentException( "root must not be null" );
		}
		this.root = root;
	}
	
	/**
	 * Sets the configuration that is to be applied to <code>root</code>. Note that this
	 * method does not call {@link DockComponentRoot#setComponentConfiguration(DockComponentConfiguration)}, instead
	 * it is expected to be called by said method.
	 * @param configuration the new configuration, can be <code>null</code>
	 */
	public void setConfiguration( DockComponentConfiguration configuration ){
		if( this.configuration != configuration ){
			if( this.configuration != null ){
				for( Component component : handledComponents ){
					DockComponentConfigurationEvent event = new DockComponentConfigurationEvent( root, component );
					this.configuration.unconfigure( event );
				}
			}
			
			this.configuration = configuration;
			
			if( this.configuration != null ){
				for( Component component : handledComponents ){
					DockComponentConfigurationEvent event = new DockComponentConfigurationEvent( root, component );
					this.configuration.configure( event );
				}
			}
		}
	}
	
	/**
	 * Gets the configuration that is applied to <code>root</code>. Note that this method
	 * does not call {@link DockComponentRoot#getComponentConfiguration()}, instead it is expected to be called by
	 * said method.
	 * @return the current configuration, can be <code>null</code>
	 */
	public DockComponentConfiguration getConfiguration() {
		return configuration;
	}
	
	/**
	 * Sets the controller in whose realm this handler is used.
	 * @param controller the controller, can be <code>null</code>
	 */
	public void setController( DockController controller ){
		if( this.controller != controller ){
			if( this.controller != null ){
				this.controller.getDockComponentManager().unregister( root );
			}
			this.controller = controller;
			if( this.controller != null ){
				this.controller.getDockComponentManager().register( root );
			}
		}
	}
	
	/**
	 * Adds <code>component</code> as root {@link Component}, it will be forwarded to the {@link DockComponentConfiguration}.
	 * @param component a new root component
	 */
	public void addRoot( Component component ){
		add( component );
	}
	
	/**
	 * Removes <code>component</code> as root.
	 * @param component the component to remove
	 */
	public void removeRoot( Component component ){
		remove( component );
	}
	
	private void add( Component component ){
		TraverseResult traversing = shouldTraverse( component );
		
		if( traversing == TraverseResult.EXCLUDE_CHILDREN || traversing == TraverseResult.INCLUDE_CHILDREN ){
			DockComponentConfigurationEvent event = new DockComponentConfigurationEvent( root, component );
			if( configuration != null ){
				configuration.configure( event );
			}
			handledComponents.add( component );
		}
		
		if( traversing == TraverseResult.INCLUDE_CHILDREN ){
			if( component instanceof Container ){
				Container container = (Container)component;
				container.addContainerListener( listener );
				for( int i = 0, n = container.getComponentCount(); i<n; i++ ){
					add( container.getComponent( i ));
				}
			}
		}
	}
	
	private void remove( Component component ){
		if( handledComponents.remove( component )){
			DockComponentConfigurationEvent event = new DockComponentConfigurationEvent( root, component );
			if( configuration != null ){
				configuration.unconfigure( event );
			}
			if( component instanceof Container ){
				Container container = (Container)component;
				container.removeContainerListener( listener );
				for( int i = 0, n = container.getComponentCount(); i<n; i++ ){
					remove( container.getComponent( i ));
				}
			}
		}
	}
	
	/**
	 * Tells whether <code>component</code> should be visited and configured.
	 * @param component the component to check
	 * @return the exact behavior for <code>component</code>
	 */
	protected abstract TraverseResult shouldTraverse( Component component );
	
	/**
	 * Behavior for traversing the {@link Component} tree
	 * @author Benjamin Sigg
	 */
	protected static enum TraverseResult{
		/** traverse a {@link Component} and its children */
		INCLUDE_CHILDREN,
		
		/** traverse a {@link Component}, but do not visit the children */
		EXCLUDE_CHILDREN,
		
		/** do not traverse a {@link Component} */
		EXCLUDE
	}
	
	private class Listener implements ContainerListener{
		public void componentAdded( ContainerEvent e ) {
			add( e.getChild() );
		}

		public void componentRemoved( ContainerEvent e ) {
			remove( e.getChild() );
		}
	}
}
