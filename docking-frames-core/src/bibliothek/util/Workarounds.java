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

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import bibliothek.util.workarounds.Java6Workaround;
import bibliothek.util.workarounds.Java7Workaround;
import bibliothek.util.workarounds.Workaround;

/**
 * Utility class providing help for bugs or specialities present in some versions of the JRE or
 * in some libraries. {@link Workarounds} is implemented as singleton, as there is only one JRE and
 * usually libraries are not loaded multiple times as well.
 * @author Benjamin Sigg
 */
public class Workarounds {
	private static Workarounds workarounds = new Workarounds();
	
	/** all listeners */
	private List<Workaround> code = new ArrayList<Workaround>();
	
	/**
	 * Gets access to the currently used {@link Workarounds}s.
	 * @return the current workarounds
	 */
	public static Workarounds getDefault(){
		return workarounds;
	}
	
	static{
		String version = System.getProperty( "java.version" );
		if( version.startsWith( "1.6" )){
			getDefault().addWorkaround( new Java6Workaround() );
		}
		else{
			getDefault().addWorkaround( new Java7Workaround() );
		}
	}
	
	/**
	 * Seets the {@link Workarounds} that should be used. This method will never be called from
	 * the framework itself. Calling this method has no effect on workarounds that are already 
	 * applied.<br>
	 * Please note that this method is not thread safe!<br>
	 * @param workarounds the new workarounds, not <code>null</code>
	 */
	@ClientOnly
	public static void setDefault( Workarounds workarounds ){
		if( workarounds == null ){
			throw new IllegalArgumentException( "workarounds must not be null" );
		}
		Workarounds.workarounds = workarounds;
	}
	
	/**
	 * Adds a new workaround to this {@link Workarounds}.
	 * @param workaround the new workaround, not <code>null</code>
	 */
	public void addWorkaround( Workaround workaround ){
		code.add( workaround );
	}
	
	/**
	 * Removes a workaround from this {@link Workarounds}.
	 * @param workaround the workaround to remove
	 */
	public void removeWorkaround( Workaround workaround ){
		code.remove( workaround );
	}
	
	/**
	 * Gets all the {@link Workaround}s that are currently active.
	 * @return all the workarounds
	 */
	public Workaround[] getWorkarounds(){
		return code.toArray( new Workaround[ code.size() ] );
	}
	
	/**
	 * This method is necessary since 1.6.14, it marks a component as
	 * transparent. If not marked then AWT components behind <code>component</code>
	 * are not visible. 
	 * @param component the component to mark completely transparent
	 */
	public void markAsGlassPane( Component component ){
		for( Workaround listener : code.toArray( new Workaround[ code.size() ] )){
			listener.markAsGlassPane( component );
		}
	}
	
	/**
	 * Makes <code>window</code> transparent, meaning that the opacity of each pixel is defined by the
	 * alpha value or the {@link Color} that was used to paint over that pixel.
	 * @param window the window that should be transparent
	 * @return <code>true</code> if the winodw is now transparent
	 */
	public boolean makeTransparent( Window window ){
		boolean result = false;
		
		for( Workaround listener : code.toArray( new Workaround[ code.size() ] )){
			result = listener.makeTransparent( window ) || result;
		}
		
		return result;
	}
}
