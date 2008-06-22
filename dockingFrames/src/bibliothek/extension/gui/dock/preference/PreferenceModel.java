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
 * A preference model is a list of objects which represent some preferences
 * of another resource. A preference model has enough information to be displayed
 * in a graphical user interface and be modified by a user.
 * @author Benjamin Sigg
 */
public interface PreferenceModel {
    /**
     * Uses an unknown source to update this model and load all the preferences
     * that are currently available.
     */
    public void read();
    
    /**
     * Writes the current preferences to the location where they are used.
     */
    public void write();
    
    /**
     * Adds a listener to this model.
     * @param listener the new listener
     */
    public void addModelListener( PreferenceModelListener listener );
    
    /**
     * Removes a listener from this model.
     * @param listener the listener to remove.
     */
    public void removeModelListener( PreferenceModelListener listener );
    
    /**
     * Gets the number of preferences stored in this model.
     * @return the number of preferences
     */
    public int getSize();
    
    /**
     * Gets a short label that can be presented to the user for the 
     * <code>index</code>'th object.
     * @param index the number the preference
     * @return a short human readable description
     */
    public String getLabel( int index );
    
    /**
     * Gets a description of the <code>index</code>'th object. The description
     * is a longer text that will be presented to the user.
     * @param index the number of the preference
     * @return the description, might be <code>null</code>, might be formated
     * in HTML
     */
    public String getDescription( int index );
    
    /**
     * Gets the <code>index</code>'th preference. The type of the result of
     * this method should be a subtype of {@link #getPreferenceClass(int)}. It
     * depends on the type whether <code>null</code> is valid.
     * @param index the number of the preference
     * @return the value or maybe <code>null</code>
     */
    public Object getValue( int index );
    
    /**
     * Sets the value of the <code>index</code>'th preference.
     * @param index the number of the preference
     * @param value the new value, may be <code>null</code>
     */
    public void setValue( int index, Object value );
    
    /**
     * Tells what kind of value the <code>index</code>'th preference requires.
     * @param index the number of the value
     * @return the class, not <code>null</code>
     */
    public Class<?> getPreferenceClass( int index );
}
