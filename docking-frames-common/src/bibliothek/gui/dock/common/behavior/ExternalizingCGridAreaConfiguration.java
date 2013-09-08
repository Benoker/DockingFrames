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

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.station.LayoutLocked;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;

/**
 * The {@link ExternalizingCGridAreaConfiguration} is a piece of code responsible for changing the behavior
 * of the framework such that externalized {@link CDockable} are not stacked, but put in a {@link ExternalizingCGridArea}.<br>
 * Clients can use {@link #install()}, {@link #installOn(CControl)} and {@link #uninstall()} to activate or 
 * deactivate this configuration.
 * 
 * @author Benjamin Sigg
 */
public class ExternalizingCGridAreaConfiguration {
	/** the control whose configuration this is */
	private CControl control;
	
	/** algorithm for inserting {@link ExternalizingCGridArea}s */
	private SplitInserter splitInserter;
	
	private ExternalizingCGridAreaFactory factory;
	
	/** whether this configuration is installed */
	private boolean installed = false;
	
	/**
	 * Creates a new {@link ExternalizingCGridAreaConfiguration} and {@link #install() installs} it.
	 * @param control the control in whose realm this configuration is used
	 * @return the new configuration
	 */
	public static ExternalizingCGridAreaConfiguration installOn( CControl control ){
		ExternalizingCGridAreaConfiguration config = new ExternalizingCGridAreaConfiguration( control );
		config.install();
		return config;
	}
	
	/**
	 * Creates a new configuration.
	 * @param control the control for which this configuration will be used, not <code>null</code>
	 */
	public ExternalizingCGridAreaConfiguration( CControl control ){
		if( control == null ){
			throw new IllegalArgumentException( "control must not be null" );
		}
		this.control = control;
		factory = new ExternalizingCGridAreaFactory( control );
	}
	
	/**
	 * Gets the listener that is responsible for inserting new {@link ExternalizingCGridArea}s.
	 * @return the listener, never <code>null</code>
	 */
	protected SplitInserter getSplitInserter(){
		if( splitInserter == null ){
			splitInserter = createSplitInserter();
		}
		return splitInserter;
	}
	
	/**
	 * Creates a new instance of {@link SplitInserter}.
	 * @return a new object, not <code>null</code>
	 */
	protected SplitInserter createSplitInserter(){
		return new SplitInserter();
	}
	
	/**
	 * Gets the {@link CControl} for which this configuration is used.
	 * @return the {@link CControl}
	 */
	public CControl getControl(){
		return control;
	}
	
	/**
	 * Creates a new {@link ExternalizingCGridArea}, the identifier of the new station is unique compared to all
	 * the {@link CDockable}s that are currently registered at {@link #getControl() control}.
	 * @return the new area, not <code>null</code>
	 */
	protected ExternalizingCGridArea createGridArea(){
		 return new ExternalizingCGridArea( control );
	}
	
	/**
	 * Activates this configuration.
	 */
	public void install(){
		if( installed ){
			throw new IllegalStateException( "already installed" );
		}
		installed = true;
		getScreenDockStation().addDockStationListener( getSplitInserter() );
		control.addSingleDockableFactory( ExternalizingCGridAreaFactory.PATTERN, factory );
	}
	
	/**
	 * Deactivates this configuration, existing {@link ExternalizingCGridArea}s will <b>not</b> be cleaned up.
	 */
	public void uninstall(){
		if( !installed ){
			throw new IllegalStateException( "not installed" );
		}
		getScreenDockStation().removeDockStationListener( getSplitInserter() );
		control.removeSingleDockableFactory( factory );
	}
	
	/**
	 * Gets the {@link DockStation} which was registered with the name {@link CControl#EXTERNALIZED_STATION_ID}.
	 * @return the station which will be the parent of all the new {@link ExternalizingCGridArea}s
	 */
	protected DockStation getScreenDockStation(){
		CStation<?> screen = control.getStation( CControl.EXTERNALIZED_STATION_ID );
		return screen.getStation();
	}
	
	/**
	 * A listener that is added to a {@link ScreenDockStation}, every time some {@link Dockable} is added to said
	 * station, a new {@link ExternalizingCGridArea} is created and inserted.
	 * @author Benjamin Sigg
	 */
	@LayoutLocked(locked=false)
	protected class SplitInserter extends DockStationAdapter {
		public void dockableAdded( DockStation station, final Dockable dockable ){
			if( !(dockable instanceof SplitDockStation) ) {
				EventQueue.invokeLater( new Runnable() {
					public void run() {
						checkAndReplace( dockable );
					}
				});
			}
		}
		
		private void checkAndReplace( Dockable dockable ){
			DockStation station = dockable.getDockParent();
			if( !(station instanceof ScreenDockStation) ) {
				return;
			}
			
			DockController controller = control.getController();
			
			try {
				controller.freezeLayout();
				ScreenDockStation screenDockStation = (ScreenDockStation)station;
				
				Dimension oldSize = dockable.getComponent().getSize();
							
				ExternalizingCGridArea split = createGridArea();
				control.addDockable( split );
				
				station.replace( dockable, split.getStation() );
				split.getStation().drop( dockable );
				
				resizeDelayed( screenDockStation, split.getStation(), dockable, oldSize );
			}
			finally {
				controller.meltLayout();
			}
		}
		
		private void resizeDelayed( final ScreenDockStation station, final Dockable parent, final Dockable dockable, final Dimension oldSize ){
			EventQueue.invokeLater( new Runnable() {
				public void run() {
					ScreenDockWindow window = station.getWindow( parent );
					if( window != null ){
						window.validate();
					
						Dimension newSize = dockable.getComponent().getSize();
						int dw = oldSize.width - newSize.width; 
						int dh = oldSize.height - newSize.height;

						Rectangle bounds = window.getWindowBounds();
						bounds.width += dw;
						bounds.height += dh;
						window.setWindowBounds( bounds );
					}
				}
			} );
		}
	};	
}
