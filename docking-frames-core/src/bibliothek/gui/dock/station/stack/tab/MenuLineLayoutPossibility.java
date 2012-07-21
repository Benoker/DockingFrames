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
package bibliothek.gui.dock.station.stack.tab;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.tab.MenuLineLayoutOrder.Item;
import bibliothek.gui.dock.station.stack.tab.layouting.LayoutBlock;
import bibliothek.gui.dock.station.stack.tab.layouting.MenuLayoutBlock;
import bibliothek.gui.dock.station.stack.tab.layouting.Size;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.station.stack.tab.layouting.TabsLayoutBlock;

/**
 * A possibility for a layout of tabs, menus and actions as described by the {@link MenuLineLayout}.
 * @author Benjamin Sigg
 */
public class MenuLineLayoutPossibility {
	private MenuLineLayoutPane pane;
	private Size menuSize;
	private Size infoSize;
	private Size tabSize;
	
	/**
	 * Creates a new layout.
	 * @param pane the {@link TabPane} for which this possible layout is checked
	 * @param tab the size of the tabs, not <code>null</code>
	 * @param menu the size of the menu, may be <code>null</code> to indicate
	 * that the menu is invisible
	 * @param info the size of the info panel, may be <code>null</code> if
	 * there is no info panel to show
	 */
	public MenuLineLayoutPossibility( MenuLineLayoutPane pane, Size tab, Size menu, Size info ){
		this.pane = pane;
		this.menuSize = menu;
		this.tabSize = tab;
		this.infoSize = info;
	}
	
	@Override
	public String toString(){
		return getClass().getSimpleName() + "@[menu=" + menuSize + ", info=" + infoSize + ", tabs=" + tabSize + "]";
	}
	
	/**
	 * Tells how good this layout is. As higher a score, as better a layout is. The layout which shows
	 * all components with their preferred size has a score of <code>1.0</code>, the layout which does not
	 * show anything has a score of <code>0.0</code>.
	 * @return the score of this layout
	 */
	public double getScore(){
		return pane.getLayout().getStrategy().getScore( this, menuSize, infoSize, tabSize );
	}
	
	/**
	 * Tells whether this layout shows all items with their preferred size.
	 * @return whether this layout is a preferred layout
	 */
	public boolean isPreferred(){
		if( tabSize == null || !tabSize.isPreferred() || !getPane().getTabs().isAllTabs( tabSize ) )
			return false;
		
		if( menuSize != null )
			return false;
		
		return true;
	}
	
	/**
	 * Gets the representation of the {@link TabPane} for which which possible layout 
	 * is evaluated. 
	 * @return the pane, not <code>null</code>
	 */
	public MenuLineLayoutPane getPane(){
		return pane;
	}
	
	/**
	 * Gets the size this layout requires, this {@link Dimension} does not include the preferred or minimal size
	 * required for the {@link #getPane() tab pane}.
	 * @return the size
	 */
	public Dimension getSize(){
		int width = tabSize.getWidth();
		int height = tabSize.getHeight();
		
		if( getPane().getPane().getDockTabPlacement().isHorizontal() ){
			if( menuSize != null ){
				width += menuSize.getWidth();
				height = Math.max( height, menuSize.getHeight() );
			}
			if( infoSize != null ){
				width += infoSize.getWidth();
				height = Math.max( height, infoSize.getHeight() );
			}
		}
		else{
			if( menuSize != null ){
				width = Math.max( width, menuSize.getWidth() );
				height += menuSize.getHeight();
			}
			if( infoSize != null ){
				width = Math.max( width, infoSize.getWidth() );
				height += infoSize.getHeight();
			}
		}
		
		return new Dimension( width, height );
	}
	
