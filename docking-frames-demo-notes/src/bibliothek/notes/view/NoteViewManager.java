package bibliothek.notes.view;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockableFocusEvent;
import bibliothek.gui.dock.event.DockableFocusListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.PropertyTransformer;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.model.NoteModelListener;
import bibliothek.notes.view.panels.NoteView;
import bibliothek.util.container.Tuple;
import bibliothek.util.xml.XElement;

/**
 * Manages the connection between {@link Note}s and {@link NoteView}s. Contains
 * various methods to show and hide views for specific <code>Note</code>s.
 * @author Benjamin Sigg
 */
public class NoteViewManager{
    /** link to the docking-frames, used to show and hide views */
	private DockFrontend frontend;
	/** the set of root-{@link DockStation}s */
	private ViewManager manager;
	/** the set of {@link Note}s */
	private NoteModel model;
	
	/** a map containing views for some {@link Note}s */
	private Map<Note, NoteView> noteViews = new HashMap<Note, NoteView>();
	/** the history of the {@link NoteView}s that were or are focused */
	private LinkedList<NoteView> focusedViews = new LinkedList<NoteView>();
	
	/** the location of various {@link Note}s when they were closed the last time */
	private Map<Note, Tuple<DockStation, DockableProperty>> locations =
		new HashMap<Note, Tuple<DockStation,DockableProperty>>();
	
	/**
	 * Creates a new manager.
	 * @param frontend the link to the docking-frames
	 * @param manager the set of the root-{@link DockStation}s
	 * @param model the mutable set of {@link Note}s
	 */
	public NoteViewManager( DockFrontend frontend, ViewManager manager, NoteModel model ){
		this.frontend = frontend;
		this.manager = manager;
		this.model = model;
		
		model.addNoteModelListener( new NoteModelListener(){
			public void noteAdded( NoteModel model, Note note ){
				// ignore
			}
			
			public void noteRemoved( NoteModel model, Note note ){
				hide( note );
				locations.remove( note );
			}
		});
		
		frontend.getController().addDockableFocusListener( new DockableFocusListener(){
		    public void dockableFocused( DockableFocusEvent event ) {
				if( event.getNewFocusOwner() instanceof NoteView ){
					NoteView view = (NoteView)event.getNewFocusOwner();
					focusedViews.remove( view );
					focusedViews.addFirst( view );
				}
			}
		});
	}
	
	/**
	 * Closes the view that shows <code>note</code>. Stores the location
	 * of the view in order to open the view again at the same location.
	 * @param note the <code>Note</code> whose view should be closed
	 */
	public void hide( Note note ){
		NoteView view = noteViews.remove( note );
		if( view != null ){
			DockStation root = DockUtilities.getRoot( view );
			DockableProperty location = DockUtilities.getPropertyChain( root, view );
			locations.put( note, new Tuple<DockStation, DockableProperty>( root, location ) );
			
			DockStation parent = view.getDockParent();
			parent.drag( view );
			view.setNote( null );
			focusedViews.remove( view );
		}
	}
	
	/**
	 * Adds an additional view to the set of known views.
	 * @param view the additional view
	 */
	public void putExternal( NoteView view ){
		noteViews.put( view.getNote(), view );
	}
	
	/**
	 * Shows a view for <code>note</code>. The view will be positioned at the
	 * same location the last view for <code>note</code> was shown. If this is
	 * the first time that the view is shown, it will be positioned at the same
	 * location as the last focused view.
	 * @param note the <code>Note</code> whose view should be shown
	 */
	public void show( Note note ){
		Tuple<DockStation, DockableProperty> location = locations.remove( note );
		
		if( location != null )
			show( note, location.getA(), location.getB() );
		else if( focusedViews.isEmpty() )
			show( note, null );
		else
			show( note, focusedViews.getFirst() );
	}
	
