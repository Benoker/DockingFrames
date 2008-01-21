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
package bibliothek.gui.dock.security;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.ControllerSetupCollection;
import bibliothek.gui.dock.control.KeyboardController;
import bibliothek.gui.dock.event.ComponentHierarchyObserverListener;
import bibliothek.gui.dock.event.ControllerSetupListener;

/**
 * A {@link KeyboardController} which adds a {@link KeyListener} to each
 * {@link Component} that can be found on a {@link Dockable} in the realm
 * of one {@link DockController}.
 * @author Benjamin Sigg
 */
public class SecureKeyboardController extends KeyboardController {
    /** a listener forwarding {@link KeyEvent}s */
    private Listener listener = new Listener();
    
    /**
     * Creates a new {@link SecureKeyboardController}.
     * @param controller the owner of this controller
     * @param setup an observable informing this object when <code>controller</code>
     * is set up.
     */
    public SecureKeyboardController( DockController controller, ControllerSetupCollection setup ) {
        super( controller );
        setup.add( new ControllerSetupListener(){
            public void done( DockController controller ) {
                controller.getComponentHierarchyObserver().addListener( new ComponentHierarchyObserverListener(){
                    public void added( DockController controller, List<Component> components ) {
                        if( listener != null ){
                            for( Component component : components ){
                                component.addKeyListener( listener );
                            }
                        }
                    }
                    public void removed( DockController controller, List<Component> components ) {
                        if( listener != null ){
                            for( Component component : components ){
                                component.removeKeyListener( listener );
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void kill() {
        if( listener != null ){
            for( Component component : getController().getComponentHierarchyObserver().getComponents()){
                component.removeKeyListener( listener );
            }
            listener = null;
        }
    }

    /**
     * A listener added to a {@link Component}, forwarding {@link KeyEvent}s
     * @author Benjamin Sigg
     */
    private class Listener implements KeyListener{
        public void keyPressed( KeyEvent e ) {
            fireKeyPressed( e );
        }

        public void keyReleased( KeyEvent e ) {
            fireKeyReleased( e );
        }

        public void keyTyped( KeyEvent e ) {
            fireKeyTyped( e );
        }
    }
}