	/**
	 * Applies the sizes specified in this layout.
	 */
	public void apply(){
		MenuLineLayoutPane layout = getPane();
		TabPane pane = layout.getPane();
		MenuLineLayoutOrder order = layout.getLayout().getFactory().createOrder( layout.getLayout(), pane );
		AxisConversion conversion = layout.getLayout().getConversion( pane );
		TabPlacement orientation = pane.getDockTabPlacement();
		
		// update visibility and layout
		MenuLayoutBlock menu = layout.getMenu();
		LayoutBlock info = layout.getInfo();
		TabsLayoutBlock tabs = layout.getTabs();
		
		tabs.setLayout( tabSize );
		if( infoSize != null && info != null )
			info.setLayout( infoSize );
		if( menuSize == null ){
			menu.getMenu().setPaneVisible( false );
		}
		else{
			menu.getMenu().setPaneVisible( true );
			menu.setLayout( menuSize );
		}
		
		// update content of tabs
		if( menuSize != null ){
			Set<Dockable> dockables = new HashSet<Dockable>();
			for( Dockable dockable : pane.getDockables() ){
				dockables.add( dockable );
			}
			for( Tab tab : tabs.getTabs( tabSize ) ){
				dockables.remove( tab.getDockable() );
			}
			TabMenu tabMenu = menu.getMenu();
			for( Dockable dockable : dockables ){
				pane.putInMenu( tabMenu, dockable );
			}
		}
		
		// update boundaries
		Rectangle available = conversion.viewToModel( pane.getAvailableArea() );
		
		// ... determine how much space is needed for menus etc
		int required;
		if( orientation.isHorizontal() ){
			required = tabSize.getHeight();
			if( infoSize != null )
				required = Math.max( required, infoSize.getHeight() );
			if( menuSize != null )
				required = Math.max( required, menuSize.getHeight() );
		}
		else{
			required = tabSize.getWidth();
			if( infoSize != null )
				required = Math.max( required, infoSize.getWidth() );
			if( menuSize != null )
				required = Math.max( required, menuSize.getWidth() );
		}

		required = Math.max( 0, Math.min( required, available.height/2 ) );
		
		pane.setSelectedBounds( conversion.modelToView( new Rectangle( available.x, available.y + required, available.width, available.height - required ) ) );
		
		// distribute empty space
		int[] widths = calculateWidths( orientation, order, available.width, tabSize, menuSize, infoSize );
		int x = available.x;
		for( MenuLineLayoutOrder.Item item : order ){
			int availableWidth = 0;
			Size size = null;
			
			switch( item ){
				case TABS:
					availableWidth = widths[0];
					size = tabSize;
					break;
				case MENU:
					availableWidth = widths[1];
					size = menuSize;
					break;
				case INFO:
					availableWidth = widths[2];
					if( info != null ){
						size = infoSize;
					}
					break;
			}
			
			if( size != null ){
				int itemWidth = calculateWidth( order, item, size, availableWidth, orientation );
				int deltaX = calculateDeltaX( order, item, itemWidth, availableWidth );
				
				switch( item ){
					case INFO:
						int reqDelta;
						
						if( orientation.isHorizontal() ){
							reqDelta = Math.max( 0, required - infoSize.getHeight() );
						}
						else{
							reqDelta = Math.max( 0, required - infoSize.getWidth() );
						}
						
						Rectangle infoBounds = new Rectangle( x + deltaX, available.y+reqDelta/2, itemWidth, required-reqDelta );
						x += availableWidth;
						infoBounds = conversion.modelToView( infoBounds );
						info.setBounds( infoBounds.x, infoBounds.y, infoBounds.width, infoBounds.height );
						break;
					case MENU:
						Rectangle menuBounds = new Rectangle( x + deltaX, available.y, itemWidth, required );
						x += availableWidth;
						menuBounds = conversion.modelToView( menuBounds );
						menu.setBounds( menuBounds.x, menuBounds.y, menuBounds.width, menuBounds.height );						
						break;
					case TABS:
						Rectangle tabBounds = new Rectangle( x + deltaX, available.y, itemWidth, required );
						tabBounds = conversion.modelToView( tabBounds );
						x += availableWidth;
						tabs.setBounds( tabBounds.x, tabBounds.y, tabBounds.width, tabBounds.height );
						break;
				}
			}
		}
	}
	
	private int calculateWidth( MenuLineLayoutOrder order, Item item, Size size, int width, TabPlacement orientation ){
		int expected;
		
		if( orientation.isHorizontal() ){
			expected = size.getWidth();
		}
		else{
			expected = size.getHeight();
		}
		
		int overflow = width - expected;
		
		if( overflow <= 0 ){
			return width;
		}
		return expected + (int)(order.getFill( item ) * overflow);
	}
	
	private int calculateDeltaX( MenuLineLayoutOrder order, Item item, int itemWidth, int availableWidth ){
		int space = availableWidth - itemWidth;
		if( space <= 0 ){
			return 0;
		}
		return (int)(space * order.getAlignment( item ));
	}
	
