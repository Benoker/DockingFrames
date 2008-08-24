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
package bibliothek.gui.dock.station.screen;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.event.DockableAdapter;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A window that uses a {@link DockableDisplayer} to show the {@link Dockable}.
 * @author Benjamin Sigg
 */
public abstract class DisplayerScreenDockWindow implements ScreenDockWindow {
    /** the owner of this station */
    private ScreenDockStation station;
    
    /** the dockable shown on this station */
    private DockableDisplayer displayer;
    
    /** the controller in whose realm this window works */
    private DockController controller;
    
    /** whether the {@link DockTitle} should be shown */
    private boolean showTitle = true;
    
    /** a listener for the current {@link Dockable}, changes the title when requested */
    private DockableListener listener = new DockableAdapter(){
        @Override
        public void titleExchanged( Dockable dockable, DockTitle title ) {
            DockUtilities.exchangeTitle( displayer, station.getTitleVersion() );
        }
    };
    
    /**
     * Creates a new window
     * @param station the owner of this window, not <code>null</code>
     */
    public DisplayerScreenDockWindow( ScreenDockStation station  ){
        if( station == null )
            throw new IllegalArgumentException( "station must not be null" );
        this.station = station;
    }
    
    /**
     * Forces the subclass of this window to show <code>displayer</code>. Only
     * one displayer should be shown at any time. A new displayer replaces
     * an old one. 
     * @param displayer the displayer to show or <code>null</code> to remove
     * the current displayer
     */
    protected abstract void showDisplayer( DockableDisplayer displayer );
    
    /**
     * Gets the component on which {@link ScreenDockWindow#setWindowBounds(java.awt.Rectangle)}
     * is applied.
     * @return the base component
     */
    protected abstract Component getWindowComponent();

    /**
     * Sets whether the {@link DockTitle} should be shown or not.
     * @param showTitle <code>true</code> if the title should be visible,
     * <code>false</code> otherwise
     */
    public void setShowTitle( boolean showTitle ) {
        if( this.showTitle != showTitle ){
            this.showTitle = showTitle;
            
            if( displayer != null ){
                if( showTitle ){
                    Dockable dockable = displayer.getDockable();
                    DockTitle title = station.createDockTitle( dockable );
                    if( title != null ){
                        dockable.bind( title );
                        displayer.setTitle( title );
                    }
                }
                else{
                    DockTitle title = displayer.getTitle();
                    if( title != null ){
                        displayer.setTitle( null );
                        displayer.getDockable().unbind( title );
                    }
                }
            }
        }
    }
    
    /**
     * Tells whether the {@link DockTitle} is generally shown.
     * @return <code>true</code> if the title is shown
     */
    public boolean isShowTitle() {
        return showTitle;
    }
    
    public Dockable getDockable() {
        if( displayer == null )
            return null;
        
        return displayer.getDockable();
    }

    public void setDockable( Dockable dockable ) {
        // remove old displayer
        if( displayer != null ){
            displayer.getDockable().removeDockableListener( listener );
            
            DockTitle title = displayer.getTitle();
            if( title != null ){
                displayer.getDockable().unbind( title );
                displayer.setTitle( null );
            }
            displayer.setDockable( null );
            station.getDisplayers().release( displayer );
            
            displayer = null;
        }
        
        // add new displayer
        if( dockable != null ){
            DockTitle title = null;
            if( isShowTitle() ){
                title = station.createDockTitle( dockable );
                if( title != null ){
                    dockable.bind( title );
                }
            }
            displayer = station.getDisplayers().fetch( dockable, title );
            dockable.addDockableListener( listener );
        }
        
        showDisplayer( displayer );
    }

    public void setController( DockController controller ) {
        // remove old DockTitle
        if( displayer != null ){
            if( this.controller != null ){
                DockTitle title = displayer.getTitle();
                if( title != null ){
                    displayer.getDockable().unbind( title );
                    displayer.setTitle( null );
                }
            }
        }
        
        this.controller = controller;
        
        // create new DockTitle
        if( displayer != null ){
            if( this.controller != null && isShowTitle() ){
                DockTitle title = station.createDockTitle( displayer.getDockable() );
                if( title != null ){
                    displayer.getDockable().bind( title );
                    displayer.setTitle( title );
                }
            }
        }
    }
    
    public Point getOffsetDrop() {
        if( displayer == null )
            return null;
        
        Insets insets = getDockableInsets();
        
        return new Point( insets.left, insets.top );
    }
    
    public Point getOffsetMove() {
        if( displayer == null )
            return null;
        
        DockTitle title = displayer.getTitle();
        if( title == null )
            return null;
        
        Component base = getWindowComponent();
        if( base == null )
            return null;
        
        Point zero = new Point( 0, 0 );
        zero = SwingUtilities.convertPoint( title.getComponent(), zero, base );
        return zero;
    }
    
    public boolean inCombineArea( int x, int y ) {
        if( displayer == null )
            return false;
        
        Point point = new Point( x, y );
        SwingUtilities.convertPointFromScreen( point, displayer.getComponent() );
        return displayer.titleContains( point.x, point.y );
    }
    
    /**
     * Gets the controller in whose realm this window is used.
     * @return the controller, can be <code>null</code>
     */
    public DockController getController() {
        return controller;
    }
    
    /**
     * Gets the station for which this window is used.
     * @return the owner, never <code>null</code>
     */
    public ScreenDockStation getStation(){
        return station;
    }
}
