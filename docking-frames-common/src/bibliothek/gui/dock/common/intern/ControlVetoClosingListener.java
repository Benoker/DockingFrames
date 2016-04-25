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
package bibliothek.gui.dock.common.intern;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import bibliothek.gui.dock.event.VetoableDockFrontendEvent;
import bibliothek.gui.dock.event.VetoableDockFrontendListener;
import bibliothek.util.FrameworkOnly;

/**
 * A converter listening for {@link VetoableDockFrontendEvent}s and firing
 * new {@link CVetoClosingEvent}s.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class ControlVetoClosingListener implements VetoableDockFrontendListener{
	/** the realm in which this listener works */
	private CControl control;
	
	/** the listener to inform about new events */
	private CVetoClosingListener callback;
	
	/**
	 * Creates a new converter.
	 * @param control the control in whose realm this converter works
	 * @param callback the listener to inform about new events
	 */
	public ControlVetoClosingListener( CControl control, CVetoClosingListener callback ){
		this.control = control;
		this.callback = callback;
	}
	
	public void hidden( VetoableDockFrontendEvent event ){
		CDockable[] dockables = getCDockables( event );
		if( dockables != null && dockables.length > 0 ){
			callback.closed( new CVetoClosingEvent( control, event, dockables ) );
		}
	}

	public void hiding( VetoableDockFrontendEvent event ){
		CDockable[] dockables = getCDockables( event );
		if( dockables != null && dockables.length > 0 ){
			callback.closing( new CVetoClosingEvent( control, event, dockables ) );
		}
	}

	/**
	 * Gets all the {@link CDockable}s that are to be forwarded by this converter.
	 * @param event the source
	 * @return the elements to forward, no event is fired if the result is empty or <code>null</code>
	 */
	protected CDockable[] getCDockables( VetoableDockFrontendEvent event ){
		List<CDockable> list = new ArrayList<CDockable>();
		for( Dockable dockable : event ){
			if( dockable instanceof CommonDockable ){
				list.add( ((CommonDockable)dockable).getDockable() );
			}
		}
		return list.toArray( new CDockable[ list.size() ] );
	}
	
	public void showing( VetoableDockFrontendEvent event ){
		// ignore
	}

	public void shown( VetoableDockFrontendEvent event ){
		// ignore
	}
}
