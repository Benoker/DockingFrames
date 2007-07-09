package bibliothek.help;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import bibliothek.demonstration.Monitor;
import bibliothek.demonstration.util.ComponentCollector;
import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.security.GlassedPane;
import bibliothek.gui.dock.security.SecureDockController;
import bibliothek.gui.dock.security.SecureFlapDockStation;
import bibliothek.gui.dock.security.SecureScreenDockStation;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.ScreenDockStation;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.help.control.LinkManager;
import bibliothek.help.control.URManager;
import bibliothek.help.control.actions.RedoDockAction;
import bibliothek.help.control.actions.UndoDockAction;
import bibliothek.help.model.HelpModel;
import bibliothek.help.util.ResourceSet;
import bibliothek.help.view.SelectingView;
import bibliothek.help.view.TypeHierarchyView;
import bibliothek.help.view.dock.Minimizer;

public class Core implements ComponentCollector{
	private boolean secure;
	private Monitor monitor;
	
	private DockFrontend frontend;
	
	private ScreenDockStation screen;
	private SplitDockStation station;
	private JFrame frame;
	
	public Core( boolean secure, Monitor monitor ){
		this.secure = secure;
		this.monitor = monitor;
	}
	
	public void startup(){
		try{
	        buildMainFrame();
	        
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
	        
	        SplitDockGrid grid = new SplitDockGrid(  );
	        grid.addDockable( 0, 0, 1, 1, viewPackage );
	        grid.addDockable( 0, 1, 1, 2, viewClasses );
	        grid.addDockable( 0, 3, 1, 1, viewConstructors );
	        grid.addDockable( 1, 3, 1, 1, viewFields );
	        grid.addDockable( 2, 3, 1, 1, viewMethods );
	        grid.addDockable( 1, 0, 2, 3, viewContent );
	        grid.addDockable( 3, 0, 1, 3, viewHierarchy );
	        
	        station.dropTree( grid.toTree() );
	        
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
	
	public void shutdown(){
		frame.setVisible( false );
		screen.setShowing( false );
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
	
	private void buildMainFrame(){
		FlapDockStation north, south, east, west;
        frame = new JFrame();
        Container content;
        
        if( secure ){
        	SecureDockController controller = new SecureDockController();
        	controller.setSingleParentRemove( true );
        	frontend = new DockFrontend( controller );
        	
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
        	frontend = new DockFrontend();
        	
        	north = new FlapDockStation();
        	south = new FlapDockStation();
        	east = new FlapDockStation();
        	west = new FlapDockStation();
        	screen = new ScreenDockStation( frame );
        	
        	content = frame.getContentPane();
        }
        
        Minimizer minimizer = new Minimizer( frontend.getController() );
        
        station = new SplitDockStation();
        minimizer.addAreaMaximized( station );
        minimizer.addAreaMaximized( screen );
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
        frontend.getController().setTheme( new NoStackTheme( new FlatTheme() ) );
	}
}
