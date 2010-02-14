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
package bibliothek.gui.dock.title;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * Set of information and callback used to obtain a {@link DockTitle}
 * from a {@link DockTitleFactory}. 
 * @author Benjamin Sigg
 *
 */
public abstract class DockTitleRequest {
	/** the element for which the title is shown */
	private Dockable target;
	/** the element that shows the title */
	private DockStation parent;
	/** the kind of title that is requested */
	private DockTitleVersion version;
	
	
	/** the current answer to this request */
	private DockTitle title;
	/** whether this request has been answered */
	private boolean answered = false;
	/** whether this request is installed on its {@link #version} */
	private boolean installed = false;
	
	/** whether {@link #request()} has been called */
	private boolean requesting = false;
	
	/**
	 * Creates a new request.
	 * @param parent the element which will show the title, can be <code>null</code>
	 * @param target the element for which the title will be shown, not <code>null</code>
	 * @param version what kind of title is requested, not <code>null</code>
	 */
	public DockTitleRequest( DockStation parent, Dockable target, DockTitleVersion version ){
		if( target == null ){
			throw new IllegalArgumentException( "target must not be null" );
		}
		if( version == null ){
			throw new IllegalArgumentException( "version must not be null" );
		}
		
		this.target = target;
		this.parent = parent;
		this.version = version;
	}
	
	/**
	 * Makes this request active. After installation the method {@link #answer(DockTitle, DockTitle)} may
	 * be called anytimes. Please note that installing does not automatically trigger a call
	 * to {@link #request()}.
	 */
	public void install(){
		if( !installed ){
			installed = true;
			version.install( this );
		}
	}
	
	/**
	 * Makes this request inactive, calls to {@link #answer(DockTitle, DockTitle)} will no longer happen.
	 */
	public void uninstall(){
		if( installed ){
			installed = false;
			version.uninstall( this );
		}
	}
	
	/**
	 * Tells whether this request is currently installed on its {@link #getVersion() version}.
	 * @return <code>true</code> if this request is active
	 */
	public boolean isInstalled(){
		return installed;
	}
	
	/**
	 * Gets the {@link Dockable} for which the title is requested.
	 * @return the target element
	 */
	public Dockable getTarget(){
		return target;
	}
	
	/**
	 * Gets the station that will show the title. The result might be <code>null</code>
	 * if the title is shown outside any station.
	 * @return the parent of {@link #getTarget()} or <code>null</code>
	 */
	public DockStation getParent(){
		return parent;
	}
	
	/**
	 * Gets the usage of the new title.
	 * @return the usage, not <code>null</code>
	 */
	public DockTitleVersion getVersion(){
		return version;
	}
	
	/**
	 * Called whenever a new title should be shown.
	 * @param previous the title that was used previously, can be <code>null</code>
	 * @param title the new title, may be <code>null</code>
	 */
	protected abstract void answer( DockTitle previous, DockTitle title );
	
	/**
	 * Asks for a new title.
	 */
	public void request(){
		answered = false;
		DockTitle old = title;
				
		try{
			requesting = true;
			
			target.requestDockTitle( this );
			if( !answered ){
				version.request( this );
			}
		}
		finally{
			requesting = false;
		}

		if( old != title ){
			answer( old, title );
		}
	}
	
	/**
	 * Asks this request to simulate a call to {@link #request()} which is
	 * answered with <code>null</code>
	 */
	public void requestNull(){
		if( title != null ){
			DockTitle old = title;
			title = null;
			answer( old, title );
		}
	}
	
	/**
	 * Informs this request that <code>title</code> should be shown. This method
	 * can be called more than once to show different titles.<br>
	 * An answer must fulfill some rules:
	 * <ul>
     *  <li>The {@link #getTarget() target} of this request must be the same {@link Dockable} as {@link DockTitle#getDockable()} .</li>
     *  <li>The {@link #getVersion() version} must match the {@link DockTitle#getOrigin() origin} of the title.</li>
     *  <li>The title must <b>not</b> be bound</li>
     *  <li>The client must call the {@link Dockable#bind(DockTitle)}-method of the target {@link Dockable}
     *  before using the title. Note that a client <b>must not</b> call the
     *  bind-method of {@link DockTitle}</li>
     *  <li>The client must call the {@link Dockable#unbind(DockTitle)}-method when he no
     *  longer needs the title. Note that the client <b>must not</b> call the
     *  unbind-method of {@link DockTitle}</li>
     * </ul>
	 * 
	 * @param title the new title or <code>null</code> to show no title at all
	 * @throws IllegalArgumentException if the title does not met the specifications described above
	 * @throws IllegalStateException if {@link #request()} is not currently executing
	 */
	public void setAnswer( DockTitle title ){
		if( !requesting ){
			throw new IllegalStateException( "not requesting a title" );
		}
		
		answered = true;
		this.title = title;
	}
	
	/**
	 * Gets the last answer made to this request.
	 * @return the last answer, may be <code>null</code>
	 */
	public DockTitle getAnswer(){
		return title;
	}
}
