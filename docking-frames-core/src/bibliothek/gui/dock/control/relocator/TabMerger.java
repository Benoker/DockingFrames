/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.control.relocator;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.themes.basic.TabDisplayerCombinerTarget;

/**
 * This {@link Merger} merges {@link StackDockStation}s that are dropped onto a single 
 * tabbed {@link Dockable}.
 * @author Benjamin Sigg
 */
public class TabMerger implements Merger{
	public boolean canMerge( StationDropOperation operation, DockStation parent, DockStation child ){
		if( operation == null ){
			return false;
		}
		
		DisplayerCombinerTarget target = operation.getDisplayerCombination();
		if( target instanceof TabDisplayerCombinerTarget ){
			TabDisplayerCombinerTarget tab = (TabDisplayerCombinerTarget)target;
			
			if( !tab.isValid() ){
				return false;
			}
			if( !(operation.getItem() instanceof StackDockStation)){
				return false;
			}
			
			Dockable item = operation.getItem();
			if( !parent.accept( item ) || !item.accept( parent )){
				return false;
			}
			
			DockController controller = parent.getController();
			if( controller != null ){
				if( !controller.getAcceptance().accept( parent, item )){
					return false;
				}
			}
			
			if( !parent.canReplace( tab.getTarget(), operation.getItem() )){
				return false;
			}
			
			if( !((StackDockStation)item).acceptable( tab.getTarget() )){
				return false;
			}
			
			return true;
		}
		return false;
	}

	public void merge( StationDropOperation operation, DockStation parent, DockStation child ){
		DisplayerCombinerTarget target = operation.getDisplayerCombination();
		if( target instanceof TabDisplayerCombinerTarget ){
			TabDisplayerCombinerTarget tab = (TabDisplayerCombinerTarget)target;
			StackDockStation station = (StackDockStation)operation.getItem();
			if( station.getDockParent() != null ){
				station.getDockParent().drag( station );
			}
			
			Dockable dockable = tab.getTarget();
			parent.replace( dockable, station );
			
			Dockable selected = station.getFrontDockable();
			
			if( tab.getIndex() == 0 ){
				station.add( dockable, station.getDockableCount() );
			}
			else{
				station.add( dockable, 0 );
			}
			
			DockController controller = station.getController();
			if( controller != null ){
				controller.setFocusedDockable( selected, false );
			}
		}
	}
}
