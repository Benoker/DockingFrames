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
package bibliothek.gui.dock.util.color;

import java.awt.Color;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.UIProperties;

/**
 * A {@link ColorManager} contains {@link Color}s, {@link ColorBridge}s and
 * {@link DockColor}s. Some <code>DockColor</code>s are associated with a 
 * <code>ColorBridge</code>. If a <code>Color</code> in this manager is
 * {@link UIProperties#put(Priority, String, Object) set}, then each <code>DockColor</code>
 * that listens for that color gets informed about the change.
 * @author Benjamin Sigg
 */
public class ColorManager extends UIProperties<Color, DockColor, ColorBridge>{
	/**
	 * Creates a new manager.
	 * @param controller the controller in whose realm this map is used
	 */
	public ColorManager( DockController controller ){
		super( controller );
	}
}
