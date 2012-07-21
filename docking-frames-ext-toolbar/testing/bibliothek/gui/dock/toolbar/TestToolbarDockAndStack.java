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
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.toolbar.expand.DefaultExpandableToolbarItemStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;

public class TestToolbarDockAndStack{

	/**
	 * @param args
	 */
	public static void main( String[] args ){

		final DockController controller = new DockController();

		final JPanel pane = new JPanel(new BorderLayout());

		/**
		 * Create a ToolbarContainerDockStation
		 * */
		final ToolbarContainerDockStation toolbarStationWest = new ToolbarContainerDockStation(
				Orientation.VERTICAL);
		pane.add(toolbarStationWest.getComponent(), BorderLayout.WEST);
		final ToolbarContainerDockStation toolbarStationNorth = new ToolbarContainerDockStation(
				Orientation.HORIZONTAL);
		pane.add(toolbarStationNorth.getComponent(), BorderLayout.NORTH);
		controller.add(toolbarStationWest);
		controller.add(toolbarStationNorth);

		// Disable the expand state action button
		controller.getProperties().set(ExpandableToolbarItemStrategy.STRATEGY,
				new DefaultExpandableToolbarItemStrategy(){
					@Override
					public boolean isEnabled( Dockable item, ExpandedState state ){
						return false;
					}
				});

		final ToolbarDockStation group1 = new ToolbarDockStation();
		JButton button = new JButton("One");
		button.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
		final ToolbarItemDockable dockable1 = new ToolbarItemDockable(button);
		group1.drop(dockable1);
		button = new JButton("One");
		button.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
		final ToolbarItemDockable dockable2 = new ToolbarItemDockable(button);
		group1.drop(dockable2);
		button = new JButton("One");
		button.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
		final ToolbarItemDockable dockable3 = new ToolbarItemDockable(button);
		group1.drop(dockable3);
		button = new JButton("One");
		button.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
		final ToolbarItemDockable dockable4 = new ToolbarItemDockable(button);
		group1.drop(dockable4);

		final ToolbarGroupDockStation toolbar1 = new ToolbarGroupDockStation();
		toolbar1.drop(group1);
		toolbarStationWest.drop(toolbar1);

		final ToolbarDockStation group2 = new ToolbarDockStation();
		final ToolbarItemDockable dockable5 = new ToolbarItemDockable(new JButton(
				"One"));
		group2.drop(dockable5);
		final ToolbarItemDockable dockable6 = new ToolbarItemDockable(new JButton(
				"One"));
		group2.drop(dockable6);
		final ToolbarDockStation group3 = new ToolbarDockStation();
		final ToolbarItemDockable dockable7 = new ToolbarItemDockable(new JButton(
				"One"));
		group3.drop(dockable7);
		final ToolbarItemDockable dockable8 = new ToolbarItemDockable(new JButton(
				"One"));
		group3.drop(dockable8);

		final ToolbarGroupDockStation toolbar2 = new ToolbarGroupDockStation();
		toolbar2.drop(group2);
		toolbar2.drop(group3);
		toolbarStationNorth.drop(toolbar2);

		/**
		 * Create a stack and add it in the center area
		 * */
		final StackDockStation stackStation = new StackDockStation();
		controller.add(stackStation);
		final DefaultDockable dockable9 = new DefaultDockable("One");
		stackStation.drop(dockable9);
		final DefaultDockable dockable10 = new DefaultDockable("One");
		stackStation.drop(dockable10);
		// controller.add(stackStation);
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		pane.add(stackStation.getComponent(), BorderLayout.CENTER);
		// toolbarStation.drop( stackStation );
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

		/**
		 * Display frame
		 * */
		final JFrame frame = new JFrame();
		frame.getContentPane().add(pane);
		// frame.add( toolbarStation.getComponent() );
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(20, 20, 400, 400);
		frame.setVisible(true);

	}

}