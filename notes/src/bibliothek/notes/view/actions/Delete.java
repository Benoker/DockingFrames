package bibliothek.notes.view.actions;

import javax.swing.JOptionPane;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabDockAction;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;

@EclipseTabDockAction
public abstract class Delete extends SimpleButtonAction{
	private NoteModel model;
	
	public Delete( NoteModel model ){
		this.model = model;
		
		setText( "Delete" );
	}
	
	public void delete( Note note, Dockable dockable ){
		int option = JOptionPane.showConfirmDialog( dockable.getComponent(), 
				"Delete \"" + note.getTitle()  + "\"?", "Delete", 
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
		
		if( option == JOptionPane.YES_OPTION )
			model.removeNote( note );
	}
}
