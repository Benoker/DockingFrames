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
package bibliothek.gui.dock.title;

import bibliothek.gui.dock.title.DockTitle.Orientation;
import bibliothek.gui.dock.util.swing.Rotation;

/**
 * Converter for telling the framework how to render text given the orientation of a {@link DockTitle}.
 * @author Benjamin Sigg
 */
public interface OrientationToRotationStrategy {
	/** Default implementation of  {@link OrientationToRotationStrategy} */
	public static final OrientationToRotationStrategy DEFAULT = new OrientationToRotationStrategy(){
		public Rotation convert( Orientation orientation, DockTitle title ){
			if( orientation.isHorizontal() ) {
				return Rotation.DEGREE_0;
			}
			else {
				return Rotation.DEGREE_90;
			}
		}

		public void addListener( OrientationToRotationStrategyListener listener ){
			// ignore
		}

		public void install( DockTitle title ){
			// ignore			
		}

		public void removeListener( OrientationToRotationStrategyListener listener ){
			// ignore
		}

		public void uninstall( DockTitle title ){
			// ignore
		}
	};

	/**
	 * Tells how to render the text of <code>title</code>.
	 * @param orientation the orientation that is or will be used by <code>title</code>.
	 * @param title the title whose text gets rendered
	 * @return the rotation for the text of <code>title</code>, not <code>null</code>
	 */
	public Rotation convert( Orientation orientation, DockTitle title );

	/**
	 * Informs this strategy that from now on it will be used for <code>title</code>.
	 * @param title the title that uses this strategy
	 */
	public void install( DockTitle title );

	/**
	 * Informs this strategy that from now on it will not be used for <code>title</code>.
	 * @param title the title that was using this strategy
	 */
	public void uninstall( DockTitle title );

	/**
	 * Adds the listener <code>listener</code> to this strategy.
	 * @param listener a new listener, not <code>null</code>
	 */
	public void addListener( OrientationToRotationStrategyListener listener );

	/**
	 * Removes <code>listener</code> from this strategy.
	 * @param listener the listener to remove
	 */
	public void removeListener( OrientationToRotationStrategyListener listener );
}
