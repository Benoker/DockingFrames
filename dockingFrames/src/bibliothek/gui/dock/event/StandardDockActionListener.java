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


package bibliothek.gui.dock.event;

import java.util.Set;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.StandardDockAction;

/**
 * This listener is added to a {@link StandardDockAction}. It receives events whenever
 * the look of the {@link StandardDockAction} changes.
 * @author Benjamin Sigg
 */
public interface StandardDockActionListener {
    /**
     * Invoked when the text of a {@link StandardDockAction} has been changed.
     * @param action The action whose text is changed
     * @param dockables The {@link Dockable Dockables} for which the text
     * was changed
     * @see StandardDockAction#getText(Dockable)
     */
    public void actionTextChanged( StandardDockAction action, Set<Dockable> dockables );
    
    /**
     * Invoked when the tooltip of a {@link StandardDockAction} has been changed.
     * @param action The action whose tooltip is changed
     * @param dockables The {@link Dockable Dockables} for which the tooltip
     * was changed
     * @see StandardDockAction#getTooltipText(Dockable)
     */
    public void actionTooltipTextChanged( StandardDockAction action, Set<Dockable> dockables );
    
    /**
     * Invoked when the icon of a {@link StandardDockAction} has been changed.
     * @param action The action whose icon is changed
     * @param dockables The {@link Dockable Dockables} for which the icon
     * was changed
     * @see StandardDockAction#getIcon(Dockable)
     */
    public void actionIconChanged( StandardDockAction action, Set<Dockable> dockables );

    /**
     * Invoked when the disabled icon of a {@link StandardDockAction} has been changed.
     * @param action The action whose icon is changed
     * @param dockables The {@link Dockable Dockables} for which the icon
     * was changed
     * @see StandardDockAction#getIcon(Dockable)
     */
    public void actionDisabledIconChanged( StandardDockAction action, Set<Dockable> dockables );
    
    /**
     * Invoked when the enabled-state of a {@link StandardDockAction} has been changed.
     * @param action The action whose state is changed
     * @param dockables The {@link Dockable Dockables} for which the state
     * was changed
     * @see StandardDockAction#isEnabled(Dockable)
     */
    public void actionEnabledChanged( StandardDockAction action, Set<Dockable> dockables );
}
