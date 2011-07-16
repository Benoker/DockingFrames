/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionIcon;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.action.core.CommonSimpleButtonAction;
import bibliothek.gui.dock.common.action.util.CActionText;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.TextManager;
import bibliothek.util.FrameworkOnly;

/**
 * This action is intended to change the {@link ExtendedMode} of a 
 * {@link CDockable} by calling {@link CDockable#setExtendedMode(ExtendedMode)}.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class CExtendedModeAction extends CDropDownItem<CExtendedModeAction.Action>{
    /** the mode into which this action leads */
    private ExtendedMode mode;
    
    /** a listener to the {@link IconManager}, may change the icon of this action */
    private DockActionIcon iconListener;
    
    /** the key stroke that triggers this action */
    private PropertyValue<KeyStroke> stroke;
    
    /** the control for which this action is used */
    private CControl control;
    
    /** the text of this action */
    private CActionText text;
    
    /** the tooltip of this action */
    private CActionText tooltip;
    
    /** the internal representation */
    private Action action;
    /** the controller of {@link #control} or <code>null</code> */
    private DockController controller;
    
    /**
     * Creates a new action.
     * @param control the control for which this action will be used
     * @param mode the mode into which this action leads
     * @param iconKey the key of the icon when searching in the {@link IconManager}
     * @param textKey the key for the text of this action when searching the {@link TextManager}
     * @param tooltipKey the key for the tooltip of this action when searching the {@link TextManager}
     * @param gotoStroke the key to the {@link KeyStroke} that triggers this action
     */
    protected CExtendedModeAction( CControl control, ExtendedMode mode, String iconKey, String textKey, String tooltipKey, PropertyKey<KeyStroke> gotoStroke ){
        super( null );
        init( control, mode, iconKey, textKey, tooltipKey, gotoStroke );
    }

    /**
     * Creates an empty, non initialized action. Subclasses must call {@link #init(CControl, ExtendedMode, String, String, String, PropertyKey)} to
     * complete initialization.
     */
    protected CExtendedModeAction(){
    	super( null );
    }
    
    /**
     * Creates a new action, this method must be called only once.
     * @param control the control for which this action will be used
     * @param mode the mode into which this action leads
     * @param iconKey the key of the icon when searching in the {@link IconManager}
     * @param textKey the key for the text of this action when searching the {@link TextManager}
     * @param tooltipKey the key for the tooltip of this action when searching the {@link TextManager}
     * @param gotoStroke the key to the {@link KeyStroke} that triggers this action
     */
    protected void init( CControl control, ExtendedMode mode, String iconKey, String textKey, String tooltipKey, PropertyKey<KeyStroke> gotoStroke ){
        action = createAction();
        init( action );
        
        if( control == null )
            throw new NullPointerException( "control is null" );
        if( mode == null )
            throw new NullPointerException( "mode is null" );
        if( iconKey == null )
            throw new NullPointerException( "iconKey is null" );
        if( gotoStroke == null )
            throw new NullPointerException( "gotoStroke is null" );
        
        this.control = control;
        this.mode = mode;
        
        iconListener = new DockActionIcon( iconKey, action ){
			protected void changed( Icon oldValue, Icon newValue ){
				setIcon( newValue );
			}
		};
        
        stroke = new PropertyValue<KeyStroke>( gotoStroke ){
            @Override
            protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
                setAccelerator( newValue );
            }
        };
        
        text = new CActionText( textKey, this ){
			protected void changed( String oldValue, String newValue ){
				setText( newValue );
			}
		};
		
		tooltip = new CActionText( tooltipKey, this ){
			protected void changed( String oldValue, String newValue ){
				setTooltip( newValue );	
			}
		};
    }
    
    /**
     * Exchanges all the properties such that they are read from <code>controller</code>
     * @param controller the controller from which to read properties, or <code>null</code>
     */
    protected void setController( DockController controller ){
        this.controller = controller;
        stroke.setProperties( controller );
        iconListener.setController( controller );
        text.setController( controller );
        tooltip.setController( controller );
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
     * @param dockable the element for which the action is executed
     */
    public void action( CDockable dockable ){
        dockable.setExtendedMode( mode );
    }
    
    /**
     * Creates an instance of the action representing this {@link CExtendedModeAction}.
     * @return the action
     */
    protected Action createAction(){
    	return new Action();
    }
    
    /**
     * The internal representation of a {@link CExtendedModeAction}.
     * @author Benjamin Sigg
     */
    public class Action extends CommonSimpleButtonAction{
        /** how many times this action was bound */
        private int count = 0;
        
        /**
         * Creates a new action.
         */
        public Action(){
        	super( CExtendedModeAction.this );
        }
        
        @Override
        protected boolean trigger( KeyEvent event, Dockable dockable ) {
            if( checkTrigger( event ))
                return super.trigger( event, dockable );
            else
                return false;
        }
        
        @Override
        public void action( Dockable dockable ) {
        	while( dockable != null ){
	            if( dockable instanceof CommonDockable ){
	                CExtendedModeAction.this.action( ((CommonDockable)dockable).getDockable() );
	                return;
	            }
	            
	            DockStation station = dockable.asDockStation();
	            if( station == null ){
	            	return;
	            }
	            else{
	            	dockable = station.getFrontDockable();
	            }
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
