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
package bibliothek.gui.dock.extension.css.theme;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.extension.css.CssPath;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.path.DefaultCssNode;
import bibliothek.gui.dock.extension.css.path.DefaultCssPath;
import bibliothek.gui.dock.extension.css.path.MultiCssPath;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * This title makes use of a {@link CssScheme} to set up its look.
 * @author Benjamin Sigg
 */
public class CssDockTitle extends AbstractDockTitle{
	private CssScheme css;
	
	private DefaultCssNode self;
	private CssPath selfPath;
	
	/**
	 * Creates a new title
	 * @param css access to all the css data
	 * @param dockable the dockable whose title this is
	 * @param origin the version which was used to create this title
	 */
	public CssDockTitle( CssScheme css, Dockable dockable, DockTitleVersion origin ){
		super( dockable, origin, true );
		this.css = css;
		self = new DefaultCssNode( "title" );
	}
	
	@Override
	public void bind(){
		super.bind();
		CssPath elementPath = css.getTree().getPathFor( getElement() );
		selfPath = new MultiCssPath( elementPath, new DefaultCssPath( self ) );
	}
	
	@Override
	public void unbind(){
		super.unbind();
	}
	
	@Override
	public void setActive( boolean active ){
		super.setActive( active );
		if( active ){
			self.addPseudoClass( "selected" );
		}
		else{
			self.removePseudoClass( "selected" );
		}
	}	
	
}
