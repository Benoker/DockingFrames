package bibliothek.demonstration;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DemoPanel extends JPanel implements ActionListener{
	private Demonstration demonstration;
	private Core core;
	
	public DemoPanel( Core core, Demonstration demonstration ){
		super( new GridBagLayout() );
		this.core = core;
		this.demonstration = demonstration;
		
		Image image = demonstration.getImage();
		JLabel imageLabel;
		
		if( image == null )
			imageLabel = new JLabel( demonstration.getIcon() );
		else
			imageLabel = new JLabel( new ImageIcon( image ));
		JLabel description = new JLabel( demonstration.getHTML() );
		JButton start = new JButton( "Startup" );
		
		add( imageLabel, new GridBagConstraints( 0, 0, 1, 2, 1.0, 1.0, 
				GridBagConstraints.CENTER, GridBagConstraints.NONE, 
				new Insets( 10, 10, 10, 10 ), 0, 0 ));
		add( description, new GridBagConstraints( 1, 0, 1, 1, 100.0, 100.0, 
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
				new Insets( 10, 10, 10, 10 ), 0, 0 ));
		add( start, new GridBagConstraints( 1, 1, 1, 1, 1.0, 1.0, 
				GridBagConstraints.LAST_LINE_END, GridBagConstraints.NONE, 
				new Insets( 10, 10, 10, 10 ), 0, 0 ));
		
		start.addActionListener( this );
	}
	
	public void actionPerformed( ActionEvent e ){
		core.start( demonstration );
	}
}
