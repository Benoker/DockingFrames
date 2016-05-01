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

package bibliothek.extension.gui.dock.theme.flat;

import java.awt.Rectangle;

import javax.swing.JTabbedPane;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.CombinedStackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentParent;

/**
 * A panel that works like a {@link JTabbedPane}, but the buttons to
 * change between the children are smaller and "flatter" than the
 * buttons of the <code>JTabbedPane</code>.
 * @author Benjamin Sigg
 */
public class FlatTabPane extends CombinedStackDockComponent<FlatTab, FlatMenu, FlatInfoComponent>{
    /** the station which uses this component */
    private StackDockComponentParent station;
    
    /**
     * Creates a new {@link FlatTabPane}
     * @param parent the station which uses this component
     */
    public FlatTabPane( StackDockComponentParent parent ){
        this.station = parent;
        setInfoComponent( new FlatInfoComponent( this ) );
    }
    
    @Override
	protected FlatTab newTab( Dockable dockable ){
		return new FlatTab( this, dockable );
	}
	
    @Override
    protected void tabRemoved( FlatTab tab ){
    	tab.setController( null );
    }
    
    @Override
    public FlatMenu newMenu(){
    	FlatMenu menu = new FlatMenu( this );
    	menu.setController( getController() );
    	return menu;
    }
    
    @Override
    protected void menuRemoved( FlatMenu menu ){
	    menu.setController( null );	
    }
    
	@Override
    public void setController( DockController controller ){
		super.setController( controller );
		for( FlatTab tab : getTabsList() ){
			tab.setController( controller );
		}
		for( FlatMenu menu : getMenuList() ){
			menu.setController( controller );
		}
	}
	
	@Override
	public void setSelectedBounds( Rectangle bounds ){
		super.setSelectedBounds( bounds );
	    for( FlatTab tab : getTabsList() ){
            tab.updateForeground();
            tab.updateFonts();
        }
	}
	
	public boolean hasBorder() {
	    return false;
	}
	
	public boolean isSingleTabComponent(){
		return false;
	}
	
	public DockStation getStation(){
		return station.getStackDockParent();
	}
}
