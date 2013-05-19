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
package bibliothek.gui.dock.common.action.predefined;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabDockAction;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.core.CommonDropDownItem;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.intern.action.CDropDownItem;
import bibliothek.gui.dock.facile.action.CloseAction;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * An action that can close any {@link CDockable} by calling
 * {@link CDockable#setVisible(boolean)}. Clients can either create one 
 * {@link CCloseAction} for each {@link CDockable} or use one action for many {@link CDockable}s at the
 * same time.
 * @author Benjamin Sigg
 */
@EclipseTabDockAction
public class CCloseAction extends CDropDownItem<CCloseAction.Action>{
    /** the control for which this action works */
    private CControl control;
    
    /** the keystroke used to trigger this action */
    private PropertyValue<KeyStroke> keyClose = new PropertyValue<KeyStroke>( CControl.KEY_CLOSE ){
        @Override
        protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
            setAccelerator( newValue );
        }
    };
    
    /**
     * Creates a new action
     * @param control the control for which this action will be used
     */
    public CCloseAction( CControl control ) {
        super( null );
        if( control == null )
            throw new NullPointerException( "control is null" );
        
        this.control = control;
        init( createAction() );
    }
    
    /**
     * Creates the action that is used for {@link #intern()}.
     * @return the internal representation of this action
     */
    protected Action createAction(){
    	return new Action();
    }
    
    /**
     * Closes <code>dockable</code> now. This method is always called when
     * this action is triggered, so this is the optimal method to be overridden
     * and extended with new features like a {@link JOptionPane} asking whether
     * <code>dockable</code> should really be closed.<br>
     * <b>Note:</b> Consider using the {@link CVetoClosingListener} instead, it can 
     * handle all kind of closing events. 
     * @param dockable the element to close
     */
    public void close( CDockable dockable ){
        if( dockable.isVisible() ){
            dockable.setVisible( false );
        }
    }
    
    /**
     * Internal representation of the {@link CCloseAction}, just calls
     * {@link CCloseAction#close(CDockable)} when triggered.
     * @author Benjamin Sigg
     */
    public class Action extends CloseAction implements CommonDropDownItem{
        /** how often this action was bound */
        private int count = 0;
        
        /**
         * Creates a new action
         */
        public Action(){
            super( null );
        }
        
        @Override
        protected void close( Dockable dockable ) {
            if( dockable instanceof CommonDockable ){
                CCloseAction.this.close( ((CommonDockable)dockable).getDockable() );
            }
            else
                super.close( dockable );
        }
        
        @Override
        protected void bound( Dockable dockable ) {
            super.bound( dockable );
            if( count == 0 ){
                setController( control.intern().getController() );
                keyClose.setProperties( control.intern().getController() );
            }
            count++;
        }
        
        @Override
        protected void unbound( Dockable dockable ) {
            super.unbound( dockable );
            count--;
            if( count == 0 ){
                setController( null );
                keyClose.setProperties( (DockProperties)null );
            }
        }
        
        public CAction getAction(){
	        return CCloseAction.this;
        }
    }
}
