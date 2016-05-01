/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.extension.gui.dock.preference;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.util.Path;

/**
 * An implementation of {@link PreferenceModel} using {@link Preference}s to
 * describe its entries.
 * @author Benjamin Sigg
 */
public class DefaultPreferenceModel extends AbstractPreferenceModel{
    private List<Entry<?>> entries = new ArrayList<Entry<?>>();
    
    public DefaultPreferenceModel( DockController controller ){
    	super( controller );
    }
    
    public int getSize() {
        return entries.size();
    }
    
    public String getLabel( int index ) {
        return getPreference( index ).getLabel();
    }
    
    @Override
    public String getDescription( int index ) {
        return getPreference( index ).getDescription();
    }
    
    public Object getValueInfo(int index) {
    	return getPreference( index ).getValueInfo();
    }
    
    public Object getValue( int index ) {
        return getPreference( index ).getValue();
    }
    
    @SuppressWarnings("unchecked")
    public void setValue( int index, Object value ) {
        ((Preference)getPreference( index )).setValue( value );
    }
    
    public Path getTypePath( int index ) {
        return getPreference( index ).getTypePath();
    }
    
    public Path getPath( int index ) {
        return getPreference( index ).getPath();
    }
    
    @Override
    public boolean isNatural( int index ) {
        return getPreference( index ).isNatural();
    }
    
    @Override
    public void setValueNatural( int index ) {
        getPreference( index ).read();
    }
    
    @Override
    public void addPreferenceModelListener( PreferenceModelListener listener ){
    	if( !hasListeners() ){
    		for( Entry<?> entry : entries ){
    			entry.setListener( true );
    		}
    	}
    	super.addPreferenceModelListener( listener );
    }
    
    @Override
    public void removePreferenceModelListener( PreferenceModelListener listener ){
    	super.removePreferenceModelListener( listener );
    	if( !hasListeners() ){
    		for( Entry<?> entry : entries ){
    			entry.setListener( false );
    		}
    	}
    }
    
    /**
     * Gets the preference of location <code>index</code>.
     * @param index the location of the preference
     * @return the preference
     */
    public Preference<?> getPreference( int index ){
        return entries.get( index ).getPreference();
    }
    
    /**
     * Adds <code>preference</code> at the end of this model.
     * @param preference the preference to add
     */
    public void add( Preference<?> preference ){
        insert( entries.size(), preference );
    }
    
    /**
     * Adds a new preference to this model.
     * @param index the location of the new preference
     * @param preference the new preference
     */
    @SuppressWarnings("unchecked")
    public void insert( int index, Preference<?> preference ){
        if( preference == null )
            throw new IllegalArgumentException( "preference must not be null" );
        
        if( index < 0 || index > entries.size() )
            throw new ArrayIndexOutOfBoundsException( index );

        Path path = preference.getPath();
        for( Entry<?> entry : entries ){
            if( entry.getPreference().getPath().equals( path ))
                throw new IllegalArgumentException( "there is already a preference with path " + path );
        }
        
        Entry<?> entry = new Entry( preference, index );
        entries.add( index, entry );
        for( int i = index+1, n = entries.size(); i<n; i++ ){
            entries.get( i ).setIndex( i );
        }
        
        firePreferenceAdded( index, index );
    }
    
    /**
     * Removes the <code>index</code>'th preference of this model.
     * @param index the preference to remove
     */
    public void remove( int index ){
        Entry<?> entry = entries.remove( index );
        entry.kill();
        
        for( int i = index, n = entries.size(); i<n; i++ )
            entries.get( i ).setIndex( i );
        
        firePreferenceRemoved( index, index );
    }
    
    /**
     * Removes <code>preference</code> from this model.
     * @param preference the preference to remove
     */
    public void remove( Preference<?> preference ){
        int index = indexOf( preference );
        if( index >= 0 )
            remove( index );
    }
    
    /**
     * Removes all preferences of this model.
     */
    public void removeAll(){
        int size = entries.size();
        if( size > 0 ){
            for( Entry<?> entry : entries )
                entry.kill();

            entries.clear();
            firePreferenceRemoved( 0, size-1 );
        }
    }
    
    /**
     * Gets the location of <code>preference</code>.
     * @param preference the preference to access
     * @return the index of <code>preference</code> or -1
     */
    public int indexOf( Preference<?> preference ){
        for( int i = 0, n = entries.size(); i<n; i++ ){
            if( entries.get( i ).getPreference() == preference )
                return i;
        }
        
        return -1;
    }
    
    @Override
    public void read() {
        for( Entry<?> entry : entries ){
            entry.getPreference().read();
        }
    }
    
    @Override
    public void write() {
        for( Entry<?> entry : entries ){
            entry.getPreference().write();
        }
    }
    
    @Override
    public PreferenceOperation[] getOperations( int index ) {
        return entries.get( index ).getPreference().getOperations();
    }
    
    @Override
    public boolean isEnabled( int index, PreferenceOperation operation ) {
        return entries.get( index ).getPreference().isEnabled( operation );
    }
    
    @Override
    public void doOperation( int index, PreferenceOperation operation ) {
        entries.get( index ).getPreference().doOperation( operation );
    }
    
    /**
     * A single preference in this model.
     * @author Benjamin Sigg
     *
     * @param <V> the kind of value this preference uses
     */
    private class Entry<V> implements PreferenceListener<V>{
        private Preference<V> preference;
        private int index;
        
        /**
         * Creates a new entry.
         * @param preference the preference to observe
         * @param index the location of this preference
         */
        public Entry( Preference<V> preference, int index ){
            this.preference = preference;
            this.index = index;
            preference.setModel( DefaultPreferenceModel.this );
            if( hasListeners() ){
            	preference.addPreferenceListener( this );
            }
        }

        public void changed( Preference<V> preference ) {
            firePreferenceChanged( index, index );
        }
        
        /**
         * Sets whether <code>this</code> is listening to the changes
         * of {@link #preference} or not.
         * @param listening whether to listen
         */
        public void setListener( boolean listening ){
        	if( listening ){
        		preference.addPreferenceListener( this );
        	}
        	else{
        		preference.removePreferenceListener( this );
        	}
        }
        
        /**
         * Destroys this entry.
         */
        public void kill(){
        	preference.setModel( null );
        	if( hasListeners() ){
        		preference.removePreferenceListener( this );
        	}
        }
        
        /**
         * Gets the preference which this entry represents.
         * @return the preference
         */
        public Preference<V> getPreference() {
            return preference;
        }
        
        /**
         * Sets the location of this entry.
         * @param index the new location
         */
        public void setIndex( int index ) {
            this.index = index;
        }
    }
}
