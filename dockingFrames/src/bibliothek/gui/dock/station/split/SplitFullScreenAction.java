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

import javax.swing.Icon;

import bibliothek.gui.DockUI;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.actions.GroupedButtonDockAction;
import bibliothek.gui.dock.event.SplitDockListener;
import bibliothek.gui.dock.station.SplitDockStation;

/**
 * This {@link DockAction} is mainly used by the {@link SplitDockStation}
 * to allow it's children to get in fullscreen-mode.
 * @author Benjamin Sigg
 */
public class SplitFullScreenAction extends GroupedButtonDockAction<Boolean> {
    private SplitDockStation split;
    
    /**
     * Constructs the action and sets the <code>station</code> on
     * which the {@link Dockable Dockables} will be made fullscreen.
     * @param station the station
     */
    public SplitFullScreenAction( SplitDockStation station ){
        super( null );
        
        split = station;
        setRemoveEmptyGroups( false );
        
        station.addSplitDockStationListener( new SplitDockListener(){
            public void fullScreenDockableChanged( SplitDockStation station, Dockable oldFullScreen, Dockable newFullScreen ) {
                if( oldFullScreen != null ){
                    change( oldFullScreen, Boolean.FALSE );
                }
                
                if( newFullScreen != null ){
                    change( newFullScreen, Boolean.TRUE );
                }
            }
        });
        
        setIcon( Boolean.TRUE, createNormalizeIcon() );
        setIcon( Boolean.FALSE, createMaximizeIcon() );
        
        setText( Boolean.TRUE, DockUI.getDefaultDockUI().getString( "split.normalize" ) );
        setText( Boolean.FALSE, DockUI.getDefaultDockUI().getString( "split.maximize" ) );
        
        setTooltipText( Boolean.TRUE, DockUI.getDefaultDockUI().getString( "split.normalize.tooltip" ));
        setTooltipText( Boolean.FALSE, DockUI.getDefaultDockUI().getString( "split.maximize.tooltip" ));
    }
    
    public void action( Dockable dockable ) {
        while( dockable.getDockParent() != split ){
            DockStation station = dockable.getDockParent();
            if( station == null )
                return;
            
            dockable = station.asDockable();
            if( dockable == null )
                return;
        }
        
        Dockable fullscreen = split.getFullScreen();
        
        if( fullscreen == dockable )
            split.setFullScreen( null );
        else
            split.setFullScreen( dockable );
    }
    
    private void change( Dockable dockable, Boolean value ){
        if( isKnown( dockable ))
            setGroup( value, dockable );
        
        DockStation station = dockable.asDockStation();
        if( station != null ){
            for( int i = 0, n = station.getDockableCount(); i<n; i++ )
                change( station.getDockable(i), value );
        }
    }
    
    @Override
    protected Boolean createGroupKey( Dockable dockable ){
        while( dockable.getDockParent() != split ){
            DockStation station = dockable.getDockParent();
            if( station == null )
                return Boolean.FALSE;
            
            dockable = station.asDockable();
            if( dockable == null )
                return Boolean.FALSE;
        }
        
        if( dockable == split.getFullScreen() )
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }

    /**
     * Creates the icon that is shown on {@link Dockable Dockables} which
     * are in fullscreen-mode.
     * @return the icon
     */
    protected Icon createNormalizeIcon(){
        return DockUI.getDefaultDockUI().getIcon( "split.normalize" );
    }
    
    /**
     * Creates the icon that is shown on {@link Dockable Dockables} which
     * are not in fullscreen-mode.
     * @return the icon
     */
    protected Icon createMaximizeIcon(){
        return DockUI.getDefaultDockUI().getIcon( "split.maximize" );
    }
}
