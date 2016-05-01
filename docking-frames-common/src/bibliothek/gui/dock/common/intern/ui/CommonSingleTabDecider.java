/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.common.intern.ui;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.displayer.SingleTabDecider;
import bibliothek.gui.dock.event.SingleTabDeciderListener;

/**
 * Observes and handles the single-tab property of {@link CDockable}s.
 * @author Benjamin Sigg
 */
public class CommonSingleTabDecider implements SingleTabDecider {
	private List<SingleTabDeciderListener> listeners = new ArrayList<SingleTabDeciderListener>();
	private CControl control;
	
	private CDockablePropertyListener dockableListener = new CDockableAdapter(){
		@Override
		public void singleTabShownChanged( CDockable cdockable ){
			Dockable dockable = cdockable.intern();

			for( SingleTabDeciderListener listener : listeners() ){
				listener.showSingleTabChanged( CommonSingleTabDecider.this, dockable );
			}
		}
	};

	/**
	 * Creates a new decider
	 * @param control the realm in which this decider works
	 */
	public CommonSingleTabDecider( CControl control ){
		this.control = control;
	}

	public void addSingleTabDeciderListener( SingleTabDeciderListener listener ){
		boolean empty = listeners.isEmpty();
		listeners.add( listener );
		if( empty ){
			control.addPropertyListener( dockableListener );
		}
	}

	public void removeSingleTabDeciderListener( SingleTabDeciderListener listener ){
		listeners.remove( listener );
		if( listeners.isEmpty() ){
			control.removePropertyListener( dockableListener );
		}
	}

	/**
	 * Gets all listeners that are currently registered at this decider.
	 * @return all listeners
	 */
	protected SingleTabDeciderListener[] listeners(){
		return listeners.toArray( new SingleTabDeciderListener[ listeners.size() ] );
	}

	public boolean showSingleTab( DockStation station, Dockable dockable ){
		if( dockable.asDockStation() != null )
			return false;

		if( station instanceof StackDockStation )
			return false;

		if( dockable instanceof CommonDockable ){
			CDockable cdockable = ((CommonDockable)dockable).getDockable();
			return cdockable.isSingleTabShown();
		}

		return false;
	}
}
