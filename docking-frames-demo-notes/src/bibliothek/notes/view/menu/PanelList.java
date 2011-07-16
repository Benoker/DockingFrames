package bibliothek.notes.view.menu;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;

import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.model.NoteModelListener;
import bibliothek.notes.view.ViewManager;
import bibliothek.notes.view.panels.ListView;
import bibliothek.notes.view.panels.NoteView;

/**
 * A menu showing selectable items for each {@link Dockable} of this application.
 * The menu is divided in a part of always available {@link Dockable}s like
 * the {@link ListView}, and changing <code>Dockables</code> like the
 * {@link NoteView}s.
 * @author Benjamin Sigg
 */
public class PanelList extends JMenu implements NoteModelListener{
    /** the manager of all {@link Dockable}s */
	private ViewManager manager;
	
	/** The {@link Note}s and their item in this menu */
	private Map<Note, NoteItem> notes = new HashMap<Note, NoteItem>();
	/** The item for the {@link ListView} */
	private DockableItem listItem;
	
	/**
	 * Creates a new menu.
	 * @param manager the manager of all {@link Dockable}s
	 * @param model the set of {@link Note}s in this application
	 */
	public PanelList( ViewManager manager, NoteModel model ){
		this.manager = manager;
		DockFrontend frontend = manager.getFrontend();
		
		setText( "Panels" );
		
		listItem = new DockableItem( frontend, manager.getList() );
		add( listItem );
		
		model.addNoteModelListener( this );
		
		frontend.getController().getRegister().addDockRegisterListener( new DockRegisterAdapter(){
			@Override
			public void dockableRegistered( DockController controller, Dockable dockable ){
				setSilent( dockable, true );
			}
			
			@Override
			public void dockableUnregistered( DockController controller, Dockable dockable ){
				setSilent( dockable, false );
			}
		});
	}
	
	/**
	 * Silently changes the state of the item that represents the
	 * visibility-state of <code>dockable</code> to the new <code>state</code>.
	 * @param dockable the element whose visibility-state has changed
	 * @param state the new visibility-state
	 */
	private void setSilent( Dockable dockable, boolean state ){
		if( dockable == manager.getList() )
			listItem.setSilent( state );
		else if( dockable instanceof NoteView ){
			NoteItem item = notes.get( ((NoteView)dockable).getNote() );
			if( item != null )
				item.setSilent( state );
		}
	}
	
	public void noteAdded( NoteModel model, Note note ){
		if( notes.isEmpty() )
			addSeparator();
			
		NoteItem item = new NoteItem( manager.getNotes(), note );
		notes.put( note, item );
		add( item );
	}
	
	public void noteRemoved( NoteModel model, Note note ){
		NoteItem item = notes.remove( note );
		remove( item );
		if( notes.isEmpty() )
			remove( 1 );
	}
}
