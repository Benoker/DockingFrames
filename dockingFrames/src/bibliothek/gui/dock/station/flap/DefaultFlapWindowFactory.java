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
package bibliothek.gui.dock.station.flap;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JDesktopPane;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockUI;
import bibliothek.gui.dock.FlapDockStation;

/**
 * This default implementation of {@link FlapWindowFactory} creates new {@link DefaultFlapWindow}s.
 * @author Benjamin Sigg
 */
public class DefaultFlapWindowFactory implements FlapWindowFactory{
	public FlapWindow create( FlapDockStation station, ButtonPane buttonPane ){
		DefaultFlapWindow.Parent parent = null;
		
		JDesktopPane desktop = getDesktopPaneOf( station );
		
		if( desktop != null ){
			parent = new JInternalDialog( desktop, station ){
				@Override
				protected JDesktopPane getDesktopPaneOf( FlapDockStation station ){
					return DefaultFlapWindowFactory.this.getDesktopPaneOf( station );
				}
			};
		}
		else{
			Window owner = SwingUtilities.getWindowAncestor( station.getComponent() );
			
			if( owner instanceof Dialog )
				parent = new DefaultFlapWindow.DialogParent( (Dialog)owner, station );
			else if( owner instanceof Frame )
				parent = new DefaultFlapWindow.DialogParent( (Frame)owner, station );
			else
				return null;
		}
		
		return new DefaultFlapWindow( station, buttonPane, parent );
	}

	public void install( FlapDockStation station ){
		// ignore
	}

	public boolean isValid( FlapWindow window, FlapDockStation station ){
		DefaultFlapWindow defaultWindow = (DefaultFlapWindow)window;
		return defaultWindow.isWindowValid();
		
	}

	public void uninstall( FlapDockStation station ){
		// ignore
	}
	
	/**
	 * Searches the {@link JDesktopPane} which shows <code>station</code>.
	 * @param station the station whose parent is searched
	 * @return the parent or <code>null</code>
	 */
	protected JDesktopPane getDesktopPaneOf( FlapDockStation station ){
		return DockUI.getDesktopPane( station.getComponent() );
	}
}
