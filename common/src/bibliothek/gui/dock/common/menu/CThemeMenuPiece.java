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

import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.facile.menu.ThemeMenuPiece;
import bibliothek.gui.dock.themes.NoStackTheme;

/**
 * A {@link ThemeMenuPiece} that uses the default {@link DockTheme}s of
 * DockingFrames, but only in the {@link NoStackTheme no-stack} version.
 * @author Benjamin Sigg
 */
public class CThemeMenuPiece extends ThemeMenuPiece{
    
    /**
     * Creates a new piece.
     * @param control the control whose theme might be changed
     */
    public CThemeMenuPiece( CControl control ) {
        super( control.intern().getController(), control.getThemes() );
    }
}
