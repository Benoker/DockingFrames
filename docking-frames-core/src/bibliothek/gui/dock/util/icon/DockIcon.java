/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.util.icon;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.util.AbstractUIValue;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * This class is used to retrieve {@link Icon}s from the {@link IconManager}.
 * @author Benjamin Sigg
 */
public abstract class DockIcon extends AbstractUIValue<Icon, DockIcon>{
	/** What kind of {@link UIValue} this is */
	public static final Path KIND_ICON = new Path( "dock.icon" );

	/**
	 * Creates a new {@link DockIcon}.
	 * @param id the unique identifier of this icon
	 * @param kind what kind of {@link UIValue} this is
	 */
	public DockIcon( String id, Path kind ){
		super( id, kind );
	}
	
	/**
	 * Creates a new {@link DockIcon}.
	 * @param id the unique identifier of this icon
	 * @param kind what kind of {@link UIValue} this is
	 * @param backup the icon to be used if no other icon is found
	 */
	public DockIcon( String id, Path kind, Icon backup ){
		super( id, kind, backup );
	}
	
	/**
	 * Sets the {@link IconManager} of <code>controller</code>
	 * @param controller the controller to observe, can be <code>null</code>
	 */
	public void setController( DockController controller ){
		if( controller == null ){
			setManager( null );
		}
		else{
			setManager( controller.getIcons() );
		}
	}
	
	@Override
	protected DockIcon me(){
		return this;
	}
}
