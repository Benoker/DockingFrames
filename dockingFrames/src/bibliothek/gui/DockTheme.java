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

package bibliothek.gui;

import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleManager;

/**
 * A theme describes how a {@link DockStation} looks like, which
 * {@link DockTitle} are selected, and other behavior.
 * @author Benjamin Sigg
 */
public interface DockTheme {
    
    /**
     * Install this theme at <code>controller</code>. The theme
     * may change any properties it likes.
     * @param controller the controller
     */
    public void install( DockController controller );
    
    /**
     * Uninstalls this theme from <code>controller</code>. The theme
     * has to remove all listeners it added. 
     * @param controller the controller
     */
    public void uninstall( DockController controller );
        
    /**
     * Gets the Combiner for <code>station</code>.
     * @param station the station whose combiner is searched
     * @return a combiner for <code>station</code>
     */
    public Combiner getCombiner( DockStation station );
    
    /**
     * Gets the paint which is used to draw things onto <code>station</code>.
     * @param station the station to paint on
     * @return the paint for <code>station</code>
     */
    public StationPaint getPaint( DockStation station );
    
    /**
     * Gets a displayer factory for <code>station</code>.
     * @param station the station on which the created {@link DockableDisplayer}
     * is shown
     * @return the factory to create displayer
     */
    public DisplayerFactory getDisplayFactory( DockStation station );
    
    /**
     * Gets the default {@link DockTitleFactory} which is used if no
     * other factory is set.<br>
     * To replace all factories, the method {@link DockTitleManager#registerTheme(String, DockTitleFactory)}
     * should be used.
     * @param controller the controller using this theme
     * @return the factory
     */
    public DockTitleFactory getTitleFactory( DockController controller );
    
    /**
     * Gets a factory for images which are moved around by the user.
     * @param controller the controller for which the factory is needed
     * @return a factory
     */
    public DockableMovingImageFactory getMovingImageFactory( DockController controller );
}
