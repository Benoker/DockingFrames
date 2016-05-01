/*
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
package bibliothek.gui.dock.common.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.facile.menu.FrontendSettingsMenuPiece;

/**
 * A piece of a menu that allows the user to store and load the layout
 * of a set of {@link Dockable}s and {@link DockStation}s.
 * @author Benjamin Sigg
 * @see FrontendSettingsMenuPiece
 */
public class CLayoutChoiceMenuPiece extends FrontendSettingsMenuPiece {
    /**
     * Creates a new menu.
     * @param control the control whose layout might change
     * @param loadAsSubmenu whether the list of layouts should be added as a
     * {@link JMenu menu} or only as a list of {@link JMenuItem items}.
     */
    public CLayoutChoiceMenuPiece( CControl control, boolean loadAsSubmenu ) {
        super( control.intern(), loadAsSubmenu );
    }
}
