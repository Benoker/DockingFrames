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
package bibliothek.gui.dock.common.action;

import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.common.action.core.CommonSeparatorAction;
import bibliothek.gui.dock.common.intern.CDockable;

/**
 * Represents a line separating some groups of {@link CAction}s. Clients should
 * use {@link #SEPARATOR}, {@link #MENU_SEPARATOR} or {@link #TITLE_SEPARATOR}
 * instead of creating a new action.
 * @author Benjamin Sigg
 */
public class CSeparator extends CAction{
    /** the normal separator */
    public static final CSeparator SEPARATOR = new CSeparator( ViewTarget.MENU, ViewTarget.TITLE, ViewTarget.DROP_DOWN );
    
    /** a separator which is only visible in menus */
    public static final CSeparator MENU_SEPARATOR = new CSeparator( ViewTarget.MENU, ViewTarget.DROP_DOWN );
    
    /** a separator which is only visible on a title of a {@link CDockable} */
    public static final CSeparator TITLE_SEPARATOR = new CSeparator( ViewTarget.TITLE );
    
    /**
     * Creates a new separator
     * @param targets where to show this action
     */
    protected CSeparator( ViewTarget<?>... targets ){
        init( new CommonSeparatorAction( this, targets ));
    }
}
