/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.common.behavior;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockHierarchyLock;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGridArea;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.mode.station.ExternalizedCSplitDockStationHandler;
import bibliothek.gui.dock.control.DockRegister;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DockRegisterListener;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.util.Path;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * This {@link CStation} is intended to be set between a {@link ScreenDockStation} and a {@link Dockable}.
 * It can clean up itself when it is no longer required.<br>
 * This station will allow its children to be "maximized" on itself. Additionally the station can be "maximized" itself,
 * but it cannot be "normalized" or "minimized" (it always remains floating on the screen).
 * @author Benjamin Sigg
 */
@Todo(priority=Priority.MAJOR, target=Version.VERSION_1_1_2, compatibility=Compatibility.COMPATIBLE,
description="The 'unmaximize' button appears at the wrong location. And during drag and drop layout information is lost" +
		" due to the listener that inserts the station after the dockable was inserted. Also perspectives will not yet work.")
public class ExternalizingCGridArea extends CGridArea {
	/** The type of this area, returned by {@link #getTypeId()} */
	public static final Path TYPE_ID = new Path( "dock", "ExternalizingCGridArea" );
	
	/** Whether {@link #autoRemove()} may ever succeed */
	private boolean autoRemove = true;
	
	/** Every unique identifier of a {@link ExternalizingCGridArea} starts with this string */
	public static final String UNIQUE_ID_PREFIX = "dock.ExternalizingCGridArea.";
	
	/**
	 * Creates a new area.
	 * @param control the owner of this area
	 */
	public ExternalizingCGridArea( CControl control ){
		this( control, createUniqueIdentifier( control ));
	}
	
	/**
	 * Creates a new area.
	 * @param control the owner of this area
	 * @param uniqueId the unique identifier of this station
	 */
	public ExternalizingCGridArea( CControl control, String uniqueId ){
		super( control, uniqueId );
		getStation().addDockStationListener( new AutoRemover() );
		
		setTitleShown( true );
		setMaximizingArea( true );
	}
	
	private static String createUniqueIdentifier( CControl control ){
		int count = 0;
		String id;
		
		do{
			id = UNIQUE_ID_PREFIX + System.currentTimeMillis() + "." + count;
			count++;
		}while( control.getSingleDockable( id ) != null );
		
		return id;
	}
	
	@Override
	protected ExternalizedCSplitDockStationHandler createSplitDockStationHandle( CControl control ){
		return new ExternalizedCSplitDockStationHandler( this, control.getLocationManager() );
	}
	
	@Override
	protected ExternalizedCSplitDockStationHandler getModeManagerHandle(){
		return (ExternalizedCSplitDockStationHandler) super.getModeManagerHandle();
	}
	
	@Override
	protected void install( CControlAccess access ){
		super.install( access );
		access.getLocationManager().getExternalizedMode().add( getModeManagerHandle().asExternalized() );
	}
	
	@Override
	protected void uninstall( CControlAccess access ){
		super.uninstall( access );
		access.getLocationManager().getExternalizedMode().remove( getModeManagerHandle().asExternalized().getUniqueId() );
	}
	
	@Override
	public boolean isMaximizable(){
		return true;
	}
	
	@Override
	public boolean isExternalizable(){
		return true;
	}
	
	@Override
	public boolean isMinimizable(){
		return false;
	}
	
	@Override
	public boolean isNormalizeable(){
		return false;
	}
	
	@Override
	protected boolean isNormalizingArea(){
		return false;
	}
	
	public Path getTypeId(){
		return TYPE_ID;
	}
	
	/**
	 * Sets whether this station can automatically delete itself from the application if the number of children
	 * drops to <code>0</code>.<br>
	 * The default value of this property is <code>true</code>.
	 * @param autoRemove whether automatic cleanup is enabled
	 */
	public void setAutoRemove( boolean autoRemove ){
		this.autoRemove = autoRemove;
	}
	
	/**
	 * Tells whether this station can automatically remove itself from its parent and the {@link CControl}.
	 * @return whether automatic cleanup is enabled
	 * @see #setAutoRemove(boolean)
	 */
	public boolean isAutoRemove(){
		return autoRemove;
	}
	
	/**
	 * Queues up a call to {@link #autoRemove()} 
	 */
	protected void tryAutoRemove(){
		CControl control = getControl();
		if( control != null ){
			DockHierarchyLock lock = control.getController().getHierarchyLock();
			lock.onRelease( new Runnable(){
				public void run(){
					autoRemove();
				}
			} );
		}
	}
	
	/**
	 * Removes this station from its parent and from the {@link CControl} if it no longer has any children,
	 * and if the layout is not currently frozen.
	 */
	private void autoRemove(){
		if( !autoRemove ){
			return;
		}
		
		CControl control = getControl();
		if( control != null ){
			DockRegister register = control.getController().getRegister();
			if( register.isStalled() ){
				register.addDockRegisterListener( new DelayedAutoRemove() );
			}
			else{
				SplitDockStation station = getStation();
				if( station.getDockableCount() == 0 ){
					DockStation parent = station.getDockParent();
					if( parent != null ){
						parent.drag( station );
					}
					control.removeDockable( this );
					control.removeStation( this );
				}
			}
		}
	}
	
	/**
	 * A listener that is added to the {@link DockStation} that is used as delegate by this {@link CStation}. If the
	 * last child of the station is removed, then {@link ExternalizingCGridArea#tryAutoRemove()} is called.
	 * @author Benjamin Sigg
	 */
	private class AutoRemover extends DockStationAdapter{
		@Override
		public void dockableRemoved( DockStation station, Dockable dockable ){
			if( station.getDockableCount() == 0 ){
				tryAutoRemove();
			}
		}
	}
	
	/**
	 * Waits for the event {@link DockRegisterListener#registerUnstalled(bibliothek.gui.DockController)}, then
	 * calls {@link ExternalizingCGridArea#tryAutoRemove()}.
	 * @author Benjamin Sigg
	 */
	private class DelayedAutoRemove extends DockRegisterAdapter{
		@Override
		public void registerUnstalled( DockController controller ){
			controller.getRegister().removeDockRegisterListener( this );
			tryAutoRemove();
		}
	}
}
