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
package bibliothek.gui.dock.support.action;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.*;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A set of modes. Adds some {@link ButtonDockAction}s to {@link Dockable}s,
 * and when the user clicks on one of these actions, then the {@link Dockable}
 * goes into another mode.<br>
 * Subclasses can assign some properties that connect mode and {@link Dockable}.<br>
 * This class is an {@link ActionGuard} and will only have an influence if
 * it is added to the {@link DockController} through {@link DockController#addActionGuard(ActionGuard)}
 * @param <A> the type of properties that has to be stored for every 
 * dockable-mode relation
 * @author Benjamin Sigg
 */
public abstract class ModeTransitionManager<A> implements ActionGuard{
    /** the set of all modes */
    private Map<String, Mode> modes = new HashMap<String, Mode>();
    
    /** the set of all dockables */
    private Map<Dockable, Entry> dockables = new HashMap<Dockable, Entry>();
    
    /**
     * Creates a new manager
     * @param modes the list of modes in which a {@link Dockable} might
     * go into
     */
    public ModeTransitionManager( String... modes ){
        for( String mode : modes ){
            this.modes.put( mode, new Mode( mode ) );
        }
    }
    
    public boolean react( Dockable dockable ) {
        return dockables.containsKey( dockable );
    }
    
    public DockActionSource getSource( Dockable dockable ) {
        return dockables.get( dockable ).source;
    }
    
    /**
     * Makes an entry for <code>dockable</code> and adds actions to its
     * global {@link DockActionSource}.
     * @param dockable the element to add
     */
    public void add( Dockable dockable ){
        Entry entry = new Entry( dockable );
        dockables.put( dockable, entry );
        entry.putMode( currentMode( dockable ) );
    }
    
    /**
     * Removes the properties that belong to <code>dockable</code>.
     * @param dockable the element to remove
     */
    public void remove( Dockable dockable ){
        dockables.remove( dockable );
    }
    
    /**
     * Gets the action that is displayed on {@link Dockable}s which are
     * currently in the mode <code>mode</code>.
     * @param mode the mode whose outgoing action is searched
     * @return the action or <code>null</code> if <code>mode</code> is unknown
     */
    public SimpleButtonAction getOutgoingAction( String mode ){
        Mode m = modes.get( mode );
        if( m == null )
        	return null;

        return m.outgoing();
    }
    
    /**
     * Gets the action that is displayed on {@link Dockable}s which might
     * to into the mode <code>mode</code>.
     * @param mode the mode whose ingoing action is searched
     * @return the action or <code>null</code> if <code>mode</code> is unknown
     */
    public SimpleButtonAction getIngoingAction( String mode ){
        Mode m = modes.get( mode );
        if( m == null )
        	return null;
        
        return m.ingoing();        
    }
    
    /**
     * Gets the mode <code>dockable</code> is currently into. This method
     * must also work if <code>dockable</code> is not registered at this
     * {@link ModeTransitionManager}.
     * @param dockable the element whose mode is searched
     * @return the current mode in <code>dockable</code> is, <code>null</code>
     * is not valid.
     */
    protected abstract String currentMode( Dockable dockable );
    
    /**
     * Makes a list of all modes <code>dockable</code> can be going into.
     * @param current the mode <code>dockable</code> is currently in
     * @param dockable the element whose available modes are searched
     * @return an ordered list of available modes. If there is a logic for
     * "going of of a mode", then the current mode should be included
     */
    protected abstract String[] availableModes( String current, Dockable dockable );

    /**
     * Gets the mode <code>dockable</code> should be go to if no other
     * mode is preferred.
     * @param dockable the element whose default mode is asked
     * @return the mode
     */
    protected abstract String getDefaultMode( Dockable dockable );
    
    /**
     * Called when a {@link Dockable} has to change from one mode to another mode.<br>
     * Subclasses might use {@link #getProperties(String, Dockable)} and
     * {@link #setProperties(String, Dockable, Object)} to get or store
     * properties associated with the mode.
     * @param oldMode the mode <code>dockable</code> is currently in
     * @param newMode the mode <code>dockable</code> is going to be
     * @param dockable the element that changes its mode
     */
    protected abstract void transition( String oldMode, String newMode, Dockable dockable );
    
    /**
     * Called when the button to go out of <code>mode</code> is pressed.
     * @param mode the mode to leave
     * @param dockable the affected element
     */
    protected void goOut( String mode, Dockable dockable ){
        transition( mode, dockables.get( dockable ).popMode(), dockable );
    }
    
    /**
     * Called when the button to go into <code>mode</code> is pressed.
     * @param mode the mode to go into
     * @param dockable the affected element
     */
    protected void goIn( String mode, Dockable dockable ){
        transition( currentMode( dockable ), mode, dockable );
        Entry entry = dockables.get( dockable );
        if( entry != null ){
            entry.putMode( mode );
        }
    }
    
    /**
     * Sets the mode of <code>dockable</code> to <code>mode</code>.
     * @param dockable the element to set the mode
     * @param mode the new mode of <code>dockable</code>
     */
    public void setMode( Dockable dockable, String mode ){
        goIn( mode, dockable );
    }
    
    /**
     * Ensures that the mode that belongs to <code>dockable</code> and all
     * its children is set correctly. Also {@link #rebuild(Dockable) rebuilds} 
     * the list of actions when necessary.
     * @param dockable the element which should be checked.
     */
    protected void validate( Dockable dockable ){
        DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
            @Override
            public void handleDockable( Dockable dockable ) {
                Entry entry = dockables.get( dockable );
                if( entry != null ){
                    String oldMode = entry.peekMode();
                    String newMode = currentMode( dockable );
                    
                    if( !newMode.equals( oldMode ) ){
                        entry.putMode( newMode );
                        rebuild( dockable );
                    }
                }
            }
        });
    }
    
    /**
     * Called when the list of actions for <code>dockable</code> has to be
     * rebuild.
     * @param dockable the element whose actions are searched
     */
    protected void rebuild( Dockable dockable ){
        Entry entry = dockables.get( dockable );
        if( entry != null ){
            String mode = currentMode( dockable );
            String[] available = availableModes( mode, dockable );
            
            entry.source.removeAll();
            for( String check : available ){
                if( modes.containsKey( check ) ){
                    if( check.equals( mode ))
                        entry.source.add( getOutgoingAction( check ));
                    else
                        entry.source.add( getIngoingAction( check ) );
                }
            }
        }
    }
    
    /**
     * Gets the properties which correspond to <code>dockable</code>
     * and <code>mode</code>.
     * @param mode the first part of the key
     * @param dockable the second part of the key
     * @return the properties or <code>null</code>
     */
    protected A getProperties( String mode, Dockable dockable ){
        Entry entry = dockables.get( dockable );
        if( entry == null )
            return null;
        
        return entry.properties.get( mode );
    }
    
    /**
     * Sets the properties which correspond to <code>dockable</code>
     * and <code>mode</code>. Does nothing if <code>dockable</code> is
     * unknown.
     * @param mode the first part of the key
     * @param dockable the second part of the key
     * @param properties the things to store or <code>null</code> to delete
     * the entry
     */
    protected void setProperties( String mode, Dockable dockable, A properties ){
        Entry entry = dockables.get( dockable );
        if( entry != null ){
            if( properties == null )
                entry.properties.remove( mode );
            else
                entry.properties.put( mode, properties );
        }
    }
    
    /**
     * Stores <code>mode</code> as new mode of <code>dockable</code>, put
     * does not call {@link #transition(String, String, Dockable)}.
     * @param dockable the element whose mode changes
     * @param mode the new mode
     */
    protected void putMode( Dockable dockable, String mode ){
    	Entry entry = dockables.get( dockable );
    	if( entry != null ){
    		entry.putMode( mode );
    		validate( dockable );
    	}
    }
    
    /**
     * Gets the mode in which <code>Dockable</code> was previously
     * @param dockable the element whose mode is searched
     * @return the previous more or <code>null</code>
     */
    protected String previousMode( Dockable dockable ){
    	Entry entry = dockables.get( dockable );
    	if( entry == null )
    		return null;
    	
    	if( entry.history.size() < 2 )
    		return null;
    	
    	return entry.history.get( entry.history.size()-2 );
    }
    
    /**
     * Describes all properties of a mode.
     * @author Benjamin Sigg
     */
    private class Mode{
        /** the identifier of the mode */
        public String name;
        /** the action to change from another mode into this mode */
        private SimpleButtonAction ingoing;
        /** the action to go out of this mode */
        private SimpleButtonAction outgoing;
        
        /**
         * Creates a new mode.
         * @param name the id of the mode
         */
        public Mode( String name ){
            this.name = name;
        }
        
        /**
         * Gets the action used to go into this mode.
         * @return the action to go in
         */
        public SimpleButtonAction ingoing(){
        	if( ingoing == null ){
	        	ingoing = new SimpleButtonAction(){
	                @Override
	                public void action( Dockable dockable ) {
	                    super.action( dockable );
	                    goIn( Mode.this.name, dockable );
	                }
	            };
        	}
        	return ingoing;
        }
        
        /**
         * Gets the action used to go out of this mode.
         * @return the action to go out
         */
        public SimpleButtonAction outgoing(){
        	if( outgoing == null ){
        		outgoing = new SimpleButtonAction(){
                    @Override
                    public void action( Dockable dockable ) {
                        super.action( dockable );
                        goOut( Mode.this.name, dockable );
                    }
                };
        	}
        	
        	return outgoing;
        }
    }
    
    /**
     * Describes all properties a {@link Dockable} has.
     * @author Benjamin Sigg
     */
    private class Entry{
        /** the {@link Dockable} for which the properties are stored */
        public Dockable dockable;
        /** the set of actions available for {@link #dockable} */
        public DefaultDockActionSource source;
        /** a map that stores some properties mapped to the different modes */
        public Map<String, A> properties;

        /** The modes this entry already visited. No mode is more than once in this list. */
        private List<String> history;
        
        /**
         * Creates a new entry
         * @param dockable the element whose properties are stores in this entry
         */
        public Entry( Dockable dockable ){
            this.dockable = dockable;
            source = new DefaultDockActionSource( new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT ) );
            properties = new HashMap<String, A>();
            history = new LinkedList<String>();
        }
        
        /**
         * Stores <code>mode</code> in a stack that describes the history
         * through which this entry moved. If <code>mode</code> is already
         * in the stack, than it is moved to the top of the stack. 
         * @param mode the mode to store
         */
        public void putMode( String mode ){
            history.remove( mode );
            history.add( mode );
        }
        
        /**
         * Removes the top mode and the returns the new top of the history.
         * If the history gets empty, then {@link ModeTransitionManager#getDefaultMode(Dockable)}
         * is returned.
         * @return the mode in which this entry was before the current mode
         * was put onto the history
         */
        public String popMode(){
            if( !history.isEmpty() )
                history.remove( history.size()-1 );
            
            if( history.isEmpty() )
                return getDefaultMode( dockable );
            else
                return history.get( history.size()-1 );
        }
        
        /**
         * Gets the current mode of this entry.
         * @return the mode or <code>null</code>
         */
        public String peekMode(){
            if( history.isEmpty() )
                return null;
            else
                return history.get( history.size()-1 );
        }
    }
}
