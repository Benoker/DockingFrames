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
package bibliothek.gui.dock.action;

import java.awt.Component;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.basic.action.buttons.MiniButton;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * Represents the background of a {@link Component} that somehow paints a {@link DockAction}. For example
 * the component could be a {@link MiniButton}.
 * @author Benjamin Sigg
 */
public interface DockActionBackgroundComponent extends BackgroundComponent{
	/** the kind of {@link UIValue} this is */
	public static final Path KIND = BackgroundComponent.KIND.append( "action" );
	
	/**
	 * Gets the {@link DockAction} which is painted by this component.
	 * @return the action, not <code>null</code>
	 */
	public DockAction getAction();

	/**
	 * Gets the {@link Dockable} for which the {@link #getAction() action} is shown.
	 * @return the dockable, not <code>null</code>
	 */
	public Dockable getDockable();
}
