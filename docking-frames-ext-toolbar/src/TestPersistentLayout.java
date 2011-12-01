import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.event.DockRegisterListener;
import bibliothek.gui.dock.toolbar.expand.DefaultExpandableToolbarItemStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.DirectWindowProvider;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;

public class TestPersistentLayout{
	public static void main( String[] args ){
		JFrame frame = new JFrame();
		JPanel pane = new JPanel(new BorderLayout());
		final JToolBar toolBar = new JToolBar();
		toolBar.setOrientation(JToolBar.VERTICAL);
		ImageIcon icon = new ImageIcon(
				TestPersistentLayout.class
						.getResource("/resources/film.png"));
		ComponentDockable button00 = new ComponentDockable(new JButton(icon));
		JButton button0 = new JButton(icon);
		toolBar.add(button00.getComponent());
		JPanel panetemp = new JPanel();
		panetemp.add(button0);
		toolBar.add((panetemp));

		final DockFrontend frontend = new DockFrontend(frame);

		DirectWindowProvider windowProvider = new DirectWindowProvider();
		windowProvider.setWindow(frame);
		ScreenDockStation screenStation = new ScreenDockStation(windowProvider);
		screenStation.setShowing(true);
		frontend.addRoot("rootScreen", screenStation);

		// Disable the expand state action button
		frontend.getController().getProperties().set( ExpandableToolbarItemStrategy.STRATEGY, 
                new DefaultExpandableToolbarItemStrategy(){
            @Override
            public boolean isEnabled( Dockable item, ExpandedState state ){
                return false;
            }
        });
		
		
		icon = new ImageIcon(
				TestPersistentLayout.class
						.getResource("/resources/film.png"));
		JButton button = new JButton(icon);
		button.setBorder(new EmptyBorder(new Insets(4, 4, 4, 4)));
		ComponentDockable button1 = new ComponentDockable(button);
		button = new JButton(icon);
		button.setBorder(new EmptyBorder(new Insets(4, 4, 4, 4)));
		ComponentDockable button2 = new ComponentDockable(button);
		button = new JButton(icon);
		button.setBorder(new EmptyBorder(new Insets(4, 4, 4, 4)));
		ComponentDockable button3 = new ComponentDockable(button);
		button = new JButton(icon);
		button.setBorder(new EmptyBorder(new Insets(4, 4, 4, 4)));
		ComponentDockable button4 = new ComponentDockable(button);
		button = new JButton(icon);
		button.setBorder(new EmptyBorder(new Insets(4, 4, 4, 4)));
		ComponentDockable button5 = new ComponentDockable(button);
		ToolbarContainerDockStation rootWest = new ToolbarContainerDockStation(
				Orientation.VERTICAL);
		ToolbarContainerDockStation rootNorth = new ToolbarContainerDockStation(
				Orientation.HORIZONTAL);

		frontend.addDockable("one", button1);
		frontend.addDockable("two", button2);
		frontend.addDockable("three", button3);
		frontend.addDockable("four", button4);
		frontend.addDockable("five", button5);
		frontend.addRoot("rootwest", rootWest);
		frontend.addRoot("rootnorth", rootNorth);

		frontend.getController().getRegister()
				.addDockRegisterListener(new DockRegisterListener(){
					@Override
					public void dockableUnregistered(
							DockController controller, Dockable dockable ){
						System.out.println(" -> unregistered: " + dockable);
					}

					@Override
					public void dockableRegistering( DockController controller,
							Dockable dockable ){
						System.out.println(" -> registering: " + dockable);
					}

					@Override
					public void dockableRegistered( DockController controller,
							Dockable dockable ){
						System.out.println(" -> registered: " + dockable);
					}

					@Override
					public void dockableCycledRegister(
							DockController controller, Dockable dockable ){
						System.out.println(" -> cycled: " + dockable);
					}

					@Override
					public void dockStationUnregistered(
							DockController controller, DockStation station ){
						System.out.println(" -> station unregistered: "
								+ station);
					}

					@Override
					public void dockStationRegistering(
							DockController controller, DockStation station ){
						System.out.println(" -> station registering: "
								+ station);
					}

					@Override
					public void dockStationRegistered(
							DockController controller, DockStation station ){
						System.out
								.println(" -> station registered: " + station);
					}
				});

		frame.getContentPane().add(pane);
		pane.add(rootWest.getComponent(), BorderLayout.WEST);
		pane.add(rootNorth.getComponent(), BorderLayout.NORTH);
		pane.add(toolBar, BorderLayout.EAST);

		final File layout = new File("layout.xml");
		boolean layouted = false;

//		if (layout.exists()){
//			try{
//				FileInputStream in = new FileInputStream(layout);
//				XElement element = XIO.readUTF(in);
//				in.close();
//				frontend.readXML(element);
//				layouted = true;
//			} catch (IOException e){
//				e.printStackTrace();
//			}
//		}
//
//		if (!layouted){
			ToolbarDockStation group = new ToolbarDockStation();
			group.drop(button1);
			group.drop(button2);
			group.drop(button3);
			group.drop(button4);

			ToolbarDockStation toolbar = new ToolbarDockStation();
			toolbar.drop(group);

			rootWest.drop(toolbar);
			rootNorth.drop(button5);
//		}

		frame.setBounds(20, 20, 400, 400);
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing( WindowEvent e ){
				try{
					XElement element = new XElement("root");
					frontend.writeXML(element);
					FileOutputStream out = new FileOutputStream(layout);
					XIO.writeUTF(element, out);
					out.close();
				} catch (IOException ex){
					ex.printStackTrace();
				}
				System.exit(0);
			}
		});
		frame.setVisible(true);
	}
}
