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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bibliothek.gui.DockController;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;

public class TestToolbarContainerDockStation{

	/**
	 * @param args
	 */
	public static void main( String[] args ){
		final JFrame frame = new JFrame();
		final JPanel pane = new JPanel(new BorderLayout());
		frame.add(pane);

		final DockController controller = new DockController();
		final ScreenDockStation screen = new ScreenDockStation(frame);
		controller.add(screen);

		System.out
				.println("###############################################################");
		System.out
				.println("##################  NEW CONTAINER  ############################");
		System.out
				.println("###############################################################");
		final ToolbarContainerDockStation toolbarStationWest = new ToolbarContainerDockStation(
				Orientation.HORIZONTAL);
		controller.add(toolbarStationWest);
		pane.add(toolbarStationWest.getComponent(), BorderLayout.NORTH);
		final ToolbarContainerDockStation toolbarStationEast = new ToolbarContainerDockStation(
				Orientation.VERTICAL);
		controller.add(toolbarStationEast);
		pane.add(toolbarStationEast.getComponent(), BorderLayout.EAST);
		System.out
				.println("###############################################################");
		System.out
				.println("##################  NEW COMPONENT  ############################");
		System.out
				.println("###############################################################");
		final ToolbarItemDockable dockable1 = createDockable("1", "One");
		System.out
				.println("###############################################################");
		System.out
				.println("##################  COMPONENT DROP INTO GROUP  ################");
		System.out
				.println("###############################################################");

		toolbarStationWest.drop(dockable1);
		dockable1.getDockParent().drop(createDockable("2", "Two"));
		dockable1.getDockParent().drop(createDockable("3", "Three"));
		dockable1.getDockParent().drop(createDockable("4", "Four"));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(20, 20, 500, 500);
		screen.setShowing(true);
		frame.setVisible(true);
		System.out.println("##############################################");
		System.out.println("##################  MAIN END  ################");
		System.out.println("##############################################");

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