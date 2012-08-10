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
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DockRegisterListener;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicStationPaint;
import bibliothek.gui.dock.toolbar.expand.DefaultExpandableToolbarItemStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.DirectWindowProvider;

public class TestPersistentLayout{
	public static void main( String[] args ){
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JPanel pane = new JPanel(new BorderLayout());
		final JToolBar toolBar = new JToolBar();
		toolBar.setOrientation(SwingConstants.VERTICAL);
		ImageIcon icon = new ImageIcon(
				TestPersistentLayout.class.getResource("/resources/film.png"));
		final ToolbarItemDockable button00 = new ToolbarItemDockable(new JButton(
				icon));
		final JButton button0 = new JButton(icon);
		toolBar.add(button00.getComponent());
		final JPanel panetemp = new JPanel();
		panetemp.add(button0);
		toolBar.add((panetemp));

		final DockFrontend frontend = new DockFrontend(frame);

		final DirectWindowProvider windowProvider = new DirectWindowProvider();
		windowProvider.setWindow(frame);
		final ScreenDockStation screenStation = new ScreenDockStation(
				windowProvider);
		screenStation.setShowing(true);
		frontend.addRoot("rootScreen", screenStation);

		// Disable the expand state action button
		frontend.getController()
				.getProperties()
				.set(ExpandableToolbarItemStrategy.STRATEGY,
						new DefaultExpandableToolbarItemStrategy(){
							@Override
							public boolean isEnabled( Dockable item,
									ExpandedState state ){
								return false;
							}
						});
		// install new station pain to change the color
		final BasicStationPaint paint = new BasicStationPaint();
		final Color color = new Color(16, 138, 230, 150);
		paint.setColor(color);
		frontend.getController()
				.getThemeManager()
				.setStationPaint(ThemeManager.STATION_PAINT + ".toolbar", paint);

		icon = new ImageIcon(
				TestPersistentLayout.class.getResource("/resources/film.png"));
		JButton button = new JButton(icon);
		button.setBorder(new EmptyBorder(new Insets(4, 4, 4, 4)));
		final ToolbarItemDockable button1 = new ToolbarItemDockable(button);
		button = new JButton(icon);
		button.setBorder(new EmptyBorder(new Insets(4, 4, 4, 4)));
		final ToolbarItemDockable button2 = new ToolbarItemDockable(button);
		button = new JButton(icon);
		button.setBorder(new EmptyBorder(new Insets(4, 4, 4, 4)));
		final ToolbarItemDockable button3 = new ToolbarItemDockable(button);
		button = new JButton(icon);
		button.setBorder(new EmptyBorder(new Insets(4, 4, 4, 4)));
		final ToolbarItemDockable button4 = new ToolbarItemDockable(button);
		button = new JButton(icon);
		button.setBorder(new EmptyBorder(new Insets(4, 4, 4, 4)));
		final ToolbarItemDockable button5 = new ToolbarItemDockable(button);
		button = new JButton(icon);
		button.setBorder(new EmptyBorder(new Insets(4, 4, 4, 4)));
		final ToolbarItemDockable button6 = new ToolbarItemDockable(button);
		final ToolbarContainerDockStation rootWest = new ToolbarContainerDockStation(
				Orientation.VERTICAL);
		rootWest.setDockablesMaxNumber(1);
		final ToolbarContainerDockStation rootNorth = new ToolbarContainerDockStation(
				Orientation.HORIZONTAL);
		rootNorth.setDockablesMaxNumber(1);

		frontend.addDockable("one", button1);
		frontend.addDockable("two", button2);
		frontend.addDockable("three", button3);
		frontend.addDockable("four", button4);
		frontend.addDockable("five", button5);
		frontend.addDockable("six", button6);
		frontend.addRoot("rootwest", rootWest);
		frontend.addRoot("rootnorth", rootNorth);

		frontend.getController().getRegister()
				.addDockRegisterListener(new DockRegisterAdapter(){
					@Override
					public void dockableUnregistered(
							DockController controller, Dockable dockable ){
						// System.out.println(" -> unregistered: " + dockable);
					}

					@Override
					public void dockableRegistering( DockController controller,
							Dockable dockable ){
						// System.out.println(" -> registering: " + dockable);
					}

					@Override
					public void dockableRegistered( DockController controller,
							Dockable dockable ){
						// System.out.println(" -> registered: " + dockable);
					}

					@Override
					public void dockableCycledRegister(
							DockController controller, Dockable dockable ){
						// System.out.println(" -> cycled: " + dockable);
					}

					@Override
					public void dockStationUnregistered(
							DockController controller, DockStation station ){
						// System.out.println(" -> station unregistered: "
						// + station);
					}

					@Override
					public void dockStationRegistering(
							DockController controller, DockStation station ){
						// System.out.println(" -> station registering: "
						// + station);
					}

					@Override
					public void dockStationRegistered(
							DockController controller, DockStation station ){
						// System.out
						// .println(" -> station registered: " + station);
					}
				});

		frame.getContentPane().add(pane);
		pane.add(rootWest.getComponent(), BorderLayout.WEST);
		pane.add(rootNorth.getComponent(), BorderLayout.NORTH);
		pane.add(toolBar, BorderLayout.EAST);

		// if (layout.exists()){
		// try{
		// FileInputStream in = new FileInputStream(layout);
		// XElement element = XIO.readUTF(in);
		// in.close();
		// frontend.readXML(element);
		// layouted = true;
		// } catch (IOException e){
		// e.printStackTrace();
		// }
		// }
		//
		// if (!layouted){
		final ToolbarDockStation group = new ToolbarDockStation();
		group.drop(button1);
		group.drop(button2);
		group.drop(button3);
		group.drop(button4);
		group.drop(button6);

		final ToolbarDockStation toolbar = new ToolbarDockStation();
		toolbar.drop(group);

		rootWest.drop(toolbar);
		rootNorth.drop(button5);
		// }

		frame.setBounds(20, 20, 400, 400);
		// frame.addWindowListener(new WindowAdapter(){
		// @Override
		// public void windowClosing( WindowEvent e ){
		// try{
		// XElement element = new XElement("root");
		// frontend.writeXML(element);
		// FileOutputStream out = new FileOutputStream(layout);
		// XIO.writeUTF(element, out);
		// out.close();
		// } catch (IOException ex){
		// ex.printStackTrace();
		// }
		// System.exit(0);
		// }
		// });
		frame.setVisible(true);
	}
}
