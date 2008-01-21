package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.themes.*;

/*
 * There are various themes which can be used. This demo shows
 * how to include them.
 */

public class Demo03_Theme {
	public static void main(String[] args) {
		// create a frame
		JFrame frame = new JFrame( "Demo" );
		frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
		frame.setSize( 600, 500 );
		
		// the controller manages all operations
		DockController controller = new DockController();
		
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
		controller.add( east );
		controller.add( west );
		controller.add( north );
		controller.add( south );
		controller.add( station );
		controller.add( screen );
		
		// create two panels
		JPanel black = new JPanel();
		black.setBackground( Color.BLACK );
		black.setOpaque( true );
		
		JPanel green = new JPanel();
		green.setBackground( Color.GREEN );
		green.setOpaque( true );
		
		// add the two panels
		station.drop( new DefaultDockable( black, "Black" ));
		station.drop( new DefaultDockable( green, "Green" ));
		
		// add the themes
		frame.setJMenuBar( createMenuBar( controller ));
		
		// make the whole thing visible
		frame.setVisible( true );
		screen.setShowing( true );
	}
	
	public static JMenuBar createMenuBar( DockController controller ){
		JMenuBar bar = new JMenuBar();
		bar.add( createThemeMenu( controller ));
		return bar;
	}
	
	public static JMenu createThemeMenu( DockController controller ){
		JMenu menu = new JMenu( "Theme" );
		/*menu.add( createItem( "Default", "Default", new BasicTheme(), controller ));
		menu.add( createItem( "small Default", "small Default", new NoStackTheme( new BasicTheme() ), controller ));
		menu.add( createItem( "Smooth", "Smooth", new SmoothTheme(), controller ));
		menu.add( createItem( "small Smooth", "small Smooth", new NoStackTheme( new SmoothTheme() ), controller ));
		menu.add( createItem( "Flat", "Flat", new FlatTheme(), controller ));
		menu.add( createItem( "small Flat", "small Flat", new NoStackTheme( new FlatTheme() ), controller ));*/
        
        for( ThemeFactory factory : DockUI.getDefaultDockUI().getThemes() ){
            menu.add( createItem( factory.getName(), factory.getDescription(), factory.create(), controller));
        }
        
		return menu;
	}
	
	public static JMenuItem createItem( String text, String tooltip, final DockTheme theme, final DockController controller ){
		JMenuItem item = new JMenuItem( text );
        item.setToolTipText( tooltip );
		item.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				controller.setTheme( theme );
			}
		});
		return item;
	}
}
