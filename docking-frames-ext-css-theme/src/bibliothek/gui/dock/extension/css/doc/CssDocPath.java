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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes a path to a documented property, or just a path that can be used for documentation.
 * @author Benjamin Sigg
 */
@Documented
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CssDocPath {
	/**
	 * Unique identifier denoting this path, the unique identifier must only be unique within the
	 * scope of the class surrounding this {@link CssDocPath}.
	 * @return the unique identifier, may be <code>"null"</code>
	 */
	public String id() default "";
	
	/**
	 * Unique identifier of the parent {@link CssDocPath}. If {@link #parentClass()} is defined, then
	 * this identifier must point to a path from that other class, otherwise it must point to a path
	 * that is defined in the same class as this annotation.<br>
	 * Note that cyclic dependencies are not allowed.
	 * @return the identifier of the parent path, can be <code>"null"</code>
	 */
	public String parentId() default "";
	
	/**
	 * The class in which the annotation of a parent {@link CssDocPath} is defined, requires {@link #parentId()}
	 * to be set as well.
	 * @return the class containing the parent path, or <code>"null"</code>
	 */
	public Class<?> parentClass() default Object.class;

	/**
	 * Unique identifier of a path which should replace <code>this</code>.
	 * @return the identifier of the replacing path.
	 */
	public String referenceId() default "";
	
	/**
	 * The class in which the replacing path is defined.
	 * @return the class, a value of <code>null</code> is replaced by the class in which this annotation was found.
	 * @see #referenceId()
	 */
	public Class<?> referencePath() default Object.class;
	
	/**
	 * A human readable description of this path.
	 * @return the description, can be <code>"null"</code>
	 */
	public CssDocText description() default @CssDocText();
	
	/**
	 * Describes the different nodes this path consists of. Callers can assume that the nodes
	 * will always be ordered exactly like in the returned array.
	 * @return the nodes, may be <code>null</code> or empty
	 */
	public CssDocPathNode[] nodes() default {};
	
	/**
	 * Describes the different nodes this path consists of. Callers can not make any assumption
	 * of the order, presence or duplication of the nodes in the returned array.
	 * @return the nodes, may be <code>null</code> or empty
	 */
	public CssDocPathNode[] unordered() default {};
}
