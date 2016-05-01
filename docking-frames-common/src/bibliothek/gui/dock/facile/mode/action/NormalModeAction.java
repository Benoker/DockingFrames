/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.facile.mode.action;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.facile.mode.NormalMode;
import bibliothek.gui.dock.station.flap.button.ButtonContentAction;
import bibliothek.gui.dock.support.mode.Mode;

/**
 * An action used to change the {@link Mode} of a {@link Dockable} to
 * the {@link NormalMode}.
 * @author Benjamin Sigg
 */
@ButtonContentAction
public class NormalModeAction extends LocationModeAction{
	/**
	 * Creates a new action.
	 * @param controller the controller in whose realm this action is used
	 * @param mode the mode which is applied
	 */
	public NormalModeAction( DockController controller, NormalMode<?> mode ){
		super( controller, mode, NormalMode.ICON_IDENTIFIER, "normalize.in", "normalize.in.tooltip", CControl.KEY_GOTO_NORMALIZED );
	}
}
