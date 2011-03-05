package tutorial.core.guide;

import java.awt.Color;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.frontend.FrontendDockablePerspective;
import bibliothek.gui.dock.frontend.Setting;
import bibliothek.gui.dock.layout.DockLayoutComposition;
import bibliothek.gui.dock.perspective.Perspective;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.split.SplitDockPerspective;
import bibliothek.gui.dock.station.stack.StackDockPerspective;

@Tutorial( id="CorePerspectives", title="Persistent Layout: Perspectives" )
public class PerspectiveExample {
	/* Perspectives allow to manipulate and set up a layout without actually creating Dockable objects */
	public static void main( String[] args ){
		/* Setting up a frame, a station and a DockFrontend */
		JTutorialFrame frame = new JTutorialFrame( PerspectiveExample.class  );
		DockFrontend frontend = new DockFrontend( frame );
		frame.destroyOnClose( frontend );
		
		SplitDockStation station = new SplitDockStation();
		frontend.addRoot( "station", station );
		frame.add( station );
		
		/* The Perspective object is needed to convert the layout to the perspective-format. */
		Perspective perspective = frontend.getPerspective( false );
		/* A setting contains all the properties a DockFrontend needs to describe one layout. Here we
		 * read the current setting. */
		Setting setting = frontend.getSetting( false );
		/* This DockLayoutComposition is the layout of "station" in the intermediate format. */
		DockLayoutComposition stationComposition = setting.getRoot( "station" );
		/* And trough the Perspective object we convert the intermediate format to a client friendly format. */
		SplitDockPerspective stationPerspective = (SplitDockPerspective)perspective.convert( stationComposition );
		
		/* We are now creating the representation of the Dockables we are later going to add */
		PerspectiveDockable dockableRed = new FrontendDockablePerspective( "red" );
		PerspectiveDockable dockableGreen = new FrontendDockablePerspective( "green" );
		PerspectiveDockable dockableBlue = new FrontendDockablePerspective( "blue" );
		/* Then we put two dockables in the same stack. */
		StackDockPerspective stack = new StackDockPerspective( new PerspectiveDockable[]{ dockableGreen, dockableBlue }, dockableGreen );
		
		/* The SplitDockStation internally uses a tree to represents its layout, here we create such a tree */
		SplitDockPerspective.Leaf childRed = new SplitDockPerspective.Leaf( dockableRed, null, null, -1 );
		SplitDockPerspective.Leaf childStack = new SplitDockPerspective.Leaf( stack, null, null, -1 );
		SplitDockPerspective.Node node = new SplitDockPerspective.Node( Orientation.HORIZONTAL, 0.4, childRed, childStack, null, null, -1 );
		stationPerspective.getRoot().setChild( node );
		
		/* The new layout has been created, now we add some Dockables to the DockFrontend in order to load 
		 * the layout */
		frontend.addDockable( "red", new ColorDockable( "Red", Color.RED ) );
		frontend.addDockable( "green", new ColorDockable( "Green", Color.GREEN ) );
		frontend.addDockable( "blue", new ColorDockable( "Blue", Color.BLUE ) );
		
		/* Finally the perspective is converted back into the intermediate format... */
		stationComposition = perspective.convert( stationPerspective );
		/* ... the frontends layout object is modified ... */
		setting.putRoot( "station", stationComposition );
		/* ... and the new layout is loaded. */
		frontend.setSetting( setting, false );
		
		frame.setVisible( true );
	}
}
