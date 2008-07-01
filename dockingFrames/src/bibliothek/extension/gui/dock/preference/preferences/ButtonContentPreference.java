
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
 */package bibliothek.extension.gui.dock.preference.preferences;

import bibliothek.extension.gui.dock.preference.DefaultPreference;
import bibliothek.extension.gui.dock.preference.preferences.choice.ButtonContentChoice;
import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.util.DockProperties;

/**
 * A preference for {@link FlapDockStation#BUTTON_CONTENT}.
 * @author Benjamin Sigg
 */
public class ButtonContentPreference extends DefaultPreference<String>{
	private DockProperties properties;
	
	/**
	 * Creates a new preference.
	 * @param properties the properties to read or write from
	 * @param path the path of this property
	 */
	public ButtonContentPreference( DockProperties properties, Path path ){
		super( Path.TYPE_STRING_CHOICE_PATH, path );
		if( properties == null )
			throw new IllegalArgumentException( "properties must not be null" );
		
		setValueInfo( new ButtonContentChoice() );
		setLabel( DockUI.getDefaultDockUI().getString( "preference.layout.ButtonContentPreference.text" ) );
		setDescription( DockUI.getDefaultDockUI().getString( "preference.layout.ButtonContentPreference.description" ) );
		this.properties = properties;
	}
	
	@Override
	public void read() {
		setValue( ButtonContentChoice.getId( properties.get( FlapDockStation.BUTTON_CONTENT )));
	}
	
	@Override
	public void write() {
		properties.setOrRemove( FlapDockStation.BUTTON_CONTENT, ButtonContentChoice.getContent( getValue() ));
	}
}
