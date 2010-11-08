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

import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.intern.CPlaceholderStrategy;
import bibliothek.gui.dock.common.intern.CommonSingleDockableFactory;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.util.Path;

/**
 * A class that represents a {@link SingleCDockable}.
 * @author Benjamin Sigg
 */
public class SingleCDockablePerspective extends AbstractCDockablePerspective {
	private CommonElementPerspective intern;
	
	private String uniqueId;
	
	/**
	 * Creates a new representation of a {@link SingleCDockable}.
	 * @param uniqueId the unique identifier of this dockable, not <code>null</code>
	 */
	public SingleCDockablePerspective( String uniqueId ){
		if( uniqueId == null ){
			throw new IllegalArgumentException( "uniqueId must not be null" );
		}
		this.uniqueId = uniqueId;
	}
	
	/**
	 * Called the first time {@link #intern()} is called, this method creates
	 * the intern representation of this {@link SingleCDockablePerspective}.
	 * @return the internal representation of <code>this</code>
	 */
	protected CommonElementPerspective create(){
		return new Intern();
	}
	
	public CommonElementPerspective intern(){
		if( intern == null ){
			intern = create();
		}
		return intern;
	}
	
	/**
	 * Gets the unique identifier that is associated with this element.
	 * @return the unique identifier
	 */
	public String getUniqueId(){
		return uniqueId;
	}

	public CDockablePerspective asDockable(){
		return this;
	}
	
	public CStationPerspective asStation(){
		return null;
	}
	
	/**
	 * The type of object that is created by the default implementation of {@link SingleCDockablePerspective#create()}
	 * @author Benjamin Sigg
	 */
	protected class Intern implements PerspectiveDockable, CommonElementPerspective{
		private PerspectiveStation parent;
		
		public CElementPerspective getElement(){
			return SingleCDockablePerspective.this;
		}
		
		public String getFactoryID(){
			return CommonSingleDockableFactory.BACKUP_FACTORY_ID;
		}
		
		public PerspectiveStation asStation(){
			return null;
		}
		
		public PerspectiveDockable asDockable(){
			return this;
		}
		
		public void setParent( PerspectiveStation parent ){
			this.parent = parent;	
		}
		
		public Path getPlaceholder(){
			return CPlaceholderStrategy.getSingleDockablePlaceholder( uniqueId );
		}
		
		public PerspectiveStation getParent(){
			return parent;
		}
	}
}
