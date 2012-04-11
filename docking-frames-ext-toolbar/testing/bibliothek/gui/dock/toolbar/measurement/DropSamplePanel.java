package bibliothek.gui.dock.toolbar.measurement;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;

public class DropSamplePanel extends OverpaintablePanel{
	private BufferedImage overlay;
	private DropSample sample;
	private boolean useSampleColors = true;
	
	public DropSamplePanel(){
		getContentPane().setLayout( new GridLayout( 1, 1 ) );
		getContentPane().setBorder( new EmptyBorder( 50, 50, 50, 50 ) );
	}
	
	public void setSample( DropSample sample ){
		this.sample = sample;
		getContentPane().removeAll();
		getContentPane().add( sample.getComponent() );
		resetOverlay();
	}
	
	public void setUseSampleColors( boolean useSampleColors ){
		this.useSampleColors = useSampleColors;
		resetOverlay();
	}
	
	public boolean isUseSampleColors(){
		return useSampleColors;
	}
	
	private void resetOverlay(){
		if( isVisible() && getWidth() > 0 && getHeight() > 0 ){
			overlay = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
			fill( overlay, 0 );
		}
	}
	
	private void fill( final BufferedImage image, final int y ){
		EventQueue.invokeLater( new Runnable(){
			@Override
			public void run(){
				if( image == overlay && sample != null ){
					paint( image, y );
					int next = y + 1;
					if( next < image.getHeight() ){
						fill( image, next );
					}
				}
			}
		} );
	}
	
	private void paint( BufferedImage image, int y ){
		int width = image.getWidth();
		Graphics g = image.createGraphics();
		
		for( int x = 0; x < width; x++ ){
			Point point = new Point( x, y );
			SwingUtilities.convertPointToScreen( point, getContentPane() );

			int layer = layerAt( point.x, point.y );
			if( layer >= 0 ){
				Color color;
				if( useSampleColors ){
					color = sample.dropAt( point.x, point.y );
				}
				else{
					color = layerColor( layer );
				}
				g.setColor( color );
				g.fillRect( x, y, 1, 1 );
			}
		}
		
		g.dispose();
		repaint();
	}
	
	private int layerAt( int x, int y ){
		if( sample == null ){
			return -1;
		}
		int index = 0;
		for( DockStationDropLayer layer : sample.getStation().getLayers() ){
			if( layer.contains( x, y )){
				return index;
			}
			index++;
		}
		return -1;
	}
	
	private Color layerColor( int layer ){
		switch( layer ){
			case 0: return Color.RED;
			case 1: return Color.GREEN;
			case 2: return Color.BLUE;
			case 3: return Color.YELLOW;
			case 4: return Color.CYAN;
			case 5: return Color.MAGENTA;
			default: throw new IllegalArgumentException( "did not except a station with that many layers, need to define more colors" );
		}
	}
	
	@Override
	protected void paintOverlay( Graphics g ){
		if( overlay == null || overlay.getWidth() != getWidth() || overlay.getHeight() != getHeight() ){
			resetOverlay();
		}
		if( overlay != null ){
			Graphics2D g2 = (Graphics2D)g;
	        
	        Composite old = g2.getComposite();
	        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.33f ));

			g.drawImage( overlay, 0, 0, this );
			
			g2.setComposite( old );
		}
	}
}
