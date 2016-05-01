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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.util.UIProperties;
import bibliothek.gui.dock.util.UIScheme;
import bibliothek.gui.dock.util.UISchemeEvent;
import bibliothek.gui.dock.util.UISchemeListener;
import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.util.Path;

/**
 * A {@link ColorScheme} that can be extended by additional {@link ColorScheme}.
 * @author Benjamin Sigg
 */
public class ExtendingColorScheme extends AbstractColorScheme{
	private ColorScheme scheme;
	private ColorScheme[] extensions;
	
	private UISchemeListener<Color, DockColor, ColorBridge> delegateListener = new UISchemeListener<Color, DockColor, ColorBridge>(){
		public void changed( final UISchemeEvent<Color, DockColor, ColorBridge> event ){
			UISchemeEvent<Color, DockColor, ColorBridge> forward = new UISchemeEvent<Color, DockColor, ColorBridge>(){
				public UIScheme<Color, DockColor, ColorBridge> getScheme(){
					return ExtendingColorScheme.this;
				}
				
				public Collection<String> changedResources( Set<String> names ){
					return event.changedResources( names );
				}
				
				public Collection<Path> changedBridges( Set<Path> names ){
					return event.changedBridges( names );
				}
			};
			
			fire( forward );
		}
	};
	
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
	}
	
	@Override
	public void addListener( UISchemeListener<Color, DockColor, ColorBridge> listener ){
		boolean has = hasListeners();
		super.addListener( listener );
		if( !has ){
			scheme.addListener( delegateListener );
			for( ColorScheme extension : extensions ){
				extension.addListener( delegateListener );
			}
		}
	}
	
	@Override
	public void removeListener( UISchemeListener<Color, DockColor, ColorBridge> listener ){
		super.removeListener( listener );
		if( !hasListeners() ){
			scheme.removeListener( delegateListener );
			for( ColorScheme extension : extensions ){
				extension.removeListener( delegateListener );
			}
		}
	}
	
	@Override
	public void install( UIProperties<Color, DockColor, ColorBridge> properties ){
		super.install( properties );
		scheme.install( properties );
		for( ColorScheme extension : extensions ){
			extension.install( properties );
		}
	}
	
	@Override
	public void uninstall( UIProperties<Color, DockColor, ColorBridge> properties ){
		super.uninstall( properties );
		scheme.uninstall( properties );
		for( ColorScheme extension : extensions ){
			extension.uninstall( properties );
		}
	}
	
	@Override
	protected void updateUI(){
		// ignore
	}
	
	public ColorBridge getBridge( Path name, UIProperties<Color, DockColor, ColorBridge> properties ){
		for( int i = extensions.length-1; i >= 0; i-- ){
			ColorBridge result = extensions[i].getBridge( name, properties );
			if( result != null ){
				return result;
			}
		}
		return scheme.getBridge( name, properties );
	}
	
	public Color getResource( String name, UIProperties<Color, DockColor, ColorBridge> properties ){
		for( int i = extensions.length-1; i >= 0; i-- ){
			Color result = extensions[i].getResource( name, properties );
			if( result != null ){
				return result;
			}
		}
		return scheme.getResource( name, properties );
	}
}
