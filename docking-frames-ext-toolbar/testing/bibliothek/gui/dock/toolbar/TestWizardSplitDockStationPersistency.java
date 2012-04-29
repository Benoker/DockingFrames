/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.toolbar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.wizard.WizardSplitDockStation;
import bibliothek.gui.dock.wizard.WizardSplitDockStation.Side;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;

public class TestWizardSplitDockStationPersistency {
	public static void main( String[] args ){
		JFrame frame = new JFrame( "Test" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		JPanel borderPanel= new JPanel(new BorderLayout());
		frame.add(borderPanel);

		final DockFrontend frontend = new DockFrontend( frame );
		WizardSplitDockStation station = new WizardSplitDockStation( Side.RIGHT );
		
		frontend.addRoot( "right", station );
		borderPanel.add( new JScrollPane( station.getComponent() ), BorderLayout.EAST);

		ScreenDockStation screen = new ScreenDockStation( frame );
		frontend.addRoot( "screen", screen );

		screen.drop( create( frontend, "A" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( frontend, "B" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( frontend, "C" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( frontend, "D" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( createPanel( frontend, "300, 300",  300, 350), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( createPanel( frontend, "400, 600 ", 400, 600 ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( frontend, "G" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( frontend, "Very long long long long button" ), new ScreenDockProperty( 420, 20, 400, 400 ) );

		frame.setBounds( 20, 20, 400, 400 );
		screen.setShowing( true );
		
		JMenuItem load = new JMenuItem( "Load" );
		load.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed( ActionEvent e ){
				try{
					InputStream in = new BufferedInputStream( new FileInputStream( "wizard.xml" ) );
					XElement element = XIO.readUTF( in );
					in.close();
					frontend.readXML( element );
				}
				catch( IOException ex ){
					ex.printStackTrace();
				}
			}
		} );
		JMenuItem save = new JMenuItem( "Save" );
		save.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed( ActionEvent e ){
				try{
					XElement element = new XElement( "layout" );
					frontend.writeXML( element );
					OutputStream out = new BufferedOutputStream( new FileOutputStream( "wizard.xml" ) );
					XIO.writeUTF( element, out );
					out.close();
				}
				catch( IOException ex ){
					ex.printStackTrace();
				}
			}
		} );
		JMenu menu = new JMenu( "Layout" );
		menu.add( load );
		menu.add( save );
		
		JMenuBar bar = new JMenuBar();
		bar.add( menu );
		frame.setJMenuBar( bar );
		
		frame.setVisible( true );
	}
	
	private static Dockable createPanel( DockFrontend frontend, String title, int width, int height){
		DefaultDockable dockable = new DefaultDockable( title );
		dockable.setLayout( new BorderLayout() );
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(width, height));
		panel.setMinimumSize( new Dimension(width, height));
		dockable.add( panel );
		frontend.addDockable( title, dockable );
		return dockable;
	}

	private static Dockable create( DockFrontend frontend, String title ){
		DefaultDockable dockable = new DefaultDockable( title );
		dockable.setLayout( new BorderLayout() );
		JButton button = new JButton( title );
		dockable.add( button );
		frontend.addDockable( title, dockable );
		return dockable;
	}

}