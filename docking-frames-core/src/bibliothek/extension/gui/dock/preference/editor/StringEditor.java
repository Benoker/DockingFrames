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

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import bibliothek.extension.gui.dock.preference.PreferenceEditor;
import bibliothek.extension.gui.dock.preference.PreferenceEditorCallback;
import bibliothek.extension.gui.dock.preference.PreferenceEditorFactory;
import bibliothek.extension.gui.dock.preference.PreferenceOperation;

/**
 * An editor used to edit {@link String}s. This editor just shows a {@link JTextField}.
 * @author Benjamin Sigg
 */
public class StringEditor extends JTextField implements PreferenceEditor<String>{
    /** a factory creating {@link StringEditor}s */
    public static final PreferenceEditorFactory<String> FACTORY = new PreferenceEditorFactory<String>(){
        public PreferenceEditor<String> create() {
            return new StringEditor();
        }
    };
    
    private PreferenceEditorCallback<String> callback;
    
    private boolean onUpdate = false;
    
    /**
     * Creates a new editor.
     */
    public StringEditor(){
        getDocument().addDocumentListener( new DocumentListener(){
            public void changedUpdate( DocumentEvent e ) {
                update( true );
            }
            public void insertUpdate( DocumentEvent e ) {
                update( true );
            }
            public void removeUpdate( DocumentEvent e ) {
                update( true );
            }
        });
    }
    
    private void update( final boolean transmit ){
        try{
            onUpdate = true;
            if( callback != null ){
                callback.setOperation( PreferenceOperation.DELETE, getText().length() > 0 );
                if( transmit ){
                    callback.set( getText() );
                }
            }
        }
        finally{
            onUpdate = false;
        }
    }
    
    public void doOperation( PreferenceOperation operation ) {
        if( operation == PreferenceOperation.DELETE ){
            setText( "" );
        }
    }

    public Component getComponent() {
        return this;
    }

    public String getValue() {
        return getText();
    }

    public void setCallback( PreferenceEditorCallback<String> callback ) {
        this.callback = callback;
        update( false );
    }

    public void setValue( String value ) {
        if( !onUpdate ){
            setText( value == null ? "" : value );
            update( false );
        }
    }

    public void setValueInfo( Object information ) {
        // ignore
    }

}
