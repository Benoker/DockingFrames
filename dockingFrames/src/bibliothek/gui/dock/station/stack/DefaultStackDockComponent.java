/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.station.stack;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTabbedPane;


/**
 * The standard-implementation of {@link StackDockComponent}. This implementation
 * uses a {@link JTabbedPane} to display its children.
 * 
 * @author Janni Kovacs
 * @see StackDockComponent
 * @see JTabbedPane
 */
public class DefaultStackDockComponent extends JTabbedPane implements StackDockComponent {
	/**
	 * Constructs the component, sets the location of the tabs to bottom.
	 */
    public DefaultStackDockComponent() {
        super(BOTTOM);
    }

    public void insertTab(String title, Icon icon, Component comp, int index) {
        insertTab(title, icon, comp, null, index);
    }

    public Component getComponent() {
        return this;
    }
}