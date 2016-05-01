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

package bibliothek.gui.dock.themes;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;

/**
 * A factory using the {@link ThemeProperties} of a {@link DockTheme} to 
 * create instances of that <code>DockTheme</code>.
 * @param <T> the type of theme created by this factory
 * @author Benjamin Sigg
 */
public class ThemePropertyFactory<T extends DockTheme> implements ThemeFactory {
	/** Default constructor of the theme */
	private Constructor<T> constructor;
	/** Information about the theme */
	private ThemeProperties properties;

	/**
	 * Creates a new factory.
	 * @param theme the class of a theme, must have the {@link ThemeProperties} annotation.
	 */
	public ThemePropertyFactory( Class<T> theme ){
		if( theme == null )
			throw new IllegalArgumentException( "Theme must not be null" );

		properties = theme.getAnnotation( ThemeProperties.class );
		if( properties == null )
			throw new IllegalArgumentException( "Theme misses annotation ThemeProperties" );

		try {
			constructor = theme.getConstructor( new Class[0] );
		}
		catch( NoSuchMethodException e ) {
			throw new IllegalArgumentException( "Missing default constructor", e );
		}
	}

	public T create( DockController controller ){
		try {
			return constructor.newInstance( new Object[0] );
		}
		catch( Exception e ) {
			System.err.println( "Can't create theme due an unknown reason" );
			e.printStackTrace();
			return null;
		}
	}

	public ThemeMeta createMeta( DockController controller ){
		return new DefaultThemeMeta( this, controller, properties.nameBundle(), properties.descriptionBundle(), getAuthors(), getWebpages() );
	}

	public String[] getAuthors(){
		return properties.authors();
	}

	public URI[] getWebpages(){
		try {
			String[] urls = properties.webpages();
			URI[] result = new URI[urls.length];
			for( int i = 0; i < result.length; i++ )
				result[i] = new URI( urls[i] );

			return result;
		}
		catch( URISyntaxException ex ) {
			System.err.print( "Can't create urls due an unknown reason" );
			ex.printStackTrace();
			return null;
		}
	}
}
