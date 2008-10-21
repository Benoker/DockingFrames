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

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.intern.AbstractCDockable;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.intern.station.SplitResizeRequestHandler;
import bibliothek.gui.dock.common.location.CWorkingAreaLocation;
import bibliothek.gui.dock.facile.state.MaximizeArea;
import bibliothek.gui.dock.facile.state.MaximizeSplitDockStation;
import bibliothek.gui.dock.security.SecureSplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockTree;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A working area is an element which is always visible and contains some
 * {@link CDockable}s which can't be dragged out of it. Also no {@link CDockable}
 * can be dropped in a {@link CWorkingArea}.<br>
 * There can be more than one {@link CWorkingArea}, and the working areas
 * can be nested.
 * @author Benjamin Sigg
 */
public class CWorkingArea extends AbstractCDockable implements SingleCDockable, CStation{
    /** the unique identifier of this area */
    private String uniqueId;
    /** the station representing this area */
    private SplitDockStation station;
    /** a handler used to update the bounds of children of this station */
    private SplitResizeRequestHandler resizeRequestHandler;
    
    /** this working area as parent of maximized dockables, can be <code>null</code> if not used */
    private MaximizeArea maximizingArea;
    
    /**
     * Creates a new area.
     * @param uniqueId a unique identifier
     * @param restrictedEnvironment <code>true</code> if the {@link SecurityManager}
     * is active, for example in an applet or when using webstart.
     */
    public CWorkingArea( String uniqueId, boolean restrictedEnvironment ){
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
        
        setTitleShown( false );
        station.setExpandOnDoubleclick( false );
        resizeRequestHandler = new SplitResizeRequestHandler( station );
    }
    
    /**
     * Exchanges all the {@link CDockable}s on this area with the
     * elements of <code>grid</code>. This method also calls
     * {@link CDockable#setWorkingArea(CStation)} for each
     * dockable in <code>grid</code>.
     * @param grid a grid containing some new {@link Dockable}s
     */
    public void deploy( CGrid grid ){
        SplitDockTree tree = grid.toTree();
        
        for( Dockable dockable : tree.getDockables() ){
            if( dockable instanceof CommonDockable ){
                CommonDockable cdock = (CommonDockable)dockable;
                cdock.getDockable().setWorkingArea( this );
            }
        }
        
        station.dropTree( tree );
    }

    public DockStation getStation() {
        return station;
    }
    
    public CDockable asDockable() {
        return this;
    }
    
    public CLocation getStationLocation() {
        return new CWorkingAreaLocation( this );
    }
    
    /**
     * Sets whether this working-area should suppress its title or not. 
     * @param suppressTitle <code>true</code> if this area should try
     * not to have a title.
     * @deprecated use {@link #setTitleShown(boolean)} instead
     */
    @Deprecated
    public void setSuppressTitle( boolean suppressTitle ) {
        setTitleShown( !suppressTitle );
    }
    
