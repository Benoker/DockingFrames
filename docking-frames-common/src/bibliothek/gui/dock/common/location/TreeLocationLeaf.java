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
package bibliothek.gui.dock.common.location;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.split.SplitDockPathProperty;

/**
 * Represents a leaf in a path of tree-nodes.
 * @author Benjamin Sigg
 */
public class TreeLocationLeaf extends AbstractStackholdingLocation{
	/** the turn above this location */
	private AbstractTreeLocation parent;
	/** the unique id of this leaf */
	private long leafId;
	
	/**
	 * Creates a new leaf.
	 * @param parent the turn above this location
	 * @param leafId the unique identifier of the leaf represented by this location, can be -1
	 */
	public TreeLocationLeaf( AbstractTreeLocation parent, long leafId ){
		if( parent == null )
			throw new NullPointerException( "parent must not be null" );
		this.parent = parent;
		this.leafId = leafId;
	}
	
	@Override
	public AbstractTreeLocation getParent(){
		return parent;
	}
	
	/**
	 * Gets the unique identifier of the leaf which is represented by this {@link TreeLocationLeaf}.
	 * @return the identifier or -1
	 */
	public long getLeafId(){
		return leafId;
	}
	
	protected SplitDockPathProperty findParentProperty(){
		return parent.findProperty( null );
	}
	
	@Override
	public SplitDockPathProperty findProperty( DockableProperty successor ){
		SplitDockPathProperty property = findParentProperty();
		property.setSuccessor( successor );
		property.setLeafId( leafId );
		return property;
	}
	
	/**
	 * @deprecated see {@link CLocation#aside()} for an explanation.
	 */
	@Deprecated
	@Override
	public CLocation aside() {
	    return stack( 1 );
	}
	
	@Override
	public String findRoot(){
		return parent.findRoot();
	}

	@Override
	public String toString() {
	    return String.valueOf( parent ) + " [leaf " + leafId + "]";
	}

	@Override
	public ExtendedMode findMode(){
		return parent.findMode();
	}
}
