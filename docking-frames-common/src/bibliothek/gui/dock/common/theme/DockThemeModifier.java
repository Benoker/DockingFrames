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
package bibliothek.gui.dock.common.theme;

import bibliothek.gui.DockTheme;
import bibliothek.util.ClientOnly;

/**
 * A {@link DockThemeModifier} modifies a {@link DockTheme}. This can either
 * mean replacing some values, or using another theme at all.
 * @author Benjamin Sigg
 */
@ClientOnly
public interface DockThemeModifier {
    /**
     * Modifies <code>theme</code> and returns either the modified theme or
     * a new instance of of another {@link DockTheme}.
     * @param theme the theme to modify, not <code>null</code>
     * @return the modified them, <code>theme</code> or a new object
     */
    public DockTheme modify( DockTheme theme );
}
