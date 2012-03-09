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
