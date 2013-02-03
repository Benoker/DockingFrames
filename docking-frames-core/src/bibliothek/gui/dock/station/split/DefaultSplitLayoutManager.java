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
package bibliothek.gui.dock.station.split;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.SwingUtilities;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.station.StationDropItem;

/**
 * The default implementation of {@link SplitLayoutManager}.
 * @author Benjamin Sigg
 */
public class DefaultSplitLayoutManager implements SplitLayoutManager{
    public void install( SplitDockStation station ) {
        // ignore
    }
    
    public void uninstall( SplitDockStation station ) {
        // ignore
    }
    
    public Dockable willMakeFullscreen( SplitDockStation station, Dockable dockable ) {
        return dockable;
    }
    
    public PutInfo prepareDrop( SplitDockStation station, StationDropItem item ){
        if( station.isFullScreen() )
            return null;
        
        if( station.getDockableCount() == 0 ){
            PutInfo putInfo = new PutInfo( null, PutInfo.Put.CENTER, item.getDockable(), true );
            putInfo = validatePutInfo( station, putInfo );
            return putInfo;
        }
        else{
            Point point = new Point( item.getMouseX(), item.getMouseY() );
            SwingUtilities.convertPointFromScreen( point, station );
            
            PutInfo putInfo = station.getRoot().getPut( point.x, point.y, item.getDockable() );
            
            if( putInfo == null && station.isAllowSideSnap() ){
                putInfo = calculateSideSnap( station, point.x, point.y, null, item.getDockable() );
                putInfo = validatePutInfo( station, putInfo );
            }
            
            if( putInfo != null ){
                putInfo.setDockable( item.getDockable() );
                calculateDivider( station, putInfo, null, item );
            }
            
            return putInfo;
        }
    }
    
    public PutInfo prepareMove( SplitDockStation station, StationDropItem item ){
        if( station.isFullScreen() )
            return null;
        
        Point point = new Point( item.getMouseX(), item.getMouseY() );
        SwingUtilities.convertPointFromScreen( point, station );
        
        Root root = station.getRoot();
        PutInfo putInfo = root.getPut( point.x, point.y, item.getDockable() );
        Leaf leaf = root.getLeaf( item.getDockable() );
        
        if( putInfo == null && station.isAllowSideSnap() ){
            putInfo = calculateSideSnap( station, point.x, point.y, leaf, item.getDockable() );
            putInfo = validatePutInfo( station, putInfo );
            if( putInfo != null ){
            	leaf = null;
            }
        }
        
        if( (putInfo != null) &&
            (putInfo.getNode() instanceof Leaf) &&
            (((Leaf)putInfo.getNode())).getDockable() == item.getDockable() ){
                putInfo.setNode( null );
        }
        
        if( putInfo != null ){
            putInfo.setDockable( item.getDockable() );
            calculateDivider( station, putInfo, leaf, item );
        }
        
        return putInfo;
    }
    
    
    /**
     * Calculates where to add a {@link Dockable} if the mouse is outside
     * this station.
     * @param station the station onto which <code>drop</code> might be dropped
     * @param x The x-coordinate of the mouse
     * @param y The y-coordinate of the mouse
     * @param leaf The leaf which was the old parent of the moved {@link Dockable} 
     * or <code>null</code>
     * @param drop the element that will be dropped
     * @return The preferred location or <code>null</code>
     */
    protected PutInfo calculateSideSnap( SplitDockStation station, int x, int y, Leaf leaf, Dockable drop ){
        if( station.getDockableCount() == 0 )
            return null;
        
        if( station.getDockableCount() == 1 && station.getDockable( 0 ) == drop )
        	return null;
        
        PutInfo info;
        
        if( SplitNode.above( 0, 0, station.getWidth(), station.getHeight(), x, y )){
            if( SplitNode.above( 0, station.getHeight(), station.getWidth(), 0, x, y )){
                // top
                info = new PutInfo( station.getRoot().getChild(), PutInfo.Put.TOP, drop, false );
            }
            else{
                // bottom
                info = new PutInfo( station.getRoot().getChild(), PutInfo.Put.RIGHT, drop, false );
            }
        }
        else{
            if( SplitNode.above( 0, station.getHeight(), station.getWidth(), 0, x, y )){
                // left
                info = new PutInfo( station.getRoot().getChild(), PutInfo.Put.LEFT, drop, false );
            }
            else{
                // right
                info = new PutInfo( station.getRoot().getChild(), PutInfo.Put.BOTTOM, drop, false );
            }            
        }
        
        if( leaf != null && station.getRoot().getChild() instanceof Node){
            Node node = (Node)station.getRoot().getChild();
            if( node.getLeft().isVisible() && node.getRight().isVisible() ){
	            if( info.getPut() == PutInfo.Put.TOP && node.getOrientation() == Orientation.VERTICAL && node.getLeft() == leaf )
	                return null;
	            
	            if( info.getPut() == PutInfo.Put.BOTTOM && node.getOrientation() == Orientation.VERTICAL && node.getRight() == leaf )
	                return null;
	            
	            if( info.getPut() == PutInfo.Put.LEFT && node.getOrientation() == Orientation.HORIZONTAL && node.getLeft() == leaf )
	                return null;
	            
	            if( info.getPut() == PutInfo.Put.RIGHT && node.getOrientation() == Orientation.HORIZONTAL && node.getRight() == leaf )
	                return null;
            }
        }
        
        return info;
    }
    
