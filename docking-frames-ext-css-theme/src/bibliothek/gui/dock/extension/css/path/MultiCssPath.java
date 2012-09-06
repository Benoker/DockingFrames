/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.path;

import java.util.ArrayList;

import java.util.List;

import bibliothek.gui.dock.extension.css.CssNode;
import bibliothek.gui.dock.extension.css.CssPath;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A {@link MultiCssPath} is a list of {@link CssPath}s.
 * @author Benjamin Sigg
 */
public class MultiCssPath extends AbstractCssPath{
	/** the different sub-paths */
	private List<CssPath> parts = new ArrayList<CssPath>();
	
	private int[] offsets = null;
	private int size = -1;
	
	private CssPathListener partListener = new CssPathListener(){
		@Override
		public void pathChanged( CssPath path ){
			size = calculateSize();
			offsets = calculateOffsets( offsets );
			firePathChanged();
		}
	};
	
	/**
	 * Creates a new multi path
	 * @param parts all the parts of the path
	 */
	public MultiCssPath( CssPath... parts ){
		for( CssPath path : parts ){
			addPart( path );
		}
	}
	
	@Override
	protected void bind(){
		for( CssPath part : parts ){	
			part.addPathListener( partListener );
		}
		size = calculateSize();
		offsets = calculateOffsets( offsets );
	}
	
	@Override
	protected void unbind(){
		for( CssPath part : parts ){
			part.removePathListener( partListener );
		}
		offsets = null;
	}
	
	@Override
	public int getSize(){
		if( isBound() ){
			return size;
		}
		else{
			return calculateSize();
		}
	}
	
	private int calculateSize(){
		int count = 0;
		for( CssPath part : parts ){
			count += part.getSize();
		}
		return count;
	}
	
	@Override
	@Todo( compatibility=Compatibility.COMPATIBLE, priority=Priority.MINOR,
		target=Version.VERSION_1_1_2, description="use a binary search to find the offset-index")
	public CssNode getNode( int index ){
		int[] offsets;
		if( isBound() ){
			offsets = this.offsets;
		}
		else{
			offsets = calculateOffsets( null );
		}
		
		for( int i = 0; i < offsets.length; i++ ){
			if( index < offsets[i]){
				if( i == 0 ){
					return parts.get( 0 ).getNode( index );
				}
				else{
					return parts.get( i ).getNode( index - offsets[i-1] );
				}
			}
		}
		
		throw new IllegalArgumentException( "index out of bonuds: " + index );
	}
	
	private int[] calculateOffsets( int[] offsets ){
		if( offsets == null || offsets.length != parts.size() ){
			offsets = new int[ parts.size() ];
		}
		int index = 0;
		int offset = 0;
		
		for( CssPath part : parts ){
			offset += part.getSize();
			offsets[ index++ ] = offset;
		}
		
		return offsets;
	}
	
	/**
	 * Gets the number of parts of this path.
	 * @return the number of parts
	 */
	public int getPartsCount(){
		return parts.size();
	}
	
	/**
	 * Gest the <code>index</code>'th part of this path.
	 * @param index the index of some part
	 * @return the part
	 */
	public CssPath getPart( int index ){
		return parts.get( index );
	}
	
	/**
	 * Adds <code>part</code> at the end of this list of paths.
	 * @param part the part to add, not <code>null</code>
	 */
	public void addPart( CssPath part ){
		addPart( getPartsCount(), part );
	}
	
	/**
	 * Inserts <code>part</code> at the <code>index</code>'th location of
	 * this list of paths.
	 * @param index the index of the new part
	 * @param part the new part, not <code>null</code>
	 */
	public void addPart( int index, CssPath part ){
		if( part == null ){
			throw new IllegalArgumentException( "part must not be null" );
		}
		parts.add( index, part );
		if( isBound() ){
			part.addPathListener( partListener );
			firePathChanged();
		}
	}
	
	/**
	 * Gets the location of <code>part</code> in this list of paths.
	 * @param part the part to search
	 * @return the location or <code>-1</code> if not found
	 */
	public int indexOf( CssPath part ){
		return parts.indexOf( part );
	}
	

	/**
	 * Removes the <code>index</code>'th part of this path.
	 * @param index the index of the part to remove
	 * @return the part that was removed
	 */
	public CssPath removePart( int index ){
		CssPath part = parts.remove( index );
		if( isBound() ){
			part.removePathListener( partListener );
			firePathChanged();
		}
		return part;
	}
	
	/**
	 * Removes <code>part</code> from this list of paths.
	 * @param part the part to remove
	 */
	public void remove( CssPath part ){
		int index = indexOf( part );
		if( index >= 0 ){
			removePart( index );
		}
	}
}
