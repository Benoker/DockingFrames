package bibliothek.notes.view.actions;

import javax.swing.JOptionPane;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.notes.model.Note;
import bibliothek.notes.util.ResourceSet;

/**
 * An action that changes the {@link Note#setTitle(String) title}-property
 * of a {@link Note}.
 * @author Benjamin Sigg
 *
 */
public class TitleAction extends SimpleButtonAction {
    /** the Note whose title might be changed */
	private Note note;
	
	/**
	 * Creates a new action.
	 * @param note the <code>Note</code> whose title might be changed by this
	 * action
	 */
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
