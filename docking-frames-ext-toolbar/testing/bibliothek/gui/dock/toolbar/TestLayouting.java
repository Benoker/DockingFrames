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

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.control.SingleParentRemover;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;

public class TestLayouting{
	public static void main( String[] args ){
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final DockController controller = new DockController();
		controller.setSingleParentRemover(new SingleParentRemover(){
			@Override
			protected boolean test( DockStation station ){
				return false;
			}
		});

		controller.setRootWindow(frame);

		final ScreenDockStation screen = new ScreenDockStation(
				controller.getRootWindowProvider());
		controller.add(screen);
		final ScreenDockProperty initial = new ScreenDockProperty(20, 20, 200,
				20);

		final ToolbarItemDockable dockable = new ToolbarItemDockable(new JButton(
				"hello"));

		final ToolbarDockStation group = new ToolbarDockStation(){
			@Override
			public boolean accept( DockStation station ){
				return true;
			}
		};
		group.drop(dockable);

		final ToolbarGroupDockStation toolbar = new ToolbarGroupDockStation();
		toolbar.drop(group);

		final boolean dropped = screen.drop(toolbar, initial);
		if (!dropped){
			throw new IllegalStateException("not dropped");
		}

		screen.setShowing(true);
		frame.setBounds(0, 0, 300, 300);
		frame.setVisible(true);

		System.out.println(dockable.getComponent().getPreferredSize());
		System.out.println(group.getComponent().getPreferredSize());
		System.out.println(toolbar.getComponent().getPreferredSize());

	}
}
