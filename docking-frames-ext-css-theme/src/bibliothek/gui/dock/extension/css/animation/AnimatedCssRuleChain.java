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

import bibliothek.gui.dock.extension.css.CssItem;
import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssScheme;

/***
 * Represents a list of {@link AnimatedCssRule} tied to one {@link CssItem}. The chain offers methods
 * to start new animations or to apply a new {@link CssRule}.
 * @author Benjamin Sigg
 */
public interface AnimatedCssRuleChain {
	
	/**
	 * Initializes <code>animation</code> and will use it the next time {@link #transition(CssRule)} is called. 
	 * Multiple animations may be initialized before a call to {@link #transition(CssRule)}.
	 * @param animation the additional animation to handle
	 * @return the rule that represents the current configuration of this chain, the same object may be used
	 * all the time, or a new object may be created when necessary
	 */
	public AnimatedCssRule animate( CssAnimation animation );

	/**
	 * Starts an animation for a transition of {@link #getRoot()} to <code>next</code>.
	 * @param next the next set of rules to use, may be the same object as {@link #getRoot()}, can be <code>null</code>
	 * @return the rule that represents the current configuration of this chain, the same object may be used
	 * all the time, or a new object may be created when necessary
	 */
	public AnimatedCssRule transition( CssRule next );
	
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
}
