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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A text describing some element. A {@link CssDocText} can be converted into a {@link String} by using
 * this policy:<br>
 * <ul>
 * <li> If {@link #id()} is not <code>null</code>, then try to load a "format" from a translated text source.</li>
 * <li> Else if {@link #format()} is not <code>null</code>, load it as "format".</li>
 * <li> If "format" is loaded, use {@link #arguments()} and {@link String#format(String, Object...)} to convert it into
 * a human readable text.</li>
 * <li> Else use {@link #text()} as fallback, without applying {@link #arguments()} </li>
 * </ul>
 * @author Benjamin Sigg
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface CssDocText {
	/**
	 * A key that can be used to load text from some map (e.g. translated text) that is to be defined elsewhere.<br>
	 * The value read from the map is applied to {@link String#format(String, Object...)}, using {@link #arguments()}.
	 * @return key leading to text, can be <code>"null"</code>
	 */
	public String id() default "";
	
	/**
	 * Fallback formatting {@link String}, used to call {@link String#format(String, Object...)} with {@link #arguments()},
	 * in case that {@link #id()} is <code>null</code>
	 * @return the format or <code>"null"</code>
	 */
	public String format() default "";
	
	/**
	 * Arguments used to call {@link String#format(String, Object...)}.
	 * @return the arguments, can be empty
	 */
	public String[] arguments() default {};
	
	/**
	 * Fallback text, to be used if the other fields cannot be used to get a {@link String}. Will be shown
	 * unformatted.
	 * @return the fallback text, can be <code>"null"</code>
	 */
	public String text() default "";
}
