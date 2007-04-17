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
import bibliothek.gui.dock.event.DockActionSourceListener;

/**
 * A {@link DockActionSource} that combines various sources in one source.
 * This source behaves like a list of {@link DockActionSource DockActionSources}.
 * @author Benjamin Sigg
 */
public class MultiDockActionSource extends AbstractDockActionSource {
    private List<DockActionSource> sources = new ArrayList<DockActionSource>();
    private List<SeparatorSource> separators = new ArrayList<SeparatorSource>();
    
    private Listener listener;
    private boolean separateSources = false;
    private LocationHint hint;
    
    /**
     * Constructs a new source. The <code>sources</code> are added as children
     * of this source.
     * @param sources The children of this source
     */
    public MultiDockActionSource( DockActionSource...sources ){
    	this( LocationHint.UNKNOWN, sources );
    }
    
    /**
     * Constructs a new source. The <code>sources</code> are added as children
     * of this source.
     * @param hint the preferred location of this source
     * @param sources The children of this source
     */
    public MultiDockActionSource( LocationHint hint, DockActionSource...sources ){
        listener = new Listener();
        
        for( DockActionSource source : sources ){
            this.sources.add( source );
        }
        
        setHint( hint );
    }
    
    public LocationHint getLocationHint(){
    	return hint;
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
    
    @Override
    public void addDockActionSourceListener( DockActionSourceListener listener ) {
        super.addDockActionSourceListener(listener);
    }
    @Override
    public void removeDockActionSourceListener( DockActionSourceListener listener ) {
        super.removeDockActionSourceListener(listener);
    }
    
    /**
     * Adds a separator at the end of the current list of actions
     */
    public void addSeparator(){
        add( SeparatorAction.SEPARATOR );
    }
    
    /**
     * Tells whether there is a separator between sources or not
     * @return <code>true</code> if there is a separator
     */
    public boolean isSeparateSources() {
        return separateSources;
    }
    
    /**
     * Sets whether there are separators between the children of this 
     * source or not.
     * @param separateSources <code>true</code> if children should be separated
     */
    public void setSeparateSources( boolean separateSources ) {
        if( this.separateSources != separateSources ){
            this.separateSources = separateSources;
            updateSeparators();
        }
    }
    
    /**
     * Adds a source as child of this source. All {@link DockAction DockActions}
     * of <code>source</code> will be presented as actions of this source.<br>
     * Note: creating circles or adding a source more than once will lead to 
     * unspecified behavior.
     * @param source the new child
     */
    public void add( DockActionSource source ){
    	SeparatorSource separator = new SeparatorSource( source );
    	
        sources.add( source );
        sources.add( separator );
        separators.add( separator );
        
        source.addDockActionSourceListener( listener );
        separator.addDockActionSourceListener( listener );
        
        int index = getDockActionCountUntil( sources.size()-1 );
        int length = source.getDockActionCount();
        if( length > 0 )
            fireAdded( index, index+length-1 );
        
        updateSeparators();
    }
    
    /**
     * Adds several actions to this source.
     * @param actions the new actions
     */
    public void add( DockAction... actions ){
        add( new DefaultDockActionSource( actions ));
    }
    
    public int getDockActionCount(){
        return getDockActionCountUntil( sources.size() );
    }
    
    /**
     * Gets the index of the child-source which contains <code>action</code>.
     * @param action the action for which is searched
     * @return the index of the source which contains the action or -1
     */
    protected int getSource( DockAction action ){
        for( int i = 0, n = sources.size(); i<n; i++ ){
            DockActionSource source = sources.get( i );
            for( int j = 0, m = source.getDockActionCount(); j<m; j++ ){
                if( source.getDockAction( j ) == action )
                    return i;
            }
        }
        
        return -1;
    }
    
    /**
     * Counts how many {@link DockAction DockActions} are provided by the
     * source-children with index 0 (incl) to <code>index</code> (excl).
     * @param index the index of the first source that should not be counted
     * @return the number of actions of the first <code>index</code>
     * child-sources.
     */
    protected int getDockActionCountUntil( int index ){
        int sum = 0;
        
        for( int i = 0; i < index; i++ )
            sum += sources.get( i ).getDockActionCount();
        
        return sum;
    }

    public DockAction getDockAction( int index ) {
        int sum = 0;
        for( int i = 0, n = sources.size(); i<n; i++ ){
            int length = sources.get( i ).getDockActionCount();
            if( sum <= index && index < sum + length )
                return sources.get( i ).getDockAction( index - sum );
            
            sum += length;
        }
        
        throw new ArrayIndexOutOfBoundsException();
    }
    
    /**
     * Ensures that all separators which must be visible are realy visible.
     */
    private void updateSeparators(){
    	int size = separators.size();
    	int index = 0;
    	
    	for( SeparatorSource source : separators ){
    		source.update( ++index == size );
    	}
    }
    
    /**
     * A listener to the sources of this group of sources.
     * @author Benjamin Sigg
     */
    private class Listener implements DockActionSourceListener{
        public void actionsAdded( DockActionSource source, int firstIndex, int lastIndex ) {
            int index = getDockActionCountUntil( sources.indexOf( source ));
            fireAdded( firstIndex + index, lastIndex + index );
            updateSeparators();
        }

        public void actionsRemoved( DockActionSource source, int firstIndex, int lastIndex ) {
            int index = getDockActionCountUntil( sources.indexOf( source ));
            fireRemoved( firstIndex + index, lastIndex + index );
            updateSeparators();
        }
    }
    
    /**
     * A source that shows one separator. 
     * @author Benjamin Sigg
     */
    private class SeparatorSource extends DefaultDockActionSource{
    	private DockActionSource predecessor;
    	
    	public SeparatorSource( DockActionSource predecessor ){
    		this.predecessor = predecessor;
    	}
    	
    	public void update( boolean last ){
    		if( !separateSources || last )
    			remove( SeparatorAction.SEPARATOR );
    		else if( predecessor.getDockActionCount() > 0 && getDockActionCount() == 0 )
    			add( SeparatorAction.SEPARATOR );
    	}
    }
}
