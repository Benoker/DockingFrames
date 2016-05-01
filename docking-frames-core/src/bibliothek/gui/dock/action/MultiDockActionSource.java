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

package bibliothek.gui.dock.action;

import java.util.ArrayList;
import java.util.Iterator;
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
    
    public Iterator<DockAction> iterator(){
    	return new Iterator<DockAction>(){
    		private Iterator<DockActionSource> sourceIterator = sources.iterator();
    		private Iterator<DockAction> actionIterator;
    		
			public boolean hasNext(){
				if( actionIterator == null ){
					if( sourceIterator.hasNext() )
						actionIterator = sourceIterator.next().iterator();
					else
						return false;
				}
				
				while( true ){
					if( actionIterator.hasNext() )
						return true;
					
					if( sourceIterator.hasNext() )
						actionIterator = sourceIterator.next().iterator();
					else
						return false;
				}
			}

			public DockAction next(){
				hasNext();
				return actionIterator.next();
			}

			public void remove(){
				hasNext();
				actionIterator.remove();
			}
    		
    	};
    }
    
    @Override
    public void addDockActionSourceListener( DockActionSourceListener listener ){
    	boolean empty = listeners.isEmpty();
    	super.addDockActionSourceListener( listener );
    	if( empty && !listeners.isEmpty() ){
    		for( DockActionSource source : sources )
    			source.addDockActionSourceListener( this.listener );
    		updateSeparators();
    	}
    }
    
    @Override
    public void removeDockActionSourceListener( DockActionSourceListener listener ){
    	boolean empty = listeners.isEmpty();
    	super.removeDockActionSourceListener( listener );
    	if( !empty && listeners.isEmpty() ){
    		for( DockActionSource source : sources )
    			source.removeDockActionSourceListener( this.listener );
    	}
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
        
        if( !listeners.isEmpty() ){
        	source.addDockActionSourceListener( listener );
        	separator.addDockActionSourceListener( listener );
        }
        
        int index = getDockActionCountUntil( sources.size()-2, false );
        int length = source.getDockActionCount();
        if( length > 0 )
            fireAdded( index, index+length-1 );
        
        updateSeparators();
    }
    
    /**
     * Removes <code>source</code> from this {@link MultiDockActionSource}.
     * @param source the child to remove
     */
    public void remove( DockActionSource source ){
    	int index = sources.indexOf( source );
    	if( index < 0 )
    		return;
    	
    	SeparatorSource separator = (SeparatorSource)sources.get( index+1 );
    	
    	int actionIndex = getDockActionCountUntil( index, false );
    	int length = source.getDockActionCount();
    	
    	sources.remove( index+1 );
    	sources.remove( index );
    	separators.remove( separator );
    	
    	if( !listeners.isEmpty() ){
    		source.removeDockActionSourceListener( listener );
    		separator.removeDockActionSourceListener( listener );
    	}
    	
    	if( length > 0 ){
    		fireRemoved( actionIndex, index+length-1 );
    	}
    	
    	updateSeparators();
    }
    
    /**
     * Removes all children of this source.
     */
    public void removeAll(){
    	int length = getDockActionCount();
    	if( !listeners.isEmpty() ){
    		for( SeparatorSource source : separators ){
    			source.removeDockActionSourceListener( listener );
    		}
    		for( DockActionSource source : sources ){
    			source.removeDockActionSourceListener( listener );
    		}
    	}
    	separators.clear();
    	sources.clear();
    	
    	if( length > 0 ){
    		fireRemoved( 0, length-1 );
    	}
    }
    
    /**
     * Adds several actions to this source.
     * @param actions the new actions
     */
    public void add( DockAction... actions ){
        add( new DefaultDockActionSource( actions ));
    }
    
    public int getDockActionCount(){
        return getDockActionCountUntil( sources.size(), true );
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
     * @param allowUpdate whether the {@link #updateSeparators()} can be called
     * by this method or not
     * @return the number of actions of the first <code>index</code>
     * child-sources.
     */
    protected int getDockActionCountUntil( int index, boolean allowUpdate ){
    	if( allowUpdate && listeners.isEmpty() )
    		updateSeparators();
    	
        int sum = 0;
        
        for( int i = 0; i < index; i++ )
            sum += sources.get( i ).getDockActionCount();
        
        return sum;
    }

    public DockAction getDockAction( int index ) {
    	if( listeners.isEmpty() )
    		updateSeparators();
    	
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
     * Ensures that all separators which must be visible are really visible.
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
            int index = getDockActionCountUntil( sources.indexOf( source ), false );
            fireAdded( firstIndex + index, lastIndex + index );
            updateSeparators();
        }

        public void actionsRemoved( DockActionSource source, int firstIndex, int lastIndex ) {
            int index = getDockActionCountUntil( sources.indexOf( source ), false );
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
