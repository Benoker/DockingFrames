package bibliothek.notes;

import java.awt.Component;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;

import bibliothek.demonstration.Monitor;
import bibliothek.demonstration.util.ComponentCollector;
import bibliothek.demonstration.util.LookAndFeelList;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.RectGradientPainter;
import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.security.SecureDockController;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.MainFrame;
import bibliothek.notes.view.ViewManager;
import bibliothek.notes.view.actions.icon.IconGrid;
import bibliothek.notes.view.panels.NoteViewFactory;
import bibliothek.notes.view.themes.NoteBasicTheme;

/**
 * The core is the center of this application. All objects can be
 * reached directly or indirectly through the core. The core also
 * contains the most important methods of this application,
 * like {@link #startup()} or {@link #shutdown()}.
 * @author Benjamin Sigg
 */
public class Core implements ComponentCollector{
    /** the set of {@link Note}s */
	private NoteModel model;
	/** the manager for different views */
	private ViewManager views;
	
	/** the first frame of this application */
	private MainFrame frame;
	/** set of available {@link LookAndFeel}s */
	private LookAndFeelList lookAndFeels;
	
	/** whether the application runs in a restricted environment or not */
	private boolean secure;
	/**
	 * Callback used when this application runs as plugin of the
	 * demonstration-framework. 
	 */
	private Monitor monitor;

	/** link to the docking-frames */
	private DockFrontend frontend;
	
	/**
	 * Creates a new core
	 * @param secure whether this application runs in a secure environment
	 * or not
	 * @param monitor a callback used to inform the demonstration-framework
	 * about the state of this application
	 */
	public Core( boolean secure, Monitor monitor ){
		this.secure = secure;
		this.monitor = monitor;
	}

	/**
	 * Tells whether this application runs in a secure environment or not.
	 * If so, the {@link SecurityManager} is active and the secure version
	 * of the docking-frames should be used.
	 * @return <code>true</code> if the application is in a secure environment
	 */
	public boolean isSecure(){
		return secure;
	}
	
	/**
	 * Starts the application. This method creates a new {@link NoteModel},
	 * {@link MainFrame}, etc... and shows these elements. This method
	 * should never be called twice.<br>
	 * This method tries to read the properties (location of {@link Note}s) of
	 * this application. If the application is in a {@link #isSecure() secure environment},
	 * then a backup-poperties-file is used.
	 * @see #shutdown()
	 */
	public void startup(){
		model = new NoteModel();
		frame = new MainFrame();
		
		final DockController controller;
		if( secure )
			controller = new SecureDockController();
		else
			controller = new DockController();
		
		controller.setTheme( new NoteBasicTheme() );
		controller.setSingleParentRemove( true );
		controller.getProperties().set( EclipseTheme.PAINT_ICONS_WHEN_DESELECTED, true );
		controller.getProperties().set( EclipseTheme.TAB_PAINTER, RectGradientPainter.FACTORY );
		
		frontend = new DockFrontend( controller, frame );
		views = new ViewManager( frontend, frame, secure, model );
		frontend.registerFactory( new NoteViewFactory( views.getNotes(), model ));
		
		if( monitor == null ){
			lookAndFeels = new LookAndFeelList();
			lookAndFeels.addComponentCollector( this );
		}
		else
			lookAndFeels = monitor.getGlobalLookAndFeel();
		
		frame.setup( this );
		frame.setBounds( 20, 20, 600, 400 );
		
		try{
			if( !secure ){
				File file = new File( "notes.properties" );
				DataInputStream in = new DataInputStream( new BufferedInputStream( new FileInputStream( file )));
				read( in );
				in.close();
			}
			else{
				DataInputStream in = new DataInputStream( ResourceSet.openStream( "/data/bibliothek/notes/backup.properties" ));
				read( in );
				in.close();
			}
		}
		catch( IOException ex ){
			ex.printStackTrace();
		}
		finally{
			SwingUtilities.invokeLater( new Runnable(){
				public void run(){
					frame.setVisible( true );
					views.getScreen().setShowing( true );
				}
			});
			
			if( monitor != null ){
				monitor.publish( this );
				monitor.running();
			}
		}
	}
	
	/**
	 * Stops this application, closes the {@link #getFrame() MainFrame} and saves
	 * all properties if the application is not in a {@link #isSecure() secure environment}.
	 */
	public void shutdown(){
		try{
			frame.setVisible( false );
			views.getScreen().setShowing( false );
			frontend.getController().kill();
			
			if( !secure ){
				try{
					File file = new File( "notes.properties" );
					DataOutputStream out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream( file )));
					write( out );
					out.flush();
					out.close();
				}
				catch( IOException ex ){
					ex.printStackTrace();
				}
				catch( Throwable t ){
					t.printStackTrace();
				}
			}
		}
		finally{
			if( monitor == null )
				System.exit( 0 );
			else
				monitor.shutdown();
		}
	}
	
	/**
	 * Gets the first frame of this application.
	 * @return the first frame
	 */
	public MainFrame getFrame(){
		return frame;
	}
	
	/**
	 * Gets the set of {@link Note}s known in this application
	 * @return the set of {@link Note}s
	 */
	public NoteModel getModel(){
		return model;
	}
	
	/**
	 * Gets the set of root-{@link DockStation}s
	 * @return the manager
	 */
	public ViewManager getViews(){
		return views;
	}
	
	/**
	 * Gets the list of available and selected {@link LookAndFeel}s.
	 * @return the list of <code>LookAndFeel</code>s 
	 */
	public LookAndFeelList getLookAndFeels(){
		return lookAndFeels;
	}
	
	/**
	 * Writes all the properties of this application into <code>out</code>.
	 * @param out the stream to write into
	 * @throws IOException if this method can't write into the stream
	 */
	public void write( DataOutputStream out ) throws IOException{
		lookAndFeels.write( out );
		model.write( out );
		views.getNotes().write( out );
		views.getFrontend().write( out );
		frame.write( out );
	}
	
	/**
	 * Reads all the properties used for this application.
	 * @param in the stream to read from
	 * @throws IOException if the stream can't be read
	 * @see #write(DataOutputStream)
	 */
	public void read( DataInputStream in ) throws IOException{
		lookAndFeels.read( in );
		model.read( in );
		views.getNotes().read( in );
		views.getFrontend().read( in );
		frame.read( in );
	}
	
    public Collection<Component> listComponents(){
        List<Component> components = new ArrayList<Component>();
        components.add( frame );
        components.add( IconGrid.GRID );
        if( frame.getAbout( false ) != null )
        	components.add( frame.getAbout( false ) );
        
        components.add( views.getList().getComponent() );
        
        for( Dockable d : views.getFrontend().getController().getRegister().listDockables() ){
        	components.add( d.getComponent() );
        }
        return components;
    }
}
