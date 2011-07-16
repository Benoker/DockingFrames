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
package bibliothek.gui.dock.security;

import java.awt.Component;
import java.awt.event.KeyListener;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.ControllerSetupCollection;
import bibliothek.gui.dock.control.DefaultKeyboardController;
import bibliothek.gui.dock.control.KeyboardController;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Version;

/**
 * A {@link KeyboardController} which adds a {@link KeyListener} to each
 * {@link Component} that can be found on a {@link Dockable} in the realm
 * of one {@link DockController}.
 * @author Benjamin Sigg
 * @deprecated this class has now the exact same behavior as {@link DefaultKeyboardController} and
 * will be removed in a future release
 */
@Deprecated
@Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Todo.Priority.MAJOR, target=Version.VERSION_1_1_1,
		description="remove this class without replacement")
public class SecureKeyboardController extends DefaultKeyboardController {
    /**
     * Creates a new {@link SecureKeyboardController}.
     * @param controller the owner of this controller
     * @param setup an observable informing this object when <code>controller</code>
     * is set up.
     */
    public SecureKeyboardController( DockController controller, ControllerSetupCollection setup ) {
        super( controller, setup );
    }
}
