package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.event.DockFrontendListener;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.ScreenDockStation;
import bibliothek.gui.dock.station.SplitDockStation;

/*
 * Not all panels should be visible the whole time, the DockFrontend
 * provides a feature to hide panels.
 */
public class Demo05_CloseAndOpen {
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
		
		// set the station where new panels will be added
		frontend.setDefaultStation( station );
		
		// add something to the controller
		addPanels( frontend );
		
		// add the themes
		frame.setJMenuBar( createMenuBar( frontend ));
		
		// make the whole thing visible
		frame.setVisible( true );
		screen.setShowing( true );
	}
	
	public static void addPanels( DockFrontend frontend ){
		Map<String, Color> colors = new HashMap<String, Color>();
		
		colors.put( "Red", Color.RED );
		colors.put( "Green", Color.GREEN );
		colors.put( "Blue", Color.BLUE );
		colors.put( "Yellow", Color.YELLOW );
		colors.put( "Black", Color.BLACK );
		colors.put( "Cyan", Color.CYAN );
		colors.put( "Gray", Color.GRAY );
		colors.put( "Magenta", Color.MAGENTA );
		colors.put( "Orange", Color.ORANGE );
		colors.put( "Pink", Color.PINK );
		colors.put( "White", Color.WHITE );
		
		for( String name : colors.keySet() ){
			JPanel panel = new JPanel();
			panel.setBackground( colors.get( name ));
			panel.setOpaque( true );
			Dockable dockable = new DefaultDockable( panel, name );
			frontend.add( dockable, name );
		}
	}
	
	public static JMenuBar createMenuBar( DockFrontend frontend ){
		JMenuBar bar = new JMenuBar();
		bar.add( Demo03_Theme.createThemeMenu( frontend.getController() ));
		bar.add( Demo04_LoadAndSave.createSaveLoadMenu( frontend ));
		bar.add( createCloseOpenMenu( frontend ));
		return bar;
	}
	
	public static JMenu createCloseOpenMenu( DockFrontend frontend ){
		JMenu menu = new JMenu( "Panels" );
		for( Dockable dockable : frontend.getDockables() ){
			menu.add( createCloseOpenItem( dockable, frontend ));
		}
		return menu;
	}
	
	public static JMenuItem createCloseOpenItem( final Dockable dockable, final DockFrontend frontend ){
		final JCheckBoxMenuItem item = new JCheckBoxMenuItem( dockable.getTitleText() );
		item.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if( item.isSelected() )
					frontend.show( dockable );
				else
					frontend.hide( dockable );
			}
		});
		
		frontend.addFrontendListener( new DockFrontendListener(){
			public void hidden(DockFrontend fronend, Dockable affected) {
				if( affected == dockable )
					item.setSelected( false );
			}

			public void showed(DockFrontend frontend, Dockable affected) {
				if( affected == dockable )
					item.setSelected( true );
			}
			
			public void deleted(DockFrontend frontend, String name) {
				// ignore
			}

			public void loaded(DockFrontend frontend, String name) {
				// ignore
			}

			public void saved(DockFrontend frontend, String name) {
				// ignore
			}
		});
		
		return item;
	}
}
