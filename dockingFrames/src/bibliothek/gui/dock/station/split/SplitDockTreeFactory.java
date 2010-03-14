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

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.split.SplitDockTree.Key;
import bibliothek.gui.dock.station.support.PlaceholderMap;

/**
 * A {@link SplitTreeFactory} that writes into a {@link SplitDockTree} and
 * so fills up an empty <code>SplitDockTree</code> until it is a full, valid
 * tree.
 * @author Benjamin Sigg
 */
public class SplitDockTreeFactory implements SplitTreeFactory<SplitDockTree.Key>{
    /** the tree to write into */
    private SplitDockTree tree;
    
    /**
     * Creates a new factory.
     * @param tree the tree into which this factory will write.
     */
    public SplitDockTreeFactory( SplitDockTree tree ){
        if( tree == null )
            throw new NullPointerException( "tree must not be null" );
        this.tree = tree;
    }
    
    public Key horizontal( Key left, Key right, double divider, long id, Path[] placeholders, PlaceholderMap placeholderMap, boolean visible ){
        return tree.horizontal( left, right, divider, placeholders, placeholderMap, id );
    }
    
    public Key vertical( Key top, Key bottom, double divider, long id, Path[] placeholders, PlaceholderMap placeholderMap, boolean visible ){
        return tree.vertical( top, bottom, divider, placeholders, placeholderMap, id );
    }
    
    public Key leaf( Dockable dockable, long id, Path[] placeholders, PlaceholderMap placeholderMap ){
        return tree.put( new Dockable[]{ dockable }, null, placeholders, placeholderMap, id );
    }

    public Key placeholder( long id, Path[] placeholders, PlaceholderMap placeholderMap ){
	    return tree.put( placeholders, placeholderMap );
    }

    public Key root( Key root, long id ){
        if( root == null )
            return null;
        
        return tree.root( root ).getRoot();
    }

}
