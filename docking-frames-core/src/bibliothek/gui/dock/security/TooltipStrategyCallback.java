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
package bibliothek.gui.dock.security;

import javax.swing.JToolTip;

/**
 * A callback forwarded to a {@link TooltipStrategy}, allows the strategy to configure
 * a {@link GlassedPane}.
 * @author Benjamin Sigg
 */
public interface TooltipStrategyCallback {
	/**
	 * Gets the owner of this callback.
	 * @return the owner, never <code>null</code>
	 */
	public GlassedPane getGlassedPane();
	
	/**
	 * Sets the tooltip that should currently be shown.
	 * @param text the current tooltip, can be <code>null</code>
	 */
	public void setToolTipText( String text );
	
	/**
	 * Gets the currently shown tooltip.
	 * @return the current tooltip, can be <code>null</code> 
	 */
	public String getToolTipText();
	
	/**
	 * Default method for creating a new {@link JToolTip}.
	 * @return some new tooltip, never <code>null</code>
	 */
	public JToolTip createToolTip();
}
