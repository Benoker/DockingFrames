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
package bibliothek.gui.dock.extension.css.intern;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssRuleContent;
import bibliothek.gui.dock.extension.css.CssRuleContentListener;
import bibliothek.gui.dock.extension.css.CssRuleListener;
import bibliothek.gui.dock.extension.css.CssSelector;
import bibliothek.gui.dock.extension.css.CssType;

/**
 * The {@link EmptyCssRule} does never offer any {@link CssProperty}s. 
 * @author Benjamin Sigg
 */
public class EmptyCssRule implements CssRule, CssRuleContent{
	private CssSelector selector;
	
	public EmptyCssRule( CssSelector selector ){
		if( selector == null ){
			throw new IllegalArgumentException( "selector must not be null" );
		}
		this.selector = selector;
	}
	
	@Override
	public CssSelector getSelector(){
		return selector;
	}
	
	@Override
	public CssRuleContent getContent(){
		return this;
	}
	
	@Override
	public <T> T getProperty( CssType<T> type, CssPropertyKey property ){
		return null;
	}

	@Override
	public void addRuleContentListener( CssRuleContentListener listener ){
		// ignore	
	}
	
	@Override
	public void removeRuleContentListener( CssRuleContentListener listener ){
		// ignore	
	}
	
	@Override
	public void addRuleListener( CssRuleListener listener ){
		// ignore
	}

	@Override
	public void removeRuleListener( CssRuleListener listener ){
		// ignore
	}
}
