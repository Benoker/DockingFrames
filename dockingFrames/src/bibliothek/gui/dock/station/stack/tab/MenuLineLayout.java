/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.tab.layouting.LayoutBlock;
import bibliothek.gui.dock.station.stack.tab.layouting.LineTabsLayoutBlock;
import bibliothek.gui.dock.station.stack.tab.layouting.MenuLayoutBlock;
import bibliothek.gui.dock.station.stack.tab.layouting.Size;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.station.stack.tab.layouting.LineTabsLayoutBlock.LineSize;


/**
 * Orders tabs in a line, if there is not enough space a menu is used. Also
 * ensures the info-panel has its preferred size.
 * @author Benjamin Sigg
 */
public class MenuLineLayout extends AbstractTabLayoutManager<MenuLineLayout.Layout>{
	@Override
	protected Layout createInfoFor( TabPane pane ){
		return new Layout( pane );
	}

	@Override
	protected void destroy( Layout info ){
		info.destroy();
	}

	public Dimension getMinimumSize( TabPane pane ){
		Layout layout = getInfo( pane );
		if( layout == null )
			throw new IllegalArgumentException( "unknown pane" );
		return layout.getMinimumSize(); 
	}

	public Dimension getPreferredSize( TabPane pane ){
		Layout layout = getInfo( pane );
		if( layout == null )
			throw new IllegalArgumentException( "unknown pane" );
		return layout.getPreferredSize();
	}

	public void layout( TabPane pane ){
		Layout layout = getInfo( pane );
		if( layout == null )
			throw new IllegalArgumentException( "unknown pane" );
		layout.layout();
	}
	
	/**
	 * Collects all the {@link Size}s whose type is <code>type</code>.
	 * @param block the source of the size, may be <code>null</code>
	 * @param type the type to search, not <code>null</code>
	 * @return an array containing sizes, may have length 0, never <code>null</code>
	 */
	protected Size[] getSizes( LayoutBlock block, Size.Type type ){
		if( block == null )
			return new Size[]{};
		
		Size[] sizes = block.getSizes();
		if( sizes == null )
			return new Size[]{};
		
		return getSizes( sizes, type );
	}
	
	/**
	 * Makes a selection of those {@link Size}s with <code>type</code>.
	 * @param choices available sizes
	 * @param type the type searched
	 * @return sizes fitting <code>type</code>
	 */
	protected Size[] getSizes( Size[] choices, Size.Type type ){
		int count = 0;
		for( Size size : choices ){
			if( size.getType() == type ){
				count++;
			}
		}
		
		Size[] result = new Size[ count ];
		int index = 0;
		for( Size size : choices ){
			if( size.getType() == type ){
				result[ index++ ] = size;
			}
		}
		return result;
	}
	
	/**
	 * Creates a new {@link AxisConversion} to convert a layout that
	 * is at the top of dockables to a layout at the {@link TabPlacement}
	 * given by <code>pane</code>.
	 * @param pane the panel for which the conversion is used
	 * @return the new conversion
	 */
	protected AxisConversion getConversion( TabPane pane ){
		return new DefaultAxisConversion( pane.getAvailableArea(), pane.getTabPlacement() );
	}
	
	/**
	 * Layout information for a {@link TabPane}.
	 * @author Benjamin Sigg
	 */
	protected class Layout extends AbstractTabLayoutManager.PaneInfo{
		private MenuLayoutBlock menu;
		private LayoutBlock info;
		private LineTabsLayoutBlock tabs;
		
		/**
		 * Creates new layout information for <code>pane</code>.
		 * @param pane the owner of this information
		 */
		public Layout( TabPane pane ){
			super( pane );
			menu = new MenuLayoutBlock();
			menu.setMenu( pane.createMenu() );
			
			LonelyTabPaneComponent infoComponent = pane.getInfoComponent();
			if( infoComponent != null )
				info = infoComponent.toLayoutBlock();
			
			tabs = new LineTabsLayoutBlock();
			tabs.setPane( pane );
		}

		/**
		 * Calculates the preferred size to show all elements.
		 * @return the preferred size
		 */
		public Dimension getPreferredSize(){
			List<PaneLayout> layouts = listLayouts();
			Dimension bestSize = new Dimension( 0, 0 );
			
			if( getPane().getTabPlacement().isHorizontal() ){
				for( PaneLayout layout : layouts ){
					if( layout.isPreferred() ){
						Dimension size = layout.getSize();
						if( size.width > bestSize.width ){
							bestSize = size;
						}
					}
				}
			}
			else{
				for( PaneLayout layout : layouts ){
					if( layout.isPreferred() ){
						Dimension size = layout.getSize();
						if( size.height > bestSize.height ){
							bestSize = size;
						}
					}
				}
			}
			
			return bestSize;
		}
		
