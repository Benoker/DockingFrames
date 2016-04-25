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
package bibliothek.gui.dock.common.theme.eclipse;

import bibliothek.extension.gui.dock.theme.eclipse.DefaultEclipseThemeConnector;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabDockAction;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabDockActionLocation;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabStateInfo;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnectorListener;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.core.CommonDockAction;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;

/**
 * This {@link EclipseThemeConnector} pays attention to features only
 * present in {@link CDockable}s.
 * @author Benjamin Sigg
 *
 */
public class CommonEclipseThemeConnector extends DefaultEclipseThemeConnector {
	private CControl control;
	
	private boolean listening = false;
	
	private CDockablePropertyListener propertyListener = new CDockableAdapter(){
		@Override
		public void singleTabShownChanged( CDockable dockable ){
			fire( dockable );
		}
		
		@Override
		public void titleShownChanged( CDockable dockable ){
			fire( dockable );
		}
		
		private void fire( CDockable cdockable ){
			Dockable dockable = cdockable.intern();
			TitleBar bar = getTitleBarKind( dockable.getDockParent(), dockable );
			for( EclipseThemeConnectorListener listener : listeners() ){
				listener.titleBarChanged( CommonEclipseThemeConnector.this, dockable, bar );
			}
		}
	};
	
	/**
	 * Creates a new connector.
	 * @param control the control in whose realm this connector works, not <code>null</code>
	 */
	public CommonEclipseThemeConnector( CControl control ){
		this.control = control;
	}
	
	@Override
	public void addEclipseThemeConnectorListener( EclipseThemeConnectorListener listener ){
		super.addEclipseThemeConnectorListener( listener );
		updateListening();
	}
	
	@Override
	public void removeEclipseThemeConnectorListener( EclipseThemeConnectorListener listener ){
		super.removeEclipseThemeConnectorListener( listener );
		updateListening();
	}
	
	private void updateListening(){
		if( hasListeners() ){
			if( !listening ){
				listening = true;
				control.addPropertyListener( propertyListener );
			}
		}
		else if( listening ){
			listening = false;
			control.removePropertyListener( propertyListener );
		}
	}
	
	@Override
	protected EclipseTabDockActionLocation getLocation( DockAction action, EclipseTabStateInfo tab ){
		if( action instanceof CommonDockAction ){
			CAction common = ((CommonDockAction)action).getAction();
			EclipseTabDockActionLocation location = getLocation( common, tab );
			if( location != null ){
				return location;
			}
		}
		return super.getLocation( action, tab );
	}
	
	/**
	 * Gets the location of <code>action</code> depending on the state of <code>tab</code>. 
	 * @param action the action whose location is searched
	 * @param tab the state of a tab
	 * @return the location or <code>null</code> to select the default location
	 */
	protected EclipseTabDockActionLocation getLocation( CAction action, EclipseTabStateInfo tab ){
		EclipseTabDockAction annotation = action.getClass().getAnnotation( EclipseTabDockAction.class );
		if( annotation != null ){
			return getLocation( annotation, tab );
		}
		else{
			return null;
		}
	}

	@Override
	public TitleBar getTitleBarKind( DockStation parent, Dockable dockable ){
		if( parent instanceof StackDockStation )
			return TitleBar.NONE;
		
		if( dockable instanceof CommonDockable ){
			boolean titleShown = ((CommonDockable)dockable).getDockable().isTitleShown();
			if( !titleShown ){
				boolean singleTab = ((CommonDockable)dockable).getDockable().isSingleTabShown();
				if( !singleTab )
					return TitleBar.NONE_HINTED_BORDERED;
			}
		}
		
		return super.getTitleBarKind( parent, dockable );
	}
}