    public void calculateDivider( SplitDockStation station, PutInfo putInfo, Leaf origin, StationDropItem item ){
        final double MINIMUM_ORIGINAL_SIZE = 0.25;
        
        SplitNode other = putInfo.getNode();
        if( other == null ){
        	return;
        }
        
        Dimension oldSize = origin == null ? 
        		item.getOriginalSize() :
                origin.getSize();

		if( other.getParent() instanceof Root ){
        	other = other.getParent();
        }
        		
        Dimension nodeSize = other.getSize();
        
        int size = Math.min( oldSize.width, oldSize.height );

        if( origin != null ){
            if( origin.getParent() instanceof Node ){
                Node originParent = (Node)origin.getParent();
                
                if( (putInfo.getPut() == PutInfo.Put.LEFT || putInfo.getPut() == PutInfo.Put.RIGHT) && 
                        originParent.getOrientation() == Orientation.HORIZONTAL ){
                    size = oldSize.width;
                }
                else if( (putInfo.getPut() == PutInfo.Put.TOP || putInfo.getPut() == PutInfo.Put.BOTTOM) && 
                        originParent.getOrientation() == Orientation.VERTICAL){
                    size = oldSize.height;
                }
            }
        }
        else{
            if( putInfo.getOldSize() != 0 ){
                size = putInfo.getOldSize();
            }
        }
        
        double divider = 0.5;
        int dividerSize = station.getDividerSize();
        
        if( putInfo.getPut() == PutInfo.Put.TOP ){
            if( size != 0 )
                divider = (size + dividerSize/2.0) / nodeSize.height;
            
            divider = validateDivider( station, divider,
            		item.getMinimumSize(),
                    other.getMinimumSize(), 
                    Orientation.VERTICAL, 
                    other.getWidth(), other.getHeight() );
            
            if( divider > 1 - MINIMUM_ORIGINAL_SIZE )
                divider = 1 - MINIMUM_ORIGINAL_SIZE;
        }
        else if( putInfo.getPut() == PutInfo.Put.BOTTOM ){
            if( size != 0 )
                divider = 1.0 - (size + dividerSize/2.0) / nodeSize.height;

            divider = validateDivider( station, divider, 
                    other.getMinimumSize(),
                    item.getMinimumSize(),
                    Orientation.VERTICAL, 
                    other.getWidth(), other.getHeight() );
            
            if( divider < MINIMUM_ORIGINAL_SIZE )
                divider = MINIMUM_ORIGINAL_SIZE;
        }
        else if( putInfo.getPut() == PutInfo.Put.LEFT ){
            if( size != 0 )
                divider = (size + dividerSize/2.0) / nodeSize.width;
            
            divider = validateDivider( station, divider, 
            		item.getMinimumSize(),
                    other.getMinimumSize(), 
                    Orientation.HORIZONTAL, 
                    other.getWidth(), other.getHeight() );
            
            if( divider > 1 - MINIMUM_ORIGINAL_SIZE )
                divider = 1 - MINIMUM_ORIGINAL_SIZE;
        }
        else if( putInfo.getPut() == PutInfo.Put.RIGHT ){
            if( size != 0 )
                divider = 1.0 - (size + dividerSize/2.0) / nodeSize.width;
            
            divider = validateDivider( station, divider, 
                    other.getMinimumSize(), 
                    item.getMinimumSize(), 
                    Orientation.HORIZONTAL, 
                    other.getWidth(), other.getHeight() );
                        
            if( divider < MINIMUM_ORIGINAL_SIZE )
                divider = MINIMUM_ORIGINAL_SIZE;
        }
        
        putInfo.setDivider( divider );
        putInfo.setOldSize( size );
    }
    
