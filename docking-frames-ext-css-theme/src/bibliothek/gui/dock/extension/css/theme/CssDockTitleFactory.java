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

import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;

/**
 * A factory to create new {@link CssDockTitle}s.
 * @author Benjamin Sigg
 */
public class CssDockTitleFactory implements DockTitleFactory{
	private CssScheme scheme;
	
	/**
	 * Creates a new factory.
	 * @param scheme the set of {@link CssRule}s
	 */
	public CssDockTitleFactory( CssScheme scheme ){
		this.scheme = scheme;
	}
	
	@Override
	public void request( DockTitleRequest request ){
		CssDockTitle title = new CssDockTitle( scheme, request.getTarget(), request.getVersion() );
		request.answer( title );
	}
	
	@Override
	public void install( DockTitleRequest request ){
		// ignore
	}

	@Override
	public void uninstall( DockTitleRequest request ){
		// ignore
	}

}
