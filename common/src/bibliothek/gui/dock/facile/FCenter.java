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
package bibliothek.gui.dock.facile;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.event.DoubleClickListener;
import bibliothek.gui.dock.facile.intern.FControlAccess;
import bibliothek.gui.dock.facile.intern.FStateManager;
import bibliothek.gui.dock.facile.intern.FacileDockable;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.station.FlapDockStation.Direction;

/**
 * A component that can is normally set into the center of the
 * main- {@link JFrame} of an application. This component can display
 * and manage some {@link FDockable}s.<br>
 * This component contains in the center a {@link SplitDockStation} allowing
 * to show several {@link FDockable}s at the same time. At each border a
 * {@link FlapDockStation} allows to show "minimized" {@link FDockable}s.<br>
 * Note: clients should not create {@link FCenter}s directly, they should use
 * a {@link FControl} to provide one <code>FCenter</code>. 
 * @author Benjamin Sigg
 */
public class FCenter extends JComponent{
    /** the child in the center */
    private SplitDockStation center;
    
    /** the child at the north border */
    private FlapDockStation north;
    /** the child at the south border */
    private FlapDockStation south;
    /** the child at the east border */
    private FlapDockStation east;
    /** the child at the west border */
    private FlapDockStation west;
    
    /**
     * Creates a new center.
     * @param access connection to a {@link FControl}
     */
    public FCenter( final FControlAccess access ){
        center = new SplitDockStation(){
            @Override
            protected ListeningDockAction createFullScreenAction() {
                return null;
            }
        };
        center.setExpandOnDoubleclick( false );
        
        north = new FlapDockStation();
        south = new FlapDockStation();
        east = new FlapDockStation();
        west = new FlapDockStation();
        
        north.setAutoDirection( false );
        north.setDirection( Direction.SOUTH );
        
        south.setAutoDirection( false );
        south.setDirection( Direction.NORTH );
        
        east.setAutoDirection( false );
        east.setDirection( Direction.WEST );
        
        west.setAutoDirection( false );
        west.setDirection( Direction.EAST );
        
        setLayout( new BorderLayout() );
        add( center, BorderLayout.CENTER );
        add( north.getComponent(), BorderLayout.NORTH );
        add( south.getComponent(), BorderLayout.SOUTH );
        add( east.getComponent(), BorderLayout.EAST );
        add( west.getComponent(), BorderLayout.WEST );
        
        FStateManager state = access.getStateManager();
        state.add( "center center", center );
        state.add( "center south", south );
        state.add( "center north", north );
        state.add( "center east", east );
        state.add( "center west", west );
        
        DockFrontend frontend = access.getOwner().getFrontend();
        frontend.addRoot( center, "center center" );
        frontend.addRoot( north, "center north" );
        frontend.addRoot( south, "center south" );
        frontend.addRoot( east, "center east" );
        frontend.addRoot( west, "center west" );
        frontend.setDefaultStation( center );
        
        frontend.getController().getDoubleClickController().addListener( new DoubleClickListener(){
            public DockElement getTreeLocation() {
                return center;
            }
            public boolean process( Dockable dockable, MouseEvent event ) {
                if( event.isConsumed() )
                    return false;
                
                if( dockable != center ){
                    if( dockable instanceof FacileDockable ){
                        FDockable fdockable = ((FacileDockable)dockable).getDockable();
                        if( center.getFullScreen() != dockable && fdockable.isMaximizable() ){
                            access.getStateManager().setMode( dockable, FStateManager.MAXIMIZED );
                            event.consume();
                            return true;
                        }
                        else if( center.getFullScreen() == dockable ){
                            access.getStateManager().setMode( dockable, FStateManager.NORMALIZED );
                            event.consume();
                            return true;
                        }
                    }
                }
                
                return false;
            }
        });
    }
}
