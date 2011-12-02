package tutorial.common.basics;

import java.awt.BorderLayout;
import java.awt.Color;

import tutorial.support.ColorSingleCDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableFactory;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.util.filter.PresetFilter;
import bibliothek.util.xml.XElement;

@Tutorial(title="SingleCDockableFactory", id="SingleCDockableFactory")
public class SingleDockableFactoryExample {
	public static void main( String[] args ){
		/* CControl supports factories to lazily create SingleCDockables. This example
		 * shows how to set up two factories. */
		
		/* creating a frame and a control */
		JTutorialFrame frame = new JTutorialFrame( SingleDockableFactoryExample.class );
		CControl control = new CControl( frame );
		frame.destroyOnClose( control );
		frame.add(control.getContentArea(), BorderLayout.CENTER);

		/* A SingleCDockableFactory receives an identifier and has to create a SingleCDockable
		 * with the same identifier. If the factory does not recognize the identifier, then
		 * it can just return null.
		 * Many factories can be registered at a CControl, each factory is associated with a
		 * filter telling the CControl which identifiers the factory can handle. If an identifier
		 * matches more than one filter, then the one factory "wins" that was added first. */
		control.addSingleDockableFactory( new PresetFilter<String>( "Red", "Green", "Blue" ), new SingleCDockableFactory(){
			public SingleCDockable createBackup( String id ){
				if( "Red".equals( id )){
					return new ColorSingleCDockable( "Red", Color.RED );
				}
				if( "Green".equals( id )){
					return new ColorSingleCDockable( "Green", Color.GREEN );
				}
				if( "Blue".equals( id )){
					return new ColorSingleCDockable( "Blue", Color.BLUE );
				}
				return null;
			}
		});
		
		/* Instead of using a filter we can also tell the CControl directly which identifier
		 * a factory will handle. Factories registered in that way have a higher priority than
		 * any other factory. In this case the "red" dockable will be created by the second
		 * factory even tough the first factory would already handle "red". */
		control.addSingleDockableFactory( "Red", new SingleCDockableFactory(){
			public SingleCDockable createBackup( String id ){
				return new ColorSingleCDockable( "Red", Color.WHITE );
			}
		});
		
		/* After the factories are registered we can load a a layout from a file (or another source)
		 * and CControl will use the factories to create Dockables if necessary */
		control.readXML( createLayout() );
		
		frame.setVisible(true);
	}

	/* This method simulates the creation of a layout */
	private static XElement createLayout(){
		CControl control = new CControl();
		control.getContentArea();
		
		DefaultSingleCDockable red = new ColorSingleCDockable( "Red", Color.RED );
		DefaultSingleCDockable green = new ColorSingleCDockable( "Green", Color.GREEN );
		DefaultSingleCDockable blue = new ColorSingleCDockable( "Blue", Color.BLUE );
		
		CGrid grid = new CGrid( control );
		grid.add( 0, 0, 10, 10, red );
		grid.add( 10, 0, 5, 5, green );
		grid.add( 10, 5, 5, 5, blue );
		
		control.getContentArea().deploy( grid );
		
		red.setLocation( CLocation.base().minimalEast() );
		red.setExtendedMode( ExtendedMode.NORMALIZED );
		
		green.setLocation( CLocation.base().minimalWest() );
		green.setExtendedMode( ExtendedMode.NORMALIZED );
		
		blue.setLocation( CLocation.base().minimalNorth() );
		blue.setExtendedMode( ExtendedMode.NORMALIZED );
		
		XElement root = new XElement( "root" );
		control.writeXML( root );
		control.destroy();
		return root;
	}
}
