package bibliothek.notes.view.actions;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.NoteViewManager;

public class CopyAction extends SimpleButtonAction {
	private NoteViewManager manager;
	private NoteModel model;
	private Note note;
	
	public CopyAction( NoteViewManager manager, NoteModel model, Note note ){
		this.manager = manager;
		this.model = model;
		this.note = note;
		
		setText( "Copy" );
		setIcon( ResourceSet.APPLICATION_ICONS.get( "copy" ) );
	}

	@Override
	public void action( Dockable dockable ){
		super.action( dockable );
		
		Note copy = model.addNote();
		
		copy.setColor( note.getColor() );
		copy.setIcon( note.getIcon() );
		copy.setText( note.getText() );
		copy.setTitle( note.getTitle() );
		
		manager.show( copy, dockable );
	}
}
