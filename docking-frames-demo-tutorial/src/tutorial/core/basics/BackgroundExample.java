package tutorial.core.basics;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.BackgroundPanel;
import bibliothek.gui.dock.util.PaintableComponent;
import bibliothek.gui.dock.util.Transparency;

@Tutorial( id="Background", title="Background" )
public class BackgroundExample {
	/* (Almost) all components of the framework use a special strategy to paint their content. Clients
	 * can replace that algorithm and paint the components in any way the like. */
	public static void main( String[] args ) throws IOException{
		/* setting up frame and controller as usual */
		JTutorialFrame frame = new JTutorialFrame( BackgroundExample.class );
		
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		/* We are going to create a transparency effect with an image shining through the application. While we
		 * keep icons and text solid, we will paint the original background and borders semi-transparent. 
		 * We will use an alpha value of 0.5 for decorations like titles and buttons, and a value of 0.75 for
		 * the parts where the user would work on. */ 
		AlphaComposite alphaDecoration = AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.5f );
		AlphaComposite alphaWork = AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.75f );
		
		/* The background image we are going to use */
		BufferedImage image = ImageIO.read( BackgroundPanel.class.getResource( "/data/tutorial/shadowsAndLight_GregMartin.jpg" ) );
		
		/* There are several possibilities how to apply our custom background. The easiest one is to use the properties
		 * to replace the default strategy. */ 
		controller.getProperties().set( DockTheme.BACKGROUND_PAINT, new CustomPaint( frame.getContentPane(), image, alphaDecoration ) );
		
		/* By accessing the ThemeManager we can set a strategy that is used by only one type of component. In this case
		 * we set a custom identifier which will later be used by our "BackgroundDockable" which is shown later in this
		 * example. */
		controller.getThemeManager().setBackgroundPaint( ThemeManager.BACKGROUND_PAINT + ".custom", new CustomPaint( frame.getContentPane(), image, alphaWork ) );
		
		/* We can apply different themes. Depending on the theme we might need to make some additional addjustements
		 * to create a good looking application. */
