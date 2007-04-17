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


package bibliothek.gui.dock.station.flap;

import javax.swing.Icon;

import bibliothek.gui.DockUI;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.actions.GroupedSelectableDockAction;
import bibliothek.gui.dock.event.FlapDockListener;
import bibliothek.gui.dock.station.FlapDockStation;

/**
 * This {@link DockAction} is shown together with the children of a 
 * {@link FlapDockStation}, and allows to "stick" a {@link Dockable}.
 * When a {@link Dockable} is sticked, it will not be disposed by the
 * {@link FlapDockStation}, when it looses the focus. In fact, this 
 * action just uses the {@link FlapDockStation#setHold(Dockable, boolean) setHold}-method
 * of the station.
 * @author Benjamin Sigg
 */
public class FlapDockHoldToggle extends GroupedSelectableDockAction.Check<Boolean> {
    private FlapDockStation flap;
    
    /**
     * Constructor, sets the icons and makes the action ready to be shown.
     * @param station The station on which the {@link Dockable Dockables}
     * are registered.
     */
    public FlapDockHoldToggle( FlapDockStation station ){
        super( null );
        setRemoveEmptyGroups( false );
        flap = station;
        
        station.addFlapDockStationListener( new FlapDockListener(){
            public void holdChanged( FlapDockStation station, Dockable dockable, boolean hold ) {
                if( isBinded( dockable ))
                    setGroup( hold, dockable );
            }
        });
        
        setIcon( Boolean.FALSE, createUpIcon() );
        setSelectedIcon( Boolean.TRUE, createDownIcon() );
        
        setSelected( Boolean.FALSE, false );
        setSelected( Boolean.TRUE, true );
        
        setText( Boolean.FALSE, DockUI.getDefaultDockUI().getString( "flap.stick.false" ) );
        setText( Boolean.TRUE, DockUI.getDefaultDockUI().getString( "flap.stick.true" ) );
        
        setTooltipText( Boolean.FALSE, DockUI.getDefaultDockUI().getString( "flap.stick.false.tooltip" ) );
        setTooltipText( Boolean.TRUE, DockUI.getDefaultDockUI().getString( "flap.stick.true.tooltip" ));
    }
    
    @Override
    public void setSelected( Dockable dockable, boolean selected ){
    	flap.setHold( dockable, selected );
    	setGroup( selected, dockable );
    }
    
    @Override
    protected Boolean createGroupKey( Dockable dockable ) {
        return flap.isHold( dockable );
    }
    
    /**
     * Creates an icon that is shown for {@link Dockable Dockables} 
     * which are sticked.
     * @return The icon for sticked children
     */
    protected Icon createDownIcon(){
        return DockUI.getDefaultDockUI().getIcon( "flap.hold" );
    }
    
    /**
     * Creates an icon that is shown for {@link Dockable Dockables}
     * which are not sticked.
     * @return The icon for non-sticked children
     */
    protected Icon createUpIcon(){
        return DockUI.getDefaultDockUI().getIcon( "flap.free" );
    }
}
