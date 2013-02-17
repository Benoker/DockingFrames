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
import bibliothek.gui.dock.extension.css.CssRuleContent;
import bibliothek.gui.dock.extension.css.CssRuleContentListener;
import bibliothek.gui.dock.extension.css.CssType;

/**
 * A wrapper around a {@link CssRuleContent}, allows to set a {@link CssRuleContent} where replacing the rule
 * is not possible yet necessary.
 * @author Benjamin Sigg
 */
public class WrappedCssRuleContent implements CssRuleContent{
	private CssRuleContent rule;
	private List<CssRuleContentListener> listeners = new ArrayList<CssRuleContentListener>();
	
	private CssRuleContentListener forwardListener = new CssRuleContentListener(){
		@Override
		public void propertyChanged( CssRuleContent source, CssPropertyKey key ){
			for( CssRuleContentListener listener : listeners() ){
				listener.propertyChanged( WrappedCssRuleContent.this, key );
			}
		}
		
		@Override
		public void propertiesChanged( CssRuleContent source ){
			for( CssRuleContentListener listener : listeners() ){
				listener.propertiesChanged( WrappedCssRuleContent.this );
			}
		}
	};
	
	/**
	 * Creates a new rule
	 * @param rule the rule from which to read properties, can be <code>null</code>
	 */
	public WrappedCssRuleContent( CssRuleContent rule ){
		setRule( rule );
	}
	
	/**
	 * Sets the rule whose properties should be forwarded.
	 * @param rule the rule, can be <code>null</code>
	 */
	public void setRule( CssRuleContent rule ){
		if( this.rule != null && !listeners.isEmpty() ){
			this.rule.removeRuleContentListener( forwardListener );
		}
		this.rule = rule;
		if( this.rule != null && !listeners.isEmpty() ){
			this.rule.addRuleContentListener( forwardListener );
			for( CssRuleContentListener listener : listeners() ){
				listener.propertiesChanged( this );
			}
		}
	}
	
	@Override
	public <T> T getProperty( CssType<T> type, CssPropertyKey property ){
		if( rule == null ){
			return null;
		}
		else{
			return rule.getProperty( type, property );
		}
	}
	
	private CssRuleContentListener[] listeners(){
		return listeners.toArray( new CssRuleContentListener[ listeners.size() ] );
	}

	@Override
	public void addRuleContentListener( CssRuleContentListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		if( listeners.isEmpty() ){
			if( rule != null ){
				rule.addRuleContentListener( forwardListener );
			}
		}
		listeners.add( listener );
	}

	@Override
	public void removeRuleContentListener( CssRuleContentListener listener ){
		listeners.remove( listener );
		if( listeners.isEmpty() ){
			if( rule != null ){
				rule.removeRuleContentListener( forwardListener );
			}
		}
	}
}
