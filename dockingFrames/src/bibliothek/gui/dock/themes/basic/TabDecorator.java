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
package bibliothek.gui.dock.themes.basic;

import java.awt.Component;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentFactory;
import bibliothek.gui.dock.station.stack.StackDockComponentParent;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * Shows a {@link StackDockComponent} as decoration. 
 * @author Benjamin Sigg
 */
public class TabDecorator implements BasicDockableDisplayerDecorator, StackDockComponentParent{
	private PropertyValue<StackDockComponentFactory> factory = 
		new PropertyValue<StackDockComponentFactory>( StackDockStation.COMPONENT_FACTORY ){
		@Override
		protected void valueChanged( StackDockComponentFactory oldValue, StackDockComponentFactory newValue ){
			if( component != null ){
				component.setController( null );
				component.removeAll();
				component = null;
			}
			
			if( newValue != null ){
				component = newValue.create( TabDecorator.this );
				if( dockable != null ){
					component.setController( controller );
					if( dockable != null ){
						component.addTab( dockable.getTitleText(), dockable.getTitleIcon(), representation, dockable );
						component.setSelectedIndex( 0 );
					}
				}
			}
		}
	};
	
	private DockController controller;
	private Dockable dockable;
	private DockStation station;
	private StackDockComponent component;
	private Component representation;
	
	/**
	 * Creates a new decorator
	 * @param station the station for which this decorator is used
	 */
	public TabDecorator( DockStation station ){
		this.station = station;
	}
	
	public DockStation getStation(){
		return station;
	}
	
	public int indexOf( Dockable dockable ){
		if( this.dockable == dockable )
			return 0;
		return -1;
	}
	
	public void setDockable( Component panel, Dockable dockable ){
		this.dockable = dockable;
		this.representation = panel;
		
		if( component != null ){
			component.removeAll();
			if( dockable != null ){
				component.addTab( dockable.getTitleText(), dockable.getTitleIcon(), representation, dockable );
				component.setSelectedIndex( 0 );
			}
		}
	}
	
	public void setController( DockController controller ){
		this.controller = controller;
		factory.setProperties( controller );
		if( component != null ){
			component.setController( controller );
		}
	}
	
	public Component getComponent(){
		if( component == null )
			return null;
		
		return component.getComponent();
	}
}
