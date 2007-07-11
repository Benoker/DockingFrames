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

public class ImagePanel extends JLabel{
	private BufferedImage image;
	private Image thumbnail;
	
	private JLabel label;
	
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

	private class Listener extends MouseAdapter{
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
