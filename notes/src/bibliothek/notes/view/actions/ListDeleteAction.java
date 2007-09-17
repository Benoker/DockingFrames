package bibliothek.notes.view.actions;

import bibliothek.gui.Dockable;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.panels.ListView;

/**
 * An action owned by the {@link ListView} of the application. When triggered,
 * the action deletes the selected {@link Note} of the <code>ListView</code>.
 * @author Benjamin Sigg
 *
 */
public class ListDeleteAction extends Delete{
    /** the owner */
	private ListView list;
	
	/**
	 * Creates a new action.
	 * @param list the owner of this action
	 * @param model the model from which a {@link Note} might be removed
	 */
	public ListDeleteAction( ListView list, NoteModel model ){
		super( model );
		this.list = list;
		
		setIcon( ResourceSet.APPLICATION_ICONS.get( "list.delete" ) );
	}
	
	@Override
	public void action( Dockable dockable ){
		super.action( dockable );
		Note note = list.getSelected();
		if( note != null )
			delete( note, list );
	}
}
