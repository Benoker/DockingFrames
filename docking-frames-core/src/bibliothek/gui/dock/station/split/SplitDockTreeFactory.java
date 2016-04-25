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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;

/**
 * A {@link SplitTreeFactory} that writes into a {@link SplitDockTree} and
 * so fills up an empty <code>SplitDockTree</code> until it is a full, valid
 * tree.
 * @author Benjamin Sigg
 */
public class SplitDockTreeFactory implements SplitTreeFactory<SplitDockTree<Dockable>.Key>{
    /** the tree to write into */
    private SplitDockTree<Dockable> tree;
    
    /**
     * Creates a new factory.
     * @param tree the tree into which this factory will write.
     */
    public SplitDockTreeFactory( SplitDockTree<Dockable> tree ){
        if( tree == null )
            throw new NullPointerException( "tree must not be null" );
        this.tree = tree;
    }
    
    public SplitDockTree<Dockable>.Key horizontal( SplitDockTree<Dockable>.Key left, SplitDockTree<Dockable>.Key right, double divider, long id, Path[] placeholders, PlaceholderMap placeholderMap, boolean visible ){
        return tree.horizontal( left, right, divider, placeholders, placeholderMap, id );
    }
    
    public SplitDockTree<Dockable>.Key vertical( SplitDockTree<Dockable>.Key top, SplitDockTree<Dockable>.Key bottom, double divider, long id, Path[] placeholders, PlaceholderMap placeholderMap, boolean visible ){
        return tree.vertical( top, bottom, divider, placeholders, placeholderMap, id );
    }
    
    public SplitDockTree<Dockable>.Key leaf( Dockable dockable, long id, Path[] placeholders, PlaceholderMap placeholderMap ){
        return tree.put( new Dockable[]{ dockable }, null, placeholders, placeholderMap, id );
    }

    public SplitDockTree<Dockable>.Key placeholder( long id, Path[] placeholders, PlaceholderMap placeholderMap ){
	    return tree.put( placeholders, placeholderMap );
    }

    public SplitDockTree<Dockable>.Key root( SplitDockTree<Dockable>.Key root, long id ){
        if( root == null )
            return null;
        
        return tree.root( root ).getRoot();
    }

}
