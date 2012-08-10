/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.common.intern;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.util.FrameworkOnly;

/**
 * A {@link DelayedWorkingAreaSetter} is a listener that is added to a {@link CControl} and
 * waits until a {@link CStation} with a specific identifier is registered. It then calls
 * {@link CDockable#setWorkingArea(CStation)} with this station. This listener automatically
 * removes itself once its mission is over.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class DelayedWorkingAreaSetter extends DockRegisterAdapter{
	private String area;
	private CDockable dockable;
	private CControl control;
	
	/**
	 * Creates a new setter.
	 * @param area the {@link CStation} for which to search
	 * @param dockable the element whose working area should be set
	 * @param control the control to monitor
	 */
	public DelayedWorkingAreaSetter( String area, CDockable dockable, CControl control ){
		this.area = area;
		this.dockable = dockable;
		this.control = control;
	}
	
	public void install(){
		control.getController().getRegister().addDockRegisterListener( this );
	}
	
	/**
	 * Removes all listeners this {@link DelayedWorkingAreaSetter} has added anywhere.
	 */
	public void uninstall(){
		control.getController().getRegister().removeDockRegisterListener( this );
	}

	public void dockStationRegistering( DockController controller, DockStation station ){
		if( station instanceof CommonDockStation<?,?>){
			CommonDockStation<?, ?> common = (CommonDockStation<?, ?>)station;
			CStation<?> cstation = common.getStation();
			String id = cstation.getUniqueId();
			if( id.equals( area )){
				if( cstation.isWorkingArea() ){
					dockable.setWorkingArea( cstation );
				}
				uninstall();
			}
		}
	}
	
	@Override
	public void registerUnstalled( DockController controller ){
		// means the layout is loaded completely
		uninstall();
	}
}
