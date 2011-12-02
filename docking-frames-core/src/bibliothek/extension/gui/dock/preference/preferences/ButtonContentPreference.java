
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
import bibliothek.extension.gui.dock.preference.model.ButtonContentPreferenceModel;
import bibliothek.extension.gui.dock.preference.preferences.choice.ButtonContentChoice;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.Priority;
import bibliothek.util.Path;

/**
 * A preference for {@link FlapDockStation#BUTTON_CONTENT}. Only a few selected settings are available through
 * this preference.
 * @author Benjamin Sigg
 * @deprecated replaced by {@link ButtonContentPreferenceModel}
 */
@Deprecated
public class ButtonContentPreference extends DefaultPreference<String>{
	private DockProperties properties;
	private ButtonContentChoice choice;
	
	/**
	 * Creates a new preference.
	 * @param properties the properties to read or write from
	 * @param path the path of this property
	 */
	public ButtonContentPreference( DockProperties properties, Path path ){
		super( Path.TYPE_STRING_CHOICE_PATH, path );
		if( properties == null )
			throw new IllegalArgumentException( "properties must not be null" );
		
		choice = new ButtonContentChoice( properties );
		setValueInfo( choice );
		setLabelId( "preference.layout.ButtonContentPreference.text" );
		setDescriptionId( "preference.layout.ButtonContentPreference.description" );
		this.properties = properties;
	}
	
	public void read() {
		setValue( choice.valueToIdentifier( properties.get( FlapDockStation.BUTTON_CONTENT, Priority.CLIENT )));
	}
	
	public void write() {
		properties.setOrRemove( FlapDockStation.BUTTON_CONTENT, choice.identifierToValue( getValue() ), Priority.CLIENT );
	}
}
