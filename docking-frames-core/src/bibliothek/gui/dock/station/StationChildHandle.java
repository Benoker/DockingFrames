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
package bibliothek.gui.dock.station;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockableAdapter;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A support class intended to be used by {@link DockStation}s. This class
 * creates and handles {@link DockableDisplayer}, {@link DockTitleRequest} and {@link DockTitle}s for
 * a {@link Dockable} on a specific {@link DockStation}.
 * @author Benjamin Sigg
 *
 */
public class StationChildHandle implements PlaceholderListItem<Dockable>{
	/** the station using this handler */
	private DockStation station;
	
	/** the element to show */
	private Dockable dockable;
	
	/** the set of available displayers */
	private DisplayerCollection displayers;
	
	/** the current displayer of the {@link Dockable} */
	private DockableDisplayer displayer;
	
	/** the current request for a {@link DockTitle} */
	private DockTitleRequest titleRequest;
	
	/** listener added to {@link #dockable} to be informed if the title needs to be updated */
	private DockableListener listener = new DockableAdapter(){
		public void titleExchanged( Dockable dockable, DockTitle title ){
			if( dockable == StationChildHandle.this.dockable ){
				if( displayer != null ){
					if( displayer.getTitle() == title ){
						requestTitle();
					}
				}
			}
		}
	};
	
	/**
	 * Creates a new handle, initializes a {@link DockTitleRequest} but no {@link DockableDisplayer} nor a {@link DockTitle}. This
	 * constructor also adds a {@link DockableListener} to <code>dockable</code> to update the title whenever <code>dockable</code>
	 * requests it.<br>
	 * Clients should call {@link #updateDisplayer()} to initialize the remaining fields of this handler.
	 * @param station the owner of this handle, the parent of <code>dockable</code>
	 * @param displayers the set of available {@link DockableDisplayer}s
	 * @param dockable the element that will be managed by this handle
	 * @param title what kind of title is requested
	 */
	public StationChildHandle( DockStation station, DisplayerCollection displayers, Dockable dockable, DockTitleVersion title ){
		this.station = station;
		this.displayers = displayers;
		this.dockable = dockable;
		dockable.addDockableListener( listener );
		
		setTitleRequest( title, false );
	}

	/**
	 * Deletes all resources that were acquired by this handler. This includes the current
	 * {@link DockableDisplayer}, the current {@link DockTitle} and the current {@link DockTitleRequest}.
	 */
	public void destroy(){
		if( displayer != null ){
			DockTitle title = displayer.getTitle();
			if( title != null ){
				dockable.unbind( title );
			}
			displayers.release( displayer );
			displayer = null;
		}
		
		if( titleRequest != null ){
			titleRequest.uninstall();
			titleRequest = null;
		}
		
		dockable.removeDockableListener( listener );
	}
	
	/**
	 * Discards the current {@link DockableDisplayer} and creates a new one using the same
	 * {@link DockTitle} as was used for the old one. If there is currently no {@link DockableDisplayer}
	 * in use, then a new {@link DockTitle} is created.
	 */
	public void updateDisplayer(){
		DockTitle title = null;
		
		if( displayer != null ){
			title = displayer.getTitle();
			displayers.release( displayer );
		}
		else{
			if( titleRequest != null ){
				titleRequest.request();
				title = titleRequest.getAnswer();
				if( title != null ){
					dockable.bind( title );
				}
			}
		}
		
		displayer = displayers.fetch( dockable, title );
	}
	
	/**
	 * Gest the current displayer for this handle.
	 * @return the current displayer, might be <code>null</code>
	 */
	public DockableDisplayer getDisplayer(){
		return displayer;
	}
	
	/**
	 * Gets the element that is handled by this handler.
	 * @return the handled element, not <code>null</code>
	 */
	public Dockable getDockable(){
		return dockable;
	}
	
	public Dockable asDockable(){
		return getDockable();
	}
	
	/**
	 * Gets the title which is currently displayed.
	 * @return the title or <code>null</code>
	 */
	public DockTitle getTitle(){
		if( titleRequest == null )
			return null;
		return titleRequest.getAnswer();
	}
	
	/**
	 * The same as <code>setTitleRequest( version, true );</code>
	 * @param version the new title-version, can be <code>null</code>
	 * @see #setTitleRequest(DockTitleVersion, boolean)
	 */
	public void setTitleRequest( DockTitleVersion version ){
		setTitleRequest( version, true );
	}
	
	/**
	 * Updates the {@link DockTitleRequest} associated with this {@link Dockable}. The old
	 * title (if there is any) is discarded. If <code>request</code> is <code>true</code>, then
	 * a new title is requested. Otherwise a new {@link DockTitleRequest} is installed but
	 * not triggered.
	 * @param version the new title-version, can be <code>null</code>
	 * @param request whether to update the current title or not
	 */
	public void setTitleRequest( DockTitleVersion version, boolean request ){
		if( titleRequest != null ){
			titleRequest.uninstall();
			titleRequest = null;
			
			DockTitle title = displayer.getTitle();
			if( title != null ){
				displayer.getDockable().unbind( title );
				displayer.setTitle( null );
			}
		}
		
		if( version != null ){
			titleRequest = new DockTitleRequest( station, dockable, version ) {
				@Override
				protected void answer( DockTitle old, DockTitle title ){
					updateTitle( title );
				}
			};
		}
		
		if( titleRequest != null ){
			titleRequest.install();
			if( request ){
				titleRequest.request();
			}
		}
	}
	
	/**
	 * Requests a new title for this {@link Dockable}
	 */
	public void requestTitle(){
		if( titleRequest != null ){
			titleRequest.request();
		}
	}
	
	/**
	 * Called if the current {@link DockTitle} needs to be exchanged.
	 * @param title the new title, may be <code>null</code>
	 */
	protected void updateTitle( DockTitle title ){
		if( displayer != null ){
			DockTitle oldTitle = displayer.getTitle();
			if( oldTitle != null ){
				dockable.unbind( oldTitle );
			}
			
			if( title != null ){
				dockable.bind( title );
			}
			displayer.setTitle( title );
		}
	}
}
