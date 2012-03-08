package bibliothek.gui.dock.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.border.EmptyBorder;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.split.DefaultSplitDividerStrategy;
import bibliothek.gui.dock.station.split.DefaultSplitLayoutManager;
import bibliothek.gui.dock.station.split.Node;
import bibliothek.gui.dock.station.split.PutInfo;
import bibliothek.gui.dock.station.split.PutInfo.Put;
import bibliothek.gui.dock.station.split.Root;
import bibliothek.gui.dock.station.split.SplitLayoutManager;
import bibliothek.gui.dock.station.split.SplitNode;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;

/**
 * A {@link WizardSplitDockStation} has some additional restrictions and other behavior than an ordinary {@link SplitDockStation}:
 * <ul>
 * 	<li>The {@link Dockable}s are ordered in columns.</li>
 *  <li>The station does not use up empty space if not needed.</li>
 *  <li>Moving a divider changes the preferred size of the station. The parent of the station should allow the size
 *  of the station to change (e.g. by using a {@link BorderLayout}).</li>
 * </ul>
 * @author Benjamin Sigg
 */
public class WizardSplitDockStation extends SplitDockStation{
	/** the side where dockables are pushed to */
	public static enum Side{
		TOP, LEFT, RIGHT, BOTTOM;
		
		public Orientation getHeaderOrientation(){
			switch( this ){
				case LEFT:
				case RIGHT:
					return Orientation.HORIZONTAL;
				case TOP:
				case BOTTOM:
					return Orientation.VERTICAL;
				default:
					throw new IllegalStateException( "unknown: " + this );
			}
		}
	}
	
	private WizardLayoutManager layoutManager;
	private Side side;
	
	public WizardSplitDockStation( Side side ){
		this.side = side;
		layoutManager = new WizardLayoutManager();
		setSplitLayoutManager( layoutManager );
		setDividerStrategy( new WizardDividerStrategy() );
		setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
	}

	@Override
	public Dimension getPreferredSize(){
		if( layoutManager == null ){
			return super.getPreferredSize();
		}
		else{
			return layoutManager.getPreferredSize();
		}
	}

	@Override
	protected void paintOverlay( Graphics g ){
		PutInfo putInfo = getDropInfo();
		
		if( putInfo != null ) {
			DefaultStationPaintValue paint = getPaint();
			if( putInfo.getNode() == null ) {
				Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());
				paint.drawInsertion(g, bounds, bounds);
			}
			else {
				CombinerTarget target = putInfo.getCombinerTarget();
				
				if( target == null ){
					Rectangle bounds = putInfo.getNode().getBounds();
					int gap = getDividerSize();
					
					if( putInfo.getPut() == PutInfo.Put.LEFT ) {
						bounds.x -= gap;
						bounds.width = gap;
						bounds.x = Math.max( 0, bounds.x );
					}
					else if( putInfo.getPut() == PutInfo.Put.RIGHT ) {
						bounds.x += bounds.width;
						bounds.width = gap;
						bounds.x = Math.min( bounds.x, getWidth()-gap-1 );
					}
					else if( putInfo.getPut() == PutInfo.Put.TOP ) {
						bounds.y -= gap;
						bounds.height = gap;
						bounds.y = Math.max( 0, bounds.y );
					}
					else if( putInfo.getPut() == PutInfo.Put.BOTTOM ) {
						bounds.y += bounds.height;
						bounds.height = gap;
						bounds.y = Math.min( bounds.y, getHeight()-gap-1 );
					}
	
					paint.drawInsertion(g, putInfo.getNode().getBounds(), bounds);
				}
				else{
					Rectangle bounds = putInfo.getNode().getBounds();
					StationPaint stationPaint = paint.get();
					if( stationPaint != null ){
						target.paint( g, getComponent(), stationPaint, bounds, bounds );
					}
				}
			}
		}

		getDividerStrategy().paint( this, g );
	}
	
	/**
	 * This {@link SplitLayoutManager} adds restrictions on how a drag and drop operation
	 * can be performed, and what the boundaries of the children are:
	 * <ul>
	 * 	<li>DnD operations must ensure that the {@link Dockable}s remain in columns, see {@link #ensureDropLocation(PutInfo)}</li>
	 *  <li> </li>
	 * </ul> 
	 * @author Benjamin Sigg
	 */
	private class WizardLayoutManager extends DefaultSplitLayoutManager {
		private WizardColumnModel model;
		
		public WizardLayoutManager(){
			model = new WizardColumnModel( WizardSplitDockStation.this, side );
		}
		
		@Override
		public PutInfo validatePutInfo( SplitDockStation station, PutInfo putInfo ){
			putInfo = ensureDropLocation( putInfo );
			return super.validatePutInfo( station, putInfo );
		}

		/**
		 * Ensures that dropping a {@link Dockable} is only possible such that the
		 * tabular like form of the layout remains intact.
		 * @param putInfo information about the item that is dropped, can be <code>null</code>
		 * @return the validated drop information
		 */
		private PutInfo ensureDropLocation( PutInfo putInfo ){
			if( putInfo != null ) {
				boolean header;
				if( side.getHeaderOrientation() == Orientation.HORIZONTAL ) {
					header = putInfo.getPut() == Put.LEFT || putInfo.getPut() == Put.RIGHT;
				}
				else {
					header = putInfo.getPut() == Put.TOP || putInfo.getPut() == Put.BOTTOM;
				}

				if( header ) {
					SplitNode node = putInfo.getNode();
					while( node != null && !model.isHeaderLevel( node ) ) {
						node = node.getParent();
					}
					putInfo.setNode( node );
				}
			}
			return putInfo;
		}
		
		@Override
		public void updateBounds( Root root, double x, double y, double factorW, double factorH ){
			model.setFactors( factorW, factorH );
			model.updateBounds( x, y );
		}
		
		@Override
		public double validateDivider( SplitDockStation station, double divider, Node node ){
			return model.validateDivider( divider, node );
		}
		
		public Dimension getPreferredSize(){
			return model.getPreferredSize();
		}
		
		public void setDivider( Node node, double divider ){
			model.setDivider( node, divider );
			revalidate();
		}
		
		public boolean isDividerMoveable( Node node ){
			if( node == null ){
				return false;
			}
			return model.isHeaderLevel( node, false );
		}
	}
	
	private class WizardDividerStrategy extends DefaultSplitDividerStrategy{
		@Override
		protected Handler createHandlerFor( SplitDockStation station ){
			return new CustomHandler( station );
		}
		
		private class CustomHandler extends Handler{
			public CustomHandler( SplitDockStation station ){
				super( station );
			}
			
			@Override
			protected Node getDividerNode( int x, int y ){
				Node node = super.getDividerNode( x, y );
				if( layoutManager.isDividerMoveable( node )){
					return node;
				}
				return null;
			}
			
			@Override
			protected void setDivider( Node node, double dividier ){
				layoutManager.setDivider( node, dividier );
			}
		}
	}
}
