/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.displayer;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.ResourceRequest;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.gui.dock.util.extension.SharedExtension;

/**
 * A {@link DisplayerRequest} is an object that can create new {@link DockableDisplayer}s by
 * calling several factories.
 * @author Benjamin Sigg
 */
public abstract class DisplayerRequest extends ResourceRequest<DockableDisplayer>{
	/** the element for which the displayer is shown */
	private Dockable target;
	/** the element that shows the displayer */
	private DockStation parent;
	/** the title that should be forwarded to the displayer, may be <code>null</code> */
	private DockTitle title;

	/** the controller in whose realm this request is used, can be <code>null</code> */
	private DockController controller;
	
	/** factories added by an extension */
	private SharedExtension<DisplayerFactory> extensions; 
	
	/** default factory provided by the {@link DockTheme} */
	private DisplayerFactory defaultFactory;
	
	/** name forwarded to the {@link ExtensionName} */
	private String displayerId;
	
	/**
	 * Creates a new request.
	 * @param parent the station which is going to show the {@link DockableDisplayer}.
	 * @param target the element which is going to be shown in the displayer
	 * @param defaultFactory the default factory, to be used if no other way was found to create the {@link DockableDisplayer}
	 * @param displayerId a unique identifier that depends on the type of <code>parent</code>, this identifier will be forwarded to
	 * {@link Extension}s allowing them an easy way to filter uninteresting {@link DisplayerRequest}s, must not be <code>null</code>
	 */
	public DisplayerRequest( DockStation parent, Dockable target, DisplayerFactory defaultFactory, String displayerId ){
		if( parent == null ){
			throw new IllegalArgumentException( "parent must not be null" );
		}
		if( target == null ){
			throw new IllegalArgumentException( "target must not be null" );
		}
		if( displayerId == null ){
			throw new IllegalArgumentException( "displayerId must not be null" );
		}
		
		this.parent = parent;
		this.target = target;
		this.defaultFactory = defaultFactory;
		this.displayerId = displayerId;
	}
	
	/**
	 * Sets the {@link DockController} in whose realm this {@link DisplayerRequest} is used. The {@link DockController} can
	 * be used to load extensions.
	 * @param controller the controller, can be <code>null</code>
	 */
	public void setController( DockController controller ){
		if( this.controller != controller ){
			this.controller = controller;
			
			if( extensions != null ){
				extensions.unbind();
				extensions = null;
			}
			if( controller != null ){
				extensions = controller.getExtensions().share( new ExtensionName<DisplayerFactory>( DisplayerFactory.DISPLAYER_EXTENSION, DisplayerFactory.class, DisplayerFactory.DISPLAYER_EXTENSION_ID, displayerId ) );
				extensions.bind();
			}
		}
	}
	
	/**
	 * Gets the controller in whose realm this request is issued.
	 * @return the controller, might be <code>null</code>
	 */
	public DockController getController(){
		return controller;
	}
	
	/**
	 * Creates a new {@link DockableDisplayer}.
	 * @param title the title that should be shown on the displayer, may be <code>null</code>
	 */
	public void request( DockTitle title ){
		this.title = title;
		try{
			request();
		}
		finally{
			this.title = null;
		}
	}
	
	/**
	 * Gets the {@link Dockable} which is going to be shown in the {@link DockableDisplayer}.
	 * @return the dockable, not <code>null</code>
	 */
	public Dockable getTarget(){
		return target;
	}
	
	/**
	 * Gets the {@link DockStation} which is going to show the {@link DockableDisplayer}.
	 * @return the station, not <code>null</code>
	 */
	public DockStation getParent(){
		return parent;
	}
	
	/**
	 * Gets the title that should be shown on the displayer.
	 * @return the title to show, or <code>null</code>
	 */
	public DockTitle getTitle(){
		return title;
	}

	/**
	 * Sets the {@link DockableDisplayer} which should be shown. A valid displayer must meet the following
	 * conditions:
	 * <ul>
	 * 	<li>it must not be <code>null</code></li>
	 *  <li>the result of {@link DockableDisplayer#getDockable()} must be {@link #getTarget()}</li>
	 * </ul> 
	 * @throws IllegalStateException if {@link #request()} was not called
	 * @throws IllegalArgumentException if <code>displayer</code> is <code>null</code>
	 */
	@Override
	public void answer( DockableDisplayer displayer ){
		super.answer( displayer );
	}
	
	@Override
	protected void executeRequestList(){
		target.requestDisplayer( this );
		if( isAnswered() ){
			return;
		}
		
		parent.requestChildDisplayer( this );
		if( isAnswered() ){
			return;
		}
		
		if( extensions != null ){
			for( DisplayerFactory factory : extensions ){
				factory.request( this );
				if( isAnswered() ){
					return;
				}
			}
		}
		
		defaultFactory.request( this );
	}

	@Override
	protected void validate( DockableDisplayer resource ){
		if( resource == null ){
			throw new IllegalArgumentException( "displayer is null" );
		}
		if( resource.getDockable() != getTarget() ){
			throw new IllegalArgumentException( "displayer.getDockable() is not target" );
		}
	}
}
