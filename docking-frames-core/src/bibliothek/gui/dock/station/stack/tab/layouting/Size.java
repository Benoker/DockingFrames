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


/**
 * A {@link Size} is used by a {@link LayoutBlock} to describe the layout
 * that fits to some size. 
 * @author Benjamin Sigg
 */
public class Size {
	/**
	 * What type of size a {@link Size} describes.
	 * @author Benjamin Sigg
	 */
	public static enum Type{
		/** The minimum size some component requires */
		MINIMUM,
		
		/** The preferred size some component requires */
		PREFERRED;
	}
	
	/** What size this key describes */
	private Type type;
	
	/** horizontal amount of pixels */
	private int width;
	
	/** vertical amount of pixels */
	private int height;
	
	/** how well this size is liked */
	private double score;
	
	/**
	 * Creates a new size.
	 * @param type what kind of size this describes
	 * @param width horizontal amount of pixels
	 * @param height vertical amount of pixels
	 * @param score how much this size is liked, a value of <code>1.0</code> indicates that
	 * this is the best possible size, while a value of <code>0.0</code> indicates that this
	 * size is as good as unusable
	 */
	public Size( Type type, int width, int height, double score ){
		if( type == null )
			throw new IllegalArgumentException( "type must not be null" );
		if( score < 0.0 || score > 1.0 ){
			throw new IllegalArgumentException( "score out of bounds: " + score );
		}
		
		this.score = score;
		this.type = type;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Creates a new size
	 * @param type what kind of size this describes
	 * @param size the amount of pixels
	 * @param score how much this size is liked, a value of <code>1.0</code> indicates that
	 * this is the best possible size, while a value of <code>0.0</code> indicates that this
	 * size is as good as unusable
	 */
	public Size( Type type, Dimension size, double score ){
		this( type, size.width, size.height, score );
	}
	
	/**
	 * Tells whether this {@link Size} describes the minimum size
	 * some component requires.
	 * @return <code>true</code> if this is a minimum size
	 */
	public boolean isMinimum(){
		return Type.MINIMUM == type;
	}
	
	/**
	 * Tells whether this {@link Size} describes the preferred size
	 * some component requires.
	 * @return <code>true</code> if this is a preferred size
	 */
	public boolean isPreferred(){
		return Type.PREFERRED == type;
	}
	
	/**
	 * Gets the type of this size.
	 * @return the type, never <code>null</code>
	 */
	public Type getType(){
		return type;
	}
	
	/**
	 * Gets the horizontal amount of pixels.
	 * @return the width
	 */
	public int getWidth(){
		return width;
	}
	
	/**
	 * Gets the vertical amount of pixels.
	 * @return the height
	 */
	public int getHeight(){
		return height;
	}
	
	/**
	 * Tells how much this size is liked, a value of <code>1.0</code> indicates that
	 * this is the best possible size, while a value of <code>0.0</code> indicates that this
	 * size is as good as unusable
	 * @return the score, a value between 0.0 and 1.0
	 */
	public double getScore(){
		return score;
	}
	
	/**
	 * Returns this size as {@link Dimension}.
	 * @return a new {@link Dimension}
	 */
	public Dimension toDimension(){
		return new Dimension( width, height );
	}
	
	@Override
	public String toString(){
		return getClass().getSimpleName() + "@[width=" + getWidth() + ", height=" + getHeight() + ", type=" + getType() + "]";
	}
}
