package bibliothek.notes.view;

import java.awt.Window;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.actions.Hide;
import bibliothek.notes.view.actions.NoteDeleteAction;
import bibliothek.notes.view.panels.ListView;

/**
 * The set of all root-{@link DockStation}s used in this application. Additionally
 * there are other dock-related properties.
 * @author Benjamin Sigg
 *
 */
public class ViewManager {
    /** the {@link Dockable} that shows a list of all {@link Note}s */
	private ListView list;
	/** the manager that can show and hide views for {@link Note}s */
	private NoteViewManager notes;
	
	/** the {@link DockStation} in the center of the {@link MainFrame} */
	private SplitDockStation split;
	/** the {@link DockStation} at the east side of the {@link MainFrame} */
	private FlapDockStation east;
	/** the {@link DockStation} at the west side of the {@link MainFrame} */
	private FlapDockStation west;
	/** the {@link DockStation} at the south side of the {@link MainFrame} */
	private FlapDockStation south;
	/** the {@link DockStation} at the north side of the {@link MainFrame} */
	private FlapDockStation north;
	/** the {@link DockStation} that represents the screen */
	private ScreenDockStation screen;
	
	/** link to the docking-frames */
	private DockFrontend frontend;
	
	/**
	 * Creates the new manager
	 * @param frontend link to the docking-frames
	 * @param owner the window used as parent for all dialogs
	 * @param secure whether this application runs in webstart or not
	 * @param model the set of {@link Note}s
	 */
	public ViewManager( DockFrontend frontend, Window owner, boolean secure, NoteModel model ){
		this.frontend = frontend;
		
		notes = new NoteViewManager( frontend, this, model );
		list = new ListView( notes, model );
		
		frontend.getController().addActionGuard( new NoteDeleteAction( model ));
		frontend.getController().addActionGuard( new Hide( frontend, notes ));
		
		frontend.getController().getProperties().set( PropertyKey.DOCK_STATION_TITLE, "Notes" );
		frontend.getController().getProperties().set( PropertyKey.DOCK_STATION_ICON, ResourceSet.APPLICATION_ICONS.get( "application" ) );
		
		frontend.addDockable( "list", list );
		
		split = new SplitDockStation();
		east = new FlapDockStation();
		west = new FlapDockStation();
		south = new FlapDockStation();
		north = new FlapDockStation();
		screen = new ScreenDockStation( owner );
		
		frontend.addRoot( "screen", screen );
		frontend.addRoot( "split", split );
		frontend.addRoot( "east", east );
		frontend.addRoot( "west", west );
		frontend.addRoot( "south", south );
		frontend.addRoot( "north", north );

		frontend.setDefaultStation( split );
	}
	
	/**
	 * Gets the {@link DockStation} that is on the east side of the {@link MainFrame}.
	 * @return the station in the east
	 */
	public FlapDockStation getEast(){
		return east;
	}

	/**
     * Gets the {@link DockStation} that is on the north side of the {@link MainFrame}.
     * @return the station in the north
     */
	public FlapDockStation getNorth(){
		return north;
	}
	
	/**
	 * Gets the {@link DockStation} which represents the screen.
	 * @return the screen-station
	 */
	public ScreenDockStation getScreen(){
		return screen;
	}
	
	/**
     * Gets the {@link DockStation} that is on the south side of the {@link MainFrame}.
     * @return the station in the south
     */
    public FlapDockStation getSouth(){
		return south;
	}
	
    /**
     * Gets the {@link DockStation} that is in the center of the {@link MainFrame}.
     * @return the station in the center
     */
	public SplitDockStation getSplit(){
		return split;
	}
	
	/**
     * Gets the {@link DockStation} that is on the west side of the {@link MainFrame}.
     * @return the station in the west
     */
    public FlapDockStation getWest(){
		return west;
	}
	
    /**
     * Gets the unique identifier which is used for a certain station.
     * @param station one of the stations known to this manager
     * @return the identifier or <code>null</code> if the station
     * is unknown
     */
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
	
	/**
	 * Gets the link to the docking-frames.
	 * @return the link
	 */
	public DockFrontend getFrontend(){
		return frontend;
	}
	
	/**
	 * Gets a station identified by its unique identifier.
	 * @param name the unique identifier
	 * @return the station or <code>null</code>
	 */
	public DockStation getStation( String name ){
		return frontend.getRoot( name );
	}
	
	/**
	 * Gets the {@link Dockable} list of {@link Note}s.
	 * @return the list
	 */
	public ListView getList(){
		return list;
	}
	
	/**
	 * Gets the manager that is used to show or hide views
	 * for {@link Note}s.
	 * @return the manager
	 */
	public NoteViewManager getNotes(){
		return notes;
	}
}
