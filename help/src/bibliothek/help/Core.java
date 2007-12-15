package bibliothek.help;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.WindowConstants;

import bibliothek.demonstration.Monitor;
import bibliothek.demonstration.util.LookAndFeelMenu;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.security.GlassedPane;
import bibliothek.gui.dock.security.SecureDockController;
import bibliothek.gui.dock.security.SecureFlapDockStation;
import bibliothek.gui.dock.security.SecureFlapDockStationFactory;
import bibliothek.gui.dock.security.SecureScreenDockStation;
import bibliothek.gui.dock.security.SecureScreenDockStationFactory;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.ScreenDockStation;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.support.lookandfeel.ComponentCollector;
import bibliothek.gui.dock.support.lookandfeel.LookAndFeelList;
import bibliothek.gui.dock.themes.ThemeFactory;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.help.control.LinkManager;
import bibliothek.help.control.URManager;
import bibliothek.help.control.actions.RedoDockAction;
import bibliothek.help.control.actions.UndoDockAction;
import bibliothek.help.model.HelpModel;
import bibliothek.help.util.ResourceSet;
import bibliothek.help.view.LayoutMenu;
import bibliothek.help.view.PanelMenu;
import bibliothek.help.view.SelectingView;
import bibliothek.help.view.TypeHierarchyView;
import bibliothek.help.view.dock.Minimizer;

/**
 * This is the central point of the application. The core is responsible to
 * startup and to shutdown the application. The core puts all elements
 * together on startup, and frees resources on shutdown.
 * @author Benjamin Sigg
 *
 */
public class Core implements ComponentCollector{
    /** whether this application runs in an secure environment or not */
	private boolean secure;
	/** monitor to inform when the state of this application changes */
	private Monitor monitor;
	
	/** main access to the docking framework */
	private DockFrontend frontend;
	
	/** dockable screen */
	private ScreenDockStation screen;
	/** central docking station */
	private SplitDockStation station;
	/** the applications main frame */
	private JFrame frame;
	
	/** whether the {@link DockTheme} currently is changing */
	private boolean onThemeUpdate = false;
	
	/**
	 * Creates a new core.
	 * @param secure whether the application should run in a restricted
	 * environment
	 * @param monitor observer to be informed when the application starts
	 * or shuts down, can be <code>null</code>
	 */
	public Core( boolean secure, Monitor monitor ){
		this.secure = secure;
		this.monitor = monitor;
	}
	
	/**
	 * Loads the model, creates the graphical user interface and shows it.
	 */
	public void startup(){
		try{
	        buildContent();
	        
	        HelpModel model = new HelpModel( "/data/bibliothek/help/help.data" );
	        LinkManager links = new LinkManager();
	        links.setModel( model );
	        
	        URManager ur = links.getUR();
	        UndoDockAction actionUndo = new UndoDockAction( ur );
	        RedoDockAction actionRedo = new RedoDockAction( ur );
	        
	        final DefaultDockActionSource actions = new DefaultDockActionSource( actionUndo, actionRedo );
	        frontend.getController().addActionGuard( new ActionGuard(){
	        	public boolean react( Dockable dockable ){
	        		return dockable.asDockStation() == null;
	        	}
	        	
	        	public DockActionSource getSource( Dockable dockable ){
	        		return actions;
	        	}
	        });
	        frontend.getController().getProperties().set( PropertyKey.DOCK_STATION_ICON, ResourceSet.ICONS.get( "application" ) );
	        frontend.getController().getProperties().set( PropertyKey.DOCK_STATION_TITLE, "Help" );
	        
	        SelectingView viewPackage = new SelectingView( links, "Packages", ResourceSet.ICONS.get( "package" ), "package-list" );
	        SelectingView viewClasses = new SelectingView( links, "Classes", ResourceSet.ICONS.get( "class" ), "class-list" );
	        SelectingView viewFields = new SelectingView( links, "Fields", ResourceSet.ICONS.get( "field" ), "field-list" );
	        SelectingView viewConstructors = new SelectingView( links, "Constructors", ResourceSet.ICONS.get( "constructor" ), "constructor-list" );
	        SelectingView viewMethods = new SelectingView( links, "Methods", ResourceSet.ICONS.get( "method" ), "method-list" );
	        SelectingView viewContent = new SelectingView( links, "Content", ResourceSet.ICONS.get( "content" ), "class",
	                "constructor-list", "constructor",
	                "field-list", "field",
	                "method-list", "method" );
	        TypeHierarchyView viewHierarchy = new TypeHierarchyView( links );
	        
	        links.select( "package-list:root" );
	        
	        frontend.add( viewPackage, "packages" );
	        frontend.add( viewClasses, "classes" );
	        frontend.add( viewFields, "fields" );
	        frontend.add( viewConstructors, "constructors" );
	        frontend.add( viewMethods, "methods" );
	        frontend.add( viewContent, "content" );
	        frontend.add( viewHierarchy, "hierarchy" );
	        
	        SplitDockGrid grid = new SplitDockGrid(  );
	        grid.addDockable( 0, 0, 1, 1, viewPackage );
	        grid.addDockable( 0, 1, 1, 2, viewClasses );
	        grid.addDockable( 0, 3, 1, 1, viewConstructors );
	        grid.addDockable( 1, 3, 1, 1, viewFields );
	        grid.addDockable( 2, 3, 1, 1, viewMethods );
	        grid.addDockable( 1, 0, 2, 3, viewContent );
	        grid.addDockable( 3, 0, 1, 3, viewHierarchy );
	        
	        station.dropTree( grid.toTree() );
	        buildMenu();
	        
	        frame.setVisible( true );
	        screen.setShowing( true );
		}
		catch( IOException ex ){
			ex.printStackTrace();
		}
		finally{
			if( monitor != null ){
				monitor.publish( this );
				monitor.running();
			}
		}
	}
	
