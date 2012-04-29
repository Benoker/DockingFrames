/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station.toolbar.group;

import java.awt.Component;

import javax.swing.JScrollBar;

import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarGroupDockStation;

/**
 * This factory creates new {@link JScrollBar}s as scrollbars.
 * @author Benjamin Sigg
 */
public class SwingColumnScrollBarFactory implements ColumnScrollBarFactory{
	@Override
	public ColumnScrollBar create( ToolbarGroupDockStation station ){
		return new SwingScrollbar();
	}
	
	private static class SwingScrollbar extends JScrollBar implements ColumnScrollBar{
		@Override
		public void setValues( int required, int available ){
			getModel().setMaximum( required );
			getModel().setExtent( available );
		}

		@Override
		public Component getComponent(){
			return this;
		}

		@Override
		public void setOrientation( Orientation orientation ){
			if( orientation == Orientation.HORIZONTAL ){
				setOrientation( HORIZONTAL );
			}
			else{
				setOrientation( VERTICAL );
			}
		}		
	}
}
