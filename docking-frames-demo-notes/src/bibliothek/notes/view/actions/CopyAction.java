package bibliothek.notes.view.actions;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.NoteViewManager;

/**
 * An action that is owned by a {@link Note}. The owner will be copied
 * when this action is triggered. The copy will then be shown at the same
 * location where the owner is or was.
 * @author Benjamin Sigg
 *
 */
public class CopyAction extends SimpleButtonAction {
    /** a manager for the graphical representation of Notes */
	private NoteViewManager manager;
	/** a model needed to create new instances of {@link Note} */
	private NoteModel model;
	/** the Note which will be copied by this action */
	private Note note;
	
	/**
	 * Creates a new action.
	 * @param manager the manager of the graphical representation of all Notes
	 * @param model the set of known Notes
	 * @param note the owner of this action. This Note will be copied when
	 * the action is triggered.
	 */
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
