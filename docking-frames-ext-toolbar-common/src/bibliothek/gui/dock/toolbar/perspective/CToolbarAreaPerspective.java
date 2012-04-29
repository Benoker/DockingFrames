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

import bibliothek.gui.dock.common.perspective.CDockablePerspective;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.common.perspective.CStationPerspective;
import bibliothek.gui.dock.common.perspective.CommonElementPerspective;
import bibliothek.gui.dock.toolbar.CToolbarArea;
import bibliothek.util.Path;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * Represents a {@link CToolbarArea} as perspective.
 * @author Benjamin Sigg
 */
@Todo( priority=Priority.MAJOR, target=Version.VERSION_1_1_1, compatibility=Compatibility.COMPATIBLE,
	description="implement this class")
public class CToolbarAreaPerspective implements CStationPerspective{
	private boolean root = true;
	private String id;
	
	/**
	 * Creates a new perspective.
	 * @param id the unique identifier of this station
	 */
	public CToolbarAreaPerspective( String id ){
		this.id = id;
	}
	
	@Override
	public CommonElementPerspective intern(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CDockablePerspective asDockable(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CStationPerspective asStation(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUniqueId(){
		return id;
	}

	@Override
	public Path getTypeId(){
		return CToolbarArea.TYPE_ID;
	}

	@Override
	public void setPerspective( CPerspective perspective ){
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isWorkingArea(){
		return false;
	}

	@Override
	public boolean isRoot(){
		return root;
	}

	@Override
	public void setRoot( boolean root ){
		this.root = root;
	}
}