//		controller.setTheme( new EclipseTheme() );
//		controller.setTheme( new BubbleTheme() );
//		controller.setTheme( new BasicTheme() );
		controller.setTheme( new FlatTheme() );
		
		/* And now we set up different DockStations and Dockables */
		SplitDockStation splitDockStation = new SplitDockStation();
		controller.add( splitDockStation );
		frame.add( splitDockStation );
		
		SplitDockGrid grid = new SplitDockGrid();
		
		grid.addDockable(  0,  0, 100, 20, new BackgroundDockable( "Red", Color.RED ));
		grid.addDockable(  0, 20,  30, 50, new BackgroundDockable( "Blue", Color.BLUE ));
		grid.addDockable(  0, 70,  30, 30, new BackgroundDockable( "Yellow", Color.YELLOW ));
		grid.addDockable( 30, 20,  80, 80, new BackgroundDockable( "White", Color.WHITE ));
		grid.addDockable( 30, 20,  80, 80, new BackgroundDockable( "Black", Color.BLACK ));
		
		splitDockStation.dropTree( grid.toTree() );
		
		FlapDockStation flapDockStation = new FlapDockStation();
		controller.add( flapDockStation );
		flapDockStation.add( new BackgroundDockable( "Green", Color.GREEN ));
		frame.add( flapDockStation.getComponent(), BorderLayout.NORTH );
		
		ScreenDockStation screenDockStation = new ScreenDockStation( controller.getRootWindowProvider() );
		controller.add( screenDockStation );
		
		/* Now we make all frames and windows visible. */
		frame.setVisible( true );
		screenDockStation.setShowing( true );
	}
	
	/* This helper method creates an image we use for the background */
	private static BufferedImage createBackground( BufferedImage image, int width, int height ){
		if( width <= 0 || height <= 0 ){
			return null;
		}
		BufferedImage result = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		Graphics g = result.createGraphics();
		g.setColor( Color.BLACK );
		g.fillRect( 0, 0, width, height );
		
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		
		int mw = Math.min( width, imageWidth );
		int mh = Math.min( height, imageHeight );
		
		g.drawImage( image, width/2-mw/2, height/2-mh/2, width/2+mw/2, height/2+mh/2, imageWidth/2-mw/2, imageHeight/2-mh/2, imageWidth/2+mw/2, imageHeight/2+mh/2, null );
		g.dispose();
		
		return result;
	}
	
	/* This is our custom painting algorithm */
	private static class CustomPaint implements BackgroundPaint{
		/* the entire image as it was read from the disk */
		private BufferedImage baseImage;
		/* an image with the same size as the frame */
		private BufferedImage image;
		
		/* our anchor point, the location 0/0 of our image and of this component will always match */
		private Component content;
		/* the alpha value we are going to apply for transparency effects */
		private AlphaComposite alpha;
		
		/* standard constructor */
		private CustomPaint( Component content, BufferedImage image, AlphaComposite alpha ){
			this.alpha = alpha;
			this.content = content;
			this.baseImage = image;
		}
		
		public void install( BackgroundComponent component ){
			// ignore	
		}
		
		public void uninstall( BackgroundComponent component ){
			// ignore
		}
		
		/* gets the image that should be used for painting */ 
		public BufferedImage getImage(){
			if( image == null || image.getWidth() != content.getWidth() || image.getHeight() != content.getHeight() ){
				image = createBackground( baseImage, content.getWidth(), content.getHeight() );
			}
			return image;
		}
		
		/* this is the method that paints the background */
		public void paint( BackgroundComponent background, PaintableComponent paintable, Graphics g ){
			if( SwingUtilities.isDescendingFrom( background.getComponent(), content )){
				/* If we are painting an non-transparent component we paint our custom background image, otherwise
				 * we just let it shine through */
				if( paintable.getTransparency() == Transparency.SOLID ){
					Point point = new Point( 0, 0 );
					point = SwingUtilities.convertPoint( paintable.getComponent(), point, content );
					BufferedImage image = getImage();
					if( image != null ){
						int w = paintable.getComponent().getWidth();
						int h = paintable.getComponent().getHeight();
						g.drawImage( image, 0, 0, w, h, point.x, point.y, point.x + w, point.y + h, null );
					}
				}

				/* and now we paint the original content of the component */
				Graphics2D g2 = (Graphics2D)g.create();
				
				g2.setComposite( alpha );
				
				paintable.paintBackground( g2 );
				paintable.paintForeground( g );
				paintable.paintBorder( g2 );
				
				g2.dispose();
				
				paintable.paintChildren( g );
				
				
			}
		}	
	}
	
	/* This is a specialized Dockable with a colored panel that seems to be transparent. */
	private static class BackgroundDockable extends DefaultDockable{
		private BackgroundPanel panel;
		private BackgroundAlgorithm background;
		
		public BackgroundDockable( String title, Color color ){
			super( title );
		
			/* We use a BackgroundPanel: it already offers methods to use a replaceable strategy
			 * for painting. */
			panel = new BackgroundPanel( Transparency.SOLID ){
				@Override
				protected void configure( Transparency transparency ){
					// we ignore transparency settings. These settings are made by the client and since in this
					// example we do not set transparency this method will never be called anyway
				}
    			@Override
    			protected void setupRenderingHints( Graphics g ) {
    				// we do not set any rendering hints (like antialising)
    			}
			};
			panel.setBackground( color );
			add( panel );
			
			/* And we need a connection between our panel and the framework. The connection has a 
			 * type (called "kind") and a unique identifier. The identifier matches the one identifier we
			 * used to register our speical background algorithm. */
			background = new BackgroundAlgorithm( BackgroundComponent.KIND.append( "custom" ), ThemeManager.BACKGROUND_PAINT + ".custom" ){
				public Component getComponent(){
					return panel;
				}
			};
			panel.setBackground( background );
		}
		
		@Override
		public void setController( DockController controller ){
			super.setController( controller );
			/* The connection between panel and framework needs to know the current DockController in
			 * order to work. */
			background.setController( controller );
		}
	}
}
