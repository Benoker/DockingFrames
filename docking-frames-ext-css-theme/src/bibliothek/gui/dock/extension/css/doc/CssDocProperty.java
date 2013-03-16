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
package bibliothek.gui.dock.extension.css.doc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import bibliothek.gui.dock.extension.css.CssProperty;

/**
 * This annotation documents a css property, it may depend on some class, method or field.
 * @author Benjamin Sigg
 */
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CssDocProperty {
	/**
	 * The type of the property that is documented, should be a {@link CssProperty}. If the type is not given,
	 * then the documentation tool may make a best guess, by analyzing the annotated field, method or class.
	 * @return the type of the property that is required
	 */
	public Class<?> type() default Object.class;
	
	/**
	 * The path that will be used to access the documented property.
	 * @return the path, should not be <code>null</code>
	 */
	public CssDocPath path() default @CssDocPath();
	
	/**
	 * The name of the property itself.
	 * @return the name of the property.
	 */
	public CssDocKey property();
	
	/**
	 * Further description of the property, can be any kind of text.
	 * @return further description, <code>null</code> is valid and just means "no description available".
	 */
	public CssDocText description() default @CssDocText();
}
