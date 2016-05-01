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
package bibliothek.gui.dock.station.stack.tab.layouting;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.tab.AxisConversion;
import bibliothek.gui.dock.station.stack.tab.DefaultAxisConversion;
import bibliothek.gui.dock.station.stack.tab.Tab;
import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.station.stack.tab.TabPaneComponent;

/**
 * This {@link TabsLayoutBlock} orders its tabs in a line.
 * @author Benjamin Sigg
 */
public class LineTabsLayoutBlock extends AbstractTabsLayoutBlock{
	/**
	 * If set then all tabs will have the same height (if the line is horizontal),
	 * otherwise the preferred/minimum height is used whenever possible 
	 */
	private boolean sameSize = true;
	
	/**
	 * If set, then all tabs have the same height (width) if laid out
	 * horizontal (vertical).
	 * @return <code>true</code> if all tabs have the same size
	 */
	public boolean isSameSize(){
		return sameSize;
	}
	
	/**
	 * If set, then all tabs have the same height (width) if laid out
	 * horizontal (vertical).
	 * @param sameSize whether all tabs should have the same height.
	 */
	public void setSameSize( boolean sameSize ){
		this.sameSize = sameSize;
	}
	
	public boolean isAllTabs( Size size ){
		return ((LineSize)size).isAllTabs();
	}
	
	public Tab[] getTabs( Size size ){
		return ((LineSize)size).getTabs();
	}
	
	public int getTabsCount( Size size ){
		return ((LineSize)size).getTabCount();
	}
	
	/**
	 * Creates the tab that should be selected and adds it at an appropriate
	 * location. Note that the additional tab might lead to a situation where
	 * there is not enough space. If a selected tab already exists then this
	 * method does nothing.
	 */
	protected void checkSelection(){
		if( getSelectedTab() != null )
			return;
		
		TabPane pane = getPane();
		if( pane == null )
			return;
		
		Dockable selection = pane.getSelectedDockable();
		if( selection == null )
			return;
		
		insertTab( pane.putOnTab( selection ));
	}

	public LineSize[] getSizes(){
		Tab[] tabs = getTabsOrderedByImportance();
		SizeCollector collector = new SizeCollector( getPane().getDockables() );
		
		LineSize[] result = new LineSize[ tabs.length+1 ];
		for( int i = 0; i < tabs.length; i++ ){
			collector.insert( tabs[i] );
			
			Dimension size = collector.getMinimumSize();
			Tab[] selection = new Tab[ i+1 ];
			System.arraycopy( tabs, 0, selection, 0, i+1 );
			
			result[i] = new LineSize( Size.Type.MINIMUM, size, selection, i+1 == tabs.length, i / (double)tabs.length );
		}
		
		Dimension preferred = collector.getPreferredSize();
		result[tabs.length] = new LineSize( Size.Type.PREFERRED, preferred, tabs, true, 1.0 );
		return result;
	}

	public int getIndexOfTabAt( Point mouseLocation ){
		Tab[] tabs = getCurrentTabs();
		
		int[] overlapPrevious = getOverlapToPrevious( tabs );
		int[] overlapNext = getOverlapToNext( tabs );
		
		int backup = -1;
		boolean horizontal = getOrientation().isHorizontal();
		
		for( int i = 0; i < tabs.length; i++ ){
			Rectangle bounds = tabs[i].getBounds();
			if( bounds.contains( mouseLocation )){
				boolean exact;
				
				if( horizontal ){
					exact = 
							(mouseLocation.x >= bounds.x + overlapPrevious[i]) &&
							(mouseLocation.x < bounds.x + bounds.width - overlapNext[i]);
				}
				else {
					exact = 
							(mouseLocation.y >= bounds.y + overlapPrevious[i]) &&
							(mouseLocation.y < bounds.y + bounds.height - overlapNext[i]);					
				}
				
				if( exact ){
					return i;
				} 
				else{
					backup = i;
				}
			}
		}
		
		return backup;
	}
	