		/**
		 * Calculates the minimal size required.
		 * @return the minimal size
		 */
		public Dimension getMinimumSize(){
			List<PaneLayout> layouts = listLayouts();
			Dimension bestSize = null;
			
			if( getPane().getTabPlacement().isHorizontal() ){
				for( PaneLayout layout : layouts ){
					Dimension size = layout.getSize();
					if( bestSize == null || size.width < bestSize.width ){
						bestSize = size;
					}
				}
			}
			else{
				for( PaneLayout layout : layouts ){
					Dimension size = layout.getSize();
					if( bestSize == null || size.height < bestSize.height ){
						bestSize = size;
					}
				}
			}
			
			return bestSize;
		}
		
		/**
		 * Informs this layout that it is no longer used and can release any
		 * resource.
		 */
		public void destroy(){
			getPane().destroyMenu( menu.getMenu() );
		}
		
		/**
		 * Updates the number of shown tabs and the boundaries of tabs, menu
		 * and info.
		 */
		public void layout(){
			AxisConversion conversion = getConversion( getPane() );
			
			List<PaneLayout> layouts = listLayouts();
			
			// search the layout that fits into the available space
			Rectangle available = conversion.viewToModel( getPane().getAvailableArea() );
			
			int space = available.width;
			
			PaneLayout best = null;
			int bestSize = -1;
			
			PaneLayout smallest = null;
			int smallestSize = -1;
			
			for( PaneLayout layout : layouts ){
				Dimension size = conversion.viewToModel( layout.getSize() );
				if( size.width <= space ){
					if( layout.isPreferred() ){
						if( (best == null || !best.isPreferred()) || bestSize < size.width ){
							bestSize = size.width;
							best = layout;
						}
					}
					else{
						if( (best == null || (!best.isPreferred()) && bestSize < size.width )){
							bestSize = size.width;
							best = layout;
						}
					}
				}
				
				if( smallest == null || size.width < smallestSize ){
					smallest = layout;
					smallestSize = size.width;
				}
			}
			
			if( best != null ){
				best.apply();
			}
			else if( smallest != null ){
				smallest.apply();
			}
		}
		
		/**
		 * Creates a list of all available layouts.
		 * @return the list of all available layouts
		 */
		private List<PaneLayout> listLayouts(){
			List<PaneLayout> results = new ArrayList<PaneLayout>();
			TabPlacement orientation = getPane().getTabPlacement();
			
			tabs.setOrientation( orientation );
			LineSize[] sizesTabs = tabs.getSizes();
			
			menu.setOrientation( orientation );
			Size[] sizesMenu = menu.getSizes();
			
			if( info != null ){
				info.setOrientation( orientation );
				Size[] sizesInfo = info.getSizes();
				for( Size size : sizesInfo ){
					listLayouts( results, size, sizesMenu, sizesTabs );
				}
			}
			else{
				listLayouts( results, null, sizesMenu, sizesTabs );
			}	
			return results;
		}
		
		private void listLayouts( List<PaneLayout> list, Size infoSize, Size[] menuSizes, LineSize[] tabSizes ){
			for( LineSize tab : tabSizes ){
				if( tab.isAllTabs() ){
					listLayouts( list, infoSize, (Size)null, tab );
				}
				else{
					for( Size menu : menuSizes ){
						listLayouts( list, infoSize, menu, tab );
					}
				}
			}
		}
		
		private void listLayouts( List<PaneLayout> list, Size infoSize, Size menuSize, LineSize tabSize ){
			boolean tabMustBeMinimum = (infoSize != null && infoSize.isMinimum()) || (menuSize != null);
			boolean tabMustBeSingle = menuSize != null && menuSize.isMinimum();
			boolean infoMustBeMinimum = menuSize != null && menuSize.isMinimum();
			
			if( tabMustBeMinimum && !tabSize.isMinimum() )
				return;
			
			if( tabMustBeSingle && (tabSize.getTabCount() > 1 ))
				return;
			
			if( infoMustBeMinimum && (infoSize != null && !infoSize.isMinimum()))
				return;
			
			list.add( new PaneLayout( tabSize, menuSize, infoSize ) );			
		}
		
		@Override
		public void infoComponentChanged( TabPane pane, LonelyTabPaneComponent oldInfo, LonelyTabPaneComponent newInfo ){
			super.infoComponentChanged( pane, oldInfo, newInfo );
			if( newInfo == null )
				info = null;
			else
				info = newInfo.toLayoutBlock();
		}
		
		/**
		 * A possibility for a layout
		 * @author Benjamin Sigg
		 */
		private class PaneLayout {
			private Size menuSize;
			private Size infoSize;
			private LineSize tabSize;
			
