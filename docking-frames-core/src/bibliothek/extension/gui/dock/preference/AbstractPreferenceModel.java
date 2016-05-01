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

/**
 * An abstract implementation of {@link PreferenceModel} handling events and
 * returning <code>null</code> in most methods.
 * @author Benjamin Sigg
 *
 */
public abstract class AbstractPreferenceModel implements PreferenceModel{
    /** the list of known listeners */
    private List<PreferenceModelListener> listeners = new ArrayList<PreferenceModelListener>();
    
    /** the controller in whose realm this model works */
    private DockController controller;
    
    public AbstractPreferenceModel( DockController controller ){
    	this.controller = controller;
    }
    
    public DockController getController(){
	    return controller;
    }
    
    public void read() {
        // do nothing
    }
    
    public void write() {
        // do nothing
    }
    
    public boolean isEnabled( int index, PreferenceOperation operation ) {
        return false;
    }
    
    public PreferenceOperation[] getOperations( int index ) {
        return null;
    }
    
    public void doOperation( int index, PreferenceOperation operation ) {
        // do nothing
    }
    
    public void addPreferenceModelListener( PreferenceModelListener listener ) {
        if( listener == null )
            throw new IllegalArgumentException( "listener must not be null" );
        listeners.add( listener );
    }
    
    public void removePreferenceModelListener( PreferenceModelListener listener ) {
        listeners.remove( listener );
    }
    
    /**
     * Gets a list of all listeners registered at this model.
     * @return the list of listeners
     */
    protected PreferenceModelListener[] listeners(){
        return listeners.toArray( new PreferenceModelListener[ listeners.size() ] );
    }
    
    /**
     * Tells whether this model has listeners attached or not.
     * @return <code>true</code> if there are listeners
     */
    protected boolean hasListeners(){
        return !listeners.isEmpty();
    }
    
    /**
     * Informs all listeners that some preferences were added.
     * @param beginIndex the index of the first new preference
     * @param endIndex the index of the last new preference
     */
    protected void firePreferenceAdded( int beginIndex, int endIndex ){
        for( PreferenceModelListener listener : listeners ){
            listener.preferenceAdded( this, beginIndex, endIndex );
        }
    }
    
    /**
     * Informs all listeners that some preferences were removed.
     * @param beginIndex the index of the first removed preference
     * @param endIndex the index of the last removed preference
     */
    protected void firePreferenceRemoved( int beginIndex, int endIndex ){
        for( PreferenceModelListener listener : listeners ){
            listener.preferenceRemoved( this, beginIndex, endIndex );
        }
    }
    
    /**
     * Informs all listeners that some preferences were changed.
     * @param beginIndex the index of the first changed preference
     * @param endIndex the index of the last changed preference
     */
    protected void firePreferenceChanged( int beginIndex, int endIndex ){
        for( PreferenceModelListener listener : listeners ){
            listener.preferenceChanged( this, beginIndex, endIndex );
        }
    }
    
    public String getDescription( int index ) {
        return null;
    }
    
    public boolean isNatural( int index ) {
        return false;
    }
    
    public void setValueNatural( int index ) {
        // ignore
    }
}
