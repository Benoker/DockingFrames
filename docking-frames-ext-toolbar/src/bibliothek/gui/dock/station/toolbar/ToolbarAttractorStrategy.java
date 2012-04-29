/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.magnet.AttractorStrategy;

/**
 * This implementation of an {@link AttractorStrategy} reads the current
 * {@link ToolbarStrategy#STRATEGY} and assumes that two {@link Dockable}s which
 * both are {@link ToolbarStrategy#isToolbarPart(Dockable) toolbar parts}
 * attract each other.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarAttractorStrategy implements AttractorStrategy{
	@Override
	public Attraction attract( ScreenDockStation parent, Dockable moved,
			Dockable fixed ){
		return attraction(parent, moved, fixed);
	}

	@Override
	public Attraction stick( ScreenDockStation parent, Dockable moved,
			Dockable fixed ){
		return Attraction.NEUTRAL;
	}

	/**
	 * Calculates the result of
	 * {@link #attract(ScreenDockStation, Dockable, Dockable)}. Subclasses may
	 * use this method to calculate
	 * {@link #stick(ScreenDockStation, Dockable, Dockable)} as well.
	 * 
	 * @param parent
	 *            the parent of <code>moved</code> and <code>fixed</code>
	 * @param moved
	 *            the {@link Dockable} that was moved
	 * @param fixed
	 *            the {@link Dockable} that was not moved
	 * @return whether the two items attract each other
	 */
	protected Attraction attraction( ScreenDockStation parent, Dockable moved,
			Dockable fixed ){
		final DockController controller = parent.getController();
		if (controller == null){
			return Attraction.NEUTRAL;
		}
		final ToolbarStrategy strategy = controller.getProperties().get(
				ToolbarStrategy.STRATEGY);
		if (strategy.isToolbarPart(fixed) && strategy.isToolbarPart(moved)){
			return Attraction.ATTRACTED;
		}

		return Attraction.NEUTRAL;
	}
}
