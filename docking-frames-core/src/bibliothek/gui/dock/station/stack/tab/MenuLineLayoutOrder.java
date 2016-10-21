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

import java.awt.Component;
import java.util.Iterator;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.tab.layouting.LayoutBlock;

/**
 * Describes in which order, location and extend the {@link LayoutBlock}s should be given by a {@link MenuLineLayout}. 
 * @author Benjamin Sigg
 */
public class MenuLineLayoutOrder implements Iterable<MenuLineLayoutOrder.Item>{
	/**
	 * The various items that are used by the {@link MenuLineLayout}.
	 * @author Benjamin Sigg
	 */
	public enum Item{
		/** The menu showing overflowing tabs, might not always be visible */
		MENU,
		/** The tabs of the various {@link Dockable}s */
		TABS,
		/** An additional info-{@link Component} delivered by the {@link TabPane} */
		INFO
	}
	
	/**
	 * The order of the items
	 */
	private ItemConstraints[] order;

	/**
	 * Creates a new order. No argument must be <code>null</code> and no two arguments must be the same.
	 * @param first the first item to show
	 * @param second the second item to show
	 * @param third the last item to show
	 */
	public MenuLineLayoutOrder( Item first, Item second, Item third ){
		if( first == null ){
			throw new IllegalArgumentException( "first must not be null" );
		}
		if( second == null ){
			throw new IllegalArgumentException( "second must not be null" );
		}
		if( third == null ){
			throw new IllegalArgumentException( "third must not be null" );
		}
		
		if( first == second ){
			throw new IllegalArgumentException( "first and second are the same: " + first );
		}
		if( first == third ){
			throw new IllegalArgumentException( "first and third are the same: " + first );
		}
		if( second == third ){
			throw new IllegalArgumentException( "second and third are the same: " + second );
		}
		
		order = new ItemConstraints[]{ new ItemConstraints( first ), new ItemConstraints( second ), new ItemConstraints( third ) };
	}

	public Iterator<Item> iterator(){
		return new Iterator<Item>(){
			private int index = 0;
			
			public boolean hasNext(){
				return index < order.length;
			}
			
			public Item next(){
				return order[ index++ ].item;
			}
			
			public void remove(){
				throw new UnsupportedOperationException();
			}
		};
	}
	
	/**
	 * Gets the first item to show.
	 * @return the item, not <code>null</code> and not the same as the other two items
	 */
	public Item getFirst(){
		return order[0].item;
	}

	/**
	 * Gets the second item to show.
	 * @return the item, not <code>null</code> and not the same as the other two items
	 */
	public Item getSecond(){
		return order[1].item;
	}
	
	/**
	 * Gets the third item to show.
	 * @return the item, not <code>null</code> and not the same as the other two items
	 */
	public Item getThird(){
		return order[2].item;
	}
	
	private ItemConstraints get( Item item ){
		for( ItemConstraints constraint : order ){
			if( constraint.item == item ){
				return constraint;
			}
		}
		throw new IllegalArgumentException( "unknown item: " + item );
	}
	
	/**
	 * A shortcut to call {@link #setAlignment(Item, float)}, {@link #setWeight(Item, float)} and
	 * {@link #setFill(Item, float)} in one invocation.
	 * @param item the item whose constraints are set
	 * @param weight the new weight, see {@link #setWeight(Item, float)}
	 * @param alignment the new alignment, see {@link #setAlignment(Item, float)}
	 * @param fill the new fill ratio, see {@link #setFill(Item, float)}
	 */
	public void setConstraints( Item item, float weight, float alignment, float fill ){
		setAlignment( item, alignment );
		setWeight( item, weight );
		setFill( item, fill );
	}
	
	/**
	 * Sets the alignment of <code>item</code>. The alignment tells how <code>item</code> reacts if there
	 * is free space. A value of 0 means that the item clings to the left wall, a value of 1 means that the
	 * item clings to the right wall.
	 * @param item the item whose alignment is set, not <code>null</code>
	 * @param alignment the new alignment, between 0 and 1
	 */
	public void setAlignment( Item item, float alignment ){
		if( alignment < 0 || alignment > 1 ){
			throw new IllegalArgumentException( "Alignment must be between 0 and 1: " + alignment );
		}
		get( item ).alignment = alignment;
	}
	
	/**
	 * Gets the alignment of <code>item</code>.
	 * @param item the item to search
	 * @return the alignment
	 * @see #setAlignment(Item, float)
	 */
	public float getAlignment( Item item ){
		return get( item ).alignment;
	}
	
	/**
	 * Sets the weight of <code>item</code>. The weight tells how free space should be distributed, as larger the number
	 * as more space is assigned to <code>item</code>.
	 * @param item the item whose weight is set, not <code>null</code>
	 * @param weight the new weight, at least 0
	 */
	public void setWeight( Item item, float weight ){
		if( weight < 0 ){
			throw new IllegalArgumentException( "Weight must be at least 0: " + weight );
		}
		get( item ).weight = weight;
	}
	
	/**
	 * Gets the weight of <code>item</code>.
	 * @param item the item to search, not <code>null</code>
	 * @return the weight
	 * @see #setWeight(Item, float)
	 */
	public float getWeight( Item item ){
		return get( item ).weight;
	}
	
	/**
	 * Sets the fill ratio of <code>item</code>. The fill parameter tells how much free space is used up by <code>item</code>.
	 * A value of 0 indicates that <code>item</code> does not use any free space, a value of 1 indicates that <code>item</code>
	 * fills up all free space.
	 * @param item the item whose fill ratio is set, not <code>null</code>
	 * @param fill the new fill ratio, between 0 and 1
	 */
	public void setFill( Item item, float fill ){
		if( fill < 0 || fill > 1 ){
			throw new IllegalArgumentException( "Fill must be between 0 and 1: " + fill );
		}
		get( item ).fill = fill;
	}
	
	/**
	 * Gets the fill ratio of <code>item</code>.
	 * @param item the item to search
	 * @return the fill ratio
	 * @see #setFill(Item, float)
	 */
	public float getFill( Item item ){
		return get( item ).fill;
	}
	
	/**
	 * Describes additional constraints for a {@link LayoutBlock} and its position.
	 * @author Benjamin Sigg
	 */
	private static class ItemConstraints{
		private Item item;
		private float alignment;
		private float weight;
		private float fill;
		
		/**
		 * Creates a new constraints object
		 * @param item the item represented by this object
		 */
		public ItemConstraints(Item item){
			this.item = item;
			alignment = 0.0f;
			weight = 1.0f;
			fill = 0.0f;
		}
	}
}
