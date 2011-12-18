
import javax.swing.JFrame;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

@Todo(compatibility=Compatibility.COMPATIBLE, priority=Priority.MAJOR, target=Version.VERSION_1_1_1,
	description="delete this class")
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
