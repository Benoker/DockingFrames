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
package bibliothek.gui.dock.support.lookandfeel;

import java.awt.Component;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;

/**
 * A {@link ComponentCollector} is used by the {@link LookAndFeelList} when
 * the {@link LookAndFeel} was changed, and the {@link JComponent}s need to
 * be updated. This <code>ComponentCollector</code> has to give the 
 * {@link LookAndFeelList} the roots of some <code>Component</code>-trees. 
 * @author Benjamin Sigg
 *
 */
public interface ComponentCollector {
    /**
     * Gets a set of roots of {@link Component}-trees in order to
     * {@link JComponent#updateUI() update} the look and feel of the
     * <code>Component</code>s. 
     * @return the roots
     */
	public Collection<Component> listComponents();
}
