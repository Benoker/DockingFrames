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

import bibliothek.gui.DockController;
import bibliothek.gui.dock.station.flap.button.ButtonContent;
import bibliothek.gui.dock.station.flap.button.ButtonContentCondition;

/**
 * A list of {@link ButtonContentConditionChoice}s.
 * @author Benjamin Sigg
 */
public class ButtonContentConditionChoice extends DefaultChoice<ButtonContentCondition> {
	public ButtonContentConditionChoice( DockController controller ){
		super( controller );
		
		addLinked( "theme", "preference.buttonContent.condition.theme", ButtonContent.THEME );
		addLinked( "true", "preference.buttonContent.condition.true", ButtonContent.TRUE );
		addLinked( "false", "preference.buttonContent.condition.false", ButtonContent.FALSE );
		addLinked( "ifDockable", "preference.buttonContent.condition.ifDockable", ButtonContent.IF_DOCKABLE );
		addLinked( "ifStation", "preference.buttonContent.condition.ifStation", ButtonContent.IF_STATION );
		addLinked( "notIfIcon", "preference.buttonContent.condition.notIfIcon", ButtonContent.NOT_IF_ICON );
		addLinked( "notIfText", "preference.buttonContent.condition.notIfText", ButtonContent.NOT_IF_TEXT );
		
		setDefaultChoice( "theme" );
	}
}
