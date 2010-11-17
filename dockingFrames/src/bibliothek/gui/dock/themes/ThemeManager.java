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
package bibliothek.gui.dock.themes;

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.UIBridge;
import bibliothek.gui.dock.util.UIProperties;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * The {@link ThemeManager} is responsible for collecting properties of the current {@link DockTheme} and redistribute them. The
 * {@link ThemeManager} provides facilities for clients to modify and override properties of a theme without the need
 * to access or change the {@link DockTheme} itself.<br>
 * The API of this manager is equivalent to {@link UIProperties} with the only distinction that this manager supports type safety. 
 * @author Benjamin Sigg
 */
public class ThemeManager {
	private UIProperties<Object, UIValue<Object>, UIBridge<Object, UIValue<Object>>> properties;
	
	private Map<Class<?>, String> classIdentifiers = new HashMap<Class<?>, String>();
	
	/**
	 * Adds the listener <code>value</code> to this manager.
	 * @param <V> the type of object <code>value</code> supports.
	 * @param id the unique identifier of the property to observe
	 * @param kind what kind of object <code>value</code> actually is
	 * @param type <code>V</code> in a form that can be tested by this manager
	 * @param value the new observer
	 * @throws IllegalArgumentException if <code>type</code> is not known to this manager or if either
	 * of the arguments is <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public <V> void add( String id, Path kind, Class<V> type, UIValue<V> value ){
		String clazz = classIdentifiers.get( type );
		if( clazz == null ){
			throw new IllegalArgumentException( "type '" + type + "' is not known to this manager" );
		}
		properties.add( clazz + "." + id, kind, (UIValue<Object>)value );
	}
	
	/**
	 * Removes the observer <code>value</code> from this manager.
	 * @param value the observer to remove
	 */
	@SuppressWarnings("unchecked")
	public void remove( UIValue<?> value ){
		properties.remove( (UIValue<Object>)value );
	}
	
	@SuppressWarnings("unchecked")
	public <V> void publish( Priority priority, Path kind, Class<V> type, UIBridge<V, UIValue<V>> bridge ){
		String clazz = classIdentifiers.get( type );
		if( clazz == null ){
			throw new IllegalArgumentException( "type '" + type + "' is not known to this manager" );
		}
		kind = new Path( clazz ).append( kind );
		properties.publish( priority, kind, (UIBridge)bridge );
	}
	
	public <V> void unpublish( Path kind, Class<V> type ){
		
	}
	
	public <V> void unpublish( UIBridge<V, UIValue<V>> bridge ){
		
	}
}
