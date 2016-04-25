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

package bibliothek.gui.dock.control;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;

/**
 * A {@link DockRelocatorMode} is used by a {@link DockRelocator} to change
 * the behaviour of the drag and drop-operation depending what the user
 * does with the mouse and the keyboard.
 * @author Benjamin Sigg
 *
 */
public interface DockRelocatorMode {
    /** the modifiers that must be pressed to activate the {@link #SCREEN_ONLY} relocator mode */
    public static final PropertyKey<ModifierMask> SCREEN_MASK = 
        new PropertyKey<ModifierMask>( "DockRelocatorMode screen mask", 
                new ConstantPropertyFactory<ModifierMask>( 
                		new ModifierMask(InputEvent.SHIFT_DOWN_MASK )), 
                false );
    
    /**
     * Ensures that a {@link Dockable} can be dragged only onto a {@link ScreenDockStation}.
     * This mode is installed automatically by the {@link DockController}.
     */
    public static DockRelocatorMode SCREEN_ONLY = new AcceptanceDockRelocatorMode( 0, 0 ){
        
        public boolean accept( DockStation parent, Dockable child ) {
            return parent instanceof ScreenDockStation;
        }

        public boolean accept( DockStation parent, Dockable child, Dockable next ) {
            return parent instanceof ScreenDockStation;
        }
        
        @Override
        public boolean shouldBeActive( DockController controller, int modifiers ) {
            ModifierMask mask = controller.getProperties().get( SCREEN_MASK );
            return mask != null && mask.matches( modifiers );
        }
    };
    
    /** the modifiers that must be pressed to activate the {@link #NO_COMBINATION} relocator mode */
    public static final PropertyKey<ModifierMask> NO_COMBINATION_MASK = 
        new PropertyKey<ModifierMask>( "DockRelocatorMode no combination", 
                new ConstantPropertyFactory<ModifierMask>(
                		new ModifierMask(InputEvent.ALT_DOWN_MASK )), false );
    
    /**
     * Ensures that a {@link Dockable} can be dragged only if no combination results.
     * This mode is installed automatically by the {@link DockController}.
     */
    public static DockRelocatorMode NO_COMBINATION = new AcceptanceDockRelocatorMode( 0, 0 ){
        public boolean accept( DockStation parent, Dockable child ) {
            return !(parent instanceof StackDockStation);
        }

        public boolean accept( DockStation parent, Dockable child, Dockable next ) {
            return false;
        }
        
        @Override
        public boolean shouldBeActive( DockController controller, int modifiers ) {
            ModifierMask mask = controller.getProperties().get( NO_COMBINATION_MASK );
            return mask != null && mask.matches( modifiers );
        }
    };
    
    /**
     * Tells whether this mode should be activated because of the state of the
     * controller and the last {@link MouseEvent}.
     * @param controller the controller which might be affected by this mode
     * @param modifiers the state of the mouse, see {@link MouseEvent#getModifiersEx()}
     * @return <code>true</code> if this mode should be activated, <code>false</code>
     * otherwise
     */
    public boolean shouldBeActive( DockController controller, int modifiers );
    
    /**
     * Tells this mode whether it should have an influence of the 
     * behaviour or not.
     * @param controller the controller for which this mode is used
     * @param active <code>true</code> if this mode should change
     * the behaviour, <code>false</code> otherwise
     */
    public void setActive( DockController controller, boolean active );
}