	/**
	 * Opens a view for <code>note</code> at the same location as
	 * <code>location</code>.
	 * @param note the <code>Note</code> which will be shown
	 * @param location the preferred location of the new view, might be <code>null</code>
	 */
	public void show( Note note, Dockable location ){
		if( location == null )
			show( note, null, null );
		else{
			DockStation station = DockUtilities.getRoot( location );
			DockableProperty property = DockUtilities.getPropertyChain( station, location );
			show( note, station, property );
		}
	}
	
	/**
	 * Shows a view for <code>note</code> at the given location as child
	 * of <code>root</code>.
	 * @param note the <code>Note</code> for which a view should be opened
	 * @param root the preferred parent, might be <code>null</code>
	 * @param location the preferred location, relative to <code>root</code>. Might
	 * be <code>null</code>.
	 */
	public void show( Note note, DockStation root, DockableProperty location ){
		NoteView view = noteViews.get( note );
		if( view == null ){
			view = new NoteView( this, model );
			view.setNote( note );
			
			if( root == null || location == null ){
				frontend.getDefaultStation().drop( view );
			}
			else{
				if( !root.drop( view, location )){
					frontend.getDefaultStation().drop( view );
				}
			}
			noteViews.put( note, view );
		}
		frontend.getController().setFocusedDockable( view, false );
	}

	/**
	 * Writes the location of the views of the known {@link Note}s.
	 * @param out the stream to write into
	 * @throws IOException if this method can't write into <code>out</code>
	 */
	public void write( DataOutputStream out ) throws IOException{
	    PropertyTransformer transformer = new PropertyTransformer( frontend.getController() );

	    out.writeInt( locations.size() );
	    for( Map.Entry<Note, Tuple<DockStation, DockableProperty>> location : locations.entrySet() ){
	        out.writeUTF( location.getKey().getId() );
	        out.writeUTF( manager.getName( location.getValue().getA() ) );
	        transformer.write( location.getValue().getB(), out );
	    }
	}

	/**
	 * Reads the location of the views of all known <code>Note</code>s.
	 * @param in the stream to read from
	 * @throws IOException if <code>in</code> can't be read
	 */
	public void read( DataInputStream in ) throws IOException{
	    PropertyTransformer transformer = new PropertyTransformer( frontend.getController() );
		
		int count = in.readInt();
		for( int i = 0; i < count; i++ ){
			Note note = model.getNote( in.readUTF() );
			DockStation station = manager.getStation( in.readUTF() );
			DockableProperty property = transformer.read( in );
			if( note != null ){
				locations.put( note, new Tuple<DockStation, DockableProperty>( station, property ) );
			}
		}
	}
	

    /**
     * Writes the location of the views of the known {@link Note}s.
     * @param element the xml-element to write into, the attributes of
     * <code>element</code> will not be changed
     */
	public void writeXML( XElement element ) throws IOException{
	    PropertyTransformer transformer = new PropertyTransformer( frontend.getController() );
	    
	    for( Map.Entry<Note, Tuple<DockStation, DockableProperty>> location : locations.entrySet() ){
	        XElement xnote = element.addElement( "note" );
	        xnote.addString( "id", location.getKey().getId() );
	        xnote.addString( "station", manager.getName( location.getValue().getA() ) );
	        transformer.writeXML( location.getValue().getB(), xnote );
        }
	}

	/**
	 * Reads the location of the views of all known <code>Note</code>s.
	 * @param element the xml-element to read from
	 */
	public void readXML( XElement element ){
	    PropertyTransformer transformer = new PropertyTransformer( frontend.getController() );

	    for( XElement xnote : element.getElements( "note" )){
	        Note note = model.getNote( xnote.getString( "id" ) );
	        DockStation station = manager.getStation( xnote.getString( "station" ) );
	        DockableProperty property = transformer.readXML( xnote );
	        if( note != null ){
	            locations.put( note, new Tuple<DockStation, DockableProperty>( station, property ) );
	        }
	    }
	}
}
