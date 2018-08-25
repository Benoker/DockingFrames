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

package bibliothek.gui.dock.station.split;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Map;

import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockHierarchyLock;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.location.AsideAnswer;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.split.PutInfo.Put;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderMap.Key;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.util.Path;

/**
 * Represents a leaf in the tree that is the structure of a {@link SplitDockStation}.
 * A leaf also represents a single {@link Dockable} which is shown
 * on the owner-station.
 * @author Benjamin Sigg
 */
public class Leaf extends SpanSplitNode{
	/** Information about the element that is shown by this leaf */
    private StationChildHandle handle;
    
    /**
     * Creates a new leaf.
     * @param access the access to the private functions of the owning {@link SplitDockStation}
     */
    public Leaf( SplitDockAccess access ){
        this( access, -1 );
    }
    
    /**
     * Creates a new leaf.
     * @param access the access to the private functions of the owning {@link SplitDockStation}
     * @param id the unique id of this leaf
     */
    public Leaf( SplitDockAccess access, long id ){
        super( access, id );
    }
    
    /**
     * Sets the element of this leaf.
     * @param handle the element
     */
    public void setHandle( StationChildHandle handle ){
		this.handle = handle;
	}
    
    @Override
    public Dimension getMinimumSize() {
    	SplitDockStation station = getStation();
    	DockableDisplayer displayer = null;
    	if( handle != null ){
    		displayer = handle.getDisplayer();
    	}
    	
    	if( displayer == null ){
	    	Dimension minimum;
	    	if( station == null ){
	    		minimum = new Dimension( 0, 0 );
	    	}
	    	else{
	    		minimum = station.getMinimumLeafSize();
	    	}
    		return minimum;
    	}
    	
    	return displayer.getComponent().getMinimumSize();
    }
    
    @Override
    public Dimension getPreferredSize(){
    	if( handle == null )
    		return new Dimension( 0, 0 );
    	
    	DockableDisplayer displayer = handle.getDisplayer();
    	if( displayer == null )
    		return new Dimension( 0, 0 );
    	return displayer.getComponent().getPreferredSize();
    }
    
    @Override
    public int getChildLocation( SplitNode child ) {
        return -1;
    }
    
    @Override
    public void setChild( SplitNode child, int location ) {
        throw new IllegalStateException( "can't add children to a leaf" );
    }
    
    @Override
    public int getMaxChildrenCount(){
    	return 0;
    }
    
    @Override
    public SplitNode getChild( int location ){
    	return null;
    }
    
    /**
     * Sets the element of this leaf. This method ensures that <code>dockable</code>
     * is registered in the {@link DockStation}
     * @param dockable the new element or <code>null</code> to remove the
     * old {@link Dockable}
     * @param token if <code>null</code>, then a token will be acquired by this method
     * and this method will fire events, otherwise this methods is executed silently. Clients should
     * use <code>null</code>.
     */
    public void setDockable( Dockable dockable, DockHierarchyLock.Token token ){
    	setDockable( dockable, token, true, false );
    }
    
    /**
     * Sets the element of this leaf. This method ensures that <code>dockable</code>
     * is registered in the {@link DockStation}
     * @param dockable the new element or <code>null</code> to remove the
     * old {@link Dockable}
     * @param token if <code>null</code>, then a token will be acquired by this method
     * and this method will fire events, otherwise this methods is executed silently
     * @param updatePlaceholders if <code>true</code>, the placeholder list of this leaf is
     * automatically updated
     * @param storePlaceholderMap if <code>true</code>, the current {@link PlaceholderMap} is
     * replaced by the map provided by the current {@link Dockable} which is a {@link DockStation}
     */
    public void setDockable( Dockable dockable, DockHierarchyLock.Token token, boolean updatePlaceholders, boolean storePlaceholderMap ){
    	if( handle != null ){
    		if( updatePlaceholders ){
    			getAccess().getPlaceholderSet().set( this, handle.getDockable() );
    		}
    		
    		if( storePlaceholderMap ){
    			DockStation station = handle.getDockable().asDockStation();
    			if( station == null ){
    				throw new IllegalStateException( "no station as child but storePlaceholderMap is set" );
    			}
    			
				PlaceholderMap placeholderMap = station.getPlaceholders();
				setPlaceholderMap( placeholderMap );

				// copy placeholders of children station to this leaf, if no other node handles the placeholders already
				if( placeholderMap != null ) {
					for( Key key : placeholderMap.getPlaceholders() ) {
						for( Path placeholder : key.getPlaceholders() ) {
							if( !getAccess().getPlaceholderSet().contains( placeholder ) ) {
								addPlaceholder( placeholder );
							}
						}
					}
				}
    		}
    		
    		getAccess().removeHandle( handle, token );
    		handle = null;
    	}
    	
        if( dockable != null ){
        	handle = getAccess().newHandle( dockable );
            
        	if( updatePlaceholders ){
        		getAccess().getPlaceholderSet().set( this, dockable );
        	}
        	
        	getAccess().addHandle( handle, token );
        }
        
        treeChanged();
    }
    
