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

package bibliothek.gui.dock.station;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.util.Path;

/**
 * A factory that creates instances of {@link DockableDisplayer}.
 * @author Benjamin Sigg
 *
 */
public interface DisplayerFactory {
	/** unique identifier of an {@link ExtensionName} which allows extensions to insert high priority {@link DisplayerFactory}s */
	public static final Path DISPLAYER_EXTENSION = new Path( "dock.DisplayerExtension" );
	
	/**
	 * parameter for {@link #DISPLAYER_EXTENSION}, this parameter depends on the {@link DockStation} which is using the displayers,
	 * each {@link DockStation} should have a constant defined with the name "DISPLAYER_ID" or a similar name (some stations may
	 * have more than one constant).
	 */
	public static final String DISPLAYER_EXTENSION_ID = "name";
	
    /**
     * Creates a new {@link DockableDisplayer}, this method needs to call
     * {@link DisplayerRequest#answer(DockableDisplayer)} once the new displayer is created.<br>
     * The new displayer will be shown on {@link DisplayerRequest#getParent()}, its content
     * must be {@link DisplayerRequest#getTarget()} and {@link DisplayerRequest#getTitle()}.<br>
     * If this factory does not want to provide a {@link DockableDisplayer} for the given request,
     * it can just <code>return</code> and not call {@link DisplayerRequest#answer(DockableDisplayer)}.
     * @param request detailed information about who is going to show the displayer, and callback to
     * set the new displayer
     */
    public void request( DisplayerRequest request );
}
