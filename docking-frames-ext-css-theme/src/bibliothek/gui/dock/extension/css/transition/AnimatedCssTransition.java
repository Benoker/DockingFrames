/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.transition;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.property.IntegerCssProperty;

/**
 * An animated {@link CssTransition} is a transition that plays some kind of animation, and
 * thus requires a parameter called <code>duration</code>. 
 * @author Benjamin Sigg
 * @param <T> the type of value this transition will handle
 */
public abstract class AnimatedCssTransition<T> extends AbstractCssTransition<T>{
	private int duration = 500;
	private int time = 0;
	
	private CssPropertyKey durationKey;
	private IntegerCssProperty durationProperty = new IntegerCssProperty(){
		@Override
		public void set( Integer value ){
			if( value == null ){
				duration = 500;
			}
			else{
				duration = value;
			}
		}
		
		@Override
		public void setScheme( CssScheme scheme, CssPropertyKey key ){
			durationKey = key;
		}
	};
	
	@Override
	public boolean isInput( CssPropertyKey key ){
		if( key.equals( durationKey ) ){
			return true;
		}
		return super.isInput( key );
	}
	
	@Override
	public String[] getPropertyKeys(){
		return new String[]{ "duration" };
	}

	@Override
	public CssProperty<?> getProperty( String key ){
		if( "duration".equals( key )){
			return durationProperty;
		}
		return null;
	}
	

	@Override
	public void step( int delay ){
		if( delay != -1 ){
			time += delay;
		}
		if( time > duration ){
			endAnimation();
		}
		else{
			double progress = time / (double)duration;
			updateProgress( progress );
		}
	}
}