    /**
     * Tells whether this working-area suppresses its title.
     * @return <code>true</code> if this area normally has no title
     * @deprecated use {@link #isTitleShown()} instead
     */
    @Deprecated
    public boolean isSuppressTitle() {
        return !isTitleShown();
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
     * Sets the icon that is shown in the title of this <code>CDockable</code>.
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
     * and adds <code>dockable</code> to the {@link CControl} which is associated
     * with this {@link CWorkingArea}. If there is no <code>CControl</code>, then
     * the <code>dockable</code> is added nowhere.
     * @param <F> the type of element to add
     * @param dockable the new element
     * @return <code>dockable</code>
     */
    public <F extends SingleCDockable> F add( F dockable ){
        dockable.setWorkingArea( this );
        CControlAccess access = control();
        if( access != null ){
            access.getOwner().add( dockable );
        }
        return dockable;
    }
    
    /**
     * Ensures that <code>this</code> is the parent of <code>dockable</code>
     * and adds <code>dockable</code> to the {@link CControl} which is associated
     * with this {@link CWorkingArea}. If there is no <code>CControl</code>, then
     * the <code>dockable</code> is added nowhere.
     * @param <F> the type of element to add
     * @param dockable the new element
     * @return <code>dockable</code>
     */
    public <F extends MultipleCDockable> F add( F dockable ){
        dockable.setWorkingArea( this );
        CControlAccess access = control();
        if( access != null ){
            access.getOwner().add( dockable );
        }
        return dockable;
    }
    
    /**
     * Sets whether this area is also used as maximizing area. If so then pressing
     * the "maximize"-button of a child of this area will have the effect that
     * the child is maximized only within this area. Otherwise it takes more
     * space.
     * @param maximize <code>true</code> if children should be maximized to this
     * area, <code>false</code> if not.
     */
    public void setMaximizingArea( boolean maximize ){
	if( maximize ){
	    if( maximizingArea == null ){
		maximizingArea = new MaximizeSplitDockStation( getUniqueId(), station );
		CControlAccess access = getControl();
		if( access != null ){
		    access.getStateManager().addMaximizingArea( maximizingArea );
		}
	    }
	}
	else{
	    if( maximizingArea != null ){
		CControlAccess access = getControl();
		if( access != null ){
		    access.getStateManager().removeMaximizingArea( maximizingArea );
		}
		maximizingArea = null;
	    }
	}
    }
    
    /**
     * Tells whether children of this area remain children when maximized or not.
     * @return <code>true</code> if children remain children
     * @see #setMaximizingArea(boolean)
     */
    public boolean isMaximizingArea(){
	return maximizingArea != null;
    }

    @Override
    public void setControl( CControlAccess control ) {
        CControlAccess old = control();
        if( old != control ){
            if( old != null ){
                old.getStateManager().remove( uniqueId );
                old.getOwner().removeResizeRequestListener( resizeRequestHandler );
            }
            
            super.setControl( control );
            
            if( control != null ){
                control.getStateManager().add( uniqueId, station );
                control.getOwner().addResizeRequestListener( resizeRequestHandler );
            }
        }
    }
    
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
    
    public boolean isWorkingArea() {
        return true;
    }
    
    /**
     * Checks whether the title created by <code>version</code> should
     * be suppressed.
     * @param version the version of the title
     * @return <code>true</code> if no {@link DockTitle} should be created
     */
    protected boolean suppressTitle( DockTitleVersion version ){
        if( !isTitleShown() ){
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
     * A {@link SplitDockStation} representing a {@link CWorkingArea}.
     * @author Benjamin Sigg
     */
    private class Station extends SplitDockStation implements CommonDockable{
        public Station(){
            addCDockablePropertyListener( new CDockableAdapter(){
                @Override
                public void titleShownChanged( CDockable dockable ) {
                    fireTitleExchanged();
                }
            });
        }
        
        public CDockable getDockable() {
            return CWorkingArea.this;
        }
        
        @Override
        protected ListeningDockAction createFullScreenAction() {
            return null;
        }
        
        @Override
        public void setFrontDockable( Dockable dockable ) {
            if( !isFullScreen() ){
                super.setFrontDockable( dockable );
            }
        }
        
        public DockActionSource getClose() {
            return CWorkingArea.this.getClose();
        }
        
        @Override
        public DockTitle getDockTitle( DockTitleVersion version ) {
            if( suppressTitle( version ))
                return null;
            return super.getDockTitle( version );
        }
    }
    
    /**
     * A {@link SecureSplitDockStation} representing a {@link CWorkingArea}.
     * @author Benjamin Sigg
     *
     */
    private class SecureStation extends SecureSplitDockStation implements CommonDockable{
        public SecureStation(){
            addCDockablePropertyListener( new CDockableAdapter(){
                @Override
                public void titleShownChanged( CDockable dockable ) {
                    fireTitleExchanged();
                }
            });
        }
        
        public DockActionSource getClose() {
            return CWorkingArea.this.getClose();
        }
        
        public CDockable getDockable() {
            return CWorkingArea.this;
        }
        
        @Override
        protected ListeningDockAction createFullScreenAction() {
            return null;
        }
        
        @Override
        public void setFrontDockable( Dockable dockable ) {
            if( !isFullScreen() ){
                super.setFrontDockable( dockable );
            }
        }
        
        @Override
        public DockTitle getDockTitle( DockTitleVersion version ) {
            if( suppressTitle( version ))
                return null;
            return super.getDockTitle( version );
        }
    }
}
