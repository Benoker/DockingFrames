/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.gui.dock.themes.color;

import java.awt.Color;
import java.util.List;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.themes.ColorBridgeFactory;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.extension.ExtensionName;

/**
 * A {@link ColorScheme} that can be extended by additional {@link ColorScheme}.
 * @author Benjamin Sigg
 */
public class ExtendingColorScheme implements ColorScheme{
	private ColorScheme scheme;
	private ColorScheme[] extensions;
	
	/**
	 * Creates a new scheme.
	 * @param scheme the basic settings
	 * @param controller the controller used to read additional schemes
	 */
	public ExtendingColorScheme( ColorScheme scheme, DockController controller ){
		ExtensionName<ColorScheme> name = new ExtensionName<ColorScheme>(
	    		ColorScheme.EXTENSION_NAME, ColorScheme.class, 
	    		ColorScheme.COLOR_SCHEME_PARAMETER, scheme );
	    List<ColorScheme> extensions = controller.getExtensions().load( name );
	    
	    this.scheme = scheme;
	    this.extensions = extensions.toArray( new ColorScheme[ extensions.size() ] );
	    
	    for( ColorScheme extension : extensions ){
	    	extension.updateUI();
	    }
	}
	
	public ColorBridgeFactory getBridgeFactory( Path kind ){
		for( int i = extensions.length-1; i >= 0; i-- ){
			ColorBridgeFactory result = extensions[i].getBridgeFactory( kind );
			if( result != null ){
				return result;
			}
		}
		return scheme.getBridgeFactory( kind );
	}
	
	public Color getColor( String id ){
		for( int i = extensions.length-1; i >= 0; i-- ){
			Color result = extensions[i].getColor( id );
			if( result != null ){
				return result;
			}
		}
		return scheme.getColor( id );
	}
	
	public void transmitAll( Priority priority, ColorManager manager ){
		try{
			manager.lockUpdate();
			
			scheme.transmitAll( priority, manager );
			for( ColorScheme extension : extensions ){
				extension.transmitAll( priority, manager );
			}
		}
		finally{
			manager.unlockUpdate();
		}
	}
	
	public boolean updateUI(){
		boolean result = false;
		for( ColorScheme extension : extensions ){
			boolean next = extension.updateUI();
			result |= next;
		}
		boolean next = scheme.updateUI();
		result |= next;
		return result;
	}
}
