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

import bibliothek.gui.dock.extension.css.CssItem;
import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssRuleContent;
import bibliothek.gui.dock.extension.css.CssScheme;

/***
 * Represents a list of {@link TransitionalCssRuleContent} tied to one {@link CssItem}. The chain offers methods
 * to start new transitions or to apply a new {@link CssRuleContent}.
 * @author Benjamin Sigg
 */
public interface TransitionalCssRuleChain {
	/**
	 * Initializes <code>transition</code> and will use it the next time {@link #transition(CssRuleContent)} is called. 
	 * Multiple transitions may be initialized before a call to {@link #transition(CssRuleContent)}.
	 * @param transitionKey the key of the {@link CssProperty} describing <code>transition</code>
	 * @param transition the additional transition to handle
	 * @return the rule that represents the current configuration of this chain, the same object may be used
	 * all the time, or a new object may be created when necessary
	 */
	public TransitionalCssRuleContent animate( CssPropertyKey transitionKey, CssTransition<?> transition );

	/**
	 * Starts an transition for a transition of {@link #getRoot()} to <code>next</code>.
	 * @param next the next set of rules to use, may be the same object as {@link #getRoot()}, can be <code>null</code>
	 * @return the rule that represents the current configuration of this chain, the same object may be used
	 * all the time, or a new object may be created when necessary
	 */
	public TransitionalCssRuleContent transition( CssRuleContent next );
	
	/**
	 * Gets the item which is animated by this chain.
	 * @return the animated item, not <code>null</code>
	 */
	public CssItem getItem();
	
	/**
	 * Gets the scheme in whose realm this chain works.
	 * @return the scheme, not <code>null</code>
	 */
	public CssScheme getScheme();
	
	/**
	 * Releases all resources this chain acquired and stops any transition right now.
	 */
	public void destroy();
}
