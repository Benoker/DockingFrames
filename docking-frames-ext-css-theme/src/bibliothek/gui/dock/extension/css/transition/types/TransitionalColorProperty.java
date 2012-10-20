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

import java.awt.Color;

import bibliothek.gui.dock.extension.css.transition.TransitionalCssProperty;
import bibliothek.gui.dock.extension.css.transition.TransitionalCssPropertyCallback;

/**
 * Animated property blending one {@link Color} into another {@link Color}.
 * @author Benjamin Sigg
 */
public class TransitionalColorProperty implements TransitionalCssProperty<Color>{
	private Color source;
	private Color target;
	private double transition;
	private TransitionalCssPropertyCallback<Color> callback;
	
	@Override
	public void setCallback( TransitionalCssPropertyCallback<Color> callback ){
		this.callback = callback;
	}

	@Override
	public void setSource( Color source ){
		this.source = source;
		update();
	}

	@Override
	public void setTarget( Color target ){
		this.target = target;
		update();
	}

	@Override
	public void setTransition( double transition ){
		this.transition = transition;
		update();
	}

	private void update(){
		if( source == null && target == null ){
			callback.set( null );
		}
		else{
			int as = 0;
			int rs = 0;
			int gs = 0;
			int bs = 0;
			
			int at = 0;
			int rt = 0;
			int gt = 0;
			int bt = 0;
			
			if( source != null ){
				as = source.getAlpha();
				rs = source.getRed();
				gs = source.getGreen();
				bs = source.getBlue();
			}
			
			if( target != null ){
				at = target.getAlpha();
				rt = target.getRed();
				gt = target.getGreen();
				bt = target.getBlue();
			}
			
			int a = mix( as, at );
			int r = mix( rs, rt );
			int g = mix( gs, gt );
			int b = mix( bs, bt );
			
			callback.set( new Color( r, g, b, a ) );
		}
	}
	
	private int mix( int a, int b ){
		return Math.min( 255, Math.max( 0, (int)Math.round( a * (1-transition) + b * transition ) ) );
	}
	
	@Override
	public void step( int delay ){
		update();
	}
}
