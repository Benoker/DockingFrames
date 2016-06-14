/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.EnableableItem;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.disable.DisablingStrategy;
import bibliothek.gui.dock.disable.DisablingStrategyListener;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Adds a {@link CDockablePropertyListener} to each {@link CDockable} and reads the value of
 * {@link CDockable#isEnabled(bibliothek.gui.dock.common.EnableableItem)} to find out which {@link Dockable}s
 * are disabled.
 * @author Benjamin Sigg
 */
public class CDisablingStrategy implements DisablingStrategy{
	/** listener added to the {@link CControl} */
	private CDockablePropertyListener propertyListener = new CDockableAdapter(){
		public void enabledChanged( CDockable dockable ){
			Dockable item = dockable.intern();
			
			for( DisablingStrategyListener listener : listeners.toArray( new DisablingStrategyListener[ listeners.size() ] )){
				listener.changed( item );
			}
		}
	};
	
	/** all the listeners that were added to this strategy */
	private List<DisablingStrategyListener> listeners = new ArrayList<DisablingStrategyListener>();
	
	/**
	 * Creates a new strategy, this constructor will add a listener to <code>control</code>.
	 * @param control the control in whose realm this strategy will operate
	 */
	public CDisablingStrategy( CControl control ){
		control.addPropertyListener( propertyListener );
	}

	public void addDisablingStrategyListener( DisablingStrategyListener listener ){
		listeners.add( listener );
	}

	public void removeDisablingStrategyListener( DisablingStrategyListener listener ){
		listeners.remove( listener );
	}

	public boolean isDisabled( DockElement item ){
		Dockable dockable = item.asDockable();
		if( dockable instanceof CommonDockable ){
			return !((CommonDockable)dockable).getDockable().isEnabled( EnableableItem.SELF );
		}
		return false;
	}

	public boolean isDisabled( Dockable dockable, DockAction item ){
		if( dockable instanceof CommonDockable ){
			return !((CommonDockable)dockable).getDockable().isEnabled( EnableableItem.ACTIONS );
		}
		return false;
	}

	public boolean isDisabled( Dockable dockable, DockTitle item ){
		if( dockable instanceof CommonDockable ){
			return !((CommonDockable)dockable).getDockable().isEnabled( EnableableItem.TITLES );
		}
		return false;
	}

	public boolean isTabDisabled( Dockable dockable ){
		if( dockable instanceof CommonDockable ){
			return !((CommonDockable)dockable).getDockable().isEnabled( EnableableItem.TABS );
		}
		return false;
	}
}
