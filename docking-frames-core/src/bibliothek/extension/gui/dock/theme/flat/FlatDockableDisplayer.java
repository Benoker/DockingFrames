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
package bibliothek.extension.gui.dock.theme.flat;

import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayerDecorator;
import bibliothek.gui.dock.themes.basic.TabDecorator;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A {@link DockableDisplayer} that uses a {@link FlatBorder}.
 * @author Benjamin Sigg
 */
public class FlatDockableDisplayer extends BasicDockableDisplayer {
    private FlatBorder border;
    
    public FlatDockableDisplayer( DockStation station, Dockable dockable, DockTitle title, Location location ){
        super( station, dockable, title, location );
        border = new FlatBorder( this );
        
        setDefaultBorderHint( true );
        setRespectBorderHint( true );
        setSingleTabShowInnerBorder( false );
        setSingleTabShowOuterBorder( true );
    }
    
    @Override
    public void setController( DockController controller ) {
        super.setController( controller );
        border.connect( controller );
    }
    
    @Override
    protected Border getDefaultBorder() {
        return border;
    }
    
    @Override
    protected BasicDockableDisplayerDecorator createTabDecorator() {
    	return new TabDecorator( getStation(), FlatTheme.ACTION_DISTRIBUTOR );
    }
    
    @Override
    protected BasicDockableDisplayerDecorator createStackedDecorator(){
    	return createStackedDecorator( FlatTheme.ACTION_DISTRIBUTOR );
    }
}
