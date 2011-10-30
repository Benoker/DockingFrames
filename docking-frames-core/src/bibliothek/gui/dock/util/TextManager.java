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
package bibliothek.gui.dock.util;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.gui.dock.util.text.TextBridge;
import bibliothek.gui.dock.util.text.TextValue;
import bibliothek.util.Path;

/**
 * A map of {@link String}-{@link String} pairs used by various objects. Each <code>String</code> can
 * be stored with different {@link Priority priorities} and can be replaced at any time. Clients should not access
 * the entries directly, they should create a {@link TextValue} and register it in order to receive updates
 * when necessary.
 * @author Benjamin Sigg
 */
public class TextManager extends UIProperties<String, TextValue, TextBridge>{
	/** Name of an {@link ExtensionName} to load additional {@link ResourceBundle}s */
	public static final Path TEXT_EXTENSION = new Path( "dock.TextManager" );
	
	/** Key for a {@link Locale} that is associated with {@link #TEXT_EXTENSION} */
	public static final String TEXT_EXTENSION_LOCALE = "locale";
	
	/**
	 * Creates a new {@link TextManager}.
	 * @param controller the owner of this manager
	 */
	public TextManager( DockController controller ){
		super( controller );
	}

	/**
	 * This method loads additional {@link ResourceBundle}s from {@link Extension}s using the name
	 * {@link #TEXT_EXTENSION}.
	 * @param locale the requested language
	 * @return the loaded bundles, may be empty
	 */
	public List<ResourceBundle> loadExtensionBundles( Locale locale ){
		return getController().getExtensions().load( new ExtensionName<ResourceBundle>( TEXT_EXTENSION, ResourceBundle.class, TEXT_EXTENSION_LOCALE, locale ) );
	}
}
