package bibliothek.notes.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import bibliothek.notes.view.MainFrame;

public class HelpMenu extends JMenu{
	private MainFrame frame;
	
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
