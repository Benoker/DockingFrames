/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.property.font;

import bibliothek.gui.dock.extension.css.CssItem;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.transition.CssContainerTransitionProperty;
import bibliothek.gui.dock.util.font.FontModifier;

/**
 * A property for handling a {@link CssFontModifier} with a transition.
 * @author Benjamin Sigg
 */
public abstract class CssFontTransitionProperty extends CssContainerTransitionProperty<CssFontModifier>{
	/** listener added to the current font modifier */
	private CssFontModifierListener listener = new CssFontModifierListener(){
		@Override
		public void modifierChanged( CssFontModifier source ){
			setModifier( source.getModifier() );	
		}
	};
	
	/** the current font modifier */
	private CssFontModifier modifier;
	
	/**
	 * Creates the new property.
	 * @param scheme the scheme in whose realm this property will work
	 * @param item the item to which this property belongs
	 */
	public CssFontTransitionProperty( CssScheme scheme, CssItem item ){
		super( scheme, item );
	}
	
	@Override
	public CssType<CssFontModifier> getType( CssScheme scheme ){
		return scheme.getConverter( CssFontModifier.class );
	}
	
	@Override
	protected void propertyChanged( CssFontModifier value ){
		if( modifier != null ){
			modifier.removeFontModifierListener( listener );
		}
		modifier = value;
		if( modifier != null ){
			modifier.addFontModifierListener( listener );
			setModifier( modifier.getModifier() );
		}
		else{
			setModifier( null );
		}
	}
	
	/**
	 * Called if the {@link CssFontModifier} was exchanged or updated.
	 * @param modifier the new modifier, may be <code>null</code>
	 */
	protected abstract void setModifier( FontModifier modifier );
	
	@Override
	protected void bind(){
		// ignore
	}
	
	@Override
	protected void unbind(){
		// ignore
	}
}
