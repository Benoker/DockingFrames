/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2018 Benjamin Sigg
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
package bibliothek.util.workarounds;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Shape;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Workarounds necessary for Java 9
 * @author Zilvinas
 */
public class Java9Workaround extends Java7Workaround {

	public void markAsGlassPane( Component component ) {
		try {
			Method setMixingCutoutShapeMethod = Component.class.getMethod( "setMixingCutoutShape", Shape.class );
			setMixingCutoutShapeMethod.invoke( component, new Rectangle() );
		} catch( NoSuchMethodException ex ) {
			System.out.println( ex.getMessage() );
		} catch( SecurityException ex ) {
			System.out.println( ex.getMessage() );
		} catch( IllegalAccessException ex ) {
			System.out.println( ex.getMessage() );
		} catch( IllegalArgumentException ex ) {
			System.out.println( ex.getMessage() );
		} catch( InvocationTargetException ex ) {
			System.out.println( ex.getMessage() );
		}
	}
}