package bibliothek.demonstration;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import bibliothek.demonstration.util.ComponentCollector;
import bibliothek.demonstration.util.LookAndFeelList;

public class Core {
	private MainPanel main;
	private StartupPanel startup;
	private LookAndFeelList lookAndFeel;
	private JFrame frame;
	
	private int windowCount = 0;
	
	public Core(){
		main = new MainPanel( this, listDemonstrations() );
		startup = new StartupPanel();
		lookAndFeel = new LookAndFeelList(){
			@Override
			public void read( DataInputStream in ) throws IOException{
				// just skip
				in.readInt();
			}
		};
		
		lookAndFeel.addComponentCollector( new ComponentCollector(){
			public Collection<Component> listComponents(){
				List<Component> list = new ArrayList<Component>();
				if( frame != null )
					list.add( frame );
				return list;
			}
		});
	}
	
	public void startup(){
		frame = new JFrame();
		frame.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		frame.addWindowListener( new WindowAdapter(){
			@Override
			public void windowClosing( WindowEvent e ){
				frame.setVisible( false );
				windowCount--;
				if( windowCount == 0 )
					System.exit( 0 );
			}
		});
		
		frame.add( main );
		frame.getRootPane().setGlassPane( startup );
		
		frame.pack();
		frame.setLocationRelativeTo( null );
		
		windowCount++;
		frame.setVisible( true );
	}
	
	private List<Demonstration> listDemonstrations(){
		return Arrays.asList( new Demonstration[]{
				new bibliothek.notes.Webstart(),
				new bibliothek.help.Webstart()
		});
	}
	
	public void start( final Demonstration demonstration ){
		Thread thread = new Thread(){
			@Override
			public void run(){
				CoreMonitor monitor = new CoreMonitor( demonstration );
				demonstration.show( monitor );
			}
		};
		
		thread.start();
	}
	
	private class CoreMonitor implements Monitor{
		private Demonstration demonstration;
		private ComponentCollector collector;
		
		public CoreMonitor( Demonstration demonstration ){
			this.demonstration = demonstration;
		}
		
		public void invokeSynchron( Runnable run ) throws InvocationTargetException{
			try {
				EventQueue.invokeAndWait( run );
			}
			catch( InterruptedException e ) {
				throw new InvocationTargetException( e );
			}
		}

		public void running(){
			try {
				invokeSynchron( new Runnable(){
					public void run(){
						startup.hideAnimation();
					}
				});
			}
			catch( InvocationTargetException e ) {
				e.printStackTrace();
			}
		}

		public void shutdown(){
			if( collector != null )
				lookAndFeel.removeComponentCollector( collector );
			
			windowCount--;
			if( windowCount == 0 )
				System.exit( 0 );
		}
		
		public LookAndFeelList getGlobalLookAndFeel(){
			return lookAndFeel;
		}
		
		public void publish( ComponentCollector collector ){
			if( this.collector != null )
				throw new IllegalStateException( "Collector can only be set once" );
			
			this.collector = collector;
			lookAndFeel.addComponentCollector( collector );
		}

		public void startup(){
			windowCount++;
			try {
				invokeSynchron( new Runnable(){
					public void run(){
						startup.showAnimation( "Startup... " + demonstration.getName() );
					}
				});
			}
			catch( InvocationTargetException e ) {
				e.printStackTrace();
			}
		}
	}
}
