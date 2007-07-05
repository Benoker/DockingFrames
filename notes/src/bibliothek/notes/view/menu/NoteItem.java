package bibliothek.notes.view.menu;

import java.awt.event.ActionEvent;

import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteListener;
import bibliothek.notes.view.NoteViewManager;

public class NoteItem extends UpdateableCheckBoxMenuItem implements NoteListener{
	private NoteViewManager manager;
	private Note note;
	
	public NoteItem( NoteViewManager manager, Note note ){
		this.manager = manager;
		this.note = note;
		
		note.addListener( this );
		
		titleChanged( note );
		iconChanged( note );
	}
	
	public void actionPerformed( ActionEvent e ){
		boolean state = getState();
		if( state )
			manager.show( note );
		else
			manager.hide( note );
	}
	
	public void titleChanged( Note note ){
		String text = note.getTitle();
		if( text == null || text.trim().length() == 0 )
			text = " - ";
		setText( text );
	}
	
	public void iconChanged( Note note ){
		setIcon( note.getIcon() );
	}
	
	public void colorChanged( Note note ){
		// ignore
	}
	
	public void textChanged( Note note ){
		// ignore
	}
}
