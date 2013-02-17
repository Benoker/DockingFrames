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

/**
 * A single element of a {@link TransitionalCssRuleChain}, represents one {@link TransitionalCssRuleContent}.
 * @author Benjamin Sigg
 */
public interface RuleChainLink {
	/**
	 * Gets the rule which is represented by this link.
	 * @return the rule of this link, can be <code>null</code> if this link is no longer in use
	 */
	public TransitionalCssRuleContent getRule();
	
	/**
	 * Gets the link that is previously in the chain.
	 * @return the parent link, can be <code>null</code> if this link is the head or if this link
	 * is no longer in use.
	 */
	public RuleChainLink getPrevious();
	
	/**
	 * Gets the link that follows in the chain.
	 * @return the next link, can be <code>null</code> if this link is the tail or if this link
	 * is no longer in use.
	 */
	public RuleChainLink getNext();
	
	/**
	 * Gets the chain to which this link belongs.
	 * @return the chain of this link, never <code>null</code> even if this link is no longer used
	 */
	public TransitionalCssRuleChain getChain();
	
	/**
	 * Removes this link from the chain. The link cannot be added to chain again.
	 */
	public void remove();
	
	/**
	 * Adds a listener to this link, the listener is informed if this item changes the position
	 * within the chain.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addListener( RuleChainLinkListener listener );
	
	/**
	 * Removes <code>listener</code> from this link.
	 * @param listener the listener to remove
	 */
	public void removeListener( RuleChainLinkListener listener );
}
