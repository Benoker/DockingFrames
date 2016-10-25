/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.toolbar.perspective;

import bibliothek.gui.dock.common.perspective.CContentPerspective;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.common.perspective.CStationPerspective;
import bibliothek.gui.dock.toolbar.CToolbarArea;
import bibliothek.gui.dock.toolbar.CToolbarContentArea;

/**
 * Represents a {@link CToolbarContentArea} as perspective. 
 * @author Benjamin Sigg
 */
public class CToolbarContentPerspective extends CContentPerspective{
	/**
	 * Wraps around <code>perspective</code> and represents the {@link CToolbarContentArea}
	 * with unique identifier <code>id</code>.
	 * @param perspective the source of all {@link CStationPerspective}s
	 * @param id the unique identifier of the {@link CToolbarContentArea}
	 * @throws IllegalStateException if one of the required {@link CStationPerspective}s is 
	 * already set up and has the wrong type.
	 */
	public CToolbarContentPerspective( CPerspective perspective, String id ){
		super( perspective, id );
		
		String north = CToolbarContentArea.getNorthToolbarIdentifier( id );
		String south = CToolbarContentArea.getSouthToolbarIdentifier( id );
		String east = CToolbarContentArea.getEastToolbarIdentifier( id );
		String west = CToolbarContentArea.getWestToolbarIdentifier( id );
		
		ensureType( north, CToolbarAreaPerspective.class );
		ensureType( south, CToolbarAreaPerspective.class );
		ensureType( east, CToolbarAreaPerspective.class );
		ensureType( west, CToolbarAreaPerspective.class );
		
		if( perspective.getStation( north ) == null ){
			perspective.addStation( new CToolbarAreaPerspective( north ) );
		}
		if( perspective.getStation( south ) == null ){
			perspective.addStation( new CToolbarAreaPerspective( south ) );
		}
		if( perspective.getStation( east ) == null ){
			perspective.addStation( new CToolbarAreaPerspective( east ) );
		}
		if( perspective.getStation( west ) == null ){
			perspective.addStation( new CToolbarAreaPerspective( west ) );
		}
	}

	/**
	 * Gets a perspective of the north {@link CToolbarArea}.
	 * @return the area, not <code>null</code>
	 */
	public CToolbarAreaPerspective getNorthToolbar(){
		return (CToolbarAreaPerspective)getPerspective().getStation( CToolbarContentArea.getNorthToolbarIdentifier( getId() ) );
	}
	
	/**
	 * Gets a perspective of the south {@link CToolbarArea}.
	 * @return the area, not <code>null</code>
	 */
	public CToolbarAreaPerspective getSouthToolbar(){
		return (CToolbarAreaPerspective)getPerspective().getStation( CToolbarContentArea.getSouthToolbarIdentifier( getId() ) );
	}
	
	/**
	 * Gets a perspective of the east {@link CToolbarArea}.
	 * @return the area, not <code>null</code>
	 */
	public CToolbarAreaPerspective getEastToolbar(){
		return (CToolbarAreaPerspective)getPerspective().getStation( CToolbarContentArea.getEastToolbarIdentifier( getId() ) );
	}

	/**
	 * Gets a perspective of the west {@link CToolbarArea}.
	 * @return the area, not <code>null</code>
	 */
	public CToolbarAreaPerspective getWestToolbar(){
		return (CToolbarAreaPerspective)getPerspective().getStation( CToolbarContentArea.getWestToolbarIdentifier( getId() ) );
	}
}
