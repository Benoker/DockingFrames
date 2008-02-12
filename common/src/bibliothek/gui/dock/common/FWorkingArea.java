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
package bibliothek.gui.dock.common;

import javax.swing.Icon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.common.intern.AbstractFDockable;
import bibliothek.gui.dock.common.intern.FControlAccess;
import bibliothek.gui.dock.common.intern.FDockable;
import bibliothek.gui.dock.common.intern.FacileDockable;
import bibliothek.gui.dock.security.SecureSplitDockStation;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A working area is an element which is always visible and contains some
 * {@link FDockable}s which can't be dragged out of it. Also no {@link FDockable}
 * can be dropped in a {@link FWorkingArea}.<br>
 * There can be more than one {@link FWorkingArea}, and the working areas
 * can be nested.
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
        
        station.setExpandOnDoubleclick( false );
    }
    
    /**
     * Exchanges all the {@link FDockable}s on this area with the
     * elements of <code>grid</code>.
     * @param grid a grid containing some new {@link Dockable}s
     */
    public void deploy( FGrid grid ){
        station.dropTree( grid.toTree() );
    }
    
    /**
     * Sets whether this working-area should suppress its title or not. 
     * @param suppressTitle <code>true</code> if this area should try
     * not to have a title.
     */
    public void setSuppressTitle( boolean suppressTitle ) {
        this.suppressTitle = suppressTitle;
    }
    
    /**
     * Tells whether this working-area suppresses its title.
     * @return <code>true</code> if this area normally has no title
     */
    public boolean isSuppressTitle() {
        return suppressTitle;
    }
    
    /**
     * Sets the text that is shown as title.
     * @param text the title
     */
    public void setTitleText( String text ){
        station.setTitleText( text );
    }
    
    /**
     * Gets the text that is shown as title.
     * @return the title
     */
    public String getTitleText(){
        return station.getTitleText();
    }
    
    /**
     * Sets the icon that is shown in the title of this <code>FDockable</code>.
     * @param icon the title-icon
     */
    public void setTitleIcon( Icon icon ){
        station.setTitleIcon( icon );
    }
    
    /**
     * Gets the icon that is shown in the title.
     * @return the title-icon, might be <code>null</code>
     */
    public Icon getTitleIcon(){
        return station.getTitleIcon();
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

    public String getUniqueId() {
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
    
    /**
     * A {@link SplitDockStation} representing a {@link FWorkingArea}.
     * @author Benjamin Sigg
     */
    private class Station extends SplitDockStation implements FacileDockable{
        public FDockable getDockable() {
            return FWorkingArea.this;
        }
        
        @Override
        protected ListeningDockAction createFullScreenAction() {
            return null;
        }
        
        @Override
        public void setFrontDockable( Dockable dockable ) {
            // ignore
        }
        
        @Override
        public DockTitle getDockTitle( DockTitleVersion version ) {
            if( suppressTitle( version ))
                return null;
            return super.getDockTitle( version );
        }
    }
    
    /**
     * A {@link SecureSplitDockStation} representing a {@link FWorkingArea}.
     * @author Benjamin Sigg
     *
     */
    private class SecureStation extends SecureSplitDockStation implements FacileDockable{
        public FDockable getDockable() {
            return FWorkingArea.this;
        }
        
        @Override
        protected ListeningDockAction createFullScreenAction() {
            return null;
        }
        
        @Override
        public void setFrontDockable( Dockable dockable ) {
            // ignore
        }
        
        @Override
        public DockTitle getDockTitle( DockTitleVersion version ) {
            if( suppressTitle( version ))
                return null;
            return super.getDockTitle( version );
        }
    }
}
