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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.*;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;

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
    
    /** the set of all entries, includes the content of {@link #dockables} */
    private Map<String, Entry> entries = new HashMap<String, Entry>();
    
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
     * Adds an empty entry to this manager. The empty entry can be used to store
     * information for a {@link Dockable} that has not yet been created. It is
     * helpful if the client intends to load first its properties and create
     * only those {@link Dockable}s which are visible.<br>
     * If there is already an entry for <code>name</code>, then this method
     * does do nothing.
     * @param name the name of the empty entry
     */
    public void addEmpty( String name ){
        if( name == null )
            throw new NullPointerException( "name must not be null" );
        
        Entry entry = entries.get( name );
        
        if( entry == null ){
            entry = new Entry( null, name );
            entries.put( name, entry );
        }
    }
    
    /**
     * Removes the entry for <code>name</code> but only if the entry is not
     * associated with any {@link Dockable}.
     * @param name the name of the entry which might be empty
     */
    public void removeEmpty( String name ){
        if( name == null )
            throw new NullPointerException( "name must not be null" );
        
        Entry entry = entries.get( name );
        if( entry.dockable == null )
            entries.remove( name );
    }
    
    /**
     * Makes an entry for <code>dockable</code> and adds actions to its
     * global {@link DockActionSource}.
     * @param name a unique name for <code>dockable</code>
     * @param dockable the element to add
     * @see #put(String, Dockable)
     */
    public void add( String name, Dockable dockable ){
        if( name == null )
            throw new NullPointerException( "name must not be null" );
        
        if( dockable == null )
            throw new NullPointerException( "dockable must not be null" );
        
        Entry entry = entries.get( name );
        if( entry != null && entry.dockable != null )
            throw new IllegalArgumentException( "There is already a dockable registered with the name: " + name );
        
        if( entry == null ){
            entry = new Entry( dockable, name );
            entries.put( entry.id, entry );
        }
        else{
            entry.dockable = dockable;
        }
        
        dockables.put( dockable, entry );
        entry.putMode( currentMode( dockable ) );
        
        added( dockable );
    }
    
    /**
     * Called when a {@link Dockable} has been added to this manager.
     * @param dockable the new dockable
     */
    protected void added( Dockable dockable ){
        
    }
    
    /**
     * Ensures that <code>dockable</code> is registered under <code>name</code>
     * and that <code>dockable</code> has an entry. If there is already a 
     * {@link Dockable} known under <code>name</code>, then this other
     * <code>Dockable</code> is replaced by <code>dockable</code>.
     * @param name the name of <code>dockable</code>
     * @param dockable the new {@link Dockable}
     * @see #add(String, Dockable)
     */
    public void put( String name, Dockable dockable ){
        if( name == null )
            throw new NullPointerException( "name must not be null" );
        
        if( dockable == null )
            throw new NullPointerException( "dockable must not be null" );
        
        Entry entry = entries.get( name );
        if( entry != null ){
            if( entry.dockable != null ){
                dockables.remove( entry.dockable );
                removed( entry.dockable );
            }
            entry.dockable = dockable;
            dockables.put( dockable, entry );
            added( dockable );
            return;
        }
        
        // was not inserted
        entry = new Entry( dockable, name );
        dockables.put( dockable, entry );
        entries.put( entry.id, entry );
        entry.putMode( currentMode( dockable ) );
        added( dockable );
    }
    
    /**
     * Removes the properties that belong to <code>dockable</code>.
     * @param dockable the element to remove
     */
    public void remove( Dockable dockable ){
        Entry entry = dockables.remove( dockable );
        if( entry != null ){
            entries.remove( entry.id );
            removed( dockable );
        }
    }
    
    /**
     * Removes <code>dockable</code> itself, put the properties of
     * <code>dockable</code> remain in the system.
     * @param dockable the element to reduce
     */
    public void reduceToEmpty( Dockable dockable ){
        Entry entry = dockables.get( dockable );
        if( entry != null ){
            entry.dockable = null;
            removed( dockable );
        }
    }
    
    /**
     * Called after a {@link Dockable} was removed from this managar.
     * @param dockable the element that was removed
     */
    protected void removed( Dockable dockable ){
        
    }
    
    /**
     * Gets a list that contains all {@link Dockable}s that are currently
     * known to this manager.
     * @return the list of known <code>Dockable</code>s, the list can be modified
     * without disturbing this manager.
     */
    public List<Dockable> getDockables(){
        return new ArrayList<Dockable>( dockables.keySet() );
    }
    
    /**
     * Searches the name of <code>dockable</code>.
     * @param dockable an element whose name is searched
     * @return the name or <code>null</code>
     */
    public String getName( Dockable dockable ){
        Entry entry = dockables.get( dockable );
        return entry == null ? null : entry.id;
    }
    
    /**
     * Gets the action that is displayed on {@link Dockable}s which are
     * currently in the mode <code>mode</code>.
     * @param mode the mode whose outgoing action is searched
     * @return the action or <code>null</code>
     * @throws IllegalArgumentException if <code>mode</code> is unknown
     */
    public SimpleButtonAction getOutgoingAction( String mode ){
        Mode m = modes.get( mode );
        if( m == null )
        	throw new IllegalArgumentException( "mode is unknown: " + mode );

        return m.outgoing();
    }
    
    /**
     * Gets the action that should be used to go out from mode <code>mode</code>
     * and that will be shown on <code>dockable</code>.
     * @param mode the mode whose outgoing action is searched
     * @param dockable the element for which the action will be used
     * @return the action
     * @throws IllegalArgumentException if <code>mode</code> is unknown
     */
    public DockAction getOutgoingAction( String mode, Dockable dockable ){
        return getOutgoingAction( mode );
    }

    /**
     * Sets the action that is displayed on {@link Dockable}s which might
     * go out of the mode <code>mode</code>.
     * @param mode some mode whose action should be exchanged
     * @param action the new action, can be <code>null</code>
     * @throws IllegalArgumentException if <code>mode</code> is unknown
     */
    protected void putOutgoingAction( String mode, SimpleButtonAction action ){
        Mode m = modes.get( mode );
        if( m == null )
            throw new IllegalArgumentException( "mode unknown: " + mode );
        
        m.outgoing = action;
    }    
    
    /**
     * Gets the action that is displayed on {@link Dockable}s which might
     * go into the mode <code>mode</code>.
     * @param mode the mode whose ingoing action is searched
     * @return the action or <code>null</code>
     * @throws IllegalArgumentException if <code>mode</code> is unknown
     */
    public SimpleButtonAction getIngoingAction( String mode ){
        Mode m = modes.get( mode );
        if( m == null )
        	throw new IllegalArgumentException( "Mode is unknown: " + mode );
        
        return m.ingoing();        
    }
    
    /**
     * Gets the action that is used to go into mode <code>mode</code>
     * and that is shown on <code>dockable</code>.
     * @param mode the mode whose ingoing action is searched
     * @param dockable the element for which the action will be used
     * @return the action or <code>null</code>
     * @throws IllegalArgumentException if <code>mode</code> is unknown
     */
    public DockAction getIngoingAction( String mode, Dockable dockable ){
        return getIngoingAction( mode );
    }
    
    /**
     * Sets the action that is displayed on {@link Dockable}s which might
     * go into the mode <code>mode</code>.
     * @param mode some mode whose action should be exchanged
     * @param action the new action, can be <code>null</code>
     * @throws IllegalArgumentException if <code>mode</code> is unknown
     */
    protected void putIngoingAction( String mode, SimpleButtonAction action ){
        Mode m = modes.get( mode );
        if( m == null )
            throw new IllegalArgumentException( "mode unknown: " + mode );
        
        m.ingoing = action;
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
     * Called while reading modes in {@link #setSetting(ModeTransitionSetting)}.
     * Subclasses might change the mode according to <code>newMode</code>.
     * @param oldMode the mode <code>dockable</code> is currently in
     * @param newMode the mode <code>dockable</code> is going to be
     * @param dockable the element that changes its mode
     */
    protected abstract void transitionDuringRead( String oldMode, String newMode, Dockable dockable );
    
    /**
     * Gets the history of modes <code>dockable</code> was into. The history
     * contains every mode at most once, beginning with oldest mode.
     * @param dockable the element whose history is searched
     * @return the history or <code>null</code> if <code>dockable</code> is
     * not known. Modifications of the array will not have any sideeffects.
     */
    protected String[] history( Dockable dockable ){
        Entry entry = dockables.get( dockable );
        if( entry == null )
            return null;
        return entry.history.toArray( new String[ entry.history.size() ] );
    }
    
    /**
     * Called when the button to go out of <code>mode</code> is pressed.
     * @param mode the mode to leave
     * @param dockable the affected element
     */
    protected void goOut( String mode, Dockable dockable ){
        transition( mode, dockables.get( dockable ).previousMode(), dockable );
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
     * Called when the list of actions has to be rebuilt for each
     * {@link Dockable}.
     */
    protected void rebuildAll(){
        for( Entry entry : dockables.values() ){
            rebuild( entry );
        }
    }
    
    /**
     * Called when the list of actions for <code>dockable</code> has to be
     * rebuild.
     * @param dockable the element whose actions are searched
     */
    protected void rebuild( Dockable dockable ){
        Entry entry = dockables.get( dockable );
        if( entry != null ){
            rebuild( entry );
        }
    }
    
    /**
     * Rebuilds the {@link DockActionSource} of <code>entry</code>.
     * @param entry the entry whose {@link Entry#source} will be rebuilt.
     */
    private void rebuild( Entry entry ){
        String mode = currentMode( entry.dockable );
        String[] available = availableModes( mode, entry.dockable );
        
        entry.source.removeAll();
        for( String check : available ){
            if( modes.containsKey( check ) ){
                if( check.equals( mode ))
                    entry.source.add( getOutgoingAction( check, entry.dockable ));
                else
                    entry.source.add( getIngoingAction( check, entry.dockable ) );
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
    	}
    }
    
    /**
     * Called when the mode of a known <code>dockable</code> has been
     * changed. This method is intended to be overriden and does not do
     * anything in its basic version.
     * @param dockable the element whose mode changed
     * @param oldMode the mode before the change
     * @param newMode the mode after the change
     */
    protected void modeChanged( Dockable dockable, String oldMode, String newMode ){
        
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
     * Gets the current set or properties.
     * @param <B> the type of the internal representation of the properties
     * @param converter converts the properties into the internal representation
     * @return the set of properties
     */
    public <B> ModeTransitionSetting<A, B> getSetting( ModeTransitionConverter<A, B> converter ){
        ModeTransitionSetting<A, B> setting = createSetting( converter );
        
        for( Map.Entry<String, Entry> element : entries.entrySet() ){
            String current = null;
            Entry entry = element.getValue();
            if( entry.dockable != null )
                current = currentMode( entry.dockable );
            
            setting.add(
                    entry.id, 
                    current,
                    entry.properties, 
                    entry.history );
        }
        
        return setting;
    }
    
    /**
     * Sets all properties of this manager. Registered elements which are not
     * present in <code>setting</code> will not be affected by this method.
     * @param setting the set of properties
     */
    public void setSetting( ModeTransitionSetting<A, ?> setting ){
        for( int i = 0, n = setting.size(); i < n; i++ ){
            String key = setting.getId( i );
            Entry entry = entries.get( key );
            if( entry != null ){
                String current = setting.getCurrent( i );
                String old = null;
                if( entry.dockable != null )
                    old = currentMode( entry.dockable );
                
                if( current == null )
                    current = old;
                
                entry.history.clear();
                for( String next : setting.getHistory( i ))
                    entry.history.add( next );
                
                entry.properties = setting.getProperties( i );
                
                if( (old == null && current != null) || (old != null && !old.equals( current ))){
                    transitionDuringRead( old, current, entry.dockable );
                }
            }
        }
    }
    
    /**
     * Creates a new, empty setting.
     * @param <B> the type of properties stored in the setting
     * @param converter used to convert properties of this manager to the properties of the setting
     * @return the new setting
     */
    protected <B> ModeTransitionSetting<A, B> createSetting( ModeTransitionConverter<A, B> converter ){
        return new ModeTransitionSetting<A, B>( converter );
    }
    
    /**
     * Writes the properties of this manager into <code>out</code>.
     * @param converter a converter that can write the properties of this manager.
     * @param out the stream to write into
     * @throws IOException if an I/O-error occurs
     */
    public <B> void write( ModeTransitionConverter<A,B> converter, DataOutputStream out ) throws IOException{
        getSetting( converter ).write( out );
    }
    
    /**
     * Reads the properties of this manager. This is equivalent then calling:
     * <pre>ModeTransitionSetting<A, B> setting = createSetting( converter );
     * setting.read( in );
     * setSetting( setting );</pre>
     * @param converter a converter that can read the properties
     * @param in the stream to read from
     * @throws IOException if the stream can't be read
     */
    public <B> void read( ModeTransitionConverter<A, B> converter, DataInputStream in ) throws IOException{
        ModeTransitionSetting<A, B> setting = createSetting( converter );
        setting.read( in );
        setSetting( setting );
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
        /** a unique id associated with {@link #dockable} */
        public String id;
        
        /** the set of actions available for {@link #dockable} */
        public DefaultDockActionSource source;
        /** a map that stores some properties mapped to the different modes */
        public Map<String, A> properties;

        /** The modes this entry already visited. No mode is more than once in this list. */
        private List<String> history;
        
        /**
         * Creates a new entry
         * @param dockable the element whose properties are stores in this entry
         * @param id the unique if of this entry
         */
        public Entry( Dockable dockable, String id ){
            this.dockable = dockable;
            this.id = id;
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
            String oldMode = peekMode();
            history.remove( mode );
            history.add( mode );
            rebuild( dockable );
            modeChanged( dockable, oldMode, mode );
        }
        
        /**
         * Gets the mode that was used previously to the current mode.
         * If the history gets empty, then {@link ModeTransitionManager#getDefaultMode(Dockable)}
         * is returned.
         * @return the mode in which this entry was before the current mode
         * was put onto the history
         */
        public String previousMode(){
            if( history.size() < 2 )
                return getDefaultMode( dockable );
            else
                return history.get( history.size()-2 );
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
