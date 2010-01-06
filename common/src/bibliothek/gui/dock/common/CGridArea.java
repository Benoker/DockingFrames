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
import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.common.intern.AbstractDockableCStation;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.station.CommonStation;
import bibliothek.gui.dock.common.intern.station.CommonStationDelegate;
import bibliothek.gui.dock.common.intern.station.SplitResizeRequestHandler;
import bibliothek.gui.dock.common.location.CGridAreaLocation;
import bibliothek.gui.dock.common.mode.CMaximizedMode;
import bibliothek.gui.dock.common.mode.station.CSplitDockStationHandle;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * In a {@link CGridArea} normalized {@link CDockable} can be shown. Clients
 * should use {@link #getComponent()} to gain access to a {@link JComponent} that
 * represents this area.
 * @author Benjamin Sigg
 */
public class CGridArea extends AbstractDockableCStation<SplitDockStation> implements SingleCDockable {
	/** the unique identifier of this area */
	private String uniqueId;
	/** the station representing this area */
	private SplitDockStation station;
	/** a handler used to update the bounds of children of this station */
	private SplitResizeRequestHandler resizeRequestHandler;

	/** this split area as parent of maximized dockables, can be <code>null</code> if not used */
	private CSplitDockStationHandle modeManagerHandle;
	/** whether children of this gridarea can be maximized to the grid or not */
	private boolean maximizing = false;

	/**
	 * Creates a new area.
	 * @param control the owner of this station
	 * @param uniqueId a unique identifier
	 */
	public CGridArea( CControl control, String uniqueId ){
		init( control, uniqueId );
	}

	/**
	 * Creates a new grid area but does not yet initialize its fields. 
	 * Subclasses must call {@link #init(CControl, String)} to complete
	 * initialization
	 */
	protected CGridArea(){
		// ignore
	}
	
	/**
	 * Initializes the fields of this area.
	 * @param control the owner of this station
	 * @param uniqueId a unique identifier
	 */
	protected void init( CControl control, String uniqueId ){
		if( uniqueId == null )
			throw new NullPointerException( "id must not be null" );
		
		this.uniqueId = uniqueId;
		CommonStation<SplitDockStation> station = control.getFactory().createSplitDockStation( new Delegate() );
		
		this.station = station.asDockStation();
		init( station );
		
		setTitleShown( false );
		this.station.setExpandOnDoubleclick( false );
		resizeRequestHandler = new SplitResizeRequestHandler( this.station );
		setMaximizingArea( true );
		
		modeManagerHandle = new CSplitDockStationHandle( this, control.getLocationManager() );
	}

	/**
	 * Exchanges all the {@link CDockable}s on this area with the
	 * elements of <code>grid</code>. 
	 * @param grid a grid containing some new {@link Dockable}s
	 */
	public void deploy( CGrid grid ){
		station.dropTree( grid.toTree() );
	}

	public SplitDockStation getStation() {
		return station;
	}

	public CDockable asDockable() {
		return this;
	}

    /**
     * Gets the {@link JComponent} which represents this station.
     * @return the component
     */
    public JComponent getComponent(){
        return station;
    }
    
	public CLocation getStationLocation() {
		return new CGridAreaLocation( this );
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
	 * Sets whether this area is also used as maximizing area. If so then pressing
	 * the "maximize"-button of a child of this area will have the effect that
	 * the child is maximized only within this area. Otherwise it takes more
	 * space.
	 * @param maximize <code>true</code> if children should be maximized to this
	 * area, <code>false</code> if not.
	 */
	public void setMaximizingArea( boolean maximize ){
		if( maximize != maximizing ){
			this.maximizing = maximize;
			CControlAccess access = getControl();
			if( access != null ){
				CMaximizedMode mode = access.getLocationManager().getMaximizedMode();
				if( maximizing ){
					mode.add( modeManagerHandle.asMaximziedModeArea() );
				}
				else{
					mode.remove( modeManagerHandle.asMaximziedModeArea().getUniqueId() );
				}
			}
		}
	}

	/**
	 * Tells whether children of this area remain children when maximized or not.
	 * @return <code>true</code> if children remain children
	 * @see #setMaximizingArea(boolean)
	 */
	public boolean isMaximizingArea(){
		return maximizing;
	}

	@Override
	protected void install( CControlAccess access ){
		access.getLocationManager().getNormalMode().add( modeManagerHandle.asNormalModeArea() );
		access.getOwner().addResizeRequestListener( resizeRequestHandler );
		if( isMaximizingArea() ){
			CMaximizedMode mode = access.getLocationManager().getMaximizedMode();
			mode.add( modeManagerHandle.asMaximziedModeArea() );
		}	
	}
	
	@Override
	protected void uninstall( CControlAccess access ){
		access.getLocationManager().getNormalMode().remove( modeManagerHandle.asNormalModeArea().getUniqueId() );
		access.getOwner().removeResizeRequestListener( resizeRequestHandler );
		if( isMaximizingArea() ){
			CMaximizedMode mode = access.getLocationManager().getMaximizedMode();
			mode.remove( modeManagerHandle.asMaximziedModeArea().getUniqueId() );
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
		return false;
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
	  * Delegate used to provide information for the {@link SplitDockStation}
	  * of this area.
	  * @author Benjamin Sigg
	  */
	 private class Delegate implements CommonStationDelegate{
		public CDockable getDockable(){
			return CGridArea.this;
		}

		public DockActionSource[] getSources(){
			return new DockActionSource[]{ getClose() };
		}

		public CStation<SplitDockStation> getStation(){
			return CGridArea.this;
		}

		public boolean isTitleDisplayed( DockTitleVersion title ){
			return !suppressTitle( title );
		}
	 }
}