			/**
			 * Creates a new layout.
			 * @param tab the size of the tabs, not <code>null</code>
			 * @param menu the size of the menu, may be <code>null</code> to indicate
			 * that the menu is invisible
			 * @param info the size of the info panel, may be <code>null</code> if
			 * there is no info panel to show
			 */
			public PaneLayout( LineSize tab, Size menu, Size info ){
				this.menuSize = menu;
				this.tabSize = tab;
				this.infoSize = info;
			}
			
			@Override
			public String toString(){
				return getClass().getSimpleName() + "@[menu=" + menuSize + ", info=" + infoSize + ", tabs=" + tabSize + "]";
			}
			
			/**
			 * Tells whether this layout is a preferred layout. A preferred layout
			 * shows all tabs, has no menu, and the info panel (if present)
			 * has its preferred size.
			 * @return <code>true</code> if this layout is a preferred layout
			 */
			public boolean isPreferred(){
				if( tabSize == null || !tabSize.isPreferred() || !tabSize.isAllTabs() )
					return false;
				
				if( menuSize != null )
					return false;
				
				return true;
			}
			
			/**
			 * Gets the size this layout requires.
			 * @return the size
			 */
			public Dimension getSize(){
				int width = tabSize.getWidth();
				int height = tabSize.getHeight();
				
				if( getPane().getTabPlacement().isHorizontal() ){
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
				TabPane pane = getPane();
				AxisConversion conversion = getConversion( pane );
				TabPlacement orientation = pane.getTabPlacement();
				
				// update visibility and layout
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
					for( Tab tab : tabSize.getTabs() ){
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
				
				// ... check whether there is enough space for all items
				int space, tabSpace, menuSpace = 0, infoSpace = 0;
				if( orientation.isHorizontal() ){
					tabSpace = tabSize.getWidth();
					space = tabSpace;

					if( infoSize != null ){
						infoSpace = infoSize.getWidth();
						space += infoSpace;
					}
					if( menuSize != null ){
						menuSpace = menuSize.getWidth();
						space += menuSpace;
					}
				}
				else{
					tabSpace = tabSize.getHeight();
					space = tabSpace;
					
					if( infoSize != null ){
						infoSpace = infoSize.getHeight();
						space += infoSpace;
					}
					if( menuSize != null ){
						menuSpace = menuSize.getHeight();
						space += menuSpace;
					}
				}
				
				
				
				if( available.width >= space ){
					// enough space for all elements
					Rectangle tabBounds = new Rectangle( available.x, available.y, available.width - space + tabSpace, required );
					tabBounds = conversion.modelToView( tabBounds );
					tabs.setBounds( tabBounds.x, tabBounds.y, tabBounds.width, tabBounds.height );
					
					if( menuSize != null ){
						Rectangle menuBounds = new Rectangle( available.x + tabSpace, available.y, menuSpace, required );
						menuBounds = conversion.modelToView( menuBounds );
						menu.setBounds( menuBounds.x, menuBounds.y, menuBounds.width, menuBounds.height );
					}
					if( infoSize != null && info != null ){
						int reqDelta = Math.max( 0, required - infoSize.getHeight() );
						Rectangle infoBounds = new Rectangle( available.x + available.width - infoSpace, available.y+reqDelta/2, infoSpace, required-reqDelta );
						infoBounds = conversion.modelToView( infoBounds );
						info.setBounds( infoBounds.x, infoBounds.y, infoBounds.width, infoBounds.height );
					}
				}
				else{
					// not enough space for all elements
					double shrinkFactor = space / (double)available.width;
					
					int x = available.x;
					int width = (int)(shrinkFactor * tabSpace);
					
					Rectangle tabBounds = new Rectangle( x, available.y, width, required );
					tabBounds = conversion.modelToView( tabBounds );
					tabs.setBounds( tabBounds.x, tabBounds.y, tabBounds.width, tabBounds.height );
					
					x += width;
					if( menuSize != null ){
						width = (int)(shrinkFactor * menuSpace);
						Rectangle menuBounds = new Rectangle( x, available.y, width, required );
						menuBounds = conversion.modelToView( menuBounds );
						menu.setBounds( menuBounds.x, menuBounds.y, menuBounds.width, menuBounds.height );
						x += width;
					}
					if( infoSize != null ){
						width = available.x + available.width - x;
						int reqDelta = Math.max( 0, required - infoSize.getHeight() );
						Rectangle infoBounds = new Rectangle( x, available.y+reqDelta/2, width, required-reqDelta );
						infoBounds = conversion.modelToView( infoBounds );
						info.setBounds( infoBounds.x, infoBounds.y, infoBounds.width, infoBounds.height );
					}
				}
			}
		}
	}
}