	/**
	 * Gets all the tabs that are currently shown, ordered by <code>z</code>.
	 * @return the currently shown tabs
	 */
	protected Tab[] getCurrentTabs(){
		TabPane pane = getPane();
		Tab[] tabs = pane.getTabs();
		Arrays.sort( tabs, new Comparator<Tab>(){
			public int compare( Tab a, Tab b ){
				return a.getZOrder() - b.getZOrder();
			}
		});
		return tabs;
	}
	
	@Override
	public void doLayout(){
		Tab[] tabs = getTabs();
		Rectangle bounds = getBounds();
		
		AxisConversion conversion = new DefaultAxisConversion( bounds, getOrientation() );
		bounds = conversion.viewToModel( bounds );
		
		Dimension[] preferreds = new Dimension[ tabs.length ];
		Dimension[] minimums = new Dimension[ tabs.length ];
		
		int sumPreferred = 0;
		int sumMinimum = 0;
		
		int[] overlapPrevious = getOverlapToPrevious( tabs );
		int[] overlapNext = getOverlapToNext( tabs );
		
		for( int i = 0; i < tabs.length; i++ ){
			preferreds[i] = conversion.viewToModel( tabs[i].getPreferredSize() );
			minimums[i] = conversion.viewToModel( tabs[i].getMinimumSize() );
			
			sumPreferred += preferreds[i].width;
			sumMinimum += minimums[i].width;
			
			if( i > 0 ){
				int delta = Math.max( overlapPrevious[i], overlapNext[i-1] );
				sumPreferred -= delta;
				sumMinimum -= delta;
			}
		}
		
		ZOrder zorder = new ZOrder( tabs );
		if( sumPreferred <= bounds.width ){
			doLayoutPreferred( conversion, bounds.width, bounds.height, preferreds, tabs, zorder, overlapPrevious, overlapNext );
		}
		else if( sumMinimum <= bounds.width ){
			doLayoutMinimum( conversion, bounds.width, bounds.height, minimums, preferreds, tabs, zorder, overlapPrevious, overlapNext );
		}
		else{
			doLayoutShrinked( conversion, bounds.width, bounds.height, minimums, tabs, zorder, overlapPrevious, overlapNext );
		}
		
		int z = 0;
		Tab[] zOrdered = zorder.getOrderedByZ();
		for( Tab tab : zOrdered ){
			if( tab != null ){
				tab.setZOrder( z++ );
			}
		}
	}

	/**
	 * Creates an array telling for each tab how much it may be overlapped by 
	 * its previous tab.
	 * @param tabs the set of tabs
	 * @return the overlap
	 */
	private int[] getOverlapToPrevious( Tab[] tabs ){
		boolean horizontal = getOrientation().isHorizontal();
		
		int[] result = new int[ tabs.length ];
		for( int i = 1; i < tabs.length; i++ ){
			Insets overlap = tabs[i].getOverlap( tabs[i-1] );
			if( horizontal )
				result[i] = overlap.left;
			else
				result[i] = overlap.top;
		}
		return result;
	}
	
	/**
	 * Creates an array telling for each tab how much it may be overlapped by 
	 * its next tab.
	 * @param tabs the set of tabs
	 * @return the overlap
	 */
	private int[] getOverlapToNext( Tab[] tabs ){
		boolean horizontal = getOrientation().isHorizontal();
		int[] result = new int[ tabs.length ];
		for( int i = tabs.length-2; i >= 0; i-- ){
			Insets overlap = tabs[i].getOverlap( tabs[i+1] );
			if( horizontal )
				result[i] = overlap.right;
			else
				result[i] = overlap.bottom;
		}
		return result;
	}
	
