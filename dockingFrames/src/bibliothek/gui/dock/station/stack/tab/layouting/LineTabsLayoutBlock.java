package bibliothek.gui.dock.station.stack.tab.layouting;
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
import java.awt.Dimension;
import java.awt.Rectangle;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.tab.AxisConversion;
import bibliothek.gui.dock.station.stack.tab.DefaultAxisConversion;
import bibliothek.gui.dock.station.stack.tab.Tab;
import bibliothek.gui.dock.station.stack.tab.TabPane;

/**
 * This {@link TabsLayoutBlock} orders its tabs in a line.
 * @author Benjamin Sigg
 */
public class LineTabsLayoutBlock extends TabsLayoutBlock{
	/**
	 * If set then all tabs will have the same height (if the line is horizontal),
	 * otherwise the preferred/minimum height is used whenever possible 
	 */
	private boolean sameSize = true;
	
	/** At which side to put the tabs if there is enough space */
	private Side side = Side.TOP;
	
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
	
	/**
	 * Gets the alignment of the tabs.
	 * @return the alignment
	 */
	public Side getSide(){
		return side;
	}
	
	/**
	 * Sets the alignment of the tabs
	 * @param side the alignment, not <code>null</code>
	 */
	public void setSide( Side side ){
		if( side == null )
			throw new IllegalArgumentException( "side must not be null" );
		this.side = side;
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
		
		Dimension minimum = new Dimension( 0, 0 );
		Dimension preferred = new Dimension( 0, 0 );
		
		LineSize[] result = new LineSize[ tabs.length+1 ];
		for( int i = 0; i < tabs.length; i++ ){
			Dimension check = tabs[i].getMinimumSize();
			minimum.width += check.width;
			minimum.height = Math.max( minimum.height, check.height );
			
			check = tabs[i].getPreferredSize();
			preferred.width += check.width;
			preferred.height = Math.max( preferred.height, check.height );
			
			Tab[] selection = new Tab[ i+1 ];
			System.arraycopy( tabs, 0, selection, 0, i+1 );
			result[i] = new LineSize( Size.Type.MINIMUM, minimum, selection, i == tabs.length );
		}
		
		result[tabs.length] = new LineSize( Size.Type.PREFERRED, preferred, tabs, true );
		return result;
	}
	
	@Override
	public void doLayout(){
		Tab[] tabs = getTabs();
		Rectangle bounds = getBounds();
		
		AxisConversion conversion = new DefaultAxisConversion( bounds, side );
		bounds = conversion.viewToModel( bounds );
		
		Dimension[] preferreds = new Dimension[ tabs.length ];
		Dimension[] minimums = new Dimension[ tabs.length ];
		
		int sumPreferred = 0;
		int sumMinimum = 0;
		
		for( int i = 0; i < tabs.length; i++ ){
			preferreds[i] = conversion.viewToModel( tabs[i].getPreferredSize() );
			minimums[i] = conversion.viewToModel( tabs[i].getMinimumSize() );
			
			sumPreferred += preferreds[i].width;
			sumMinimum += minimums[i].width;
		}
		
		if( sumPreferred <= bounds.width ){
			doLayoutPreferred( conversion, bounds.width, bounds.height, preferreds, tabs );
		}
		else if( sumMinimum <= bounds.width ){
			doLayoutMinimum( conversion, bounds.width, bounds.height, minimums, preferreds, tabs );
		}
		else{
			doLayoutShrinked( conversion, bounds.width, bounds.height, minimums, tabs );
		}
	}

	private void doLayoutPreferred( AxisConversion conversion, int width, int height, Dimension[] preferreds, Tab[] tabs ){
		int x = 0;
		
		for( int i = 0; i < tabs.length; i++ ){
			Dimension size = conversion.viewToModel( preferreds[i] );
			
			tabs[i].setBounds( conversion.modelToView( new Rectangle( x, 0, size.width, sameSize ? height : Math.min( height, size.height ) ) ) );
			x += size.width;
		}
	}
	
	private void doLayoutMinimum( AxisConversion conversion, int width, int height, Dimension[] minimums, Dimension[] preferreds, Tab[] tabs ){
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
		}
		
		if( tabs.length > 0 ){
			int last = tabs.length-1;
			
			int tabWidth = Math.min( width - x, preferreds[ last ].width );
			tabs[last].setBounds( conversion.modelToView( new Rectangle( x, 0, tabWidth, sameSize ? height : Math.min( height, preferreds[last].height )) ));
		}
	}
	
	private void doLayoutShrinked( AxisConversion conversion, int width, int height, Dimension[] minimums, Tab[] tabs ){
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
		 */
		public LineSize( Type type, Dimension size, Tab[] tabs, boolean allTabs ){
			super( type, size, tabs );
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
}