	/**
	 * Closes the graphical user interface and frees resources. Exits the 
	 * application if no {@link Monitor} was provided through the constructor.
	 */
	public void shutdown(){
		frame.setVisible( false );
		frame.dispose();
		screen.setShowing( false );
		frontend.getController().kill();
		if( monitor == null )
			System.exit( 0 );
		else
			monitor.shutdown();
	}
	
	public Collection<Component> listComponents(){
		List<Component> list = new ArrayList<Component>();
		list.add( frame );
		for( Dockable d : frontend.getController().getRegister().listDockables() )
			list.add( d.getComponent() );
		return list;
	}
	
	/**
	 * Creates the main frame and all {@link DockStation}s.
	 */
	private void buildContent(){
		FlapDockStation north, south, east, west;
        frame = new JFrame();
        frame.setTitle( "Help - Demonstration of DockingFrames" );
        frame.setIconImage( ResourceSet.toImage( ResourceSet.ICONS.get( "application" ) ) );
        Container content;
        
        if( secure ){
        	SecureDockController controller = new SecureDockController();
        	controller.setSingleParentRemove( true );
        	frontend = new DockFrontend( controller, frame );
        	frontend.registerFactory( new SecureScreenDockStationFactory( frame ));
        	frontend.registerFactory( new SecureFlapDockStationFactory() );
        	
        	north = new SecureFlapDockStation();
        	south = new SecureFlapDockStation();
        	east = new SecureFlapDockStation();
        	west = new SecureFlapDockStation();
        	screen = new SecureScreenDockStation( frame );
        	
        	GlassedPane glass = new GlassedPane();
        	content = glass.getContentPane();
        	frame.setContentPane( glass );
        	controller.getFocusObserver().addGlassPane( glass );
        }
        else{
        	frontend = new DockFrontend( frame );
        	
        	north = new FlapDockStation();
        	south = new FlapDockStation();
        	east = new FlapDockStation();
        	west = new FlapDockStation();
        	screen = new ScreenDockStation( frame );
        	
        	content = frame.getContentPane();
        }
        
        Minimizer minimizer = new Minimizer( this, frontend.getController() );
        
        station = new SplitDockStation();
        minimizer.addAreaNormalized( station );
        minimizer.addAreaNormalized( screen );
        minimizer.setDefaultStation( station );
        
        content.setLayout( new BorderLayout() );
        content.add( station, BorderLayout.CENTER );
        content.add( south.getComponent(), BorderLayout.SOUTH );
        content.add( north.getComponent(), BorderLayout.NORTH );
        content.add( east.getComponent(), BorderLayout.EAST );
        content.add( west.getComponent(), BorderLayout.WEST );
        
        minimizer.addAreaMinimized( north, SplitDockProperty.NORTH );
        minimizer.addAreaMinimized( south, SplitDockProperty.SOUTH );
        minimizer.addAreaMinimized( east, SplitDockProperty.EAST );
        minimizer.addAreaMinimized( west, SplitDockProperty.WEST );
        
        frame.setBounds( 20, 20, 800, 600 );
        frame.setTitle( "Help - Demonstration of DockingFrames" );
        frame.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
        frame.addWindowListener( new WindowAdapter(){
        	@Override
        	public void windowClosing( WindowEvent e ){
        		shutdown();
        	}
        });
        
        frontend.addRoot( north, "north" );
        frontend.addRoot( south, "south" );
        frontend.addRoot( east, "east" );
        frontend.addRoot( west, "west" );
        frontend.addRoot( station, "root" );
        frontend.addRoot( screen, "screen" );
	}
	
	/**
	 * Builds the menubar and adds it to {@link #frame}
	 */
	private void buildMenu(){
		JMenuBar menubar = new JMenuBar();
		menubar.add( new PanelMenu( frontend ) );
		
		menubar.add( new LayoutMenu( frontend ) );
		
		JMenu themes = new JMenu( "Themes" );
		menubar.add( themes );
		
		LookAndFeelList list;
		if( monitor == null ){
			list = LookAndFeelList.getDefaultList();
			list.addComponentCollector( this );
		}
		else
			list = monitor.getGlobalLookAndFeel();
		
		themes.add( new LookAndFeelMenu( frame, list ) );
		themes.add( createThemeMenu() );
		
		frame.setJMenuBar( menubar );
	}
	
	/**
	 * Creates a menu that contains items to change the {@link DockTheme}.
	 * @return the newly created menu
	 */
	private JMenu createThemeMenu(){
		JMenu dockTheme = new JMenu( "Theme" );
		ButtonGroup group = new ButtonGroup();
		boolean first = true;
		
		for( final ThemeFactory factory : DockUI.getDefaultDockUI().getThemes()){
			JRadioButtonMenuItem item = new JRadioButtonMenuItem( factory.getName() );
			if( first ){
				item.setSelected( true );
				frontend.getController().setTheme( factory.create() );
				first = false;
			}
			
			group.add( item );
			item.setToolTipText( factory.getDescription() );
			item.addActionListener( new ActionListener(){
				public void actionPerformed( ActionEvent e ){
					onThemeUpdate = true;
					frontend.getController().setTheme( factory.create() );
					onThemeUpdate = false;
				}
			});
			dockTheme.add( item );
		}
		return dockTheme;
	}
	
	/**
	 * Tells whether the {@link DockTheme} is currently exchanging or not.
	 * @return <code>true</code> if the application currently changes the
	 * theme
	 */
	public boolean isOnThemeUpdate(){
		return onThemeUpdate;
	}
}
