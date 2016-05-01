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
package bibliothek.gui.dock.themes.color;

import java.awt.Color;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.util.color.AbstractDockColor;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.util.Path;

/**
 * A color used for a {@link DockAction}.
 * @author Benjamin Sigg
 */
public abstract class ActionColor extends AbstractDockColor{
    /** the path describing this kind of color */
    public static final Path KIND_ACTION_COLOR = DockColor.KIND_DOCK_COLOR.append( "ActionColor" );
    
    /** the dockable for which the action is used */
    private Dockable dockable;
    
    /** the action for which this color is used */
    private DockAction action;
    
    /**
     * Creates a new {@link DockColor}.
     * @param id the identifier of this color
     * @param kind which kind of color this is
     * @param dockable the Dockable for which the action is shown
     * @param action the action for which the color is used
     * @param backup a backup in case a color is missing
     */
    public ActionColor( String id, Path kind, Dockable dockable, DockAction action, Color backup ){
        super( id, kind, backup );
        this.action = action;
        this.dockable = dockable;
    }
    
    
    /**
     * Creates a new {@link DockColor}.
     * @param id the identifier of this color
     * @param dockable the Dockable for which the action is shown
     * @param action the action for which the color is used
     * @param backup a backup in case a color is missing
     */
    public ActionColor( String id, Dockable dockable, DockAction action, Color backup ){
        this( id, KIND_ACTION_COLOR, dockable, action, backup );
    }
    
    /**
     * Gets the action for which this color is used.
     * @return the action
     */
    public DockAction getAction() {
        return action;
    }
    
    /**
     * Gets the {@link Dockable} for which the action is shown.
     * @return the owner of the action
     */
    public Dockable getDockable() {
        return dockable;
    }
}
