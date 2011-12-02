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
package bibliothek.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation marks classes, interfaces and other elements that should not be used
 * by clients. To be more specific:
 * <ul>
 * 	<li>A class marked with <code>FrameworkOnly</code> should not be instantiated or subclassed by a client</li>
 *  <li>An interface marked with <code>FrameworkOnly</code> should not be implemented by a client</li>
 *  <li>A method marked with <code>FrameworkOnly</code> should not be invoked by a client</li>
 *  <li>It is perfectly legitimate to use a subclass of a marked class/interface, the <code>FrameworkOnly</code> attribute is not inherited</li>
 *  <li>It is also perfectly legitimate to call methods of a class/interface that is marked as <code>FrameworkOnly</code></li>
 * </ul>
 * This annotation is only a hint: there may very well exist situations were a client needs to create or implement a <code>FrameworkOnly</code> element.
 * @author Benjamin Sigg
 * @see ClientOnly
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface FrameworkOnly {

}
