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
package bibliothek.gui.dock.extension.css.transition;

/**
 * This listener can be added to a {@link RuleChainLink} and will be informed if the link changes its
 * position in the chain.
 * @author Benjamin Sigg
 */
public interface RuleChainLinkListener {
	/**
	 * Called if the predecessor of <code>source</code> changed.
	 * @param source the source of the event
	 * @param oldPrevious the old predecessor, may be <code>null</code>
	 * @param newPrevious the new predecessor, may be <code>null</code>
	 */
	public void previousChanged( RuleChainLink source, RuleChainLink oldPrevious, RuleChainLink newPrevious );
	
	/**
	 * Called if the successor of <code>source</code> changed.
	 * @param source the source of the event
	 * @param oldNext the old successor, may be <code>null</code>
	 * @param newNext the new successor, may be <code>null</code>
	 */
	public void nextChanged( RuleChainLink source, RuleChainLink oldNext, RuleChainLink newNext );
	
	/**
	 * Called after <code>source</code> has been removed from the chain. This is always the
	 * last event a link fires, it will never again be part of the chain.
	 * @param source the link that was removed
	 */
	public void removed( RuleChainLink source );
}
