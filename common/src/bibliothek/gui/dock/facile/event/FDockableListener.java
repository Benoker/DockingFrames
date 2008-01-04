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
package bibliothek.gui.dock.facile.event;

import bibliothek.gui.dock.facile.intern.FDockable;

/**
 * A listener added to a {@link FDockable}, this listener will get informed
 * about changes of {@link FDockable}.
 * @author Benjamin Sigg
 *
 */
public interface FDockableListener {
    /**
     * Called when the {@link FDockable#isCloseable() closeable}-property has 
     * changed.
     * @param dockable the source of the event
     */
    public void closeableChanged( FDockable dockable );
    
    /**
     * Called when the {@link FDockable#isMinimizable() minimizable}-property
     * has changed.
     * @param dockable the source of the event
     */
    public void minimizableChanged( FDockable dockable );
    
    /**
     * Called when the {@link FDockable#isMaximizable() maximizable}-property
     * has changed.
     * @param dockable the source of the event
     */
    public void maximizableChanged( FDockable dockable );
    
    /**
     * Called when the {@link FDockable#isExternalizable() externalizable}-property
     * has changed.
     * @param dockable the source of the event
     */
    public void externalizableChanged( FDockable dockable );
    
    /**
     * Called when the {@link FDockable#isVisible() visibility}-property
     * has changed.
     * @param dockable the source of the event
     */
    public void visibilityChanged( FDockable dockable );
    
    /**
     * Called when the <code>dockable</code> has been minimized.
     * @param dockable the source of the event
     */
    public void minimized( FDockable dockable );
    
    /**
     * Called when the <code>dockable</code> has been maximized.
     * @param dockable the source of the event
     */
    public void maximized( FDockable dockable );
    
    /**
     * Called when the <code>dockable</code> has been normalized.
     * @param dockable the source of the event
     */
    public void normalized( FDockable dockable );
    
    /**
     * Called when the <code>dockable</code> has been externalized.
     * @param dockable the source of the event
     */
    public void externalized( FDockable dockable );
}
