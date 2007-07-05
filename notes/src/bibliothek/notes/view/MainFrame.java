package bibliothek.notes.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import bibliothek.gui.dock.security.GlassedPane;
import bibliothek.gui.dock.security.SecureDockController;
import bibliothek.notes.Core;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.menu.HelpMenu;
import bibliothek.notes.view.menu.LookAndFeelMenu;
import bibliothek.notes.view.menu.PanelList;
import bibliothek.notes.view.menu.ThemeMenu;

public class MainFrame extends JFrame{
	private Core core;
	private ThemeMenu themes;
	private About about;
	
	public MainFrame(){
		setTitle( "Notes - Demo for DockingFrames" );
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		setIconImage( ResourceSet.toImage( ResourceSet.APPLICATION_ICONS.get( "application" )));
	}
	
	public void setup( Core core ){
		this.core = core;
		setupStations();
		setupListeners();
		setupMenu();
	}
	
	private void setupStations(){
		Container content;
		if( core.isSecure() ){
			GlassedPane pane = new GlassedPane();
			SecureDockController controller = (SecureDockController)core.getViews().getFrontend().getController();
			controller.getFocusObserver().addGlassPane( pane );
			content = pane.getContentPane();
			
			setLayout( new GridLayout( 1, 1 ) );
			add( pane );
		}
		else
			content = getContentPane();
		
		content.setLayout( new BorderLayout() );
		
		content.add( core.getViews().getSplit(), BorderLayout.CENTER );
		content.add( core.getViews().getNorth().getComponent(), BorderLayout.NORTH );
		content.add( core.getViews().getSouth().getComponent(), BorderLayout.SOUTH );
		content.add( core.getViews().getEast().getComponent(), BorderLayout.EAST );
		content.add( core.getViews().getWest().getComponent(), BorderLayout.WEST );
	}
	
	private void setupListeners(){
		addWindowListener( new WindowAdapter(){
			@Override
			public void windowClosing( WindowEvent e ){
				core.shutdown();
			}
		});
	}
	
	private void setupMenu(){
		JMenuBar menubar = new JMenuBar();
		menubar.add( new PanelList( core.getViews(), core.getModel() ));
		
		JMenu theme = new JMenu( "Themes" );
		themes = new ThemeMenu( core.getViews().getFrontend() );
		theme.add( new LookAndFeelMenu( core.getLookAndFeels() ) );
		theme.add( themes );
		menubar.add( theme );
		menubar.add( new HelpMenu( this ) );
		
		setJMenuBar( menubar );
	}
	
	public void read( DataInputStream in ) throws IOException{
		int state = in.readInt();
		setBounds( in.readInt(), in.readInt(), in.readInt(), in.readInt() );
		setExtendedState( state );
		
		themes.read( in );
	}
	
	public void write( DataOutputStream out ) throws IOException{
		out.writeInt( getExtendedState() );
		setExtendedState( NORMAL );
		out.writeInt( getX() );
		out.writeInt( getY() );
		out.writeInt( getWidth() );
		out.writeInt( getHeight() );
		
		themes.write( out );
	}
	
	public About getAbout( boolean lazy ){
		if( about == null && lazy ){
			about = new About( this );
			about.pack();
			about.setLocationRelativeTo( this );
		}
		return about;
	}
}
