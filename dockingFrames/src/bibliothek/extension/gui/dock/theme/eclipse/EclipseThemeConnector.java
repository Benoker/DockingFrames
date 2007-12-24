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
package bibliothek.extension.gui.dock.theme.eclipse;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleManager;

/**
 * @author Janni Kovacs
 */
public interface EclipseThemeConnector {
    /**
     * Describes which kind of title, and which kind of {@link DockableDisplayer} 
     * should be used for a {@link Dockable} or a {@link DockStation}. This
     * enumeration is mainly used by {@link EclipseThemeConnector#getTitleBarKind(Dockable)}
     * @author Benjamin Sigg
     */
    public enum TitleBar{
        /** 
         * The very basic kind that uses a {@link DockTitle} to mark a {@link Dockable}. There
         * will be a border around the whole <code>Dockable</code><br>
         * Note: the <code>DockTitle</code> will be created using the {@link DockTitleManager}-system,
         * clients can change the kind of title by:<br>
         * <ul>
         *  <li>replacing the factories directly through the <code>DockTitleManager</code>, clients
         *  should then encapsulate their new factories in a {@link EclipseDockTitleFactory}</li>
         *  <li>using {@link EclipseTheme#setTitleFactory(bibliothek.gui.dock.title.DockTitleFactory)}</li>
         * </ul>
         */
        BASIC_BORDERED,
        /**
         * The same as {@link #BASIC_BORDERED}, but without the border
         */
        BASIC,
        /**
         * The special kind of tabs that are used when multiple {@link Dockable}s are combined
         * and the {@link EclipseTheme} is active. This type always has a
         * border.
         */
        ECLIPSE,
        /** No title at all, but with the typical eclipse-border */
        NONE_BORDERED,
        /** No title and no border */
        NONE
    }
    
    /**
     * Tells which kind of title and {@link DockableDisplayer} should be 
     * shown for <code>dockable</code> assuming everything can be chosen
     * freely.
     * @param dockable the element that stands alone on a {@link DockStation}
     * @return which kind of title and displayer should be used
     */
	public TitleBar getTitleBarKind( Dockable dockable );
		
	/**
	 * Tells whether <code>action</code> should be displayed on the tab
	 * of <code>dockable</code> or on the right side.
	 * @param dockable the owner of <code>action</code>
	 * @param action the action to display
	 * @return <code>true</code> if <code>action</code> should be child of
	 * the tab
	 */
	public boolean isTabAction( Dockable dockable, DockAction action );
}
