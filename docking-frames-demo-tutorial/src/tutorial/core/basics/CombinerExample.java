package tutorial.core.basics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import javax.swing.SwingUtilities;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.station.support.Enforcement;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.themes.CombinerValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.util.UIBridge;
import bibliothek.gui.dock.util.UIValue;

@Tutorial( id="Combiner", title="Combining Dockables" )
public class CombinerExample {
	public static void main( String[] args ){
		
		/* Whenever the user or the client tries to merge (combine) two Dockables, an interface is
		 * asked to do the job. This interface is called "Combiner" and can be set by clients.
		 * 
		 * Usually the Combiner is used by DockStations in this way:
		 * 
		 * 1. The caller creates a "CombinerSource". This object delivers basic information about the 
		 *    elements that are going to be combined.
		 * 2. The caller invokes "Combiner.prepare", which returns a "CombinerTarget" or "null" as answer.
		 * 3. The caller caches the "CombinerTarget" and may use it for painting.
		 * 4. The caller invokes "Combiner.combine" which lets the Combiner do its main work: combine two
		 *    Dockables and create a new one.
		 * 
		 * The chain of events can be canceled in any step. */
		
		/* Setting up basic frame and controller */
		JTutorialFrame frame = new JTutorialFrame( CombinerExample.class );
		
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		/* With the help of the ThemeManager and an UIBridge we can easily replace all occurances of
		 * a Combiner with our custom implementation */
		ThemeManager themeManager = controller.getThemeManager();
		themeManager.setCombinerBridge( CombinerValue.KIND_COMBINER, new UIBridge<Combiner, UIValue<Combiner>>(){
			public void add( String id, UIValue<Combiner> uiValue ){
				// ignore	
			}
			public void remove( String id, UIValue<Combiner> uiValue ){
				// ignore
			}
			public void set( String id, Combiner value, UIValue<Combiner> uiValue ){
				uiValue.set( new CustomCombiner() );
			}
		});
		
		/* And now we set up different DockStations and Dockables */
		SplitDockStation splitDockStation = new SplitDockStation();
		controller.add( splitDockStation );
		frame.add( splitDockStation );
		
		SplitDockGrid grid = new SplitDockGrid();
		
		grid.addDockable(  0,  0, 100, 20, new ColorDockable( "Gray", Color.GRAY ));
		grid.addDockable(  0, 20,  30, 50, new ColorDockable( "Dark", Color.DARK_GRAY ));
		grid.addDockable(  0, 70,  30, 30, new ColorDockable( "Light", Color.LIGHT_GRAY ));
		grid.addDockable( 30, 20,  80, 80, new ColorDockable( "White", Color.WHITE ));
		grid.addDockable( 30, 20,  80, 80, new ColorDockable( "Black", Color.BLACK ));
		
		splitDockStation.dropTree( grid.toTree() );
		
		frame.setVisible( true );
	}
	
	/* This is our custom Combiner. It has customized painting code, but the final result of combining two
	 * Dockables will be the same as if using the standard Combiner. */
	private static class CustomCombiner implements Combiner{
		public CombinerTarget prepare( final CombinerSource source, Enforcement force ){
			/* We do not combine anything unless we are forced */
			if( force.getForce() < 0.5f ){
				return null;
			}
			
			/* The CombinerTarget can be used for painting and for keeping information that is later
			 * needed. In this case we just use it for painting. */
			return new CombinerTarget(){
				public void paint( Graphics g, Component component, StationPaint paint, Rectangle stationBounds, Rectangle dockableBounds ){
					/* Our custom painting code paints some arrows... */
					Graphics2D g2 = (Graphics2D)g.create();
					g2.setColor( Color.GREEN );
					
					int[] x = new int[]{ 0, 20, 8, 8, -8, -8, -20 };
					int[] y = new int[]{ 0, 20, 20, 40, 40, 20, 20 };
					Polygon arrow = new Polygon( x, y, x.length );
					
					g2.translate( stationBounds.x + stationBounds.width/2, stationBounds.y + stationBounds.height/2 );
					AffineTransform transform = new AffineTransform();
					transform.rotate( Math.PI/2 );
					
					g2.translate( 0, 10 );
					g2.fillPolygon( arrow );
					g2.translate( 0, -10 );
					
					g2.transform( transform );
					g2.translate( 0, 10 );
					g2.fillPolygon( arrow );
					g2.translate( 0, -10 );
					
					g2.transform( transform );
					g2.translate( 0, 10 );
					g2.fillPolygon( arrow );
					g2.translate( 0, -10 );
					
					g2.transform( transform );
					g2.translate( 0, 10 );
					g2.fillPolygon( arrow );
					g2.translate( 0, -10 );
					
					g2.setStroke( new BasicStroke( 10 ) );
					g2.drawOval( -55, -55, 110, 110 );
					
					g2.dispose();
					
					// ... and a point at the location of the mouse
					Point mouse = source.getMousePosition();
					if( mouse != null ){
						mouse = SwingUtilities.convertPoint( source.getOld().getComponent(), mouse, component );
						g.setColor( new Color( 0, 150, 0 ) );
						g.fillOval( mouse.x-5, mouse.y-5, 10, 10 );
					}
				}
				
				public DisplayerCombinerTarget getDisplayerCombination(){
					/* Some meta data that we can ignore */
					return null;
				}
			};
		}
		
		/* This method takes two Dockables and combines them. In this case we just create
		 * a new StackDockStation */
		public Dockable combine( CombinerSource source, CombinerTarget target ){
			DockStation parent = source.getParent();
			PlaceholderMap placeholders = source.getPlaceholders();
			
		    StackDockStation stack = new StackDockStation( parent.getTheme() );
	        stack.setController( parent.getController() );
	        
	        /* By setting the placeholders we allow the framework to more efficiently keep track
	         * of the location of invisible Dockables */
	        if( placeholders != null ){
	        	stack.setPlaceholders( placeholders );
	        }
	        
	        /* Finally we add the two Dockables that get combined */
	        stack.drop( source.getOld() );
	        stack.drop( source.getNew() );
	        
	        return stack;
	    }
		
		/* This method would be used to put a Dockable "aside" another dockable, but we do ignore 
		 * it in this example. */
		public void aside( AsideRequest request ){
			// ignore	
		}
	}
}