	/**
	 * Calculates how much space each item receives.
	 * @param orientation the orientation of the layout
	 * @param order constraints of the items
	 * @param available the total available space
	 * @param tabSize the size required for the tabs
	 * @param menuSize the size required for the menu, can be <code>null</code>
	 * @param infoSize the size required for the info component, can be <code>null</code>
	 * @return the widths, where 0 is tabs, 1 is menus and 2 is info components. The sum of the widths equal to <code>available</code>
	 */
	private int[] calculateWidths( TabPlacement orientation, MenuLineLayoutOrder order, int available, Size tabSize, Size menuSize, Size infoSize ){
		int[] result = new int[3];
		
		// ... check whether there is enough space for all items
		int space, tabSpace, menuSpace = 0, infoSpace = 0;
		
		float weightTabs = order.getWeight( Item.TABS );
		float weightInfo = 0;
		float weightMenu = 0;
		float totalWeight = weightTabs;
		
		if( orientation.isHorizontal() ){
			tabSpace = tabSize.getWidth();
			space = tabSpace;

			if( infoSize != null ){
				infoSpace = infoSize.getWidth();
				weightInfo = order.getWeight( Item.INFO );
				space += infoSpace;
				totalWeight += weightInfo;
			}
			if( menuSize != null ){
				menuSpace = menuSize.getWidth();
				space += menuSpace;
				weightMenu = order.getWeight( Item.MENU );
				totalWeight += weightMenu;
			}
		}
		else{
			tabSpace = tabSize.getHeight();
			space = tabSpace;
			
			if( infoSize != null ){
				infoSpace = infoSize.getHeight();
				space += infoSpace;
				weightInfo = order.getWeight( Item.INFO );
				totalWeight += weightInfo;
			}
			if( menuSize != null ){
				menuSpace = menuSize.getHeight();
				space += menuSpace;
				weightMenu = order.getWeight( Item.MENU );
				totalWeight += weightMenu;
			}			
		}
		
		int overflowSpace = available - space;
		
		float tabsWeight = totalWeight == 0 ? 0 : (weightTabs / totalWeight);
		float menuWeight = totalWeight == 0 ? 0 : (weightMenu / totalWeight);
		float infoWeight = totalWeight == 0 ? 0 : (weightInfo / totalWeight);
		
		int visibleItems = 1;
		if( overflowSpace >= 0 ){
			
			result[0] = (int)(tabSpace + tabsWeight * overflowSpace);
			if( menuSize != null ){
				result[1] = (int)(menuSpace + menuWeight * overflowSpace);
				visibleItems++;
			}
			if( infoSize != null ){
				result[2] = (int)(infoSpace + infoWeight * overflowSpace);
				visibleItems++;
			}
		}
		else{
			float[] maximums = { tabSpace, menuSpace, infoSpace };
			float[] claims = { available * weightTabs, available * weightMenu, available * weightInfo }; 
			
			if( menuSize == null ){
				claims[1] = -1;
			}
			if( infoSize == null ){
				claims[2] = -1;
			}
			
			for( int i = 0; i < available; i++ ){
				float maxClaim = -1;
				int maxIndex = -1;
				for( int c = 0; c < 3; c++ ){
					if( claims[c] > maxClaim ){
						maxClaim = claims[c];
						maxIndex = c;
					}
				}
				if( maxIndex >= 0 ){
					claims[maxIndex] -= 1;
					if( claims[maxIndex] < 0 ){
						claims[maxIndex] = -0.5f;
					}
					result[maxIndex] += 1;
					if( result[maxIndex] >= maximums[maxIndex] ){
						claims[maxIndex] = -1;
					}
				}
			}
			
			if( menuSize != null ){
				visibleItems++;
			}
			if( infoSize != null ){
				visibleItems++;
			}
		}
		
		// make sure sum equals available
		int sum = 0;
		for( int r : result ){
			sum += r;
		}
		int delta = available - sum;
		if( delta != 0 ){
			// distribute equally, if there is a pixel too much the tabs gets them
			result[0] += delta / visibleItems;
			if( menuSize != null ){
				result[1] += delta / visibleItems;
			}
			if( infoSize != null ){
				result[2] += delta/ visibleItems;
			}
			
			delta -= visibleItems * (delta / visibleItems);
			result[0] += delta;
		}
		
		return result;
	}
}
