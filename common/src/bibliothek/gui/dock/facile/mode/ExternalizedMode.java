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
package bibliothek.gui.dock.facile.mode;

import java.awt.Component;
import java.awt.Point;

import javax.swing.SwingUtilities;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.action.predefined.CExternalizeAction;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.action.ExternalizedModeAction;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.support.mode.ModeSetting;
import bibliothek.gui.dock.support.mode.ModeSettingFactory;
import bibliothek.gui.dock.support.mode.NullModeSettingsFactory;
import bibliothek.gui.dock.support.util.Resources;
import bibliothek.gui.dock.util.IconManager;

/**
 * Represents a mode in which dockables are freely floating on the screen.
 * @author Benjamin Sigg
 */
public class ExternalizedMode<M extends ExternalizedModeArea> extends DefaultLocationMode<M>{
	/** the unique identifier of this mode */
	public static final Path IDENTIFIER = new Path( "dock.mode.externalized" );

    /** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "externalize"-action */
    public static final String ICON_IDENTIFIER = "location.externalize";
	
	/**
	 * Creates a new mode.
	 * @param control the control in whose realm this mode works
	 */
	public ExternalizedMode( CControl control ){
		setSelectModeAction( new CExternalizeAction( control ) );
	}
	
	/**
	 * Creates a new mode.
	 * @param controller the owner of this mode
	 */
	public ExternalizedMode( DockController controller ){
		IconManager icons = controller.getIcons();
        icons.setIconDefault( "externalize", Resources.getIcon( "externalize" ) );
        
		setSelectModeAction( new ExternalizedModeAction( controller, this ) );
	}
	
	public Path getUniqueIdentifier(){
		return IDENTIFIER;
	}

	public ExtendedMode getExtendedMode(){
		return ExtendedMode.EXTERNALIZED;
	}
	
	public boolean isDefaultMode( Dockable dockable ){
		return false;
	}
	
	@Override
	public void runApply( Dockable dockable, Location history, AffectedSet set ){
		externalize( dockable, history, set );
	}
	
    /**
     * Makes sure that <code>dockable</code> is externalized, where
     * <code>location</code> describes the new position of <code>dockable</code>.
     * @param dockable the element to externalized, can already be in 
     * externalize-state.
     * @param location a location describing the new position of <code>dockable</code>,
     * the behavior is unspecified if <code>location</code> does not describe
     * an externalized position. Can be <code>null</code>.
     * @param affected a set of <code>Dockable</code>s which will be filled by the
     * elements that change their mode because of this method
     */
    private void externalize( Dockable dockable, Location location, AffectedSet affected ){
    	affected.add( dockable );

        ExternalizedModeArea area = null;
        if( location != null ){
        	area = get( location.getRoot() );
        }
        if( area == null ){
        	area = getDefaultArea();
        }

        DockableProperty property = null;
        if( location != null ){
        	property = location.getLocation();
        }
        if( property == null && !area.isChild( dockable )){
            Component component = dockable.getComponent();
            component.invalidate();

            Component parent = component;
            while( parent.getParent() != null )
                parent = parent.getParent();
            parent.validate();

            Point corner = new Point();
            SwingUtilities.convertPointToScreen( corner, dockable.getComponent() );

            property = new ScreenDockProperty( corner.x, corner.y, component.getWidth(), component.getHeight() );
        }
        
        area.setLocation( dockable, property, affected );
    }
    
    public ModeSettingFactory<Location> getSettingFactory(){
    	return new NullModeSettingsFactory<Location>( getUniqueIdentifier() );
    }
    
    public void ensureNotHidden( Dockable dockable ){
	    // ignore	
    }
    
    public void writeSetting( ModeSetting<Location> setting ){
	    // ignore	
    }
    
    public void readSetting( ModeSetting<Location> setting ){
    	// ignore
    }
}
