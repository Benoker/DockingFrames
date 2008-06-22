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
package bibliothek.extension.gui.dock.preference;

/**
 * A callback is an object that has access to some value and can either read
 * or store it.
 * @author Benjamin Sigg
 *
 * @param <V> the kind of object this callback has access to
 */
public interface PreferenceEditorCallback<V> {
    /**
     * Gets the value to which this callback has access to. 
     * @return the value, might be <code>null</code>
     */
    public V get();
    
    /**
     * Sets the value of this callback.
     * @param value the value, might be <code>null</code>
     */
    public void set( V value );
    
    /**
     * Tells this callback that the editor using it can perform some action. This
     * method can be called more than once if <code>enabled</code> changes it value.
     * @param operation the key of the action
     * @param enabled whether the operation is available right now 
     */
    public void setOperation( PreferenceEditorOperation operation, boolean enabled );
}
