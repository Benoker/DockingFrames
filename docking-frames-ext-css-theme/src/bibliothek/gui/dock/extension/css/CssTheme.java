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
package bibliothek.gui.dock.extension.css;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.extension.css.intern.CssParser;
import bibliothek.gui.dock.extension.css.theme.CssDockTitleFactory;
import bibliothek.gui.dock.extension.css.tree.CssTree;
import bibliothek.gui.dock.themes.BasicTheme;

/**
 * The {@link CssTheme} makes use of files with a CSS-like syntax for defining how the different
 * elements of the framework look like.
 * @author Benjamin Sigg
 */
public class CssTheme extends BasicTheme{
	private CssTree tree;
	private CssScheme scheme = new CssScheme();
	
	/**
	 * Sets up a new theme
	 */
	public CssTheme(){
		setTitleFactory( new CssDockTitleFactory( scheme ) );
	}
	
	/**
	 * Gets the set of {@link CssRule}s that are currently used.
	 * @return the set of rules, not <code>null</code>
	 */
	public CssScheme getScheme(){
		return scheme;
	}
	
	@Override
	protected void install( DockController controller ){
		if( getController() != null ){
			throw new IllegalStateException( "Theme is already in use" );
		}
		tree = new CssTree( controller );
		scheme.setTree( tree );
		super.install( controller );
	}
	
	@Override
	public void uninstall( DockController controller ){
		super.uninstall( controller );
		scheme.setTree( null );
	}
	
	/**
	 * Reads a css-file, any existing {@link CssRule}s are deleted.
	 * @param file the file to read
	 * @throws IOException if the file cannot be read
	 */
	public void read( File file ) throws IOException{
		FileReader reader = new FileReader( file );
		try{
			read( reader, true );
		}
		finally{
			reader.close();
		}
	}
	
	/**
	 * Reads a css-file.
	 * @param reader the file to read
	 * @param discard whether existing {@link CssRule}s are to be deleted
	 * @throws IOException if the file cannot be read
	 */
	public void read( Reader reader, boolean discard ) throws IOException{
		CssParser parser = new CssParser();
		List<CssRule> rules = parser.parse( reader );
		if( discard ){
			scheme.setRules( rules );
		}
		else{
			scheme.addRules( rules );
		}
	}
}
