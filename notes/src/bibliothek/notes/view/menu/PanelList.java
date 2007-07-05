package bibliothek.notes.view.menu;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;

import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockControllerAdapter;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.model.NoteModelListener;
import bibliothek.notes.view.ViewManager;
import bibliothek.notes.view.panels.NoteView;

public class PanelList extends JMenu implements NoteModelListener{
	private ViewManager manager;
	
	private Map<Note, NoteItem> notes = new HashMap<Note, NoteItem>();
	private DockableItem listItem;
	
	public PanelList( ViewManager manager, NoteModel model ){
		this.manager = manager;
		DockFrontend frontend = manager.getFrontend();
		
		setText( "Panels" );
		
		listItem = new DockableItem( frontend, manager.getList() );
		add( listItem );
		
		model.addNoteModelListener( this );
		
		frontend.getController().getRegister().addDockRegisterListener( new DockControllerAdapter(){
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
