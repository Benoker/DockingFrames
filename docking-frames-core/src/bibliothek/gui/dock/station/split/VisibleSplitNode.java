/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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

import java.awt.Rectangle;

import bibliothek.gui.dock.SplitDockStation;

/**
 * Represents a {@link SplitNode} that is visible to the user.
 * @author Benjamin Sigg
 */
public abstract class VisibleSplitNode extends SplitNode{
	/** the current bounds of this node */
    private Rectangle currentBounds = new Rectangle();
	
    /**
     * Creates a new node.
     * @param access access to the {@link SplitDockStation}
     * @param id the unique identifier of this node
     */
    protected VisibleSplitNode( SplitDockAccess access, long id ){
		super( access, id );
	}
    
    @Override
    public void updateBounds( double x, double y, double width,  double height, double factorW, double factorH, boolean updateComponentBounds ){
        super.updateBounds( x, y, width, height, factorW, factorH, updateComponentBounds );
        getAccess().getOwner().revalidate();
        currentBounds = getBounds();
    }
    
    
    /**
     * Gets the current bounds of this root. The difference between the current
     * bounds and the value {@link #getBounds()} is, that the current bounds are
     * cached. The current bounds are calculated every time when 
     * {@link #updateBounds(double, double, double, double, double, double, boolean) updateBounds} 
     * is called, and then remain until the bounds are updated again.
     * @return the current bounds
     */
    public Rectangle getCurrentBounds() {
        return currentBounds;
    }
}
