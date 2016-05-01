/**
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

package bibliothek.gui.dock.action;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Suppresses a popup to show
 * @author Benjamin Sigg
 */
public interface ActionPopupSuppressor {
    /**
     * Allows every popup to appear.
     */
    public static final ActionPopupSuppressor ALLOW_ALWAYS = new ActionPopupSuppressor(){
        public boolean suppress( Dockable dockable, DockActionSource source ) {
            return false;
        }
    };
    
    /**
     * Never allows a popup to appear.
     */
    public static final ActionPopupSuppressor SUPPRESS_ALWAYS = new ActionPopupSuppressor(){
        public boolean suppress( Dockable dockable, DockActionSource source ) {
            return true;
        }
    };
    
    /**
     * Allows a popup only to appear if there are no titles for a {@link Dockable}
     * or if more than one action would be shown.
     */
    public static final ActionPopupSuppressor BALANCED = new ActionPopupSuppressor(){
        public boolean suppress( Dockable dockable, DockActionSource source ) {
            if( source.getDockActionCount() > 1 )
                return false;
            
            DockTitle[] titles = dockable.listBoundTitles();
            if( titles == null || titles.length == 0 )
                return false;
            
            return true;
        }
    };
    
    /**
     * Tells whether to suppress or to allow a popup for <code>source</code>.
     * @param dockable the Dockable for which the popup would be shown
     * @param source the source which would be shown
     * @return <code>true</code> if nothing should be shown, <code>false</code>
     * if the popup is not suppressed
     */
    public boolean suppress( Dockable dockable, DockActionSource source );
}
