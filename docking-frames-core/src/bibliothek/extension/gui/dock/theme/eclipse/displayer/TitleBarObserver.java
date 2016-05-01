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
package bibliothek.extension.gui.dock.theme.eclipse.displayer;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector.TitleBar;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnectorListener;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * This class adds an {@link EclipseThemeConnectorListener} automatically
 * to the current {@link EclipseThemeConnector} and calls {@link #invalidated()}
 * if the current {@link TitleBar} for some {@link Dockable} does not fit
 * a previously given description.
 * @author Benjamin Sigg
 */
public abstract class TitleBarObserver {
	private DockController controller;
	private DockStation station;
	private TitleBar titleBar;
	private Dockable dockable;
	
	private EclipseThemeConnectorListener connectorListener = new EclipseThemeConnectorListener(){
		public void titleBarChanged( EclipseThemeConnector source, Dockable dockable, TitleBar title ){
			if( connector != null && controller != null && dockable == TitleBarObserver.this.dockable ){
				if( title != titleBar ){
					destroy();
					invalidated();
				}
			}
		}
	};
	
	private PropertyValue<EclipseThemeConnector> connector =
		new PropertyValue<EclipseThemeConnector>( EclipseTheme.THEME_CONNECTOR ){
		
		@Override
		protected void valueChanged( EclipseThemeConnector oldValue, EclipseThemeConnector newValue ){
			if( oldValue != null ){
				oldValue.removeEclipseThemeConnectorListener( connectorListener );
			}
			
			if( connector != null && controller != null && dockable != null && newValue != null ){
				if( titleBar != newValue.getTitleBarKind( station, dockable )){
					destroy();
					invalidated();
				}
			}
			
			if( connector != null && controller != null && newValue != null ){
				newValue.addEclipseThemeConnectorListener( connectorListener );
			}
		}
	};
	
	/**
	 * Creates a new observer.
	 * @param station the current or future parent of <code>dockable</code>
	 * @param dockable the dockable whose {@link TitleBar} is checked
	 * @param titleBar the value that is valid
	 */
	public TitleBarObserver( DockStation station, Dockable dockable, TitleBar titleBar ){
		this.station = station;
		this.dockable = dockable;
		this.titleBar = titleBar;
	}
	
	/**
	 * Exchanges the checked dockable, does not trigger {@link #invalidated()}.
	 * @param dockable the new dockable, may be <code>null</code>
	 */
	public void setDockable( Dockable dockable ){
		this.dockable = dockable;
	}
	
	/**
	 * Sets the controller to read the current {@link EclipseThemeConnector}.
	 * @param controller the controller, <code>null</code> is allowed
	 * and will not trigger {@link #invalidated()}
	 */
	public void setController( DockController controller ){
		this.controller = controller;
		
		if( connector != null ){
			connector.setProperties( controller );
		}
	}
	
	/**
	 * Disposes this observer, this observer will neither receive nor
	 * send events after this method has been called.
	 */
	public void destroy(){
		if( connector != null ){
			setController( null );
			connector.getValue().removeEclipseThemeConnectorListener( connectorListener );
			connector = null;
		}
	}
	
	/**
	 * Called if an invalid {@link TitleBar} has been chosen, this
	 * method is called only once. This observer is {@link #destroy()}ed 
	 * before this method is called.
	 */
	protected abstract void invalidated();
}
