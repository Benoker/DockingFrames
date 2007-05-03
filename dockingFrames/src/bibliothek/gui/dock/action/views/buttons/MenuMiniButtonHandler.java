/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

package bibliothek.gui.dock.action.views.buttons;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.ActionPopup;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.MenuDockAction;

/**
 * A handler that will show a popup-menu whenever the user triggers the 
 * handler.
 * @author Benjamin Sigg
 */
public class MenuMiniButtonHandler extends AbstractMiniButtonHandler<MenuDockAction, MiniButton> {

	/**
	 * Creates the new handler.
	 * @param action the action for which a menu will be shown
	 * @param dockable the owner of the action
	 * @param button the button that must be pressed in order to show the menu
	 */
	public MenuMiniButtonHandler( MenuDockAction action, Dockable dockable, MiniButton button ){
		super( action, dockable, button );
	}

	public void triggered(){
		final DockActionSource source = getAction().getMenu( getDockable() );
        if( source != null && source.getDockActionCount() > 0 ){
            ActionPopup popup = new ActionPopup( false ){
                @Override
                protected Dockable getDockable() {
                    return MenuMiniButtonHandler.this.getDockable();
                }

                @Override
                protected DockActionSource getSource() {
                    return source;
                }

                @Override
                protected boolean isEnabled() {
                    return true;
                }
            };
            
            MiniButton button = getButton();
            popup.popup( button, 0, button.getHeight() );
        }
	}
}
