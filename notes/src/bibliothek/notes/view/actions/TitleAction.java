package bibliothek.notes.view.actions;

import javax.swing.JOptionPane;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.notes.model.Note;
import bibliothek.notes.util.ResourceSet;

public class TitleAction extends SimpleButtonAction {
	private Note note;
	
	public TitleAction( Note note ){
		this.note = note;
		
		setText( "Title" );
		setIcon( ResourceSet.APPLICATION_ICONS.get( "title" ) );
	}
	
	@Override
	public void action( Dockable dockable ){
		super.action( dockable );
		
		String title = JOptionPane.showInputDialog( dockable.getComponent(),
				"Please enter new title of note", "Change title",
				JOptionPane.PLAIN_MESSAGE );
		
		if( title != null )
			TitleAction.this.note.setTitle( title );
	}
}
