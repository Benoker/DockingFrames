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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bibliothek.gui.DockController;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.DirectWindowProvider;

public class TestToolbarDockStation{

	/**
	 * @param args
	 */
	public static void main( String[] args ){
		final JFrame frame = new JFrame();

		final DockController controller = new DockController();
		// controller.getRelocator().setDragOnlyTitel(true);

		final DirectWindowProvider windowProvider = new DirectWindowProvider();
		windowProvider.setWindow(frame);
		final ScreenDockStation screenStation = new ScreenDockStation(
				windowProvider);
		screenStation.setShowing(true);
		controller.add(screenStation);
		System.out
				.println("###############################################################");
		System.out
				.println("##################  NEW CONTAINER  ############################");
		System.out
				.println("###############################################################");
		final ToolbarContainerDockStation toolbarStation = new ToolbarContainerDockStation(
				Orientation.HORIZONTAL);
		controller.add(toolbarStation);
		System.out
				.println("###############################################################");
		System.out
				.println("##################  NEW COMPONENT  ############################");
		System.out
				.println("###############################################################");
		final ToolbarItemDockable dockable1 = createDockable("1", "One");
		final ToolbarItemDockable dockable2 = createDockable("2", "Two");
		final ToolbarItemDockable dockable3 = createDockable("3", "Three");
		System.out
				.println("###############################################################");
		System.out
				.println("##################  NEW GROUP  ################################");
		System.out
				.println("###############################################################");
		final ToolbarDockStation group1 = new ToolbarDockStation();
		System.out
				.println("###############################################################");
		System.out
				.println("##################  NEW TOOLBAR  ##############################");
		System.out
				.println("###############################################################");
		final ToolbarGroupDockStation toolbar1 = new ToolbarGroupDockStation();
		final ToolbarGroupDockStation toolbar2 = new ToolbarGroupDockStation();
		final ToolbarGroupDockStation toolbar3 = new ToolbarGroupDockStation();
		System.out
				.println("###############################################################");
		System.out
				.println("##################  COMPONENT DROP INTO GROUP  ################");
		System.out
				.println("###############################################################");
		group1.drop(dockable1);
		System.out
				.println("###############################################################");
		System.out
				.println("##################  GROUP DROP INTO TOOLBAR  ##################");
		System.out
				.println("###############################################################");
		toolbar1.drop(group1);
		toolbar2.drop(dockable2);
		toolbar3.drop(dockable3);
		System.out
				.println("###############################################################");
		System.out
				.println("##################  TOOLBAR DROP INTO CONTAINER  ##############");
		System.out
				.println("###############################################################");
		toolbarStation.drop(toolbar1);
		toolbarStation.drop(toolbar2);
		toolbarStation.drop(toolbar3);

		// ToolbarDockStation toolbar3 = new ToolbarDockStation();
		// ComponentDockable dockable6 = new ComponentDockable( new JButton(
		// "Six" ) );
		// toolbar3.drop( dockable6 );
		// toolbarStation.drop( toolbar3, Position.SOUTH );
		//
		// ToolbarGroupDockStation group2 = new ToolbarGroupDockStation();
		// ComponentDockable dockable5 = new ComponentDockable( new JButton(
		// "Five" ) );
		// group2.drop( dockable5 );
		//
		// ToolbarDockStation toolbar2 = new ToolbarDockStation();
		// toolbar2.drop( group2 );
		// toolbarStation.drop( toolbar2, Position.WEST );

		// Dockable other = new DefaultDockable( "Hallo" );
		// screenStation.drop( other );

		frame.add(toolbarStation.getComponent());

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(20, 20, 500, 500);
		frame.setVisible(true);
		// screenStation.setShowing(true);

	}

	private static ToolbarItemDockable createDockable( String small, String large ){
		final ToolbarItemDockable dockable = new ToolbarItemDockable();
		dockable.setComponent(new JLabel(small), ExpandedState.SHRUNK);
		dockable.setComponent(new JButton(large), ExpandedState.STRETCHED);
		dockable.setComponent(new JScrollPane(new JTextArea(small + "\n\n"
				+ large)), ExpandedState.EXPANDED);
		return dockable;
	}
}