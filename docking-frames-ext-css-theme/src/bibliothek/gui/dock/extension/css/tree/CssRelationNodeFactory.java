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
package bibliothek.gui.dock.extension.css.tree;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.extension.css.CssNode;

/**
 * A factory for creating {@link CssNode}s describing the relation
 * between a {@link Dockable} and its parent {@link DockStation}.
 * @author Benjamin Sigg
 *
 * @param <S> the type of station this factory manages
 */
public interface CssRelationNodeFactory<S extends DockStation> {
	/**
	 * Creates a node describing the relation between <code>parent</code> 
	 * and <code>child</code>.
	 * @param parent the parent {@link DockStation}
	 * @param child the child {@link Dockable}
	 * @return the relation or <code>null</code>
	 */
	public CssNode createRelation( S parent, Dockable child );
}
