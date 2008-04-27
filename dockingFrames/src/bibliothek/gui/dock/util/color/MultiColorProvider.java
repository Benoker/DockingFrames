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

import bibliothek.gui.dock.util.MultiUIBridge;

/**
 * A {@link ColorProvider} which uses other providers to handle some
 * colors.
 * @author Benjamin Sigg
 * @param <D> the kind of {@link DockColor}s this provider handles.
 * @deprecated this class was replaced by {@link MultiUIBridge} 
 */
@Deprecated
public class MultiColorProvider<D extends DockColor> extends MultiUIBridge<Color, D> implements ColorProvider<D>{
    /**
     * Creates a new {@link ColorProvider}.
     * @param manager the manager from whom this provider will get default
     * colors when necessary
     */
    public MultiColorProvider( ColorManager manager ){
        super( manager );
    }
}
