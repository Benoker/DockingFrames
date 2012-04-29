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

import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.station.split.DockableSplitDockTree;
import bibliothek.gui.dock.station.split.SplitDockPlaceholderProperty;
import bibliothek.gui.dock.station.split.SplitDockTree.Key;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategyListener;
import bibliothek.gui.dock.wizard.WizardSplitDockStation;
import bibliothek.gui.dock.wizard.WizardSplitDockStation.Side;
import bibliothek.util.Path;

public class TestWizardSplitDockStationPlaceholder {
	public static void main( String[] args ){
		JFrame frame = new JFrame( "Test" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		JPanel borderPanel = new JPanel( new BorderLayout() );
		frame.add( borderPanel );

		final DockFrontend frontend = new DockFrontend( frame );
		frontend.getController().getProperties().set( PlaceholderStrategy.PLACEHOLDER_STRATEGY, new PlaceholderStrategy(){

			@Override
			public void uninstall( DockStation station ){
				// TODO Auto-generated method stub

			}

			@Override
			public void removeListener( PlaceholderStrategyListener listener ){
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isValidPlaceholder( Path placeholder ){
				return true;
			}

			@Override
			public void install( DockStation station ){
				// TODO Auto-generated method stub

			}

			@Override
			public Path getPlaceholderFor( Dockable dockable ){
				if( dockable instanceof DefaultDockable ) {
					return new Path( "test", dockable.getTitleText() );
				}
				return null;
			}

			@Override
			public void addListener( PlaceholderStrategyListener listener ){
				// TODO Auto-generated method stub

			}
		} );

		WizardSplitDockStation station = new WizardSplitDockStation( Side.RIGHT );

		frontend.addRoot( "right", station );
		borderPanel.add( new JScrollPane( station.getComponent() ), BorderLayout.EAST );

		DockableSplitDockTree tree = new DockableSplitDockTree();
		DockableSplitDockTree.Key a1 = tree.put( create( "A1" ) );
		DockableSplitDockTree.Key a2 = tree.put( create( "A2" ) );
		DockableSplitDockTree.Key a3 = tree.put( create( "A3" ) );
		DockableSplitDockTree.Key b1 = tree.put( create( "B1" ) );
		DockableSplitDockTree.Key b2 = tree.put( create( "B2" ) );
		DockableSplitDockTree.Key a2b = tree.put( new Path[]{ new Path( "test", "A2b" ) }, null );

		DockableSplitDockTree.Key col1 = tree.vertical( tree.vertical( a1, tree.horizontal( a2b, a2 ) ), a3 );
		DockableSplitDockTree.Key col2 = tree.vertical( b1, b2 );
		tree.root( tree.horizontal( col1, col2 ) );

		station.dropTree( tree );

		station.drop( create( "A2b" ), new SplitDockPlaceholderProperty( new Path( "test", "A2b" ) ) );

		frame.setBounds( 20, 20, 400, 400 );

		frame.setVisible( true );
	}

	private static Dockable createPanel( String title, int width, int height ){
		DefaultDockable dockable = new DefaultDockable( title );
		dockable.setLayout( new BorderLayout() );
		JPanel panel = new JPanel();
		panel.setPreferredSize( new Dimension( width, height ) );
		panel.setMinimumSize( new Dimension( width, height ) );
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