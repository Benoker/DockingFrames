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
package bibliothek.gui.dock.frontend;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.layout.DockLayoutComposition;
import bibliothek.gui.dock.layout.DockableProperty;

/**
 * A bag of information about a {@link Dockable} that might be registered
 * at a {@link DockFrontend}.
 * @author Benjamin Sigg
 */
public interface FrontendEntry {
    /**
     * Sets whether the dockable of this entry should be shown or not. Note that
     * this method will not change any state of the dockable, it only
     * stores a hint.
     * @param shown whether the element should be shown
     */
    public void setShown( boolean shown );
    
    /**
     * Tells whether this infos dockable should be shown. Note that this
     * is only a hint, not the actual state.
     * @return whether this element should be shown
     */
    public boolean isShown();
    
    /**
     * If set, then every entry {@link Setting} can change the layout
     * of this element.
     * @return <code>true</code> if the layout of this element should be
     * stored always.
     */
    public boolean isEntryLayout();
    
    /**
     * If set, then every entry {@link Setting} can change the layout
     * of this element.
     * @param entryLayout if the layout of this element should be
     * stored always.
     */
    public void setEntryLayout( boolean entryLayout );
    
    /**
     * Tells whether to show a "close"-action for the {@link #getDockable() dockable}
     * or not.
     * @return <code>true</code> if the element can be made invisible.
     */
    public boolean isHideable();
    
    /**
     * Sets whether {@link #getDockable() the element} can be made
     * invisible or not. This method will have an immediate effect.
     * @param hideable the new state
     */
    public void setHideable( boolean hideable );
    
    /**
     * The element for which this object stores information.
     * @return the element, can be <code>null</code>
     */
    public Dockable getDockable();
    
    /**
     * The name which is used for this object.
     * @return the name
     */
    public String getKey();
    
    /**
     * Sets the location of {@link #getDockable() the element}. Note that this
     * method will have no effect if the dockable is currently shown.
     * @param root the root, might be <code>null</code>
     * @param location the location, might be <code>null</code>
     */
    public void setLocation( String root, DockableProperty location );
    
    /**
     * Gets the name of the station on which {@link #getDockable() the element}
     * was the last time when it was made invisible.
     * @return the name or <code>null</code>
     */
    public String getRoot();
    
    /**
     * Gets the location of {@link #getDockable() the element} which it had
     * the last time it was made invisible.
     * @return the location or <code>null</code>
     */
    public DockableProperty getLocation();
    
    /**
     * Sets information about the layout of this element. Note: this is only
     * a hint and might only be applied if the dockable of this entry is currently
     * missing and added later.
     * @param layout the layout, can be <code>null</code>
     */
    public void setLayout( DockLayoutComposition layout );
    
    /**
     * Gets information about the layout of this element.
     * @return the information, might be <code>null</code>
     */
    public DockLayoutComposition getLayout();
    
    /**
     * Updates the values of {@link #getRoot() root} and {@link #getLocation() location}
     * according to the current location of {@link #getDockable() the element}.
     */
    public void updateLocation();
}
