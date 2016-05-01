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
import bibliothek.gui.dock.common.action.predefined.CMinimizeAction;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.action.MinimizedModeAction;
import bibliothek.gui.dock.support.mode.ModeSetting;
import bibliothek.gui.dock.support.mode.ModeSettingFactory;
import bibliothek.gui.dock.support.mode.NullModeSettingsFactory;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.util.Path;

/**
 * Only the title of a minimized {@link Dockable} is visible.
 * @author Benjamin Sigg
 */
public class MinimizedMode<M extends MinimizedModeArea> extends DefaultLocationMode<M>{
	/** the unique identifier of this mode */
	public static final Path IDENTIFIER = new Path( "dock.mode.minimized" );
	
    /** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "minimize"-action */
    public static final String ICON_IDENTIFIER = CLocationModeManager.ICON_MANAGER_KEY_MINIMIZE;

    /**
     * Empty default constructor. Subclasses should call 
     * {@link #setActionProvider(LocationModeActionProvider)} to complete
     * initialization of this mode.
     */
    protected MinimizedMode(){
    	setShouldAutoFocus( false );
    }
    
	/**
	 * Creates a new mode.
	 * @param control the control in whose realm this mode is used
	 */
	public MinimizedMode( CControl control ){
		setActionProvider( new DefaultLocationModeActionProvider( new CMinimizeAction( control ) ) );
		setShouldAutoFocus( false );
	}
	
	/**
	 * Creates a new mode.
	 * @param controller the owner of this mode
	 */
	public MinimizedMode( DockController controller ){
		setActionProvider( new DefaultLocationModeActionProvider( new MinimizedModeAction( controller, this ) ) );
        setShouldAutoFocus( false );
	}
	
	public Path getUniqueIdentifier(){
		return IDENTIFIER;
	}
	
	public ExtendedMode getExtendedMode(){
		return ExtendedMode.MINIMIZED;
	}
	
	public boolean isDefaultMode( Dockable dockable ){
		return false;
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
