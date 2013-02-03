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
package bibliothek.gui.dock.extension.css.transition.types;

import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.transition.AbstractCssTransition;
import bibliothek.gui.dock.extension.css.transition.CssTransition;
import bibliothek.gui.dock.extension.css.transition.TransitionalCssProperty;

/**
 * This specialized {@link CssTransition} instantly finishes the transition.
 * @author Benjamin Sigg
 * @param <T> the type of item this  handles 
 */
public class InstantCssTransition<T> extends AbstractCssTransition<T>{
	@Override
	public void step( int delay ){
		endAnimation();
	}

	@Override
	protected TransitionalCssProperty<T> createProperty( CssType<T> type, CssPropertyKey key ){
		return type.createTransition();
	}

	@Override
	protected void bind(){
		// ignore
	}

	@Override
	protected void unbind(){
		// ignore
	}
}
