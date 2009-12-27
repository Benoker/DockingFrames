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
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.event.IconManagerListener;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.LocationModeManager;
import bibliothek.gui.dock.support.util.Resources;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * Action for changing the {@link LocationMode} of a {@link Dockable}.
 * @author Benjamin Sigg
 */
public class LocationModeAction extends SimpleButtonAction{
	/** the mode this action applies */
	private LocationMode mode;
	
    /** the key of the icon in the {@link IconManager} */
    private String iconKey;
    /** the key of the icon in the {@link Resources} */
    private String defaultIconKey;
    
    /** how often this action is binded */
    private int count = 0;
    
    /** a listener to the IconManager, may change the icon of this action */
    private IconManagerListener iconListener = new IconManagerListener(){
        public void iconChanged( String key, Icon icon ) {
            setIcon( controller.getIcons().getIcon( iconKey ) );
        }
    };
    
    /** the key stroke that triggers this action */
    private PropertyValue<KeyStroke> stroke;
    
    /** the controller of {@link #control} or <code>null</code> */
    private DockController controller;
    
    /**
     * Creates a new action.
     * @param controller the controller in whose realm this action works
     * @param mode the mode that is applied to any {@link Dockable} for which this button is pressed
     * @param defaultIconKey the default key of the icon,  this icon is used if {@link #setIcon(Icon)} is called with <code>null</code>
     * or if <code>iconKey</code> does not lead to an icon
     * @param iconKey the key of an icon to be used on this action
     * @param gotoStroke the key for an accelerator which triggers this action
     */
	public LocationModeAction( DockController controller, LocationMode mode, String defaultIconKey, String iconKey, PropertyKey<KeyStroke> gotoStroke ){
		this.mode = mode;
		
        if( mode == null )
            throw new NullPointerException( "mode is null" );
        if( defaultIconKey == null )
            throw new NullPointerException( "defaultIconKey is null" );
        if( iconKey == null )
            throw new NullPointerException( "iconKey is null" );
        if( gotoStroke == null )
            throw new NullPointerException( "gotoStroke is null" );
        
        this.mode = mode;
        this.iconKey = iconKey;
        this.defaultIconKey = defaultIconKey;
        
        stroke = new PropertyValue<KeyStroke>( gotoStroke ){
            @Override
            protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
                setAccelerator( newValue );
            }
        };
        
        setController( controller );
	}
	
	@Override
	public void action( Dockable dockable ){
		super.action( dockable );
		LocationModeManager manager = mode.getManager();
		manager.alter( dockable, mode );
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
    	if( count > 0 ){
    		connect( this.controller, controller );
    	}
    	this.controller = controller;
    }
    
    private void connect( DockController oldController, DockController newController ){
    	if( oldController != null ){
            IconManager icons = oldController.getIcons();
            icons.remove( defaultIconKey, iconListener );
            icons.remove( iconKey, iconListener );
            setIcon( null );
        }
        
        stroke.setProperties( newController );
        
        if( newController != null ){
            IconManager icons = newController.getIcons();
            icons.add( defaultIconKey, iconListener );
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