    /**
     * Gets the {@link Dockable} which is shown on the displayer
     * of this leaf.
     * @return the Dockable
     */
    public Dockable getDockable() {
        return handle == null ? null : handle.getDockable();
    }
    
    
    /**
     * Gets the displayer of this leaf.
     * @return the displayer
     */
    public DockableDisplayer getDisplayer(){
        return handle == null ? null : handle.getDisplayer();
    }
    
    /**
     * Gets the handle which is responsible for the current {@link Dockable}.
     * @return the handle, might be <code>null</code>
     */
    public StationChildHandle getDockableHandle(){
		return handle;
	}
    
    @Override
    public boolean isVisible(){
	    return true;
    }
    
    @Override
    public SplitNode getVisible(){
	    return this;
    }
    
    @Override
    public boolean isOfUse(){
    	if( !getAccess().isTreeAutoCleanupEnabled() ){
    		return true;
    	}
    	return handle != null || hasPlaceholders();
    }
    
    /**
     * Disconnects this leaf from its {@link Dockable}. This leaf either deletes
     * itself or replaces itself with a {@link Placeholder}.
     * @param keepCurrent if <code>true</code>, the placeholder of the current
     * {@link Dockable} is added to the set of the placeholders, otherwise the placeholder
     * is removed. 
     */
    public void placehold( boolean keepCurrent ){
    	Dockable dockable = getDockable();

    	if( dockable != null ){
    		SplitDockAccess access = getAccess();
        	PlaceholderStrategy strategy = access.getOwner().getPlaceholderStrategy();
        	if( strategy != null ){
        		updatePlaceholders( dockable, keepCurrent, strategy );
        	}
	    	
	    	DockStation station = dockable.asDockStation();
	    	if( station != null && keepCurrent ){
	    		setPlaceholderMap( station.getPlaceholders() );
	    	}
    	}
    	if( hasPlaceholders() ){
    		Placeholder placeholder = createPlaceholder( getId() );
    		placeholder.setPlaceholders( getPlaceholders() );
    		movePlaceholderMap( placeholder );
    		replace( placeholder );
    	}
    	else{
    		delete( true );
    	}
    }
    
