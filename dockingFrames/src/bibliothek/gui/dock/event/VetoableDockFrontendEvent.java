/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.event;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;

/**
 * Event that is received by a {@link VetoableDockFrontendListener}.
 * @author Benjamin Sigg
 *
 */
public class VetoableDockFrontendEvent {
    private DockFrontend frontend;
    private Dockable dockable;
    
    private boolean cancelable;
    private boolean canceled = false;
    private boolean expected;
    
    /**
     * Creates a new event
     * @param frontend the source of the event
     * @param dockable the element which will be or is hidden
     * @param cancelable whether the operation can be aborted
     * @param expected whether the event is expected or unexpected
     */
    public VetoableDockFrontendEvent( DockFrontend frontend, Dockable dockable, boolean cancelable, boolean expected ){
        this.frontend = frontend;
        this.dockable = dockable;
        this.cancelable = cancelable;
        this.expected = expected;
    }
    
    /**
     * Gets the source of the event.
     * @return the source, never <code>null</code>
     */
    public DockFrontend getFrontend() {
        return frontend;
    }
    
    /**
     * Gets the element which will be or is hidden.
     * @return the element, never <code>null</code>
     */
    public Dockable getDockable() {
        return dockable;
    }
    
    /**
     * Tells whether the operation can be canceled or not. If not,
     * then the result of {@link VetoableDockFrontendListener#hiding(VetoableDockFrontendEvent)}
     * will be ignored.
     * @return <code>true</code> if the operation can be stopped
     */
    public boolean isCancelable() {
        return cancelable;
    }
    
    /**
     * Aborts the operation. Has no effect if {@link #isCancelable()}
     * returns <code>false</code> or the operation is already executed.
     */
    public void cancel(){
        if( cancelable ){
            canceled = true;
        }
    }
    
    /**
     * Whether the operation is aborted or not.
     * @return <code>true</code> if the operation is aborted
     */
    public boolean isCanceled() {
        return canceled;
    }
    
    /**
     * Tells whether {@link VetoableDockFrontendListener#hiding(VetoableDockFrontendEvent)}
     * or {@link VetoableDockFrontendListener#showing(VetoableDockFrontendEvent)}
     * was called for this event or not.<br>
     * If <code>true</code> then this is a standard expected event that either happens
     * when the user clicks onto the close-action delivered by {@link DockFrontend},
     * or if the client calls {@link DockFrontend#hide(Dockable, boolean)} or
     * {@link DockFrontend#show(Dockable, boolean)}.<br>
     * If <code>false</code> then this is an unexpected event that can have
     * any cause, i.e. loading a new layout. 
     * @return whether the event is expected or unexpected 
     */
    public boolean isExpected() {
        return expected;
    }
}
