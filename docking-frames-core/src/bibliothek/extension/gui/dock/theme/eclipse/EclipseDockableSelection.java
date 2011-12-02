/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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

import java.awt.Color;

import javax.swing.BorderFactory;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.focus.DefaultDockableSelection;
import bibliothek.gui.dock.focus.DockableSelection;
import bibliothek.gui.dock.themes.color.DockableSelectionColor;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * A {@link DockableSelection} that uses its own border.
 * @author Benjamin Sigg
 */
@ColorCodes( {"selection.border"} )
public class EclipseDockableSelection extends DefaultDockableSelection {
    private DockableSelectionColor borderColor;
   
    /**
     * Creates the new selection
     */
    public EclipseDockableSelection(){
        borderColor = new DockableSelectionColor( this, "selection.border", Color.BLACK ){
            @Override
            protected void changed( Color oldColor, Color newColor ) {
                setBorder( BorderFactory.createLineBorder( newColor ));
            }
        };
    }
    
    @Override
    public void open( DockController controller ) {
        borderColor.setManager( controller.getColors() );
        super.open( controller );
    }
    
    @Override
    public void close() {
        borderColor.setManager( null );
        super.close();
    }
}
