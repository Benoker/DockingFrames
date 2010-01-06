/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.common.intern.station;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.security.SecureSplitDockStation;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * The secure version of {@link CSplitDockStation}.
 * @author Benjamin Sigg
 */
public class SecureCSplitDockStation extends SecureSplitDockStation implements CommonStation<SplitDockStation>{
	private CommonStationDelegate delegate;

	public SecureCSplitDockStation( CommonStationDelegate delegate ){
		this.delegate = delegate;

		getDockable().addCDockablePropertyListener( new CDockableAdapter(){
			@Override
			public void titleShownChanged( CDockable dockable ) {
				fireTitleExchanged();
			}
		});
	}

	public CDockable getDockable() {
		return delegate.getDockable();
	}

	public CStation<?> getStation(){
		return delegate.getStation();
	}
	
	@Override
	public SplitDockStation asDockStation(){
		return this;
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

	public DockActionSource[] getSources(){
		return delegate.getSources();
	}

	@Override
	public DockTitle getDockTitle( DockTitleVersion version ) {
		if( delegate.isTitleDisplayed( version ))
			return super.getDockTitle( version );
		
		return null;
	}
}
