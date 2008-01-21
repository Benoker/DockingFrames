package demo;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

import bibliothek.gui.*;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.ThemeFactory;
import bibliothek.gui.dock.themes.basic.BasicDockTitle;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.Priority;

/*
 * Sometimes you might like to exchange the titles that are shown
 * for the panels.
 * There are several possibilities:
 * - Override the method Dockable#getDockTitle
 * - Implement your own DockTheme (or customize one of the existing themes)
 * - Use the DockTitleManager to register your own DockFactory
 * 
 * This demo uses the last possibility
 */
public class Demo06_YourOwnTitle {
    public static void main(String[] args) {
        // create a frame
        JFrame frame = new JFrame( "Demo" );
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setSize( 600, 500 );
        
        // the frontend provides additional methods. If a ScreenDockStation is
        // used, then the frontend needs to know the owner of the station.
        DockFrontend frontend = new DockFrontend( frame );
        frontend.getController().setTheme( new BasicTheme() );
        
        registerSpecialTitles( frontend );
        
        // some stations
        SplitDockStation station = new SplitDockStation();
        ScreenDockStation screen = new ScreenDockStation( frame );
        FlapDockStation east = new FlapDockStation();
        FlapDockStation west = new FlapDockStation();
        FlapDockStation south = new FlapDockStation();
        FlapDockStation north = new FlapDockStation();
        
        // the stations have to be registered
        frame.add( station, BorderLayout.CENTER );
        frame.add( east.getComponent(), BorderLayout.EAST );
        frame.add( west.getComponent(), BorderLayout.WEST );
        frame.add( north.getComponent(), BorderLayout.NORTH );
        frame.add( south.getComponent(), BorderLayout.SOUTH );

        // the order matters: it tells something about the importance of the
        // stations. A important station has to be added first
        frontend.addRoot( east, "east" );
        frontend.addRoot( west, "west" );
        frontend.addRoot( north, "north" );
        frontend.addRoot( south, "south" );
        frontend.addRoot( station, "center" );
        frontend.addRoot( screen, "screen" );
        
        // set the station where new panels will be added
        frontend.setDefaultStation( station );
        
        // add something to the controller
        Demo05_CloseAndOpen.addPanels( frontend );
        
        // add the themes
        frame.setJMenuBar( createMenuBar( frontend ));
        
        // make the whole thing visible
        frame.setVisible( true );
        screen.setShowing( true );
    }
    
	public static JMenuBar createMenuBar( DockFrontend frontend ){
		JMenuBar bar = new JMenuBar();
		bar.add( createThemeMenu( frontend.getController() ));
		bar.add( Demo04_LoadAndSave.createSaveLoadMenu( frontend ));
		bar.add( Demo05_CloseAndOpen.createCloseOpenMenu( frontend ));
		return bar;
	}
	
	public static JMenu createThemeMenu( DockController controller ){
		JMenu menu = new JMenu( "Theme" );
        
        for( ThemeFactory factory : DockUI.getDefaultDockUI().getThemes() ){
            menu.add( Demo03_Theme.createItem( factory.getName(), factory.getDescription(), factory.create(), controller));
        }
        /*
        menu.add( Demo03_Theme.createItem( "Default", "Default", new DefaultTheme(), controller ));
        menu.add( Demo03_Theme.createItem( "small Default", "small Default", new NoStackTheme( new DefaultTheme() ), controller ));
        menu.add( Demo03_Theme.createItem( "Smooth", "Smooth", new SmoothTheme(), controller ));
        menu.add( Demo03_Theme.createItem( "small Smooth", "small Smooth", new NoStackTheme( new SmoothTheme() ), controller ));
        menu.add( Demo03_Theme.createItem( "Flat", "Flat", new FlatTheme(), controller ));
        menu.add( Demo03_Theme.createItem( "small Flat", "small Flat", new NoStackTheme( new FlatTheme() ), controller ));
        */
        return menu;
	}
	
    public static void registerSpecialTitles( DockFrontend frontend ){
        DockTitleManager manager = frontend.getController().getDockTitleManager();
        DemoFactory factory = new DemoFactory();
        
        // the new factory has to be used at various locations
        manager.registerClient( SplitDockStation.TITLE_ID, factory );
        manager.registerClient( ScreenDockStation.TITLE_ID, factory );
        manager.registerClient( FlapDockStation.WINDOW_TITLE_ID, factory );
        manager.registerClient( StackDockStation.TITLE_ID, factory );
    }
    
    private static class DemoFactory implements DockTitleFactory{
        public DockTitle createDockableTitle( Dockable dockable, DockTitleVersion version ) {
            // ok, getting the background color could be done in a nicer way...
            Color background = ((DefaultDockable)dockable).getContentPane().getComponent( 0 ).getBackground();
            
            Color dark = change( background, -35 );
            Color bright = change( background, 35 );
            
            BasicDockTitle title = new BasicDockTitle( dockable, version );
            title.setActiveLeftColor( dark );
            title.setActiveRightColor( bright );
            title.setActiveTextColor( Color.BLACK );
            
            title.setInactiveLeftColor( bright );
            title.setInactiveRightColor( bright );
            title.setInactiveTextColor( dark );
            
            return title;
        }
        
        private Color change( Color base, int delta ){
            return new Color(
                    Math.max( 0, Math.min( 0xFF, base.getRed() + delta )),
                    Math.max( 0, Math.min( 0xFF, base.getGreen() + delta )),
                    Math.max( 0, Math.min( 0xFF, base.getBlue() + delta )));
        }

        public <D extends Dockable & DockStation> DockTitle createStationTitle( D dockable, DockTitleVersion version ) {
            // let's use the default factory
            DockTitleFactory factory = version.getFactory( Priority.THEME );
            if( factory == null )
                factory = version.getFactory( Priority.DEFAULT );
            
            return factory.createStationTitle( dockable, version );
        }
    }
}
