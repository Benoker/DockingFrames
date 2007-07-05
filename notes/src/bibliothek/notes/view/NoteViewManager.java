package bibliothek.notes.view;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableProperty;
import bibliothek.gui.dock.PropertyTransformer;
import bibliothek.gui.dock.event.DockControllerAdapter;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.model.NoteModelListener;
import bibliothek.notes.view.panels.NoteView;
import bibliothek.util.container.Tuple;

public class NoteViewManager{
	private DockFrontend frontend;
	private ViewManager manager;
	private NoteModel model;
	
	private Map<Note, NoteView> noteViews = new HashMap<Note, NoteView>();
	private LinkedList<NoteView> focusedViews = new LinkedList<NoteView>();
	
	private Map<Note, Tuple<DockStation, DockableProperty>> locations =
		new HashMap<Note, Tuple<DockStation,DockableProperty>>();
	
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
		
		frontend.getController().addDockControllerListener( new DockControllerAdapter(){
			@Override
			public void dockableFocused( DockController controller, Dockable dockable ){
				if( dockable instanceof NoteView ){
					NoteView view = (NoteView)dockable;
					focusedViews.remove( view );
					focusedViews.addFirst( view );
				}
			}
		});
	}
	
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
	
	public void putExternal( NoteView view ){
		noteViews.put( view.getNote(), view );
	}
	
	public void show( Note note ){
		Tuple<DockStation, DockableProperty> location = locations.remove( note );
		
		if( location != null )
			show( note, location.getA(), location.getB() );
		else if( focusedViews.isEmpty() )
			show( note, null );
		else
			show( note, focusedViews.getFirst() );
	}
	
	public void show( Note note, Dockable location ){
		if( location == null )
			show( note, null, null );
		else{
			DockStation station = DockUtilities.getRoot( location );
			DockableProperty property = DockUtilities.getPropertyChain( station, location );
			show( note, station, property );
		}
	}
	
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
	
	public void read( DataInputStream in ) throws IOException{
		PropertyTransformer transformer = new PropertyTransformer();
		
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
	
	public void write( DataOutputStream out ) throws IOException{
		PropertyTransformer transformer = new PropertyTransformer();
		
		out.writeInt( locations.size() );
		for( Map.Entry<Note, Tuple<DockStation, DockableProperty>> location : locations.entrySet() ){
			out.writeUTF( location.getKey().getId() );
			out.writeUTF( manager.getName( location.getValue().getA() ) );
			transformer.write( location.getValue().getB(), out );
		}
	}
}
