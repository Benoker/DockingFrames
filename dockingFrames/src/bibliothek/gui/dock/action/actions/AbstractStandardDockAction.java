/**
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

package bibliothek.gui.dock.action.actions;

import java.util.*;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.event.StandardDockActionListener;

/**
 * An abstract implementation of the {@link DockAction}-interface. 
 * This implementation allows to {@link #addDockActionListener(StandardDockActionListener) register}
 * some {@link StandardDockActionListener}, and stores the {@link Dockable Dockables}
 * which are using this action.  
 * @author Benjamin Sigg
 */
public abstract class AbstractStandardDockAction implements StandardDockAction {
    /** The listeners that are registered by this action */
    protected List<StandardDockActionListener> listeners = new ArrayList<StandardDockActionListener>();
    
    /** All {@link Dockable Dockables} which can be used by this action */
    private Map<Dockable, Integer> bound = new HashMap<Dockable, Integer>();

    public void addDockActionListener( StandardDockActionListener listener ) {
        listeners.add( listener );
    }

    public void removeDockActionListener( StandardDockActionListener listener ) {
        listeners.remove( listener );
    }

    /**
     * Invoked by this {@link AbstractStandardDockAction} when a {@link Dockable}
     * was bound to this action the first time.
     * @param dockable The Dockable that was not known to this action
     * before the method was invoked
     */
    protected void bound( Dockable dockable ) {
    	// do nothing
    }
    
    /**
     * Called by this {@link AbstractStandardDockAction} when the {@link Dockable}
     * <code>dockable</code> will not be used in any means by this
     * action. Note that the {@link #bound(Dockable)}-method can be
     * invoked again with the <code>dockable</code>.
     * @param dockable The Dockable which will not by used in any way.
     */
    protected void unbound( Dockable dockable ) {
    	// do nothing
    }

    /**
     * Tells whether the <code>dockable</code> is bound with this
     * action, or not.
     * @param dockable The {@link Dockable} to test
     * @return <code>true</code> if it is bound, <code>false</code>
     * otherwise
     */
    public boolean isBound( Dockable dockable ){
        return bound.containsKey( dockable );
    }
    
    /**
     * Gets a set of all {@link Dockable Dockables} which are currently
     * bound to this {@link DockAction}.
     * @return The bound Dockables
     */
    public Set<Dockable> getBoundDockables(){
        return Collections.unmodifiableSet( bound.keySet() );
    }
    
    public void bind( Dockable dockable ) {
        Integer old = bound.get( dockable );
        if( old == null ){
            bound.put( dockable, 1 );
            bound( dockable );
        }
        else
            bound.put( dockable, old+1 );
    }
    
    public void unbind( Dockable dockable ) {
        Integer old = bound.get( dockable );
        if( old == null ){
            // that should not happen...
            try{
                throw new NullPointerException( "Unbind called too often, omit unbind and continue" );
            }
            catch( NullPointerException ex ){
                ex.printStackTrace();
            }
        }
        else{
            if( old == 1 ){
                bound.remove( dockable );
                unbound( dockable );
            }
            else
                bound.put( dockable, old-1 );
        }
    }
    
    /**
     * Invokes the 
     * {@link StandardDockActionListener#actionTextChanged(StandardDockAction, Set) actionTextChanged}-
     * method of all currently registered {@link StandardDockActionListener}
     * @param dockables The set of dockables for which the text has been
     * changed.
     */
    protected void fireActionTextChanged( Set<Dockable> dockables ){
        for( StandardDockActionListener listener : listeners.toArray( new StandardDockActionListener[ listeners.size() ] ))
            listener.actionTextChanged( this, dockables );
    }

    /**
     * Invokes the 
     * {@link StandardDockActionListener#actionTooltipTextChanged(StandardDockAction, Set) actionTooltipTextChanged}-
     * method of all currently registered {@link StandardDockActionListener}
     * @param dockables The set of dockables for which the tooltip has been
     * changed.
     */
    protected void fireActionTooltipTextChanged( Set<Dockable> dockables ){
        for( StandardDockActionListener listener : listeners.toArray( new StandardDockActionListener[ listeners.size() ] ))
            listener.actionTooltipTextChanged( this, dockables );
    }
    
    /**
     * Invokes the 
     * {@link StandardDockActionListener#actionIconChanged(StandardDockAction, Set) actionIconChanged}-
     * method of all currently registered {@link StandardDockActionListener}
     * @param dockables The set of dockables for which the icon has been
     * changed.
     */
    protected void fireActionIconChanged( Set<Dockable> dockables ){
        for( StandardDockActionListener listener : listeners.toArray( new StandardDockActionListener[ listeners.size() ] ))
            listener.actionIconChanged( this, dockables );
    }
    
    /**
     * Invokes the 
     * {@link StandardDockActionListener#actionDisabledIconChanged(StandardDockAction, Set) actionIconChanged}-
     * method of all currently registered {@link StandardDockActionListener}
     * @param dockables The set of dockables for which the icon has been
     * changed.
     */
    protected void fireActionDisabledIconChanged( Set<Dockable> dockables ){
        for( StandardDockActionListener listener : listeners.toArray( new StandardDockActionListener[ listeners.size() ] ))
            listener.actionDisabledIconChanged( this, dockables );
    }
     
    /**
     * Invokes the 
     * {@link StandardDockActionListener#actionEnabledChanged(StandardDockAction, Set) actionEnabledChanged}-
     * method of all currently registered {@link StandardDockActionListener}
     * @param dockables The set of dockables for which the enabled-state has been
     * changed.
     */
    protected void fireActionEnabledChanged( Set<Dockable> dockables ){
        for( StandardDockActionListener listener : listeners.toArray( new StandardDockActionListener[ listeners.size() ] ))
            listener.actionEnabledChanged( this, dockables );
    }
}
