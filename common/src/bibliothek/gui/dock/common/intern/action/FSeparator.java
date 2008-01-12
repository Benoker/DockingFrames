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
package bibliothek.gui.dock.common.intern.action;

import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.common.action.FAction;
import bibliothek.gui.dock.common.intern.FDockable;

/**
 * Represents a line separating some groups of {@link FAction}s. Clients should
 * use {@link #SEPARATOR}, {@link #MENU_SEPARATOR} or {@link #TITLE_SEPARATOR}
 * instead of creating a new action.
 * @author Benjamin Sigg
 */
public class FSeparator extends FAction{
    /** the normal separator */
    public static final FSeparator SEPARATOR = new FSeparator( SeparatorAction.SEPARATOR );
    
    /** a separator which is only visible in menus */
    public static final FSeparator MENU_SEPARATOR = new FSeparator( SeparatorAction.MENU_SEPARATOR );
    
    /** a separator which is only visible on a title of a {@link FDockable} */
    public static final FSeparator TITLE_SEPARATOR = new FSeparator( SeparatorAction.TITLE_SEPARATOR );
    
    /**
     * Creates a new separator
     * @param separator the internal representation
     */
    protected FSeparator( SeparatorAction separator ){
        super( separator );
    }
}
