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
package bibliothek.extension.gui.dock.preference.editor;

import java.awt.Component;

import javax.swing.JLabel;

import bibliothek.extension.gui.dock.preference.PreferenceEditor;
import bibliothek.extension.gui.dock.preference.PreferenceEditorCallback;
import bibliothek.extension.gui.dock.preference.PreferenceEditorFactory;
import bibliothek.extension.gui.dock.preference.PreferenceOperation;

/**
 * A label editor just shows some object, but does not modify the object.
 * @author Benjamin Sigg
 */
public class LabelEditor extends JLabel implements PreferenceEditor<Object>{
    /**
     * A factory creating new {@link LabelEditor}s.
     */
    public static final PreferenceEditorFactory<Object> FACTORY = new PreferenceEditorFactory<Object>(){
        public PreferenceEditor<Object> create() {
            return new LabelEditor();
        }
    };
    
    private Object value;
    
    public void doOperation( PreferenceOperation operation ) {
        // does not declare any operations
    }

    public Component getComponent() {
        return this;
    }

    public Object getValue() {
        return value;
    }

    public void setCallback( PreferenceEditorCallback<Object> callback ) {
        // ignore
    }

    public void setValue( Object value ) {
        this.value = value;
        setText( String.valueOf( value ) );
    }

    public void setValueInfo( Object information ) {
        // ignore
    }

}
