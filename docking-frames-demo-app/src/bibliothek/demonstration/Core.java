package bibliothek.demonstration;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import bibliothek.gui.dock.support.lookandfeel.ComponentCollector;
import bibliothek.gui.dock.support.lookandfeel.LookAndFeelList;

/**
 * The center of the demonstration-application. Is responsible to create
 * the graphical user interface, to start and stop the application.
 * @author Benjamin Sigg
 */
public class Core {
	/** the panel showing all demos */
	private MainPanel main;
	/** a panel shown while a demo starts up */
	private StartupPanel startup;
	/** the list of available look and feels */
	private LookAndFeelList lookAndFeel;
	/** the frame which represents the application*/
	private JFrame frame;
	
	/** the number of open demonstrations */
	private int windowCount = 0;
	
	/**
	 * Creates a new core, creates the graphical user interface.
	 */
	public Core(){
		lookAndFeel = LookAndFeelList.getDefaultList();
		lookAndFeel.setAllowReadOnlyOnce( true );
		lookAndFeel.setReadOnce( true );
		lookAndFeel.setLookAndFeel( lookAndFeel.getSystem() );
		
		main = new MainPanel( this, listDemonstrations() );
        startup = new StartupPanel();
		
		lookAndFeel.addComponentCollector( new ComponentCollector(){
			public Collection<Component> listComponents(){
				List<Component> list = new ArrayList<Component>();
				if( frame != null )
					list.add( frame );
				return list;
			}
		});
	}
	
	/**
	 * Shows the graphical user interface
	 */
	public void startup(){
		frame = new JFrame();
		frame.setTitle( "DockingFrames" );
		
		frame.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		frame.addWindowListener( new WindowAdapter(){
			@Override
			public void windowClosing( WindowEvent e ){
				frame.dispose();
				windowCount--;
				if( windowCount == 0 )
					System.exit( 0 );
			}
		});
		
		frame.add( main );
		frame.getRootPane().setGlassPane( startup );
		
		frame.setSize( 800, 600 );
		frame.setLocationRelativeTo( null );
		
		windowCount++;
		frame.setVisible( true );
	}
	
	/**
	 * Gets a list of all demonstrations known to this Core.
	 * @return the list of demonstrations
	 */
	private List<Demonstration> listDemonstrations(){
		return Arrays.asList( new Demonstration[]{
				new bibliothek.notes.Webstart(),
				new bibliothek.help.Webstart(),
				new bibliothek.chess.Main(),
				new bibliothek.paint.Webstart(),
				new bibliothek.sizeAndColor.Core(),
				new bibliothek.layouts.Core()
		});
	}
	
	/**
	 * Starts up the given <code>demonstration</code>. The demonstration
	 * is running in an own thread.
	 * @param demonstration the demo to start
	 */
	public void start( final Demonstration demonstration ){
		Thread thread = new Thread(){
			@Override
			public void run(){
				CoreMonitor monitor = new CoreMonitor( demonstration );
				
				try{
					demonstration.show( monitor );
				}
				catch( Throwable t ){
					JOptionPane.showMessageDialog( frame, "Error on startup:\n" + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
					t.printStackTrace();
					
					monitor.running();
					monitor.shutdown();
				}
			}
		};
		
		thread.setPriority( Thread.MIN_PRIORITY );
		thread.start();
	}
	
	/**
	 * A Monitor for {@link Demonstration Demonstrations}, used in
	 * {@link Core#start(Demonstration)}.
	 * @author Benjamin Sigg
	 *
	 */
	private class CoreMonitor implements Monitor{
		/** the monitored demonstration */
		private Demonstration demonstration;
		/** the root {@link Component Components} used in this {@link #demonstration} */
		private ComponentCollector collector;
		
		/**
		 * Creates a new Monitor
		 * @param demonstration the demo that will be monitored
		 */
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
			if( collector != null ){
				lookAndFeel.removeComponentCollector( collector );
			}
			
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
