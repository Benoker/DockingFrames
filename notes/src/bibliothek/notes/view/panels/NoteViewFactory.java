package bibliothek.notes.view.panels;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.layout.DockLayout;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.view.NoteViewManager;
import bibliothek.util.xml.XElement;

/**
 * A factory creating new {@link NoteView}s.
 * @author Benjamin Sigg
 */
public class NoteViewFactory implements DockFactory<NoteView, NoteViewFactory.NoteViewLayout> {
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
	
	public NoteViewLayout getLayout( NoteView element, Map<Dockable, Integer> children ) {
	    return new NoteViewLayout( element.getNote().getId() );
	}
	
	public NoteView layout( NoteViewLayout layout ) {
	    NoteView view = new NoteView( manager, model );
	    view.setNote( model.getNote( layout.getId() ) );
	    manager.putExternal( view );
	    return view;
	}
	
	public NoteView layout( NoteViewLayout layout, Map<Integer, Dockable> children ) {
	    NoteView view = new NoteView( manager, model );
        view.setNote( model.getNote( layout.getId() ) );
        manager.putExternal( view );
        return view;
	}
	
	public void setLayout( NoteView element, NoteViewLayout layout ) {
	    element.setNote( model.getNote( layout.getId() ) );
	}
	
	public void setLayout( NoteView element, NoteViewLayout layout, Map<Integer, Dockable> children ) {
	    element.setNote( model.getNote( layout.getId() ) );
	}
	
	public void write( NoteViewLayout layout, DataOutputStream out ) throws IOException {
	    out.writeUTF( layout.getId() );
	}
	
	public void write( NoteViewLayout layout, XElement element ) {
	    element.addElement( "note" ).setString( layout.getId() );
	}
	
	public NoteViewLayout read( DataInputStream in ) throws IOException {
	    return new NoteViewLayout( in.readUTF() );
	}
	
	public NoteViewLayout read( XElement element ) {
	    return new NoteViewLayout( element.getElement( "note" ).getString() );
	}
	
	/**
	 * A layout storing all the data of a {@link NoteView}. 
	 * @author Benjamin Sigg
	 */
	public static class NoteViewLayout implements DockLayout{
	    /** the id of the factory that wrote this layout */
	    private String factory;
	    /** the id of the note */
	    private String id;
	    
	    /**
	     * Creates a new layout.
	     * @param id the id of the note
	     */
	    public NoteViewLayout( String id ){
	        this.id = id;
	    }
	    
	    /**
	     * Gets the id of the note.
	     * @return the id of the note
	     */
	    public String getId() {
            return id;
        }
	    
        public String getFactoryID() {
            return factory;
        }

        public void setFactoryID( String id ) {
            this.factory = id;
        }
	    
	}
}
