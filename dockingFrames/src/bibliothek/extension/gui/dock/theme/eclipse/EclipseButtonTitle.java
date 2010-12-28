/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicButtonDockTitle;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * This title is used by the {@link EclipseTheme} to replace the default
 * {@link DockTitle} for the {@link FlapDockStation}.
 * @author Benjamin Sigg
 */
public class EclipseButtonTitle extends BasicButtonDockTitle{

	public EclipseButtonTitle( Dockable dockable, DockTitleVersion origin ){
		super( dockable, origin );
	}

	@Override
	public void setOrientation( Orientation orientation ){
		super.setOrientation( orientation );
		changeBorder();
	}
	
	@Override
	protected void changeBorder(){
		int flags;
		
		EmptyBorder empty;
		
		if( getOrientation().isHorizontal() ){
			flags = EclipseButtonBorder.TOP_RIGHT | EclipseButtonBorder.BOTTOM_RIGHT;
			empty = new EmptyBorder( 2, 2, 2, 4 );
		}
		else{
			flags = EclipseButtonBorder.BOTTOM_LEFT | EclipseButtonBorder.BOTTOM_RIGHT;
			empty = new EmptyBorder( 2, 2, 4, 2 );
		}
		
		Border border = new EclipseButtonBorder( getOrigin().getController(), true, flags );
		
		setBorder( ThemeManager.BORDER_MODIFIER + ".title.eclipse.button.flat", new CompoundBorder( border, empty ) );
	}
}
