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
package bibliothek.gui.dock.extension.css.property;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainer;
import bibliothek.gui.dock.extension.css.CssPropertyContainerListener;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.paint.CssPaint;

/**
 * Allows access to a {@link CssPaint}.
 * @author Benjamin Sigg
 */
public abstract class PaintCssProperty extends AbstractCssPropertyContainer implements CssProperty<CssPaint>{
	private CssPaint paint;
	private CssPropertyContainerListener paintListener = new CssPropertyContainerListener(){
		@Override
		public void propertyRemoved( CssPropertyContainer source, String key, CssProperty<?> property ){
			firePropertyRemoved( key, property );
		}
		
		@Override
		public void propertyAdded( CssPropertyContainer source, String key, CssProperty<?> property ){
			firePropertyAdded( key, property );
		}
	};
	
	@Override
	public String[] getPropertyKeys(){
		if( paint == null ){
			return new String[]{};
		}
		else{
			return paint.getPropertyKeys();
		}
	}

	@Override
	public CssProperty<?> getProperty( String key ){
		if( paint == null ){
			return null;
		}
		else{
			return paint.getProperty( key );
		}
	}

	@Override
	public final void set( CssPaint value ){
		if( this.paint != value ){
			if( isBound() && this.paint != null ){
				for( String key : getPropertyKeys() ){
					firePropertyRemoved( key, getProperty( key ) );
				}
			}
			this.paint = value;
			if( isBound() && this.paint != null ){
				for( String key : getPropertyKeys() ){
					firePropertyAdded( key, getProperty( key ) );
				}
			}
			paintChanged( this.paint );
		}
	}
	
	/**
	 * Called if the paint algorithm changed.
	 * @param paint the new paint algorithm, can be <code>null</code>
	 */
	protected abstract void paintChanged( CssPaint paint );

	@Override
	public CssType<CssPaint> getType( CssScheme scheme ){
		return scheme.getConverter( CssPaint.class );
	}
	
	@Override
	protected void bind(){
		if( paint != null ){
			paint.addPropertyContainerListener( paintListener );
		}
	}

	@Override
	protected void unbind(){
		if( paint != null ){
			paint.removePropertyContainerListener( paintListener );
		}
	}
}

