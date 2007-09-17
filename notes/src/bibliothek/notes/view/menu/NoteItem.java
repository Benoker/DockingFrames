package bibliothek.notes.view.menu;

import java.awt.event.ActionEvent;

import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteListener;
import bibliothek.notes.view.NoteViewManager;
import bibliothek.notes.view.panels.NoteView;

/**
 * A check-box-menu-item representing one {@link Note}. When the item is
 * selected, then the <code>Note</code> is shown, otherwise the <code>Note</code>
 * is hidden. NoteItems are managed by the {@link PanelList}.
 * @author Benjamin Sigg
 *
 */
public class NoteItem extends UpdateableCheckBoxMenuItem implements NoteListener{
    /** a manager of the graphical representation of the {@link Note}s */
	private NoteViewManager manager;
	/** the <code>Note</code> whose visibility-state is represented by this item */
	private Note note;
	
	/**
	 * Creates a new item.
	 * @param manager the manager of the {@link NoteView}s
	 * @param note the <code>Note</code> whose visibility-state is represented
	 * by this item
	 */
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
