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

import bibliothek.gui.dock.common.action.core.CommonDockAction;
import bibliothek.gui.dock.common.intern.CDockable;

/**
 * A {@link CAction} is associated with one {@link CDockable}, allowing the
 * user to perform actions which are somehow connected to that <code>CDockable</code>. 
 * @author Benjamin Sigg
 */
public class CAction {
    /** the internal representation of the action */
    private CommonDockAction action;
    
    /**
     * Creates a new empty {@link CAction}, subclasses must call
     * {@link #init(CommonDockAction)} to complete initialization of this action.
     */
    protected CAction(){
    	// nothing
    }
    
    /**
     * Creates a new CAction
     * @param action the internal representation of this action. Subclasses
     * can put <code>null</code> in here and later call {@link #init(CommonDockAction)}
     */
    protected CAction( CommonDockAction action ){
        if( action != null )
            init( action );
    }
    
    /**
     * Initializes this action. This method can be called only once.
     * @param action the internal representation, not <code>null</code>
     */
    protected void init( CommonDockAction action ){
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
    public CommonDockAction intern(){
        return action;
    }
}
