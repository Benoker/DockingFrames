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

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.extension.css.CssItem;
import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssScheme;

/**
 * Default implementation of {@link AnimatedCssRuleChain}, executes animations in parallel and does not allow
 * animations to influence each other.
 * @author Benjamin Sigg
 */
public class DefaultAnimatedCssRuleChain implements AnimatedCssRuleChain{
	private CssScheme scheme;
	private CssItem item;
	
	/** the very first rule of this chain, may not be <code>null</code> */
	private Link head;
	/** the most recent rule of this chain, may not be <code>null</code> */
	private Link tail;
	
	/**
	 * Creates the new chain.
	 * @param scheme the scheme in whose realm this chain is used
	 * @param item the item which is animated by this chain
	 * @param scheduler responsible for executing animations asynchronously
	 */
	public DefaultAnimatedCssRuleChain( CssScheme scheme, CssItem item ){
		this.scheme = scheme;
		this.item = item;
		
		head = new Link( null );
		tail = head;
	}

	@Override
	public AnimatedCssRule animate( CssAnimation<?> animation ){
		AnimatedCssRule rule = tail.getRule();
		rule.animate( animation );
		return rule;
	}

	@Override
	public AnimatedCssRule transition( CssRule next ){
		AnimatedCssRule oldRule = tail.getRule();
		
		Link link = new Link( next );
		tail.setNext( link );
		link.setPrevious( tail );
		tail = link;
		
		oldRule.transition( next );
		
		return link.getRule();
	}

	@Override
	public CssItem getItem(){
		return item;
	}

	@Override
	public CssScheme getScheme(){
		return scheme;
	}
	
	@Override
	public void destroy(){
		Link link = head;
		while( link != null ){
			link.destroy();
			link = link.getNext();
		}
	}

	/**
	 * Creates a new {@link AnimatedCssRule} which takes its default properties from <code>root</code>.
	 * @param root the root rule, can be <code>null</code>
	 * @return the new animated rule
	 */
	protected AnimatedCssRule createRule( CssRule root ){
		return new DefaultAnimatedCssRule( root );
	}
	
	private class Link implements RuleChainLink{
		private List<RuleChainLinkListener> listeners = new ArrayList<RuleChainLinkListener>( 2 );
		private AnimatedCssRule rule;
		private Link previous;
		private Link next;
		
		public Link( CssRule root ){
			rule = createRule( root );
			rule.inserted( this );
		}
		
		@Override
		public AnimatedCssRule getRule(){
			return rule;
		}

		@Override
		public Link getPrevious(){
			return previous;
		}
		
		public void setPrevious( Link previous ){
			Link oldPrevious = this.previous;
			this.previous = previous;
			for( RuleChainLinkListener listener : listeners() ){
				listener.previousChanged( this, oldPrevious, this.previous );
			}
		}

		@Override
		public Link getNext(){
			return next;
		}
		
		public void setNext( Link next ){
			Link oldNext = this.next;
			this.next = next;
			for( RuleChainLinkListener listener : listeners() ){
				listener.nextChanged( this, oldNext, this.next );
			}
		}

		@Override
		public AnimatedCssRuleChain getChain(){
			return DefaultAnimatedCssRuleChain.this;
		}

		@Override
		public void remove(){
			if( head == this && tail == this ){
				// this should never happen: remove is only to be called after a transition, and
				// if there is a transition there are at least two links in the chain.
				throw new IllegalStateException( "the only link in the chain cannot remove itself" );
			}
			destroy();
		}
		
		/**
		 * Removes this link from the chain without any further validity checks.
		 */
		public void destroy(){
			if( next != null ){
				next.setPrevious( previous );
			}
			if( previous != null ){
				previous.setNext( next );
			}
			
			if( tail == this ){
				tail = previous;
			}
			if( head == this ){
				head = next;
			}
			
			setNext( null );
			setPrevious( null );
			
			for( RuleChainLinkListener listener : listeners() ){
				listener.removed( this );
			}
		}

		@Override
		public void addListener( RuleChainLinkListener listener ){
			if( listener == null ){
				throw new IllegalArgumentException( "listener must not be null" );
			}
			listeners.add( listener );
		}

		@Override
		public void removeListener( RuleChainLinkListener listener ){
			listeners.remove( listener );
		}
		
		private RuleChainLinkListener[] listeners(){
			return listeners.toArray( new RuleChainLinkListener[ listeners.size() ] );
		}
	}
}
