package tutorial;

/*
 *    ************
 *    ** README **
 *    ************
 * 
 * This is a series of small examples introducing some basic and some more
 * advanced concepts of DockingFrames.
 * 
 * Each file contains a main-Methode. You'll need to add "dockingFramesCore.jar"
 * and "dockingFramesCommon.jar" to your classpath in order to run them. These
 * applications will run with Java 1.6.
 * 
 * This first class is not part of the tutorial, but provides an application 
 * with which you can easily start all the other classes.
 */


import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import tutorial.support.Tutorial;
import tutorial.support.TutorialPanel;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.layout.ThemeMap;

public class Tutorial_00_Readme extends JFrame{
	@SuppressWarnings("unchecked")
	private Class[] tutorials = {
			Tutorial_01_HelloWorld.class,
			Tutorial_02_TheStations.class
	};
	
	public static void main( String[] args ){
		Tutorial_00_Readme readme = new Tutorial_00_Readme();
		readme.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		readme.setBounds( 20, 20, 800, 600 );
		readme.setVisible( true );
	}
	
	private TutorialPanel currentSelection;
	
	private Map<String, String> descriptions = new HashMap<String, String>();
	private Map<String, BufferedImage> images = new HashMap<String, BufferedImage>();
	
	public Tutorial_00_Readme(){
		CControl control = new CControl( this );
		control.setTheme( ThemeMap.KEY_FLAT_THEME );
		add( control.getContentArea() );
		
		CGrid layout = new CGrid( control );
		
		currentSelection = new TutorialPanel();
		DefaultSingleCDockable currentSelectionDockable = new DefaultSingleCDockable( "selection", "Selection" );
		currentSelectionDockable.setLayout( new BorderLayout() );
		currentSelectionDockable.add( currentSelection, BorderLayout.CENTER );
		currentSelectionDockable.setCloseable( false );
		currentSelectionDockable.setExternalizable( false );
		currentSelectionDockable.setMinimizable( false );
		layout.add( 30, 0, 70, 100, currentSelectionDockable );
		
		String[] titles = new String[tutorials.length];
		for( int i = 0; i < titles.length; i++ ){
			titles[i] = getTitle( tutorials[i] );
		}
		final JList tutorialsList = new JList(titles);
		tutorialsList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		tutorialsList.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged( ListSelectionEvent e ){
				select( tutorialsList.getSelectedIndex() );
			}
		});
		tutorialsList.setSelectedIndex( 0 );
		select( 0 );
		DefaultSingleCDockable listDockable = new DefaultSingleCDockable( "list", "Tutorials" );
		listDockable.setLayout( new BorderLayout() );
		listDockable.add( new JScrollPane( tutorialsList ), BorderLayout.CENTER );
		listDockable.setCloseable( false );
		listDockable.setExternalizable( false );
		listDockable.setMinimizable( false );
		layout.add( 0, 0, 30, 100, listDockable );
		
		control.getContentArea().deploy( layout );
	}
	
	@SuppressWarnings("unchecked")
	private void select( int index ){
		if( index < 0 ){
			currentSelection.set( null, null, null, null );
		}
		else{
			Tutorial tutorial = (Tutorial)tutorials[index].getAnnotation( Tutorial.class );
			if( tutorial == null ){
				currentSelection.set( tutorials[index].getSimpleName(), null, null, tutorials[index] );
			}
			else{
				currentSelection.set( tutorial.title(), getDescription( tutorial.id() ), getImage( tutorial.id() ), tutorials[index] );
			}
		}
	}

	private String getDescription( String id ){
		String result = descriptions.get( id );
		if( result == null ){
			try{
				InputStream in = getClass().getResourceAsStream( "/data/tutorial/" + id + ".html");
				InputStreamReader reader = new InputStreamReader( in, "UTF-8" );
				StringBuilder builder = new StringBuilder();
				int next;
				while( (next = reader.read()) != -1 ){
					builder.append( (char)next );
				}
				reader.close();
				result = builder.toString();
			}
			catch( IOException e ){
				e.printStackTrace();
				result = "<html><body>" + e.getMessage() + "</body></html>";
			}
			descriptions.put( id, result );
		}
		return result;
	}
	
	private BufferedImage getImage( String id ){
		BufferedImage result = images.get( id );
		if( result == null ){
			try{
				result = ImageIO.read( getClass().getResourceAsStream( "/data/tutorial/" + id + ".png" ));
				images.put( id, result );
			}
			catch( IOException e ){
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private String getTitle( Class<?> clazz ){
		Tutorial tutorial = (Tutorial)clazz.getAnnotation( Tutorial.class );
		if( tutorial == null ){
			return clazz.getSimpleName();
		}
		else{
			return tutorial.title();
		}
	}
}