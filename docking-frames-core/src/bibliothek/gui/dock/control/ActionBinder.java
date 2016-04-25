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

package bibliothek.gui.dock.control;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.gui.dock.event.DockRegisterAdapter;

/**
 * A manager which ensures that every {@link DockAction} is bound to its {@link Dockable}s.
 * @author Benjamin Sigg
 *
 */
public class ActionBinder extends DockRegisterAdapter{
    /** the observers of each Dockable that is known to this manager */
    private Map<Dockable, SourceObserver> observers = new HashMap<Dockable, SourceObserver>();
    
    /** the controller for which this binder works */
    private DockController controller;
    
    /**
     * Creates a new binder.
     * @param controller the owner, not <code>null</code>
     */
    public ActionBinder( DockController controller ){
    	this.controller = controller;
    }
    
    /**
     * Gets the owner of this binder.
     * @return the owner, not <code>null</code>
     */
    public DockController getController(){
		return controller;
	}
    
    @Override
    public void dockableRegistered( DockController controller, Dockable dockable ) {
        observers.put( dockable, new SourceObserver( dockable ) );
    }
    
    @Override
    public void dockableUnregistered( DockController controller, Dockable dockable ) {
        observers.remove( dockable ).destroy();
    }
    
    /**
     * Observes the {@link DockAction}s of one {@link Dockable} and makes sure
     * that each action is bound.
     * @author Benjamin Sigg
     *
     */
    private static class SourceObserver implements DockActionSourceListener{
        /** the set of bound actions */
        private List<DockAction> actions = new LinkedList<DockAction>();
        
        /** the dockable whose actions are observed */
        private Dockable dockable;
        
        /**
         * Creates a new observer
         * @param dockable the element whose actions will be managed
         */
        public SourceObserver( Dockable dockable ){
            this.dockable = dockable;
            DockActionSource source = dockable.getGlobalActionOffers();
            actionsAdded( source, 0, source.getDockActionCount()-1 );
            source.addDockActionSourceListener( this );
        }
        
        /**
         * Removes all listeners added by this listener.
         */
        public void destroy(){
            DockActionSource source = dockable.getGlobalActionOffers();
            source.removeDockActionSourceListener( this );
            actionsRemoved( source, 0, source.getDockActionCount()-1 );
        }
        
        public void actionsAdded( DockActionSource source, int firstIndex, int lastIndex ) {
            for( int i = firstIndex; i <= lastIndex; i++ ){
                DockAction action = source.getDockAction( i );
                actions.add( i, action );
                action.bind( dockable );
            }
        }

        public void actionsRemoved( DockActionSource source, int firstIndex, int lastIndex ) {
            for( int i = lastIndex; i >= firstIndex; i-- ){
                DockAction action = actions.remove( i );
                action.unbind( dockable );
            }
        }
    }
}
