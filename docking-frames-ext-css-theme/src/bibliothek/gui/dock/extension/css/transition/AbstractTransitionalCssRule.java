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

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssRuleContentListener;
import bibliothek.gui.dock.extension.css.CssRuleListener;

/**
 * This {@link TransitionalCssRuleContent} offers functionality to store and listen
 * to a {@link RuleChainLink}, and offers methods to fire events to
 * {@link CssRuleListener}s. 
 * @author Benjamin Sigg
 */
public abstract class AbstractTransitionalCssRule implements TransitionalCssRuleContent{
	private List<CssRuleContentListener> listeners = new ArrayList<CssRuleContentListener>();
	
	/** implements all the listener interfaces required by this rule */
	private Listener listener = new Listener();
	
	/** the position of this rule */
	private RuleChainLink link;
	
	/** the predecessor transition */
	private TransitionalCssRuleContent previous;
	
	/** jobs to execute on destruction */
	private List<Runnable> jobs = new ArrayList<Runnable>();
	
	@Override
	public void inserted( RuleChainLink link ){
		if( this.link != null ){
			throw new IllegalStateException( "this rule is already part of a chain" );
		}
		if( link == null ){
			throw new IllegalArgumentException( "link must not be null" );
		}
		this.link = link;		
		link.addListener( listener );
	}
	
	/**
	 * Gets the current position of this rule in the chain.
	 * @return the position, can be <code>null</code>
	 */
	public RuleChainLink getLink(){
		return link;
	}
	
	@Override
	public void addRuleContentListener( CssRuleContentListener listener ){
		listeners.add( listener );
	}

	@Override
	public void removeRuleContentListener( CssRuleContentListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Sets the predecessor transition.
	 * @param previous the predecessor, may be <code>null</code>
	 */
	protected void setPrevious( TransitionalCssRuleContent previous ){
		this.previous = previous;
	}
	
	/**
	 * Gets the predecessor transition.
	 * @return the predecessor transition
	 */
	protected TransitionalCssRuleContent getPrevious(){
		return previous;
	}
	
	/**
	 * Fires an event informing all {@link CssRuleContentListener}s that property <code>key</code> changed.
	 * @param key the key of the changed property
	 */
	protected void fireChanged( CssPropertyKey key ){
		for( CssRuleContentListener listener : listeners.toArray( new CssRuleContentListener[ listeners.size()] )){
			listener.propertyChanged( this, key );
		}
	}
		
	@Override
	public void onDestroyed( Runnable job ){
		if( job == null ){
			throw new IllegalArgumentException( "job must not be null" );
		}
		jobs.add( job );	
	}
	
	private class Listener implements RuleChainLinkListener{
		@Override
		public void removed( RuleChainLink source ){
			link.removeListener( this );
			setPrevious( null );
			for( Runnable job : jobs ){
				job.run();
			}
		}
		
		@Override
		public void nextChanged( RuleChainLink source, RuleChainLink oldNext, RuleChainLink newNext ){
			// ignore
		}
		
		@Override
		public void previousChanged( RuleChainLink source, RuleChainLink oldPrevious, RuleChainLink newPrevious ){
			if( newPrevious == null ){
				setPrevious( null );
			}
			else{
				setPrevious( newPrevious.getRule() );
			}
		}
	}
}
