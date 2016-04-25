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
package bibliothek.extension.gui.dock.theme.eclipse.stack;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.event.MouseInputListener;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabStateInfo;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabComponent;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.station.stack.CombinedTab;
import bibliothek.gui.dock.station.stack.tab.AbstractTab;
import bibliothek.gui.dock.station.stack.tab.Tab;
import bibliothek.gui.dock.station.stack.tab.TabConfiguration;
import bibliothek.gui.dock.station.stack.tab.TabPaneComponent;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;

/**
 * A wrapper around a {@link TabComponent} allowing to use the {@link TabComponent}
 * also as {@link CombinedTab}.
 * @author Benjamin Sigg
 */
public class EclipseTab extends AbstractTab implements CombinedTab{
	/** painting code for this tab */
	private TabComponent component;
	
	private EclipseTabPane parent;
	
	private List<MouseInputListener> mouseInputListeners = new ArrayList<MouseInputListener>();
	
	private boolean enabled = true;
	
	/**
	 * Creates a new tab.
	 * @param parent the owner of this tab.
	 * @param dockable the element associated with this tab.
	 * @param delegate the real {@link TabComponent}
	 */
	public EclipseTab( EclipseTabPane parent, Dockable dockable, TabComponent delegate ){
		super( parent, dockable );
		this.parent = parent;
		this.component = delegate;
		
		component.setIcon( dockable.getTitleIcon() );
		component.setText( dockable.getTitleText() );
		component.setTab( this );
	}
	
	/**
	 * Gets information about the state of this tab.
	 * @return information about this tab
	 */
	public EclipseTabStateInfo getEclipseTabStateInfo(){
		return component.getEclipseTabStateInfo();
	}
	
	public void setConfiguration( TabConfiguration configuration ){
		component.setConfiguration( configuration );
	}
	
	@Override
	public Component getComponent(){
		return component.getComponent();
	}

	/**
	 * Gets the {@link TabComponent} which is shown on this tab.
	 * @return the component
	 */
	public TabComponent getTabComponent(){
		return component;
	}
	
	public void setPaneVisible( boolean visible ){
		parent.getTabHandler().setVisible( this, visible );
	}
	
	public boolean isPaneVisible(){
		return parent.getTabHandler().isVisible( this );
	}
	
	public void setZOrder( int order ){
		parent.getTabHandler().setZOrder( this, order );	
	}
	
	public int getZOrder(){
		return parent.getTabHandler().getZOrder( this );
	}
	
	public void setIcon( Icon icon ){
		component.setIcon( icon );
	}

	public void setText( String text ){
		component.setText( text );
	}

	public void setTooltip( String tooltip ){
		component.setTooltip( tooltip );
	}
	
	@Override
	public void setOrientation( TabPlacement orientation ){
		super.setOrientation( orientation );
		component.setOrientation( orientation );
	}

	public Dimension getMinimumSize( Tab[] tabs ){
		return component.getMinimumSize( extract( tabs ) );
	}
	
	public Dimension getPreferredSize( Tab[] tabs ){
		return component.getPreferredSize( extract( tabs ) );
	}
	
	private TabComponent[] extract( Tab[] tabs ){
		TabComponent[] components = new TabComponent[ tabs.length ];
		for( int i = 0; i < tabs.length; i++ ){
			if( tabs[i] instanceof EclipseTab ){
				components[i] = ((EclipseTab)tabs[i]).getTabComponent();
			}
		}
		return components;
	}
	
	public DockElement getElement(){
		return getDockable();
	}
	
	public Point getPopupLocation( Point click, boolean popupTrigger ){
		if( popupTrigger ){
			return click;
		}
		else{
			return null;
		}
	}

	public boolean isUsedAsTitle(){
		return true;
	}
	
	public boolean shouldFocus(){
    	return true;
    }
	
	public boolean shouldTransfersFocus(){
		return true;
	}

	public void setEnabled( boolean enabled ){
		if( enabled != this.enabled ){
			this.enabled = enabled;
			component.setEnabled( enabled );
			
			if( enabled ){
				for( MouseInputListener listener : mouseInputListeners ){
					component.addMouseInputListener( listener );
				}
			}
			else{
				for( MouseInputListener listener : mouseInputListeners ){
					component.removeMouseInputListener( listener );
				}
			}
		}
	}
	
	public void removeMouseInputListener( MouseInputListener listener ){
		mouseInputListeners.remove( listener );
		if( enabled ){
			component.removeMouseInputListener( listener );
		}
	}

	public void addMouseInputListener( MouseInputListener listener ){
		mouseInputListeners.add( listener );
		if( enabled ){
			component.addMouseInputListener( listener );
		}
	}
	
	/**
	 * Adds observers to parent and {@link DockController} to keep the 
	 * user interface up to date.
	 */
	public void bind(){
		component.bind();
		super.bind();
	}
	
	/**
	 * Removes observers from parent and {@link DockController}.
	 */
	public void unbind(){
		component.unbind();
		super.unbind();
	}
	
	@Override
	public Insets getOverlap( TabPaneComponent other ){
		if( other instanceof EclipseTab ){
			EclipseTab tab = (EclipseTab)other;
			return getTabComponent().getOverlap( tab.getTabComponent() );
		}
		
		return super.getOverlap( other );
	}

	/**
	 * Tells this tab to paint icons when not selected.
	 * @param paint whether to paint the icons
	 */
	public void setPaintIconWhenInactive( boolean paint ){
		component.setPaintIconWhenInactive( paint );
	}

	@Override
	protected void informFocusChanged( boolean focused ){
		component.setFocused( focused );
	}

	@Override
	protected void informSelectionChanged( boolean selected ){
		component.setSelected( selected );
	}
	
	@Override
	public String toString(){
		Dockable dockable = component.getElement().asDockable();
		if( dockable == null )
			return getClass().getSimpleName() + "@[component=" + component + "]";
		else
			return getClass().getSimpleName() + "@[dockable title=" + dockable.getTitleText() + "]";
	}
}
