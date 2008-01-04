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

import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.facile.intern.AbstractFDockable;
import bibliothek.gui.dock.facile.intern.FControlAccess;
import bibliothek.gui.dock.facile.intern.FDockable;
import bibliothek.gui.dock.facile.intern.FacileDockable;
import bibliothek.gui.dock.security.SecureSplitDockStation;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A working area is an element which is always visible and contains some
 * {@link FDockable}s which can't be dragged out of it. Also no {@link FDockable}
 * can be dropped in a {@link FWorkingArea}.
 * @author Benjamin Sigg
 */
public class FWorkingArea extends AbstractFDockable implements FSingleDockable{
    /** the unique identifier of this area */
    private String uniqueId;
    /** the station representing this area */
    private SplitDockStation station;
    /** whether the {@link DockTitle} should not be created */
    private boolean suppressTitle = true;
    
    /**
     * Creates a new area.
     * @param uniqueId a unique identifier
     * @param restrictedEnvironment <code>true</code> if the {@link SecurityManager}
     * is active, for example in an applet or when using webstart.
     */
    public FWorkingArea( String uniqueId, boolean restrictedEnvironment ){
        super( null );
        if( uniqueId == null )
            throw new NullPointerException( "id must not be null" );
        
        this.uniqueId = uniqueId;
        if( restrictedEnvironment ){
            SecureStation station = new SecureStation();
            this.station = station;
            init( station );
        }
        else{
            Station station = new Station();
            this.station = station;
            init( station );
        }
    }
    
    /**
     * Ensures that <code>this</code> is the parent of <code>dockable</code>
     * and adds <code>dockable</code> to the {@link FControl} which is associated
     * with this {@link FWorkingArea}. If there is no <code>FControl</code>, then
     * the <code>dockable</code> is added nowhere.
     * @param <F> the type of element to add
     * @param dockable the new element
     * @return <code>dockable</code>
     */
    public <F extends FSingleDockable> F add( F dockable ){
        dockable.setWorkingArea( this );
        FControlAccess access = control();
        if( access != null ){
            access.getOwner().add( dockable );
        }
        return dockable;
    }
    
    /**
     * Ensures that <code>this</code> is the parent of <code>dockable</code>
     * and adds <code>dockable</code> to the {@link FControl} which is associated
     * with this {@link FWorkingArea}. If there is no <code>FControl</code>, then
     * the <code>dockable</code> is added nowhere.
     * @param <F> the type of element to add
     * @param dockable the new element
     * @return <code>dockable</code>
     */
    public <F extends FMultipleDockable> F add( F dockable ){
        dockable.setWorkingArea( this );
        FControlAccess access = control();
        if( access != null ){
            access.getOwner().add( dockable );
        }
        return dockable;
    }

    @Override
    public void setControl( FControlAccess control ) {
        FControlAccess old = control();
        if( old != null ){
            old.getOwner().intern().removeRoot( station );
            old.getStateManager().remove( uniqueId );
        }
        super.setControl( control );
        if( control != null ){
            control.getStateManager().add( uniqueId, station );
            control.getOwner().intern().addRoot( station, uniqueId );
        }
    }
    
    @Override
    public boolean isCloseable() {
        return false;
    }

    public String getId() {
        return uniqueId;
    }

    public boolean isExternalizable() {
        return false;
    }

    public boolean isMaximizable() {
        return false;
    }

    public boolean isMinimizable() {
        return false;
    }
    
    public boolean isStackable() {
        return false;
    }
    
    /**
     * Checks whether the title created by <code>version</code> should
     * be suppressed.
     * @param version the version of the title
     * @return <code>true</code> if no {@link DockTitle} should be created
     */
    protected boolean suppressTitle( DockTitleVersion version ){
        if( suppressTitle ){
            if( version.getID().equals( SplitDockStation.TITLE_ID ))
                return true;
            if( version.getID().equals( FlapDockStation.WINDOW_TITLE_ID ))
                return true;
            if( version.getID().equals( ScreenDockStation.TITLE_ID ))
                return true;
            if( version.getID().equals( StackDockStation.TITLE_ID ))
                return true;
        }
        return false;
    }
    
    private class Station extends SplitDockStation implements FacileDockable{
        public FDockable getDockable() {
            return FWorkingArea.this;
        }
        
        @Override
        public DockTitle getDockTitle( DockTitleVersion version ) {
            if( suppressTitle( version ))
                return null;
            return super.getDockTitle( version );
        }
    }
    
    private class SecureStation extends SecureSplitDockStation implements FacileDockable{
        public FDockable getDockable() {
            return FWorkingArea.this;
        }
        
        @Override
        public DockTitle getDockTitle( DockTitleVersion version ) {
            if( suppressTitle( version ))
                return null;
            return super.getDockTitle( version );
        }
    }
}
