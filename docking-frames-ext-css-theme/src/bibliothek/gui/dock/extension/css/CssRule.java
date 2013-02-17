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
package bibliothek.gui.dock.extension.css;

/**
 * A {@link CssRule} is a set of properties and exactly one {@link CssSelector} that tells which
 * {@link CssItem}s are entitled to read these properties.
 * @author Benjamin Sigg
 */
public interface CssRule {
	/**
	 * The selector tells which {@link CssItem} should read properties from this rule.
	 * @return the selector, not <code>null</code>
	 */
	public CssSelector getSelector();
	
	/**
	 * Gets the set of properties. This method should always return the same object, and it
	 * should not return <code>null</code>.
	 * @return the set of properties, not <code>null</code>
	 */
	public CssRuleContent getContent();
	
	/**
	 * Adds <code>listener</code> to this rule, <code>listener</code> will be informed if
	 * a property or the selector of his rule changes.
	 * @param listener the listener to add, not <code>null</code>
	 */
	public void addRuleListener( CssRuleListener listener );
	
	/**
	 * Removes <code>listener</code> from this rule.
	 * @param listener the listener to remove
	 */
	public void removeRuleListener( CssRuleListener listener );
}
