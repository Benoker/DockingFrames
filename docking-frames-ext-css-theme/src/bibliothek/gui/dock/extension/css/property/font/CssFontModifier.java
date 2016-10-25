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
package bibliothek.gui.dock.extension.css.property.font;

import java.awt.Font;

import bibliothek.gui.dock.extension.css.CssPropertyContainer;
import bibliothek.gui.dock.util.font.FontModifier;

/**
 * A {@link CssFontModifier} is an algorithm that takes a default {@link Font}, and
 * converts it into a new font.
 * @author Benjamin Sigg
 */
public interface CssFontModifier extends CssPropertyContainer{
	/**
	 * Gets the actual modifier. The object returned by this method is immutable.
	 * @return the actual modifier
	 */
	public FontModifier getModifier();
	
	/**
	 * Adds <code>listener</code> as observer to this property.
	 * @param listener the new observer
	 */
	public void addFontModifierListener( CssFontModifierListener listener );

	/**
	 * Removes <code>listener</code> as observer from this property.
	 * @param listener the observer to remove
	 */
	public void removeFontModifierListener( CssFontModifierListener listener );
}
