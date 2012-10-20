/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.transition.types;

import bibliothek.gui.dock.extension.css.transition.TransitionalCssProperty;
import bibliothek.gui.dock.extension.css.transition.TransitionalCssPropertyCallback;

/**
 * Animated property blending one {@link Integer} into another {@link Integer}.
 * @author Benjamin Sigg
 */
public class TransitionalIntegerProperty implements TransitionalCssProperty<Integer>{
	private int source = 0;
	private int target = 0;
	private double transition;
	private TransitionalCssPropertyCallback<Integer> callback;
	
	@Override
	public void setCallback( TransitionalCssPropertyCallback<Integer> callback ){
		this.callback = callback;
	}

	@Override
	public void setSource( Integer source ){
		if( source == null ){
			this.source = 0;
		}
		else{
			this.source = source;
		}
		update();
	}

	@Override
	public void setTarget( Integer target ){
		if( target == null ){
			this.target = 0;
		}
		else{
			this.target = target;
		}
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
		callback.set( (int)( source * (1-transition) + target * transition ) );
	}
}
