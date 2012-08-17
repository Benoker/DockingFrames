package test;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.common.perspective.SingleCDockablePerspective;
import bibliothek.gui.dock.frontend.DockFrontendPerspective;
import bibliothek.gui.dock.layout.DockLayoutComposition;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.perspective.Perspective;
import bibliothek.gui.dock.toolbar.perspective.CToolbarContentPerspective;

public class LocationEstimationTest {
	public static void main( String[] args ){
		CControl control = new CControl();
		
		CPerspective perspective = control.getPerspectives().createEmptyPerspective();
		CToolbarContentPerspective content = new CToolbarContentPerspective( perspective, "center" );
		
		content.getNorthToolbar().group( 0 ).column( 0 ).toolbar( 0 ).add( "red-1" );
		content.getNorthToolbar().group( 0 ).column( 0 ).toolbar( 0 ).add( "red-2" );
		
		content.getNorthToolbar().group( 0 ).column( 0 ).toolbar( 1 ).add( "green-1" );
		content.getNorthToolbar().group( 0 ).column( 0 ).toolbar( 1 ).add( "green-2" );
		content.getNorthToolbar().group( 0 ).column( 0 ).toolbar( 1 ).add( "green-3" );
		
		content.getNorthToolbar().group( 0 ).column( 1 ).toolbar( 0 ).add( "blue-1" );
		content.getNorthToolbar().group( 0 ).column( 1 ).toolbar( 0 ).add( "blue-2" );
		content.getNorthToolbar().group( 0 ).column( 1 ).toolbar( 0 ).add( "blue-3" );
		
		content.getCenter().gridAdd( 0, 0, 1, 1, new SingleCDockablePerspective("a"), new SingleCDockablePerspective( "b" ) );
		content.getCenter().gridDeploy();
		
		DockFrontendPerspective conversion = control.getPerspectives().conversion( perspective, true );
		DockLayoutComposition north = conversion.getPerspective().convert( content.getNorthToolbar().intern() );
		// DockLayoutComposition north = conversion.convert( content.getCenter().intern() );
		
		conversion.getPerspective().getSituation().estimateLocations( north );
		
		print( north );
	}
	
	private static void print( DockLayoutComposition composition ){
		DockLayoutInfo info = composition.getLayout();
		System.out.println( info.getDataLayout().getFactoryID() + " " + info.getLocation() );
		
		for( DockLayoutComposition child : composition.getChildren() ){
			print( child );
		}
	}
}
