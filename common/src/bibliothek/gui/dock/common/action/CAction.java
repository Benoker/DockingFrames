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

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A {@link CAction} is associated with one {@link CDockable}, allowing the
 * user to perform actions which are somehow connected to that <code>CDockable</code>. 
 * @author Benjamin Sigg
 */
@Todo(compatibility=Compatibility.COMPATIBLE, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_0,
		description="remove 'abstract'")
public abstract class CAction {
    /** the internal representation of the action */
    private DockAction action;
    
    /**
     * Creates a new CAction
     * @param action the internal representation of this action. Subclasses
     * can put <code>null</code> in here and later call {@link #init(DockAction)}
     */
    protected CAction( DockAction action ){
        if( action != null )
            init( action );
    }
    
    /**
     * Initializes this action. This method can be called only once.
     * @param action the internal representation, not <code>null</code>
     */
    protected void init( DockAction action ){
        if( this.action != null )
            throw new IllegalStateException( "already initialized" );
        
        if( action == null )
            throw new NullPointerException( "action is null" );
        
        this.action = action;
    }
    
    /**
     * Gets the internal representation of the action.
     * @return the representation
     */
    public DockAction intern(){
        return action;
    }
}
