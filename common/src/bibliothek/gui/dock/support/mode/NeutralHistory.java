/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.support.mode;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.Dockable;

/**
 * Stores the history of neutral modes applied to a {@link Dockable}.
 * @author Benjamin Sigg
 */
public class NeutralHistory {
	/** the element whose history is stored */
	private Dockable dockable;
	
	/** the initial mode of {@link #dockable} */
	private Mode mode;
	
	/** the already applied neutral modes */
	private List<NeutralMode<?>> modes = new ArrayList<NeutralMode<?>>();
	
	/** data objects created by {@link NeutralMode} */
	private List<Object> data = new ArrayList<Object>();
	
	/**
	 * Creates a new history.
	 * @param dockable the element whose history is stored
	 * @param mode the initial mode of <code>dockable</code>
	 * @throws IllegalArgumentException if either <code>dockable</code>
	 * or <code>mode</code> is <code>null</code>
	 */
	public NeutralHistory( Dockable dockable, Mode mode ){
		if( dockable == null )
			throw new IllegalArgumentException( "dockable null" );
		
		if( mode == null )
			throw new IllegalArgumentException( "mode null" );
		
		this.dockable = dockable;
		this.mode = mode;
	}
	
	/**
	 * Gets the number of neutral modes this history stores.
	 * @return the number of modes
	 */
	public int getSize(){
		return modes.size();
	}
	
	/**
	 * Tries to apply the next {@link NeutralMode} to its dockable.
	 * @param callback to interact with other histories
	 * @return <code>true</code> if a new mode was applied, <code>false</code>
	 * if there is no next mode.
	 */
	@SuppressWarnings("unchecked")
	public boolean advance( NeutralModeCallback callback ){
		NeutralMode<Object> next;
		if( modes.isEmpty() ){
			next = (NeutralMode<Object>)mode.getNeutralMode( dockable );
		}
		else{
			NeutralMode<Object> last = (NeutralMode<Object>)modes.get( modes.size()-1 );
			next = (NeutralMode<Object>)last.getNext( dockable, data.get( modes.size()-1 ) );
		}
		if( next == null )
			return false;
		
		Object data = next.toNeutral( dockable, callback );
		modes.add( next );
		this.data.add( data );
		
		return true;
	}

	/**
	 * Restores and deletes the last neutral state.
	 * @param callback to interact with other histories
	 * @return <code>true</code> if there are more neutral states
	 * to restore
	 */
	@SuppressWarnings("unchecked")
	public boolean restore( NeutralModeCallback callback ){
		if( getSize() == 0 )
			throw new IllegalStateException( "nothing to restore left" );
		
		int last = modes.size()-1;
		NeutralMode<Object> mode = (NeutralMode<Object>)modes.remove( last );
		Object data = this.data.remove( last );
		
		mode.toSpecific( dockable, data, callback );
		
		return getSize() > 0;
	}
}
