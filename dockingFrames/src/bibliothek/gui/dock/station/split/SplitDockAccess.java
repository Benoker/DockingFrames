/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.station.split;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Provides access to some internal methods and attributes of a {@link SplitDockStation}. An access
 * is granted only to a few selected friend classes.
 * @author Benjamin Sigg
 */
public interface SplitDockAccess {
	/**
	 * Gets the station to which this object grants access.
	 * @return the owner
	 */
    public SplitDockStation getOwner();
        
    /**
     * Gets the {@link DockableDisplayer} which is currently in fullscreen-mode.
     * @return the displayer, may be <code>null</code>
     */
    public DockableDisplayer getFullScreenDockable();
    
    /**
     * Tests whether the given value of the divider is a legal value or not.
     * @param divider The value of the divider
     * @param node the node for which the divider is validated
     * @return a legal value, as near as possible to <code>divider</code>.
     */
    public double validateDivider( double divider, Node node );
    
    /**
     * Creates a new {@link DockableDisplayer} for the <code>dockable</code> with a preset
     * <code>title</code>.
     * @param dockable the {@link DockableDisplayer} which will be child of the displayer
     * @param title the title of the dockable, might be <code>null</code>
     * @return the newly created displayer
     */
    public DockableDisplayer createDockableDisplayer( Dockable dockable, DockTitle title );
    
    /**
     * Adds a displayer to the list of all known displayers.
     * @param displayer the new displayer
     */
    public void add( DockableDisplayer displayer );
    
    /**
     * Adds <code>dockable</code> to the list of children, generates a 
     * title and a {@link Leaf} for <code>dockable</code>, but does not
     * add the leaf to this station. The location of the leaf has be set by
     * the caller. 
     * @param dockable the new child of this station.
     * @return the {@link Leaf} for <code>dockable</code>
     */
    public Leaf createLeaf( Dockable dockable );
}
