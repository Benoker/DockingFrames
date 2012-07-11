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
package bibliothek.gui.dock.toolbar.intern;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.LocationModeManager;
import bibliothek.gui.dock.facile.mode.status.ExtendedModeEnablement;
import bibliothek.gui.dock.facile.mode.status.ExtendedModeEnablementFactory;
import bibliothek.gui.dock.facile.mode.status.ExtendedModeEnablementListener;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.toolbar.location.CToolbarMode;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * Using the {@link ToolbarStrategy} this {@link ExtendedModeEnablement} makes sure that 
 * any item that is part of a toolbar does not have any available modes other than the
 * {@link CToolbarMode}.
 * @author Benjamin Sigg
 */
public class ToolbarExtendedModeEnablement implements ExtendedModeEnablement{
	/**
	 * A factory creating new {@link ToolbarExtendedModeEnablement}s.
	 */
	public static final ExtendedModeEnablementFactory FACTORY = new ExtendedModeEnablementFactory(){
		@Override
		public ExtendedModeEnablement create( LocationModeManager<?> manager ){
			return new ToolbarExtendedModeEnablement( manager.getController() );
		}
	};
	
	/** the current {@link ToolbarStrategy} */
	private PropertyValue<ToolbarStrategy> strategy = new PropertyValue<ToolbarStrategy>( ToolbarStrategy.STRATEGY ){
		@Override
		protected void valueChanged( ToolbarStrategy oldValue, ToolbarStrategy newValue ){
			// ignore
		}
	};
	
	public ToolbarExtendedModeEnablement( DockController controller ){
		strategy.setProperties( controller );
	}
	
	@Override
	public Hidden isHidden( Dockable dockable, ExtendedMode mode ){
		ToolbarStrategy strategy = this.strategy.getValue();
		if( strategy == null ){
			return Hidden.UNCERTAIN;
		}
		if( strategy.isToolbarGroupPart( dockable ) || strategy.isToolbarPart( dockable )){
			if( CToolbarMode.TOOLBAR.equals( mode )){
				return Hidden.WEAK_VISIBLE;
			}
			else{
				return Hidden.WEAK_HIDDEN;
			}
		}
		else{
			return Hidden.UNCERTAIN;
		}
	}
	
	@Override
	public Availability isAvailable( Dockable dockable, ExtendedMode mode ){
		ToolbarStrategy strategy = this.strategy.getValue();
		if( strategy == null ){
			return Availability.UNCERTAIN;
		}
		if( strategy.isToolbarGroupPart( dockable ) || strategy.isToolbarPart( dockable )){
			if( CToolbarMode.TOOLBAR.equals( mode )){
				return Availability.STRONG_AVAILABLE;
			}
			else if( ExtendedMode.EXTERNALIZED.equals( mode )){
				return Availability.WEAK_AVAILABLE;
			}
			else{
				return Availability.WEAK_FORBIDDEN;
			}
		}
		else{
			if( CToolbarMode.TOOLBAR.equals( mode )){
				return Availability.WEAK_FORBIDDEN;
			}
			else{
				return Availability.UNCERTAIN;
			}
		}
	}

	@Override
	public void addListener( ExtendedModeEnablementListener listener ){
		// ignore
	}

	@Override
	public void removeListener( ExtendedModeEnablementListener listener ){
		// ignore
	}

	@Override
	public void destroy(){
		strategy.setProperties( (DockController)null );
	}
}
