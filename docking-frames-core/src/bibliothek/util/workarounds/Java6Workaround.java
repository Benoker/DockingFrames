/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Workarounds necessary for Java 1.6.
 * @author Benjamin Sigg
 */
public class Java6Workaround implements Workaround{
	private boolean invocationTargetException = false;
	
	public void markAsGlassPane( Component component ){
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
	
	public boolean makeTransparent( Window window ){
		try{
			Class<?> awtUtilities = Class.forName( "com.sun.awt.AWTUtilities" );
			Method setWindowOpaque = awtUtilities.getMethod( "setWindowOpaque", Window.class, boolean.class );
			setWindowOpaque.invoke( null, window, false );
			return true;
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
			if( !invocationTargetException ){
				invocationTargetException = true;
				ex.printStackTrace();
			}
		}
		catch( IllegalArgumentException e ){
			// ignore
		}
		catch( IllegalAccessException e ){
			// ignore
		}
		catch( Exception e ){
			e.printStackTrace();
		}
		return false;
	}
}
