package bibliothek.notes;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;

import bibliothek.demonstration.Monitor;
import bibliothek.extension.gui.dock.DockingFramesPreference;
import bibliothek.extension.gui.dock.preference.PreferenceStorage;
import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.RectGradientPainter;
import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.station.flap.button.ButtonContent;
import bibliothek.gui.dock.support.lookandfeel.ComponentCollector;
import bibliothek.gui.dock.support.lookandfeel.LookAndFeelList;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.MainFrame;
import bibliothek.notes.view.ViewManager;
import bibliothek.notes.view.actions.icon.IconGrid;
import bibliothek.notes.view.panels.NoteViewFactory;
import bibliothek.notes.view.themes.NoteBasicTheme;
import bibliothek.util.xml.XElement;

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
	
	/** the available preferences */
	private PreferenceTreeModel preferences;
	
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
		
		DockController.disableCoreWarning();
		final DockController controller = new DockController();
		controller.setRestrictedEnvironment( secure );
		
		controller.setTheme( new NoteBasicTheme() );
		controller.getProperties().set( EclipseTheme.PAINT_ICONS_WHEN_DESELECTED, true );
		controller.getProperties().set( EclipseTheme.TAB_PAINTER, RectGradientPainter.FACTORY );
		controller.getProperties().set( FlapDockStation.BUTTON_CONTENT, ButtonContent.ICON_AND_TEXT_ONLY );
		
		frontend = new DockFrontend( controller, frame );
		preferences = new DockingFramesPreference( controller );
		views = new ViewManager( frontend, frame, secure, model );
		frontend.registerFactory( new NoteViewFactory( views.getNotes(), model ));
		
		if( monitor == null ){
			lookAndFeels = LookAndFeelList.getDefaultList();
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
				in.close(); // */ 
			    /*
			    File file = new File( "properties.xml" );
			    InputStreamReader in = new InputStreamReader( new BufferedInputStream( new FileInputStream( file )), "UTF-8" );
			    XElement element = XIO.read( in );
			    in.close();
			    readXML( element );
			    
			    // */
			}
			else{
				DataInputStream in = new DataInputStream( ResourceSet.openStream( "/data/bibliothek/notes/notes.properties" ));
				read( in );
				in.close();
			    
                /*InputStreamReader in = new InputStreamReader( ResourceSet.openStream( "/data/bibliothek/notes/properties.xml" ), "UTF-8" );
                XElement element = XIO.read( in );
                in.close();
                readXML( element );*/
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
			frame.dispose();
			views.getScreen().setShowing( false );
			
			if( !secure ){
				try{
					File file = new File( "notes.properties" );
					DataOutputStream out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream( file )));
					write( out );
					out.flush();
					out.close();
				    /*
				    XElement element = new XElement( "properties" );
                    writeXML( element );
				    
				    File file = new File( "properties.xml" );
                    OutputStream out = new BufferedOutputStream( new FileOutputStream( file ));
                    OutputStreamWriter writer = new OutputStreamWriter( out, "UTF-8" );
                    XIO.write( element, writer );
                    writer.close();*/
				}
				catch( IOException ex ){
					ex.printStackTrace();
				}
				catch( Throwable t ){
					t.printStackTrace();
				}
			}
			
			frontend.getController().kill();
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
	 * Gets the user made settings of this application.
	 * @return the preferences
	 */
	public PreferenceTreeModel getPreferences() {
		return preferences;
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
		
		preferences.read();
		PreferenceStorage.write( preferences, out );
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
		
		PreferenceStorage.read( preferences, in );
		preferences.write();
	}

	/**
	 * Writes all the properties of this application into <code>out</code>.
	 * @param element the xml-element to write into
	 */
	public void writeXML( XElement element ) throws IOException{
	    lookAndFeels.writeXML( element.addElement( "lookandfeel" ) );
	    model.writeXML( element.addElement( "model" ) );
	    views.getNotes().writeXML( element.addElement( "notes" ) );
	    views.getFrontend().writeXML( element.addElement( "frontend" ) );
	    frame.writeXML( element.addElement( "frame" ) );
	    
	    preferences.read();
	    PreferenceStorage.writeXML( preferences, element.addElement( "preferences" ));
	}

	/**
	 * Reads all the properties used for this application.
	 * @param element the xml-element to read from
	 */
	public void readXML( XElement element ){
	    lookAndFeels.readXML( element.getElement( "lookandfeel" ) );
	    model.readXML( element.getElement( "model" ) );
	    views.getNotes().readXML( element.getElement( "notes" ) );
	    views.getFrontend().readXML( element.getElement( "frontend" ) );
	    frame.readXML( element.getElement( "frame" ) );
	    
	    PreferenceStorage.readXML( preferences, element.getElement( "preferences" ));
	    preferences.write();
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
