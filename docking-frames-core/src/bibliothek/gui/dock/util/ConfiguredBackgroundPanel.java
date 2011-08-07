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

import java.awt.LayoutManager;

import bibliothek.gui.dock.util.BackgroundComponent.Transparency;

/**
 * A {@link BackgroundPanel} implementing {@link #configure(Transparency)}. This implementation
 * just changes the {@link #setSolid(boolean) solid} and {@link #setTransparent(boolean) transparent}
 * property according to the current {@link Transparency}. 
 * @author Benjamin Sigg
 */
public class ConfiguredBackgroundPanel extends BackgroundPanel{
	private boolean defaultSolid;
	private boolean defaultTransparent;
	
	/**
	 * Creates a new panel.
	 * @param layout the layout manager to use on this panel, can be <code>null</code>
	 * @param solid the default value of the solid property, whether all pixels on the background should be painted
	 * @param transparent the default value of the transparency property, whether no pixels on the background should be painted
	 */
	public ConfiguredBackgroundPanel( LayoutManager layout, boolean solid, boolean transparent ){
		super( layout, solid, transparent );
		defaultSolid = solid;
		defaultTransparent = transparent;
	}

	public ConfiguredBackgroundPanel( boolean solid, boolean transparent ){
		super( solid, transparent );
		defaultSolid = solid;
		defaultTransparent = transparent;
	}
	
	@Override
	protected void configure( Transparency transparency ){
		switch( transparency ){
			case DEFAULT:
				setTransparent( defaultTransparent );
				setSolid( defaultSolid );
				break;
			case SOLID:
				setTransparent( false );
				setSolid( true );
				break;
			case TRANSPARENT:
				setTransparent( true );
				setSolid( false );
				break;
		}
	}
}
