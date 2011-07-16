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
package bibliothek.gui.dock.util;

import java.util.Collection;
import java.util.Set;

import bibliothek.util.Path;

/**
 * An event fired by an {@link UIScheme} after some content changed.
 * @author Benjamin Sigg
 */
public interface UISchemeEvent<V, U extends UIValue<V>, B extends UIBridge<V, U>> {
	/**
	 * Gets the scheme which fired this event.
	 * @return the source of the event
	 */
	public UIScheme<V,U,B> getScheme();
	
	/**
	 * Checks which resources have changed, assuming that <code>names</code> are the
	 * only identifiers that are used.
	 * @param names a set of identifiers to check, <code>null</code> indicates that every possible identifier
	 * must be included in the search
	 * @return the identifiers of resources that have actually been changed, should be a subset of <code>names</code>. A
	 * value of <code>null</code> indicates that either all resources changed, or that this method cannot exactly
	 * tell what effect the event had 
	 */
	public Collection<String> changedResources( Set<String> names );
	
	/**
	 * Checks which bridges have changed, assuming that <code>names</code> are the
	 * only identifiers that are used.
	 * @param names a set of identifiers to check, <code>null</code> indicates that every possible identifier
	 * must be included in the search
	 * @return the identifiers of bridges that have actually been changed, should be a subset of <code>names</code>. A
	 * value of <code>null</code> indicates that either all bridges changed, or that this method cannot exactly
	 * tell what effect the event had 
	 */
	public Collection<Path> changedBridges( Set<Path> names );
}
