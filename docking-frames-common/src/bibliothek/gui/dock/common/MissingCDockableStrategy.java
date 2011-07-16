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
package bibliothek.gui.dock.common;

import bibliothek.gui.dock.common.intern.CDockable;

/**
 * A strategy that tells what to do if {@link CControl} finds a description of
 * a {@link CDockable} that it does not know while reading a layout.
 * @author Benjamin Sigg
 */
public interface MissingCDockableStrategy {
    /**
     * A strategy that will always throw away information.
     */
    public static final MissingCDockableStrategy PURGE = new MissingCDockableStrategy(){
        public boolean shouldStoreSingle( String id ) {
            return false;
        }
         
        public boolean shouldStoreMulti( String arg0 ){
        	return false;
        }
        
        public boolean shouldCreate( String id, MultipleCDockableFactory<?, ?> factory ) {
            return false;
        }
        
        public <L extends MultipleCDockableLayout> boolean shouldCreate(
                String id, MultipleCDockableFactory<?, L> factory,
                String uniqueId, L data ) {
            return false;
        }
    };
    
    /**
     * A strategy that will always throw away information for {@link MultipleCDockable}s
     * but store information for {@link SingleCDockable}.
     */
    public static final MissingCDockableStrategy SINGLE = new MissingCDockableStrategy(){
        public boolean shouldStoreSingle( String id ) {
            return true;
        }
        
        public boolean shouldStoreMulti( String arg0 ){
        	return false;
        }
        
        public boolean shouldCreate( String id, MultipleCDockableFactory<?, ?> factory ) {
            return false;
        }
        public <L extends MultipleCDockableLayout> boolean shouldCreate(
                String id, MultipleCDockableFactory<?, L> factory,
                String uniqueId, L data ) {
            return false;
        }
    };
    
    /**
     * A strategy that will always store any information
     */
    public static final MissingCDockableStrategy STORE = new MissingCDockableStrategy(){
        public boolean shouldStoreSingle( String id ) {
            return true;
        }
        
        public boolean shouldStoreMulti( String arg0 ){
        	return true;
        }
        
        public boolean shouldCreate( String id, MultipleCDockableFactory<?, ?> factory ) {
            return true;
        }
        public <L extends MultipleCDockableLayout> boolean shouldCreate(
                String id, MultipleCDockableFactory<?, L> factory,
                String uniqueId, L data ) {
            return true;
        }
    };
    
    
    /**
     * Tells whether layout information for the missing {@link SingleCDockable}
     * with identifier <code>id</code> should be stored.
     * @param id the identifier of the missing {@link SingleCDockable}
     * @return <code>true</code> if layout information should remain available,
     * <code>false</code> if the information should be purged.
     */
    public boolean shouldStoreSingle( String id );

    /**
     * Tells whether layout information for the missing {@link MultipleCDockable}
     * with identifier <code>id</code> should be stored.
     * @param id the identifier of the missing {@link MultipleCDockable}
     * @return <code>true</code> if layout information should remain available,
     * <code>false</code> if the information should be purged.
     */
    public boolean shouldStoreMulti( String id );
    
    /**
     * Tells whether the factory <code>factory</code> should be used to create
     * {@link MultipleCDockable}s for which location information is available.
     * @param id the identifier of the factory
     * @param factory the factory to use
     * @return <code>true</code> if dockables can be restored by <code>factory</code>
     */
    public boolean shouldCreate( String id, MultipleCDockableFactory<?,?> factory );
    
    /**
     * Tells whether the {@link MultipleCDockable} with identifier <code>uniqueId</code>
     * should automatically be created outside the normal setup-phase. This method
     * is not called when applying a new layout through the normal ways, only
     * when <code>factory</code> has become available after the layout was already
     * set.
     * @param <L> the kind of data <code>factory</code> uses
     * @param id the identifier of <code>factory</code>
     * @param factory the factory which would create the new element
     * @param uniqueId the identifier of the element that would be created
     * @param data the data that would be given to <code>factory</code> to create
     * the new element
     * @return <code>true</code> if a new element should be created
     */
    public <L extends MultipleCDockableLayout> boolean shouldCreate( String id, MultipleCDockableFactory<?, L> factory, String uniqueId, L data );
}
