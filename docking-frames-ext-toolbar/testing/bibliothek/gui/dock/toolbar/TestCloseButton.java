package bibliothek.gui.dock.toolbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.action.actions.SimpleSelectableAction;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupProperty;
import bibliothek.gui.dock.themes.basic.BasicSpanFactory;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;

public class TestCloseButton {
	private static int count = 0;
	
	public static void main( String[] args ){
		final JFrame frame = new JFrame();
		final JPanel pane = new JPanel( new BorderLayout() );
		frame.add( pane );

		final DockFrontend frontend = new DockFrontend( frame );
		
		//controller.setTheme( new EclipseTheme() );

		frontend.getController().getProperties().set( DockTheme.SPAN_FACTORY, new BasicSpanFactory( 500, 250 ) );
		//controller.getProperties().set( DockTheme.SPAN_FACTORY, new NoSpanFactory() );
		
		// controller.setRestrictedEnvironment( true );

		final ScreenDockStation screen = new ScreenDockStation( frame );
		frontend.addRoot( "screen", screen );

		final ToolbarContainerDockStation west = new ToolbarContainerDockStation( Orientation.VERTICAL, 5 );
		final ToolbarContainerDockStation east = new ToolbarContainerDockStation( Orientation.VERTICAL, 5 );
		final ToolbarContainerDockStation north = new ToolbarContainerDockStation( Orientation.HORIZONTAL, 5 );
		final ToolbarContainerDockStation south = new ToolbarContainerDockStation( Orientation.HORIZONTAL, 5 );

		JPanel center = new JPanel();
		center.setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) );
		frame.add( center, BorderLayout.CENTER );
		
		frontend.addRoot( "west", west );
		frontend.addRoot( "east", east );
		frontend.addRoot( "north", north );
		frontend.addRoot( "south", south );
		
		frame.add( west.getComponent(), BorderLayout.WEST );
		frame.add( east.getComponent(), BorderLayout.EAST );
		frame.add( north.getComponent(), BorderLayout.NORTH );
		frame.add( south.getComponent(), BorderLayout.SOUTH );

		final ToolbarGroupDockStation group = new ToolbarGroupDockStation();

		Icon icon = new Icon(){
			@Override
			public void paintIcon( Component c, Graphics g, int x, int y ){
				g.setColor( Color.RED );
				g.fillOval( x, y, 40, 40 );
			}

			@Override
			public int getIconWidth(){
				return 40;
			}

			@Override
			public int getIconHeight(){
				return 40;
			}
		};

		group.drop( createToolbar( true, frontend, icon, icon, icon ), 0, 0 );
		group.drop( createToolbar( true, frontend, icon, icon, icon ), 0, 1 );
		group.drop( createToolbar( false, frontend, icon, icon ), 1, 0 );
		group.drop( createToolbar( true, frontend, icon, icon ), 1, 1 );

		group.drop( createToolbar( false, frontend, icon, icon ), new ToolbarGroupProperty( 1, 0, null ) );
		group.drop( createToolbar( false, frontend, icon, icon, icon ), new ToolbarGroupProperty( 3, 2, null ) );
		group.drop( createToolbar( true, frontend, icon, icon, icon ), new ToolbarGroupProperty( -1, 5, null ) );

		// Disable the expand state action button
//		controller.getProperties().set( ExpandableToolbarItemStrategy.STRATEGY, new DefaultExpandableToolbarItemStrategy(){
//			@Override
//			public boolean isEnabled( Dockable item, ExpandedState state ){
//				return false;
//			}
//		} );
		// group.move( group.getDockable( 0 ), new ToolbarGroupProperty( 2, 1,
		// null ) );

		west.drop( group );

		frame.setBounds( 20, 20, 400, 400 );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
		screen.setShowing( true );
	}

	private static ToolbarDockStation createToolbar(  boolean largeText, DockFrontend frontend, Icon... icons){
		ToolbarDockStation toolbar = new ToolbarDockStation();
		for( Icon icon : icons ) {
			toolbar.drop( createDockable( icon, largeText, frontend ) );
		}
		return toolbar;
	}

	private static ToolbarItemDockable createDockable( Icon icon, boolean largeText, DockFrontend frontend ){
		JButton button = new JButton( icon );
		button.setBorder( new EmptyBorder( new Insets( 4, 4, 4, 4 ) ) );
		final ToolbarItemDockable dockable = new ToolbarItemDockable( button );
		if (largeText) {
			dockable.setComponent( new JButton( "a lot of text is written!!" ), ExpandedState.STRETCHED );
		} else {
			dockable.setComponent( new JButton( "short text" ), ExpandedState.STRETCHED );
		}
		
		SimpleSelectableAction.Check check = new SimpleSelectableAction.Check();
		check.setText( "Checkbox" );
		DefaultDockActionSource actions = new DefaultDockActionSource( new LocationHint( LocationHint.DOCKABLE, LocationHint.MIDDLE ),
				check, SeparatorAction.MENU_SEPARATOR );
		dockable.setActionOffers( actions );
		frontend.addDockable( String.valueOf( count++ ), dockable );
		frontend.setHideable( dockable, true );
		return dockable;
	}
}
