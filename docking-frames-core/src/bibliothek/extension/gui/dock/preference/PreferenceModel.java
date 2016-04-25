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

import bibliothek.gui.DockController;
import bibliothek.util.Path;

/**
 * A preference model is a layer between the framework and its properties, and the
 * dialog, table or tree with which the user can modify these properties. A model
 * consists of "preferences", each preference is a wrapper around a single property. A preference
 * also serves as buffer: modifying a preference does not immediately change the property. 
 * Clients have to call {@link #read()} to fill the preference-buffers, and have to call {@link #write()}
 * to write changes back.<br> 
 * The framework stores and loads some properties automatically, the preferences for these
 * properties are called "natural". If on the other hand the framework assumes that a
 * property is managed by the client, its preference is called "artificial". Clients have
 * to use a {@link PreferenceStorage} to persistently store and load the artificial preferences.<br>
 * A preference model also offers information of how to present its preferences to the
 * user.<br>
 * The typical lifecycle of a {@link PreferenceModel} looks as follows:<br>
 * <ol>
 *  <li>A {@link PreferenceStorage} is created and its content loaded using one of its <code>read</code>-methods.</li>	
 *  <li>The new, empty model is created.</li>
 *  <li>With a call to {@link PreferenceStorage#load(PreferenceModel, boolean)} the artificial and natural preferences are loaded.</li>
 *  <li>With a call to {@link #write()} all preferences are made available to the entire framework.</li>
 *  <li>At any time a call to {@link #read()} will update the contents of the model.</li>
 *  <li>At any time a call to {@link #write()} will make modified preferences available to the entire framework.</li> 
 *  <li>With a call to {@link PreferenceStorage#store(PreferenceModel)} the preferences are stored.</li>
 *  <li>Using one of the <code>write</code>-methods of the {@link PreferenceStorage} the client can store the preferences persistently</li>
 * </ol>
 * Note that many {@link PreferenceModel}s can share the same {@link PreferenceStorage}. In such a case its best to re-create and refill the
 * models before using them.
 * @author Benjamin Sigg
 */
public interface PreferenceModel {
	/**
	 * Gets the {@link DockController} in whose realm this model is used. The controller
	 * is mainly necessary to load icons and text.
	 * @return the controller in whose realm this model is used
	 */
	public DockController getController();
	
    /**
     * Uses an unknown source to update this model and load all the preferences
     * that are currently available. If the underlying resource cannot be
     * read, or returns invalid data, then this model should not change its
     * content.
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
    public void addPreferenceModelListener( PreferenceModelListener listener );
    
    /**
     * Removes a listener from this model.
     * @param listener the listener to remove.
     */
    public void removePreferenceModelListener( PreferenceModelListener listener );
    
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
     * Tells whether the operation <code>operation</code> is enabled for
     * the preference at location <code>index</code>.
     * @param index some location
     * @param operation an operation from {@link #getOperations(int)}
     * @return <code>true</code> if the operation is enabled, <code>false</code>
     * if not
     */
    public boolean isEnabled( int index, PreferenceOperation operation );
    
    /**
     * Gets all operations for which this model has a definition for
     * the preference at location <code>index</code>. Note: a {@link PreferenceEditor}
     * has operations as well, if the editor and the model share an operation,
     * then the operation is considered to belong to the editor.
     * @param index the location of a preference
     * @return the list of available operations (enabled and disabled operations),
     * can be <code>null</code>
     */
    public PreferenceOperation[] getOperations( int index );
    
    /**
     * Executes the enabled operation <code>operation</code>.
     * @param index the location of the affected preference
     * @param operation the operation to execute
     */
    public void doOperation( int index, PreferenceOperation operation );
    
    /**
     * Gets information about how the <code>index</code>'th value can
     * be modified. For an integer that might be its upper and lower boundaries.
     * The type of this objects depends on {@link #getTypePath(int)}.
     * @param index the index of the info
     * @return the information or <code>null</code> if no information is
     * available
     */
    public Object getValueInfo( int index );
    
    /**
     * Gets the <code>index</code>'th preference. The {@link #getTypePath(int) type path}
     * determines how the value is to be presented on the screen.
     * @param index the number of the preference
     * @return the value or maybe <code>null</code>, has to be immutable
     */
    public Object getValue( int index );
    
    /**
     * Sets the value of the <code>index</code>'th preference.
     * @param index the number of the preference
     * @param value the new value, may be <code>null</code>
     */
    public void setValue( int index, Object value );
    
    /**
     * Tells whether the <code>index</code>'th preference is natural or
     * artificial.
     * <ul>
     * <li>A natural preference is just available, it does not need
     * to be stored anywhere and will maintain its value even if the application
     * is restarted. Natural preferences may be views of other models which
     * already have persistent storage or represent values that are calculated
     * from other values.</li>
     * <li>An artificial preference needs to be stored. It represents some
     * setting that is not available in the wild. It cannot maintain its state
     * during application restarts.</li>
     * </ul>
     * @param index the index of the preference
     * @return <code>true</code> if the preference is natural, <code>false</code>
     * if it is artificial
     */
    public boolean isNatural( int index );
    
    /**
     * Like {@link #setValue(int, Object)} this method changes the value of the <code>index</code>'th
     * preference. But this time the natural preference has to extract the value from
     * its underlying property.
     * @param index the index of the preference to update
     */
    public void setValueNatural( int index );
    
    /**
     * Tells what kind of type the <code>index</code>'th value is. The type
     * is represented as a path. Most times the path would equal the name of
     * some class. Note: there is a set of standard paths defined in {@link Path}.
     * @param index the number of the value
     * @return a unique path for the type of this value
     */
    public Path getTypePath( int index );
    
    /**
     * Gets the unique identifier of the <code>index</code>'th preference of
     * this model.
     * @param index the index of the preference
     * @return the unique path, compared to the other paths of this model
     */
    public Path getPath( int index );
}
