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
import bibliothek.extension.gui.dock.preference.preferences.choice.ChoiceListener;

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
	
	private Model model;
	private Choice choice;
	
	private boolean onChange = false;
	private PreferenceEditorCallback<String> callback;
	
	/** this listener is added to the current {@link #choice} */
	private ChoiceListener listener = new ChoiceListener(){
		public void updated( Choice choice, int indexStart, int indexEnd ){
			int delta = 0;
			if( choice.isNullEntryAllowed() ){
				delta = 1;
			}
			
			for( int i = indexStart; i <= indexEnd; i++ ){
				Entry entry = (Entry)model.getElementAt( i+delta );
				entry.text = choice.getText( i );
			}
			
			model.fireContentsChanged( indexStart+delta, indexEnd+delta );
		}
		
		public void removed( Choice choice, int indexStart, int indexEnd ){
			if( choice.isNullEntryAllowed() ){
				indexStart++;
				indexEnd++;
			}
			
			for( int i = indexEnd; i >= indexStart; i-- ){
				model.removeElementAt( i );
			}
		}
		
		public void inserted( Choice choice, int indexStart, int indexEnd ){
			int delta = 0;
			if( choice.isNullEntryAllowed() ){
				delta = 1;
			}	
			
			for( int i = indexStart; i <= indexEnd; i++ ){
				model.insertElementAt( new Entry( choice.getId( i ), choice.getText( i ) ), i+delta );
			}
		}
	};
	
	/**
	 * Creates a new editor.
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
		if( this.choice != null ){
			this.choice.removeChoiceListener( listener );
			this.choice.setController( null );
		}
		
		if( information instanceof Choice )
			choice = (Choice)information;
		else
			choice = null;
		
		model = new Model();
		if( choice != null ){
			if( choice.isNullEntryAllowed() ){
				model.addElement( new Entry( null, "" ));
			}

			for( int i = 0, n = choice.size(); i<n; i++ ){
				model.addElement( new Entry( choice.getId( i ), choice.getText( i ) ) );
			}
		}
		
		if( choice != null ){
			choice.addChoiceListener( listener );
			if( callback != null ){
				choice.setController( callback.getModel().getController() );
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
		if( choice != null ){
			if( callback == null ){
				choice.setController( null );
			}
			else{
				choice.setController( callback.getModel().getController() );
			}
		}
		
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
	 * Shows varios entries of a {@link Choice}
	 * @author Benjamin Sigg
	 */
	private static class Model extends DefaultComboBoxModel{
		/**
		 * Informs the client of this {@link Model} that the entries <code>start</code>
		 * to <code>end</code> changed.
		 * @param start the index of the first changed item
		 * @param end the index of the last changed item
		 */
		public void fireContentsChanged( int start, int end ){
			fireContentsChanged( this, start, end );
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
