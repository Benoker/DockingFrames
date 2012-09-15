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
package bibliothek.gui.dock.extension.css.animation;

import bibliothek.gui.dock.extension.css.CssRule;

/**
 * An {@link AnimatedCssRule} is a {@link CssRule} whose fields may constantly be changing
 * because of an animation. The animation itself is implemented by a set of {@link CssAnimation}s.<br>
 * An {@link AnimatedCssRule} usually is a wrapper around another rule, and modifies the fields of that
 * other rule on the fly. The source rule is called {@link #getRoot() root rule}.<br>
 * {@link AnimatedCssRule}s are ordered in a list, this list is called the {@link AnimatedCssRuleChain}.
 * @author Benjamin Sigg
 */
public interface AnimatedCssRule extends CssRule{
	/**
	 * Gets the root {@link CssRule}. The root rule is the source of all the properties offered by this
	 * {@link AnimatedCssRule}. Note that if several animations are nested, then the root rule is not the previous
	 * animation, but still the original source of all properties.
	 * @return the root rule, the rule that would be applied if there was no animation, can be <code>null</code>
	 * or <code>this</code>
	 */
	public CssRule getRoot();
	
	/**
	 * Informs this rule that it has been inserted into the list of animations. This method may only be called
	 * one time, afterwards it will always throw an {@link IllegalStateException}.
	 * @param link information about the list and the position of this rule, not <code>null</code>,
	 * rule should add a {@link RuleChainLinkListener} to <code>link</code>
	 * @throws IllegalStateException if this rule already is part of a chain
	 * @throws IllegalArgumentException if the chain is not acceptable of any reason
	 */
	public void inserted( RuleChainLink link );
	
	/**
	 * Tells whether the property with name <code>property</code> is an animated property. The value of an 
	 * animated property is set by this or one of the predecessor {@link AnimatedCssRule}s, while the value
	 * of an ordinary property originates from a root {@link CssRule}.
	 * @param property the name of a property
	 * @return whether <code>property</code> is animated.
	 */
	public boolean isAnimated( String property );
	
	/**
	 * Starts an additional animation on this rule. {@link CssAnimation#init(CssRule, CssAnimationCallback)} should
	 * be called on <code>animation</code>. If the {@link #transition(CssRule) transition} already started
	 * then {@link CssAnimation#transition(CssRule)} should be called as well.
	 * @param animation the new animation, not <code>null</code>
	 */
	public void animate( CssAnimation animation );
	
	/**
	 * Starts a transition on this rule. {@link CssAnimation#transition(CssRule)} should be called
	 * on all animations. This rule has to call {@link RuleChainLink#remove()} as soon as all
	 * animations have finished the transition.
	 * @param root the next root rule, may be <code>null</code>
	 */
	public void transition( CssRule root );
}