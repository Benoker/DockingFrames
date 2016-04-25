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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bibliothek.gui.Dockable;

/**
 * An ActionOffer that collects some {@link DockActionSource DockActionSources}.
 * This ActionOffer tries to sort the sources by their {@link LocationHint},
 * and uses the {@link LocationHint.Origin Origin} if the 
 * {@link LocationHint.Hint Hint} does not carry enough information.
 * @author Benjamin Sigg
 */
public class DefaultActionOffer implements ActionOffer {
	/** whether separators should be inserted between different sources */
    private boolean separators = false;
    
    /** the preferred order of origins, from left to right  */
    private LocationHint.Origin[] origins;
    
    /** the preferred order of hints, from left to right */
    private LocationHint.Hint[] hints = new LocationHint.Hint[]{
    		LocationHint.LEFT_OF_ALL,
    		LocationHint.VERY_LEFT,
    		LocationHint.LEFT,
    		LocationHint.LITTLE_LEFT,
    		LocationHint.MIDDLE,
    		LocationHint.LITTLE_RIGHT,
    		LocationHint.RIGHT,
    		LocationHint.VERY_RIGHT,
    		LocationHint.RIGHT_OF_ALL
    };
    
    /**
     * Creates a new DefaultActionOffer.
     */
    public DefaultActionOffer(){
        this( 
        		LocationHint.INDIRECT_ACTION,
        		LocationHint.DIRECT_ACTION,
        		LocationHint.ACTION_GUARD,
        		LocationHint.DOCKABLE );
    }
    
    /**
     * Creates a new DefaultActionOffer. The order of <code>origin</code> is
     * used if several sources have the same preferred location hint.
     * @param origins The order of the {@link DockActionSource sources} with
     * equal location hint
     */
    public DefaultActionOffer( LocationHint.Origin... origins ){
        if( origins == null )
            throw new IllegalArgumentException( "Elements must not be null." );
        
        this.origins = origins;
    }
    
    /**
     * Sets the preferred order of sources according to their origin.
     * @param origins the preferred order
     */
    public void setOrigins( LocationHint.Origin[] origins ){
    	if( origins == null )
    		throw new IllegalArgumentException( "value must not be null" );
		this.origins = origins;
	}
    
    /**
     * Sets the preferred order of sources according to their hint.
     * @param hints the preferred order
     */
    public void setHints( LocationHint.Hint[] hints ){
    	if( hints == null )
    		throw new IllegalArgumentException( "value must not be null" );
		this.hints = hints;
	}
    
    /**
     * Whether there shall be separators between groups.
     * @param separators <code>true</code> if separators will be inserted
     */
    public void setSeparators( boolean separators ) {
        this.separators = separators;
    }
    
    /**
     * Gets whether there are separators between groups.
     * @return <code>true</code> if separators are inserted
     */
    public boolean isSeparators() {
        return separators;
    }
    
    public boolean interested( Dockable dockable ) {
        return true;
    }
    
    public DockActionSource getSource( Dockable dockable, DockActionSource source, DockActionSource[] guards, DockActionSource parent, DockActionSource[] parents ){
        MultiDockActionSource multiSource = new MultiDockActionSource();
        multiSource.setHint( new LocationHint( LocationHint.ACTION_OFFER, LocationHint.MIDDLE ));
        multiSource.setSeparateSources( isSeparators() );
        
        List<DockActionSource> sources = new ArrayList<DockActionSource>();
        
        if( source != null )
        	sources.add( source );
        
        if( guards != null ){
	        for( DockActionSource guard : guards )
	        	if( guard != null )
	        		sources.add( guard );
        }
        
        if( parent != null )
        	sources.add( parent );
        
        if( parents != null )
        	for( DockActionSource action : parents )
        		if( action != null )
        			sources.add( action );
        
        Collections.sort( sources, new Comparator<DockActionSource>(){
        	public int compare( DockActionSource a, DockActionSource b ){
        		int indexA = indexOf( hints, a.getLocationHint().getHint() );
        		int indexB = indexOf( hints, b.getLocationHint().getHint() );

        		if( indexA == indexB ){
        			indexA = indexOf( origins, a.getLocationHint().getOrigin() );
        			indexB = indexOf( origins, b.getLocationHint().getOrigin() );
        		}
        		
        		if( indexA < indexB )
        			return -1;
        		
        		if( indexA > indexB )
        			return 1;
        		
        		return 0;
        	}
        	
        	private <A> int indexOf( A[] array, A element ){
            	for( int i = 0; i < array.length; i++ )
            		if( element.equals( array[i] ))
            			return i;
            	
            	return -1;
            }
        });
        
        for( DockActionSource action : sources )
        	multiSource.add( action );
        
        return multiSource;
    }
}
