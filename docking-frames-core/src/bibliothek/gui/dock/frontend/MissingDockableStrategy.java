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
package bibliothek.gui.dock.frontend;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;

/**
 * Used by the {@link DockFrontend} to handle missing {@link Dockable}s. The
 * methods of this interface are called to find out, for which missing {@link Dockable}
 * information should be stored, and for which not.
 * @author Benjamin Sigg
 */
public interface MissingDockableStrategy {
    /**
     * This strategy throws away all information.
     */
    public static MissingDockableStrategy DISCARD_ALL = new MissingDockableStrategy(){
        public boolean shouldStoreHidden( String key ) {
            return false;
        }
        public boolean shouldStoreShown( String key ) {
            return false;
        }
        public <L> boolean shouldCreate( DockFactory<?,?,L> factory, L data ) {
            return false;
        }
    };
    
    /**
     * This strategy stores all information.
     */
    public static MissingDockableStrategy STORE_ALL = new MissingDockableStrategy(){
        public boolean shouldStoreHidden( String key ) {
            return true;
        }
        public boolean shouldStoreShown( String key ) {
            return true;
        }
        public <L> boolean shouldCreate( DockFactory<?,?,L> factory, L data ) {
            return true;
        }
    };
    
    /**
     * Tells whether the location of the hidden and missing {@link Dockable}
     * <code>key</code> should be stored anyway. A {@link DockFrontend} will
     * create an {@link DockFrontend#addEmpty(String) empty info} for all
     * keys which pass this method.
     * @param key the name of a missing element
     * @return <code>true</code> if its location should be stored for the
     * case that it becomes known later, or <code>false</code> if its location
     * should be discarded
     */
    public boolean shouldStoreHidden( String key );
    
    /**
     * Tells whether the location of the shown but missing {@link Dockable}
     * <code>key</code> should be stored anyway. A {@link DockFrontend} will
     * create an {@link DockFrontend#addEmpty(String) empty info} for all
     * keys which pass this method.
     * @param key the name of a missing element
     * @return <code>true</code> if its location should be stored for the
     * case that it becomes known later, or <code>false</code> if its location
     * should be discarded
     */
    public boolean shouldStoreShown( String key );
        
    /**
     * Tells whether <code>factory</code> should be used to create a new
     * {@link Dockable} using <code>data</code>. This method is only called
     * when the element would be created outside the normal setup phase, i.e.
     * when a new factory has become known and old data is revisited.
     * @param <L> the kind of data to use for <code>factory</code>
     * @param factory the factory which might create new elements
     * @param data the information <code>factory</code> would convert
     * @return <code>true</code> if <code>factory</code> is allowed to convert
     * <code>data</code>
     */
    public <L> boolean shouldCreate( DockFactory<?,?,L> factory, L data );
}
