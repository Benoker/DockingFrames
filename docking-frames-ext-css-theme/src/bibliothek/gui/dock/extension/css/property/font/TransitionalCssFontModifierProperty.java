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

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainerListener;
import bibliothek.gui.dock.extension.css.transition.MiddleTransitionalCssProperty;
import bibliothek.gui.dock.extension.css.transition.types.AbstractTransitionalCssProperty;
import bibliothek.gui.dock.util.font.FontModifier;
import bibliothek.gui.dock.util.font.GenericFontModifier;
import bibliothek.gui.dock.util.font.GenericFontModifier.Modify;

/**
 * Converts one {@link CssFontModifier} into another. This implementation expects that only 
 * {@link GenericCssFontModifier}s exists, if it encounters another type of {@link CssFontModifier} it will fallback
 * to a behavior similar to {@link MiddleTransitionalCssProperty}.
 * @author Benjamin Sigg
 */
public class TransitionalCssFontModifierProperty extends AbstractTransitionalCssProperty<CssFontModifier>{
	@Override
	protected void update(){
		CssFontModifier source = getSource();
		CssFontModifier target = getTarget();
		
		if( (source == null || source instanceof GenericCssFontModifier ) && (target == null || target instanceof GenericCssFontModifier )){
			update( (GenericCssFontModifier)source, (GenericCssFontModifier)target );
		}
		else{
			if( getTransition() < 0.5 ){
				getCallback().set( source );
			}
			else{
				getCallback().set( target );
			}
		}
	}
	
	private void update( GenericCssFontModifier source, GenericCssFontModifier target ){
		GenericFontModifier modifier = new GenericFontModifier();
		modifier.setBold( bold( source, target ) );
		modifier.setItalic( italic( source, target ) );
		
		int size = size( source, target );
		int delta = delta( source, target );
		
		if( size <= 0 ){
			modifier.setSizeDelta( true );
			modifier.setSize( delta );
		}
		else{
			modifier.setSizeDelta( false );
			modifier.setSize( size + delta );
		}
		getCallback().set( new CombinedCssFont( modifier ) );
	}
	
	private Modify italic( GenericCssFontModifier source, GenericCssFontModifier target ){
		if( getTransition() < 0.5 ){
			if( source == null ){
				return Modify.IGNORE;
			}
			else{
				return source.getItalic();
			}
		}
		else{
			if( target == null ){
				return Modify.IGNORE;
			}
			else{
				return target.getItalic();
			}
		}
	}
	
	private Modify bold( GenericCssFontModifier source, GenericCssFontModifier target ){
		if( getTransition() < 0.5 ){
			if( source == null ){
				return Modify.IGNORE;
			}
			else{
				return source.getBold();
			}
		}
		else{
			if( target == null ){
				return Modify.IGNORE;
			}
			else{
				return target.getBold();
			}
		}
	}
	
	private int size( GenericCssFontModifier source, GenericCssFontModifier target ){
		if( source == null || target == null ){
			if( getTransition() < 0.5 ){
				if( source == null ){
					return -1;
				}
				else{
					return source.getSize();
				}
			}
			else{
				if( target == null ){
					return -1;
				}
				else{
					return target.getSize();
				}
			}
		}
		else{
			double t = getTransition();
			return (int)( (1-t) * source.getSize() + t * target.getSize() + 0.5 );
		}
	}
	
	private int delta( GenericCssFontModifier source, GenericCssFontModifier target ){
		int deltaSource = 0;
		if( source != null ){
			deltaSource = source.getDelta();
		}
		
		int deltaTarget = 0;
		if( target != null ){
			deltaTarget = target.getDelta();
		}
		
		double t = getTransition();
		return (int)( (1-t) * deltaSource + t * deltaTarget + 0.5 );
	}
	
	private class CombinedCssFont implements CssFontModifier{
		private FontModifier delegate;
		
		public CombinedCssFont( FontModifier delegate ){
			this.delegate = delegate;
		}
		
		@Override
		public String[] getPropertyKeys(){
			return new String[]{};
		}

		@Override
		public CssProperty<?> getProperty( String key ){
			return null;
		}

		@Override
		public void addPropertyContainerListener( CssPropertyContainerListener listener ){
			// ignore
		}

		@Override
		public void removePropertyContainerListener( CssPropertyContainerListener listener ){
			// ignore
		}
		
		@Override
		public FontModifier getModifier(){
			return delegate;
		}
		
		@Override
		public void addFontModifierListener( CssFontModifierListener listener ){
			// ignore
		}
		
		@Override
		public void removeFontModifierListener( CssFontModifierListener listener ){
			// ignore	
		}
	}
}
