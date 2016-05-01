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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.KeyStroke;

import bibliothek.extension.gui.dock.preference.PreferenceEditor;
import bibliothek.extension.gui.dock.preference.PreferenceEditorCallback;
import bibliothek.extension.gui.dock.preference.PreferenceEditorFactory;
import bibliothek.extension.gui.dock.preference.PreferenceOperation;
import bibliothek.extension.gui.dock.preference.preferences.KeyStrokeValidator;

/**
 * An editor for {@link KeyStroke}s.
 * @author Benjamin Sigg
 */
public class KeyStrokeEditor extends JTextField implements PreferenceEditor<KeyStroke>{
    /**
     * A factory for {@link KeyStrokeEditor}s.
     */
    public static final PreferenceEditorFactory<KeyStroke> FACTORY = new PreferenceEditorFactory<KeyStroke>(){
        public PreferenceEditor<KeyStroke> create() {
            return new KeyStrokeEditor();
        }
    };
    
    /**
     * Transforms <code>stroke</code> into a human readable string.
     * @param stroke the stroke to transform
     * @param complete if set, then the combination is complete, otherwise
     * a "+" sign will be added at the end.
     * @return the human readable form
     */
    public static final String toString( KeyStroke stroke, boolean complete ){
        // source copy & pasted from BasicMenuItemUI
        String result = "";
        
        int modifiers = stroke.getModifiers();
        if( modifiers != 0 ){
            result = KeyEvent.getKeyModifiersText( modifiers );
            if( !complete ){
            	result += "+";
            }
        }

        int keyCode = stroke.getKeyCode();
        if( !isModifierKey( keyCode )){
        	if( complete && modifiers != 0 )
        		result += "+";
        	
            if( keyCode != 0 ){
                result += KeyEvent.getKeyText( keyCode );
            }
            else{
                result += stroke.getKeyChar();
            }
            
            if( !complete )
            	result += "+";
        }

        return result;
    }
    
    /**
     * Tells whether <code>keyCode</code> is a modifier key.
     * @param keyCode some code of a key
     * @return <code>true</code> if <code>keyCode</code> is a modifier key
     * like "shift" or "ctrl".
     */
    public static boolean isModifierKey( int keyCode ){
        switch( keyCode ){
            case KeyEvent.VK_ALT:
            case KeyEvent.VK_ALT_GRAPH:
            case KeyEvent.VK_CONTROL:
            case KeyEvent.VK_SHIFT:
            case KeyEvent.VK_META:
                return true;
            default:
                return false;
        }
    }
    
    private KeyStroke stroke;
    private KeyStrokeValidator validator = KeyStrokeValidator.EVERYTHING;
    private PreferenceEditorCallback<KeyStroke> callback;
    private boolean focused = false;
    private EditorText text;
    
    /**
     * Creates a new editor
     */
    public KeyStrokeEditor(){
        setEditable( false );
        
        addFocusListener( new FocusListener(){
            public void focusGained( FocusEvent e ) {
                focused = true;
                setValue( stroke );
            }
            public void focusLost( FocusEvent e ) {
                focused = false;
                
                if( callback != null ){
                    if( stroke == null ){
                        setValue( callback.get() );
                    }
                    else{
                        callback.set( stroke );
                    }
                }
                else{
                    setValue( stroke );
                }
            }
        });
        
        text = new EditorText( "preference.keystroke.click", this ){
			protected void changed( String oldValue, String newValue ){
				if( !focused ){
					setText( newValue );
				}
			}
		};
        
        addKeyListener( new KeyAdapter(){
            @Override
            public void keyPressed( KeyEvent e ) {
                setValue( KeyStroke.getKeyStrokeForEvent( e ) );
                KeyStroke value = stroke;
                stroke = null;
                maybeStore( value );
            }
        });
    }
    
    private void maybeStore( KeyStroke stroke ){
        if( stroke != null && callback != null ){
            if( validator.isValid( stroke ) ){
                callback.set( stroke );
            }
        }
    }

    public void setValueInfo( Object information ) {
    	if( information instanceof KeyStrokeValidator )
    		validator = (KeyStrokeValidator)information;
    	else
    		validator = KeyStrokeValidator.EVERYTHING;
    }
    
    public Component getComponent() {
        return this;
    }

    public KeyStroke getValue() {
        return stroke;
    }

    public void setCallback( PreferenceEditorCallback<KeyStroke> callback ) {
        this.callback = callback;
        if( callback != null ){
            callback.setOperation( PreferenceOperation.DELETE, stroke != null );
            text.setController( callback.getModel().getController() );
        }
        else{
        	text.setController( null );
        }
    }

    public void setValue( KeyStroke value ) {
        stroke = value;
        if( value == null ){
            if( focused ){
                setText( "" );
            }
            else{
                setText( text.value() );
            }
        }
        else{
        	if( validator.isValid( value )){
        		setText( toString( value, true ) );
        	}
        	else{
        		setText( toString( value, !validator.isCompleteable( value ) ));
        	}
        }
        
        if( callback != null ){
            callback.setOperation( PreferenceOperation.DELETE, stroke != null );
        }
    }

    public void doOperation( PreferenceOperation operation ) {
        if( operation == PreferenceOperation.DELETE ){
            setValue( null );
            if( callback != null ){
                callback.set( null );
            }
        }
    }
}
