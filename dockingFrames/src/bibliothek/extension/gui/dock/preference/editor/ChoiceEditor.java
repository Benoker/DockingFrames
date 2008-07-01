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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import bibliothek.extension.gui.dock.preference.PreferenceEditor;
import bibliothek.extension.gui.dock.preference.PreferenceEditorCallback;
import bibliothek.extension.gui.dock.preference.PreferenceEditorFactory;
import bibliothek.extension.gui.dock.preference.PreferenceOperation;
import bibliothek.extension.gui.dock.preference.preferences.choice.Choice;

/**
 * An editor using a {@link Choice} to let the user select one string-identifier.
 * @author Benjamin Sigg
 */
public class ChoiceEditor extends JComboBox implements PreferenceEditor<String>{
	/** a factory creating new {@link ChoiceEditor}s */
	public static final PreferenceEditorFactory<String> FACTORY = new PreferenceEditorFactory<String>(){
		public PreferenceEditor<String> create() {
			return new ChoiceEditor();
		}
	};
	
	private DefaultComboBoxModel model;
	private Choice choice;
	
	private boolean onChange = false;
	private PreferenceEditorCallback<String> callback;
	
	/**
	 * Creates a new editor.
	 * @param choice the available choices, note that this editor assumes that
	 * <code>choice</code> is immutable
	 */
	public ChoiceEditor(){
		addItemListener( new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if( !onChange ){
					if( e.getStateChange() == ItemEvent.SELECTED ){
						if( callback != null ){
							callback.set( getValue() );
						}
						checkOperations();
					}
				}
			}
		});
	}
	
	public void setValueInfo(Object information) {
		if( information instanceof Choice )
			choice = (Choice)information;
		else
			choice = null;
		
		model = new DefaultComboBoxModel();
		if( choice != null ){
			if( choice.isNullEntryAllowed() ){
				model.addElement( new Entry( null, "" ));
			}

			for( int i = 0, n = choice.size(); i<n; i++ ){
				model.addElement( new Entry( choice.getId( i ), choice.getText( i ) ) );
			}
		}
		
		setModel( model );
		checkOperations();
	}
	
	public void doOperation( PreferenceOperation operation ){
		if( operation == PreferenceOperation.DEFAULT ){
			setValue( choice.getDefaultChoice() );
			if( callback != null )
				callback.set( getValue() );
		}
		if( operation == PreferenceOperation.DELETE ){
			setValue( null );
			if( callback != null )
				callback.set( null );
		}
	}

	public Component getComponent() {
		return this;
	}

	public String getValue() {
		return ((Entry)model.getSelectedItem()).id;
	}

	public void setCallback( PreferenceEditorCallback<String> callback ) {
		this.callback = callback;
		checkOperations();
	}

	private void checkOperations(){
		if( callback != null && choice != null ){
			if( choice.isNullEntryAllowed() ){
				callback.setOperation( PreferenceOperation.DELETE, getValue() != null );
			}
			String value = getValue();
			boolean enabled = (value == null && choice.getDefaultChoice() != null) || (value != null && !value.equals( choice.getDefaultChoice() ));
			callback.setOperation( PreferenceOperation.DEFAULT, enabled );
		}
	}
	
	public void setValue( String value ) {
		try{
			onChange = true;
			for( int i = 0, n = model.getSize(); i<n; i++ ){
				Entry entry = (Entry)model.getElementAt( i );
				if( entry.id == value || (value != null && value.equals( entry.id ))){
					model.setSelectedItem( entry );
					break;
				}
			}
		}
		finally{
			onChange = false;
			checkOperations();
		}
	}

	/**
	 * An entry in the combobox of a {@link ChoiceEditor}.
	 * @author Benjamin Sigg
	 */
	private static class Entry{
		public String id;
		public String text;
		
		public Entry( String id, String text ){
			this.id = id;
			this.text = text;
		}
		
		@Override
		public String toString() {
			return text;
		}
	}
}
