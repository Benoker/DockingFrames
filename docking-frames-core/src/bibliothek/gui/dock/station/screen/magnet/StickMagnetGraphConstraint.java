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
package bibliothek.gui.dock.station.screen.magnet;

import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.screen.magnet.MagnetRequest.Side;

/**
 * A constraint tells how much a side of a {@link ScreenDockWindow} has to move. Constraints
 * are used by the {@link StickMagnetGraph} during calculations.
 * @author Benjamin Sigg
 */
public class StickMagnetGraphConstraint {
	/** how many pixels to move */
	private int[] deltas = new int[4];
	
	/** whether the constraint is hard, i.e. depends directly on user input */
	private boolean[] hard = new boolean[4];
	
	/** whether the constraint is set directly, or just guessed */
	private boolean[] direct = new boolean[4];
	
	/**
	 * Creates a new constraint
	 */
	public StickMagnetGraphConstraint(){
		reset();
	}
	
	/**
	 * Unsets all values of this constraint.
	 */
	public void reset(){
		for( int i = 0; i < 4; i++ ){
			deltas[i] = Integer.MIN_VALUE;
			hard[i] = false;
			direct[i] = false;
		}
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append( getClass().getSimpleName() ).append( "[" );
		boolean first = true;
		
		for( Side side : Side.values() ){
			if( first ){
				first = false;
			}
			else{
				builder.append( ", " );
			}
			
			builder.append( side.name().toLowerCase() ).append( ":" );
			if(isSet( side )){
				builder.append( " " );
				builder.append( get( side ) );
			}
			if(isHard( side )){
				builder.append( " (hard)");
			}
		}
		
		builder.append( "]" );
		return builder.toString();
	}
	
	/**
	 * Sets how much <code>side</code> has to be moved.
	 * @param side the side to move
	 * @param delta the amount of pixels to move
	 */
	public void set( Side side, int delta ){
		deltas[ side.ordinal() ] = delta;
	}
	
	/**
	 * Calls {@link #set(MagnetRequest.Side, int)}, {@link #setDirect(MagnetRequest.Side, boolean)} and {@link #setHard(MagnetRequest.Side, boolean)}.
	 * @param side the side to change
	 * @param delta the amount of pixels to move <code>side</code>
	 * @param direct whether the side was calculated or just guessed
	 * @param hard whether the side depends directly from user input
	 */
	public void set( Side side, int delta, boolean direct, boolean hard ){
		set( side, delta );
		setHard( side, hard );
		setDirect( side, direct );
	}
	
	/**
	 * Tells whether {@link #set(MagnetRequest.Side, int)} was called for <code>side</code>.
	 * @param side the side to check
	 * @return <code>true</code> if {@link #set(MagnetRequest.Side, int)} was called at least once
	 */
	public boolean isSet( Side side ){
		return deltas[ side.ordinal() ] != Integer.MIN_VALUE;
	}
	
	/**
	 * Gets the value that was {@link #set(MagnetRequest.Side, int) set} earlier for <code>side</code>. 
	 * @param side the side to get
	 * @return the amount of pixels to move this side
	 * @throws IllegalArgumentException if <code>side</code> was not set
	 */
	public int get( Side side ){
		if( !isSet( side )){
			throw new IllegalArgumentException( "side " + side + " is not set" );
		}
		return deltas[ side.ordinal() ];
	}
	
	/**
	 * Marks <code>side</code> as a hard side. A hard side depends directly from the user
	 * input.
	 * @param side the side to change
	 * @param hard whether the side is hard or not
	 */
	public void setHard( Side side, boolean hard ){
		this.hard[ side.ordinal() ] = hard;
	}
	
	/**
	 * Tells whether <code>side</code> is a hard side. A hard side depends directly from the
	 * user input.
	 * @param side the side to access
	 * @return whether the side is hard
	 */
	public boolean isHard( Side side ){
		return hard[ side.ordinal() ];
	}
	
	/**
	 * Marks <code>side</code> to be set directly. A direct side is actually calculated, an indirect side
	 * is just guessed.
	 * @param side the side to change
	 * @param direct whether the side was set directly
	 */
	public void setDirect( Side side, boolean direct ){
		this.direct[ side.ordinal() ] = direct;
	}
	
	/**
	 * Tells whether <code>side</code> is a directly set side.
	 * @param side the side to access
	 * @return whether the side was set directly
	 */
	public boolean isDirect( Side side ){
		return direct[ side.ordinal() ];
	}
}
