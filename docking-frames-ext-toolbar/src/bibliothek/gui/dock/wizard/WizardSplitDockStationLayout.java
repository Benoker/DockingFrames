/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.wizard;

import bibliothek.gui.dock.station.split.SplitDockStationLayout;

/**
 * Describes the layout of a {@link WizardSplitDockStation}.
 * @author Benjamin Sigg
 */
public class WizardSplitDockStationLayout extends SplitDockStationLayout {
	/** the persistent columns and their sizes */
	private Column[] columns;

	public WizardSplitDockStationLayout( Entry root, int fullscreen, boolean hasFullscreenAction ){
		super( root, fullscreen, hasFullscreenAction );
	}

	public void setColumns( Column[] columns ){
		this.columns = columns;
	}
	
	public Column[] getColumns(){
		return columns;
	}
	
	public static class Column {
		private int size;
		private int[] cellKeys;
		private int[] cellSizes;

		public Column( int size, int[] cellKeys, int[] cellSizes ){
			this.size = size;
			this.cellKeys = cellKeys;
			this.cellSizes = cellSizes;
			
			if( cellKeys.length != cellSizes.length ){
				throw new IllegalArgumentException( "the size of cellKeys and cellSizes must be equal" );
			}
		}
		
		public int getSize(){
			return size;
		}
		
		public int[] getCellKeys(){
			return cellKeys;
		}
		
		public int[] getCellSizes(){
			return cellSizes;
		}
	}
}
