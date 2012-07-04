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

package bibliothek.gui.dock.toolbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupProperty;
import bibliothek.gui.dock.themes.basic.BasicSpanFactory;

public class ToolbarDockActionTest {
	public static void main( String[] args ){
		final JFrame frame = new JFrame();
		final JPanel pane = new JPanel( new BorderLayout() );
		frame.add( pane );

		final DockController controller = new DockController();
		
		Timer timer = new Timer( 2000, new ActionListener(){
			private boolean state;
			@Override
			public void actionPerformed( ActionEvent e ){
				if( state ){	
					controller.setTheme( new BubbleTheme() );			
				}
				else{
					controller.setTheme( new FlatTheme() );
				}
				state = !state;
			}
		} );
		timer.setRepeats( true );
		timer.start();
		
		controller.getProperties().set( DockTheme.SPAN_FACTORY, new BasicSpanFactory( 500, 250 ) );

		final ScreenDockStation screen = new ScreenDockStation( frame );
		controller.add( screen );

		final ToolbarContainerDockStation west = new ToolbarContainerDockStation( Orientation.VERTICAL, 5 );
		final ToolbarContainerDockStation east = new ToolbarContainerDockStation( Orientation.VERTICAL, 5 );
		final ToolbarContainerDockStation north = new ToolbarContainerDockStation( Orientation.HORIZONTAL, 5 );
		final ToolbarContainerDockStation south = new ToolbarContainerDockStation( Orientation.HORIZONTAL, 5 );

		controller.add( west );
		controller.add( east );
		controller.add( north );
		controller.add( south );

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

		group.drop( createToolbar( true, icon, icon, icon ), 0, 0 );
		group.drop( createToolbar( true, icon, icon, icon ), 0, 1 );
		group.drop( createToolbar( false, icon, icon ), 1, 0 );
		group.drop( createToolbar( true, icon, icon ), 1, 1 );

		group.drop( createToolbar( false, icon, icon ), new ToolbarGroupProperty( 1, 0, null ) );
		group.drop( createToolbar( false, icon, icon, icon ), new ToolbarGroupProperty( 3, 2, null ) );
		group.drop( createToolbar( true, icon, icon, icon ), new ToolbarGroupProperty( -1, 5, null ) );

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

	private static ToolbarDockStation createToolbar( boolean largeText, Icon... icons ){
		ToolbarDockStation toolbar = new ToolbarDockStation();
		for( Icon icon : icons ) {
			toolbar.drop( createDockable( icon, largeText ) );
		}
		return toolbar;
	}

	private static ToolbarItemDockable createDockable( Icon icon, boolean largeText ){
		SimpleButtonAction action = new SimpleButtonAction();
		action.setIcon( icon );
		if( largeText ){
			action.setText( "a lot of text is written" );
		}
		else{
			action.setText( "short text" );
		}
		
		return new ToolbarItemDockable( action );
	}
}
