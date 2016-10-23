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
import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssRuleContent;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.util.Filter;

/**
 * A {@link CssTransition} describes the transition between one {@link CssRule} to another {@link CssRule}. The transition
 * may include features like colors softly blending over to a new style, or shapes slowly changing their outline.<br>
 * The lifecycle of any {@link CssTransition} looks always like this:
 * <ol>
 * 	<li>Upon applying a new {@link CssRule} the {@link CssTransition} is created from a factory.</li>
 *  <li>Immediately afterwards {@link #init(CssRule, CssTransitionCallback) init} is called, the transition is now
 *  allowed to change properties whenever it wants.</li>
 *  <li>If another {@link CssRule} is applied, {@link #transition(CssRule) transition} is called. The transition should
 *  now slowly apply the new rule. As there may already be an transition active on the new rule, this transition
 *  may actually gain access to a wrapper which behaves like an animated {@link CssRule}.</li>
 *  <li>At the end of its lifetime the transition should call {@link CssTransitionCallback#destroyed()} to inform
 *  the framework that this transition will never be used again.</li>
 * </ol>
 * The transition can and should make use of the {@link CssTransitionCallback} to learn:
 * <ul>
 * 	<li>Which properties can be animated</li>
 *  <li>Override properties with new values</li>
 *  <li>Ask for delayed execution of {@link #step()}</li>
 *  <li>Inform the framework that it is no longer active</li>
 * </ul>
 * 
 * If transitions are handled by the {@link DefaultAnimatedCssRuleChain}, some limitations are applied: transitions of 
 * one {@link CssRule} run in parallel, they do not know of each other nor can they influence each other. It is not
 * possible to use the output of one transition as input for another transition. If two transitions modify the same 
 * property, then one of the transitions is silently ignored. If a customized {@link TransitionalCssRuleChain} is in use,
 * these limitations may no longer apply.
 * 
 * @author Benjamin Sigg
 */
public interface CssTransition<T> extends CssPropertyContainer{
	/**
	 * Sets a filter telling this transition which properties should actually be animated, and which not. 
	 * @param propertyFilter the filter, only properties passing the filter should be animated, can be <code>null</code>
	 */
	public void setPropertyFilter( Filter<CssPropertyKey> propertyFilter );
	
	/**
	 * Informs this transition about the type of the properties is should handle.
	 * @param type the type of the properties
	 */
	public void setType( CssType<T> type );
	
	/**
	 * Tells whether <code>property</code> is declaring input values for the transition. The 
	 * <code>property</code> may be a child of this {@link CssPropertyContainer}, or a child of one
	 * of the {@link CssProperty}s returned by this container.
	 * @param property the name of the property to check
	 * @return whether this transition depends on <code>property</code>
	 */
	public boolean isInput( CssPropertyKey property );
	
	/**
	 * Initializes this transition.
	 * @param source a rule representing the properties before the transition started
	 * @param callback information about the properties and utility methods
	 */
	public void init( CssRuleContent source, CssTransitionCallback callback );
	
	/**
	 * Called asynchronously if {@link CssTransitionCallback#step()} is called, or if one of the underlying {@link CssRuleContent}s
	 * changed a property. This method is always executed in the EDT.<br>
	 * This method should recalculate all the properties affected by this transition and transfer the new values
	 * to the {@link CssTransitionCallback}.
	 * @param delay the amount of milliseconds that passed since the last call of this method, or <code>-1</code>
	 * if the call to this method is out of order. This argument will always be <code>-1</code> if
	 * this transition does not call {@link CssTransitionCallback#step()} during execution of this method.
	 */
	public void step( int delay );
	
	/**
	 * Called if this transition is about to become obsolete. The transition should gracefully transform the properties
	 * to the properties of <code>destination</code>, and then shutdown by calling {@link CssTransitionCallback#destroyed()}.
	 * @param destination the target rule which should be adapted by this transition
	 */
	public void transition( CssRuleContent destination );
}
