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

package bibliothek.gui.dock.station.split;

import java.awt.Dimension;
import java.awt.Rectangle;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.split.SplitDockTree.Key;

/**
 * Represents a leaf in the tree that is the structure of a {@link SplitDockStation}.
 * A leaf also represents a single {@link Dockable} which is shown
 * on the owner-station.
 * @author Benjamin Sigg
 */
public class Leaf extends SplitNode{
	/** The displayer whose size and location is calculated by this leaf */
    private DockableDisplayer displayer;
    /** The Dockable on the displayer*/
    private Dockable dockable;
    
    /**
     * Creates a new leaf.
     * @param access the access to the private functions of the owning {@link SplitDockStation}
     */
    public Leaf( SplitDockAccess access ){
        super(access);
    }
    
    /**
     * Creates a new leaf.
     * @param access the access to the private functions of the owning {@link SplitDockStation}
     * @param displayer the displayer whose size and location will be determined by this leaf.
     */
    public Leaf( SplitDockAccess access, DockableDisplayer displayer ){
        super(access);
        setDisplayer( displayer );
    }
    
    @Override
    public Dimension getMinimumSize() {
    	if( displayer == null )
    		return new Dimension( 0, 0 );
    	return displayer.getComponent().getMinimumSize();
    }
    
    /**
     * Sets the displayer whose size and location will be determined by this leaf.
     * @param displayer the displayer, must not be <code>null</code>
     */
    public void setDisplayer( DockableDisplayer displayer ){
        this.displayer = displayer;
        dockable = displayer.getDockable();
    }
    
    @Override
    public void replace( SplitNode old, SplitNode child ) {
        throw new IllegalArgumentException( "not a child " + old );
    }
    
    /**
     * Gets the displayer of this leaf.
     * @return the displayer
     * @see #setDisplayer(DockableDisplayer)
     */
    public DockableDisplayer getDisplayer(){
        return displayer;
    }
    
    /**
     * Gets the {@link Dockable} which is shown on the displayer
     * of this leaf.
     * @return the Dockable
     */
    public Dockable getDockable() {
        return dockable;
    }
    
    @Override
    public void updateBounds( double x, double y, double width, double height, double factorW, double factorH ) {
        super.updateBounds( x, y, width, height, factorW, factorH );
        
        if( displayer != getAccess().getFullScreenDockable() )
            displayer.getComponent().setBounds( getBounds() );
    }
        
    @Override
    public PutInfo getPut( int x, int y, double factorW, double factorH, Dockable drop ) {            
        Rectangle bounds = getBounds();
        PutInfo result = null;
        
        if( displayer.getTitle() != null ){
            if( displayer.getTitleLocation() == DockableDisplayer.Location.TOP ){
                int height = displayer.getTitle().getComponent().getHeight();
                bounds.y += height;
                bounds.height -= height;
                
                if( y <= bounds.y ){
                    result = getAccess().checkPutInfo( new PutInfo( this, PutInfo.Put.TITLE, drop ));
                }
            }
            
            else if( displayer.getTitleLocation() == DockableDisplayer.Location.BOTTOM ){
                int height = displayer.getTitle().getComponent().getHeight();
                bounds.height -= height;
                
                if( y >= bounds.y+bounds.height ){
                    result = getAccess().checkPutInfo( new PutInfo( this, PutInfo.Put.TITLE, drop ));
                }
            }
            
            else if( displayer.getTitleLocation() == DockableDisplayer.Location.LEFT ){
                int width = displayer.getTitle().getComponent().getWidth();
                bounds.x += width;
                bounds.width -= width;
                
                if( x <= bounds.x ){
                    result = getAccess().checkPutInfo( new PutInfo( this, PutInfo.Put.TITLE, drop ));
                }
            }
            
            else if( displayer.getTitleLocation() == DockableDisplayer.Location.RIGHT ){
                int width = displayer.getTitle().getComponent().getWidth();
                bounds.width -= width;
                
                if( x >= bounds.x + bounds.width ){
                    result = getAccess().checkPutInfo( new PutInfo( this, PutInfo.Put.TITLE, drop ));
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
            
            result = getAccess().checkPutInfo( new PutInfo( this, PutInfo.Put.CENTER, drop ));
        }
        
        if( result != null )
            return result;
        
        if( above( bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, x, y )){
            if( above( bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y, x, y ))
                result = getAccess().checkPutInfo( new PutInfo( this, PutInfo.Put.TOP, drop ));
            else
                result = getAccess().checkPutInfo(  new PutInfo( this, PutInfo.Put.RIGHT, drop ));
        }
        else{
            if( above( bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y, x, y ))
                result = getAccess().checkPutInfo(  new PutInfo( this, PutInfo.Put.LEFT, drop ));
            else
                result = getAccess().checkPutInfo(  new PutInfo( this, PutInfo.Put.BOTTOM, drop ));
        }
        
        if( result != null )
            return result;
        
        return getAccess().checkPutInfo( new PutInfo( this, PutInfo.Put.CENTER, drop ));
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
    	// nothing to do
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
            
            Leaf leaf = create( dockable );
            if( leaf == null )
                return false;
            
            SplitNode parent = getParent();
            if( reverse ){
                split = new Node( getAccess(), this, leaf, orientation );
                split.setDivider( 1 - node.getSize() );
            }
            else{
                split = new Node( getAccess(), leaf, this, orientation );
                split.setDivider( node.getSize() );
            }
            
            parent.replace( this, split );
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
            return getAccess().drop( dockable, property.toLocation(), this );
        }
    }

    @Override
    public <N> N submit( SplitTreeFactory<N> factory ){
        return factory.leaf( getDockable() );
    }
        
    @Override
    public Leaf getLeaf( Dockable dockable ) {
        if( dockable == displayer.getDockable() )
            return this;
        else
            return null;
    }
    
    @Override
    public Node getDividerNode( int x, int y ){
        return null;
    }
    
    @Override
    public void visit( SplitNodeVisitor visitor ) {
        visitor.handleLeaf( this );
    }
}