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
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.facile.menu.ThemeMenuPiece;

/**
 * A {@link ThemeMenuPiece} that offers the {@link DockTheme}s specified
 * in the {@link ThemeMap} of the owning {@link CControl}.
 * @author Benjamin Sigg
 * @see CControl#getThemes()
 */
public class CThemeMenuPiece extends ThemeMenuPiece{
    
    /**
     * Creates a new piece.
     * @param control the control whose theme might be changed using one of themes
     * of {@link CControl#getThemes()}
     */
    public CThemeMenuPiece( CControl control ) {
        super( control.intern().getController(), control.getThemes() );
    }
}
