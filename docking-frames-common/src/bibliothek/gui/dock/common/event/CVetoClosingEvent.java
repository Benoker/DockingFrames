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
package bibliothek.gui.dock.common.event;

import java.util.Iterator;
import java.util.NoSuchElementException;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.event.VetoableDockFrontendEvent;

/**
 * Event that is delivered to a {@link CVetoClosingListener} before and after a set
 * of {@link CDockable}s is closed.
 * @author Benjamin Sigg
 *
 */
public class CVetoClosingEvent implements Iterable<CDockable> {
	/** the owner of this event */
	private CControl control;
	
	/** the event that caused this event to be fired */
	private VetoableDockFrontendEvent source;
	
	/** the affected dockables */
	private CDockable[] dockables;
	
	/**
	 * Creates a new event.
	 * @param control the owner of this event
	 * @param source the reason for this event
	 * @param dockables the affected dockables (may be only a subset of all affected dockables)
	 */
	public CVetoClosingEvent( CControl control, VetoableDockFrontendEvent source, CDockable... dockables ){
		this.control = control;
		this.source = source;
		this.dockables = dockables;
	}
	
	/**
	 * Gets the source of this event. Clients may use the source to obtain
	 * additional information about this event.
	 * @return the source of this event
	 */
	public VetoableDockFrontendEvent intern(){
		return source;
	}
	
	/**
	 * Gets the number of elements that are associated with this event.
	 * @return the number of elements
	 */
	public int getDockableCount(){
		return dockables.length;
	}
	
	/**
	 * Gets the <code>index</code>'th dockable that is associated with this event.
	 * @param index the index of a dockable
	 * @return the element
	 */
	public CDockable getDockable( int index ){
		return dockables[index];
	}
	
	public Iterator<CDockable> iterator(){
		return new Iterator<CDockable>() {
			private int index = 0;
			
			public boolean hasNext(){
				return index < dockables.length;
			}
			
			public CDockable next(){
				if( hasNext() )
					return dockables[index++];
				throw new NoSuchElementException();
			}
			
			public void remove(){
				throw new UnsupportedOperationException();
			}
		};
	}
	
	/**
	 * Gets the control in whose realm this event was issued.
	 * @return the control, never <code>null</code>
	 */
	public CControl getControl(){
		return control;
	}
	
	/**
	 * Tells whether this event can still be canceled or is already
	 * bound to happen.
	 * @return <code>true</code> if a veto will have an effect
	 */
	public boolean isCancelable(){
		return source.isCancelable();
	}
	
	/**
	 * Stops this event from happening
	 */
	public void cancel(){
		source.cancel();
	}
	
	/**
	 * Tells whether this event has already been canceled.
	 * @return whether {@link #cancel()} has already been called
	 */
	public boolean isCanceled(){
		return source.isCanceled();
	}
	
	/**
	 * Tells whether this event was expected. An event is expected
	 * if {@link CVetoClosingListener#closing(CVetoClosingEvent)} was called before
	 * {@link CVetoClosingListener#closed(CVetoClosingEvent)}.<br>
	 * Unexpected events can never be canceled. They may happen if a {@link CDockable} is closed
	 * in an unexpected way like not using {@link CDockable#setVisible(boolean)} but directly
	 * removing it from its parent {@link DockStation}, or if a new layout ({@link CControl#load(String)}) 
	 * is applied and the framework could not guess correctly which elements are going to be replaced. 
	 * @return whether this event was expected to happen or not
	 */
	public boolean isExpected(){
		return source.isExpected();
	}
}
