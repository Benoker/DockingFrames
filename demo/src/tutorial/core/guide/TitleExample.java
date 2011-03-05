package tutorial.core.guide;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.Priority;

@Tutorial( id="DockTitle", title="DockTitle: Custom title" )
public class TitleExample {
	/* All the titles used for Dockables can be replaced by custom implementations. 
	 * This example introduces a hippie style title which is used by the SplitDockStation. */
	public static void main( String[] args ){
		/* Setting up a frame, a station and a controller */
		JTutorialFrame frame = new JTutorialFrame( TitleExample.class );
		DockController controller = new DockController();
		frame.destroyOnClose( controller );
		controller.setRootWindow( frame );
		
		SplitDockStation station = new SplitDockStation();
		controller.add( station );
		frame.add( station );
		
		/* The DockTitleManager manages the factories for DockTitles */
		DockTitleManager titles = controller.getDockTitleManager();
		/* We access (or create) the key for factories used by the SplitDockStation. */
		DockTitleVersion version = titles.getVersion( SplitDockStation.TITLE_ID, null );
		/* This factory creates our custom DockTitle */
		DockTitleFactory factory = new CustomTitleFactory();
		/* And now we apply our factory */
		version.setFactory( factory, Priority.CLIENT );
		
		/* We need a Dockable to show the title */
		station.drop( new ColorDockable( "White", Color.WHITE ));
		
		frame.setVisible( true );
	}
	
	/* This method creates the colors of a rainbow */
	private static Color[] rainbow( int n ){
		Color[] result = new Color[ n ];
		for( int i = 0; i < n; i++ ){
			result[i] = Color.getHSBColor( ((float)i / (n-1)), 1.0f, 1.0f );
		}
		return result;
	}
	
	/* A DockTitleFactory receives a request for a DockTitle. The factory can either answer
	 * to this request or just do nothing. If a DockTitleRequest is installed on the factory, then
	 * the factory can trigger an update of the title at any time. */
	private static class CustomTitleFactory implements DockTitleFactory{
		public void install( DockTitleRequest request ){
			// ignore			
		}

		public void request( DockTitleRequest request ){
			/* When asked for a title, answer with a title */
			request.answer( new CustomTitle( request.getTarget(), request.getVersion() ) );
		}

		public void uninstall( DockTitleRequest request ){
			// ignore
		}
	}
	
	/* This is our custom title which makes heavy use of the default implementation for titles. Clients can
	 * implement the interface DockTitle directly, then they basically have a Component on which they
	 * can paint whatever they want to paint. */
	private static class CustomTitle extends AbstractDockTitle{
		public CustomTitle( Dockable dockable, DockTitleVersion origin ){
			super( dockable, origin );
		}
		
		@Override
		protected void paintBackground( Graphics g, JComponent component ){
			Graphics2D g2 = (Graphics2D)g;
			Color[] rainbow = rainbow( 10 );
			
			int width = component.getWidth();
			int height = component.getHeight();
			for( int i = 1, n = rainbow.length; i < n; i++ ){
				int x1 = width * (i-1) / (n-1);
				int x2 = width * i / (n-1);
				
				g2.setPaint( new GradientPaint( x1, 0, rainbow[i-1], x2, 0, rainbow[i] ) );
				g.fillRect( x1, 0, x2-x1, height );
			}
		}
	}
}
