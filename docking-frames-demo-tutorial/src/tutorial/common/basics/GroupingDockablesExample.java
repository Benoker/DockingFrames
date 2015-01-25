/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2015 Benjamin Sigg
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
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package tutorial.common.basics;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.EmptyMultipleCDockableFactory;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.grouping.PlaceholderGrouping;
import bibliothek.gui.dock.common.perspective.CGridPerspective;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.util.Colors;
import bibliothek.util.Path;

/*
 * This example shows how to:
 * 1. use the perspective API to put some "placeholders" into the layout
 * 2. use the grouping mechanism to put real dockables at the location of the placeholders
 * 
 * It also demonstrates how dockables that are associated with the same group tend to stick together.
 */
@Tutorial(title = "Grouping Dockables", id="GroupingDockables")
public class GroupingDockablesExample {
	public static void main( String[] args ) {
		/* We need a frame to show stuff... */
		JTutorialFrame frame = new JTutorialFrame( GroupingDockablesExample.class );
		
		/* ... and we need a CControl to place stuff. */
		CControl control = new CControl( frame );
		
		/* The factory is just here because we want to use MultipleCDockables in this example. */
		MultipleCDockableFactory<DefaultMultipleCDockable, ?> factory = new EmptyMultipleCDockableFactory<DefaultMultipleCDockable>() {
			@Override
			public DefaultMultipleCDockable createDockable() {
				/* Returning null, because this method is not required in this example */
				return null;
			}
		};
		control.addMultipleDockableFactory( "factory", factory );
		
		/* Adding a CContentArea to the left, and some buttons to the right side of the frame. */
		frame.setLayout( new GridBagLayout() );
		frame.add( control.getContentArea(), new GridBagConstraints( 0, 0, 1, 1, 100.0, 100.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ));
		frame.add( buildActions( control, factory ), new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
		
		/* After setting up the JComponents, we mark some locations with placeholders. */
		initialLayout( control );
		
		frame.setVisible( true );
	}
	
	private static void initialLayout( CControl control ){
		CPerspective layout = control.getPerspectives().createEmptyPerspective();
		
		/* Adding a placeholder called "custom.red" to the right "minimized area" of the frame */
		layout.getContentArea().getEast().addPlaceholder( new Path( "custom", "red" ));
		
		/* One of the many locations of the "external area" is associated with a placeholder. */
		layout.getScreenStation().addPlaceholder( new Path( "custom", "red" ), 80, 80, 500, 200 );
		
		/* Building a grid (3 rows, 2 columns) of placeholder. */
		CGridPerspective center = layout.getContentArea().getCenter();
		center.gridPlaceholder( 0, 0, 1, 1, new Path( "custom", "red" ));
		center.gridPlaceholder( 1, 0, 1, 1, new Path( "custom", "green" ));
		center.gridPlaceholder( 0, 1, 1, 1, new Path( "custom", "blue" ));
		center.gridPlaceholder( 1, 1, 1, 1, new Path( "custom", "yellow" ));
		center.gridPlaceholder( 0, 2, 1, 1, new Path( "custom", "magenta" ));
		center.gridPlaceholder( 1, 2, 1, 1, new Path( "custom", "cyan" ));
		
		/* Finally we tell the CControl that the perspective we just set up should be displayed. */
		control.getPerspectives().setPerspective( layout, true );
	}
	
	private static JComponent buildActions( CControl control, MultipleCDockableFactory<DefaultMultipleCDockable, ?> factory ){
		/* Just creating a planel with some buttons. Each button adds Dockables for another group. */
		JPanel panel = new JPanel( new GridLayout( 6, 1 ) );
		panel.add( buildAction( "Red", new AddDockableAction( control, factory, Color.RED, new Path( "custom", "red" ))));
		panel.add( buildAction( "Green", new AddDockableAction( control, factory, Color.GREEN, new Path( "custom", "green" ))));
		panel.add( buildAction( "Blue", new AddDockableAction( control, factory, Color.BLUE, new Path( "custom", "blue" ))));
		panel.add( buildAction( "Yellow", new AddDockableAction( control, factory, Color.YELLOW, new Path( "custom", "yellow" ))));
		panel.add( buildAction( "Magenta", new AddDockableAction( control, factory, Color.MAGENTA, new Path( "custom", "magenta" ))));
		panel.add( buildAction( "Cyan", new AddDockableAction( control, factory, Color.CYAN, new Path( "custom", "cyan" ))));
		return panel;
	}
	
	private static JComponent buildAction( String name, AddDockableAction action ){
		JButton button = new JButton( "Add " + name );
		button.addActionListener( action );
		return button;
	}
	
	private static class AddDockableAction implements ActionListener{
		private int count = 0;
		
		private CControl control;
		private MultipleCDockableFactory<DefaultMultipleCDockable, ?> factory; 
		private Color color;
		private Path placeholder;
		
		public AddDockableAction( CControl control, MultipleCDockableFactory<DefaultMultipleCDockable, ?> factory, Color color, Path placeholder ){
			this.control = control;
			this.factory = factory;
			this.color = color;
			this.placeholder = placeholder;
		}
		
		public void actionPerformed( ActionEvent e ) {
			/* Defining a new dockable, we are setting the title and some colors to easily keep track of its position. 
			 * Each color represents one group of Dockables. */
			DefaultMultipleCDockable dockable = new DefaultMultipleCDockable( factory );
			dockable.setTitleText( String.valueOf( ++count ) );
			dockable.setCloseable( true );
			dockable.setRemoveOnClose( true );
			dockable.getColors().setColor( ColorMap.COLOR_KEY_TITLE_BACKGROUND, Colors.brighter( color, 0.75 ) );
			dockable.getColors().setColor( ColorMap.COLOR_KEY_TITLE_BACKGROUND_FOCUSED, color );
			
			/* We define the group of "dockable". It will now show up at the location where "placeholder" was inserted into the layout,
			 * or at the location of other dockables that have the same group. */ 
			dockable.setGrouping( new PlaceholderGrouping( control, placeholder ) );
			
			/* Without defining the location, we make the dockable visible. It will always show up in the "normalized mode", and
			 * it will automatically be stacked onto another dockable that is associated with the same group. */
			control.addDockable( dockable );
			dockable.setVisible( true );
		}
	}
}
