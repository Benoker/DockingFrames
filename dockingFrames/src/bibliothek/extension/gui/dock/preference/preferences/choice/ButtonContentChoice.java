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

import bibliothek.gui.DockUI;
import bibliothek.gui.dock.FlapDockStation.ButtonContent;

/**
 * A choice for a {@link ButtonContent}.
 * @author Benjamin Sigg
 *
 */
public class ButtonContentChoice extends DefaultChoice{
	
	/**
	 * Creates a new choice
	 */
	public ButtonContentChoice(){
		DockUI ui = DockUI.getDefaultDockUI();
		
		add( "td", ui.getString( "preference.layout.choice.ButtonContent.theme_dependent" ));
		
		add( "io", ui.getString( "preference.layout.choice.ButtonContent.icon_only" ));
		add( "to", ui.getString( "preference.layout.choice.ButtonContent.text_only" ));
		add( "ito", ui.getString( "preference.layout.choice.ButtonContent.icon_and_text_only" ));
		add( "itto", ui.getString( "preference.layout.choice.ButtonContent.icon_then_text_only" ));
		add( "ttio", ui.getString( "preference.layout.choice.ButtonContent.text_then_icon_only" ));
		
		add( "ia", ui.getString( "preference.layout.choice.ButtonContent.icon_actions" ));
		add( "ta", ui.getString( "preference.layout.choice.ButtonContent.text_actions" ));
		add( "ita", ui.getString( "preference.layout.choice.ButtonContent.icon_and_text_actions" ));
		add( "itta", ui.getString( "preference.layout.choice.ButtonContent.icon_then_text_actions" ));
		add( "ttia", ui.getString( "preference.layout.choice.ButtonContent.text_then_icon_actions" ));
	}
	
	/**
	 * Given the string identifier <code>id</code> this method returns the
	 * {@link ButtonContent} that is associated with that id.
	 * @param id the unique identifier
	 * @return its content
	 */
	public static ButtonContent getContent( String id ){
		if( "td".equals( id ))
			return ButtonContent.THEME_DEPENDENT;
		
		if( "io".equals( id ))
			return ButtonContent.ICON_ONLY;
		if( "to".equals( id ))
			return ButtonContent.TEXT_ONLY;
		if( "ito".equals( id ))
			return ButtonContent.ICON_AND_TEXT_ONLY;
		if( "itto".equals( id ))
			return ButtonContent.ICON_THEN_TEXT_ONLY;
		if( "ttio".equals( id ))
			return ButtonContent.TEXT_THEN_ICON_ONLY;
		
		if( "ia".equals( id ))
			return ButtonContent.ICON_ACTIONS;
		if( "ta".equals( id ))
			return ButtonContent.TEXT_ACTIONS;
		if( "ita".equals( id ))
			return ButtonContent.ICON_AND_TEXT_ACTIONS;
		if( "itta".equals( id ))
			return ButtonContent.ICON_THEN_TEXT_ACTIONS;
		if( "ttia".equals( id ))
			return ButtonContent.TEXT_THEN_ICON_ACTIONS;
		
		return null;
	}
	
	/**
	 * Given a {@link ButtonContent} this method returns the unique
	 * string identifier for that content.
	 * @param content the content whose identifier is searched
	 * @return the unique string identifier
	 */
	public static String getId( ButtonContent content ){
		switch( content ){
			case THEME_DEPENDENT: return "td";
			
			case ICON_ONLY: return "io";
			case TEXT_ONLY: return "to";
			case ICON_AND_TEXT_ONLY: return "ito";
			case ICON_THEN_TEXT_ONLY: return "itto";
			case TEXT_THEN_ICON_ONLY: return "ttio";
			
			case ICON_ACTIONS: return "ia";
			case TEXT_ACTIONS: return "ta";
			case ICON_AND_TEXT_ACTIONS: return "ita";
			case ICON_THEN_TEXT_ACTIONS: return "itta";
			case TEXT_THEN_ICON_ACTIONS: return "ttia";
			
			default: return null;
		}
	}
}
