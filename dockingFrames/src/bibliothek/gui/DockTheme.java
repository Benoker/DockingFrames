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

import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.focus.DockableSelection;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.themes.DockThemeExtension;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.util.Priority;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Version;

/**
 * A theme describes how a {@link DockStation} looks like, which
 * {@link DockTitle} are selected, and other behavior. A theme needs
 * only to support one {@link DockController} at a time.<br>
 * Warning: this interface will get a big update in version 1.1.0, backwards compatibility
 * will be broken.
 * @author Benjamin Sigg
 */
@Todo(compatibility=Compatibility.COMPATIBLE, target=Version.VERSION_1_1_0, priority=Todo.Priority.MAJOR,
		description="Each property of DockTheme gets a PropertyKey: the key is associated with a factory/wrapper that just calls the DockTheme's methods. But clients can easily replace the factory/wrapper by their own implementation.")
public interface DockTheme {
    /**
     * Install this theme at <code>controller</code>. The theme
     * may change any properties it likes.
     * @param controller the controller
     * @param extensions a set of extensions specifically for this theme
     */
    public void install( DockController controller, DockThemeExtension[] extensions );
    
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
     * Gets the default {@link DockTitleFactory} which is used if no other factory is set.<br>
     * The result of this method is installed in the {@link DockTitleManager} using
     * the key {@link DockTitleManager#THEME_FACTORY_ID} and priority {@link Priority#THEME}. A
     * theme may use the manager to change the factory at any time.
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
    
    /**
     * Gets a selector for {@link Dockable}s.
     * @param controller the controller for which the selector will be used
     * @return the selector
     */
    public DockableSelection getDockableSelection( DockController controller );
}
