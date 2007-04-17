package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.ScreenDockStation;
import bibliothek.gui.dock.station.SplitDockStation;

/*
 * The DockFrontend provides some useful features, for example the ability
 * to store the layout of the panels.
 */

public class Demo04_LoadAndSave {
	public static void main(String[] args) {
		// create a frame
		JFrame frame = new JFrame( "Demo" );
		frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
		frame.setSize( 600, 500 );
		
		// the frontend provides additional methods. If a ScreenDockStation is
		// used, then the frontend needs to know the owner of the station.
		DockFrontend frontend = new DockFrontend( frame );
		
		// let the controller handle sub-stations with only one child
		frontend.getController().setSingleParentRemove( true );
		
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
		
		// add something to the controller
		addPanels( frontend, station );
		
		// add the themes
		frame.setJMenuBar( createMenuBar( frontend ));
		
		// make the whole thing visible
		frame.setVisible( true );
		screen.setShowing( true );
	}
	
	public static void addPanels( DockFrontend frontend, DockStation station ){
		Map<String, Color> colors = new HashMap<String, Color>();
		
		colors.put( "Red", Color.RED );
		colors.put( "Green", Color.GREEN );
		colors.put( "Blue", Color.BLUE );
		colors.put( "Yellow", Color.YELLOW );
		
		for( String name : colors.keySet() ){
			JPanel panel = new JPanel();
			panel.setBackground( colors.get( name ));
			panel.setOpaque( true );
			Dockable dockable = new DefaultDockable( panel, name );
			station.drop( dockable );
			
			// the Dockables have to be registered at the frontend.
			// DefaultDockables, which are not registered, will lose their content
			// when they are reloaded. You can use a DockFactory to provide your
			// own methods how to save and load a Dockable.
			frontend.add( dockable, name );
			frontend.setHideable( dockable, false );
		}
	}
	
	public static JMenuBar createMenuBar( DockFrontend frontend ){
		JMenuBar bar = new JMenuBar();
		bar.add( Demo03_Theme.createThemeMenu( frontend.getController() ));
		bar.add( createSaveLoadMenu( frontend ));
		return bar;
	}
	
	public static JMenu createSaveLoadMenu( final DockFrontend frontend ){
		final JMenu menu = new JMenu( "Layout" );
		JMenuItem save = new JMenuItem( "Save" );
		menu.add( save );
		menu.addSeparator();
		
		save.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String name = "Entry " + (frontend.getSettings().size()+1);
				frontend.save( name );
				menu.add( createLoadItem( name, frontend ));
			}
		});
		return menu;
	}
	
	public static JMenuItem createLoadItem( final String name, final DockFrontend frontend ){
		JMenuItem item = new JMenuItem( name );
		item.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				frontend.load( name );
			}
		});
		return item;
	}
}
