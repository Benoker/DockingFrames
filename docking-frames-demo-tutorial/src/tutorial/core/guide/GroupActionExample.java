package tutorial.core.guide;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import tutorial.support.ColorDockable;
import tutorial.support.ColorIcon;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.action.actions.GroupKeyGenerator;
import bibliothek.gui.dock.action.actions.GroupedButtonDockAction;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.station.split.SplitDockProperty;

@Tutorial( id="GroupAction", title="Actions: GroupActions" )
public class GroupActionExample {
	/* GroupActions are a handy tool for implementing actions whose content depends on the properties of a Dockable.
	 * 
	 * This example shows an action that changes the color of a Dockable. Once the color changed, the action gives
	 * itself a new icon. Note that the same action is used for two different Dockables. */
	public static void main( String[] args ){
		/* Setting up a frame, station and a controller */
		JTutorialFrame frame = new JTutorialFrame( GroupActionExample.class );
		DockController controller = new DockController();
		frame.destroyOnClose( controller );
		controller.setRootWindow( frame );
		
		SplitDockStation station = new SplitDockStation();
		controller.add( station );
		frame.add( station );
		
		/* We set up a line of colors: each value in the map is the key for another value. */
		Map<Color, Color> list = new HashMap<Color, Color>();
		fillLine( list, Color.RED, Color.GREEN );
		fillLine( list, Color.GREEN, Color.BLUE );
		fillLine( list, Color.BLUE, Color.RED );
		
		/* And this is our customized GroupAction */
		ColorSwitchAction action = new ColorSwitchAction( list );
		
		/* Now we create two Dockables and set their actions */
		DefaultDockable dockableA = new ColorDockable( "One", Color.RED );
		DefaultDockable dockableB = new ColorDockable( "Two", Color.BLUE );
		
		dockableA.setActionOffers( new DefaultDockActionSource( new LocationHint( LocationHint.DOCKABLE, LocationHint.LEFT ), action ) );
		dockableB.setActionOffers( new DefaultDockActionSource( new LocationHint( LocationHint.DOCKABLE, LocationHint.LEFT ), action ) );
		
		/* Finally making the dockables visible */
		station.drop( dockableA );
		station.drop( dockableB, new SplitDockProperty( 0.5, 0, 0.5, 1.0 ));
		
		frame.setVisible( true );
	}
	
	private static void fillLine( Map<Color, Color> list, Color start, Color end ){
		int n = 5;
		Color before = start;
		
		for( int i = 1; i <= n; i++ ){
			int red = (start.getRed() * (n-i) + end.getRed() * i) / n;
			int green = (start.getGreen() * (n-i) + end.getGreen() * i) / n;
			int blue = (start.getBlue() * (n-i) + end.getBlue() * i) / n;
			Color next = new Color( red, green, blue );
			
			list.put( before, next );
			before = next;
		}
	}

	/* The key-generator tells what the initial key of a Dockable is. */
	private static class ColorKeyGenerator implements GroupKeyGenerator<Color>{
		public Color generateKey( Dockable dockable ){
			return ((ColorDockable)dockable).getColor();
		}
	}
	
	/* This is our customiazed GroupAction, it represents a button. */
	private static class ColorSwitchAction extends GroupedButtonDockAction<Color>{
		private Map<Color, Color> list = new HashMap<Color, Color>();
		
		public ColorSwitchAction( Map<Color, Color> list ){
			super( new ColorKeyGenerator() );
			this.list = list;
			
			/* We allow automatic cleanup, but have to override 'createGroup' in order
			 * to re-create groups that have been automatically deleted. */
			setRemoveEmptyGroups( true );
		}
		
		/* Here we set up one group */
		@Override
		protected SimpleButtonAction createGroup( Color key ){
			SimpleButtonAction action = super.createGroup( key );
			action.setText( "Set color to " + key.getRGB() );
			action.setIcon( new ColorIcon( key ) );
			return action;
		}
		
		/* This method is called when the user clickes the button. */
		public void action( Dockable dockable ){
			ColorDockable colorDockable = (ColorDockable)dockable;
			Color next = list.get( colorDockable.getColor() );
			colorDockable.setColor( next );
			
			/* The group of a Dockable can be changed at any time: */
			setGroup( next, colorDockable );
		}
	}
}
