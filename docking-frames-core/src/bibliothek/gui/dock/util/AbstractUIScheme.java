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
package bibliothek.gui.dock.util;

import java.util.ArrayList;
import java.util.List;


/**
 * An abstract implementation of {@link UIScheme} offering support for listeners.
 * @author Benjamin Sigg
 *
 * @param <V> The kind of value managed by the scheme
 * @param <U> The kind of {@link UIValue} required to access the values
 * @param <B> The kind of filter between <code>V</code> and <code>U</code>
 */
public abstract class AbstractUIScheme<V, U extends UIValue<V>, B extends UIBridge<V,U>> implements UIScheme<V,U,B>{
	/** all the listeners of this scheme */
	private List<UISchemeListener<V, U, B>> listeners = new ArrayList<UISchemeListener<V,U,B>>();
	
	public void addListener( UISchemeListener<V, U, B> listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		listeners.add( listener );
	}

	public void removeListener( UISchemeListener<V, U, B> listener ){
		listeners.remove( listener );
	}

	/**
	 * Fires the event <code>event</code> to all registered {@link UISchemeListener}s.
	 * @param event the event to fire
	 */
	@SuppressWarnings("unchecked")
	protected void fire( UISchemeEvent<V, U, B> event ){
		UISchemeListener<V, U, B>[] listeners = this.listeners.toArray( new UISchemeListener[ this.listeners.size() ] );
		for( UISchemeListener<V,U,B> listener : listeners ){
			listener.changed( event );
		}
	}
}
