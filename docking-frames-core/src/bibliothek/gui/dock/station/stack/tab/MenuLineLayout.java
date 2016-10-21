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
import java.awt.Point;

import bibliothek.gui.dock.station.stack.tab.layouting.LayoutBlock;
import bibliothek.gui.dock.station.stack.tab.layouting.Size;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;


/**
 * Orders tabs in a line, if there is not enough space a menu is used. Also
 * ensures the info-panel has its preferred size.
 * @author Benjamin Sigg
 */
public class MenuLineLayout extends AbstractTabLayoutManager<MenuLineLayoutPane>{
	/** this factory creates various items that are required by this {@link MenuLineLayout} */
	private MenuLineLayoutFactory factory = new DefaultMenuLineLayoutFactory();
	
	/** customizable algorithms  */
	private MenuLineLayoutStrategy strategy = new DefaultMenuLineLayoutStrategy();
	
	/**
	 * Sets a factory to create items that are required by this {@link MenuLineLayout}.
	 * @param factory the new factory, not <code>null</code>
	 */
	public void setFactory( MenuLineLayoutFactory factory ){
		if( factory == null ){
			throw new IllegalArgumentException( "factory must not be null" );
		}
		this.factory = factory;
	}
	
	/**
	 * Gets a factory which can be used to create items useful for this {@link MenuLineLayout}.
	 * @return the factory
	 */
	public MenuLineLayoutFactory getFactory(){
		return factory;
	}
	
	/**
	 * Sets a strategy offering customized algorithms. These algorithms are used
	 * to fine tune the behavior of this {@link MenuLineLayout}.
	 * @param strategy the strategy, not <code>null</code>
	 */
	public void setStrategy( MenuLineLayoutStrategy strategy ){
		if( strategy == null ){
			throw new IllegalArgumentException( "strategy must not be null" );
		}
		this.strategy = strategy;
	}
	
	/**
	 * Gets the strategy with the customized algorithms.
	 * @return the strategy
	 * @see #setStrategy(MenuLineLayoutStrategy)
	 */
	public MenuLineLayoutStrategy getStrategy(){
		return strategy;
	}
	
	@Override
	protected MenuLineLayoutPane createInfoFor( TabPane pane ){
		return new MenuLineLayoutPane( this, pane );
	}

	@Override
	protected void destroy( MenuLineLayoutPane info ){
		info.destroy();
	}
	
	public int getIndexOfTabAt( TabPane pane, Point mouseLocation ){
		MenuLineLayoutPane layout = getInfo( pane );
		if( layout == null )
			throw new IllegalArgumentException( "unknown pane" );
		return layout.getIndexOfTabAt( mouseLocation );
	}

	public Dimension getMinimumSize( TabPane pane ){
		MenuLineLayoutPane layout = getInfo( pane );
		if( layout == null )
			throw new IllegalArgumentException( "unknown pane" );
		return layout.getMinimumSize(); 
	}

	public Dimension getPreferredSize( TabPane pane ){
		MenuLineLayoutPane layout = getInfo( pane );
		if( layout == null )
			throw new IllegalArgumentException( "unknown pane" );
		return layout.getPreferredSize();
	}

	public void layout( TabPane pane ){
		MenuLineLayoutPane layout = getInfo( pane );
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
		return new DefaultAxisConversion( pane.getAvailableArea(), pane.getDockTabPlacement() );
	}
}
