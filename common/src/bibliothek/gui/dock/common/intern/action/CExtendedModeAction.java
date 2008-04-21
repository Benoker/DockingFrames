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
package bibliothek.gui.dock.common.intern.action;

import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.intern.CDockable.ExtendedMode;
import bibliothek.gui.dock.event.IconManagerListener;
import bibliothek.gui.dock.support.util.Resources;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * This action is intended to change the {@link ExtendedMode} of a 
 * {@link CDockable} by calling {@link CDockable#setExtendedMode(ExtendedMode)}.
 * @author Benjamin Sigg
 */
public class CExtendedModeAction extends CDropDownItem{
    /** the mode into which this action leads */
    private ExtendedMode mode;
    
    /** the key of the icon in the {@link IconManager} */
    private String iconKey;
    /** the key of the icon in the {@link Resources} */
    private String defaultIconKey;
    
    /** a listener to the IconManager, may change the icon of this action */
    private IconManagerListener iconListener = new IconManagerListener(){
        public void iconChanged( String key, Icon icon ) {
            setIcon( icon );
        }
    };
    
    /** the key stroke that triggers this action */
    private PropertyValue<KeyStroke> stroke;
    
    /** the control for which this action is used */
    private CControl control;
    
    /** the internal representation */
    private Action action;
    /** the controller of {@link #control} or <code>null</code> */
    private DockController controller;
    
    /**
     * Creates a new action.
     * @param control the control for which this action will be used
     * @param mode the mode into which this action leads
     * @param defaultIconKey the key of the icon when searching in {@link Resources}
     * @param iconKey the key of the icon when searching in the {@link IconManager}
     * @param gotoStroke the key to the {@link KeyStroke} that triggers this action
     */
    protected CExtendedModeAction( CControl control, ExtendedMode mode, String defaultIconKey, String iconKey, PropertyKey<KeyStroke> gotoStroke ){
        super( null );
        action = new Action();
        init( action );
        
        if( control == null )
            throw new NullPointerException( "control is null" );
        if( mode == null )
            throw new NullPointerException( "mode is null" );
        if( defaultIconKey == null )
            throw new NullPointerException( "defaultIconKey is null" );
        if( iconKey == null )
            throw new NullPointerException( "iconKey is null" );
        if( gotoStroke == null )
            throw new NullPointerException( "gotoStroke is null" );
        
        this.control = control;
        this.mode = mode;
        this.iconKey = iconKey;
        this.defaultIconKey = defaultIconKey;
        
        stroke = new PropertyValue<KeyStroke>( gotoStroke ){
            @Override
            protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
                setAccelerator( newValue );
            }
        };
    }
    
    @Override
    public void setIcon( Icon icon ) {
        if( icon == null ){
            icon = Resources.getIcon( defaultIconKey );
        }
        
        super.setIcon( icon );
    }
    
    /**
     * Exchanges all the properties such that they are read from <code>controller</code>
     * @param controller the controller from which to read properties, or <code>null</code>
     */
    protected void setController( DockController controller ){
        if( this.controller != null ){
            IconManager icons = this.controller.getIcons();
            icons.remove( iconKey, iconListener );
            setIcon( null );
        }
        
        this.controller = controller;
        stroke.setProperties( controller );
        
        if( controller != null ){
            IconManager icons = controller.getIcons();
            icons.add( iconKey, iconListener );
            setIcon( icons.getIcon( iconKey ) );
        }
    }
    
    /**
     * Gets the controller from which this action currently reads its content.
     * @return the controller or <code>null</code>
     */
    protected DockController getController() {
        return controller;
    }
    
    /**
     * Checks whether this action is able to trigger this action.
     * @param event an event that matches the accelerator of this action
     * @return <code>true</code> if this action really is triggered
     */
    protected boolean checkTrigger( KeyEvent event ){
        return true;
    }
    
    /**
     * This method actually changes the {@link ExtendedMode} of <code>dockable</code> 
     * to the mode that was given to this action in the constructor. Every 
     * triggering of this action will finally call this method, so this method
     * is the optimal point to be overridden and modified.
     * @param dockable
     */
    public void action( CDockable dockable ){
        dockable.setExtendedMode( mode );
    }
    
    /**
     * The internal representation of a {@link CExtendedModeAction}.
     * @author Benjamin Sigg
     */
    private class Action extends SimpleButtonAction{
        /** how many times this action was bound */
        private int count = 0;
        
        @Override
        protected boolean trigger( KeyEvent event, Dockable dockable ) {
            if( checkTrigger( event ))
                return super.trigger( event, dockable );
            else
                return false;
        }
        
        @Override
        public void action( Dockable dockable ) {
            if( dockable instanceof CommonDockable ){
                CExtendedModeAction.this.action( ((CommonDockable)dockable).getDockable() );
            }
        }
        
        @Override
        protected void bound( Dockable dockable ) {
            super.bound( dockable );
            if( count == 0 ){
                setController( control.intern().getController() );
            }
            count++;
        }
        
        @Override
        protected void unbound( Dockable dockable ) {
            super.unbound( dockable );
            count--;
            if( count == 0 ){
                setController( null );
            }
        }
    }
}
