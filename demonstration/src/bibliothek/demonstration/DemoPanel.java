package bibliothek.demonstration;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class DemoPanel extends JPanel implements ActionListener{
	private Demonstration demonstration;
	private Core core;
	
	public DemoPanel( Core core, Demonstration demonstration ){
		super( new GridBagLayout() );
		this.core = core;
		this.demonstration = demonstration;
		
		ImagePanel image = new ImagePanel( demonstration.getImage() );
		
		JLabel title = new JLabel( demonstration.getName() ){
			@Override
			public void updateUI(){
				setFont( null );
				super.updateUI();
				setFont( getFont().deriveFont( 32f ) );
			}
		};
		JTextPane description = new JTextPane();
		description.setContentType( "text/html" );
		description.setEditable( false );
		
		description.setText( demonstration.getHTML() );
		
		JButton start = new JButton( "Startup" );
		
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
	
	public void actionPerformed( ActionEvent e ){
		core.start( demonstration );
	}
}
