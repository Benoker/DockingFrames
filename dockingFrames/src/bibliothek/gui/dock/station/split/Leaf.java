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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DockAcceptance;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.station.SplitDockStation;
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
    	return displayer.getMinimumSize();
    }
    
    /**
     * Sets the displayer whose size and location will be determined by this leaf.
     * @param displayer the displayer, must not be <code>null</code>
     */
    public void setDisplayer( DockableDisplayer displayer ){
        this.displayer = displayer;
        dockable = displayer.getDockable();
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
            displayer.setBounds( getBounds() );
    }
        
    @Override
    public PutInfo getPut( int x, int y, double factorW, double factorH, Dockable drop ) {            
        Rectangle bounds = getBounds();
        
        if( displayer.getTitle() != null ){
            if( displayer.getTitleLocation() == DockableDisplayer.Location.TOP ){
                int height = displayer.getTitle().getComponent().getHeight();
                bounds.y += height;
                bounds.height -= height;
                
                if( y <= bounds.y ){
                    return combinationalPut( PutInfo.Put.TITLE, drop );
                }
            }
            
            if( displayer.getTitleLocation() == DockableDisplayer.Location.BOTTOM ){
                int height = displayer.getTitle().getComponent().getHeight();
                bounds.height -= height;
                
                if( y >= bounds.y+bounds.height ){
                    return combinationalPut( PutInfo.Put.TITLE, drop );
                }
            }
            
            if( displayer.getTitleLocation() == DockableDisplayer.Location.LEFT ){
                int width = displayer.getTitle().getComponent().getWidth();
                bounds.x += width;
                bounds.width -= width;
                
                if( x <= bounds.x ){
                    return combinationalPut( PutInfo.Put.TITLE, drop );
                }
            }
            
            if( displayer.getTitleLocation() == DockableDisplayer.Location.RIGHT ){
                int width = displayer.getTitle().getComponent().getWidth();
                bounds.width -= width;
                
                if( x >= bounds.x + bounds.width ){
                    return combinationalPut( PutInfo.Put.TITLE, drop );
                }
            }
        }
        
        
        float sideSnapSize = getAccess().getOwner().getSideSnapSize();
        
        if( x > bounds.x + sideSnapSize*bounds.width && 
            x < bounds.x + bounds.width - sideSnapSize*bounds.width &&
            y > bounds.y + sideSnapSize*bounds.height &&
            y < bounds.y + bounds.height - sideSnapSize*bounds.height ){
            
            return combinationalPut( PutInfo.Put.CENTER, drop );
        }
        
        if( above( bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, x, y )){
            if( above( bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y, x, y ))
                return new PutInfo( this, PutInfo.Put.TOP );
            else
                return new PutInfo( this, PutInfo.Put.RIGHT );
        }
        else{
            if( above( bounds.x, bounds.y + bounds.height, bounds.x + bounds.width, bounds.y, x, y ))
                return new PutInfo( this, PutInfo.Put.LEFT );
            else
                return new PutInfo( this, PutInfo.Put.BOTTOM );
        }
    }
    
    /**
     * Creates a new instance of {@link PutInfo} if <code>drop</code>
     * no acceptance-test fails between the station an the Dockable.
     * @param put the location where to put <code>drop</code>.
     * @param drop the Dockable to drop
     * @return the new PutInfo or <code>null</code> if at least one
     * test fails.
     */
    private PutInfo combinationalPut( PutInfo.Put put, Dockable drop ){
        SplitDockStation parent = getAccess().getOwner();
        Dockable dockable = getDockable();
        DockController controller = parent.getController();
        DockAcceptance acceptance = controller == null ? null : controller.getAcceptance();
        
        if( drop.accept( parent, dockable ) &&
                dockable.accept( parent, drop ) &&
                ( acceptance == null ||
                        acceptance.accept( parent, dockable, drop )))
            return new PutInfo( this, put );
        else
            return null;
    }
    
    @Override
    public void evolve( Key key ){
    	// nothing to do
    }
    
    @Override
    public Key submit( SplitDockTree tree ){
    	return tree.put( getDockable() );
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
    
    @Override
    public SplitNode read( Map<Integer, Dockable> children, DataInputStream in ) throws IOException {
        Dockable dockable = children.get( in.readInt() );
        if( dockable == null )
            return null;
        
        setDisplayer( getAccess().getOwner().getDisplayers().fetch( dockable, null ));
        getAccess().add( displayer );
        
        return this;
    }
    
    @Override
    public void write( Map<Dockable, Integer> children, DataOutputStream out ) throws IOException {
        out.writeInt( children.get( getDockable() ) );
    }
}