	private void doLayoutPreferred( AxisConversion conversion, int width, int height, Dimension[] preferreds, Tab[] tabs, ZOrder order, int[] overlapPrevious, int[] overlapNext ){
		int x = 0;
		
		for( int i = 0; i < tabs.length; i++ ){
			Dimension size = preferreds[i];
			
			tabs[i].setBounds( conversion.modelToView( new Rectangle( x, 0, size.width, sameSize ? height : Math.min( height, size.height ) ) ) );
			x += size.width;
			
			if( i+1 < tabs.length ){
				if( overlapNext[i] > overlapPrevious[i+1] ){
					x -= overlapNext[i];
					order.putOrder( tabs[i+1], tabs[i] );
				}
				else{
					x -= overlapPrevious[i+1];
					order.putOrder( tabs[i], tabs[i+1] );
				}
			}
		}
	}
	
	private void doLayoutMinimum( AxisConversion conversion, int width, int height, Dimension[] minimums, Dimension[] preferreds, Tab[] tabs, ZOrder order, int[] overlapPrevious, int[] overlapNext ){
		int x = 0;
		
		int sumMinimum = 0;
		int sumPreferred = 0;
				
		for( Dimension minimum : minimums )
			sumMinimum += minimum.width;
		for( Dimension preferred : preferreds )
			sumPreferred += preferred.width;
		
		double passage = (width - sumMinimum) / (double)(sumPreferred - sumMinimum);
		
		for( int i = 0; i < tabs.length-1; i++ ){
			int tabWidth = (int)(minimums[i].width + passage * (preferreds[i].width - minimums[i].width ));
			
			tabs[i].setBounds( conversion.modelToView( new Rectangle( x, 0, tabWidth, sameSize ? height : Math.min( height, preferreds[i].height )) ));
			
			x += tabWidth;
			if( overlapNext[i] > overlapPrevious[i+1] ){
				x -= overlapNext[i];
				order.putOrder( tabs[i+1], tabs[i] );
			}
			else{
				x -= overlapPrevious[i+1];
				order.putOrder( tabs[i], tabs[i+1] );
			}
		}
		
		if( tabs.length > 0 ){
			int last = tabs.length-1;
			
			int tabWidth = Math.min( width - x, preferreds[ last ].width );
			tabs[last].setBounds( conversion.modelToView( new Rectangle( x, 0, tabWidth, sameSize ? height : Math.min( height, preferreds[last].height )) ));
		}
	}
	
	private void doLayoutShrinked( AxisConversion conversion, int width, int height, Dimension[] minimums, Tab[] tabs, ZOrder order, int[] overlapPrevious, int[] overlapNext ){
		int x = 0;
		
		int sum = 0;		
		for( Dimension minimum : minimums ){
			sum += minimum.width;
		}
		
		double factor = sum / (double)width;
		
		for( int i = 0; i < tabs.length-1; i++ ){
			int tabWidth = (int)(factor * minimums[i].width);
			tabs[i].setBounds( conversion.modelToView( new Rectangle( x, 0, tabWidth, sameSize ? height : Math.min( height, minimums[i].height ) ) ) );
			x += tabWidth;
			
			if( overlapNext[i] > overlapPrevious[i+1] ){
				x -= overlapNext[i];
				order.putOrder( tabs[i+1], tabs[i] );
			}
			else{
				x -= overlapPrevious[i+1];
				order.putOrder( tabs[i], tabs[i+1] );
			}
		}
		
		int last = tabs.length-1;
		if( last >= 0 ){
			tabs[last].setBounds( conversion.modelToView( new Rectangle( x, 0, width - x, sameSize ? height : Math.min( height, minimums[last].height ) ) ) );
		}
	}
	
	/**
	 * {@link Size} information about a line on a {@link LineTabsLayoutBlock}.
	 * @author Benjamin Sigg
	 */
	public class LineSize extends TabsSize{
		private boolean allTabs;
		
