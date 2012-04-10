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
package bibliothek.gui.dock.station.flap;

import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.support.ListSpanStrategy;
import bibliothek.gui.dock.themes.ThemeManager;

/**
 * Manages the {@link Span}s of a {@link FlapDockStation}.
 * @author Benjamin Sigg
 */
public class FlapSpanStrategy extends ListSpanStrategy{
	private FlapDockStation station;
	private ButtonPane buttons;
	
	/**
	 * Creates a new strategy.
	 * @param station the owner of this strategy
	 * @param buttons the panel showing all buttons
	 */
	public FlapSpanStrategy( FlapDockStation station, ButtonPane buttons ){
		super( ThemeManager.SPAN_FACTORY + ".flap", station );
		this.buttons = buttons;
		this.station = station;
	}

	@Override
	protected int getNumberOfDockables(){
		return buttons.getNumberOfButtons();
	}
	
	@Override
	protected boolean isHorizontal(){
		switch( station.getDirection() ){
			case NORTH:
			case SOUTH:
				return true;
			default:
				return false;
		}
	}
	
	@Override
	protected void spanResized(){
		buttons.spanResized();
	}
}
