package bibliothek.demonstration;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

/**
 * A panel that shows a thumbnail of an image. When the user presses the mouse
 * on this panel, a popup will appear showing the full sized image.
 * @author Benjamin Sigg
 *
 */
public class ImagePanel extends JLabel{
    /** the full sized image */
	private BufferedImage image;
	/** the thumbnail of {@link #image} */
	private Image thumbnail;
	
	/** the label showing {@link #image} */
	private JLabel label;
	
	/**
	 * Creates a new panel.
	 * @param image the image from which a thumbnail will be fetched
	 */
	public ImagePanel( BufferedImage image ){
		if( image != null ){
			this.image = image;
			
			double factor = Math.min( 300.0 / image.getWidth(), 200.0 / image.getHeight() );
			
			thumbnail = image.getScaledInstance( 
					(int)(image.getWidth()*factor),
					(int)(image.getHeight()*factor),
					Image.SCALE_SMOOTH );
			
			addMouseListener( new Listener() );
			
			label = new JLabel( new ImageIcon( image ));
			setIcon( new ImageIcon( thumbnail ) );
			setText( "Press the mouse to enlarge the image" );
			setVerticalTextPosition( BOTTOM );
			setHorizontalTextPosition( CENTER );
		}
	}

	/**
	 * A listener to an {@link ImagePanel}, this listener opens a
	 * popup showing the full sized image of the enclosing <code>ImagePanel</code>.
	 * @author Benjamin Sigg
	 *
	 */
	private class Listener extends MouseAdapter{
	    /** the window that shows the full sized image */
		private Popup popup;
		
		@Override
		public void mousePressed( MouseEvent e ){
			if( popup == null ){
				Point location = new Point( 
						(getWidth() - image.getWidth())/2, 
						(getHeight() - image.getHeight())/2 );
				SwingUtilities.convertPointToScreen( location, ImagePanel.this );
				
				popup = PopupFactory.getSharedInstance().getPopup( ImagePanel.this, label, location.x, location.y );
				popup.show();
			}
		}
		
		@Override
		public void mouseReleased( MouseEvent e ){
			if( popup != null ){
				popup.hide();
				popup = null;
			}
		}
	}
}
