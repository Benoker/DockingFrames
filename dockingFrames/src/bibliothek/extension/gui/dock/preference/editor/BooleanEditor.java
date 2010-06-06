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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import bibliothek.extension.gui.dock.preference.PreferenceEditor;
import bibliothek.extension.gui.dock.preference.PreferenceEditorCallback;
import bibliothek.extension.gui.dock.preference.PreferenceEditorFactory;
import bibliothek.extension.gui.dock.preference.PreferenceOperation;
import bibliothek.util.Path;

/**
 * An editor intended to be used for {@link Path#TYPE_BOOLEAN_PATH}. This editor just shows a {@link JCheckBox}.
 * @author Benjamin Sigg
 */
public class BooleanEditor extends JCheckBox implements PreferenceEditor<Boolean>{
	/**
	 * A factory creating {@link BooleanEditor}s.
	 */
	public static final PreferenceEditorFactory<Boolean> FACTORY = new PreferenceEditorFactory<Boolean>(){
		public PreferenceEditor<Boolean> create() {
			return new BooleanEditor();
		}
	};
	
	private PreferenceEditorCallback<Boolean> callback;
	
	/**
	 * Creates a new editor
	 */
	public BooleanEditor(){
		addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if( callback != null )
					callback.set( getValue() );
			}
		});
	}
	
	public void doOperation( PreferenceOperation operation ){
		// ignore
	}

	public Component getComponent() {
		return this;
	}

	public Boolean getValue() {
		return isSelected();
	}

	public void setCallback(PreferenceEditorCallback<Boolean> callback) {
		this.callback = callback;
	}

	public void setValue( Boolean value ){
		setSelected( Boolean.TRUE.equals( value ));
	}

	public void setValueInfo(Object information) {
		// ignore
	}
}
