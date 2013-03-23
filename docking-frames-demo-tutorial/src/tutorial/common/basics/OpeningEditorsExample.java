package tutorial.common.basics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.DefaultSingleCDockable;

@Tutorial( title="Opening a CDockable on a CWorkingArea", id="OpeningEditors" )
public class OpeningEditorsExample {
	public static void main( String[] args ){
		/* A common task is to open yet another CDockable. When opening a CDockable we want that dockable 
		 * to show up close to the currently focused dockable. CDockable offers several methods to do that,
		 * all have a name like "CDockable.setLocationsAside...".
		 * 
		 * If opening CDockables on a CWorkingArea we can also make use of the "show" method, which not only
		 * sets the location, but also registers the Dockable at the CControl. */
		
		JTutorialFrame frame = new JTutorialFrame( OpeningEditorsExample.class );
		CControl control = new CControl( frame );
		frame.destroyOnClose( control );
		
		frame.add( control.getContentArea() );
		
		final CWorkingArea work = control.createWorkingArea( "work" );
		
		CGrid grid = new CGrid( control );
		grid.add( 1, 1, 3, 3, work );
		grid.add( 0, 0, 1, 4, new DefaultSingleCDockable( "Outline" ));
		grid.add( 1, 3, 3, 1, new DefaultSingleCDockable( "Console" ));
		control.getContentArea().deploy( grid );
		
		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu( "Editors" );
		menubar.add( menu );
		JMenuItem openEditor = new JMenuItem( "Open new editor" );
		menu.add( openEditor );
		frame.setJMenuBar( menubar );
		
		openEditor.addActionListener( new ActionListener(){
			private int count = 0;
			
			public void actionPerformed( ActionEvent e ){
				DefaultMultipleCDockable editor = new DefaultMultipleCDockable( null );
				editor.setTitleText( "Editor " + (count++) );
				editor.setCloseable( true );
				
				/* All that is needed to show "editor" aside the currently focused CDockable, is calling "show". */
				work.show( editor );
				editor.toFront();
			}
		});
		
		frame.setVisible( true );
	}
}
