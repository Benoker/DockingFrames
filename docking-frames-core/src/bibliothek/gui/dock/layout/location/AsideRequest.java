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
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.property.DynamicPropertyFactory;
import bibliothek.util.Path;

/**
 * An {@link AsideRequest} represents the action of generating a {@link DockableProperty} that is
 * "aside" another property, this includes modifying {@link DockStation}s and layouts to store
 * a placeholder for the new property.<br>
 * One {@link AsideRequest} is always tied to exactly one {@link DockStation} or {@link Combiner},
 * forwarding a request to another {@link DockStation} will produce very strange results.
 * @author Benjamin Sigg
 */
public interface AsideRequest {
	/**
	 * Property key for getting the default {@link AsideRequestFactory}.
	 */
	public static final PropertyKey<AsideRequestFactory> REQUEST_FACTORY = new PropertyKey<AsideRequestFactory>( "dock.AsideRequestFactory",
			new DynamicPropertyFactory<AsideRequestFactory>(){
				public AsideRequestFactory getDefault( PropertyKey<AsideRequestFactory> key, DockProperties properties ){
					return new DefaultAsideRequestFactory( properties );
				}
			}, true );
	
	/**
	 * Executes this request calling the <code>aside</code> method of <code>station</code>.
	 * @param station the station whose {@link DockStation#aside(AsideRequest) aside} method is to be called
	 * @return a new location
	 * @throws IllegalStateException if this request is already executed
	 */
	public AsideAnswer execute( DockStation station );
	
	/**
	 * Gets the old location, the location whose neighbor is searched. The property
	 * may have a {@link DockableProperty#getSuccessor() successor}.
	 * @return the old location, can be <code>null</code>
	 */
	public DockableProperty getLocation();

	/**
	 * The placeholder that should be used for the new location. 
	 * @return the placeholder, may be <code>null</code>
	 */
	public Path getPlaceholder();

	/**
	 * Gets the layout of the current non-existent station. The layout is only set in
	 * situations where no {@link DockStation} is available, and it is only set if it
	 * is known.
	 * @return the layout as it was created by {@link DockStation#getPlaceholders()}, <code>null</code>
	 * if not known or not necessary
	 */
	public PlaceholderMap getLayout();
	
	/**
	 * Gets the {@link DockStation} that is the parent of the current {@link DockStation} or {@link Combiner}.
	 * @return the parent station, may be <code>null</code> either for a root station or if the recursion of
	 * {@link Combiner}s is too high
	 */
	public DockStation getParentStation();

	/**
	 * Shortcut for calling {@link #answer(DockableProperty)} with a value of <code>null</code>.
	 */
	public void answer();

	/**
	 * Sets the result of this request, <code>location</code> will be merged with other
	 * results from the parent and children stations using the 
	 * {@link DockableProperty#setSuccessor(DockableProperty) successor} property. If <code>location</code>
	 * already has a successor, then the successor of the last {@link DockableProperty} in the chain
	 * may be modified by this method.<br>
	 * By not calling this method at all, clients can tell the action that no "neighbor" location was found, in
	 * this case the request will be canceled. Note how "not calling" and "calling with <code>null</code>" are
	 * two different things.
	 * @param location the location describing the "neighbor" of {@link DockableProperty}, a value
	 * of <code>null</code> indicates that the parent and child request should be merged directly 
	 */
	public void answer( DockableProperty location );

	/**
	 * Tells this request how the layout of a non-existent station looks after the request has been handled.
	 * Calling this method with a value of <code>null</code> indicates that there will be no layout after the
	 * request finished. Not calling the method at all indicates that the current layout is not affected.<br>
	 * Note that calling this method does not change the result of {@link #getLayout()}.
	 * @param station the new layout, can be <code>null</code>
	 */
	public void answer( PlaceholderMap station );
	
	/**
	 * Sets the result of this request, and tells how the layout of a non-existent stations looks after the request
	 * has been handled. The arguments can be <code>null</code>, as described in {@link #answer(DockableProperty)}
	 * and {@link #answer(PlaceholderMap)}.
	 * @param location the location describing the "neighbor" of {@link DockableProperty}, a value
	 * of <code>null</code> indicates that the parent and child request should be merged directly
	 * @param station the new layout, can be <code>null</code>
	 */
	public void answer( DockableProperty location, PlaceholderMap station );

	/**
	 * Calls the {@link DockStation#aside(AsideRequest)} method of <code>station</code> with the 
	 * {@link DockableProperty#getSuccessor() successor} of the {@link #getLocation() current location}.
	 * The <code>aside</code> method is called in any case, even if the current location is <code>null</code>
	 * or has no successor.
	 * @param station the station on which to continue the request
	 * @return how <code>station</code> reacted to the request
	 */
	public AsideAnswer forward( DockStation station );

	/**
	 * Calls the {@link Combiner#aside(AsideRequest)} method of <code>combiner</code> with the
	 * {@link DockableProperty#getSuccessor() successor} of the {@link #getLocation() current location}.
	 * The <code>aside</code> method is called in any case, even if the current location is <code>null</code>
	 * or has no successor.
	 * @param combiner the non-existent station on which to continue the request
	 * @return how <code>combiner</code> reacted to the request
	 */
	public AsideAnswer forward( Combiner combiner );

	/**
	 * Calls the {@link Combiner#aside(AsideRequest)} method of <code>combiner</code> with the
	 * {@link DockableProperty#getSuccessor() successor} of the {@link #getLocation() current location}.
	 * The <code>aside</code> method is called in any case, even if the current location is <code>null</code>
	 * or has no successor.
	 * @param combiner the non-existent station on which to continue the request
	 * @param layout the layout of the non-existent station, as was created by {@link DockStation#getPlaceholders()}.
	 * Can be <code>null</code> in which case this method behaves like {@link #forward(Combiner)}
	 * @return how <code>combiner</code> reacted to the request
	 */
	public AsideAnswer forward( Combiner combiner, PlaceholderMap layout );
}
