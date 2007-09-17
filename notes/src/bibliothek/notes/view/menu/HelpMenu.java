package bibliothek.notes.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import bibliothek.notes.view.MainFrame;

/**
 * The "about" menu, presented in the menubar of the main-frame. This menu
 * gives access to some information about the application.
 * @author Benjamin Sigg
 */
public class HelpMenu extends JMenu{
    /** the main-frame of this application */
	private MainFrame frame;
	
	/**
	 * Creates a new menu.
	 * @param frame the main-frame of this application
	 */
	public HelpMenu( MainFrame frame ){
		this.frame = frame;
		
		setText( "About" );
		
		JMenuItem about = new JMenuItem( "About" );
		add( about );
		about.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				HelpMenu.this.frame.getAbout( true ).setVisible( true );
			}
		});
	}
}
