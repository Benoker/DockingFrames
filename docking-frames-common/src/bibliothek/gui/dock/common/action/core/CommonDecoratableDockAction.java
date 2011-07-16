/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.common.action.core;

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.actions.SharingStandardDockAction;
import bibliothek.gui.dock.common.intern.action.CDecorateableAction;
import bibliothek.util.FrameworkOnly;

/**
 * The combination of a {@link CommonDockAction} and a {@link SharingStandardDockAction}, does not introduce
 * any new methods or fields.<br>
 * This is the interface any {@link DockAction} that is used by a {@link CDecorateableAction} must implement.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public interface CommonDecoratableDockAction extends CommonDockAction, SharingStandardDockAction{

}
