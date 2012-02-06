import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupProperty;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;

public class TestToolbarGroupDockStation {
	public static void main( String[] args ){
		//		
		//		Toolkit.getDefaultToolkit().addAWTEventListener( new AWTEventListener(){
		//			
		//			@Override
		//			public void eventDispatched( AWTEvent event ){
		//				System.out.println( " - " + event );
		//			}
		//		}, MouseEvent.MOUSE_EVENT_MASK | MouseEvent.MOUSE_MOTION_EVENT_MASK );
		//		
		final JFrame frame = new JFrame();
		final JPanel pane = new JPanel( new BorderLayout() );
		frame.add( pane );

		final DockController controller = new DockController();
		//controller.setTheme( new EclipseTheme() );

		controller.setRestrictedEnvironment( true );

		final ScreenDockStation screen = new ScreenDockStation( frame );
		controller.add( screen );

		final ToolbarContainerDockStation west = new ToolbarContainerDockStation( Orientation.VERTICAL );
		final ToolbarContainerDockStation east = new ToolbarContainerDockStation( Orientation.VERTICAL );
		final ToolbarContainerDockStation north = new ToolbarContainerDockStation( Orientation.HORIZONTAL );
		final ToolbarContainerDockStation south = new ToolbarContainerDockStation( Orientation.HORIZONTAL );

		controller.add( west );
		controller.add( east );
		controller.add( north );
		controller.add( south );

		frame.add( west.getComponent(), BorderLayout.WEST );
		frame.add( east.getComponent(), BorderLayout.EAST );
		frame.add( north.getComponent(), BorderLayout.NORTH );
		frame.add( south.getComponent(), BorderLayout.SOUTH );

		final ToolbarGroupDockStation group = new ToolbarGroupDockStation();
//
//		group.getColumnModel().addListener( new ToolbarColumnModelListener(){
//			private ToolbarColumnListener listener = new ToolbarColumnListener(){
//				@Override
//				public void inserted( ToolbarColumn column, Dockable item, int index ){
//					System.out.println( "item inserted: " + column.getColumnIndex() + ", " + index );
//				}
//
//				@Override
//				public void removed( ToolbarColumn column, Dockable item, int index ){
//					System.out.println( "item removed: " + column.getColumnIndex() + ", " + index );
//				}
//			};
//
//			@Override
//			public void removed( ToolbarColumnModel model, ToolbarColumn column, int index ){
//				System.out.println( "column removed: " + index );
//				column.removeListener( listener );
//			}
//
//			@Override
//			public void inserted( ToolbarColumnModel model, ToolbarColumn column, int index ){
//				System.out.println( "column inserted: " + index );
//				column.addListener( listener );
//			}
//		} );

		//		Icon icon = new ImageIcon(
		//				TestPersistentLayout.class.getResource("/resources/film.png"));

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

		group.drop( createToolbar( icon, icon, icon ), 0, 0 );
		group.drop( createToolbar( icon, icon, icon ), 0, 1 );
		group.drop( createToolbar( icon, icon ), 1, 0 );
		group.drop( createToolbar( icon, icon ), 1, 1 );

		group.drop( createToolbar( icon, icon ), new ToolbarGroupProperty( 1, 0, null ) );
		group.drop( createToolbar( icon, icon, icon ), new ToolbarGroupProperty( 3, 2, null ) );
		group.drop( createToolbar( icon, icon, icon ), new ToolbarGroupProperty( -1, 5, null ) );

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

	private static ToolbarDockStation createToolbar( String... buttons ){
		final ToolbarDockStation toolbar = new ToolbarDockStation();
		for( final String button : buttons ) {
			toolbar.drop( createDockable( button.toLowerCase(), button.toUpperCase() ) );
		}
		return toolbar;
	}

	private static ToolbarDockStation createToolbar( Icon... icons ){
		ToolbarDockStation toolbar = new ToolbarDockStation();
		for( Icon icon : icons ) {
			toolbar.drop( createDockable( icon ) );
		}
		return toolbar;
	}

	private static ComponentDockable createDockable( String small, String large ){
		final ComponentDockable dockable = new ComponentDockable();
		dockable.setComponent( new JLabel( small ), ExpandedState.SHRUNK );
		dockable.setComponent( new JButton( large ), ExpandedState.STRETCHED );
		dockable.setComponent( new JScrollPane( new JTextArea( small + "\n\n" + large ) ), ExpandedState.EXPANDED );
		return dockable;
	}

	private static ComponentDockable createDockable( Icon icon ){
		JButton button = new JButton( icon );
		button.setBorder( new EmptyBorder( new Insets( 4, 4, 4, 4 ) ) );
		final ComponentDockable dockable = new ComponentDockable( button );
		dockable.setComponent( new JButton( "some text" ), ExpandedState.STRETCHED );
		return dockable;
	}
}
