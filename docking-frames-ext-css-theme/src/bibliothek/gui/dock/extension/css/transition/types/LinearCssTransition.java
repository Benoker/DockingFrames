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

import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.property.paint.CssPaint;
import bibliothek.gui.dock.extension.css.transition.AnimatedCssTransition;
import bibliothek.gui.dock.extension.css.transition.TransitionalCssProperty;

/**
 * An transition property fading one {@link CssPaint} into another.
 * @author Benjamin Sigg
 * @param <T> the type of value this transition will handle
 */
public class LinearCssTransition<T> extends AnimatedCssTransition<T>{
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
