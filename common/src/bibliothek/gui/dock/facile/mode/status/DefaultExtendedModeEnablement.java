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
package bibliothek.gui.dock.facile.mode.status;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.mode.LocationModeManager;

/**
 * This default implementation observes {@link CDockable#isExternalizable()},
 * {@link CDockable#isMinimizable()} and {@link CDockable#isMaximizable()}.
 * @author Benjamin Sigg
 *
 */
public class DefaultExtendedModeEnablement extends AbstractExtendedModeEnablement{
	/**
	 * This factory creates new {@link DefaultExtendedModeEnablement}s.
	 */
	public static final ExtendedModeEnablementFactory FACTORY = new ExtendedModeEnablementFactory() {
		public ExtendedModeEnablement create( LocationModeManager<?> manager ){
			return new DefaultExtendedModeEnablement( manager );
		}
	};
	
	/** added to any {@link CDockable} */
	private CDockablePropertyListener listener = new CDockableAdapter(){
		public void minimizableChanged( CDockable dockable ){ 
			fire( dockable.intern(), ExtendedMode.MINIMIZED, isAvailable( dockable.intern(), ExtendedMode.MINIMIZED ));
		}
		
		public void maximizableChanged( CDockable dockable ){
			fire( dockable.intern(), ExtendedMode.MAXIMIZED, isAvailable( dockable.intern(), ExtendedMode.MAXIMIZED ));
		}
		
		public void externalizableChanged( CDockable dockable ){
			fire( dockable.intern(), ExtendedMode.EXTERNALIZED, isAvailable( dockable.intern(), ExtendedMode.EXTERNALIZED ));
		}
	};
	
	/**
	 * Creates a new enablement.
	 * @param manager the manager to observe
	 */
	public DefaultExtendedModeEnablement( LocationModeManager<?> manager ){
		super( manager );
		init();
	}
	
	@Override
	protected void connect( Dockable dockable ){
		if( dockable instanceof CommonDockable ){
			((CommonDockable)dockable).getDockable().addCDockablePropertyListener( listener );
		}
	}

	@Override
	protected void disconnect( Dockable dockable ){
		if( dockable instanceof CommonDockable ){
			((CommonDockable)dockable).getDockable().removeCDockablePropertyListener( listener );
		}
	}

	public boolean isAvailable( Dockable dockable, ExtendedMode mode ){
		if( mode == ExtendedMode.NORMALIZED ){
			return true;
		}
		
		if( dockable instanceof CommonDockable ){
			CDockable cdockable = ((CommonDockable)dockable).getDockable();
			
			if( mode == ExtendedMode.EXTERNALIZED )
				return cdockable.isExternalizable();
			
			if( mode == ExtendedMode.MAXIMIZED )
				return cdockable.isMaximizable();
			
			if( mode == ExtendedMode.MINIMIZED )
				return cdockable.isMinimizable();
		}
		
		return false;
	}
}
