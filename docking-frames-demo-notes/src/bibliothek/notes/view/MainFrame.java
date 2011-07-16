package bibliothek.notes.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import bibliothek.demonstration.util.LookAndFeelMenu;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.notes.Core;
import bibliothek.notes.model.Note;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.menu.HelpMenu;
import bibliothek.notes.view.menu.PanelList;
import bibliothek.notes.view.menu.PreferenceItem;
import bibliothek.notes.view.menu.ThemeMenu;
import bibliothek.util.xml.XElement;

/**
 * The most important frame of this application. This frame shows the
 * menu and contains the {@link DockStation}s in which the {@link Note}s
 * are displayed.
 * @author Benjamin Sigg
 *
 */
public class MainFrame extends JFrame{
    /** the center of the application */
	private Core core;
	/** the menu for the {@link DockTheme}s */
	private ThemeMenu themes;
	/** the dialog that shows the authors and libraries of this application */
	private About about;
	
	/**
	 * Creates a new frame. The content of the frame is not created by
	 * this constructor, clients must call {@link #setup(Core)}.
	 */
	public MainFrame(){
		setTitle( "Notes - Demonstration of DockingFrames" );
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		setIconImage( ResourceSet.toImage( ResourceSet.APPLICATION_ICONS.get( "application" )));
	}
	
	/**
	 * Creates the content of this frame. These are the {@link DockStation}s
	 * and the menus. It's unspecified what happens when this method is called
	 * twice.
	 * @param core the center of the application, not <code>null</code>
	 */
	public void setup( Core core ){
		this.core = core;
		setupStations();
		setupListeners();
		setupMenu();
	}
	
	/**
	 * Creates and adds all {@link DockStation}s that are displayed on
	 * this {@link MainFrame}.
	 */
	private void setupStations(){
		Container content = getContentPane();
		
		content.setLayout( new BorderLayout() );
		
		content.add( core.getViews().getSplit(), BorderLayout.CENTER );
		content.add( core.getViews().getNorth().getComponent(), BorderLayout.NORTH );
		content.add( core.getViews().getSouth().getComponent(), BorderLayout.SOUTH );
		content.add( core.getViews().getEast().getComponent(), BorderLayout.EAST );
		content.add( core.getViews().getWest().getComponent(), BorderLayout.WEST );
	}
	
	/**
	 * Creates and adds all observers that are needed by this {@link MainFrame}.
	 */
	private void setupListeners(){
		addWindowListener( new WindowAdapter(){
			@Override
			public void windowClosing( WindowEvent e ){
				core.shutdown();
			}
		});
	}
	
	/**
	 * Creates and adds all menus and the menubar.
	 */
	private void setupMenu(){
		JMenuBar menubar = new JMenuBar();
		menubar.add( new PanelList( core.getViews(), core.getModel() ));
		
		JMenu theme = new JMenu( "Window" );
		themes = new ThemeMenu( core.getViews().getFrontend() );
		theme.add( new LookAndFeelMenu( this, core.getLookAndFeels() ) );
		theme.add( themes );
		theme.add( new PreferenceItem( this, core.getPreferences() ));
		menubar.add( theme );
		menubar.add( new HelpMenu( this ) );
		
		setJMenuBar( menubar );
	}

	/**
	 * Writes location, extended-state and theme of this frame.
	 * @param out the stream to write into
     * @throws IOException if the method can't write into <code>out</code>
     */
    public void write( DataOutputStream out ) throws IOException{
        out.writeInt( getExtendedState() );
        setExtendedState( NORMAL );
        out.writeInt( getX() );
        out.writeInt( getY() );
        out.writeInt( getWidth() );
        out.writeInt( getHeight() );
        
        themes.write( out );
    }
	
	/**
	 * Reads location, extended-state and theme of this frame.
	 * @param in the stream to read from
	 * @throws IOException if the stream can't be read
	 */
	public void read( DataInputStream in ) throws IOException{
		int state = in.readInt();
		setBounds( in.readInt(), in.readInt(), in.readInt(), in.readInt() );
		setExtendedState( state );

		themes.read( in );
	}

	/**
	 * Writes location, extended-state and theme of this frame.
	 * @param element the xml-element to write into
	 */
	public void writeXML( XElement element ){
	    element.addElement( "extended" ).setInt( getExtendedState() );
        setExtendedState( NORMAL );
        XElement xbounds = element.addElement( "bounds" );
        xbounds.addInt( "x", getX() );
        xbounds.addInt( "y", getY() );
        xbounds.addInt( "width", getWidth() );
        xbounds.addInt( "height", getHeight() );
                
        themes.writeXML( element.addElement( "theme" ) );
    }
	
    /**
     * Reads location, extended-state and theme of this frame.
     * @param element the xml-element to read from
     */
    public void readXML( XElement element ){
        int state = element.getElement( "extended" ).getInt();
        XElement xbounds = element.getElement( "bounds" );
        setBounds( xbounds.getInt( "x" ), xbounds.getInt( "y" ), xbounds.getInt( "width" ), xbounds.getInt( "height" ) );
        setExtendedState( state );

        themes.readXML( element.getElement( "theme" ) );
    }
	
	/**
	 * Gets the about-dialog of this application.
	 * @param lazy <code>true</code> if the dialog should be newly created if
	 * it is <code>null</code>, or <code>false</code> if the current value
	 * of the property should be returned.
	 * @return the dialog, maybe <code>null</code> but only if <code>lazy</code>
	 * was <code>false</code>
	 */
	public About getAbout( boolean lazy ){
		if( about == null && lazy ){
			about = new About( this );
			about.pack();
			about.setLocationRelativeTo( this );
		}
		return about;
	}
}
