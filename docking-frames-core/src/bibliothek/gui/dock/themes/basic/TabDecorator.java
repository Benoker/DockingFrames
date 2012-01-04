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

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.event.DockableAdapter;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentFactory;
import bibliothek.gui.dock.station.stack.StackDockComponentParent;
import bibliothek.gui.dock.station.stack.StackDockComponentRepresentative;
import bibliothek.gui.dock.station.stack.TabContent;
import bibliothek.gui.dock.station.stack.TabContentFilterListener;
import bibliothek.gui.dock.station.stack.action.DockActionDistributor;
import bibliothek.gui.dock.station.stack.action.DockActionDistributor.Target;
import bibliothek.gui.dock.station.stack.action.DockActionDistributorSource;
import bibliothek.gui.dock.station.stack.tab.TabContentFilter;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.util.PropertyKey;
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
				component.setDockTabPlacement( tabPlacement.getValue() );
				
				component.setController( controller );
				if( dockable != null ){
					component.addTab( dockable.getTitleText(), dockable.getTitleIcon(), representation, dockable );
					component.setSelectedIndex( 0 );
				}
			}
			
			representative.setComponent( component );
		}
	};
	
	private PropertyValue<TabPlacement> tabPlacement = 
		new PropertyValue<TabPlacement>( StackDockStation.TAB_PLACEMENT ){
		
		@Override
		protected void valueChanged( TabPlacement oldValue, TabPlacement newValue ){
			if( component != null ){
				component.setDockTabPlacement( newValue );
			}
		}
	};

	private PropertyValue<TabContentFilter> filter = new PropertyValue<TabContentFilter>( StackDockStation.TAB_CONTENT_FILTER ){
		@Override
		protected void valueChanged( TabContentFilter oldValue, TabContentFilter newValue ){
			if( oldValue != null ){
				oldValue.uninstall( component );
				oldValue.removeListener( filterListener );
			}
			if( newValue != null ){
				newValue.install( component );
				newValue.addListener( filterListener );
			}
			
			updateTabContent();
		}
	};
	
	private DockableListener dockableListener = new DockableAdapter() {
		public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ){			
			updateTabContent();
		}
		
		public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ){
			updateTabContent();
		}
		
		public void titleToolTipChanged( Dockable dockable, String oldTooltip, String newTooltip ){
			updateTabContent();
		}
	};
	
	private TabContentFilterListener filterListener = new TabContentFilterListener() {
		public void contentChanged(){
			updateTabContent();
		}
		
		public void contentChanged( StackDockComponent component ){
			if( component == TabDecorator.this.component ){
				updateTabContent();
			}
		}
		
		public void contentChanged( StackDockStation station ){
			// ignore
		}
		
		public void contentChanged( Dockable dockable ){
			if( dockable == TabDecorator.this.dockable ){
				updateTabContent();
			}
		}
	};
	
	private DockController controller;
	private Dockable dockable;
	private DockStation station;
	private StackDockComponent component;
	private Component representation;
	private StackDockComponentRepresentative representative = new StackDockComponentRepresentative();
	private DockActionDistributorSource actions;
	
	/**
	 * Creates a new decorator
	 * @param station the station for which this decorator is used
	 * @param distributor key to a {@link DockActionDistributor} that suggests the actions for the title
	 */
	public TabDecorator( DockStation station, PropertyKey<DockActionDistributor> distributor ){
		this.station = station;
		if( distributor != null ){
			actions = new DockActionDistributorSource( Target.TITLE, distributor );
		}
	}
	
	public DockStation getStackDockParent(){
		return station;
	}
	
	public int indexOf( Dockable dockable ){
		if( this.dockable == dockable )
			return 0;
		return -1;
	}
	
	/**
	 * Gets the component which is used by this {@link TabDecorator}.
	 * @return the {@link StackDockComponent}, may be <code>null</code>
	 */
	public StackDockComponent getStackComponent(){
		return component;
	}
	
	public void setDockable( Component panel, Dockable dockable ){
		if( this.dockable != null ){
			this.dockable.removeDockableListener( dockableListener );
		}
		
		this.dockable = dockable;
		this.representation = panel;
		
		if( this.dockable != null ){
			this.dockable.addDockableListener( dockableListener );
		}
		
		if( component != null ){
			component.removeAll();
			if( dockable != null ){
				component.addTab( null, null, representation, dockable );
				component.setSelectedIndex( 0 );
				updateTabContent();
			}
		}
		
		representative.setTarget( dockable );
		if( actions != null ){
			actions.setDockable( dockable );
		}
	}

	private void updateTabContent(){
		if( dockable != null && component != null && component.getTabCount() == 1 ){
			TabContent content = new TabContent( dockable.getTitleIcon(), dockable.getTitleText(), dockable.getTitleToolTip() );
			TabContentFilter contentFilter = filter.getValue();
			if( contentFilter != null ){
				content = contentFilter.filter( content, component, dockable );
			}
			if( content == null ){
				component.setTitleAt( 0, null );
				component.setIconAt( 0, null );
				component.setTooltipAt( 0, null );
			}
			else{
				component.setTitleAt( 0, content.getTitle() );
				component.setIconAt( 0, content.getIcon() );
				component.setTooltipAt( 0, content.getTooltip() );
			}
		}
	}
	
	public void setController( DockController controller ){
		this.controller = controller;
		factory.setProperties( controller );
		tabPlacement.setProperties( controller );
		filter.setProperties( controller );
		if( component != null ){
			component.setController( controller );
		}
		representative.setController( controller );
	}
	
	public Component getComponent(){
		if( component == null )
			return null;
		
		return component.getComponent();
	}
	
	public DockActionSource getActionSuggestion(){
		return actions;
	}
}
