package bibliothek.gui.dock.toolbar.measurement;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.ToolbarDockStation;

public class DropSampleMain {
	public static void main( String[] args ){
		toolbarGroupDockStation();
		// toolbarDockStation();
	}
	
	private static void toolbarGroupDockStation(){
		ToolbarGroupDockStationSample sample = new ToolbarGroupDockStationSample();
		
		ToolbarDockStation[] children = new ToolbarDockStation[4];
		for( int i = 0; i < children.length; i++ ){
			children[i] = new ToolbarDockStation();
			children[i].drop( new ComponentDockable( new JButton( "A" ) ) );
			children[i].drop( new ComponentDockable( new JButton( "B" ) ) );
			children[i].drop( new ComponentDockable( new JButton( "C" ) ) );
		}
		
		sample.getStation().drop( children[0], 0 );
		sample.getStation().drop( children[1], 1 );
		sample.getStation().drop( children[2], 1, 1 );
		sample.getStation().drop( children[3], 2 );
		start( sample );
	}
	
	private static void toolbarDockStation(){
		ToolbarDockStationSample sample = new ToolbarDockStationSample();
		sample.getStation().drop( new ComponentDockable( new JButton( "a" )));
		sample.getStation().drop( new ComponentDockable( new JButton( "b" )));
		sample.getStation().drop( new ComponentDockable( new JButton( "c" )));
		start( sample );
	}
	
	private static void start( DropSample sample ){
		JFrame frame = new JFrame( "Sample" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		final DropSamplePanel panel = new DropSamplePanel();
		
		final JCheckBoxMenuItem useSampleColors = new JCheckBoxMenuItem( "Use sample colors" );
		useSampleColors.setSelected( panel.isUseSampleColors() );
		useSampleColors.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed( ActionEvent e ){
				panel.setUseSampleColors( useSampleColors.isSelected() );
			}
		} );
		
		JMenu menu = new JMenu( "Settings" );
		JMenuBar bar = new JMenuBar();
		menu.add( useSampleColors );
		bar.add( menu );
		frame.setJMenuBar( bar );
		
		panel.setSample( sample );
		frame.add( panel, BorderLayout.CENTER );
		frame.pack();
		frame.setLocation( 20, 20 );
		frame.setVisible( true );
	}
}
