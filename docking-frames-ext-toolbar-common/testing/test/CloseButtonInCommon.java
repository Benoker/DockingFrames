package test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.facile.action.CloseAction;
import bibliothek.gui.dock.frontend.FrontendEntry;
import bibliothek.gui.dock.toolbar.CToolbarContentArea;
import bibliothek.gui.dock.toolbar.CToolbarItem;
import bibliothek.gui.dock.toolbar.expand.DefaultExpandableToolbarItemStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.toolbar.location.CToolbarAreaLocation;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.DockUtilities.DockVisitor;

public class CloseButtonInCommon {
	public static void main( String[] args ){
		JFrame frame = new JFrame();

		final CControl control = new CControl( frame );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setBounds( 20, 20, 400, 400 );

		control.putProperty( ExpandableToolbarItemStrategy.STRATEGY, new DefaultExpandableToolbarItemStrategy(){
			public boolean isEnabled( Dockable item, ExpandedState state ){
				return super.isEnabled( item, state ) && state != ExpandedState.EXPANDED;
			}
		} );
		control.getController().addActionGuard( new ToolbarGroupClosing( control ) );

		CToolbarContentArea area = new CToolbarContentArea( control, "root" );
		control.addStationContainer( area );
		frame.add( area );

		CToolbarAreaLocation location = new CToolbarAreaLocation( area.getEastToolbar() );

		add( control, "A", location.group( 0 ).toolbar( 0, 0 ).item( 0 ) );
		add( control, "B", location.group( 0 ).toolbar( 0, 0 ).item( 1 ) );
		add( control, "C", location.group( 0 ).toolbar( 0, 0 ).item( 2 ) );
		add( control, "D", location.group( 0 ).toolbar( 0, 0 ).item( 3 ) );

		JMenuItem item = new JMenuItem( "Open all" );
		item.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed( ActionEvent e ){
				for( SingleCDockable dockable : control.getRegister().getSingleDockables() ) {
					dockable.setVisible( true );
				}
			}
		} );

		JMenu menu = new JMenu( "Docking" );
		menu.add( item );
		JMenuBar menubar = new JMenuBar();
		menubar.add( menu );
		frame.setJMenuBar( menubar );

		frame.setVisible( true );
	}

	private static void add( CControl control, String id, CLocation location ){
		CToolbarItem item = new CToolbarItem( id );
		item.intern().setComponent( new JButton( id ), ExpandedState.SHRUNK );
		item.setLocation( location );
		control.addDockable( item );
		item.setVisible( true );
	}

	public static class ToolbarGroupClosing extends CloseAction implements ActionGuard {
		private CControl control;

		public ToolbarGroupClosing( CControl control ){
			super( control.getController() );
			this.control = control;
		}

		@Override
		public void action( Dockable dockable ){
			control.getLocationManager().store( dockable );

			DockUtilities.visit( dockable, new DockVisitor(){
				@Override
				public void handleDockable( Dockable dockable ){
					DockFrontend frontend = control.intern();
					FrontendEntry entry = frontend.getFrontendEntry( dockable );
					if( entry != null ) {
						entry.updateLocation();
					}
				}
			} );
			DockStation parent = dockable.getDockParent();
			if( parent != null ) {
				parent.drag( dockable );
			}
		}

		@Override
		public boolean react( Dockable dockable ){
			return dockable instanceof ToolbarGroupDockStation;
		}

		@Override
		public DockActionSource getSource( Dockable dockable ){
			return new DefaultDockActionSource( new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT_OF_ALL ), this );
		}
	}
}
