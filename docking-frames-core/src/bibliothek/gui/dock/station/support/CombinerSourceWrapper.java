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
package bibliothek.gui.dock.station.support;

import java.awt.Dimension;
import java.awt.Point;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.DockableDisplayer;

/**
 * A wrapper around a {@link CombinerSource}, can be used to easily override
 * methods of some source. 
 * @author Benjamin Sigg
 */
public class CombinerSourceWrapper implements CombinerSource{
	/** the wrapped source */
	private CombinerSource delegate;
	
	/**
	 * Creates a new wrapper.
	 * @param delegate the source to hide
	 */
	public CombinerSourceWrapper( CombinerSource delegate) {
		if( delegate == null ){
			throw new IllegalArgumentException( "delegate must not be null" );
		}
		this.delegate = delegate;
	}
	
	/**
	 * Gets the source which is hidden by this wrapper.
	 * @return the source, not <code>null</code>
	 */
	public CombinerSource getDelegate(){
		return delegate;
	}
	
	public Point getMousePosition(){
		return delegate.getMousePosition();
	}
	
	public Dockable getNew(){
		return delegate.getNew();
	}
	
	public DockableDisplayer getOldDisplayer(){
		return delegate.getOldDisplayer();
	}

	public Dockable getOld(){
		return delegate.getOld();
	}

	public DockStation getParent(){
		return delegate.getParent();
	}

	public PlaceholderMap getPlaceholders(){
		return delegate.getPlaceholders();
	}

	public Dimension getSize(){
		return delegate.getSize();
	}

	public boolean isMouseOverTitle(){
		return delegate.isMouseOverTitle();
	}
}
