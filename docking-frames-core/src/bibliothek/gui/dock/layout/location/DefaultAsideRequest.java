/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.layout.location;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;

/**
 * The default implementation of an {@link AsideRequest}, it does not modify any properties or
 * layouts.
 * @author Benjamin Sigg
 */
public class DefaultAsideRequest implements AsideRequest{
	/** The location of an existing {@link Dockable} */
	private DockableProperty location;
	
	/** The unique identifier of the new {@link Dockable} */
	private Path placeholder;
	
	/** The parent of the current {@link DockStation} or {@link Combiner} */
	private DockStation parent;
	
	/** The {@link DockStation} or {@link Combiner} that is handled by this request */
	private Forward current;
	
	/** The new location of the element */
	private DockableProperty resultingLocation;
	
	/** The new layout of the non-existing station */
	private PlaceholderMap resultingLayout;
	
	/** Whether any of the <code>answer</code> methods have been called */
	private boolean hasAnswer = false;
	
	/** If there was a call to any <code>forward</code> method, then this is the answer to that call */
	private AsideAnswer successorAnswer;
	
	/**
	 * Creates a new {@link AsideRequest}
	 * @param location the location of an existing {@link Dockable}, must not be <code>null</code>
	 * @param placeholder the unique identifier of the {@link Dockable} to insert, the algorithms work much better if
	 * this arguments is not <code>null</code>
	 */
	public DefaultAsideRequest( DockableProperty location, Path placeholder ){
		this.location = location;
		this.placeholder = placeholder;
	}
	
	/**
	 * Creates a new {@link AsideRequest} which is used to examine the next child {@link DockStation}.
	 * @param location the location on the child station
	 * @return the new aside request, must not be <code>null</code> nor <code>this</code>
	 */
	protected DefaultAsideRequest createForwardRequest( DockableProperty location ){
		return new DefaultAsideRequest( location, getPlaceholder() );
	}
	
	public DockableProperty getLocation(){
		return location;
	}

	public Path getPlaceholder(){
		return placeholder;
	}

	public PlaceholderMap getLayout(){
		return current.getLayout();
	}

	public DockStation getParentStation(){
		return parent;
	}
	
	/**
	 * Sets the result of {@link #getParentStation()}.
	 * @param parent the {@link DockStation} that should be used as parent station
	 */
	protected void setParentStation( DockStation parent ){
		this.parent = parent;
	}

	public void answer(){
		answer( (DockableProperty)null );
	}

	public void answer( DockableProperty location ){
		hasAnswer = true;
		resultingLocation = location;
	}

	public void answer( PlaceholderMap station ){
		hasAnswer = true;
		resultingLayout = station;
	}

	public void answer( DockableProperty location, PlaceholderMap station ){
		answer( location );
		answer( station );
	}
	
	/**
	 * Sets all the information required to process a {@link DockStation} or {@link Combiner}.
	 * @param current the current item to work with
	 */
	protected void setCurrent( Forward current ){
		this.current = current;
		this.resultingLayout = current.getLayout();
	}
	
	public AsideAnswer execute( DockStation station ){
		setCurrent( new DockStationForward( station ) );
		return execute();
	}
	
	/**
	 * Makes a call to {@link DockStation#aside(AsideRequest)} or {@link Combiner#aside(AsideRequest)}. This method
	 * should be called only once.
	 * @return the location and layout of the {@link Dockable} that is to be inserted
	 */
	protected AsideAnswer execute(){
		if( hasAnswer ){
			throw new IllegalStateException( "this request is already used, it cannot be executed a second time" );
		}
		
		current.execute( this );
		if( hasAnswer ){
			hasAnswer = true;
			DockableProperty location = answerLocation( successorAnswer );
			return new DefaultAsideAnswer( false, location, resultingLayout );
		}
		else{
			return new DefaultAsideAnswer( true, null, null );
		}
	}
	
	private DockableProperty answerLocation( AsideAnswer successor ){
		if( successor == null || successor.getLocation() == null ){
			return resultingLocation;
		}
		if( resultingLocation == null ){
			return successor.getLocation();
		}
		
		DockableProperty last = resultingLocation;
		while( last.getSuccessor() != null ){
			last = last.getSuccessor();
		}
		
		last.setSuccessor( successor.getLocation() );
		return resultingLocation;
	}

	public AsideAnswer forward( DockStation station ){
		return forward( new DockStationForward( station ));
	}

	public AsideAnswer forward( Combiner combiner ){
		return forward( new CombinerForward( combiner, null ));
	}

	public AsideAnswer forward( Combiner combiner, PlaceholderMap layout ){
		return forward( new CombinerForward( combiner, layout ));
	}

	protected AsideAnswer forward( Forward forward ){
		DockableProperty successor = null;
		if( location != null ){
			successor = location.getSuccessor();
		}
		DefaultAsideRequest request = createForwardRequest( successor );
		request.setCurrent( forward );
		request.setParentStation( current.getStation() );
		successorAnswer = request.execute();
		return successorAnswer;
	}
	
	/**
	 * All the information required to create a new request and forward the call to a
	 * new {@link DockStation} or {@link Combiner}.
	 * @author Benjamin Sigg
	 */
	protected interface Forward{
		/**
		 * Gets the layout of the current station.
		 * @return the layout, may be <code>null</code>
		 */
		public PlaceholderMap getLayout();
		
		/**
		 * Gets <code>this</code> as {@link DockStation}, if <code>this</code> represents
		 * a station.
		 * @return the station or <code>null</code>
		 */
		public DockStation getStation();
		
		/**
		 * Calls the <code>aside</code> method of the item represented by this {@link Forward}.
		 * @param request information about the location of an element and of its new neighbor
		 */
		public void execute( AsideRequest request );
	}
	
	/**
	 * An adapter mapping {@link DockStation} to {@link Forward}.
	 * @author Benjamin Sigg
	 */
	protected static class DockStationForward implements Forward{
		private DockStation station;
		
		public DockStationForward( DockStation station ){
			this.station = station;
		}
		
		public PlaceholderMap getLayout(){
			return null;
		}
		
		public void execute( AsideRequest request ){
			station.aside( request );	
		}
		
		public DockStation getStation(){
			return station;
		}
	}
	
	/**
	 * An adapter mapping {@link Combiner} to {@link Forward}.
	 * @author Benjamin Sigg
	 */
	protected static class CombinerForward implements Forward{
		private Combiner combiner;
		private PlaceholderMap layout;
		
		public CombinerForward( Combiner combiner, PlaceholderMap layout ){
			this.combiner = combiner;
			this.layout = layout;
		}
		
		public PlaceholderMap getLayout(){
			return layout;
		}
		
		public void execute( AsideRequest request ){
			combiner.aside( request );	
		}
		
		public DockStation getStation(){
			return null;
		}
	}
}
