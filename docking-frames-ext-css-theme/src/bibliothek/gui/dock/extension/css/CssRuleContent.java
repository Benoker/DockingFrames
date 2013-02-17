/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css;

/**
 * A set of properties that may be associated with one or several {@link CssRule}s.
 * @author Benjamin Sigg
 */
public interface CssRuleContent {
	/**
	 * Gets one of the properties of this rule.
	 * @param type the type which the property is supposed to have 
	 * @param property the name of the property to read
	 * @return the property or <code>null</code> if not present
	 * @throws IllegalArgumentException if the property cannot be understood as <code>type</code>
	 */
	public <T> T getProperty( CssType<T> type, CssPropertyKey property );
	
	/**
	 * Adds the observer <code>listener</code> to this object.
	 * @param listener the new observer, not <code>null</code>
	 */
	public void addRuleContentListener( CssRuleContentListener listener );
	
	/**
	 * Removes the observer <code>listener</code> from this object.
	 * @param listener the listener to remove
	 */
	public void removeRuleContentListener( CssRuleContentListener listener );
}
