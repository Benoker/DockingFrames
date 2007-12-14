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
package bibliothek.gui.dock.facile.menu;

import javax.swing.JMenu;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.menu.CloseableDockableMenuPiece;
import bibliothek.gui.dock.event.DockFrontendAdapter;
import bibliothek.gui.dock.facile.FControl;
import bibliothek.gui.dock.facile.FDockable;
import bibliothek.gui.dock.facile.event.FDockableAdapter;
import bibliothek.gui.dock.facile.intern.FacileDockable;
import bibliothek.gui.dock.support.menu.MenuPiece;

/**
 * A piece of a menu that adds an item for each closeable {@link FDockable}
 * that can be found in a {@link FControl}. The user can show or hide
 * {@link FDockable}s by clicking onto these items.
 * @author Benjamin Sigg
 */
public class FSingleDockableListMenuPiece extends CloseableDockableMenuPiece {
    /** the list of all {@link FDockable}s */
    private FControl control;
    
    /** a manager that ensures that those {@link FDockable}s which are closeable have an item in the menu */
    private CloseableListenerManager closeableManager = new CloseableListenerManager();
    
    /**
     * Creates a new piece.
     * @param menu the menu into which this piece will add its items.
     * @param control the control to observe for new {@link Dockable}s, can be <code>null</code>.
     */
    public FSingleDockableListMenuPiece( JMenu menu, FControl control ) {
        super( menu );
        setControl( control );
    }

    /**
     * Creates a new piece.
     * @param predecessor the piece directly above this piece
     * @param control the control to observe for new {@link Dockable}s, can be <code>null</code>.
     */
    public FSingleDockableListMenuPiece( MenuPiece predecessor, FControl control ) {
        super( predecessor );
        setControl( control );
    }
    
    /**
     * Exchanges the {@link FControl} whose {@link FDockable}s are observed
     * by this piece.
     * @param control the new control to observe, can be <code>null</code>
     */
    public void setControl( FControl control ) {
        if( this.control != control ){
            this.control = control;
            if( control == null ){
                setFrontend( null );
            }
            else{
                setFrontend( control.getFrontend() );
            }
        }
    }
    
    @Override
    public void setFrontend( DockFrontend frontend ) {
        super.setFrontend( frontend );
        closeableManager.setFrontend( frontend );
    }
    
    @Override
    protected void show( Dockable dockable ) {
        if( dockable instanceof FacileDockable )
            ((FacileDockable)dockable).getDockable().setVisible( true );
        else
            super.show( dockable );
    }
    
    @Override
    protected void hide( Dockable dockable ) {
        if( dockable instanceof FacileDockable )
            ((FacileDockable)dockable).getDockable().setVisible( false );
        else
            super.show( dockable );
    }
    
    @Override
    protected boolean include( Dockable dockable ) {
        if( dockable instanceof FacileDockable )
            return ((FacileDockable)dockable).getDockable().isCloseable();
        
        return super.include( dockable );
    }
    
    /**
     * A listener added to a {@link DockFrontend}, will add or remove
     * the {@link CloseableListener} from {@link FDockable}s when they are
     * added or removed to {@link #frontend}.
     * @author Benjamin Sigg
     */
    private class CloseableListenerManager extends DockFrontendAdapter{
        /** the listener to add or remove */
        private CloseableListener listener = new CloseableListener();
        /** the frontend to observe */
        private DockFrontend frontend;
        
        /**
         * Sets the frontend that will be observed for new or deleted
         * {@link FDockable}s.
         * @param frontend the new frontend, can be <code>null</code>
         */
        public void setFrontend( DockFrontend frontend ) {
            if( this.frontend != frontend ){
                if( this.frontend != null ){
                    this.frontend.removeFrontendListener( this );
                    for( Dockable dockable : frontend.getDockables() )
                        removed( this.frontend, dockable );
                }
                
                this.frontend = frontend;
                
                if( this.frontend != null ){
                    this.frontend.addFrontendListener( this );
                    for( Dockable dockable : frontend.getDockables() )
                        added( this.frontend, dockable );
                }
            }
        }
        
        @Override
        public void added( DockFrontend frontend, Dockable dockable ) {
            if( dockable instanceof FacileDockable ){
                ((FacileDockable)dockable).getDockable().addFDockableListener( listener );
            }
        }
        @Override
        public void removed( DockFrontend frontend, Dockable dockable ) {
            if( dockable instanceof FacileDockable ){
                ((FacileDockable)dockable).getDockable().removeFDockableListener( listener );
            }
        }
    }
    
    /**
     * A listener waiting for the closeable-property to change, and then
     * calling {@link FSingleDockableListMenuPiece#check(Dockable)}.
     * @author Benjamin Sigg
     */
    private class CloseableListener extends FDockableAdapter{
        @Override
        public void closeableChanged( FDockable dockable ) {
            check( dockable.getDockable() );
        }
    }
}
