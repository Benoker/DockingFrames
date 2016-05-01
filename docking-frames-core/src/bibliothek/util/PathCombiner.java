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
package bibliothek.util;

/**
 * Generic algorithm to combine two {@link Path}s into one <code>Path</code>.
 * @author Benjamin Sigg
 */
public interface PathCombiner {
    /**
     * This combiner uses {@link Path#append(Path)} to combine its paths.
     */
    public static final PathCombiner APPEND = new PathCombiner(){
        public Path combine( Path first, Path second ) {
            return first.append( second );
        }
    };
    
    /**
     * This combiner uses {@link Path#uniqueAppend(Path)} to combine its paths.
     */
    public static final PathCombiner UNIQUE = new PathCombiner(){
        public Path combine( Path first, Path second ) {
            return first.uniqueAppend( second );
        }
    };
    
    /**
     * This combiner always discards the first path and just returns the second.
     */
    public static final PathCombiner SECOND = new PathCombiner(){
        public Path combine( Path first, Path second ) {
            return second;
        }
    };
    
    /**
     * Creates a combination of <code>first</code> and of <code>second</code>. It
     * is up to this combiner how the combination looks. This method must respect
     * only one condition: <code>combine( x, y ) = combine( x, y )</code>
     * must be true for all times.
     * @param first the first part of the path, not <code>null</code>
     * @param second the second part of the path, not <code>null</code>
     * @return the result, not <code>null</code>
     */
    public Path combine( Path first, Path second );
}
