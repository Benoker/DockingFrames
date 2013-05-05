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

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.Window;
import java.lang.reflect.Method;

/**
 * Workarounds necessary for Java 1.7.
 * @author Benjamin Sigg
 */
public class Java7Workaround extends Java6Workaround{
	
	private boolean supports( String translucencyName ){
		try{
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();
	
			Class<?> windowTransulcency = Class.forName( "java.awt.GraphicsDevice$WindowTranslucency" );
			Method isWindowTranslucencySupported = GraphicsDevice.class.getMethod( "isWindowTranslucencySupported", windowTransulcency );
			return (Boolean)isWindowTranslucencySupported.invoke( gd, windowTransulcency.getField( translucencyName ).get( null ) );
		}
		catch( Exception e ){
			e.printStackTrace();
			return false;
		}	
	}
	
	@Override
	public boolean supportsPerpixelTransparency( Window window ){
		return supports( "PERPIXEL_TRANSPARENT" );
	}
	
	@Override
	public boolean supportsPerpixelTranslucency( Window window ){
		return supports( "PERPIXEL_TRANSLUCENT" );
	}
	
	@Override
	public boolean setTransparent( Window window, Shape shape ){
		if( !supportsPerpixelTransparency( window )){
			return false;
		}
		
		try{
			Method setShape = Window.class.getMethod( "setShape", Shape.class );
			setShape.invoke( window, shape );
			return true;
		}
		catch( Exception e ){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean setTranslucent( Window window ){
		if( !supportsPerpixelTranslucency( window )){
			return false;
		}
		
		try{
			window.setBackground( new Color(0,0,0,0) );
			return true;
		}
		catch( Exception e ){
			e.printStackTrace();
		}
		return false;
	}
}
