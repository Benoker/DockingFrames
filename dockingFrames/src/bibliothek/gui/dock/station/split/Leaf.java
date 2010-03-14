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

import java.awt.Dimension;
import java.awt.Rectangle;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.split.SplitDockTree.Key;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;

/**
 * Represents a leaf in the tree that is the structure of a {@link SplitDockStation}.
 * A leaf also represents a single {@link Dockable} which is shown
 * on the owner-station.
 * @author Benjamin Sigg
 */
public class Leaf extends VisibleSplitNode{
	/** Information about the element that is shown by this leaf */
    private StationChildHandle handle;
    
    /**
     * Creates a new leaf.
     * @param access the access to the private functions of the owning {@link SplitDockStation}
     */
    public Leaf( SplitDockAccess access ){
        super( access, -1 );
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
    	if( handle == null )
    		return new Dimension( 0, 0 );
    	
    	DockableDisplayer displayer = handle.getDisplayer();
    	if( displayer == null )
    		return new Dimension( 0, 0 );
    	return displayer.getComponent().getMinimumSize();
    }
    
    @Override
    public int getChildLocation( SplitNode child ) {
        return -1;
    }
    
    @Override
    public void setChild( SplitNode child, int location ) {
        throw new IllegalStateException( "can't add children to a leaf" );
    }
    
    /**
     * Sets the element of this leaf. This method ensures that <code>dockable</code>
     * is registered in the {@link DockStation}
     * @param dockable the new element or <code>null</code> to remove the
     * old {@link Dockable}
     * @param fire whether to inform {@link DockStationListener}s about the
     * change or not. Clients should set fire = <code>true</code>.
     */
    public void setDockable( Dockable dockable, boolean fire ){
    	setDockable( dockable, fire, false, false );
    }
    
    /**
     * Sets the element of this leaf. This method ensures that <code>dockable</code>
     * is registered in the {@link DockStation}
     * @param dockable the new element or <code>null</code> to remove the
     * old {@link Dockable}
     * @param fire whether to inform {@link DockStationListener}s about the
     * change or not. Clients should set fire = <code>true</code>.
     * @param updatePlaceholders if <code>true</code>, the placeholder list of this leaf is
     * automatically updated
     * @param storePlaceholderMap if <code>true</code>, the current {@link PlaceholderMap} is
     * replaced by the map provided by the current {@link Dockable} which is a {@link DockStation}
     */
    public void setDockable( Dockable dockable, boolean fire, boolean updatePlaceholders, boolean storePlaceholderMap ){
    	PlaceholderStrategy strategy = null;
    	if( updatePlaceholders ){
			 strategy = getAccess().getOwner().getPlaceholderStrategy();
		}
		
    	if( handle != null ){
    		if( strategy != null ){
    			Path placeholder = strategy.getPlaceholderFor( handle.getDockable() );
    			if( placeholder != null ){
    				addPlaceholder( placeholder );
    			}
    		}
    		if( storePlaceholderMap ){
    			DockStation station = handle.getDockable().asDockStation();
    			if( station == null ){
    				throw new IllegalStateException( "no station as child but storePlaceholderMap is set" );
    			}
    			setPlaceholderMap( station.getPlaceholders() );
    		}
    		
    		getAccess().removeHandle( handle, fire );
    		handle = null;
    	}
    	
        if( dockable != null ){
        	if( strategy != null ){
        		Path placeholder = strategy.getPlaceholderFor( dockable );
        		if( placeholder != null ){
        			removePlaceholder( placeholder );
        		}
        	}
            handle = getAccess().addHandle( dockable, fire );
        }
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
    	return handle != null || hasPlaceholders();
    }
    
    /**
     * Disconnects this leaf from its {@link Dockable}. This leaf either deletes
     * itself or replaces itself with a {@link Placeholder}.
     */
    public void placehold(){
    	Dockable dockable = getDockable();
    	if( dockable != null ){
	    	SplitDockAccess access = getAccess();
	    	PlaceholderStrategy strategy = access.getOwner().getPlaceholderStrategy();
	    	Path placeholder = strategy.getPlaceholderFor( dockable );
	    	if( placeholder != null ){
	    		if( !hasPlaceholder( placeholder )){
	    			addPlaceholder( placeholder );
	    		}
	    	}
    	}
    	if( hasPlaceholders() ){
    		Placeholder placeholder = createPlaceholder( getId() );
    		placeholder.setPlaceholders( getPlaceholders() );
    		replace( placeholder );
    	}
    	else{
    		delete( true );
    	}
    }
    
    @Override
    public void updateBounds( double x, double y, double width, double height, double factorW, double factorH, boolean components ) {
        super.updateBounds( x, y, width, height, factorW, factorH, components );
        DockableDisplayer displayer = getDisplayer();
        
        if( components && displayer != null && displayer != getAccess().getFullScreenDockable() )
            displayer.getComponent().setBounds( getBounds() );
    }
        
