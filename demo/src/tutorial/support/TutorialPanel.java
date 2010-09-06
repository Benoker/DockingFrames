package tutorial.support;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * A panel that shows the contents of a tutorial, there is also
 * a button for the user such that he can start the tutorial.
 * @author Benjamin Sigg
 *
 */
public class TutorialPanel extends JPanel implements ActionListener{
	private ImagePanel image;
	private JLabel title;
	private JTextPane description;
	private JButton start;
	private Class<?> tutorial;
	
    /**
	 * Creates a new panel.
	 */
	public TutorialPanel(){
		super( new GridBagLayout() );
		
		image = new ImagePanel();
		
		title = new JLabel(){
			@Override
			public void updateUI(){
				setFont( null );
				super.updateUI();
				setFont( getFont().deriveFont( 32f ) );
			}
		};
		description = new JTextPane();
		description.setContentType( "text/html" );
		description.setEditable( false );
		
		start = new JButton( "Startup" );
		start.setEnabled( false );
		
		add( title, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, 
				GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL,
				new Insets( 10, 10, 10, 10 ), 0, 0 ));
		add( image, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, 
				GridBagConstraints.NORTH, GridBagConstraints.NONE, 
				new Insets( 10, 10, 10, 10 ), 0, 0 ));
		add( new JScrollPane( description ), new GridBagConstraints( 0, 2, 1, 1, 100.0, 100.0, 
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, 
				new Insets( 10, 10, 10, 10 ), 0, 0 ));
		add( start, new GridBagConstraints( 0, 3, 1, 1, 1.0, 1.0, 
				GridBagConstraints.LAST_LINE_END, GridBagConstraints.NONE, 
				new Insets( 10, 10, 10, 10 ), 0, 0 ));
		
		start.addActionListener( this );
	}
	
	public void set( String title, String description, BufferedImage image, Class<?> tutorial ){
		this.title.setText( title );
		this.description.setText( description );
		this.image.setImage( image );
		this.start.setEnabled( tutorial != null );
		this.tutorial = tutorial;
	}
	
	public void actionPerformed( ActionEvent event ){
		try {
			tutorial.getMethod("main", String[].class).invoke(null, new Object[]{new String[]{}});
		}
		catch( Exception e ){
			e.printStackTrace();
			JOptionPane.showMessageDialog( this, "Unable to start tutorial: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
		}
	}
}
