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
package bibliothek.gui.dock.common.menu;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.event.DockFrontendAdapter;
import bibliothek.gui.dock.facile.menu.CloseableDockableMenuPiece;

/**
 * A piece of a menu that adds an item for each closeable {@link CDockable}
 * that can be found in a {@link CControl}. The user can show or hide
 * {@link CDockable}s by clicking onto these items.<br>
 * <b>Please note: </b> this menu shows only {@link CDockable}s that really do exist,
 * if a dockable is loaded lazy, then this menu does not show the element!
 * @author Benjamin Sigg
 */
public class SingleCDockableListMenuPiece extends CloseableDockableMenuPiece {
    /** the list of all {@link CDockable}s */
    private CControl control;
    
    /** a manager that ensures that those {@link CDockable}s which are closeable have an item in the menu */
    private CloseableListenerManager closeableManager = new CloseableListenerManager();
    
    /**
     * Creates a new piece.
     * @param control the control to observe for new {@link Dockable}s, can be <code>null</code>.
     */
    public SingleCDockableListMenuPiece( CControl control ) {
        setControl( control );
    }

    /**
     * Exchanges the {@link CControl} whose {@link CDockable}s are observed
     * by this piece.
     * @param control the new control to observe, can be <code>null</code>
     */
    public void setControl( CControl control ) {
        if( this.control != control ){
            this.control = control;
            if( control == null ){
                setFrontend( null );
            }
            else{
                setFrontend( control.intern() );
            }
        }
    }
    
    @Override
    public void bind(){
    	if( !isBound() ){
    		super.bind();
    		closeableManager.bind();
    	}
    }
    
    @Override
    public void unbind(){
    	if( isBound() ){
    		super.unbind();
    		closeableManager.unbind();
    	}
    }
    
    @Override
    public void setFrontend( DockFrontend frontend ) {
        super.setFrontend( frontend );
        closeableManager.setFrontend( frontend );
    }
    
    @Override
    protected void show( Dockable dockable ) {
        if( dockable instanceof CommonDockable )
            ((CommonDockable)dockable).getDockable().setVisible( true );
        else
            super.show( dockable );
    }
    
    @Override
    protected void hide( Dockable dockable ) {
        if( dockable instanceof CommonDockable )
            ((CommonDockable)dockable).getDockable().setVisible( false );
        else
            super.hide( dockable );
    }
    
    @Override
    protected boolean include( Dockable dockable ) {
        if( dockable instanceof CommonDockable )
            return ((CommonDockable)dockable).getDockable().isCloseable();
        
        return super.include( dockable );
    }
    
    /**
     * A listener added to a {@link DockFrontend}, will add or remove
     * the {@link CloseableListener} from {@link CDockable}s when they are
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
         * {@link CDockable}s.
         * @param frontend the new frontend, can be <code>null</code>
         */
        public void setFrontend( DockFrontend frontend ) {
        	if( this.frontend != frontend ){
        		if( isBound() ){
	                if( this.frontend != null ){
	                    this.frontend.removeFrontendListener( this );
	                    for( Dockable dockable : frontend.listDockables() )
	                        removed( this.frontend, dockable );
	                }
        		}
                
                this.frontend = frontend;
                
                if( isBound() ){
	                if( this.frontend != null ){
	                    this.frontend.addFrontendListener( this );
	                    for( Dockable dockable : frontend.listDockables() )
	                        added( this.frontend, dockable );
	                }
                }
            }
        }
        
        /**
         * Connects this listener with {@link #frontend}
         */
        public void bind(){
        	if( this.frontend != null ){
                frontend.addFrontendListener( this );
                for( Dockable dockable : frontend.listDockables() )
                    added( frontend, dockable );
            }
        }
        
        /**
         * Disconnects this listener from {@link #frontend}
         */
        public void unbind(){
        	if( frontend != null ){
                frontend.removeFrontendListener( this );
                for( Dockable dockable : frontend.listDockables() )
                    removed( frontend, dockable );
            }
        }
        
        @Override
        public void added( DockFrontend frontend, Dockable dockable ) {
            if( dockable instanceof CommonDockable ){
                ((CommonDockable)dockable).getDockable().addCDockablePropertyListener( listener );
            }
        }
        @Override
        public void removed( DockFrontend frontend, Dockable dockable ) {
            if( dockable instanceof CommonDockable ){
                ((CommonDockable)dockable).getDockable().removeCDockablePropertyListener( listener );
            }
        }
    }
    
    /**
     * A listener waiting for the closeable-property to change, and then
     * calling {@link SingleCDockableListMenuPiece#check(Dockable)}.
     * @author Benjamin Sigg
     */
    private class CloseableListener extends CDockableAdapter{
        @Override
        public void closeableChanged( CDockable dockable ) {
            check( dockable.intern() );
        }
    }
}
