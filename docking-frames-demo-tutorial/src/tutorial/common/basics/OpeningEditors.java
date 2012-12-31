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

@Tutorial( title="Opening editors", id="openingEditors" )
public class OpeningEditors {
	public static void main( String[] args ){
		JTutorialFrame frame = new JTutorialFrame( OpeningEditors.class );
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
				work.show( editor );
				editor.toFront();
			}
		});
		
		frame.setVisible( true );
	}
}
