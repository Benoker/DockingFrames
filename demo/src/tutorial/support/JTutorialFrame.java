package tutorial.support;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;

public class JTutorialFrame extends JFrame{
	private List<Runnable> runOnClose = new ArrayList<Runnable>();
	
	public JTutorialFrame( Class<?> clazz ){
		setBounds( 40, 40, 800, 600 );
		
		Tutorial tutorial = (Tutorial)clazz.getAnnotation( Tutorial.class );
		setTitle( tutorial == null ? clazz.getSimpleName() : tutorial.title() );
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		addWindowListener( new WindowAdapter(){
			public void windowClosing( WindowEvent e ){
				dispose();
				for( Runnable onClose : runOnClose ){
					onClose.run();
				}
			}
		});
	}
	
	public void runOnClose( Runnable run ){
		runOnClose.add( run );
	}
	
	public void destroyOnClose( final CControl control ){
		runOnClose( new Runnable(){
			public void run(){
				control.destroy();
			}
		});
	}
	
	public void destroyOnClose( final DockController controller ){
		runOnClose( new Runnable(){
			public void run(){
				controller.kill();
			}
		});
	}
	
	public void destroyOnClose( final DockFrontend frontend ){
		runOnClose( new Runnable(){
			public void run(){
				frontend.kill();	
			}
		});
	}
}
