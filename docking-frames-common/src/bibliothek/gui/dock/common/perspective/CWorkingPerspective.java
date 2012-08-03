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

import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.split.SplitDockPerspective.Entry;
import bibliothek.gui.dock.station.split.SplitDockPerspective.EntryListener;
import bibliothek.gui.dock.station.split.SplitDockPerspective.Leaf;
import bibliothek.util.Path;

/**
 * A representation of a {@link CWorkingArea}. When using this perspective to handle the children of a {@link CWorkingArea} 
 * the following rules must be followed:
 * <ul>
 * 	<li>Adding or removing a {@link CDockablePerspective} to this station may trigger a call to {@link CDockablePerspective#setWorkingArea(CStationPerspective)}, clients
 * can however first add/remove a dockable and then alter the working-area of the dockable to their likings.</li>
 * 	<li>Adding: the working-area of a child is only set to <code>this</code> if the working-area had a value of <code>null</code></li>
 * 	<li>Removing: the working-area of a child is only set to <code>null</code> if the working-area had a value of <code>this</code> and if
 * 	{@link #isAutoUnset()} returns <code>true</code>. </li>
 *  <li>This perspective recursively visits all dockables and stations when adding or removing a child.</li>
 * 	<li>{@link MultipleCDockablePerspective}: if the working-area is set, then it is automatically set in the {@link MultipleCDockable} as well.</li>
 * 	<li>{@link SingleCDockablePerspective}: if the working-area is set, then it is automatically set in the {@link SingleCDockable} as well.</li>
 * 	<li>This perspective does not track changes on children. If some {@link PerspectiveStation} was added and then some children are added
 * to that station, then this <code>CworkingPerspective</code> will <b>not</b> set the working-area property of the new children.</li>
 * </ul>
 * @author Benjamin Sigg
 */
public class CWorkingPerspective extends CGridPerspective{
	private boolean autoUnset = false;
	
	/**
	 * Creates a new working area.
	 * @param id the unique identifier of this area
	 */
	public CWorkingPerspective( String id ){
		this( id, null );
	}
	
	/**
	 * Creates a new working area.
	 * @param id the unique identifier of this area
	 * @param typeId the type of this station, can be <code>null</code>
	 */
	public CWorkingPerspective( String id, Path typeId ){
		super( id, typeId == null ? CWorkingArea.TYPE_ID : typeId, true );
	}
	
	/**
	 * Tells this station to set the {@link CDockablePerspective#setWorkingArea(CStationPerspective) working-area}
	 * to <code>null</code> when a child of this station is removed. The default value of this property is
	 * <code>false</code>.
	 * @param autoUnset whether the working-area should be automatically set to <code>null</code>
	 */
	public void setAutoUnset( boolean autoUnset ){
		this.autoUnset = autoUnset;
	}
	
	/**
	 * Tells whether the working-area of children is automatically set to <code>null</code> when the children
	 * are removed from this station.
	 * @return whether the working-area is set to <code>null</code>
	 */
	public boolean isAutoUnset(){
		return autoUnset;
	}
	
	@Override
	protected CommonSplitDockPerspective create(){
		CommonSplitDockPerspective result = super.create();
		result.addListener( new EntryListener(){
			public void removed( Entry parent, Entry child ){
				if( isAutoUnset() ){
					remove( child );
				}
			}
			
			public void added( Entry parent, Entry child ){
				add( child );
			}
			
			public void modified( Leaf leaf, PerspectiveDockable oldDockable, PerspectiveDockable newDockable ){
				if( oldDockable != null ){
					if( isAutoUnset() ){
						remove( oldDockable );
					}
				}
				if( newDockable != null ){
					add( newDockable );
				}
			}
		});
		return result;
	}
	
	private void remove( Entry child ){
		if( child != null ){
			if( child.asNode() != null ){
				remove( child.asNode().getChildA() );
				remove( child.asNode().getChildB() );
			}
			else{
				PerspectiveDockable dockable = child.asLeaf().getDockable();
				if( dockable != null ){
					remove( dockable );
				}
			}
		}
	}
	
	private void remove( PerspectiveDockable dockable ){
		if( dockable instanceof CommonElementPerspective ){
			CDockablePerspective cdockable = ((CommonElementPerspective)dockable).getElement().asDockable();
			if( cdockable.getWorkingArea() == this ){
				cdockable.setWorkingArea( null );
			}
		}
		PerspectiveStation station = dockable.asStation();
		if( station != null ){
			for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
				remove( station.getDockable( i ));
			}
		}
	}
	
	private void add( Entry child ){
		if( child != null ){
			if( child.asNode() != null ){
				add( child.asNode().getChildA() );
				add( child.asNode().getChildB() );
			}
			else{
				PerspectiveDockable dockable = child.asLeaf().getDockable();
				if( dockable != null ){
					add( dockable );
				}
			}
		}
	}
	
	private void add( PerspectiveDockable dockable ){
		if( dockable instanceof CommonElementPerspective ){
			CDockablePerspective cdockable = ((CommonElementPerspective)dockable).getElement().asDockable();
			if( cdockable.getWorkingArea() == null ){
				cdockable.setWorkingArea( this );
			}
		}
		PerspectiveStation station = dockable.asStation();
		if( station != null ){
			for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
				add( station.getDockable( i ));
			}
		}
	}
}
