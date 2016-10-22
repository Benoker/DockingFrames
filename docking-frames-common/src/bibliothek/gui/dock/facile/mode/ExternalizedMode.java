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

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.action.predefined.CExternalizeAction;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.action.ExternalizedModeAction;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.support.mode.ModeSetting;
import bibliothek.gui.dock.support.mode.ModeSettingFactory;
import bibliothek.gui.dock.support.mode.NullModeSettingsFactory;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.util.Path;

/**
 * Represents a mode in which dockables are freely floating on the screen.
 * @author Benjamin Sigg
 * @param <M> the areas that are managed by this mode
 */
public class ExternalizedMode<M extends ExternalizedModeArea> extends DefaultLocationMode<M>{
	/** the unique identifier of this mode */
	public static final Path IDENTIFIER = new Path( "dock.mode.externalized" );

    /** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "externalize"-action */
    public static final String ICON_IDENTIFIER = CLocationModeManager.ICON_MANAGER_KEY_EXTERNALIZE;
	
    /** customizable algorithms */
    private ExternalizedModeBehavior behavior = new DefaultExternalizedModeBehavior();
    
    /**
     * Empty default constructor. Subclasses should call 
     * {@link #setActionProvider(LocationModeActionProvider)} to complete
     * initialization of this mode.
     */
    protected ExternalizedMode(){
    	// nothing
    }
    
	/**
	 * Creates a new mode.
	 * @param control the control in whose realm this mode works
	 */
	public ExternalizedMode( CControl control ){
		setActionProvider( new DefaultLocationModeActionProvider( new CExternalizeAction( control ) ) );
	}
	
	/**
	 * Creates a new mode.
	 * @param controller the owner of this mode
	 */
	public ExternalizedMode( DockController controller ){
		setActionProvider( new DefaultLocationModeActionProvider( new ExternalizedModeAction( controller, this ) ) );
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
	public boolean runApply( Dockable dockable, Location history, AffectedSet set ){
		return externalize( dockable, history, set );
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
     * @return whether the operation was a success or not
     */
    private boolean externalize( Dockable dockable, Location location, AffectedSet affected ){
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
        	property = behavior.findLocation( area, dockable );
        }
        
        return area.setLocation( dockable, property, affected );
    }
    
    public ModeSettingFactory<Location> getSettingFactory(){
    	return new NullModeSettingsFactory<Location>( getUniqueIdentifier() );
    }
    
    /**
     * Tells this {@link ExternalizedMode} how some algorithms are implemented.
     * @param behavior the new behavior, not <code>null</code>
     */
    public void setBehavior( ExternalizedModeBehavior behavior ){
    	if( behavior == null ){
    		throw new IllegalArgumentException( "behavior must not be null" );
    	}
    	
		this.behavior = behavior;
	}
    
    /**
     * Gets the current implementation of some algorithms of this mode.
     * @return the behavior
     */
    public ExternalizedModeBehavior getBehavior(){
		return behavior;
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
