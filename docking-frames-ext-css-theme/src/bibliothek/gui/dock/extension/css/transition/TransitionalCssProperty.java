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
package bibliothek.gui.dock.extension.css.transition;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainer;
import bibliothek.gui.dock.extension.css.CssRule;

/**
 * Represents one {@link CssProperty} whose value is animated.<br>
 * Things get interesting if <code>T</code> is a {@link CssPropertyContainer}:
 * <ul>
 * 	<li>To fully support the sub-properties of <code>T</code>, the clients needs to access the property with 
 * a {@link CssContainerTransitionProperty}.</li>
 *  <li>Values that are calculated should not show up as {@link CssProperty}, otherwise they will be overridden
 *  by the values defined in the current, leading {@link CssRule} (negating the effects of the transition).</li> 
 * </ul>
 * @author Benjamin Sigg
 * @param <T> the type of value handled by this property
 */
public interface TransitionalCssProperty<T> {
	/**
	 * Sets a callback, any change of the value has to be reported to <code>callback</code>.
	 * @param callback the callback
	 */
	public void setCallback( TransitionalCssPropertyCallback<T> callback );
	
	/**
	 * Informs this property about the source value, the value that is used before the transition starts.
	 * @param source the source value, may be <code>null</code>
	 */
	public void setSource( T source );
	
	/**
	 * Informs this property about the target value, the value that is used after the transition stopped.
	 * @param target the target value, can be <code>null</code>
	 */
	public void setTarget( T target );
	
	/**
	 * Informs this property of the current progress of the transition from source to target {@link CssRule}.
	 * @param transition the progress, a value between <code>0</code> and <code>1</code>
	 */
	public void setTransition( double transition );
	
	/**
	 * Tells this property to check its value, and maybe call {@link TransitionalCssPropertyCallback#set(Object)}
	 * if the value changed.
	 * @param delay the delay since the last call to this method or <code>-1</code> if the call is either out of
	 * order or the call was not scheduled
	 */
	public void step( int delay );
}
