/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * A focus tracker keeps information about the last focused {@link Component}
 * of some {@link Container}.
 * @author Benjamin Sigg
 */
public class FocusTracker {
	/** the last component that was focused */
	private Component lastFocused;
	
	/** root container */
	private Component root;
	
	/** added to all {@link Component}s */
	private FocusListener focusListener = new FocusListener(){
		public void focusLost( FocusEvent e ){
			
		}
		public void focusGained( FocusEvent e ){
			lastFocused = e.getComponent();
		}
	};
	
	/** added to all {@link Container}s */
	private ContainerListener containerListener = new ContainerListener(){
		public void componentRemoved( ContainerEvent e ){
			remove( e.getChild() );
		}
		
		public void componentAdded( ContainerEvent e ){
			add( e.getChild() );
		}
	};
	
	/**
	 * Creates a new focus tracker.
	 * @param root the root container whose children can be focused
	 */
	public FocusTracker( Component root ){
		this.root = root;
		add( root );
	}

	/**
	 * Removes any listeners this {@link FocusTracker} added anywhere allowing this {@link FocusTracker}
	 * to be collected by the garbage collector.
	 */
	public void destroy(){
		remove( root );
	}
	
	/**
	 * Gets the last component that was focused
	 * @return the last component, can be <code>null</code>, will be a child of the root container or the root
	 * container itself
	 */
	public Component getLastFocused(){
		return lastFocused;
	}
	
	/**
	 * Adds listeners to <code>component</code> and its children to track the focus.
	 * @param component the component and its children to track
	 */
	protected void add( Component component ){
		component.addFocusListener( focusListener );
		if( component instanceof Container ){
			Container container = (Container)component;
			container.addContainerListener( containerListener );
			for( int i = 0, n = container.getComponentCount(); i<n; i++ ){
				add( container.getComponent( i ));
			}
		}
	}
	
	/**
	 * Removes listeners from <code>component</code> and its children.
	 * @param component the component to remove
	 */
	protected void remove( Component component ){
		if( lastFocused == component ){
			lastFocused = null;
		}
		
		component.removeFocusListener( focusListener );
		if( component instanceof Container ){
			Container container = (Container)component;
			container.removeContainerListener( containerListener );
			for( int i = 0, n = container.getComponentCount(); i<n; i++ ){
				remove( container.getComponent( i ));
			}
		}
	}
}
