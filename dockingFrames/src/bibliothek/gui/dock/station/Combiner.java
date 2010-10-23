/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

package bibliothek.gui.dock.station;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerTarget;

/**
 * A <code>Combiner</code> merges two {@link Dockable Dockables} into
 * one {@link Dockable}. How this is done, is up to the Combiner, but
 * most Combiners will create a new {@link DockStation} and put the Dockables
 * onto this new station.
 */
public interface Combiner {
    /**
     * Prepares information telling how two {@link Dockable}s may be combined.
     * @param source the {@link Dockable} which may be combined, their parent station and
     * other helpful information.
     * @param force if <code>true</code> then a combination must happen, otherwise the
     * result may be <code>null</code> indicating that a combination is not desired by 
     * this {@link Combiner}.
     * @return How to combine the {@link Dockable}s, may be <code>null</code> to indicate that
     * a combination is not desired
     */
    public CombinerTarget prepare( CombinerSource source, boolean force );
	
	/**
     * Merges two {@link Dockable}s into a new Dockable. This method may
     * set the {@link DockController} of the created element in order to initialize 
     * it more efficiently.
     * @param source information about the two {@link Dockable}s that are going to be merged, not <code>null</code>. This 
     * object may or may not have been created by this {@link Combiner}, some sanity checks are advised before using it
     * @param target information that was created by {@link #prepare(CombinerSource, boolean)} using <code>source</code>, not <code>null</code>
     * @return the combined {@link Dockable}, not <code>null</code>
     */
    public Dockable combine( CombinerSource source, CombinerTarget target );
    

}