    private void updatePlaceholders( Dockable dockable, boolean keep, PlaceholderStrategy strategy ){
    	Path placeholder = strategy.getPlaceholderFor( dockable );
    	if( placeholder != null ){
    		if( !keep ){
    			getAccess().getPlaceholderSet().set( null, placeholder );
    		}
    		else {
    			getAccess().getPlaceholderSet().set( this, placeholder );
    		}
    	}
    	
    	DockStation station = dockable.asDockStation();
    	if( station != null ){
    		for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
    			updatePlaceholders( station.getDockable( i ), keep, strategy );
    		}
    	}
    }
    
    @Override
    public void updateBounds( double x, double y, double width, double height, double factorW, double factorH, boolean components ) {
        super.updateBounds( x, y, width, height, factorW, factorH, components );
        if( components ){
        	resetDisplayerBounds();
        }
    }
    
    @Override
    public void setBounds( double x, double y, double width, double height, double factorW, double factorH, boolean updateComponentBounds ){
    	super.setBounds( x, y, width, height, factorW, factorH, updateComponentBounds );
    	if( updateComponentBounds ){
    		resetDisplayerBounds();
    	}
    }
    
    @Override
    public void onSpanResize(){
    	resetDisplayerBounds();
    }
    
    /**
     * Resets the boundaries of the {@link DockableDisplayer} of this {@link Leaf}, using the current {@link Span}s
     * as well.
     */
    public void resetDisplayerBounds(){
    	DockableDisplayer displayer = getDisplayer();
    	StationChildHandle fullscreen = getAccess().getFullScreenDockable();
        if( displayer != null && (fullscreen == null || displayer != fullscreen.getDisplayer() )){
        	Rectangle bounds = getBounds();
        	bounds = getAccess().getSpanStrategy().modifyBounds( bounds, this );
        	Component component = displayer.getComponent();
        	component.setBounds( bounds );
        	if( component instanceof JComponent ){
        		((JComponent)component).revalidate();
        	}
        }
    }
    
    @Override
    public PutInfo getPut( int x, int y, double factorW, double factorH, Dockable drop ) {
    	DockableDisplayer displayer = getDisplayer();
        if( displayer == null )
            return null;
        
        Rectangle bounds = getBounds();
        PutInfo result = null;
        boolean centered = false;
        
        bounds = removeTitle( bounds );
        
        if( isTitlePut( bounds, x, y )){
        	centered = true;
        	result = getAccess().validatePutInfo( new PutInfo( this, PutInfo.Put.TITLE, drop, true ));
        }
        
        if( result != null )
            return result;
        
        if( isCenterPut( bounds, x, y )){
        	centered = true;
            result = getAccess().validatePutInfo( new PutInfo( this, PutInfo.Put.CENTER, drop, true ));
        }
        
        if( result != null )
            return result;
        
        result = createSidePut( bounds, x, y, drop, centered );
        
        if( result != null )
            return result;
        
        return getAccess().validatePutInfo( new PutInfo( this, PutInfo.Put.CENTER, drop, centered ));
    }
    
    /**
     * Asks the {@link DockableDisplayer} for its current {@link DockTitle} and removes the size
     * of the title from <code>bounds</code>. Depending on the position of the title <code>bounds</code>
     * is moved, shrunk only horizontally or vertically.
     * @param bounds some boundaries, usually describing the boundaries of this {@link Leaf} but
     * any {@link Rectangle} can be modified by this method
     * @return a copy of <code>bounds</code> where the size of the title (if there is any) has been removed
     */
    protected Rectangle removeTitle( Rectangle bounds ){
    	DockableDisplayer displayer = getDisplayer();
    	bounds = new Rectangle( bounds );
    	
        if( displayer.getTitle() != null ){
            if( displayer.getTitleLocation() == DockableDisplayer.Location.TOP ){
                int height = displayer.getTitle().getComponent().getHeight();
                bounds.y += height;
                bounds.height -= height;
            }
            else if( displayer.getTitleLocation() == DockableDisplayer.Location.BOTTOM ){
                int height = displayer.getTitle().getComponent().getHeight();
                bounds.height -= height;
            }
            else if( displayer.getTitleLocation() == DockableDisplayer.Location.LEFT ){
                int width = displayer.getTitle().getComponent().getWidth();
                bounds.x += width;
                bounds.width -= width;
            }
            else if( displayer.getTitleLocation() == DockableDisplayer.Location.RIGHT ){
                int width = displayer.getTitle().getComponent().getWidth();
                bounds.width -= width;                
            }
        }
        
        return bounds;
    }

    /**
     * Tells whether the position of the mouse <code>x/y</code> would result in a drag and drop operation where
     * {@link Put#CENTER} is appropriate.
     * @param bounds the boundaries of the {@link Dockable}, this {@link Leaf} or any other representation of
     * the {@link Dockable}
     * @param x the x-coordinate of the mouse
     * @param y the y-coordinate of the mouse
     * @return whether the location of the mouse would allow a combination of the {@link Dockable}s
     */
    protected boolean isCenterPut( Rectangle bounds, int x, int y ){
        float sideSnapSize = getAccess().getOwner().getSideSnapSize();
        
        return x > bounds.x + sideSnapSize*bounds.width && 
            x < bounds.x + bounds.width - sideSnapSize*bounds.width &&
            y > bounds.y + sideSnapSize*bounds.height &&
            y < bounds.y + bounds.height - sideSnapSize*bounds.height;
    }
    
    /**
     * Tells whether the position of the mouse <code>x/y</code> would result in a drag and drop operation where
     * {@link Put#TITLE} is appropriate.
     * @param bounds the boundaries of the {@link Dockable}, this {@link Leaf} or any other representation of
     * the {@link Dockable}
     * @param x the x-coordinate of the mouse
     * @param y the y-coordinate of the mouse
     * @return whether the location of the mouse would allow a combination of the {@link Dockable}s
     */
    protected boolean isTitlePut( Rectangle bounds, int x, int y ){
    	DockableDisplayer displayer = getDisplayer();
    	
        if( displayer.getTitle() != null ){
        	if( displayer.getTitleLocation() == DockableDisplayer.Location.TOP ){
                return y <= bounds.y;
            }
            else if( displayer.getTitleLocation() == DockableDisplayer.Location.BOTTOM ){
                return y >= bounds.y+bounds.height;
            }
            else if( displayer.getTitleLocation() == DockableDisplayer.Location.LEFT ){
                return x <= bounds.x;
            }
            else if( displayer.getTitleLocation() == DockableDisplayer.Location.RIGHT ){
                return x >= bounds.x + bounds.width;
            }
        }
        
        return false;
    }
    
    /**
     * Assuming the mouse at <code>x/y</code> is within <code>bounds</code>, this method calculates which one
     * of the non-combining {@link Put}s describe the situation best. The method creates and validates a new
     * {@link PutInfo}.
     * @param bounds the boundaries of the {@link Dockable}, the {@link Leaf} or any other representation of the
     * {@link Dockable}.
     * @param x the x-coordinate of the mouse
     * @param y the y-coordinate of the mouse
     * @param drop the item that is about to be dropped
     * @param centered whether the mouse position alone would usually require one of the combining {@link Put}s
     * @return the new drag and drop operation, or <code>null</code> if the suggested operation is not valid
     */
    protected PutInfo createSidePut( Rectangle bounds, int x, int y, Dockable drop, boolean centered ){
        if( above( bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, x, y )){
            if( above( bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y, x, y ))
                return getAccess().validatePutInfo( new PutInfo( this, PutInfo.Put.TOP, drop, centered ));
            else
                return getAccess().validatePutInfo(  new PutInfo( this, PutInfo.Put.RIGHT, drop, centered ));
        }
        else{
            if( above( bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y, x, y ))
                return getAccess().validatePutInfo(  new PutInfo( this, PutInfo.Put.LEFT, drop, centered ));
            else
                return getAccess().validatePutInfo(  new PutInfo( this, PutInfo.Put.BOTTOM, drop, centered ));
        }
    }
    
    @Override
    public boolean isInOverrideZone( int x, int y, double factorW, double factorH ){
        float sideSnapSize = getAccess().getOwner().getSideSnapSize();
        Rectangle bounds = getBounds();
        
        if( x > bounds.x + sideSnapSize*bounds.width && 
            x < bounds.x + bounds.width - sideSnapSize*bounds.width &&
            y > bounds.y + sideSnapSize*bounds.height &&
            y < bounds.y + bounds.height - sideSnapSize*bounds.height ){
            
            return false;
        }
        
        return true;
    }
    
    @Override
    public void evolve( SplitDockTree<Dockable>.Key key, boolean checkValidity, Map<Leaf, Dockable> linksToSet ){
    	setPlaceholders( key.getTree().getPlaceholders( key ) );
    	setPlaceholderMap( key.getTree().getPlaceholderMap( key ) );
    }
    
    @Override
    public boolean insert( SplitDockPlaceholderProperty property, Dockable dockable ){
    	Path placeholder = property.getPlaceholder();
    	if( hasPlaceholder( placeholder )){
            // try to melt with child
            DockStation station = getDockable().asDockStation();
            DockableProperty stationLocation = property.getSuccessor();
            if( station != null && stationLocation != null ){
                if( dockable.accept( station ) && station.accept( dockable )){
                    DockController controller = getAccess().getOwner().getController();
                    DockAcceptance acceptance = controller == null ? null : controller.getAcceptance();
                    if( acceptance == null || acceptance.accept( station, dockable )){
                        boolean done = station.drop( dockable, stationLocation );
                        if( done ){
                        	getAccess().getPlaceholderSet().set( null, placeholder, this );
                            return true;
                        }
                    }
                }
            }
            
            // try using the theoretical boundaries of the element
            SplitDockProperty selfLocation = new SplitDockProperty( getX(), getY(), getWidth(), getHeight() );
            selfLocation.setSuccessor( property.getSuccessor() );
            boolean done = getAccess().drop( dockable, selfLocation, this );
            if( done ){
            	removePlaceholder( placeholder );
            }
            return done;
    	}
    	return false;
    }
    
    @Override
    public boolean aside( AsideRequest request ){
    	if( request.getPlaceholder() != null ){
    		addPlaceholder( request.getPlaceholder() );
	    	DockStation station = getDockable().asDockStation();
	    	if( station == null ){
	    		AsideAnswer answer = request.forward( getStation().getCombiner(), getPlaceholderMap() );
	    		if( answer.isCanceled() ){
	    			return false;
	    		}
	    		setPlaceholderMap( answer.getLayout() );
	    	}
	    	else{
	    		AsideAnswer answer = request.forward( station );
	    		if( answer.isCanceled() ){
	    			return false;
	    		}
	    	}
    	}
    	return true;
    }
    
    @Override
    public boolean aside( SplitDockPathProperty property, int index, AsideRequest request ){
    	if( request.getPlaceholder() != null ){
    		if( index < property.size() ){
    			DockStation station = getDockable().asDockStation();
	    		if( station == null ){
		    		Placeholder placeholder = createPlaceholder( property.getLeafId() );
		    		split( property, index, placeholder );
		    		return placeholder.aside( request );
	    		}
	    	}
	    	else{
	    		return aside( request );
	    	}
    	}
    	return true;
    }
    
    @Override
    public boolean insert( SplitDockPathProperty property, int depth, Dockable dockable ) {
        if( depth < property.size() ){
            // split up the leaf
        	Leaf leaf = create( dockable, property.getLeafId() );
        	if( leaf == null ){
        		return false;
        	}
        	split( property, depth, leaf );
        	leaf.setDockable( dockable, null );
        	return true;
        }
        else{
            // try to melt with child
            DockStation station = getDockable().asDockStation();
            DockableProperty stationLocation = property.getSuccessor();
            if( station != null && stationLocation != null ){
                if( dockable.accept( station ) && station.accept( dockable )){
                    DockController controller = getAccess().getOwner().getController();
                    DockAcceptance acceptance = controller == null ? null : controller.getAcceptance();
                    if( acceptance == null || acceptance.accept( station, dockable )){
                        boolean done = station.drop( dockable, stationLocation );
                        if( done )
                            return true;
                    }
                }
            }
            
            // try using the theoretical boundaries of the element
            return getAccess().drop( dockable, property.toLocation( this ), this );
        }
    }
    
    @Override
    public <N> N submit( SplitTreeFactory<N> factory ){
    	PlaceholderMap map = getPlaceholderMap();
    	if( map == null ){
    		Dockable dockable = getDockable();
    		if( dockable != null ){
    			DockStation station = dockable.asDockStation();
    			if( station != null ){
    				map = station.getPlaceholders();
    			}
    		}
    	}
    	
        return factory.leaf( getDockable(), getId(), getPlaceholders(), map );
    }
        
    @Override
    public Leaf getLeaf( Dockable dockable ) {
    	Dockable mine = getDockable();
    	
    	if( mine != null && dockable == getDockable() ){
    		return this;
    	}
    	else{
    		return null;
    	}
    }
    
    @Override
    public Node getDividerNode( int x, int y ){
        return null;
    }
    
    @Override
    public void visit( SplitNodeVisitor visitor ) {
        visitor.handleLeaf( this );
    }
    
    @Override
    public void toString( int tabs, StringBuilder out ) {
    	Dockable dockable = getDockable();
        out.append( "Leaf[ " );
        if( dockable != null ){
            out.append( dockable.getTitleText() );
            out.append( ", " );
        }
        out.append( "placeholders={" );
        Path[] placeholders = getPlaceholders();
        if( placeholders != null ){
        	for( int i = 0; i < placeholders.length; i++ ){
        		if( i > 0 ){
        			out.append( ", " );
        		}
        		out.append( placeholders[i].toString() );
        	}
        }
        out.append( "}, " );
        out.append( "id=" );
        out.append( getId() );
        out.append( ", bounds=" );
        out.append( getBounds() );
        out.append( " ]" );
    }
}
