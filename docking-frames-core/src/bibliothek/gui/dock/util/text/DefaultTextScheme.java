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
package bibliothek.gui.dock.util.text;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import bibliothek.gui.dock.util.AbstractUIScheme;
import bibliothek.gui.dock.util.TextManager;
import bibliothek.gui.dock.util.UIProperties;
import bibliothek.gui.dock.util.UIScheme;
import bibliothek.util.Path;

/**
 * This {@link UIScheme} is used by the {@link TextManager} to load the default set of strings from
 * one or several {@link ResourceBundle}s.
 * @author Benjamin Sigg
 */
public class DefaultTextScheme extends AbstractUIScheme<String, TextValue, TextBridge>{
	/** the bundles to contact for reading a resource */
	private ResourceBundle[] bundles;
	
	/**
	 * Creates a new scheme reading content from <code>bundles</code>
	 * @param bundles the bundles to read, must not contain a <code>null</code> value
	 */
	public DefaultTextScheme( ResourceBundle... bundles ){
		for( int i = 0; i < bundles.length; i++ ){
			if( bundles[i] == null ){
				throw new IllegalArgumentException( "bundle '" + i + "' is null" );
			}
		}
		this.bundles = bundles;
	}
	
	public TextBridge getBridge( Path name, UIProperties<String, TextValue, TextBridge> properties ){
		return null;
	}

	public String getResource( String name, UIProperties<String, TextValue, TextBridge> properties ){
		for( ResourceBundle bundle : bundles ){
			try{
				return bundle.getString( name );
			}
			catch( MissingResourceException e ){
				// ignore
			}
		}
		return null;
	}

	public void install( UIProperties<String, TextValue, TextBridge> properties ){
		// ignore
	}

	public void uninstall( UIProperties<String, TextValue, TextBridge> properties ){
		// ignore
	}
}
