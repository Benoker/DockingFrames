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

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.JToolTip;

/**
 * A {@link TooltipStrategy} is used by a {@link GlassedPane} to find out which tooltip present.
 * @author Benjamin Sigg
 */
public interface TooltipStrategy {
	/**
	 * Called if a {@link GlassedPane} starts using this strategy.
	 * @param pane the pane using this strategy
	 */
	public void install( GlassedPane pane );
	
	/**
	 * Called if a {@link GlassedPane} is no longer using this strategy.
	 * @param pane the pane that is no longer using this strategy
	 */
	public void uninstall( GlassedPane pane );
	
	/**
	 * Asks this strategy to find the tooltip of <code>component</code>, after the <code>event</code> is
	 * dispatched.
	 * @param component the current component, can be <code>null</code>
	 * @param event the event that is dispatched, can be <code>null</code>
	 * @param overNewComponent whether the component under the mouse changed since the last call of this method
	 * @param callback the caller, this method can call {@link TooltipStrategyCallback#setToolTipText(String)}
	 * to set a new tooltip
	 */
	public void setTooltipText( Component component, MouseEvent event, boolean overNewComponent, TooltipStrategyCallback callback );
	
	/**
	 * Asks this strategy to create a new tooltip, this tooltip will be shown as soon as the current
	 * event of the <code>EDT</code> is over.
	 * @param component the component over which the mouse currently hovers, can be <code>null</code>
	 * @param callback the caller of this method
	 * @return the new tooltip, must not be <code>null</code>
	 */
	public JToolTip createTooltip( Component component, TooltipStrategyCallback callback );
}
