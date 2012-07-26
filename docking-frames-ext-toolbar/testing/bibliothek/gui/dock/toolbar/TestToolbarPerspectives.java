package bibliothek.gui.dock.toolbar;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.frontend.DockFrontendPerspective;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerDockPerspective;
import bibliothek.gui.dock.station.toolbar.ToolbarDockPerspective;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockPerspective;
import bibliothek.gui.dock.toolbar.perspective.FrontendToolbarItemPerspective;

public class TestToolbarPerspectives {
	public static void main( String[] args ){
		JFrame frame = new JFrame();
		DockFrontend frontend = new DockFrontend( frame );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setBounds( 20, 20, 400, 400 );
		
		ToolbarContainerDockStation container = new ToolbarContainerDockStation( Orientation.VERTICAL );
		frame.add( container.getComponent(), BorderLayout.WEST );
		frontend.addRoot( "left", container );
		
		ToolbarItemDockable itemA = new ToolbarItemDockable( new JButton( "A" ) );
		ToolbarItemDockable itemB = new ToolbarItemDockable( new JButton( "B" ) );
		ToolbarItemDockable itemC = new ToolbarItemDockable( new JButton( "C" ) );
		ToolbarItemDockable itemD = new ToolbarItemDockable( new JButton( "D" ) );
		
		frontend.addDockable( "a", itemA );
		frontend.addDockable( "b", itemB );
		frontend.addDockable( "c", itemC );
		frontend.addDockable( "d", itemD );
		
		DockFrontendPerspective perspective = frontend.getPerspective( true );
		setup( perspective );
		perspective.apply();
		
		frame.setVisible( true );
	}
	
	private static void setup( DockFrontendPerspective perspective ){
		ToolbarContainerDockPerspective container = (ToolbarContainerDockPerspective)perspective.getRoot( "left" );
		ToolbarGroupDockPerspective group = new ToolbarGroupDockPerspective();
		ToolbarDockPerspective toolbar1 = new ToolbarDockPerspective();
		ToolbarDockPerspective toolbar2 = new ToolbarDockPerspective();
		
		container.add( group );
		group.add( 0, toolbar1 );
		group.add( 0, toolbar2 );
		
		toolbar1.add( new FrontendToolbarItemPerspective( "a" ) );
		toolbar1.add( new FrontendToolbarItemPerspective( "b" ) );
		toolbar2.add( new FrontendToolbarItemPerspective( "c" ) );
		toolbar2.add( new FrontendToolbarItemPerspective( "d" ) );
	}
}
