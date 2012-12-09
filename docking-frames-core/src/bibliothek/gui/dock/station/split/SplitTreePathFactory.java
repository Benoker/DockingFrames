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
package bibliothek.gui.dock.station.split;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;

/**
 * A factory for creating a {@link SplitDockPathProperty} pointing to some node.<br>
 * Subclasses can override any method and create a new {@link SplitDockPathProperty} when appropriate, the factory
 * will fill out all the fields of that property. 
 * @author Benjamin Sigg
 */
public abstract class SplitTreePathFactory implements SplitTreeFactory<SplitDockPathProperty>{
	public SplitDockPathProperty leaf( Dockable check, long id, Path[] placeholders, PlaceholderMap placeholderMap ){
		return null;
	}

	public SplitDockPathProperty placeholder( long id, Path[] placeholders, PlaceholderMap placeholderMap ){
		return null;
	}

	public SplitDockPathProperty root( SplitDockPathProperty root, long id ){
		return root;
	}

	public SplitDockPathProperty horizontal( SplitDockPathProperty left, SplitDockPathProperty right, double divider, long id, Path[] placeholders, PlaceholderMap placeholderMap, boolean visible ){
		if( left != null ) {
			if( visible ) {
				left.insert(SplitDockPathProperty.Location.LEFT, divider, 0, id);
			}
			return left;
		}
		if( right != null ) {
			if( visible ) {
				right.insert(SplitDockPathProperty.Location.RIGHT, 1 - divider, 0, id);
			}
			return right;
		}
		return null;
	}

	public SplitDockPathProperty vertical( SplitDockPathProperty top, SplitDockPathProperty bottom, double divider, long id, Path[] placeholders, PlaceholderMap placeholderMap, boolean visible ){
		if( top != null ) {
			if( visible ) {
				top.insert(SplitDockPathProperty.Location.TOP, divider, 0, id);
			}
			return top;
		}
		if( bottom != null ) {
			if( visible ) {
				bottom.insert(SplitDockPathProperty.Location.BOTTOM, 1 - divider, 0, id);
			}
			return bottom;
		}
		return null;
	}
}
