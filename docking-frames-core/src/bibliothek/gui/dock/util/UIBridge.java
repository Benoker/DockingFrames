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
package bibliothek.gui.dock.util;

import bibliothek.util.Path;


/**
 * An {@link UIBridge} is a filter between the {@link UIProperties} and
 * several {@link UIValue}s. Each change of an {@link UIValue} by the
 * <code>UIProperties</code> goes through an <code>UIBridge</code>. The bridge
 * can modify each request of the <code>UIProperties</code> and change
 * the resource that is transmitted to the <code>UIValue</code>. It is up to
 * the bridge how, why and when a resource is changed. Each bridge handles
 * only one kind (identified by a {@link Path}) of <code>UIValue</code>.
 * @author Benjamin Sigg
 * @param <V> the type of resources that are transmitted to the {@link UIValue}s.
 * @param <U> the kind of {@link UIValue}s this provider can manage
 */
public interface UIBridge<V, U extends UIValue<V>> {
    /**
     * Adds a new {@link UIValue} that gets its resource from this bridge.
     * @param id the id of the resource the new value needs
     * @param uiValue a value that can be modified by this bride
     */
    public void add( String id, U uiValue );
    
    /**
     * Removes a value from this bridge.
     * @param id the id of the resource the value needed
     * @param uiValue the value that is no longer in use
     */
    public void remove( String id, U uiValue );
    
    /**
     * Called by the {@link UIProperties} when one resource or {@link UIValue}
     * has been exchanged. Normally an {@link UIBridge} can just
     * call {@link UIValue#set(Object)} with the argument <code>value</code>.<br>
     * This method may also be called with an {@link UIValue} that was not {@link #add(String, UIValue) added}
     * to this {@link UIBridge}.
     * @param id the identifier of the resource
     * @param value the new resource, can be <code>null</code>
     * @param uiValue the value that is affected by the change
     */
    public void set( String id, V value, U uiValue );
}
