package bibliothek.notes.view;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * A dialog that shows, which authors and libraries have contributed
 * to this application. 
 * @author Benjamin Sigg
 *
 */
public class About extends JDialog{
    /**
     * Creates a new dialog
     * @param owner the frame below this dialog
     */
	public About( JFrame owner ){
		super( owner );
		
		setModal( true );
		setTitle( "About" );
		
		JTextPane pane = new JTextPane();
		pane.setEditable( false );
		
		add( new JScrollPane( pane ));
		
		pane.setText( 
				"Notes:\n" +
				"\tDemonstration of DockingFrames\n" +
				"\tLGPL 2.1\n" +
				"\tby Benjamin Sigg\n" +
				"\tbenjamin_sigg@gmx.ch\n" +
				"\n" +
				"DockingFrames:\n" +
				"\tJava-Swing docking framework\n" +
				"\tLGPL 2.1\n" +
				"\thttp://dock.javaforge.com/\n" +
				"\n" +
				"Icons:\n" +
				"\tSilk\n" +
				"\tCreative Commons Attribution 2.5 License.\n" +
				"\thttp://www.famfamfam.com/lab/icons/silk/"
		);
	}
}
