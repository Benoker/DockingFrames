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
package bibliothek.gui.dock.focus;

import java.awt.Component;
import java.awt.Container;

/**
 * An interface which mimics the behavior of {@link java.awt.FocusTraversalPolicy}
 * but implementing classes are not required to respect any other
 * {@link Container#isFocusCycleRoot() focus cycle roots}, 
 * {@link Container#isFocusTraversalPolicyProvider() policy providers} or 
 * visibility, focusability, displayability nor the enabled state. This
 * {@link SimplifiedFocusTraversalPolicy} will be put into a {@link DockFocusTraversalPolicy}
 * which will handle all these issues.
 * @author Benjamin Sigg
 */
public interface SimplifiedFocusTraversalPolicy {
    /**
     * Gets the {@link Component} which should be focused next.
     * @param container the root or provider of this policy
     * @param component the currently focused component
     * @return the next component or <code>null</code>
     */
    public abstract Component getAfter( Container container, Component component );

    /**
     * Gets the {@link Component} which should be focused when stepping through
     * the circle backwards.
     * @param container the root or provider of this policy
     * @param component the currently focused component
     * @return the previous component or <code>null</code>
     */
    public abstract Component getBefore( Container container, Component component );

    /**
     * Gets the {@link Component} which should be focused when entering the circle.
     * @param container the root or provider of this policy
     * @return the next component or <code>null</code>
     */
    public abstract Component getFirst( Container container );

    /**
     * Gets the {@link Component} which should be focused when entering the
     * circle backwards.
     * @param container the root or provider of this policy
     * @return the next component or <code>null</code>
     */
    public abstract Component getLast( Container container );

    /**
     * Gets the {@link Component} which should be focused per default.
     * @param container the root or provider of this policy
     * @return the default component or <code>null</code>
     */
    public abstract Component getDefault( Container container );
}
