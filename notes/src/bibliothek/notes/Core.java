package bibliothek.notes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.security.SecureDockController;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.MainFrame;
import bibliothek.notes.view.ViewManager;
import bibliothek.notes.view.menu.LookAndFeelList;
import bibliothek.notes.view.panels.NoteViewFactory;
import bibliothek.notes.view.themes.NoteBasicTheme;

public class Core {
	private NoteModel model;
	private ViewManager views;
	
	private MainFrame frame;
	private LookAndFeelList lookAndFeels;
	
	private boolean secure;
	
	public Core( boolean secure ){
		this.secure = secure;
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
		DockFrontend frontend = new DockFrontend( controller, frame );
		views = new ViewManager( frontend, frame, secure, model );
		frontend.registerFactory( new NoteViewFactory( views.getNotes(), model ));
		
		lookAndFeels = new LookAndFeelList( frame, views );
		
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
				DataInputStream in = new DataInputStream( ResourceSet.openStream( "/data/backup.properties" ));
				read( in );
				in.close();
			}
		}
		catch( IOException ex ){
			ex.printStackTrace();
		}
		finally{
			frame.setVisible( true );
			views.getScreen().setShowing( true );
		}
	}
	
	public void shutdown(){
		try{
			frame.setVisible( false );
			views.getScreen().setShowing( false );
		
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
			System.exit( 0 );
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
}
