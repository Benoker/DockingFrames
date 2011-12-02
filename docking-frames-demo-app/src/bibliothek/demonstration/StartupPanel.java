package bibliothek.demonstration;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * A panel that is shown above the whole main-frame, this panel
 * contains nothing more than an animation. It is used to lock the
 * graphical user interface while a {@link Demonstration} is starting up.
 * @author Benjamin Sigg
 */
public class StartupPanel extends JPanel{
    /** clock ensuring repaint */
	private Timer timer;
	
	/** information about the {@link Demonstration} that is starting up */
	private JLabel text;
	
	/**
	 * Creates a new panel
	 */
	public StartupPanel(){
		timer = new Timer( 90, new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				repaint();
			}
		});
		
		setVisible( false );
		setOpaque( false );
		
		text = new JLabel(){
			@Override
			public void updateUI(){
				setFont( null );
				super.updateUI();
				setFont( getFont().deriveFont( 32f ) );
			}
		};
		
		text.setOpaque( false );
		
		setLayout( new GridBagLayout() );
		JPanel panel = new JPanel( new GridBagLayout() );
		panel.setOpaque( false );
		
		panel.add( text, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, 
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets( 10, 10, 10, 10 ), 0, 0 ) );
		panel.add( new Animation(), new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, 
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets( 10, 10, 10, 10 ), 0, 0 ) );
		
		add( panel, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, 
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets( 0, 0, 0, 0 ), 0, 0 ));
		
		
		addMouseMotionListener( new MouseMotionListener(){
			public void mouseDragged( MouseEvent e ){
				e.consume();
			}

			public void mouseMoved( MouseEvent e ){
				e.consume();	
			}
		});
		
		addMouseListener( new MouseListener(){
			public void mouseClicked( MouseEvent e ){
				e.consume();
			}

			public void mouseEntered( MouseEvent e ){
				e.consume();
			}

			public void mouseExited( MouseEvent e ){
				e.consume();
			}

			public void mousePressed( MouseEvent e ){
				e.consume();
			}

			public void mouseReleased( MouseEvent e ){
				e.consume();				
			}
		});
	}
	
	@Override
	protected void paintComponent( Graphics g ){
		Graphics2D g2 = (Graphics2D)g.create();
		g2.setColor( Color.WHITE );
		g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.5f ) );
		g2.fillRect( 0, 0, getWidth(), getHeight() );
		g2.dispose();
	}
	
	/**
	 * Starts the animation and shows <code>text</code>.
	 * @param text detailed information about the current operation
	 */
	public void showAnimation( String text ){
		this.text.setText( text );
		setVisible( true );
		if( !timer.isRunning() )
			timer.start();
	}
	
	/**
	 * Stops the animation.
	 */
	public void hideAnimation(){
		setVisible( false );
		timer.stop();
	}
	
	/**
	 * A panel painting a rotating circle of circles.
	 * @author Benjamin Sigg
	 */
	private class Animation extends JPanel{
	    /**
	     * Creates a new panel
	     */
		public Animation(){
			setOpaque( false );
			
			setPreferredSize( new Dimension( 85, 85 ) );
		}
		
		@Override
		protected void paintComponent( Graphics g ){
			final int POINTS = 7;
			final float SIZE = 6; 
			final float BORDER = 3;
			
			Graphics2D g2 = (Graphics2D)g.create();
			
			g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			
			long time = System.currentTimeMillis();
			time %= 2000;
			
			double angle = time / 2000.0 * (POINTS+1);
			angle = (int)angle;
			angle = angle / (POINTS+1) * Math.PI * 2.0;
			
			double w = getWidth();
			double h = getHeight();
			
			for( int i = 0; i < POINTS; i++ ){
				double x = Math.sin( angle );
				double y = -Math.cos( angle );
				
				angle += Math.PI * 2.0 / (POINTS+1);
				
				float strength = 1f - (POINTS - i - 1) / ((float)POINTS);
				g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, strength ) );
				
				double mx = w/2 + x*w *(SIZE-2)/SIZE/2.0;
				double my = h/2 + y*h *(SIZE-2)/SIZE/2.0;
				
				g2.setColor( Color.BLACK );
				g2.fillOval( (int)(mx - w/SIZE/2), (int)(my - h/SIZE/2), (int)(w/SIZE), (int)(h/SIZE) );
				
				g2.setColor( Color.WHITE );
				g2.fillOval( (int)(mx - w/SIZE/2 + BORDER ), (int)(my - h/SIZE/2 + BORDER), (int)(w/SIZE - 2*BORDER), (int)(h/SIZE - 2*BORDER) );
			}
			
			g2.dispose();
		}
	}
}
