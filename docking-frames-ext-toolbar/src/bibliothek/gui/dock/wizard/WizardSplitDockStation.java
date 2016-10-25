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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.DockHierarchyLock.Token;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.gui.dock.station.split.DefaultSplitDividerStrategy;
import bibliothek.gui.dock.station.split.DefaultSplitLayoutManager;
import bibliothek.gui.dock.station.split.Divideable;
import bibliothek.gui.dock.station.split.Leaf;
import bibliothek.gui.dock.station.split.Node;
import bibliothek.gui.dock.station.split.Placeholder;
import bibliothek.gui.dock.station.split.PutInfo;
import bibliothek.gui.dock.station.split.PutInfo.Put;
import bibliothek.gui.dock.station.split.Root;
import bibliothek.gui.dock.station.split.SplitDockPlaceholderProperty;
import bibliothek.gui.dock.station.split.SplitLayoutManager;
import bibliothek.gui.dock.station.split.SplitNode;
import bibliothek.gui.dock.station.split.SplitNodeVisitor;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.themes.DefaultStationPaintValue;
import bibliothek.gui.dock.themes.basic.NoSpanFactory;
import bibliothek.gui.dock.wizard.WizardNodeMap.Column;

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
	private WizardSpanStrategy wizardSpanStrategy;
	private Side side;
	private boolean onRevalidating = false;
	private int sideGap = 3;
	private boolean resizeOnRemove = false;
	private Column columnToResize = null;
	private Dockable dockableCausingResize = null;
	private int maxColumnCount = -1;
	
	/**
	 * Creates a new station.
	 * @param side the side at which this station is presented, e.g. if this station is at the left side
	 * of a {@link JFrame}, the parameter should be {@link Side#LEFT}
	 */
	public WizardSplitDockStation( Side side ){
		this.side = side;
		layoutManager = new WizardLayoutManager();
		wizardSpanStrategy = new WizardSpanStrategy( this );
		setSplitLayoutManager( layoutManager );
		setDividerStrategy( new WizardDividerStrategy() );
		setAllowSideSnap( true );
		
		// disable the standard mechanism for showing spans
		getSpanStrategy().getFactory().setDelegate( new NoSpanFactory() );
		
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
				storeColumnToResize( dockable );
			}
			
			@Override
			public void dockableRemoved( DockStation station, Dockable dockable ){
				resizeStoredColumn();
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
			if( EventQueue.isDispatchThread() ){
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
								if( parent instanceof JComponent ){
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
	}
	
	@Override
	public String getFactoryID(){
		return WizardSplitDockStationFactory.ID;
	}
	
	public WizardLayoutManager getWizardSplitLayoutManager(){
		return layoutManager;
	}
	
	/**
	 * Gets the strategy which is responsible for managing the {@link Span}s.
	 * @return the span strategy
	 */
	public WizardSpanStrategy getWizardSpanStrategy(){
		return wizardSpanStrategy;
	}
	
	@Override
	public void setDividerSize( int dividerSize ){
		super.setDividerSize( dividerSize );
		if( wizardSpanStrategy != null ){
			wizardSpanStrategy.reset();
		}
	}
	
	/**
	 * If a {@link Dockable} is removed from this {@link WizardSplitDockStation}, then the column of the
	 * {@link Dockable} is resized such that it has again its preferred size.
	 * @param resizeOnRemove whether to automatically resize the columns
	 */
	public void setResizeOnRemove( boolean resizeOnRemove ){
		this.resizeOnRemove = resizeOnRemove;
	}
	
	/**
	 * Tells whether the column of a removed {@link Dockable} is automatically resized.
	 * @return whether to automatically resize a column if one of its elements is removed
	 */
	public boolean isResizeOnRemove(){
		return resizeOnRemove;
	}
	
	@Override
	public void setController( DockController controller ){
		wizardSpanStrategy.setController( controller );
		super.setController( controller );
	}
	
	@Override
	protected void setPut( PutInfo info ){
		wizardSpanStrategy.setPut( info );
		if( info != null ){
			storeColumnToResize( info.getDockable() );
		}
	}
	
	@Override
	protected void unsetPut(){
		wizardSpanStrategy.unsetPut();
	}
	
	@Override
	protected boolean dropAside( SplitNode neighbor, Put put, Dockable dockable, Leaf leaf, double divider, Token token ){
		if( super.dropAside( neighbor, put, dockable, leaf, divider, token ) ){
			resizeStoredColumn();
			return true;
		}
		return false;
	}
	
	@Override
	protected boolean dropOver( Leaf leaf, Dockable dockable, DockableProperty property, CombinerSource source, CombinerTarget target ){
		if( super.dropOver( leaf, dockable, property, source, target ) ){
			resizeStoredColumn();
			return true;
		}
		return false;
	}
	
	/**
	 * Checks in which {@link Column} <code>dockable</code> is stored, and remembers that
	 * column for later resizing.
	 * @param dockable the element whose column should be stored
	 */
	private void storeColumnToResize( Dockable dockable ){
		if( resizeOnRemove && dockable.getDockParent() == this ){
			columnToResize = layoutManager.getMap().getColumn( dockable );
			
			// if the column will disappear, do not resize
			if( columnToResize != null && columnToResize.getCellCount() == 1 ){
				columnToResize = null;
			}
			else{
				dockableCausingResize = dockable;
			}
		}
		else{
			columnToResize = null;
		}
	}
	
	/**
	 * Resizes the column stored by {@link #storeColumnToResize(Dockable)}.
	 */
	private void resizeStoredColumn(){
		if( columnToResize != null ){
			WizardNodeMap map = layoutManager.getMap();
			
			Column newColumn = map.getColumn( dockableCausingResize );
			
			PersistentColumn column = null;
			
			if( newColumn == null || newColumn.getIndex() > columnToResize.getIndex() ){
				// item was removed or moved to the right side
				column = map.getPersistentColumn( columnToResize.getIndex() );	
			}
			else if( newColumn.getCellCount() == 1 ){
				// a new column was created
				column = map.getPersistentColumn( columnToResize.getIndex()+1 );
			}
			else if( newColumn.getIndex() != columnToResize.getIndex() ){
				// item was moved to an existing column 
				column = map.getPersistentColumn( columnToResize.getIndex() );
			}
			
			if( column != null ){
				column.setSize( column.getPreferredSize() );
			}
			
			columnToResize = null;
			dockableCausingResize = null;
		}
		revalidateOutside();
	}
	
	/**
	 * Gets the size of the empty space at the moveable side of this station.
	 * @return the empty space at the side
	 */
	public int getSideGap(){
		return sideGap;
	}
	
	/**
	 * Sets an empty space at the moveable side of this station.
	 * @param sideGap the size of the gap, should be at least <code>0</code>
	 */
	public void setSideGap( int sideGap ){
		if( sideGap < 0 ){
			throw new IllegalArgumentException( "sideGap must be at least 0: " + sideGap );
		}
		this.sideGap = sideGap;
		revalidate();
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
			resetToPreferredSizes();
		}
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
	public Dimension getMinimumSize(){
		return getPreferredSize();
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
					int gap = getWizardSpanStrategy().getGap();
					
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
	
	public PersistentColumn[] getPersistentColumns(){
		return layoutManager.model.getPersistentColumns();
	}
	
	public void setPersistentColumns( Dockable[][] columnsAndCells, int[][] cellSizes, int[] columnSizes ){
		layoutManager.model.setPersistentColumns( columnsAndCells, cellSizes, columnSizes );
	}
	
	/**
	 * Gets the maximal amount of columns.
	 * @return the maximal amount of columns, or <code>-1</code>
	 */
	public int getMaxColumnCount() {
		return maxColumnCount;
	}
	
	/**
	 * Sets the maximum amount of columns that the user can create. The station does not
	 * re-organize itself if there are currently more columns than <code>maxColumnCount</code>.<br>
	 * A value of <code>-1</code> can be set, telling the station that there is no maximum.
	 * @param maxColumnCount the maximum amount of columns, <code>-1</code> or any number that is greater than <code>0</code>
	 */
	public void setMaxColumnCount( int maxColumnCount ) {
		if( maxColumnCount == -1 || maxColumnCount >= 1 ){
			this.maxColumnCount = maxColumnCount;
		}
		else{
			throw new IllegalArgumentException( "maxColumnCount is out of bounds" );
		}
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
	 * Changes the size of all columns and cells such that they met their preferred size.
	 */
	public void resetToPreferredSizes(){
		layoutManager.model.resetToPreferredSizes();
	}
	
	/**
	 * This {@link SplitLayoutManager} adds restrictions on how a drag and drop operation
	 * can be performed, and what the boundaries of the children are:
	 * <ul>
	 * 	<li>DnD operations must ensure that the {@link Dockable}s remain in columns, see {@link #ensureDropLocation(PutInfo)}</li>
	 * </ul> 
	 * @author Benjamin Sigg
	 */
	public class WizardLayoutManager extends DefaultSplitLayoutManager {
		private WizardColumnModel model;
		
		public WizardLayoutManager(){
			model = new WizardColumnModel( WizardSplitDockStation.this );
		}
		
		@Override
		public PutInfo validatePutInfo( SplitDockStation station, PutInfo putInfo ){
			putInfo = ensureDropLocation( putInfo );
			if( putInfo != null ){
				return super.validatePutInfo( station, putInfo );
			}
			else{
				return null;
			}
		}
		
		public Leaf[] getLastLeafOfColumns(){
			return model.getLastLeafOfColumns();
		}
		
		@Override
		protected PutInfo calculateSideSnap( SplitDockStation station, int x, int y, Leaf leaf, Dockable drop ){
			WizardSpanStrategy spanStrategy = getWizardSpanStrategy();
			WizardLayoutManager layout = getWizardSplitLayoutManager();
			WizardNodeMap map = layout.getMap();
			Column[] columns = map.getSortedColumns();
			
			int columnCount = map.getColumnCount();
			
			int first = 0;
			int total = 0;
			if( columnCount > 0 ){
				first = spanStrategy.getGap( 0 );
				total = first;
				for( int i = 1; i < columnCount; i++ ){
					total += spanStrategy.getGap( i );
				}
			}
			
			if( side.getHeaderOrientation() == Orientation.HORIZONTAL ){
				for( int i = 0; i < columnCount; i++ ){
					Rectangle bounds = columns[i].getBounds();
					if( x >= bounds.x && x <= bounds.x + bounds.width ){
						if( y < bounds.y ){
							return new PutInfo( columns[i].getRoot(), Put.TOP, drop, false );
						}
						else if( y > bounds.y + bounds.height ){
							return new PutInfo( columns[i].getRoot(), Put.BOTTOM, drop, false );
						}
						else if( x < bounds.x + bounds.width/2 ){
							return new PutInfo( columns[i].getRoot(), Put.LEFT, drop, false );
						}
						else{
							return new PutInfo( columns[i].getRoot(), Put.RIGHT, drop, false );
						}
					}
				}
				
				int width = getWidth() - total;
				x -= first;
				
				
				if( x < width / 2 ){
					return new PutInfo( leftMost( getRoot() ), Put.LEFT, drop, false );
				}
				else{
					return new PutInfo( rightMost( getRoot() ), Put.RIGHT, drop, false );
				}
			}
			else{
				for( int i = 0; i < columnCount; i++ ){
					Rectangle bounds = columns[i].getBounds();
					if( y >= bounds.y && y <= bounds.y + bounds.height ){
						if( x < bounds.x ){
							return new PutInfo( columns[i].getRoot(), Put.LEFT, drop, false );
						}
						else if( x > bounds.x + bounds.width ){
							return new PutInfo( columns[i].getRoot(), Put.RIGHT, drop, false );
						}
						else if( y < bounds.y + bounds.height/2 ){
							return new PutInfo( columns[i].getRoot(), Put.TOP, drop, false );
						}
						else{
							return new PutInfo( columns[i].getRoot(), Put.BOTTOM, drop, false );
						}
					}
				}
				
				int height = getHeight() - total;
				y -= first;
				
				if( y < height / 2 ){
					return new PutInfo( leftMost( getRoot() ), Put.TOP, drop, false );
				}
				else{
					return new PutInfo( rightMost( getRoot() ), Put.BOTTOM, drop, false );
				}
			}
		}
		
		private SplitNode leftMost( SplitNode node ){
			while( true ){
				if( node.getMaxChildrenCount() > 0 ){
					node = node.getChild( 0 );
				}
				else{
					return node;
				}
			}
		}
		
		private SplitNode rightMost( SplitNode node ){
			while( true ){
				int max = node.getMaxChildrenCount();
				if( max > 0 ){
					node = node.getChild( max-1 );
				}
				else{
					return node;
				}
			}
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
					int columnCount = getMap().getColumnCount();
					boolean canHaveNewColumns = maxColumnCount == -1 || columnCount < maxColumnCount;
					if( !canHaveNewColumns ){
						return null;
					}
					
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
		
		public WizardNodeMap getMap(){
			return model.getMap();
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
					if( side == Side.RIGHT && x <= gap ){
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
