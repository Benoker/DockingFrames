package bibliothek.gui.dock.wizard;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.split.DefaultSplitDividerStrategy;
import bibliothek.gui.dock.station.split.DefaultSplitLayoutManager;
import bibliothek.gui.dock.station.split.Divideable;
import bibliothek.gui.dock.station.split.Leaf;
import bibliothek.gui.dock.station.split.Node;
import bibliothek.gui.dock.station.split.Placeholder;
import bibliothek.gui.dock.station.split.PutInfo;
import bibliothek.gui.dock.station.split.SplitNodeVisitor;
import bibliothek.gui.dock.station.split.PutInfo.Put;
import bibliothek.gui.dock.station.split.Root;
import bibliothek.gui.dock.station.split.SplitDockPlaceholderProperty;
import bibliothek.gui.dock.station.split.SplitLayoutManager;
import bibliothek.gui.dock.station.split.SplitNode;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;

/**
 * A {@link WizardSplitDockStation} has some additional restrictions and other behavior than an ordinary {@link SplitDockStation}:
 * <ul>
 * 	<li>The {@link Dockable}s are ordered in columns.</li>
 *  <li>The station does not use up empty space if not needed.</li>
 *  <li>Moving a divider changes the preferred size of the station.</li>
 *  <li>This station should be wrapped into a {@link JScrollPane}, it even implements {@link Scrollable} to fully support the {@link JScrollPane}.</li>
 * </ul>
 * @author Benjamin Sigg
 */
public class WizardSplitDockStation extends SplitDockStation implements Scrollable{
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
		
