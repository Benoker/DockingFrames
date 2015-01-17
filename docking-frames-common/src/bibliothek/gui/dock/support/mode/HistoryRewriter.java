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
package bibliothek.gui.dock.support.mode;

import bibliothek.gui.Dockable;

/**
 * A {@link HistoryRewriter} can rewrite the history information that is associated with a {@link Dockable} in a
 * specific {@link Mode}. HistoryRewriters are applied at the moment when history information is read, and are 
 * used by the {@link ModeManager}.
 * @author Benjamin Sigg
 * @param <M> the kind of {@link Mode} this rewriter accepts
 * @param <H> the kind of history this rewriter accepts
 */
public interface HistoryRewriter<H, M extends Mode<H>> {
	/**
	 * Checks whether the history object <code>history</code> is still valid.
	 * @param dockable the element which is about to change its mode
	 * @param mode the mode that is going to be applied
	 * @param history the history object that will be forwarded to <code>mode</code>, may be <code>null</code>
	 * @return the history object to use, may be <code>null</code>
	 */
	public H rewrite( Dockable dockable, M mode, H history );
}
