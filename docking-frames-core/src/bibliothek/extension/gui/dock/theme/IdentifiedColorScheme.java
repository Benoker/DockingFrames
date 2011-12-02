/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme;

import java.awt.Color;
import java.util.Collection;
import java.util.Set;

import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.themes.color.AbstractColorScheme;
import bibliothek.gui.dock.util.UIProperties;
import bibliothek.gui.dock.util.UIScheme;
import bibliothek.gui.dock.util.UISchemeEvent;
import bibliothek.gui.dock.util.UISchemeListener;
import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.util.Path;

/**
 * A {@link ColorScheme} that is wrapped around another scheme. This scheme
 * contains a unmodifiable {@link String} which is used for {@link #equals(Object)}
 * and for {@link #hashCode()}.
 * @author Benjamin Sigg
 */
public class IdentifiedColorScheme extends AbstractColorScheme{
	/** the unique id of this scheme */
	private String id;
	/** the real scheme */
	private ColorScheme delegate;
	
	private UISchemeListener<Color, DockColor, ColorBridge> delegateListener = new UISchemeListener<Color, DockColor, ColorBridge>(){
		public void changed( final UISchemeEvent<Color, DockColor, ColorBridge> event ){
			fire( new UISchemeEvent<Color, DockColor, ColorBridge>(){
				public Collection<Path> changedBridges( Set<Path> names ){
					return event.changedBridges( names );
				}
				public Collection<String> changedResources( Set<String> names ){
					return event.changedResources( names );
				}
				public UIScheme<Color, DockColor, ColorBridge> getScheme(){
					return IdentifiedColorScheme.this;
				}
			});
		}
	};
	
	/**
	 * Creates a new scheme.
	 * @param id the unique identifier of this scheme
	 * @param delegate the source for any value of this scheme
	 * @throws IllegalArgumentException if either <code>id</code> or
	 * <code>delegate</code> is <code>null</code>
	 */
	public IdentifiedColorScheme( String id, ColorScheme delegate ){
		if( id == null )
			throw new IllegalArgumentException( "id must not be null" );
		
		if( delegate == null )
			throw new IllegalArgumentException( "delegate must not be null" );
		
		this.id = id;
		this.delegate = delegate;
	}
	
	@Override
	public void addListener( UISchemeListener<Color, DockColor, ColorBridge> listener ){
		boolean has = hasListeners();
		super.addListener( listener );
		if( !has ){
			delegate.addListener( delegateListener );
		}
	}
	
	@Override
	public void removeListener( UISchemeListener<Color, DockColor, ColorBridge> listener ){
		super.removeListener( listener );
		if( !hasListeners() ){
			delegate.removeListener( delegateListener );
		}
	}
	
	@Override
	public void install( UIProperties<Color, DockColor, ColorBridge> properties ){
		super.install( properties );
		delegate.uninstall( properties );
	}
	
	@Override
	public void uninstall( UIProperties<Color, DockColor, ColorBridge> properties ){
		super.uninstall( properties );
		delegate.uninstall( properties );
	}
	
	@Override
	protected void updateUI(){
		// ignore
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals( Object obj ) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final IdentifiedColorScheme other = (IdentifiedColorScheme) obj;
		return id.equals( other.id );
	}

	public ColorBridge getBridge( Path name, UIProperties<Color, DockColor, ColorBridge> properties ){
		return delegate.getBridge( name, properties );
	}
	
	public Color getResource( String name, UIProperties<Color, DockColor, ColorBridge> properties ){
		return delegate.getResource( name, properties );
	}
}
