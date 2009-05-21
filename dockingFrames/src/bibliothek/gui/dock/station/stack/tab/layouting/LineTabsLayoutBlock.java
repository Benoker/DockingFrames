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
import java.util.Arrays;

import com.sun.java_cup.internal.internal_error;

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
	
	@Override
	public void autoSelectTabs(){
		checkExistence();
		checkSelection();
		checkSpace();
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
	
	/**
	 * Checks whether the current tabs have enough space using their preferred
	 * size and whether additional tabs can be added. This method does never
	 * remove the selected tab (if there is any) nor remove the last
	 * remaining tab (if there is any). This method may create new {@link Tab}s
	 * to check their size.
	 */
	protected void checkSpace(){
		// collect required data
		Tab[] tabs = getTabs();
		Tab selected = getSelectedTab();
		
		int available = availableSpace();
		
		int[] preferred = tabRequirements( tabs );
		int sumPreferred = 0;
		for( int value : preferred )
			sumPreferred += value;
		
		// check whether there is enough space...
		if( sumPreferred > available ){
			// ... there is not
			
			if( selected == null && tabs.length > 0 )
				selected = tabs[0];
			
			// start returning from the right side
			int right = tabs.length-1;
			while( right >= 0 && tabs[right] != selected && sumPreferred >= available ){
				removeTab( right );
				sumPreferred -= preferred[right];
				right--;
			}
			
			// remove elements from the left side
			int left = 0;
			while( left < right && sumPreferred >= available ){
				removeTab( 0 );
				sumPreferred -= preferred[left];
				left--;
			}
		}
		else{
			// ... there is
			ds
		}
	}
	
	private int availableSpace(){
		if( getSide().isHorizontal() )
			return getBounds().width;
		else
			return getBounds().height;
	}
	
	private int[] tabRequirements( Tab[] tabs ){
		int[] preferred = new int[ tabs.length ];
		boolean horizontal = getSide().isHorizontal();
		
		for( int i = 0; i < tabs.length; i++ ){
			Dimension size = tabs[i].getPreferredSize();
			if( horizontal )
				preferred[i] = size.width;
			else
				preferred[i] = size.height;
		}
		
		return preferred;
	}
	
	@Override
	public void doLayout(){
		Tab[] tabs = getTabs();
		Rectangle bounds = getBounds();
		
		AxisConversion conversion = new DefaultAxisConversion( bounds, side );
		bounds = conversion.viewToModel( bounds );
		
		Dimension preferred = getPreferredSize( tabs );
		if( preferred.width <= bounds.width && preferred.height <= bounds.height ){
			doLayoutPreferred( conversion, bounds.width, bounds.height, tabs );
		}
		else{
			Dimension minimum = getMinimumSize( tabs );
			if( minimum.width <= bounds.width && minimum.height <= bounds.height ){
				doLayoutMinimum( conversion, bounds.width, bounds.height, tabs );
			}
			else{
				doLayoutShrinked( conversion, bounds.width, bounds.height, tabs );
			}
		}
	}

	private void doLayoutPreferred( AxisConversion conversion, int width, int height, Tab[] tabs ){
		int x = 0;
		
		for( Tab tab : tabs ){
			Dimension size = conversion.viewToModel( tab.getPreferredSize() );
			
			tab.setBounds( conversion.modelToView( new Rectangle( x, 0, size.width, sameSize ? height : size.height ) ) );
			x += size.width;
		}
	}
	
	private void doLayoutMinimum( AxisConversion conversion, int width, int height, Tab[] tabs ){
		int x = 0;
		
		for( Tab tab : tabs ){
			Dimension size = conversion.viewToModel( tab.getMinimumSize() );
			
			tab.setBounds( conversion.modelToView( new Rectangle( x, 0, size.width, sameSize ? height : size.height ) ) );
			x += size.width;
		}
	}
	
	private void doLayoutShrinked( AxisConversion conversion, int width, int height, Tab[] tabs ){
		int x = 0;
		
		for( Tab tab : tabs ){
			Dimension size = conversion.viewToModel( tab.getMinimumSize() );
			
			tab.setBounds( conversion.modelToView( new Rectangle( x, 0, width / tabs.length, sameSize ? height : Math.min( height, size.height ) ) ) );
			x += width / tabs.length;
		}
	}
	
	@Override
	public Dimension getMinimumSize( Tab[] tabs ){
		int width = 0;
		int height = 0;
		
		if( side == Side.TOP || side == Side.BOTTOM ){
			for( Tab tab : tabs ){
				Dimension size = tab.getMinimumSize();
				width += size.width;
				height = Math.max( height, size.height );
			}
		}
		else{
			for( Tab tab : tabs ){
				Dimension size = tab.getMinimumSize();
				width = Math.max( width, size.width );
				height += size.height;
			}
		}
		
		return new Dimension( width, height );
	}

	@Override
	public Dimension getPreferredSize( Tab[] tabs ){
		int width = 0;
		int height = 0;
		
		if( side == Side.TOP || side == Side.BOTTOM ){
			for( Tab tab : tabs ){
				Dimension size = tab.getPreferredSize();
				width += size.width;
				height = Math.max( height, size.height );
			}
		}
		else{
			for( Tab tab : tabs ){
				Dimension size = tab.getPreferredSize();
				width = Math.max( width, size.width );
				height += size.height;
			}
		}
		
		return new Dimension( width, height );
	}
}
