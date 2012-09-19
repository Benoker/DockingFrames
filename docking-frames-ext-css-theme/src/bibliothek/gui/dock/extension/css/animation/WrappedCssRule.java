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

import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssRuleListener;
import bibliothek.gui.dock.extension.css.CssSelector;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.intern.DefaultCssSelector;

/**
 * A wrapper around a {@link CssRule}, allows to set a {@link CssRule} where replacing the rule
 * is not possible yet necessary.
 * @author Benjamin Sigg
 */
public class WrappedCssRule implements CssRule{
	private CssRule rule;
	private List<CssRuleListener> listeners = new ArrayList<CssRuleListener>();
	
	private CssRuleListener forwardListener = new CssRuleListener(){
		@Override
		public void selectorChanged( CssRule source ){
			for( CssRuleListener listener : listeners() ){
				listener.selectorChanged( WrappedCssRule.this );
			}
		}
		
		@Override
		public void propertyChanged( CssRule source, String key ){
			for( CssRuleListener listener : listeners() ){
				listener.propertyChanged( WrappedCssRule.this, key );
			}
		}
		
		@Override
		public void propertiesChanged( CssRule source ){
			for( CssRuleListener listener : listeners() ){
				listener.propertiesChanged( WrappedCssRule.this );
			}
		}
	};
	
	/**
	 * Creates a new rule
	 * @param rule the rule from which to read properties, can be <code>null</code>
	 */
	public WrappedCssRule( CssRule rule ){
		setRule( rule );
	}
	
	/**
	 * Sets the rule whose properties should be forwarded.
	 * @param rule the rule, can be <code>null</code>
	 */
	public void setRule( CssRule rule ){
		if( this.rule != null && !listeners.isEmpty() ){
			this.rule.removeRuleListener( forwardListener );
		}
		this.rule = rule;
		if( this.rule != null && !listeners.isEmpty() ){
			this.rule.addRuleListener( forwardListener );
			for( CssRuleListener listener : listeners() ){
				listener.selectorChanged( this );
				listener.propertiesChanged( this );
			}
		}
	}
	
	@Override
	public CssSelector getSelector(){
		if( rule == null ){
			return DefaultCssSelector.selector().build();
		}
		return rule.getSelector();
	}

	@Override
	public <T> T getProperty( CssType<T> type, String property ){
		if( rule == null ){
			return null;
		}
		else{
			return rule.getProperty( type, property );
		}
	}
	
	private CssRuleListener[] listeners(){
		return listeners.toArray( new CssRuleListener[ listeners.size() ] );
	}

	@Override
	public void addRuleListener( CssRuleListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		if( listeners.isEmpty() ){
			if( rule != null ){
				rule.addRuleListener( forwardListener );
			}
		}
		listeners.add( listener );
	}

	@Override
	public void removeRuleListener( CssRuleListener listener ){
		listeners.remove( listener );
		if( listeners.isEmpty() ){
			if( rule != null ){
				rule.removeRuleListener( forwardListener );
			}
		}
	}
}
