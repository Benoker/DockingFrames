/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Hervé Guillaume, Benjamin Sigg
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
 * Hervé Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock;

import java.awt.Component;

import javax.swing.Icon;

import bibliothek.gui.DockStation;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.station.OrientationObserver;
import bibliothek.gui.dock.station.OrientedDockStation;

/**
 * This kind of {@link ComponentDockable} is useful for components with an
 * orientation. It allows to set automatically the orientation of the component
 * with regards to the orientation of its parent {@link DockStation}, if this
 * parent is an {@link OrientedDockStation}.
 * 
 * @author Herve Guillaume
 * 
 */
public abstract class OrientedComponentDockable extends ComponentDockable {

	public OrientedComponentDockable(){
		super();
		init();
	}

	public OrientedComponentDockable( Component component, Icon icon ){
		super( component, icon );
		init();
	}

	public OrientedComponentDockable( Component component, String title, Icon icon ){
		super( component, title, icon );
		init();
	}

	public OrientedComponentDockable( Component component, String title ){
		super( component, title );
		init();
	}

	public OrientedComponentDockable( Component component ){
		super( component );
		init();
	}

	public OrientedComponentDockable( Icon icon ){
		super( icon );
		init();
	}

	public OrientedComponentDockable( String title ){
		super( title );
		init();
	}

	private void init(){
		new OrientationObserver( this ){
			@Override
			protected void orientationChanged( Orientation current ){
				if( current != null ){
					setOrientation( current );
				}
			}
		};
	}
	
	/**
	 * Sets the new {@link Orientation} of this dockable.
	 * @param orientation the new orientation, not <code>null</code>
	 */
	protected abstract void setOrientation( Orientation orientation );
}
