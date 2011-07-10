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
import bibliothek.gui.dock.action.actions.SharingDropDownItemAction;
import bibliothek.gui.dock.common.intern.action.CDropDownItem;
import bibliothek.util.FrameworkOnly;

/**
 * A combination of {@link SharingDropDownItemAction} and {@link CommonDecoratableDockAction}, this interface
 * does not add any new methods or fields.<br>
 * {@link DockAction}s used by the {@link CDropDownItem} must implement this interface.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public interface CommonDropDownItem extends SharingDropDownItemAction, CommonDecoratableDockAction{

}