		/**
		 * Creates a new size
		 * @param type the kind of size this is
		 * @param size the amount of needed pixels
		 * @param tabs the tabs shown with this size
		 * @param allTabs whether <code>tabs</code> includes all available tabs
		 * @param score how well this size is liked
		 */
		public LineSize( Type type, Dimension size, Tab[] tabs, boolean allTabs, double score ){
			super( type, size, tabs, score );
			this.allTabs = allTabs;
		}

		/**
		 * Tells whether this size represents a size where all tabs are shown.
		 * @return <code>true</code> if all tabs are shown
		 */
		public boolean isAllTabs(){
			return allTabs;
		}
	}
	
	/**
	 * Calculates the {@link TabPaneComponent#setZOrder(int) z-order} of various
	 * components requiring only a subset of all comparisons 
	 */
	protected class ZOrder{
		private Tab[] tabs;
		
		private List<Integer>[] onTop;
		private List<Integer>[] onBottom;
		
		/**
		 * Creates a new {@link ZOrder}
		 * @param tabs the tabs whose z-order needs to be calculated.
		 */
		@SuppressWarnings("unchecked")
		public ZOrder( Tab[] tabs ){
			this.tabs = tabs;
			
			onTop = new List[ tabs.length ];
			onBottom = new List[ tabs.length ];
			
			for( int i = 0; i < tabs.length; i++ ){
				onTop[i] = new ArrayList<Integer>( 5 );
				onBottom[i] = new ArrayList<Integer>( 5 );
			}
		}
		
		/**
		 * Sets <code>front</code> in front of <code>back</code>. The behavior
		 * is undefined if previous calls already established that
		 * <code>back</code> must be in front of <code>front</code>.
		 * @param front the front tab
		 * @param back the back tab
		 */
		public void putOrder( Tab front, Tab back ){
			for( int f = 0; f < tabs.length; f++ ){
				if( tabs[f] == front ){
					for( int b = 0; b < tabs.length; b++ ){
						if( tabs[b] == back ){
							onTop[b].add( f );
							onBottom[f].add( b );
							return;
						}
					}
				}
			}
		}
		
		/**
		 * Calculates the z-orders for the tabs known to this {@link ZOrder}.
		 * @return the z order.
		 */
		public int[] getZOrders(){
			int[] results = new int[ tabs.length ];
			boolean[] handled = new boolean[ results.length ];
			
			for( int i = 0; i < results.length; i++ ){
				for( int j = 0; j < results.length; j++ ){
					if( !handled[j] ){
						if( onTop[j].isEmpty() ){
							results[j] = results.length-i;
							handled[j] = true;
							Integer index = i;
							
							for( int bottom : onBottom[j] ){
								onTop[bottom].remove( index );
							}
						}
					}
				}
			}
			return results;
		}
		
		/**
		 * Returns the tabs ordered by their z-order. The first tab should
		 * be the one in front of all other tabs.
		 * @return the tabs ordered by z
		 */
		public Tab[] getOrderedByZ(){
			Tab[] results = new Tab[ tabs.length ];
			boolean[] handled = new boolean[ results.length ];
			
			base:for( int i = 0; i < results.length; i++ ){
				for( int j = 0; j < results.length; j++ ){
					if( !handled[j] ){
						if( onTop[j].isEmpty() ){
							results[i] = tabs[j];
							handled[j] = true;
							Integer index = i;
							
							for( int bottom : onBottom[j] ){
								onTop[bottom].remove( index );
							}
							continue base;
						}
					}
				}
			}
			return results;
		}
	}
	
	/**
	 * Used to calculate the minimum and preferred size of a set of 
	 * {@link Tab}s.
	 * @author Benjamin Sigg
	 */
	protected class SizeCollector{
		private Dockable[] dockables;
		private Tab[] tabs;
		
		private Dimension[] minimum;
		private Dimension[] preferred;
		private int[] overlapPrevious;
		private int[] overlapNext;
		
