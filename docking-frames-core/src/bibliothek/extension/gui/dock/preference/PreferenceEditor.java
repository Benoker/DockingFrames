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

import java.awt.Component;

/**
 * An editor configures a {@link Component} which is used to edit some value. How
 * exactly this component is configured depends on the {@link #setValueInfo(Object) meta-information}
 * extracted from the {@link PreferenceModel#getValueInfo(int) preference} which is edited.<br>
 * An editor communicates with its surroundings through a {@link #setCallback(PreferenceEditorCallback) callback}. The
 * editor can ask its parent to show some buttons which represent operations this editor can execute. For
 * example an editor could ask its parent to show a "reset value to default"-button. The editor manages these buttons
 * by calling {@link PreferenceEditorCallback#setOperation(PreferenceOperation, boolean)} and by implementing
 * {@link #doOperation(PreferenceOperation)}.  
 * @author Benjamin Sigg
 * @param <V> the kind of value this editor edits
 */
public interface PreferenceEditor<V> {
    /**
     * Gets a component which displays the contents of this editor.
     * @return the component, not <code>null</code>
     */
    public Component getComponent();
    
    /**
     * Sets a callback, a callback can be used to read the value that has to
     * be edited or to store the edited value.<br>
     * Note: this editor should call {@link PreferenceEditorCallback#set(Object)}
     * whenever this editor shows a new valid value.
     * @param callback the callback, might be <code>null</code>
     */
    public void setCallback( PreferenceEditorCallback<V> callback );
    
    /**
     * Sets information about the value that is shown. This method is called
     * before {@link #setValue(Object)}. This method might be called with
     * a <code>null</code> argument when the editor is no longer needed.
     * @param information the information, may be <code>null</code>
     */
    public void setValueInfo( Object information );
    
    /**
     * Sets the current value of this editor.
     * @param value the value, might be <code>null</code> 
     */
    public void setValue( V value );
    
    /**
     * Gets the value of this editor.<br>
     * Note: editors should call {@link PreferenceEditorCallback#set(Object)}
     * when their value got edited by the user. An editor should not expect
     * a call to this method.
     * @return the value, might be <code>null</code>
     */
    public V getValue();
    
    /**
     * Executes the operation that matches <code>operation</code> or does
     * nothing if <code>operation</code> is unknown.
     * @param operation the operation that was triggered
     */
    public void doOperation( PreferenceOperation operation );
}
