package bibliothek.notes.view.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.NoteViewManager;

public class ListNewAction extends SimpleButtonAction implements ActionListener{
	private NoteModel model;
	private NoteViewManager manager;
	
	public ListNewAction( NoteViewManager manager, NoteModel model ){
		this.manager = manager;
		this.model = model;

		setText( "New note" );
		setIcon( ResourceSet.APPLICATION_ICONS.get( "list.new" ) );
		
		addActionListener( this );
	}
	
	public void actionPerformed( ActionEvent e ){
		manager.show( model.addNote() );
	}
}
