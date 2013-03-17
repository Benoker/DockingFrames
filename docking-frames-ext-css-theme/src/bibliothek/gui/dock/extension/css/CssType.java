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

import java.awt.Color;

import bibliothek.gui.dock.extension.css.transition.TransitionalCssProperty;

/**
 * A {@link CssType} describes the type of the value part of a statement like "tab.shape = value". A type represents
 * some kind of {@link Object}, like a {@link Color}, and offers methods to convert the text from a css file to that
 * specific type of object. The type also offers the default transition algorithm for converting one <code>T</code> 
 * into another <code>T</code>.
 * 
 * @author Benjamin Sigg
 *
 * @param <T> the type of the value
 */
public interface CssType<T> {
	/**
	 * Converts some text <code>value</code> that was found in a css file to a <code>T</code>. Please note that 
	 * <code>null</code> always means that <code>value</code> is invalid, a text that represents <code>null</code> is
	 * not even sent to this method, because <code>null</code> can be cast to any object.
	 * @param value the text to convert
	 * @return the actual object <code>value</code> represents, or <code>null</code> if <code>value</code>
	 * cannot be converted
	 */
	public T convert( CssDeclarationValue value );
	
	/**
	 * Creates the default transition used to merge two objects of type <code>T</code> together.
	 * @return the new default transition, can be <code>null</code>
	 */
	public TransitionalCssProperty<T> createTransition();
}
