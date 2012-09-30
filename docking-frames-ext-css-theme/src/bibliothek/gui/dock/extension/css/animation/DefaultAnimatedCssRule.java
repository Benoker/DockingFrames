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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.dock.extension.css.CssItem;
import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.CssSelector;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.animation.scheduler.AnimationSchedulable;
import bibliothek.gui.dock.extension.css.animation.scheduler.AnimationScheduler;

/**
 * The defeault implementation of {@link AnimatedCssRule} makes use of one {@link CssRule} and a list
 * of {@link CssAnimation}s to perform animations. 
 * @author Benjamin Sigg
 */
public class DefaultAnimatedCssRule extends AbstractAnimatedCssRule {
	private CssRule root;
	private WrappedCssRule source;
	private List<Animation> animations = new ArrayList<Animation>();
	
	private CssRule nextRoot;
	private boolean transition = false;
	
	/**
	 * Creates a new animated rule.
	 * @param root the root rule, the source of all properties, can be <code>null</code>
	 */
	public DefaultAnimatedCssRule( CssRule root ){
		this.root = root;
		source = new WrappedCssRule( root );
	}
	
	@Override
	protected void setPrevious( AnimatedCssRule previous ){
		super.setPrevious( previous );
		if( previous == null ){
			source.setRule( root );
		}
		else{
			source.setRule( previous );
		}
	}
	
	@Override
	public void animate( CssAnimation<?> animation ){
		Animation callback = new Animation( animation );
		animations.add( callback );
		animation.init( source, callback );
		if( transition ){
			animation.transition( nextRoot );
		}
	}
	
	@Override
	public void transition( CssRule root ){
		nextRoot = root;
		transition = true;
		if( animations.isEmpty() ){
			getLink().remove();
		}
		else{
			for( Animation animation : animations ){
				animation.animation.transition( root );
			}
		}
	}
	
	@Override
	public boolean isInput( CssPropertyKey property ){
		for( Animation animation : animations ){
			if( animation.animation.isInput( property )){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isAnimated( CssPropertyKey property ){
		for( Animation animation : animations ){
			if( animation.overridenProperties.containsKey( property )){
				return true;
			}
		}
		AnimatedCssRule previous = getPrevious();
		if( previous == null ){
			return false;
		}
		return previous.isAnimated( property );
	}
	
	@Override
	public CssRule getRoot(){
		return root;
	}
	
	@Override
	public CssSelector getSelector(){
		return root.getSelector();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty( CssType<T> type, CssPropertyKey property ){
		AnimatedProperty<?> animated = null;
		for( Animation animation : animations ){
			animated = animation.overridenProperties.get( property );
			if( animated != null ){
				break;
			}
		}
		
		if( animated == null ){
			AnimatedCssRule previous = getPrevious();
			if( previous == null || !previous.isAnimated( property )){
				return root.getProperty( type, property );
			}
			return previous.getProperty( type, property );
		}
		if( !animated.type.equals( type )){
			throw new IllegalArgumentException( "type conflict, expected " + type + ", but found " + animated.type + " for property " + property );
		}
		return (T)animated.value;
	}
	
	private static class AnimatedProperty<T>{
		private CssType<T> type;
		private T value;
		
		public AnimatedProperty( CssType<T> type, T value ){
			this.type = type;
			this.value = value;
		}
	}
	
	private class Animation implements CssAnimationCallback, AnimationSchedulable{
		private CssAnimation<?> animation;
		private Map<CssPropertyKey, AnimatedProperty<?>> overridenProperties = new HashMap<CssPropertyKey, AnimatedProperty<?>>();
		
		public Animation( CssAnimation<?> animation ){
			this.animation = animation;
		}
		
		@Override
		public CssScheme getScheme(){
			return getLink().getChain().getScheme();
		}
		
		public CssItem getItem(){
			return getLink().getChain().getItem();
		}

		@Override
		public void step( AnimationScheduler scheduler, int delay ){
			if( animation != null ){	
				animation.step( delay );
			}
		}
		
		@Override
		public <T> CssPropertyKey[] getPropertiesOfType( CssType<T> type ){
			CssItem item = getItem();
			List<CssPropertyKey> result = new ArrayList<CssPropertyKey>();
			for( String key : item.getPropertyKeys() ){
				if( item.getProperty( key ).getType( getScheme() ).equals( type )){
					result.add( new CssPropertyKey( key ));
				}
			}
			return result.toArray( new CssPropertyKey[ result.size() ] );
		}

		@Override
		public <T> void setProperty( CssType<T> type, CssPropertyKey key, T value ){
			overridenProperties.put( key, new AnimatedProperty<T>( type, value ) );
			fireChanged( key );
		}
		
		@Override
		public <T> T getProperty( CssType<T> type, CssPropertyKey key ){
			return DefaultAnimatedCssRule.this.getProperty( type, key );
		}

		@Override
		public void step(){
			getLink().getChain().getScheme().getScheduler().step( this );
		}

		@Override
		public void step( int delay ){
			getLink().getChain().getScheme().getScheduler().step( this, delay );
		}
		
		@Override
		public void destroyed(){
			animations.remove( this );
			animation = null;
			for( CssPropertyKey key : overridenProperties.keySet() ){
				fireChanged( key );
			}
			if( animations.isEmpty() ){
				RuleChainLink link = getLink();
				if( link != null ){
					link.remove();
				}
			}
		}
	}
}
