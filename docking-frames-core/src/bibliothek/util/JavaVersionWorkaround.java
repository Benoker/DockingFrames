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

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Shape;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class providing help for bugs or specialities present in some
 * versions of the JRE.
 * @author Benjamin Sigg
 */
public class JavaVersionWorkaround {
	/**
	 * This method is necessary since 1.6.14, it marks a component as
	 * transparent. If not marked then AWT components behind <code>component</code>
	 * are not visible. 
	 * @param component the component to mark completely transparent
	 */
	public static void markAsGlassPane( Component component ){
		try{
			Class<?> clazz = Class.forName( "com.sun.awt.AWTUtilities" );
			Method method = clazz.getMethod( "setComponentMixingCutoutShape", new Class[]{ Component.class, Shape.class } );
			method.invoke( null, component, new Rectangle() );
		}
		catch( ClassNotFoundException ex ){
			// ignore
		}
		catch( NoSuchMethodException ex ){
			// ignore
		}
		catch( SecurityException ex ){
			// ignore
		}
		catch( InvocationTargetException ex ){
			// ignore
		}
		catch( IllegalArgumentException e ){
			// ignore
		}
		catch( IllegalAccessException e ){
			// ignore
		}
	}
}
