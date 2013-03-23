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
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import tutorial.support.CodePanel;
import tutorial.support.CopyCodeAction;
import tutorial.support.TutorialPanel;
import tutorial.support.sets.RootSet;
import tutorial.support.sets.TutorialTreeModel;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.theme.ThemeMap;

public class TutorialMain extends JFrame{
	@SuppressWarnings("deprecation")
	public static void main( String[] args ) throws InstantiationException, IllegalAccessException{
		DockController.disableCoreWarning();
		
		TutorialMain readme = new TutorialMain();
		readme.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		readme.setBounds( 20, 20, 800, 600 );
		readme.setVisible( true );
	}
	
	private TutorialPanel currentSelection;
	private CodePanel currentCode;
	
	public TutorialMain() throws InstantiationException, IllegalAccessException{
		setTitle( "DockingFrames - Examples" );
		
		CControl control = new CControl( this );
		control.setTheme( ThemeMap.KEY_FLAT_THEME );
		add( control.getContentArea() );
		
		CGrid layout = new CGrid( control );
		
		currentSelection = new TutorialPanel();
		DefaultSingleCDockable currentSelectionDockable = new DefaultSingleCDockable( "selection", "Selection" );
		currentSelectionDockable.setLayout( new BorderLayout() );
		currentSelectionDockable.add( currentSelection, BorderLayout.CENTER );
		currentSelectionDockable.setCloseable( false );
		layout.add( 30, 0, 70, 100, currentSelectionDockable );
		
		currentCode = new CodePanel();
		DefaultSingleCDockable currentCodeDockable = new DefaultSingleCDockable( "code", "Code" );
		currentCodeDockable.setLayout( new BorderLayout() );
		currentCodeDockable.add( currentCode.toComponent(), BorderLayout.CENTER );
		currentCodeDockable.setCloseable( false );
		if( !control.getController().isRestrictedEnvironment() ){
			currentCodeDockable.addAction( new CopyCodeAction( currentCode ));
		}
		layout.add( 30, 0, 70, 100, currentCodeDockable );
		layout.select( 30, 0, 70, 100, currentSelectionDockable ); 
	
		final JTree tutorialsTree = new JTree( new TutorialTreeModel( RootSet.class, loadExtensions() ));
		tutorialsTree.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		tutorialsTree.setShowsRootHandles( true );
		tutorialsTree.setRootVisible( false );
		tutorialsTree.addTreeSelectionListener( new TreeSelectionListener(){
			public void valueChanged( TreeSelectionEvent e ){
				TreePath path = tutorialsTree.getSelectionPath();
				if( path == null ){
					select( null );
				}
				else{
					select( (TutorialTreeModel.Node)path.getLastPathComponent() );
				}
			}
		});
		tutorialsTree.setSelectionRow( 0 );
		DefaultSingleCDockable listDockable = new DefaultSingleCDockable( "list", "Tutorials" );
		listDockable.setLayout( new BorderLayout() );
		listDockable.add( new JScrollPane( tutorialsTree ), BorderLayout.CENTER );
		listDockable.setCloseable( false );
		layout.add( 0, 0, 30, 100, listDockable );
		
		control.getContentArea().deploy( layout );
	}
	
	private Set<TutorialExtension> loadExtensions(){
		Set<TutorialExtension> set = new HashSet<TutorialExtension>();
		
		try{
			Class<?> clazz = Class.forName( "tutorial.toolbar.TutorialToolbarExtension" );
			set.add( (TutorialExtension)clazz.newInstance() );
		}
		catch( Exception e ){
			// ignore
		}
		
		return set;
	}
	
	private void select( TutorialTreeModel.Node node ){
		if( node == null ){
			currentSelection.set( null, null, null, null );
		}
		else{
			try{
				currentSelection.set( node.getTitle(), node.getDescription(), node.getImage(), node.getMainClass() );
				currentCode.setCode( node.getCode() );
			}
			catch( IOException e ){
				e.printStackTrace();
				currentCode.setCode( "" );
			}
		}
	}

}