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
package glass.eclipse.theme;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.theme.color.CColorBridge;
import bibliothek.gui.dock.common.intern.theme.color.CColorBridgeExtension;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.util.Path;

public class GlassEclipseTabTransmitterFactory implements CColorBridgeExtension{

	public CColorBridge create( CControl control, ColorManager manager ){
		GlassEclipseTabTransmitter transmitter = new GlassEclipseTabTransmitter( manager );
		transmitter.setControl( control );
		return transmitter;
	}

	public Path getKey(){
		return TabColor.KIND_TAB_COLOR;
	}
	
}
