
import javax.swing.JFrame;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.StackDockStation;

public class TestStackDockStation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DockController controller = new DockController(); 
		
		StackDockStation station = new StackDockStation();
		controller.add(station);
		
		DefaultDockable dockable1 = new DefaultDockable("First");
		station.drop(dockable1);
		DefaultDockable dockable2 = new DefaultDockable("Second");
		station.drop(dockable2);
//		DefaultDockable dockable3 = new DefaultDockable("Third");
		
		JFrame frame =  new JFrame();
		frame.add(station.getComponent());
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(20, 20, 400, 400);
		frame.setVisible(true);	

	}

}
