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
package glass.eclipse.theme.icon;

import javax.swing.*;
import bibliothek.gui.dock.station.stack.tab.*;
import bibliothek.gui.dock.themes.icon.*;


/**
 * Specialized icon bridge which returns a different styled icon for
 * tab overflow menus.
 * @author cthilber
 *
 */
public class CTabMenuOverflowIconBridge extends TabMenuOverflowIconBridge {
   /**
    * Returns an icon that represents <code>menu</code> in its current state. This method is called
    * every time when the number of children of <code>menu</code> changes.
    * @param menu the menu for which an icon is required
    * @return the icon, can (but should not) be <code>null</code>
    */
   @Override
   protected Icon createIcon (TabMenu menu) {
      return new CTabMenuOverflowIcon(menu.getDockableCount());
   }
}
