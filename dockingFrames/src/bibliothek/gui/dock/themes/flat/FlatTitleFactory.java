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

package bibliothek.gui.dock.themes.flat;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.title.*;

/**
 * A factory that creates instances of {@link DefaultDockTitle}, but
 * changes their active and inactive right color to the Dockables background.
 * If {@link JComponent#updateUI() updateUI} is called, the colors will be 
 * updated as well.
 * @author Benjamin Sigg
 */
public class FlatTitleFactory implements DockTitleFactory{
    public DockTitle createDockableTitle( Dockable dockable, DockTitleVersion version ) {
        DefaultDockTitle title = new DefaultDockTitle( dockable, version ){
            @Override
            public void updateUI() {
                super.updateUI();
                if( getDockable() != null ){
                    Color background = getDockable().getComponent().getBackground();
                    setInactiveRightColor( background );
                    setActiveRightColor( background );
                }
            }
        };
        
        Color background = dockable.getComponent().getBackground();
        title.setInactiveRightColor( background );
        title.setActiveRightColor( background );
        
        return title;
    }
    
    public <D extends Dockable & DockStation> DockTitle createStationTitle( 
            D dockable, DockTitleVersion version ){
        
        AbstractDockTitle title = new AbstractDockTitle( dockable, version );
        title.setBorder( BorderFactory.createLineBorder( title.getBackground().darker() ));
        return title;
    }
}
