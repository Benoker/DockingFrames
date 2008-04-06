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
package bibliothek.extension.gui.dock.theme.bubble;

import java.awt.Point;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.FlapDockStation.ButtonContent;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A {@link DockTitle} used for the buttons on a {@link FlapDockStation}.
 * @author Benjamin Sigg
 */
public class BubbleButtonDockTitle extends BubbleDockTitle{
    /**
     * A factory which creates new {@link BubbleButtonDockTitle}s.
     */
    public static final DockTitleFactory FACTORY = new DockTitleFactory(){
        public DockTitle createDockableTitle( Dockable dockable, DockTitleVersion version ) {
            return new BubbleButtonDockTitle( dockable, version );
        }
        public <D extends Dockable & DockStation> DockTitle createStationTitle( D dockable, DockTitleVersion version ) {
            return new BubbleButtonDockTitle( dockable, version );
        }
    };
    
    private ButtonContent behavior;
    
    /**
     * Creates a new title.
     * @param dockable the dockable for which this title will be shown
     * @param origin the {@link DockTitleVersion} which was used to create this title
     */
    public BubbleButtonDockTitle( Dockable dockable, DockTitleVersion origin ) {
        behavior = FlapDockStation.ButtonContent.THEME_DEPENDENT;
        if( origin != null )
            behavior = origin.getController().getProperties().get( FlapDockStation.BUTTON_CONTENT );
        
        init( dockable, origin, behavior.showActions( false ) );
    }
    
    @Override
    protected void updateIcon() {
        String text = getDockable().getTitleText();
        if( behavior.showIcon( text != null && text.length() > 0, true ) )
            super.updateIcon();
        else
            setIcon( null );
    }
    
    @Override
    protected void updateText() {
        if( behavior.showText( getDockable().getTitleIcon() != null, true ) )
            super.updateText();
        else
            setText( "" );     
    }
    
    @Override
    public Point getPopupLocation( Point click ){
        return null;
    }
    
    @Override
    public void setOrientation( Orientation orientation ) {
        switch( orientation ){
            case SOUTH_SIDED:
            case NORTH_SIDED:
            case FREE_HORIZONTAL:
                orientation = Orientation.FREE_HORIZONTAL;
                break;
            case EAST_SIDED:
            case WEST_SIDED:
            case FREE_VERTICAL:
                orientation = Orientation.FREE_VERTICAL;
                break;
        }
        
        super.setOrientation( orientation );
    }
}
