package bibliothek.notes.view.panels;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.view.NoteViewManager;

/**
 * A factory creating new {@link NoteView}s.
 * @author Benjamin Sigg
 */
public class NoteViewFactory implements DockFactory<NoteView> {
    /** the unique id for this factory */
	public static final String FACTORY_ID = "note";
	
	/** a manager of all {@link NoteView}s */
	private NoteViewManager manager;
	/** the set of known {@link Note}s */
	private NoteModel model;
	
	/**
	 * Creates a new factory
	 * @param manager used to store newly loaded {@link NoteView}s
	 * @param model used to create new {@link NoteView}s
	 */
	public NoteViewFactory( NoteViewManager manager, NoteModel model ){
		this.manager = manager;
		this.model = model;
	}
	
	public String getID(){
		return "note";
	}

	public NoteView read( Map<Integer, Dockable> children, boolean ignoreChildren, DataInputStream in ) throws IOException{
		NoteView note = new NoteView( manager, model );
		read( children, ignoreChildren, note, in );
		return note;
	}

	public void read( Map<Integer, Dockable> children, boolean ignoreChildren, NoteView preloaded, DataInputStream in ) throws IOException{
		String id = in.readUTF();
		Note note = model.getNote( id );
		preloaded.setNote( note );
		manager.putExternal( preloaded );
	}

	public void write( NoteView element, Map<Dockable, Integer> children, DataOutputStream out ) throws IOException{
		out.writeUTF( element.getNote().getId() );
	}
}
