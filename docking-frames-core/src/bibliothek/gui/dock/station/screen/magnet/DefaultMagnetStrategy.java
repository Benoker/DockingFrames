/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.station.screen.magnet;

import bibliothek.gui.dock.station.screen.ScreenDockWindow;


/**
 * The {@link DefaultMagnetStrategy} is based on the {@link DefaultMagnetOperation}. It searches the nearest 
 * attracted {@link ScreenDockWindow} to a moved side and if that other window is nearer than a given threshold
 * then the moved window is attracted.
 * @author Benjamin Sigg
 */
public class DefaultMagnetStrategy implements MagnetStrategy{
	/** how many pixels two sides can be apart and still attract each other */
	private int threshold = 10;
	
	/**
	 * Sets how many pixels two sides can be apart and still attract each other.
	 * @param threshold the maximal distance, at least 1
	 */
	public void setThreshold( int threshold ){
		if( threshold < 1 ){
			throw new IllegalArgumentException( "the threshold must be at least 1" );
		}
		this.threshold = threshold;
	}
	
	/**
	 * Tells how many pixels two sides can be apart and still attract each other.
	 * @return the maximal distance, at least 1
	 */
	public int getThreshold(){
		return threshold;
	}
	
	public void install( MagnetController controller ){
		// ignore
	}

	public void uninstall( MagnetController controller ){
		// ignore
	}
	
	public MagnetOperation start( MagnetController controller, MagnetRequest request ){
		return new DefaultMagnetOperation( threshold );
	}
}
