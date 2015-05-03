/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.util;

import java.awt.Graphics;
import java.awt.LayoutManager;


/**
 * A {@link BackgroundPanel} implementing {@link #configure(Transparency)}. This implementation
 * just changes the {@link #setTransparency(Transparency) transparency property} property according to the current
 * {@link Transparency}. 
 * @author Benjamin Sigg
 */
public class ConfiguredBackgroundPanel extends BackgroundPanel{
	/** The {@link Transparency} to use if {@link #configure(Transparency)} is called with {@link Transparency#DEFAULT} */
	private Transparency defaultTransparency = Transparency.DEFAULT;
	
	/**
	 * Creates a new panel.
	 * @param layout the layout manager to use on this panel, can be <code>null</code>
	 * @param transparency the default transparency to use if nothing else is set
	 */
	public ConfiguredBackgroundPanel( LayoutManager layout, Transparency transparency ){
		super( layout, transparency );
		defaultTransparency = transparency;
	}

	/**
	 * Creates a new panel.
	 * @param transparency the default transparency to use if nothing else is set
	 */
	public ConfiguredBackgroundPanel( Transparency transparency ){
		super( transparency );
		defaultTransparency = transparency;
	}
	
	@Override
	protected void configure( Transparency transparency ){
		switch( transparency ){
			case DEFAULT:
				setTransparency( defaultTransparency );
				break;
			case SOLID:
			case TRANSPARENT:
				setTransparency( transparency );
				break;
		}
	}
	
	@Override
	protected void setupRenderingHints( Graphics g ) {
		// ignore	
	}
}
