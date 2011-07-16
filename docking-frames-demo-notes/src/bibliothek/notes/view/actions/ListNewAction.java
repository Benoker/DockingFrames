package bibliothek.notes.view.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.NoteViewManager;
import bibliothek.notes.view.panels.ListView;

/**
 * An action used to create new {@link Note}s. This action is shown above
 * the {@link ListView}.
 * @author Benjamin Sigg
 *
 */
public class ListNewAction extends SimpleButtonAction implements ActionListener{
    /** the model for which a new {@link Note} might be created */
	private NoteModel model;
	/** the manager of the graphical representations of the {@link Note}s */
	private NoteViewManager manager;
	
	/**
	 * Creates a new action.
	 * @param manager the manager of the graphical representations of the
	 * {@link Note}s, used to show the {@link Note}s that are created
	 * by this action.
	 * @param model the model that will own the newly created <code>Notes</code>.
	 */
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
