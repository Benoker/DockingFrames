/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.dockable.DockableStateEvent;
import bibliothek.gui.dock.dockable.DockableStateListener;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.frontend.FrontendEntry;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategyListener;
import bibliothek.gui.dock.toolbar.expand.DefaultExpandableToolbarItemStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.DockUtilities.DockVisitor;
import bibliothek.util.Path;

public class CloseButtonInCore {
	public static void main( String[] args ){
		JFrame frame = new JFrame();

		final DockFrontend frontend = new DockFrontend( frame );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setBounds( 20, 20, 400, 400 );

		frontend.getDockProperties().set( ExpandableToolbarItemStrategy.STRATEGY, new DefaultExpandableToolbarItemStrategy(){
			public boolean isEnabled( Dockable item, ExpandedState state ){
				return super.isEnabled( item, state ) && state != ExpandedState.EXPANDED;
			}
		} );
		frontend.getController().addActionGuard( new ToolbarGroupClosing( frontend ) );

		JPanel pane = new JPanel( new BorderLayout() );
		frame.add( pane );

		SplitDockStation center = new SplitDockStation();
		ScreenDockStation screen = new ScreenDockStation( frame );
		frontend.addRoot( "screen", screen );
		frontend.addRoot( "center", center );

		ToolbarContainerDockStation toolbarStationWest = new ToolbarContainerDockStation( Orientation.VERTICAL );
		ToolbarContainerDockStation toolbarStationNorth = new ToolbarContainerDockStation( Orientation.HORIZONTAL );

		frontend.addRoot( "west", toolbarStationWest );
		frontend.addRoot( "north", toolbarStationNorth );

		pane.add( center.getComponent(), BorderLayout.CENTER );
		pane.add( toolbarStationWest.getComponent(), BorderLayout.WEST );
		pane.add( toolbarStationNorth.getComponent(), BorderLayout.NORTH );

		final ToolbarItemDockable dockable1 = createDockable( "1", "One" );
		final ToolbarItemDockable dockable2 = createDockable( "2", "Two" );
		final ToolbarItemDockable dockable3 = createDockable( "3", "Three" );
		final ToolbarItemDockable dockable4 = createDockable( "4", "Four" );

		/*
		 * This is how a PlaceholderStrategy would look like. It just needs a method that takes a Dockable,
		 * and tells what placeholder this dockable has.
		 * You could store the placeholder directly in the ComponentDockable and make a cast...
		 */
		frontend.getDockProperties().set( PlaceholderStrategy.PLACEHOLDER_STRATEGY, new PlaceholderStrategy(){
			@Override
			public void uninstall( DockStation station ){
				// ignore
			}

			@Override
			public void removeListener( PlaceholderStrategyListener listener ){
				// ignore	
			}

			@Override
			public boolean isValidPlaceholder( Path placeholder ){
				return true;
			}

			@Override
			public void install( DockStation station ){
				// ignore
			}

			@Override
			public Path getPlaceholderFor( Dockable dockable ){
				String postfix = null;
				if( dockable == dockable1 ) {
					postfix = "dock1";
				}
				else if( dockable == dockable2 ) {
					postfix = "dock2";
				}
				else if( dockable == dockable3 ) {
					postfix = "dock3";
				}
				else if( dockable == dockable4 ) {
					postfix = "dock4";
				}
				else {
					return null;
				}

				return new Path( "custom", postfix );
			}

			@Override
			public void addListener( PlaceholderStrategyListener listener ){
				// ignore	
			}
		} );

		frontend.setDefaultHideable( false );

		frontend.addDockable( "d1", dockable1 );
		frontend.addDockable( "d2", dockable2 );
		frontend.addDockable( "d3", dockable3 );
		frontend.addDockable( "d4", dockable4 );

		toolbarStationWest.drop( dockable1 );
		dockable1.getDockParent().drop( dockable2 );
		dockable1.getDockParent().drop( dockable3 );
		dockable1.getDockParent().drop( dockable4 );
		
		JMenu menu = new JMenu( "Panels" );
		menu.add( createItem( "1", dockable1, frontend ) );
		menu.add( createItem( "2", dockable2, frontend ) );
		menu.add( createItem( "3",dockable3, frontend ) );
		menu.add( createItem( "4",dockable4, frontend ) );

		DefaultDockable dockable = new DefaultDockable( "Center" );
		dockable.addDockableStateListener( new DockableStateListener(){
			@Override
			public void changed( DockableStateEvent event ){
				System.out.println( "center " + event );
			}
		} );
		center.drop( dockable );

		JButton openAll = new JButton( "open all closed buttons" );
		pane.add( openAll, BorderLayout.CENTER );
		openAll.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed( ActionEvent e ){
				for( Dockable dockable : frontend.listDockables() ) {
					frontend.show( dockable );
				}
			}
		} );

		JMenuBar menuBar = new JMenuBar();
		menuBar.add( menu );
		frame.setJMenuBar( menuBar );
		
		screen.setShowing( true );
		frame.setVisible( true );
	}

	private static JMenuItem createItem( String title, final Dockable dockable, final DockFrontend frontend ){
		final JCheckBoxMenuItem item = new JCheckBoxMenuItem( title );
		item.setSelected( true );
		dockable.addDockHierarchyListener( new DockHierarchyListener(){
			
			@Override
			public void hierarchyChanged( DockHierarchyEvent event ){
				item.setSelected( dockable.getController() != null );
			}
			
			@Override
			public void controllerChanged( DockHierarchyEvent event ){
				item.setSelected( dockable.getController() != null );
			}
		} );
		
		item.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed( ActionEvent e ){
				if( item.isSelected() ){
					frontend.show( dockable );
				}
				else{
					frontend.hide( dockable );
				}
			}
		} );
		
		return item;
	}
	
	private static ToolbarItemDockable createDockable( final String small, String large ){
		final ToolbarItemDockable dockable = new ToolbarItemDockable();
		dockable.setComponent( new JButton( new ButtonIcon() ), ExpandedState.SHRUNK );
		dockable.setComponent( new JButton( large ), ExpandedState.STRETCHED );

		dockable.addDockableStateListener( new DockableStateListener(){

			@Override
			public void changed( DockableStateEvent event ){
				System.out.println( small + ": " + event );
			}
		} );

		return dockable;
	}

	public static class ButtonIcon implements Icon{
		private Color color;

		public ButtonIcon(){
			this.color = new Color( (int)(Math.random() * (1 << 24)) );
		}

		public int getIconWidth(){
			return 16;
		}

		public int getIconHeight(){
			return 16;
		}

		@Override
		public void paintIcon( Component c, Graphics g, int x, int y ){
			g.setColor( color );
			g.fillOval( x, y, 16, 16 );
		}
	}
	
	public static class CloseIcon implements Icon {
		private Color color;

		public CloseIcon( Color color ){
			this.color = color;
		}

		public int getIconWidth(){
			return 8;
		}

		public int getIconHeight(){
			return 8;
		}

		@Override
		public void paintIcon( Component c, Graphics g, int x, int y ){
			g.setColor( color );
			g.drawLine( x + 2, y + 2, x + 7, y + 7 );
			g.drawLine( x + 3, y + 2, x + 7, y + 6 );
			g.drawLine( x + 2, y + 3, x + 6, y + 7 );

			g.drawLine( x + 2, y + 7, x + 7, y + 2 );
			g.drawLine( x + 3, y + 7, x + 6, y + 2 );
			g.drawLine( x + 2, y + 6, x + 7, y + 3 );
		}
	}

	public static class ToolbarGroupClosing extends SimpleButtonAction implements ActionGuard {
		private DockFrontend frontend;

		public ToolbarGroupClosing( DockFrontend frontend ){
			this.frontend = frontend;

			setText( "Close" );
			setTooltip( "Close this toolbar" );
			setIcon( ActionContentModifier.NONE_HOVER, new CloseIcon( Color.RED ) );
			setIcon( new CloseIcon( Color.LIGHT_GRAY ) );
		}

		@Override
		public void action( Dockable dockable ){
			DockUtilities.visit( dockable, new DockVisitor(){
				@Override
				public void handleDockable( Dockable dockable ){
					FrontendEntry entry = frontend.getFrontendEntry( dockable );
					if( entry != null ) {
						entry.updateLocation();
					}
				}
			} );
			frontend.hide( dockable );
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
