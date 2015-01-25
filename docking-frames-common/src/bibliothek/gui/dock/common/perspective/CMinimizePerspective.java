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

import bibliothek.gui.dock.common.CMinimizeArea;
import bibliothek.gui.dock.common.intern.station.CommonDockStationFactory;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.perspective.mode.CMinimizedModePerspective;
import bibliothek.gui.dock.common.perspective.mode.CModeAreaPerspective;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.flap.FlapDockPerspective;
import bibliothek.gui.dock.station.flap.FlapDockProperty;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;

/**
 * This {@link PerspectiveStation} represents a {@link CMinimizeArea}.
 * @author Benjamin Sigg
 */
public class CMinimizePerspective implements CStationPerspective{
	/** the intern representation of this perspective */
	private CommonFlapDockPerspective delegate;
	
	/** a unique identifier of this station */
	private String id;
	
	/** the owner of this object */
	private CPerspective perspective;

	/** The type of this perspective */
	private Path typeId;
	
	/** Whether this is a root station s*/
	private boolean root = true;
	
	/** the mode this station represents */
	private CModeAreaPerspective mode = new CModeAreaPerspective() {
		public String getUniqueId(){
			return CMinimizePerspective.this.getUniqueId();
		}
		public boolean isChild( PerspectiveDockable dockable ){
			return dockable.getParent() == intern();
		}
		public boolean isChildLocation( DockableProperty location ){
			return location instanceof FlapDockProperty;
		}
	};

	/**
	 * Creates a new, empty perspective.
	 * @param id the unique identifier of this perspective
	 */
	public CMinimizePerspective( String id ){
		this( id, null );
	}
	
	/**
	 * Creates a new, empty perspective.
	 * @param id the unique identifier of this perspective
	 * @param typeId the type of this station, can be <code>null</code>
	 */
	public CMinimizePerspective( String id, Path typeId ){
		if( id == null ){
			throw new IllegalArgumentException( "id is null" );
		}
		this.id = id;
		if( typeId == null ){
			typeId = CMinimizeArea.TYPE_ID;
		}
		this.typeId = typeId;
		delegate = new CommonFlapDockPerspective();
	}

	public void setPerspective( CPerspective perspective ){
		if( this.perspective != null ){
		    CMinimizedModePerspective mode = (CMinimizedModePerspective) this.perspective.getLocationManager().getMode( ExtendedMode.MINIMIZED );
		    mode.remove( this.mode );
		}
		this.perspective = perspective;
		if( this.perspective != null ){
			CMinimizedModePerspective mode = (CMinimizedModePerspective) this.perspective.getLocationManager().getMode( ExtendedMode.MINIMIZED );
			mode.add( this.mode );
		}
	}
	
	public CPerspective getPerspective(){
		return perspective;
	}

	public Path getTypeId(){
		return typeId;
	}
	
	public boolean isRoot(){
		return root;
	}
	
	public void setRoot( boolean root ){
		this.root = root;
	}
	
	/**
	 * Adds <code>dockable</code> at the end of the list of children of this area.
	 * @param dockable the element to add
	 */
	public void add( CDockablePerspective dockable ){
		delegate.add( dockable.intern().asDockable() );
	}
	
	/**
	 * Inserts <code>dockable</code> at location <code>index</code> to the list of children of this area.
	 * @param index the location of <code>dockable</code>
	 * @param dockable the element to insert
	 */
	public void insert( int index, CDockablePerspective dockable ){
		delegate.insert( index, dockable.intern().asDockable() );
	}
	
	/**
	 * Adds a placeholder for <code>dockable</code> at the end of the list of children of this area.
	 * @param dockable the element for which a placeholder will be registered
	 */
	public void addPlaceholder( CDockablePerspective dockable ){
		delegate.addPlaceholder( dockable.intern().asDockable() );
	}

	/**
	 * Adds a placeholder at the end of the list of children of this area.
	 * @param placeholder the new placeholder, not <code>null</code>
	 */
	public void addPlaceholder( Path placeholder ){
		delegate.addPlaceholder( placeholder );
	}
	
	/**
	 * Inserts a placeholder for <code>dockable</code> at location <code>index</code> in the list of
	 * children of this area.
	 * @param index the location of the placeholder
	 * @param dockable the element for which a placeholder will be registered
	 */
	public void insertPlaceholder( int index, CDockablePerspective dockable ){
		delegate.insertPlaceholder( index, dockable.intern().asDockable() );
	}

	/**
	 * Inserts a placeholder at location <code>index</code> in the list of
	 * children of this area.
	 * @param index the location of the placeholder
	 * @param placeholder the new placeholder, not <code>null</code>
	 */
	public void insertPlaceholder( int index, Path placeholder ){
		delegate.insertPlaceholder( index, placeholder );
	}
	
	/**
	 * Gets the location of <code>dockable</code> on this area.
	 * @param dockable some child of this area
	 * @return the location or -1 if not found
	 */
	public int indexOf( CDockablePerspective dockable ){
		return delegate.indexOf( dockable.intern().asDockable() );
	}
	
	/**
	 * Removes <code>dockable</code> from this area.
	 * @param dockable the element to remove
	 * @return <code>true</code> if <code>dockable</code> was found and removed, 
	 * <code>false</code> otherwise
	 */
	public boolean remove( CDockablePerspective dockable ){
		return delegate.remove( dockable.intern().asDockable() );
	}
	
	/**
	 * Removes the <code>index</code>'th child of this area.
	 * @param index the index of the child to remove
	 * @return the child that was removed, <code>null</code> if the child is not a {@link CDockablePerspective}
	 */
	public CDockablePerspective remove( int index ){
		PerspectiveDockable dockable = delegate.remove( index );
		if( dockable instanceof CommonElementPerspective ){
			return ((CommonElementPerspective)dockable).getElement().asDockable();
		}
		else{
			return null;
		}
	}
	
	public CDockablePerspective asDockable(){
		return null;
	}

	public CStationPerspective asStation(){
		return this;
	}
	
	public String getFactoryID(){
		return delegate.getFactoryID();
	}
	
	public String getUniqueId(){
		return id;
	}
	
	public CommonFlapDockPerspective intern(){
		return delegate;
	}

	public PlaceholderMap getPlaceholders(){
		return delegate.getPlaceholders();
	}
	
	public void setPlaceholders( PlaceholderMap placeholders ){
		delegate.setPlaceholders( placeholders );	
	}
	
	public boolean isWorkingArea(){
		return false;
	}
	
	/**
	 * The type of object that is used by {@link CMinimizePerspective} as intern representation.
	 * @author Benjamin Sigg
	 */
	public class CommonFlapDockPerspective extends FlapDockPerspective implements CommonDockStationPerspective{
		public CElementPerspective getElement(){
			return CMinimizePerspective.this;
		}
		
		@Override
		public String getFactoryID(){
			return CommonDockStationFactory.FACTORY_ID;
		}
		
		public String getConverterID(){
			return super.getFactoryID();
		}
	}
}
