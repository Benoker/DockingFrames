package tutorial.toolbar.common;

import java.awt.Color;

import tutorial.support.ColorIcon;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableFactory;
import bibliothek.gui.dock.common.action.CButton;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.toolbar.CToolbarContentArea;
import bibliothek.gui.dock.toolbar.CToolbarItem;
import bibliothek.gui.dock.toolbar.perspective.CToolbarContentPerspective;
import bibliothek.util.filter.RegexFilter;

@Tutorial(id = "ToolbarCommonPerspective", title = "Perspectives")
public class CommonPerspective {
	public static void main( String[] args ){
		/* The Toolbar extension supports the perspective API. With help of the perspective API a client
		 * can build up a layout without the need to create any CDockables. */
		
		/* As usual we need a frame and a CControl */
		JTutorialFrame frame = new JTutorialFrame( CommonPerspective.class );
		CControl control = new CControl( frame );

		/* We initialize the elements that are always present, namely the CToolbarContentArea */
		CToolbarContentArea area = new CToolbarContentArea( control, "center" );
		control.addStationContainer( area );
		frame.add( area );

		/* We can use SingleCDockableFactories to lazily create the buttons. Here we are using
		 * the unique identifier of the buttons to decide which icon the button should have. */
		control.addSingleDockableFactory( new RegexFilter( "red.*" ), new ColorToolbarItemFactory( Color.RED ));
		control.addSingleDockableFactory( new RegexFilter( "green.*" ), new ColorToolbarItemFactory( Color.GREEN ));
		control.addSingleDockableFactory( new RegexFilter( "blue.*" ), new ColorToolbarItemFactory( Color.BLUE ));
		
		/* Now we access the perspective API, we start by creating an empty perspective */
		CPerspective perspective = control.getPerspectives().createEmptyPerspective();
		/* We access the perspective of the CToolbarContentArea by using the same unique identifier
		 * as we used when creating the area. */
		CToolbarContentPerspective content = new CToolbarContentPerspective( perspective, "center" );
		
		/* The buttons are added by simulating drag and drop operations. Imagine the empty application,
		 * then dropping the button "red-1", followed by "red-2", etc. */
		content.getNorthToolbar().group( 0 ).column( 0 ).toolbar( 0 ).add( "red-1" );
		content.getNorthToolbar().group( 0 ).column( 0 ).toolbar( 0 ).add( "red-2" );
		
		content.getNorthToolbar().group( 0 ).column( 0 ).toolbar( 1 ).add( "green-1" );
		content.getNorthToolbar().group( 0 ).column( 0 ).toolbar( 1 ).add( "green-2" );
		content.getNorthToolbar().group( 0 ).column( 0 ).toolbar( 1 ).add( "green-3" );
		
		content.getNorthToolbar().group( 0 ).column( 1 ).toolbar( 0 ).add( "blue-1" );
		content.getNorthToolbar().group( 0 ).column( 1 ).toolbar( 0 ).add( "blue-2" );
		content.getNorthToolbar().group( 0 ).column( 1 ).toolbar( 0 ).add( "blue-3" );
		
		/* Once our initial layout has been created, we apply it. */
		control.getPerspectives().setPerspective( perspective, true );
		
		/* And finally make the frame visible */
		frame.setVisible( true );
	}

	/* This factory creates new CToolbarItems (buttons on a toolbar) */
	private static class ColorToolbarItemFactory implements SingleCDockableFactory {
		private Color color;

		public ColorToolbarItemFactory( Color color ){
			this.color = color;
		}

		@Override
		public SingleCDockable createBackup( String id ){
			CToolbarItem item = new CToolbarItem( id );
			item.setItem( new CButton( "Action", new ColorIcon( color ) ) );
			return item;
		}
	}
}