    public double validateDivider( SplitDockStation station, double divider, Node node ){
        divider = Math.min( 1, Math.max( 0, divider ));
        
        SplitNode left = node.getLeft();
        SplitNode right = node.getRight();
        
        Dimension leftMin = null;
        Dimension rightMin = null;
        
        if( left != null ){
        	leftMin = left.getMinimumSize();
        }
        if( right != null ){
        	rightMin = right.getMinimumSize();
        }
        
        if( leftMin == null ){
        	leftMin = new Dimension();
        }
        if( rightMin == null ){
        	rightMin = new Dimension();
        }
        
        return validateDivider( station, divider, leftMin, rightMin, node.getOrientation(), node.getWidth(), node.getHeight() );
    }
    
    /**
     * Tests whether the specified <code>divider</code>-value is legal or not.
     * @param station the station for which the divider is intended
     * @param divider the value of a divider on a {@link Node}
     * @param minimumLeft the minimal number of pixels on the left or top side of the divider
     * @param minimumRight the minimal number of pixels on the right or bottom side of the divider
     * @param orientation the orientation of the divider
     * @param width the relative width of the base (in respect to the size of this station)
     * @param height the relative height of the base (in respect to the size of this station)
     * @return a legal value as near as possible to <code>divider</code>
     */
    protected double validateDivider( SplitDockStation station, double divider, Dimension minimumLeft, Dimension minimumRight, Orientation orientation, double width, double height ){
        double factor;
        double size;
        
        int left, right;
        
        if( orientation == Orientation.HORIZONTAL ){
            factor = station.getRoot().getWidthFactor();
            size = width;
            left = minimumLeft.width;
            right = minimumRight.width;
        }
        else{
            factor = station.getRoot().getHeightFactor();
            size = height;
            left = minimumLeft.height;
            right = minimumRight.height;
        }
        
        if( factor <= 0 || Double.isNaN( factor ))
            return divider;
        
        double leftNeed = left / factor;
        double rightNeed = right / factor;
        double dividerNeed = station.getDividerSize() / factor;

        if( leftNeed + rightNeed + dividerNeed >= size )
            divider = (leftNeed + dividerNeed / 2) / ( leftNeed + rightNeed + dividerNeed );
        else if( divider * size < leftNeed + dividerNeed / 2 )
            divider = (leftNeed + dividerNeed / 2) / size;
        else if( divider * size > size - rightNeed - dividerNeed / 2 )
            divider = (size - rightNeed - dividerNeed / 2) / size;
        
        return divider;
    }
    
    public PutInfo validatePutInfo( SplitDockStation station, PutInfo putInfo ){
        if( putInfo != null ){
            if( !station.accept( putInfo.getDockable() ))
                return null;
            
            if( putInfo.getNode() != null && (putInfo.getPut() == PutInfo.Put.CENTER || putInfo.getPut() == PutInfo.Put.TITLE )){
                if( !putInfo.getDockable().accept( station, ((Leaf)putInfo.getNode()).getDockable() ) ||
                        !((Leaf)putInfo.getNode()).getDockable().accept( station, putInfo.getDockable() ) ||
                        !station.getController().getAcceptance().accept( station, ((Leaf)putInfo.getNode()).getDockable(), putInfo.getDockable() )){
                    return null;
                }
            }
            else{
                if( !putInfo.getDockable().accept( station ) ||
                        !station.getController().getAcceptance().accept( station, putInfo.getDockable() )){
                    return null;
                }
            }
        }
        return putInfo;
    }
    
    public void updateBounds( Root root, double x, double y, double factorW, double factorH ) {
        root.updateBounds( x, y, 1, 1, factorW, factorH, true );
    }
}
