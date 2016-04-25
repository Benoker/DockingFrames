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
import bibliothek.gui.dock.common.action.predefined.CNormalizeAction;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.action.NormalModeAction;
import bibliothek.gui.dock.support.mode.ModeSetting;
import bibliothek.gui.dock.support.mode.ModeSettingFactory;
import bibliothek.gui.dock.support.mode.NullModeSettingsFactory;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.util.Path;

/**
 * {@link CDockable}s are in {@link NormalMode} if they are a child
 * of a {@link NormalModeArea}.
 * @author Benjamin Sigg
 *
 */
public class NormalMode<M extends NormalModeArea> extends DefaultLocationMode<M>{
	/** The unique identifier of this mode */
	public static final Path IDENTIFIER = new Path( "dock.mode.normal" );
	
    /** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "normalize"-action */
    public static final String ICON_IDENTIFIER = CLocationModeManager.ICON_MANAGER_KEY_NORMALIZE;
    
    /**
     * Empty default constructor. Subclasses should call 
     * {@link #setActionProvider(LocationModeActionProvider)} to complete
     * initialization of this mode.
     */
    protected NormalMode(){
    	// nothing
    }
    
	/**
	 * Creates a new normal mode.
	 * @param control the owner of this mode
	 */
	public NormalMode( CControl control ){
		setActionProvider( new DefaultLocationModeActionProvider( new CNormalizeAction( control )) );
	}
	
	/**
	 * Creates a new mode.
	 * @param controller the owner of this mode
	 */
	public NormalMode( DockController controller ){
		setActionProvider( new DefaultLocationModeActionProvider( new NormalModeAction( controller, this ) ) );
	}
	
	public Path getUniqueIdentifier(){
		return IDENTIFIER;
	}

	public ExtendedMode getExtendedMode(){
		return ExtendedMode.NORMALIZED;
	}
	
	public boolean isCurrentMode( Dockable dockable ){
		for( NormalModeArea area : this ){
			if( area.isNormalModeChild( dockable )){
				return true;
			}
		}
		
		return false;
	}

	public boolean isDefaultMode( Dockable dockable ){
		return true;
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
