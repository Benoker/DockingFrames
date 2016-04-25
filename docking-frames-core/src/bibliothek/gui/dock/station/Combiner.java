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
import bibliothek.gui.dock.control.relocator.Inserter;
import bibliothek.gui.dock.control.relocator.Merger;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.station.support.Enforcement;

/**
 * A <code>Combiner</code> merges two {@link Dockable Dockables} into
 * one {@link Dockable}. How this is done, is up to the Combiner, but
 * most Combiners will create a new {@link DockStation} and put the Dockables
 * onto this new station.
 * @see Merger
 * @see Inserter
 */
public interface Combiner {
    /**
     * Prepares information telling how two {@link Dockable}s may be combined.
     * @param source the {@link Dockable}s which may be combined, their parent station and
     * other helpful information.
     * @param force how much the caller of this method would like the method not to return a 
     * value of <code>null</code>. Implementations should read the {@link Enforcement#getForce() force}
     * property, where a value of <code>1</code> means that this method must success, while <code>0</code>
     * means that this method may or may not success. In general the modules in the framework expect that a value below
     * <code>0.5</code> results in <code>null</code>, while a value above <code>0.5f</code> results in a non-<code>null</code>
     * value.
     * @return How to combine the {@link Dockable}s, may be <code>null</code> to indicate that
     * a combination is not desired
     */
    public CombinerTarget prepare( CombinerSource source, Enforcement force );
	
	/**
     * Merges two {@link Dockable}s into a new Dockable. This method may
     * set the {@link DockController} of the created element in order to initialize 
     * it more efficiently.
     * @param source information about the two {@link Dockable}s that are going to be merged, not <code>null</code>. This 
     * object may or may not have been created by this {@link Combiner}, some sanity checks are advised before using it
     * @param target information that was created by {@link #prepare(CombinerSource, Enforcement)} using <code>source</code>, not <code>null</code>
     * @return the combined {@link Dockable}, not <code>null</code>
     */
    public Dockable combine( CombinerSource source, CombinerTarget target );
    
	/**
	 * Prepares the layout of the {@link DockStation}s that are created by this {@link Combiner} to
	 * keep track of a new {@link DockableProperty} with its own placeholder. The new property
	 * is set "aside" an existing location. For more information please read the documentation
	 * of {@link DockStation#asDockable()}.
	 * @param request information about a location and methods to create the neighbor location
	 * @see DockStation#aside(AsideRequest)
	 */
	public void aside( AsideRequest request );
}
