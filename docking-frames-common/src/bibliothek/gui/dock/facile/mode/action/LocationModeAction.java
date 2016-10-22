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
package bibliothek.gui.dock.facile.mode.action;

import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionIcon;
import bibliothek.gui.dock.action.DockActionText;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.LocationModeManager;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.TextManager;

/**
 * Action for changing the {@link LocationMode} of a {@link Dockable}.
 * @author Benjamin Sigg
 */
public class LocationModeAction extends SimpleButtonAction{
	/** the mode this action applies */
	private LocationMode mode;
	
    /** how often this action is bound */
    private int count = 0;
    
    /** a listener to the IconManager, may change the icon of this action */
    private DockActionIcon iconListener;
    
    /** the key stroke that triggers this action */
    private PropertyValue<KeyStroke> stroke;
    
    /** the text of this action */
    private DockActionText text;
    
    /** the tooltip of this action */
    private DockActionText tooltip;
    
    /** the controller in whose realm this mode action is used or <code>null</code> */
    private DockController controller;
    
    /**
     * Creates a new action.
     * @param controller the controller in whose realm this action works
     * @param mode the mode that is applied to any {@link Dockable} for which this button is pressed
     * @param iconKey the key of an icon to be used on this action
     * @param textKey the key for the text of this action when searching the {@link TextManager}
     * @param tooltipKey the key for the tooltip of this action when searching the {@link TextManager}
     * @param gotoStroke the key for an accelerator which triggers this action
     */
	public LocationModeAction( DockController controller, LocationMode mode, String iconKey, String textKey, String tooltipKey, PropertyKey<KeyStroke> gotoStroke ){
		if( mode == null )
            throw new NullPointerException( "mode is null" );
        if( iconKey == null )
            throw new NullPointerException( "iconKey is null" );
        if( gotoStroke == null )
            throw new NullPointerException( "gotoStroke is null" );
        
        this.mode = mode;
        
        stroke = new PropertyValue<KeyStroke>( gotoStroke ){
            @Override
            protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
                setAccelerator( newValue );
            }
        };
        
        iconListener = new DockActionIcon( iconKey, this ){
			protected void changed( Icon oldValue, Icon newValue ){
				setIcon( newValue );
			}
		};

        text = new DockActionText( textKey, this ){
			protected void changed( String oldValue, String newValue ){
				setText( newValue );
			}
		};
		
		tooltip = new DockActionText( tooltipKey, this ){
			protected void changed( String oldValue, String newValue ){
				setTooltip( newValue );	
			}
		};
		
        setController( controller );
	}
	
	@Override
	public void action( Dockable dockable ){
		super.action( dockable );
		LocationModeManager<?> manager = mode.getManager();
		
		while( dockable != null ){
			if( manager.isRegistered( dockable )){
				manager.apply( dockable, mode.getUniqueIdentifier(), false );
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
	
    /**
     * Exchanges all the properties such that they are read from <code>controller</code>
     * @param controller the controller from which to read properties, or <code>null</code>
     */
    protected void setController( DockController controller ){
    	if( count > 0 ){
    		connect( this.controller, controller );
    	}
    	this.controller = controller;
    }
    
    private void connect( DockController oldController, DockController newController ){
    	stroke.setProperties( newController );
        iconListener.setController( newController );
        text.setController( newController );
        tooltip.setController( newController );
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
    
    @Override
    protected void bound( Dockable dockable ) {
        super.bound( dockable );
        if( count == 0 ){
            connect( null, controller );
        }
        count++;
    }
    
    @Override
    protected void unbound( Dockable dockable ) {
        super.unbound( dockable );
        count--;
        if( count == 0 ){
            connect( controller, null );
        }
    }
}
