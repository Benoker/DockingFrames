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
package bibliothek.gui.dock.security;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.SwingUtilities;

import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.station.flap.ButtonPane;
import bibliothek.gui.dock.station.flap.DefaultFlapWindow;
import bibliothek.gui.dock.station.flap.DefaultFlapWindowFactory;
import bibliothek.gui.dock.station.flap.FlapWindow;

/**
 * This factory creates {@link SecureFlapWindow}s.
 * @author Benjamin Sigg
 */
public class SecureFlapWindowFactory extends DefaultFlapWindowFactory{
	public FlapWindow create( FlapDockStation station, ButtonPane buttonPane ){
		Window owner = SwingUtilities.getWindowAncestor( station.getComponent() );
		
		DefaultFlapWindow.Parent parent = null;
		
		if( owner instanceof Dialog )
			parent = new DefaultFlapWindow.DialogParent( (Dialog)owner, station );
		else if( owner instanceof Frame )
			parent = new DefaultFlapWindow.DialogParent( (Frame)owner, station );
		else
			return null;
		
		return new SecureFlapWindow( station, buttonPane, parent );
	}
}
