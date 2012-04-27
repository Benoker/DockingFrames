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

package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarActionDockable;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.ToolbarTabDockStation;

/**
 * The default implementation of {@link ToolbarStrategy}.
 * <p>
 * Reminder: the toolbar API defines one dockable {@link ComponentDockable} and
 * three dockstation layers: {@link ToolbarDockStation},
 * {@link ToolbarGroupDockStation} and {@link ToolbarContainerDockStation}.
 * <p>
 * A <code>ComponentDockable</code> can be put in the three layers. A
 * <code>ToolbarDockStation</code> acts exactly the same. A
 * <code>ToolbarGroupDockStation</code> can be put only in a
 * <code>ToolbarGroupDockStation</code> (with some constraints, but this
 * constraints are handled by the station itself) or in
 * <code>ToolbarContainerDockStation</code>.
 * <p>
 * About the layers (and not about the question to know if a station accept a
 * particular dockable), in the layer hierarchy, a station can only contains the
 * immediate lower dockable:
 * <ul>
 * <li> <code>ToolbarContainerDockStation</code> <=
 * <code>ToolbarGroupDockStation</code>
 * <li> <code>ToolbarGroupDockStation</code> <= <code>ToolbarDockStation</code>
 * <li> <code>ToolbarDockStation</code> <= <code>ComponentDockable</code>
 * </ul>
 * 
 * @author Benjamin Sigg
 * @author Herve Guillaume
 */
public class DefaultToolbarStrategy implements ToolbarStrategy{

	@Override
	public Dockable ensureToolbarLayer( DockStation station, Dockable dockable ){
		if (station instanceof ToolbarDockStation ){
			return dockable;
		}

		if (station instanceof ToolbarGroupDockStation){
			if (dockable instanceof ToolbarDockStation){
				return dockable;
			} else{
				return new ToolbarDockStation();
			}
		}

		if ((station instanceof ToolbarContainerDockStation)
				|| (station instanceof ScreenDockStation)){
			if (dockable instanceof ToolbarGroupDockStation){
				return dockable;
			} else{
				return new ToolbarGroupDockStation();
			}
		}

		return null;
	}

	@Override
	public boolean isToolbarGroupPartParent( DockStation parent,
			Dockable child, boolean strong ){
		if (strong){
			if (child instanceof ComponentDockable || child instanceof ToolbarActionDockable){
				return (parent instanceof ToolbarDockStation);
			}

			if (child instanceof ToolbarDockStation){
				return parent instanceof ToolbarGroupDockStation;
			}

			if (child instanceof ToolbarGroupDockStation){
				return (parent instanceof ToolbarContainerDockStation)
						|| (parent instanceof ScreenDockStation);
			}

			return false;
		} else{
			// floating policy
			if ((parent instanceof ScreenDockStation)
					&& (child instanceof ToolbarGroupDockStation)){
				return true;
			}

			// ?? policy
			if ((child instanceof ComponentDockable || child instanceof ToolbarActionDockable)
					&& (parent instanceof ToolbarTabDockStation)){
				return true;
			}
			// docking and merging policy
			if (parent instanceof ToolbarInterface){
				if ((child instanceof ComponentDockable || child instanceof ToolbarActionDockable)
						|| (child instanceof ToolbarDockStation)){
					return true;
				} else if ((child instanceof ToolbarGroupDockStation)
						&& ((parent instanceof ToolbarGroupDockStation) || (parent instanceof ToolbarContainerDockStation))){
					return true;
				} else{
					return false;
				}
			} else{
				return false;
			}
		}
	}

	@Override
	public boolean isToolbarGroupPart( Dockable dockable ){
		return (dockable instanceof ComponentDockable || dockable instanceof ToolbarActionDockable)
				|| (dockable instanceof ToolbarDockStation);
	}

	@Override
	public boolean isToolbarPart( Dockable dockable ){
		return (dockable instanceof ToolbarGroupDockStation)
				|| isToolbarGroupPart(dockable);
	}
}
