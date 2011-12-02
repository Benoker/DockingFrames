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
package bibliothek.extension.gui.dock.preference.preferences.choice;

import bibliothek.extension.gui.dock.preference.model.ButtonContentPreferenceModel;
import bibliothek.gui.dock.station.flap.button.ButtonContent;
import bibliothek.gui.dock.util.DockProperties;

/**
 * A choice for a {@link ButtonContent}. Only a selected set of {@link ButtonContent}s are available through this
 * choice.
 * @author Benjamin Sigg
 * @deprecated replaced by {@link ButtonContentPreferenceModel}
 */
@Deprecated
public class ButtonContentChoice extends DefaultChoice<ButtonContent>{
	
	/**
	 * Creates a new choice
	 * @param properties default settings
	 */
	public ButtonContentChoice( DockProperties properties ){
		super( properties.getController() );
		
		addLinked( "td", "preference.layout.choice.ButtonContent.theme_dependent", ButtonContent.THEME_DEPENDENT );
		
		addLinked( "io", "preference.layout.choice.ButtonContent.icon_only", ButtonContent.ICON_ONLY );
		addLinked( "to", "preference.layout.choice.ButtonContent.text_only", ButtonContent.TEXT_ONLY );
		addLinked( "ito", "preference.layout.choice.ButtonContent.icon_and_text_only", ButtonContent.ICON_AND_TEXT_ONLY );
		addLinked( "itto", "preference.layout.choice.ButtonContent.icon_then_text_only", ButtonContent.ICON_THEN_TEXT_ONLY );
		addLinked( "ttio", "preference.layout.choice.ButtonContent.text_then_icon_only", ButtonContent.TEXT_THEN_ICON_ONLY );
		
		addLinked( "ia", "preference.layout.choice.ButtonContent.icon_actions", ButtonContent.ICON_ACTIONS );
		addLinked( "ta", "preference.layout.choice.ButtonContent.text_actions", ButtonContent.TEXT_ACTIONS );
		addLinked( "ita", "preference.layout.choice.ButtonContent.icon_and_text_actions", ButtonContent.ICON_AND_TEXT_ACTIONS );
		addLinked( "itta", "preference.layout.choice.ButtonContent.icon_then_text_actions", ButtonContent.ICON_THEN_TEXT_ACTIONS );
		addLinked( "ttia", "preference.layout.choice.ButtonContent.text_then_icon_actions", ButtonContent.TEXT_THEN_ICON_ACTIONS );
	}
}
