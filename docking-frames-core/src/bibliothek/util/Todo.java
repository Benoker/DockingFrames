/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Retention;

/**
 * Used to mark elements that need to be modified in a future release.
 * @author Benjamin Sigg
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Todo {
	/** Tells how important an {@link Todo} is */
	public static enum Priority{
		/** The highest priority, bugs are to be repaired as soon as possible */
		BUG,
		/** An important enhancement that will ease the use of the framework considerably, also an enhancement that will require some time to complete */
		MAJOR,
		/** An enhancement of lesser importance, but will not require much time to implement */
		MINOR,
		/** An enhancement with zero importance */
		ENHANCEMENT
	}
	
	/** Tells whether a {@link Todo} is backwards compatible */
	public static enum Compatibility{
		/** The modification affects only a method or class, no-one will notice the change */
		COMPATIBLE, 
		/** Some internal classes are affected, clients should not notice the change */
		BREAK_MINOR, 
		/** Clients will need to be updated as well */
		BREAK_MAJOR
	}
	
	/** Tells when a {@link Todo} is scheduled to be implemented */
	public static enum Version{
		VERSION_1_1_0,
		VERSION_1_1_1,
		VERSION_1_1_2,
		VERSION_1_1_3;
	}
	
	public Priority priority() default Priority.MAJOR;
	public Compatibility compatibility() default Compatibility.BREAK_MINOR;
	public Version target() default Version.VERSION_1_1_0;
	public String description() default "to be implemented";
}
