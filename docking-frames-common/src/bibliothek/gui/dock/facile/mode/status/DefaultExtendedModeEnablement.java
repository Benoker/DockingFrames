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

import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.LocationModeManager;
import bibliothek.gui.dock.util.extension.ExtensionManager;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.util.Path;

/**
 * This default implementation observes {@link CDockable#isNormalizeable()}, {@link CDockable#isExternalizable()},
 * {@link CDockable#isMinimizable()} and {@link CDockable#isMaximizable()}.
 * @author Benjamin Sigg
 *
 */
public class DefaultExtendedModeEnablement extends AbstractExtendedModeEnablement{
	/**
	 * Name of an {@link ExtensionName} to add additional rules to this enablement. The extensions
	 * are of type {@link ExtendedModeEnablementFactory}.
	 */
	public static final Path EXTENSION = new Path( "dock.DefaultExtendedModeEnablement" );
	
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
			fire( dockable.intern(), ExtendedMode.MINIMIZED, isAvailable( dockable.intern(), ExtendedMode.MINIMIZED ).isAvailable() );
		}
		
		public void maximizableChanged( CDockable dockable ){
			fire( dockable.intern(), ExtendedMode.MAXIMIZED, isAvailable( dockable.intern(), ExtendedMode.MAXIMIZED ).isAvailable() );
		}
		
		public void externalizableChanged( CDockable dockable ){
			fire( dockable.intern(), ExtendedMode.EXTERNALIZED, isAvailable( dockable.intern(), ExtendedMode.EXTERNALIZED ).isAvailable() );
		}
		
		public void normalizeableChanged( CDockable dockable ){
			fire( dockable.intern(), ExtendedMode.NORMALIZED, isAvailable( dockable.intern(), ExtendedMode.NORMALIZED ).isAvailable() );
		}
	};
	
	/** a listener added to all {@link #extensions} */
	private ExtendedModeEnablementListener extensionListener = new ExtendedModeEnablementListener(){
		public void availabilityChanged( Dockable dockable, ExtendedMode mode, boolean available ){
			fire( dockable, mode, isAvailable( dockable, mode ).isAvailable() );
		}
	};
	
	/** Additional rules loaded from the {@link ExtensionManager} */
	private ExtendedModeEnablement[] extensions;
	
	/**
	 * Creates a new enablement.
	 * @param manager the manager to observe
	 */
	public DefaultExtendedModeEnablement( LocationModeManager<?> manager ){
		super( manager );
		init();
		List<ExtendedModeEnablementFactory> factories = manager.getController().getExtensions().load( new ExtensionName<ExtendedModeEnablementFactory>( EXTENSION, ExtendedModeEnablementFactory.class ));
		extensions = new ExtendedModeEnablement[ factories.size() ];
		int index = 0;
		for( ExtendedModeEnablementFactory factory : factories ){
			extensions[index] = factory.create( manager );
			extensions[index].addListener( extensionListener );
		}
	}
	
	@Override
	public void destroy(){
		for( ExtendedModeEnablement extension : extensions ){
			extension.removeListener( extensionListener );
			extension.destroy();
		}
		super.destroy();
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

	public Availability isAvailable( Dockable dockable, ExtendedMode mode ){
		Availability available = isModeAvailable( dockable, mode );
		for( ExtendedModeEnablement extension : extensions ){
			available = available.strongest( extension.isAvailable( dockable, mode ) );
		}
		return available;
	}
	
	public Hidden isHidden( Dockable dockable, ExtendedMode mode ){
		Hidden hidden = Hidden.WEAK_VISIBLE;
		for( ExtendedModeEnablement extension : extensions ){
			hidden = hidden.strongest( extension.isHidden( dockable, mode ) );
		}
		return hidden;
	}
	
	/**
	 * The actual implementation of {@link ExtendedModeEnablement#isAvailable(Dockable, ExtendedMode)}
	 * @param dockable the item whose mode should be checked
	 * @param mode the mode to check
	 * @return whether <code>mode</code> is available or not
	 */
	protected Availability isModeAvailable( Dockable dockable, ExtendedMode mode ){
		if( dockable instanceof CommonDockable ){
			CDockable cdockable = ((CommonDockable)dockable).getDockable();
			
			boolean result = false;
			boolean set = false;
			
			if( mode == ExtendedMode.EXTERNALIZED ){
				result = cdockable.isExternalizable();
				set = true;
			}
			else if( mode == ExtendedMode.MAXIMIZED ){
				result = cdockable.isMaximizable();
				set = true;
			}
			else if( mode == ExtendedMode.MINIMIZED ){
				result = cdockable.isMinimizable();
				set = true;
			}
			else if( mode == ExtendedMode.NORMALIZED ){
				result = cdockable.isNormalizeable();
				set = true;
			}
			
			if( set ){
				if( result ){
					return Availability.WEAK_AVAILABLE;
				}
				else{
					return Availability.WEAK_FORBIDDEN;
				}
			}
		}
		
		DockStation station = dockable.asDockStation();
		if( station != null ){
			for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
				Availability result = isModeAvailable( station.getDockable( i ), mode );
				if( result != Availability.UNCERTAIN ){
					return result;
				}
			}
		}
		
		return Availability.UNCERTAIN;
	}
}
