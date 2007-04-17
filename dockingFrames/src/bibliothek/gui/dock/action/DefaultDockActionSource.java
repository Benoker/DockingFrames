/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.action;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.action.actions.SeparatorAction;

/**
 * The DefaultDockActionSource is nothing more than a list of 
 * {@link DockAction DockActions} that may be changed at any time.
 * @author Benjamin Sigg
 */
public class DefaultDockActionSource extends AbstractDockActionSource {
	/** the actions used in this source*/
    private List<DockAction> actions = new ArrayList<DockAction>();
    
    /** the preferred location of this source */
    private LocationHint hint;
    

    /**
     * Defaultconstructor, fills the list with some initial actions.
     * @param actions The actions to add
     */
    public DefaultDockActionSource( DockAction...actions ){
    	this( LocationHint.UNKNOWN, actions );
    }
    
    /**
     * Defaultconstructor, fills the list with some initial actions.
     * @param hint the preferred location of this source
     * @param actions The actions to add
     */
    public DefaultDockActionSource( LocationHint hint, DockAction...actions ){
        for( DockAction action : actions )
            this.actions.add( action );
        
        setHint( hint );
    }
    
    /**
     * Sets the location-hint of this source.
     * @param hint the hint that tells an {@link ActionOffer} where to
     * put this source.
     */
    public void setHint( LocationHint hint ){
    	if( hint == null )
    		throw new IllegalArgumentException( "Hint must not be null" );
		this.hint = hint;
	}
    
    public LocationHint getLocationHint(){
    	return hint;
    }
    
    public int getDockActionCount() {
        return actions.size();
    }

    public DockAction getDockAction( int index ) {
        return actions.get( index );
    }
    
    /**
     * Adds a separator at <code>position</code>
     * @param position the location where to add the separator
     */
    public void addSeparator( int position ){
        add( position, SeparatorAction.SEPARATOR );
    }
    
    /**
     * Adds a separator at the end of this source
     */
    public void addSeparator(){
        add( SeparatorAction.SEPARATOR );
    }
    
    /**
     * Adds all given actions to the end of this source. 
     * @param action The actions to append
     */
    public void add( DockAction...action ){
        add( getDockActionCount(), action );
    }
    
    /**
     * Inserts all given actions such that the first action
     * will have the given <code>index</code>, the second action
     * <code>index+1</code>, and so on...
     * @param index The index of the first action
     * @param actions The actions to insert
     */
    public void add( int index, DockAction...actions ){
        int firstIndex = index;
        for( DockAction action : actions ){
            this.actions.add( index++, action );
        }
        fireAdded( firstIndex, index-1 );
    }
    
    /**
     * Gets the index of the given {@link DockAction action}
     * @param action The action to search in this source
     * @return The index of the action, -1 if the action was not found
     */
    public int indexOf( DockAction action ){
        return actions.indexOf( action );
    }
    
    /**
     * Removes the given <code>action</code> from this source.
     * @param action The action to remove
     */
    public void remove( DockAction action ){
        int index = indexOf( action );
        if( index >= 0 )
            remove( index );
    }
    
    /**
     * Removes the action at <code>index</code> from this source. 
     * @param index The index of the action to remove
     */
    public void remove( int index ){
        remove( index, 1 );
    }
    
    /**
     * Removes all actions between <code>index</code> (incl.)
     * and <code>index+length</code> (excl.).
     * @param index The index of the first action to remove
     * @param length The number of actions to remove
     * @throws IllegalArgumentException If some actions should be removed that
     * do not exist
     */
    public void remove( int index, int length ){
        if( index < 0 )
            throw new IllegalArgumentException( "Index must not be negative" );
        
        if( length < 0 )
            throw new IllegalArgumentException( "Length must not be negative" );
        
        if( index + length > getDockActionCount() )
            throw new IllegalArgumentException( "index + length too great" );
        
        if( length > 0 ){
            for( int i = length-1; i>=0; i-- )
                actions.remove( index + i );
            
            fireRemoved( index, index+length-1 );
        }
    }
}
