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

package bibliothek.gui.dock.facile.action;

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.util.ClientOnly;

/**
 * A factory creating {@link RenameAction RenameActions} for a
 * {@link DockController}. There is one action created for each {@link Class}, 
 * these actions are not removed before this factory is collected by the 
 * garbage collector.
 * @author Benjamin Sigg
 */
@ClientOnly
public class RenameActionFactory {
    /** The controller for which actions are created */
    private DockController controller;
    
    /** Already created actions */
    private Map<Class<?>, RenameAction> actions = new HashMap<Class<?>, RenameAction>();
    
    /**
     * Creates a new factory
     * @param controller the controller for which actions will be created
     */
    public RenameActionFactory( DockController controller ){
        if( controller == null )
            throw new IllegalArgumentException( "Controller must not be null" );
        
        this.controller = controller;
    }
    
    /**
     * Gets the controller which is used to create the actions.
     * @return the controller
     */
    public DockController getController() {
        return controller;
    }
    
    /**
     * Gets an action for <code>owner</code>.
     * @param owner the Dockable for which an action is searched
     * @return the action
     */
    public RenameAction find( Dockable owner ){
        if( owner == null )
            throw new IllegalArgumentException( "Owner must not be null" );
        
        Class<?> clazz = owner.getClass();
        RenameAction action = find( clazz );
        if( action == null )
            throw new IllegalStateException( "Can't find a rule for " + clazz.getName() );
        return action;
    }
    
    /**
     * Searches or creates a {@link RenameAction} for <code>owner</code>.
     * @param owner the owner for which an action is searched
     * @return the action or <code>null</code> if no rule for <code>owner</code>
     * was found
     */
    protected RenameAction find( Class<?> owner ){
        RenameAction action = actions.get( owner );
        if( action == null ){
            action = create( owner );
            if( action == null ){
                Class<?> superclass = owner.getSuperclass();
                if( superclass == null )
                    return null;
                
                action = find( superclass );
            }
            
            if( action != null ){
                actions.put( owner, action );
            }
        }
        return action;
    }
    
    /**
     * Creates a new action for the specified type. Subclasses may override this
     * method to support more classes. Currently supported are {@link DefaultDockable},
     * {@link SplitDockStation}, {@link FlapDockStation} and {@link StackDockStation}.
     * @param owner the type for which the action is created
     * @return the new action and the type which can use the action.
     */
    protected RenameAction create( Class<?> owner ){
        if( owner.equals( DefaultDockable.class ) )
            return new RenameAction.RenameDefaultDockable( controller );
        
        if( owner.equals( SplitDockStation.class ))
            return  new RenameAction.RenameSplitDockStation( controller );
        
        if( owner.equals( FlapDockStation.class ))
            return  new RenameAction.RenameFlapDockStation( controller );
        
        if( owner.equals( StackDockStation.class ))
            return new RenameAction.RenameStackDockStation( controller );
        
        return null;
    }
}
