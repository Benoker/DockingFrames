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
package bibliothek.gui.dock.util.color;

import java.awt.Color;

import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.UIProperties;

/**
 * A {@link ColorManager} contains {@link Color}s, {@link ColorProvider}s and
 * {@link DockColor}s. Some <code>DockColor</code>s are associated with a 
 * <code>ColorProvider</code>. If a <code>Color</code> in this manager is
 * {@link #put(Priority, String, Color) set}, then each <code>DockColor</code> that listens
 * for that color gets informed about the change either through its 
 * provider or directly from the manager.
 * @author Benjamin Sigg
 */
public class ColorManager extends UIProperties<Color, DockColor>{
    // no new methods
}
