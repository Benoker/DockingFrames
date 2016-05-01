/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.station.support;

import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;

/**
 * 
 * @author Benjamin Sigg
 * @param <D> the kind of item this converter is used for
 */
public class PerspectivePlaceholderListItemConverter<D extends PlaceholderListItem<PerspectiveDockable>> extends PlaceholderListItemAdapter<PerspectiveDockable, D> {
	@Override
	public ConvertedPlaceholderListItem convert( int index, D dockable ){
		if( dockable.asDockable() != null ){
			ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
			item.setPlaceholder( dockable.asDockable().getPlaceholder() );
			PerspectiveStation station = dockable.asDockable().asStation();
			if( station != null ){
				item.setPlaceholderMap( station.getPlaceholders() );
			}
			return item;
		}
		return null;
	}
}
