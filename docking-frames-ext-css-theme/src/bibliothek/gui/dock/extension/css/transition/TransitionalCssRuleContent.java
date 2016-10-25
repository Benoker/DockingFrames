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
import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssRuleContent;

/**
 * An {@link TransitionalCssRuleContent} is a {@link CssRuleContent} whose fields may constantly be changing
 * because of an transition. The transition itself is implemented by a set of {@link CssTransition}s.<br>
 * An {@link TransitionalCssRuleContent} usually is a wrapper around another rule, and modifies the fields of that
 * other rule on the fly. The source rule is called {@link #getRoot() root rule}.<br>
 * {@link TransitionalCssRuleContent}s are ordered in a list, this list is called the {@link TransitionalCssRuleChain}.
 * @author Benjamin Sigg
 */
public interface TransitionalCssRuleContent extends CssRuleContent{
	/**
	 * Gets the root {@link CssRuleContent}. The root rule is the source of all the properties offered by this
	 * {@link TransitionalCssRuleContent}. Note that if several transitions are nested, then the root rule is not the previous
	 * transition, but still the original source of all properties.
	 * @return the root rule, the rule that would be applied if there was no transition, can be <code>null</code>
	 * or <code>this</code>
	 */
	public CssRuleContent getRoot();
	
	/**
	 * Informs this rule that it has been inserted into the list of transitions. This method may only be called
	 * one time, afterwards it will always throw an {@link IllegalStateException}.
	 * @param link information about the list and the position of this rule, not <code>null</code>,
	 * rule should add a {@link RuleChainLinkListener} to <code>link</code>
	 * @throws IllegalStateException if this rule already is part of a chain
	 * @throws IllegalArgumentException if the chain is not acceptable of any reason
	 */
	public void inserted( RuleChainLink link );
	
	/**
	 * Tells whether the property with name <code>property</code> is an animated property. The value of an 
	 * animated property is set by this or one of the predecessor {@link TransitionalCssRuleContent}s, while the value
	 * of an ordinary property originates from a root {@link CssRule}.
	 * @param property the name of a property
	 * @return whether <code>property</code> is animated.
	 */
	public boolean isAnimated( CssPropertyKey property );
	
	/**
	 * Tells whether the property with name <code>property</code> is required as input value for the transitions. 
	 * {@link CssProperty}s matching this method are not removed when the {@link CssRule} changes, instead they
	 * continue to be used until the transitions end.
	 * @param property the name of a property
	 * @return whether <code>property</code> declares an input value
	 */
	public boolean isInput( CssPropertyKey property );
	
	/**
	 * Starts an additional transition on this rule. {@link CssTransition#init(CssRuleContent, CsstransitionCallback)} should
	 * be called on <code>transition</code>. If the {@link #transition(CssRuleContent) transition} already started
	 * then {@link CssTransition#transition(CssRuleContent)} should be called as well.
	 * @param transitionKey the key of the {@link CssProperty} describing <code>transition</code>
	 * @param transition the new transition, not <code>null</code>
	 */
	public void animate( CssPropertyKey transitionKey, CssTransition<?> transition );
	
	/**
	 * Starts a transition on this rule. {@link CssTransition#transition(CssRuleContent)} should be called
	 * on all transitions. This rule has to call {@link RuleChainLink#remove()} as soon as all
	 * transitions have finished the transition.
	 * @param root the next root rule, may be <code>null</code>
	 */
	public void transition( CssRuleContent root );
	
	/**
	 * Executes <code>job</code> once this {@link TransitionalCssRuleContent} no longer is active.
	 * @param job the job to execute, not <code>null</code>
	 */
	public void onDestroyed( Runnable job );
}