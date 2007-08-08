package bibliothek.notes.view;

import java.awt.Window;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.dock.security.SecureFlapDockStation;
import bibliothek.gui.dock.security.SecureScreenDockStation;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.ScreenDockStation;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.actions.Hide;
import bibliothek.notes.view.actions.NoteDeleteAction;
import bibliothek.notes.view.panels.ListView;

public class ViewManager {
	private ListView list;
	private NoteViewManager notes;
	
	private SplitDockStation split;
	private FlapDockStation east, west, south, north;
	private ScreenDockStation screen;
	
	private DockFrontend frontend;
	
	public ViewManager( DockFrontend frontend, Window owner, boolean secure, NoteModel model ){
		this.frontend = frontend;
		
		notes = new NoteViewManager( frontend, this, model );
		list = new ListView( notes, model );
		
		frontend.getController().addActionGuard( new NoteDeleteAction( model ));
		frontend.getController().addActionGuard( new Hide( frontend, notes ));
		
		frontend.getController().getProperties().set( PropertyKey.DOCK_STATION_TITLE, "Notes" );
		frontend.getController().getProperties().set( PropertyKey.DOCK_STATION_ICON, ResourceSet.APPLICATION_ICONS.get( "application" ) );
		
		frontend.add( list, "list" );
		
		if( secure ){
			split = new SplitDockStation();
			east = new SecureFlapDockStation();
			west = new SecureFlapDockStation();
			south = new SecureFlapDockStation();
			north = new SecureFlapDockStation();
			screen = new SecureScreenDockStation( owner );
		}
		else{
			split = new SplitDockStation();
			east = new FlapDockStation();
			west = new FlapDockStation();
			south = new FlapDockStation();
			north = new FlapDockStation();
			screen = new ScreenDockStation( owner );
		}

		frontend.addRoot( screen, "screen" );
		frontend.addRoot( split, "split" );
		frontend.addRoot( east, "east" );
		frontend.addRoot( west, "west" );
		frontend.addRoot( south, "south" );
		frontend.addRoot( north, "north" );

		frontend.setDefaultStation( split );
	}
	
	public FlapDockStation getEast(){
		return east;
	}
	
	public FlapDockStation getNorth(){
		return north;
	}
	
	public ScreenDockStation getScreen(){
		return screen;
	}
	
	public FlapDockStation getSouth(){
		return south;
	}
	
	public SplitDockStation getSplit(){
		return split;
	}
	
	public FlapDockStation getWest(){
		return west;
	}
	
	public String getName( DockStation station ){
		if( station == split )
			return "split";
		if( station == east )
			return "east";
		if( station == west )
			return "west";
		if( station == north )
			return "north";
		if( station == south )
			return "south";
		if( station == screen )
			return "screen";
		
		return null;
	}
	
	public DockFrontend getFrontend(){
		return frontend;
	}
	
	public DockStation getStation( String name ){
		return frontend.getRoot( name );
	}
	
	public ListView getList(){
		return list;
	}
	
	public NoteViewManager getNotes(){
		return notes;
	}
}