		public Orientation getColumnOrientation(){
			switch( this ){
				case LEFT:
				case RIGHT:
					return Orientation.VERTICAL;
				case TOP:
				case BOTTOM:
					return Orientation.HORIZONTAL;
				default:
					throw new IllegalStateException( "unknown: " + this );
			}			
		}
	}
	
	private WizardLayoutManager layoutManager;
	private Side side;
	private boolean onRevalidating = false;
	
	/**
	 * Creates a new station.
	 * @param side the side at which this station is presented, e.g. if this station is at the left side
	 * of a {@link JFrame}, the parameter should be {@link Side#LEFT}
	 */
	public WizardSplitDockStation( Side side ){
		this.side = side;
		layoutManager = new WizardLayoutManager();
		setSplitLayoutManager( layoutManager );
		setDividerStrategy( new WizardDividerStrategy() );
		setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
		
		addDockStationListener( new DockStationListener(){
			@Override
			public void dockablesRepositioned( DockStation station, Dockable[] dockables ){
				revalidateOutside();
			}
			
			@Override
			public void dockableShowingChanged( DockStation station, Dockable dockable, boolean showing ){
				revalidateOutside();
			}
			
			@Override
			public void dockableSelected( DockStation station, Dockable oldSelection, Dockable newSelection ){
				// ignore
			}
			
			@Override
			public void dockableRemoving( DockStation station, Dockable dockable ){
				// ignore
			}
			
			@Override
			public void dockableRemoved( DockStation station, Dockable dockable ){
				revalidateOutside();
			}
			
			@Override
			public void dockableAdding( DockStation station, Dockable dockable ){
				// ignore
			}
			
			@Override
			public void dockableAdded( DockStation station, Dockable dockable ){
				revalidateOutside();
			}
		} );
	}

	/**
	 * Calls {@link #revalidate()} on the first {@link JComponent} that is outside of the current {@link JScrollPane}. 
	 */
	public void revalidateOutside(){
		if( !onRevalidating ){
			revalidate();
			EventQueue.invokeLater( new Runnable(){
				@Override
				public void run(){
					if( getParent() instanceof JViewport ){
						Container parent = getParent();
						while( parent != null && !(parent instanceof JScrollPane)){
							parent = parent.getParent();
						}
						if( parent != null ){
							parent = parent.getParent();
							if( parent != null && parent instanceof JComponent ){
								((JComponent)parent).revalidate();
							}
						}
					}
					try{
						onRevalidating = true;
						updateBounds();
					}
					finally{
						onRevalidating = false;
					}
				}
			} );
		}
	}
	
	@Override
	public String getFactoryID(){
		return WizardSplitDockStationFactory.ID;
	}
	
	public WizardLayoutManager getWizardSplitLayoutManager(){
		return layoutManager;
	}
	
	/**
	 * Gets the side to which this station leans.
	 * @return the side which does not change its position ever
	 */
	public Side getSide(){
		return side;
	}
	
	/**
	 * Sets the side to which this station leans. If the current side and the new side do not have the same orientation,
	 * then the "columns" of this station are rotated by 90 degrees.
	 * @param side the new side, not <code>null</code>
	 */
	public void setSide( Side side ){
		if( side == null ){
			throw new IllegalArgumentException( "side must not be null" );
		}
		if( this.side != side ){
			boolean rotate = this.side.getHeaderOrientation() != side.getHeaderOrientation();
			this.side = side;
			if( rotate ){
				root().visit( new SplitNodeVisitor(){
					@Override
					public void handleRoot( Root root ){
						// ignore
					}
					
					@Override
					public void handlePlaceholder( Placeholder placeholder ){
						// ignore						
					}
					
					@Override
					public void handleNode( Node node ){
						switch( node.getOrientation() ){
							case HORIZONTAL:
								node.setOrientation( Orientation.VERTICAL );
								break;
							case VERTICAL:
								node.setOrientation( Orientation.HORIZONTAL );
								break;
						}
					}
					
					@Override
					public void handleLeaf( Leaf leaf ){
						// ignore
					}
				} );
			}
			revalidateOutside();
		}
	}
	
	@Override
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}
	
	@Override
	public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ){
		return 10;
	}
	
	@Override
	public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction ){
		return 20;
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize(){
		return getPreferredSize();
	}
	
	@Override
	public boolean getScrollableTracksViewportWidth(){
		if( side == Side.LEFT || side == Side.RIGHT ){
			return true;
		}
		if( getParent() instanceof JViewport ){
			if( getParent().getWidth() > getPreferredSize().width ){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean getScrollableTracksViewportHeight(){
		if( side == Side.TOP || side == Side.BOTTOM ){
			return true;
		}
		if( getParent() instanceof JViewport ){
			if( getParent().getHeight() > getPreferredSize().height ){
				return true;
			}
		}
		return false;
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
				Insets insets = getInsets();
				Rectangle bounds = new Rectangle( insets.left, insets.top, getWidth()-insets.left-insets.right, getHeight()-insets.top-insets.bottom );
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
	
	private Leaf resizeableLeafAt( int x, int y ){
		Leaf[] leafs = layoutManager.getLastLeafOfColumns();
		int gap = getDividerSize();
		
		if( side == Side.RIGHT || side == Side.LEFT ){
			for( Leaf leaf : leafs ){
				Rectangle bounds = leaf.getBounds();
				if( bounds.x <= x && bounds.x + bounds.width >= x ){
					if( bounds.y + bounds.height <= y && bounds.y + bounds.height + gap >= y ){
						return leaf;
					}
				}
			}
		}
		else if( side == Side.BOTTOM || side == Side.TOP ){
			for( Leaf leaf : leafs ){
				Rectangle bounds = leaf.getBounds();
				if( bounds.y <= y && bounds.y + bounds.height >= y ){
					if( bounds.x + bounds.width <= x && bounds.x + bounds.width + gap >= x ){
						return leaf;
					}
				}
			}
		}
		return null;
	}
	
	public WizardColumnModel.PersistentColumn[] getPersistentColumns(){
		return layoutManager.model.getPersistentColumns();
	}
	
	public void setPersistentColumns( Dockable[][] columnsAndCells, int[][] cellSizes, int[] columnSizes ){
		layoutManager.model.setPersistentColumns( columnsAndCells, cellSizes, columnSizes );
	}
	
	/**
	 * Ensures that the dropped {@link Dockable} does not come to rest at a location that would destroy the columns.
	 */
	@Override
	public boolean drop( Dockable dockable, SplitDockPlaceholderProperty property ){
		SplitNode node = getRoot().getPlaceholderNode( property.getPlaceholder() );
		if( node != null && !(node instanceof Leaf) && !(node instanceof Root)){
			if( node instanceof Placeholder || ((Node)node).getOrientation() == side.getHeaderOrientation() ){
				pushIntoHeader( node );
			}
		}
		return super.drop( dockable, property );
	}
	
	private void pushIntoHeader( SplitNode node ){
		while( true ){
			SplitNode parent = node.getParent();
			if( parent == null || layoutManager.model.isHeaderLevel( parent, false )){
				return;
			}
			if( parent instanceof Node ){
				Node n = (Node)parent;
				SplitNode superParent = parent.getParent();
				if( superParent instanceof Root ){
					n.setOrientation( side.getHeaderOrientation() );
					return;
				}
				else if( superParent instanceof Node ){
					Node s = (Node)superParent;
					if( n.getLeft() == node ){
						if( s.getLeft() == n ){
							s.setLeft( node );
							SplitNode old = s.getRight();
							s.setRight( n );
							n.setLeft( old );
						}
						else{
							n.setLeft( s.getLeft() );
							s.setLeft( node );
						}
					}
					else{
						if( s.getRight() == n ){
							s.setRight( node );
							SplitNode old = s.getLeft();
							s.setLeft( n );
							n.setRight( old );
						}
						else{
							n.setRight( s.getRight() );
							s.setRight( node );
						}
					}
					n.setOrientation( side.getColumnOrientation() );
					s.setOrientation( side.getHeaderOrientation() );
				}
				node = node.getParent();
			}
		}
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
	public class WizardLayoutManager extends DefaultSplitLayoutManager {
		private WizardColumnModel model;
		
		public WizardLayoutManager(){
			model = new WizardColumnModel( WizardSplitDockStation.this, side );
		}
		
		@Override
		public PutInfo validatePutInfo( SplitDockStation station, PutInfo putInfo ){
			putInfo = ensureDropLocation( putInfo );
			return super.validatePutInfo( station, putInfo );
		}
		
		public Leaf[] getLastLeafOfColumns(){
			return model.getLastLeafOfColumns();
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
System.out.println( "updateBounds " + root );
			model.setFactors( factorW, factorH );
			model.updateBounds( x, y );
		}
		
		@Override
		public double validateDivider( SplitDockStation station, double divider, Node node ){
			return model.validateDivider( divider, node );
		}
		
		public double validateDivider( double divider, Leaf leaf ){
			return model.validateDivider( divider, leaf );
		}
		
		public double validateColumnDivider( double divider ){
			return model.validateColumnDivider( divider );
		}
		
		public Dimension getPreferredSize(){
			return model.getPreferredSize();
		}
		
		public void setDivider( Divideable node, double divider ){
			model.setDivider( node, divider );
			revalidate();
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
			protected void setDivider( Divideable node, double dividier ){
				layoutManager.setDivider( node, dividier );
			}
			
			@Override
			protected Divideable getDividerNode( int x, int y ){
				Divideable node = super.getDividerNode( x, y );
				if( node == null ){
					int gap = getDividerSize();
					if( side == Side.RIGHT && x < gap ){
						return new ColumnDividier( WizardSplitDockStation.this );
					}
					else if( side == Side.LEFT && x >= getWidth() - gap - 1 ){
						return new ColumnDividier( WizardSplitDockStation.this );
					}
					else if( side == Side.TOP && y >= getHeight() - gap - 1 ){
						return new ColumnDividier( WizardSplitDockStation.this );
					}
					else if( side == Side.BOTTOM && y <= gap ){
						return new ColumnDividier( WizardSplitDockStation.this );
					}
					Leaf leaf = resizeableLeafAt( x, y );
					if( leaf != null ){
						return new CellDivider( WizardSplitDockStation.this, leaf );
					}
				}
				return node;
			}
		}
	}
}
