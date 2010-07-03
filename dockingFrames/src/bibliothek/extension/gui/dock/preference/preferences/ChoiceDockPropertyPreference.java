/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.extension.gui.dock.preference.preferences;

import bibliothek.extension.gui.dock.preference.DefaultPreference;
import bibliothek.extension.gui.dock.preference.preferences.choice.DefaultChoice;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.util.Path;

/**
 * A preference that offers a choice and write the value into a {@link DockProperties}.
 * @author Benjamin Sigg
 *
 * @param <V> the kind of value to store into the properties
 */
public class ChoiceDockPropertyPreference<V> extends DefaultPreference<String>{
	private DockProperties properties;
	private PropertyKey<V> key;
	private DefaultChoice<V> choice;
	
	/**
	 * Creates a new preference.
	 * @param properties the properties to read and write from
	 * @param key the key for the property that gets accessed 
	 * @param path the unique identifier of this preference
	 * @param choice the available choices
	 */
	public ChoiceDockPropertyPreference( DockProperties properties, PropertyKey<V> key, Path path, DefaultChoice<V> choice ){
		super( Path.TYPE_STRING_CHOICE_PATH, path );
		this.properties = properties;
		this.choice = choice;
		this.key = key;
		
		setValueInfo( choice );
	}
	
	public void write() {
		properties.setOrRemove( key, choice.identifierToValue( getValue() ), Priority.CLIENT );
	}
	
	public void read() {
		V property = properties.get( key, Priority.CLIENT );
		if( property == null ){
			setValue( choice.getDefaultChoice() );
		}
		else{
			String value = choice.valueToIdentifier( property );
			if( value != null || choice.isNullEntryAllowed() ){
				setValue( value );
			}
		}
	}
}
