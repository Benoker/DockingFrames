/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.station.stack;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockElementRepresentative;

/**
 * This utility class keeps track of the current {@link DockElementRepresentative} 
 * that is created by a {@link StackDockComponent}. It also registers the representative
 * at a {@link DockController}.
 * @author Benjamin Sigg
 */
public class StackDockComponentRepresentative {
	/** the controller to which {@link #representative} is added */
	private DockController controller;
	/** the target to which {@link #representative} points */
	private DockElement target;
	/** the component that creates {@link #representative} */
	private StackDockComponent component;
	/** the representation of {@link #target} */
	private DockElementRepresentative representative;

	/**
	 * Sets the factory that creates new {@link DockElementRepresentative}s.
	 * @param component the new factory, can be <code>null</code>
	 */
	public void setComponent( StackDockComponent component ){
		if( this.component != component ){
			clearRepresentative();
			this.component = component;
			buildRepresentative();
		}
	}
	
	/**
	 * Sets the controller to which new {@link DockElementRepresentative}s should
	 * be added.
	 * @param controller the new controller, can be <code>null</code>
	 */
	public void setController( DockController controller ){
		if( this.controller != controller ){
			clearRepresentative();
			this.controller = controller;
			buildRepresentative();
		}
	}
	
	/**
	 * Sets the target to which new {@link DockElementRepresentative}s point.
	 * @param target the new target, can be <code>null</code>
	 */
	public void setTarget( DockElement target ){
		if( this.target != target ){
			clearRepresentative();
			this.target = target;
			buildRepresentative();
		}
	}
	
	private void clearRepresentative(){
		if( representative != null && controller != null ){
			controller.removeRepresentative( representative );
			representative = null;
		}
	}
	
	private void buildRepresentative(){
		if( controller != null && target != null && representative == null && component != null ){
			representative = component.createDefaultRepresentation( target );
			if( representative != null ){
				controller.addRepresentative( representative );
			}
		}
	}
}
