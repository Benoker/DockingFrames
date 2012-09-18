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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.station.screen.ScreenDropSizeStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategyListener;
import bibliothek.gui.dock.themes.basic.BasicSpanFactory;
import bibliothek.gui.dock.wizard.WizardSplitDockStation;
import bibliothek.gui.dock.wizard.WizardSplitDockStation.Side;
import bibliothek.util.Path;

public class WizardSplitDockStationTest {
	public static void main( String[] args ){
		JFrame frame = new JFrame( "Test" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		JPanel borderPanel= new JPanel(new BorderLayout());
		frame.add(borderPanel);

		DockController controller = new DockController();
		controller.setRootWindow( frame );
		
		controller.getProperties().set( DockTheme.SPAN_FACTORY, new BasicSpanFactory( 500, 20) );
		
		controller.setTheme( new EclipseTheme() );
		
		controller.getProperties().set( ScreenDockStation.DROP_SIZE_STRATEGY, ScreenDropSizeStrategy.PREFERRED_SIZE );

		controller.getProperties().set( PlaceholderStrategy.PLACEHOLDER_STRATEGY, new PlaceholderStrategy(){
			@Override
			public void uninstall( DockStation station ){
				// ignore
			}
			
			@Override
			public void removeListener( PlaceholderStrategyListener listener ){
				// ignore				
			}
			
			@Override
			public boolean isValidPlaceholder( Path placeholder ){
				return true;
			}
			
			@Override
			public void install( DockStation station ){
				// ignore
			}
			
			@Override
			public Path getPlaceholderFor( Dockable dockable ){
				if( dockable instanceof DefaultDockable ){
					return new Path( "test", dockable.getTitleText() );
				}
				return null;
			}
			
			@Override
			public void addListener( PlaceholderStrategyListener listener ){
				// ignore	
			}
		} );
		
		WizardSplitDockStation station = new WizardSplitDockStation( Side.RIGHT );
		WizardSplitDockStation stationBottom = new WizardSplitDockStation( Side.BOTTOM );
		WizardSplitDockStation stationTop = new WizardSplitDockStation( Side.TOP );
		WizardSplitDockStation stationLeft = new WizardSplitDockStation( Side.LEFT );
		
		station.setResizeOnRemove( true );
		
		controller.add( station );
		controller.add( stationBottom );
		controller.add( stationTop );
		controller.add( stationLeft );
		borderPanel.add( new JScrollPane( station.getComponent() ), BorderLayout.EAST);
		borderPanel.add( new JScrollPane( stationLeft.getComponent() ), BorderLayout.WEST);
		borderPanel.add( new JScrollPane( stationTop.getComponent() ), BorderLayout.NORTH);
		borderPanel.add( stationBottom.getComponent(), BorderLayout.SOUTH);

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

		frame.setBounds( 20, 20, 400, 400 );
		screen.setShowing( true );
		frame.setVisible( true );
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