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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.station.screen.ScreenDropSizeStrategy;
import bibliothek.gui.dock.wizard.WizardSplitDockStation;
import bibliothek.gui.dock.wizard.WizardSplitDockStation.Side;

public class TestWizardSplitDockStationSide {
	public static void main( String[] args ){
		final JFrame frame = new JFrame( "Test" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		JPanel borderPanel= new JPanel(new BorderLayout());
		frame.add(borderPanel);

		DockController controller = new DockController();
		controller.setRootWindow( frame );
		
		controller.getProperties().set( ScreenDockStation.DROP_SIZE_STRATEGY, ScreenDropSizeStrategy.PREFERRED_SIZE );
		
		final WizardSplitDockStation station = new WizardSplitDockStation( Side.RIGHT );
		
		controller.add( station );
		JScrollPane scroll = new JScrollPane( station.getComponent() );
		scroll.setBorder( null );
		borderPanel.add( scroll, BorderLayout.EAST);

		ScreenDockStation screen = new ScreenDockStation( frame );
		controller.add( screen );

		screen.drop( create( "A" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( "B" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( "C" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( "D" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( createPanel( "300, 300",  300, 350), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( createPanel( "400, 600 ", 400, 600 ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( "G" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( "Very long long long long button" ), new ScreenDockProperty( 420, 20, 400, 400 ) );

		JMenu menu = new JMenu( "Sides" );
		for( Side side : Side.values() ){
			JMenuItem item = new JMenuItem( side.name().toLowerCase() );
			final Side itemSide = side;
			item.addActionListener( new ActionListener(){
				@Override
				public void actionPerformed( ActionEvent e ){
					setSide( station, frame, itemSide );
				}
			});
			menu.add( item );
		}
		JMenuBar bar = new JMenuBar();
		bar.add( menu );
		frame.setJMenuBar( bar );
		
		frame.setBounds( 20, 20, 400, 400 );
		screen.setShowing( true );
		frame.setVisible( true );
	}
	
	private static void setSide( WizardSplitDockStation station, JFrame frame, Side side ){
		switch( side ){
			case BOTTOM:
				frame.add( new JScrollPane( station.getComponent() ), BorderLayout.SOUTH );
				break;
			case TOP:
				frame.add( new JScrollPane( station.getComponent() ), BorderLayout.NORTH );
				break;
			case LEFT:
				frame.add( new JScrollPane( station.getComponent() ), BorderLayout.WEST );
				break;
			case RIGHT:
				frame.add( new JScrollPane( station.getComponent() ), BorderLayout.EAST );
				break;
		}
		station.setSide( side );
	}
	
	private static Dockable createPanel( String title, int width, int height){
		DefaultDockable dockable = new DefaultDockable( title );
		dockable.setLayout( new BorderLayout() );
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(width, height));
		panel.setMinimumSize( new Dimension(width, height));
		dockable.add( panel );
		return dockable;
	}

	private static Dockable create( String title ){
		DefaultDockable dockable = new DefaultDockable( title );
		dockable.setLayout( new BorderLayout() );
		JButton button = new JButton( title );
		dockable.add( button );
		return dockable;
	}
}
