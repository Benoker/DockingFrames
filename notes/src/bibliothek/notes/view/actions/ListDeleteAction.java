package bibliothek.notes.view.actions;

import bibliothek.gui.Dockable;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.panels.ListView;

public class ListDeleteAction extends Delete{
	private ListView list;
	
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
