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

import javax.swing.SwingUtilities;

import bibliothek.demonstration.Monitor;
import bibliothek.demonstration.util.ComponentCollector;
import bibliothek.demonstration.util.LookAndFeelList;
import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.security.SecureDockController;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.MainFrame;
import bibliothek.notes.view.ViewManager;
import bibliothek.notes.view.actions.icon.IconGrid;
import bibliothek.notes.view.panels.NoteViewFactory;
import bibliothek.notes.view.themes.NoteBasicTheme;

public class Core implements ComponentCollector{
	private NoteModel model;
	private ViewManager views;
	
	private MainFrame frame;
	private LookAndFeelList lookAndFeels;
	
	private boolean secure;
	private Monitor monitor;

	private DockFrontend frontend;
	
	public Core( boolean secure, Monitor monitor ){
		this.secure = secure;
		this.monitor = monitor;
	}

	public boolean isSecure(){
		return secure;
	}
	
	public void startup(){
		model = new NoteModel();
		frame = new MainFrame();
		
		DockController controller;
		if( secure )
			controller = new SecureDockController();
		else
			controller = new DockController();
		
		controller.setTheme( new NoteBasicTheme() );
		controller.setSingleParentRemove( true );
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
	
	public MainFrame getFrame(){
		return frame;
	}
	
	public NoteModel getModel(){
		return model;
	}
	
	public ViewManager getViews(){
		return views;
	}
	
	public LookAndFeelList getLookAndFeels(){
		return lookAndFeels;
	}
	
	public void write( DataOutputStream out ) throws IOException{
		lookAndFeels.write( out );
		model.write( out );
		views.getNotes().write( out );
		views.getFrontend().write( out );
		frame.write( out );
	}
	
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
