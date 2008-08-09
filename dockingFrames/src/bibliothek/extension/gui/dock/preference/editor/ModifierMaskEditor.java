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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

import bibliothek.extension.gui.dock.preference.PreferenceEditor;
import bibliothek.extension.gui.dock.preference.PreferenceEditorCallback;
import bibliothek.extension.gui.dock.preference.PreferenceEditorFactory;
import bibliothek.extension.gui.dock.preference.PreferenceOperation;
import bibliothek.gui.dock.control.ModifierMask;

/**
 * An editor for {@link ModifierMask}s.
 * @author Benjamin Sigg
 */
public class ModifierMaskEditor extends JTextField implements PreferenceEditor<ModifierMask>{
	/** factory for new {@link ModifierMaskEditor}s */
	public static final PreferenceEditorFactory<ModifierMask> FACTORY = new PreferenceEditorFactory<ModifierMask>(){
		public PreferenceEditor<ModifierMask> create() {
			return new ModifierMaskEditor();
		}
	};
	
	private ModifierMask mask = new ModifierMask( 0 );
	
	private PreferenceEditorCallback<ModifierMask> callback;
	
	public ModifierMaskEditor(){
		setEditable( false );
		setText( mask.onMaskToString() );
		
		addKeyListener( new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				mask = new ModifierMask( e.getModifiersEx() );
				if( mask.getOnmask() != 0 ){
					setText( mask.onMaskToString() );
					if( callback != null )
						callback.set( mask );
				}
			}
		});
	}
	
	public void doOperation( PreferenceOperation operation ){
		// nothing
	}

	public Component getComponent(){
		return this;
	}

	public ModifierMask getValue() {
		return mask;
	}

	public void setCallback( PreferenceEditorCallback<ModifierMask> callback ) {
		this.callback = callback;
	}

	public void setValue( ModifierMask value ) {
		this.mask = value;
		if( mask == null )
			setText( "" );
		else
			setText( mask.onMaskToString() );
	}

	public void setValueInfo( Object information ) {
		// ignore
	}
}
