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
package bibliothek.gui.dock.util.font;

import java.awt.Component;
import java.awt.Font;

/**
 * A font modifier generates a {@link Font} object for some {@link Component}.
 * @author Benjamin Sigg
 */
public interface FontModifier {
    /**
     * Creates or gets a font for <code>component</code>. Note that <code>component</code>
     * can already have a font set from this or another {@link FontModifier}.
     * @param component the component for which a font is requested
     * @return the font for <code>component</code>, can be <code>null</code>
     */
    public Font getFont( Component component );
}
