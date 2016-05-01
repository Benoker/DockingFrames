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
package bibliothek.gui.dock.common.mode;

import bibliothek.gui.dock.facile.mode.ExternalizedMode;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.MaximizedMode;
import bibliothek.gui.dock.facile.mode.MinimizedMode;
import bibliothek.gui.dock.facile.mode.NormalMode;
import bibliothek.util.ClientOnly;
import bibliothek.util.Path;

/**
 * A extendible set of unique singleton identifiers for the various instances of {@link LocationMode mode}.
 * @author Benjamin Sigg
 */
@ClientOnly
public class ExtendedMode {
	/** the dockable is as small as possible, see also {@link MinimizedMode} */
	public static final ExtendedMode MINIMIZED = new ExtendedMode( MinimizedMode.IDENTIFIER );
	/** the dockable is as big as possible, see also {@link MaximizedMode} */
	public static final ExtendedMode MAXIMIZED = new ExtendedMode( MaximizedMode.IDENTIFIER );
	/** the dockable has the normal size, see also {@link NormalMode} */
	public static final ExtendedMode NORMALIZED = new ExtendedMode( NormalMode.IDENTIFIER );
	/** the dockable is floating in a dialog, see also {@link ExternalizedMode} */
	public static final ExtendedMode EXTERNALIZED = new ExtendedMode( ExternalizedMode.IDENTIFIER );
	
	/** the unique identifier of the mode */
	private Path modeIdentifier;
	
	/**
	 * Creates a new key.
	 * @param modeIdentifier the unique identifier of the mode
	 */
	public ExtendedMode( Path modeIdentifier ){
		if( modeIdentifier == null )
			throw new IllegalArgumentException( "identifer must not be null" );
		this.modeIdentifier = modeIdentifier;
	}
	
	/**
	 * Gets the unique identifier of the mode.
	 * @return the identifier, not <code>null</code>
	 */
	public Path getModeIdentifier(){
		return modeIdentifier;
	}

	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((modeIdentifier == null) ? 0 : modeIdentifier.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object obj ){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtendedMode other = (ExtendedMode) obj;
		if (modeIdentifier == null) {
			if (other.modeIdentifier != null)
				return false;
		} else if (!modeIdentifier.equals( other.modeIdentifier ))
			return false;
		return true;
	}
}
