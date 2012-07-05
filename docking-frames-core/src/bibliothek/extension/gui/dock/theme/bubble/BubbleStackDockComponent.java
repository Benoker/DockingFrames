/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

package bibliothek.extension.gui.dock.theme.bubble;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.CombinedStackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentParent;

/**
 * A {@link bibliothek.gui.dock.station.stack.StackDockComponent StackDockComponent} 
 * used by a {@link BubbleTheme}. This component can animate its tabs.
 * @author Benjamin Sigg
 */
public class BubbleStackDockComponent extends CombinedStackDockComponent<BubbleTab, BubbleTabMenu, BubbleInfoComponent> {

	/** the station for which this component is used */
	private StackDockComponentParent station;
	
	/**
	 * Creates a new component.
	 * @param parent the station on which this component is used
	 */
	public BubbleStackDockComponent( StackDockComponentParent parent ){
		this.station = parent;
		setInfoComponent( new BubbleInfoComponent( this ) );
	}
	
	/**
	 * Gets the station on which this component is used.
	 * @return the parent of this component
	 */
	public DockStation getStation(){
		return station.getStackDockParent();
	}
	
	@Override
	protected BubbleTab newTab( Dockable dockable ){
		BubbleTab tab = new BubbleTab( this, dockable );
		addStackDockComponentListener( tab );
		return tab;
	}
	
	@Override
	protected void tabRemoved( BubbleTab tab ){
		removeStackDockComponentListener( tab );
		tab.setController( null );
        tab.stopAnimation();
	}
	
	@Override
	public BubbleTabMenu newMenu(){
		BubbleTabMenu menu = new BubbleTabMenu( this );
		menu.setController( getController() );
		return menu;
	}
	
	@Override
	protected void menuRemoved( BubbleTabMenu menu ){
		menu.setController( null );
	}
	
	@Override
	public void setController( DockController controller ){
		super.setController( controller );
		
		for( BubbleTab tab : getTabsList() ){
			tab.setController( controller );
		}
		for( BubbleTabMenu menu : getMenuList() ){
			menu.setController( controller );
		}
	}

	public boolean hasBorder() {
	    return true;
	}
	
	public boolean isSingleTabComponent(){
		return false;
	}
}
