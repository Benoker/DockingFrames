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

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainerListener;
import bibliothek.gui.dock.extension.css.property.IntegerCssProperty;
import bibliothek.gui.dock.util.font.FontModifier;
import bibliothek.gui.dock.util.font.GenericFontModifier;
import bibliothek.gui.dock.util.font.GenericFontModifier.Modify;

/**
 * This {@link CssFontModifier} makes use of a {@link GenericFontModifier} to change size, italic and boldness of a font.
 * @author Benjamin Sigg
 */
public class GenericCssFontModifier implements CssFontModifier {
	/** all observers of this modifier */
	private List<CssFontModifierListener> listeners = new ArrayList<CssFontModifierListener>( 2 );
	
	/** the actual algorithm */
	private GenericFontModifier delegate = new GenericFontModifier();
	
	/** default value for {@link #italic} */
	private Modify defaultItalic = Modify.IGNORE;
	
	/** whether the font should be italic */
	private FontModifyCssProperty italic = new FontModifyCssProperty(){
		@Override
		public void set( Modify value ){
			if( value == null ){
				value = defaultItalic;
			}
			delegate.setItalic( value );
			fireEvent();
		}
	};
	
	/** default value for {@link #bold} */
	private Modify defaultBold = Modify.IGNORE; 
	
	/** whether the font should be bold */
	private FontModifyCssProperty bold = new FontModifyCssProperty(){
		@Override
		public void set( Modify value ){
			if( value == null ){
				value = defaultBold;
			}
			delegate.setBold( value );
			fireEvent();
		}
	};
	
	/** the default value of {@link #size} */
	private int defaultSize = -1;
	
	/** the latest value of {@link #size} */
	private int sizeCache = -1;
	
	/** the size of the font */
	private IntegerCssProperty size = new IntegerCssProperty(){
		@Override
		public void set( Integer value ){
			if( value == null ){
				value = defaultSize;
			}
			resize( value, deltaCache );
			fireEvent();
		}
	};
	
	/** the default value of {@link #delta} */
	private int defaultDelta = 0;
	
	/** the latest value of {@link #delta} */
	private int deltaCache = -1;
	
	/** the delta of the size */
	private IntegerCssProperty delta = new IntegerCssProperty(){
		@Override
		public void set( Integer value ){
			if( value == null ){
				value = defaultDelta;
			}
			resize( sizeCache, value );
			fireEvent();
		}		
	};
	
	private void resize( int size, int delta ){
		sizeCache = size;
		deltaCache = delta;
		
		if( size <= 0 ){
			delegate.setSizeDelta( true );
			delegate.setSize( delta );
		}
		else{
			delegate.setSizeDelta( false );
			delegate.setSize( size + delta );
		}
	}
	
	@Override
	public FontModifier getModifier(){
		GenericFontModifier copy = new GenericFontModifier();
		copy.setBold( delegate.getBold() );
		copy.setItalic( delegate.getItalic() );
		copy.setSize( delegate.getSize() );
		copy.setSizeDelta( delegate.isSizeDelta() );
		return copy;
	}
	
	@Override
	public void addFontModifierListener( CssFontModifierListener listener ){
		listeners.add( listener );	
	}
	
	@Override
	public void removeFontModifierListener( CssFontModifierListener listener ){
		listeners.remove( listener );
	}
	
	private void fireEvent(){
		for( CssFontModifierListener listener : listeners ){
			listener.modifierChanged( this );
		}
	}
	
	/**
	 * Gets the behavior of the italic parameter.
	 * @return whether the font is italic or not
	 */
	public Modify getItalic(){
		return delegate.getItalic();
	}
	
	/**
	 * Gets the behavior of the bold parameter.
	 * @return whether the font is bold or not
	 */
	public Modify getBold(){
		return delegate.getBold();
	}
	
	/**
	 * Gets the current size of the font, without delta.
	 * @return the size, or -1 is not set
	 */
	public int getSize(){
		return sizeCache;
	}
	
	/**
	 * Gets the current delta in size of the font.
	 * @return the delta
	 */
	public int getDelta(){
		return deltaCache;
	}

	@Override
	public String[] getPropertyKeys(){
		return new String[]{ "size", "delta", "italic", "bold" };
	}

	@Override
	public CssProperty<?> getProperty( String key ){
		if( "size".equals( key )){
			return size;
		}
		if( "delta".equals( key )){
			return delta;
		}
		if( "italic".equals( key )){
			return italic;
		}
		if( "bold".equals( key )){
			return bold;
		}
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
}
