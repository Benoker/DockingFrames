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
package bibliothek.gui.dock.themes.color;

import java.awt.Color;

import bibliothek.gui.dock.focus.DockableSelection;
import bibliothek.gui.dock.util.color.AbstractDockColor;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.util.Path;

/**
 * A color used by a {@link DockableSelection}.
 * @author Benjamin Sigg
 */
public abstract class DockableSelectionColor extends AbstractDockColor {
    /** the kind of color {@link DockableSelectionColor} is */
    public static final Path KIND_DOCKABLE_SELECTION_COLOR = KIND_DOCK_COLOR.append( "DockableSelectionColor" );
    
    private DockableSelection selection;
    
    /**
     * Creates a new {@link DockColor}.
     * @param selection the owner of this color
     * @param id the id of this color
     * @param kind the kind of color this is
     * @param backup the backup color if nothing is available
     */
    public DockableSelectionColor( DockableSelection selection, String id, Path kind, Color backup ){
        super( id, kind, backup );
        this.selection = selection;
    }
    
    /**
     * Creates a new {@link DockColor}.
     * @param selection the owner of this color
     * @param id the id of this color
     * @param backup the backup color if nothing is available
     */
    public DockableSelectionColor( DockableSelection selection, String id, Color backup ){
        super( id, KIND_DOCKABLE_SELECTION_COLOR, backup );
        this.selection = selection;
    }
    
    /**
     * Gets the selection that uses this color.
     * @return the owner
     */
    public DockableSelection getSelection() {
        return selection;
    }
}
