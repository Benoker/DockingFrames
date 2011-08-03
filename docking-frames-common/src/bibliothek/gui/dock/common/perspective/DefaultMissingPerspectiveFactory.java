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
package bibliothek.gui.dock.common.perspective;

import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CExternalizeArea;
import bibliothek.gui.dock.common.CGridArea;
import bibliothek.gui.dock.common.CMinimizeArea;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.util.Path;

/**
 * This default implementation of a {@link MissingPerspectiveStrategy} creates {@link CStationPerspective}s for
 * the {@link CStation}s that are already known to the framework.
 * @author Benjamin Sigg
 */
public class DefaultMissingPerspectiveFactory implements MissingPerspectiveStrategy{
	public CStationPerspective createStation( String id, Path typeId ){
		if( CContentArea.TYPE_ID_CENTER.equals( typeId )){
			return new CGridPerspective( id, typeId, false );
		}
		if( CContentArea.TYPE_ID_MINIMIZE.equals( typeId )){
			return new CMinimizePerspective( id, typeId );
		}
		if( CGridArea.TYPE_ID.equals( typeId )){
			return new CGridPerspective( id, typeId, false );
		}
		if( CWorkingArea.TYPE_ID.equals( typeId )){
			return new CGridPerspective( id, typeId, true );
		}
		if( CMinimizeArea.TYPE_ID.equals( typeId )){
			return new CMinimizePerspective( id, typeId );
		}
		if( CExternalizeArea.TYPE_ID.equals( typeId )){
			return new CExternalizePerspective( id, typeId );
		}
		
		return null;
	}
}
