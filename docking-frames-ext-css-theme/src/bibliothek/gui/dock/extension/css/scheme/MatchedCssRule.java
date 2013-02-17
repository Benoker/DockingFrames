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
package bibliothek.gui.dock.extension.css.scheme;

import bibliothek.gui.dock.extension.css.CssItem;
import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainer;
import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssRuleContent;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.transition.TransitionalCssRuleContent;

/**
 * A {@link MatchedCssRule} is responsible for reading the properties of one {@link CssRule}
 * and forward them to a {@link CssItem}. Due to transitions several {@link CssRule}s may be active
 * at the same time, in this case this {@link MatchedCssRule} is marked as {@link #outdate()}. Only
 * {@link CssProperty}s which are considered {@link #isInput(CssPropertyKey) input properties} remain
 * active. Input properties usually are attached to a {@link CssPropertyContainer} which got involved
 * in an transition, yet itself is not a {@link CssProperty} nor a {@link CssItem}.
 * @author Benjamin Sigg
 */
public class MatchedCssRule {
	private static enum Mode{
		NEW, HEAD, OUTDATED, DESTROYED
	}
	
	/** the values of all the properties */
	private TransitionalCssRuleContent rule;
	
	/** the behavior of this {@link MatchedCssRule} */
	private Mode mode = Mode.NEW;
	
	private Forwarder forwarder;
	
	/**
	 * Creates a new match
	 * @param scheme the scheme in whose realm this rule is used
	 * @param item the item whose properties are set
	 * @param rule the rule from which to read properties, can be <code>null</code>
	 */
	public MatchedCssRule( CssScheme scheme, CssItem item, TransitionalCssRuleContent rule ){
		this.rule = rule;
		forwarder = new Forwarder( rule, item, scheme );
	}
	
	/**
	 * Install <code>this</code>, starts monitoring all {@link CssProperty}s of the {@link CssItem}.
	 * @param firstRule whether this is the first rule for the {@link CssItem}, the first rule also
	 * call {@link CssProperty#setScheme(CssScheme, String)}
	 */
	public void install( boolean firstRule ){
		if( mode != Mode.NEW ){
			throw new IllegalStateException( "already installed" );
		}
		mode = Mode.HEAD;
		forwarder.install( firstRule );
	}
	
	private boolean isInput( CssPropertyKey key ){
		if( rule == null ){
			return false;
		}
		return rule.isInput( key );
	}
	
	/**
	 * Informs this {@link MatchedCssRule} that it is no longer working with the most recent
	 * {@link CssRule}. The current {@link CssProperty}s will be copied and kept alive until
	 * {@link #destroy()} is called. 
	 */
	public void outdate(){
		if( mode != Mode.DESTROYED ){
			if( mode == Mode.HEAD ){
				forwarder.outdate();
			}
			mode = Mode.OUTDATED;
		}
	}
	
	/**
	 * Informs this {@link MatchedCssRule} that it is no longer in use, all resources are released.
	 */
	public void destroy(){
		if( mode != Mode.DESTROYED ){
			outdate();
			mode = Mode.DESTROYED;
			forwarder.destroy();
		}
	}
	
	private class Forwarder extends PropertyForwarder{
		public Forwarder( CssRuleContent source, CssPropertyContainer target, CssScheme scheme ){
			super( source, target, scheme );
		}
		
		@Override
		protected <T> void addProperty( CssPropertyKey key, CssProperty<T> property, boolean firstRule ){
			if( mode == Mode.OUTDATED && !isInput(key) ){
				throw new IllegalStateException( "attempt to register a static property after rule has been outdated" );
			}
			super.addProperty( key, property, firstRule );
		}
		
		public void outdate(){
			// remove all static properties, keep dynamic properties until destruction
			ignoreTarget();
			
			CssPropertyKey[] keys = getKeys();
			for( CssPropertyKey key : keys ){
				CssProperty<?> property = getProperty( key );
				if( !isInput(key) ){
					removeProperty( key, property, false );
				}
			}
		}
	}
}
