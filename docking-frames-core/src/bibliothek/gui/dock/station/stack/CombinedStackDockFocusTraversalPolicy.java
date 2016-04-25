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
package bibliothek.gui.dock.station.stack;

import java.awt.Component;
import java.awt.Container;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.focus.SimplifiedFocusTraversalPolicy;
import bibliothek.gui.dock.station.stack.tab.LonelyTabPaneComponent;
import bibliothek.gui.dock.station.stack.tab.Tab;
import bibliothek.gui.dock.station.stack.tab.TabPane;

/**
 * Focus traversal policy for a {@link CombinedStackDockComponent}, this
 * policy switches between current {@link Dockable}, its {@link Tab} and
 * the optional {@link TabPane#getInfoComponent() info component}.
 * @author Benjamin Sigg
 */
public class CombinedStackDockFocusTraversalPolicy implements SimplifiedFocusTraversalPolicy {
	private CombinedStackDockContentPane pane;

	/**
	 * Creates a new traversal policy.
	 * @param pane the owner of this policy, not <code>null</code>
	 */
	public CombinedStackDockFocusTraversalPolicy( CombinedStackDockContentPane pane ){
		this.pane = pane;
	}
	
	/**
	 * Creates a list of all {@link Component}s that should be visited when 
	 * following this policy.
	 * @return the components, might be of size 0 or contain <code>null</code> values.
	 */
	private Component[] list(){
		Component[] list = new Component[3];
		CombinedStackDockComponent<? extends CombinedTab, ?, ? extends LonelyTabPaneComponent> parent = pane.getParentPane();
		
		int index = parent.getSelectedIndex();
		if( index >= 0 ){
			 list[0] = parent.getLayerAt( index );
			 CombinedTab tab = parent.getTab( parent.getDockable( index ) );
			 if( tab != null ){
				 list[1] = tab.getComponent();
			 }
		}
	
		CombinedInfoComponent info = parent.getInfoComponent();
		if( info != null ){
			list[2] = info.getComponent();
		}
		
		return list;
	}
	
	public Component getAfter( Container container, Component component ){
		Component[] list = list();
		for( int i = 0; i < list.length; i++ ){
			if( list[i] == component ){
				return list[ (i+1) % list.length ];
			}
		}
		
		return getDefault( container );
	}

	public Component getBefore( Container container, Component component ){
		Component[] list = list();
		for( int i = 0; i < list.length; i++ ){
			if( list[i] == component ){
				return list[ (i-1+list.length) % list.length ];
			}
		}
		
		return getDefault( container );
	}

	public Component getDefault( Container container ){
		return getFirst( container );
	}

	public Component getFirst( Container container ){
		Component[] list = list();
		for( int i = 0; i < list.length; i++ ){
			if( list[i] != null ){
				return list[ i ];
			}
		}
		
		return null;
	}

	public Component getLast( Container container ){
		Component[] list = list();
		for( int i = list.length-1; i >= 0; i-- ){
			if( list[i] != null ){
				return list[ i ];
			}
		}
		
		return null;
	}
}
