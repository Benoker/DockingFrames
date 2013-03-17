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

/**
 * A {@link TransitionalCssProperty} that performs a jump from the source to the target value in the
 * middle of the transition.
 * @author Benjamin Sigg
 * @param <T> the type of value "animated" by this "transition"
 */
public class MiddleTransitionalCssProperty<T> implements TransitionalCssProperty<T> {
	private TransitionalCssPropertyCallback<T> callback;
	private T source;
	private T target;
	private double transition;
	
	@Override
	public void setCallback( TransitionalCssPropertyCallback<T> callback ){
		this.callback = callback;
	}

	@Override
	public void setSource( T source ){
		this.source = source;
		update();
	}

	@Override
	public void setTarget( T target ){
		this.target = target;
		update();
	}

	@Override
	public void setTransition( double transition ){
		this.transition = transition;
		update();
	}

	@Override
	public void step( int delay ){
		update();
	}

	private void update(){
		if( transition < 0.5 ){
			callback.set( source );
		}
		else{
			callback.set( target );
		}
	}
}