		/**
		 * Creates a new collector
		 * @param dockables underlying set of {@link Dockable}s.
		 */
		public SizeCollector( Dockable[] dockables ){
			this.dockables = dockables;
			int size = dockables.length;
			
			tabs = new Tab[ size ];
			minimum = new Dimension[ size ];
			preferred = new Dimension[ size ];
			overlapPrevious = new int[ size ];
			overlapNext = new int[ size ];
		}
		
		/**
		 * Adds a new tab to this collector, all sizes of this collector
		 * change because of this action.
		 * @param tab the new tab
		 */
		public void insert( Tab tab ){
			Dockable dockable = tab.getDockable();
			for( int i = 0; i < dockables.length; i++ ){
				if( dockables[i] == dockable ){
					insert( tab, i );
					return;
				}
			}
		}
		
		/**
		 * Gets all the tabs that are currently not <code>null</code>
		 * @return the visible tabs
		 */
		private Tab[] getVisibleTabs(){
			int count = 0;
			for( Tab tab : tabs ){
				if( tab != null ){
					count++;
				}
			}
			Tab[] visible = new Tab[count];
			int index = 0;
			for( Tab tab : tabs ){
				if( tab != null ){
					visible[ index++ ] = tab;
				}
			}
			return visible;
		}
		
		private void insert( Tab tab, int index ){
			tabs[ index ] = tab;
			Tab[] visibleTabs = getVisibleTabs();
			
			for( int i = 0; i < tabs.length; i++ ){
				if( tabs[i] != null ){
					minimum[ i ] = tabs[i].getMinimumSize( visibleTabs );
					preferred[ i ] = tabs[i].getPreferredSize( visibleTabs );
				}
			}
			
			boolean horizontal = getOrientation().isHorizontal();
			
			// search previous
			for( int i = index-1; i >= 0; i-- ){
				if( tabs[i] != null ){
					if( horizontal ){
						overlapNext[i] = tabs[i].getOverlap( tab ).right;
						overlapPrevious[ index ] = tab.getOverlap( tabs[i] ).left;
					}
					else{
						overlapNext[i] = tabs[i].getOverlap( tab ).bottom;
						overlapPrevious[ index ] = tab.getOverlap( tabs[i] ).top;
					}
					break;
				}
			}
			
			// search next
			for( int i = index+1; i < tabs.length; i++ ){
				if( tabs[i] != null ){
					if( horizontal ){
						overlapNext[ index ] = tab.getOverlap( tabs[i] ).right;
						overlapPrevious[i] = tabs[i].getOverlap( tab ).left;
					}
					else{
						overlapNext[ index ] = tab.getOverlap( tabs[i] ).bottom;
						overlapPrevious[i] = tabs[i].getOverlap( tab ).top;
					}
					break;
				}
			}
		}
		
		/**
		 * Gets the current minimum size.
		 * @return minimum size
		 */
		public Dimension getMinimumSize(){
			return getSize( minimum );
		}
		
		/**
		 * Gets the current preferred size.
		 * @return preferred size
		 */
		public Dimension getPreferredSize(){
			return getSize( preferred );
		}
		
		private Dimension getSize( Dimension[] required ){
			int width = 0;
			int height = 0;
			
			int previous = -1;
			
			if( getOrientation().isHorizontal() ){
				for( int i = 0; i < tabs.length; i++ ){
					if( tabs[i] != null ){
						Dimension size = required[i];
						
						height = Math.max( height, size.height );
						
						width += size.width;
						
						if( previous != -1 ){
							width -= Math.max( overlapNext[ previous ], overlapPrevious[ i ] );
						}
						
						previous = i;
					}
				}
			}
			else{
				for( int i = 0; i < tabs.length; i++ ){
					if( tabs[i] != null ){
						Dimension size = required[i];
						
						width = Math.max( width, size.width );
						
						height += size.height;
						
						if( previous != -1 ){
							height -= Math.max( overlapNext[ previous ], overlapPrevious[ i ] );
						}
						
						previous = i;
					}
				}
			}
				
			return new Dimension( width, height );			
		}
	}
}
