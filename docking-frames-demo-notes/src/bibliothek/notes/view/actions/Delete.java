package bibliothek.notes.view.actions;

import javax.swing.JOptionPane;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseTabDockAction;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;

/**
 * An action that contains methods to remove a {@link Note} from
 * the {@link NoteModel}.
 * @author Benjamin Sigg
 *
 */
@EclipseTabDockAction
public abstract class Delete extends SimpleButtonAction{
    /** the model from which a Note might be removed */
	private NoteModel model;
	
	/**
	 * Creates a new action.
	 * @param model the model from which <code>Notes</code> will be
	 * removed.
	 */
	public Delete( NoteModel model ){
		this.model = model;
		
		setText( "Delete" );
	}
	
	/**
	 * Removes <code>note</code> from {@link #model}, but only after the
	 * user confirmed the action. This method will show a question-dialog
	 * above <code>dockable</code>.
	 * @param note the Note to delete
	 * @param dockable the owner of the dialog which will be shown
	 */
	public void delete( Note note, Dockable dockable ){
		int option = JOptionPane.showConfirmDialog( dockable.getComponent(), 
				"Delete \"" + note.getTitle()  + "\"?", "Delete", 
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
		
		if( option == JOptionPane.YES_OPTION )
			model.removeNote( note );
	}
}