    @Override
    public PutInfo getPut( int x, int y, double factorW, double factorH, Dockable drop ) {
    	DockableDisplayer displayer = getDisplayer();
        if( displayer == null )
            return null;
        
        Rectangle bounds = getBounds();
        PutInfo result = null;
        
        if( displayer.getTitle() != null ){
            if( displayer.getTitleLocation() == DockableDisplayer.Location.TOP ){
                int height = displayer.getTitle().getComponent().getHeight();
                bounds.y += height;
                bounds.height -= height;
                
                if( y <= bounds.y ){
                    result = getAccess().validatePutInfo( new PutInfo( this, PutInfo.Put.TITLE, drop ));
                }
            }
            
            else if( displayer.getTitleLocation() == DockableDisplayer.Location.BOTTOM ){
                int height = displayer.getTitle().getComponent().getHeight();
                bounds.height -= height;
                
                if( y >= bounds.y+bounds.height ){
                    result = getAccess().validatePutInfo( new PutInfo( this, PutInfo.Put.TITLE, drop ));
                }
            }
            
            else if( displayer.getTitleLocation() == DockableDisplayer.Location.LEFT ){
                int width = displayer.getTitle().getComponent().getWidth();
                bounds.x += width;
                bounds.width -= width;
                
                if( x <= bounds.x ){
                    result = getAccess().validatePutInfo( new PutInfo( this, PutInfo.Put.TITLE, drop ));
                }
            }
            
            else if( displayer.getTitleLocation() == DockableDisplayer.Location.RIGHT ){
                int width = displayer.getTitle().getComponent().getWidth();
                bounds.width -= width;
                
                if( x >= bounds.x + bounds.width ){
                    result = getAccess().validatePutInfo( new PutInfo( this, PutInfo.Put.TITLE, drop ));
                }
            }
        }
        
        if( result != null )
            return result;
        
        float sideSnapSize = getAccess().getOwner().getSideSnapSize();
        
        if( x > bounds.x + sideSnapSize*bounds.width && 
            x < bounds.x + bounds.width - sideSnapSize*bounds.width &&
            y > bounds.y + sideSnapSize*bounds.height &&
            y < bounds.y + bounds.height - sideSnapSize*bounds.height ){
            
            result = getAccess().validatePutInfo( new PutInfo( this, PutInfo.Put.CENTER, drop ));
        }
        
        if( result != null )
            return result;
        
        if( above( bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, x, y )){
            if( above( bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y, x, y ))
                result = getAccess().validatePutInfo( new PutInfo( this, PutInfo.Put.TOP, drop ));
            else
                result = getAccess().validatePutInfo(  new PutInfo( this, PutInfo.Put.RIGHT, drop ));
        }
        else{
            if( above( bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y, x, y ))
                result = getAccess().validatePutInfo(  new PutInfo( this, PutInfo.Put.LEFT, drop ));
            else
                result = getAccess().validatePutInfo(  new PutInfo( this, PutInfo.Put.BOTTOM, drop ));
        }
        
        if( result != null )
            return result;
        
        return getAccess().validatePutInfo( new PutInfo( this, PutInfo.Put.CENTER, drop ));
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
    public void evolve( Key key, boolean checkValidity ){
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
                        	removePlaceholder( placeholder );
                            return true;
                        }
                    }
                }
            }
            
            // try using the theoretical boundaries of the element
            boolean done = getAccess().drop( dockable, property.toSplitLocation( this ), this );
            if( done ){
            	removePlaceholder( placeholder );
            }
            return done;
    	}
    	return false;
    }
    
    @Override
    public boolean insert( SplitDockPathProperty property, int depth, Dockable dockable ) {
        if( depth < property.size() ){
            // split up the leaf
            Node split;
            SplitDockPathProperty.Node node = property.getNode( depth );
            
            SplitDockStation.Orientation orientation;
            if( node.getLocation() == SplitDockPathProperty.Location.LEFT ||
                    node.getLocation() == SplitDockPathProperty.Location.RIGHT )
                orientation = SplitDockStation.Orientation.HORIZONTAL;
            else
                orientation = SplitDockStation.Orientation.VERTICAL;
            
            boolean reverse = node.getLocation() == SplitDockPathProperty.Location.RIGHT ||
                node.getLocation() == SplitDockPathProperty.Location.BOTTOM;
            
            SplitDockPathProperty.Node lastNode = property.getLastNode();
            long id = -1;
            if( lastNode != null ){
            	id = lastNode.getId();
            }
            Leaf leaf = create( dockable, true, id );
            if( leaf == null )
                return false;
            
            SplitNode parent = getParent();
            int location = parent.getChildLocation( this );
            if( reverse ){
                split = new Node( getAccess(), this, leaf, orientation );
                split.setDivider( 1 - node.getSize() );
            }
            else{
                split = new Node( getAccess(), leaf, this, orientation );
                split.setDivider( node.getSize() );
            }
            
            parent.setChild( split, location );
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
        return factory.leaf( getDockable(), getId(), getPlaceholders(), getPlaceholderMap() );
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
        out.append( "id=" );
        out.append( getId() );
        out.append( " ]" );
    }
}