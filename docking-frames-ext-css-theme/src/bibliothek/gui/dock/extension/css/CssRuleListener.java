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
 * This observer is added to a {@link CssRule}.
 * @author Benjamin Sigg
 */
public interface CssRuleListener {
	/**
	 * Called if {@link CssRule#getSelector()} changed.
	 * @param source the source of the event
	 */
	public void selectorChanged( CssRule source );
	
	/**
	 * Called if {@link CssRule#getProperty(String)} changed.
	 * @param source the source of the event
	 * @param key the name of the property that changed
	 */
	public void propertyChanged( CssRule source, CssPropertyKey key );
	
	/**
	 * Called if all {@link CssRule#getProperty(CssType, String) properties} changed.
	 * @param source the source of the event
	 */
	public void propertiesChanged( CssRule source );
}
