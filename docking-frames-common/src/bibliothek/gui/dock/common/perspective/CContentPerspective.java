/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.common.perspective;

import bibliothek.gui.dock.common.CContentArea;

/**
 * A representation of a {@link CContentArea}. Please note that 
 * {@link CContentPerspective} is only a wrapper around a {@link CPerspective}, two
 * {@link CContentPerspective} represent the same {@link CContentArea} if they have the
 * same identifier.
 * @author Benjamin Sigg
 */
public class CContentPerspective {
	/** the map containing the stations of this perspective */
	private CPerspective perspective;
	/** the unique identifier of this perspective */
	private String id;
	
	/**
	 * Creates a new perspective. Clients should use {@link CPerspective#getContentArea()} to obtain
	 * a {@link CContentPerspective}.
	 * @param perspective a map containing the stations which are used by this perspective
	 * @param id the unique identifier of this perspective
	 */
	public CContentPerspective( CPerspective perspective, String id ){
		if( perspective == null ){
			throw new IllegalArgumentException( "perspective must not be null" );
		}
		if( id == null ){
			throw new IllegalArgumentException( "id must not be null" );
		}
		
		this.perspective = perspective;
		this.id = id;
		
		String center = CContentArea.getCenterIdentifier( id );
		String north = CContentArea.getNorthIdentifier( id );
		String south = CContentArea.getSouthIdentifier( id );
		String east = CContentArea.getEastIdentifier( id );
		String west = CContentArea.getWestIdentifier( id );
		
		ensureType( center, CGridPerspective.class );
		ensureType( north, CMinimizePerspective.class );
		ensureType( south, CMinimizePerspective.class );
		ensureType( east, CMinimizePerspective.class );
		ensureType( west, CMinimizePerspective.class );
		
		if( perspective.getStation( center ) == null ){
			perspective.addStation( new CGridPerspective( center, CContentArea.TYPE_ID_CENTER, false ));
		}
		if( perspective.getStation( north ) == null ){
			perspective.addStation( new CMinimizePerspective( north, CContentArea.TYPE_ID_MINIMIZE ));
		}
		if( perspective.getStation( south ) == null ){
			perspective.addStation( new CMinimizePerspective( south, CContentArea.TYPE_ID_MINIMIZE ));
		}
		if( perspective.getStation( east ) == null ){
			perspective.addStation( new CMinimizePerspective( east, CContentArea.TYPE_ID_MINIMIZE ));
		}
		if( perspective.getStation( west ) == null ){
			perspective.addStation( new CMinimizePerspective( west, CContentArea.TYPE_ID_MINIMIZE ));
		}
	}
	
	/**
	 * Searches the {@link CStationPerspective} with unique identifier <code>id</code> and makes sure the
	 * station is either <code>null</code> or of type <code>type</code>.
	 * @param id the unique identifier of the station
	 * @param type the type of the station
	 * @throws IllegalStateException if the station has the wrong type
	 */
	protected void ensureType( String id, Class<?> type ){
		CStationPerspective station = perspective.getStation( id );
		if( station != null && station.getClass() != type ){
			throw new IllegalStateException( "present root station '" + id + "' is of type '" + station.getClass() + "' but should be of type '" + type + "'" );
		}
	}

	/**
	 * Gets the unique identifier of this perspective
	 * @return the unique identifier
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * Gets the {@link CPerspective} in whose realm this {@link CContentPerspective} works.
	 * @return the perspective, not <code>null</code>
	 */
	public CPerspective getPerspective(){
		return perspective;
	}
	
	/**
	 * Gets the station that is shown in the middle of the content-area.
	 * @return the center
	 */
	public CGridPerspective getCenter(){
		return (CGridPerspective)perspective.getStation( CContentArea.getCenterIdentifier( id ));
	}
	
	/**
	 * Gets the station that is shown at the north side of the content-area.
	 * @return the minimize-area at the top side
	 */
	public CMinimizePerspective getNorth(){
		return (CMinimizePerspective)perspective.getStation( CContentArea.getNorthIdentifier( id ));
	}
	
	/**
	 * Gets the station that is shown at the south side of the content-area.
	 * @return the minimize-area at the bottom side
	 */
	public CMinimizePerspective getSouth(){
		return (CMinimizePerspective)perspective.getStation( CContentArea.getSouthIdentifier( id ));
	}
	
	/**
	 * Gets the station that is shown at the east side of the content-area.
	 * @return the minimize-area at the right side
	 */
	public CMinimizePerspective getEast(){
		return (CMinimizePerspective)perspective.getStation( CContentArea.getEastIdentifier( id ));
	}
	
	/**
	 * Gets the station that is shown at the west side of the content-area.
	 * @return the minimize-area at the left side
	 */
	public CMinimizePerspective getWest(){
		return (CMinimizePerspective)perspective.getStation( CContentArea.getWestIdentifier( id ));
	}
}
