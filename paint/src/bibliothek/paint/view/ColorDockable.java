/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.paint.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.paint.util.Resources;

/**
 * A Dockable that lets the user choose the color for new
 * {@link bibliothek.paint.model.Shape}s.
 * @author Benjamin Sigg
 *
 */
public class ColorDockable extends DefaultSingleCDockable{
    /** the manager of all {@link CDockable}s, used to forward a newly selected color */
	private ViewManager manager;
	/** the big button which will open a {@link JColorChooser} when pressed */
	private JButton colorButton;
	
	/**
	 * Creates a new dockable
	 * @param manager the manager which is used to read and write the selected color
	 */
	public ColorDockable( ViewManager manager ){
		super( "ColorDockable" );
		this.manager = manager;
		
		setCloseable( true );
		setMinimizable( true );
		setExternalizable( true );
		setMaximizable( true );
		setTitleText( "Color" );
		setTitleIcon( Resources.getIcon( "dockable.color" ) );
		setResizeLocked( true );
		
		colorButton = new JButton( new ColorIcon( 48, 48, manager.getColor() ));
		colorButton.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				Color color = JColorChooser.showDialog( getContentPane(), "Color", ColorDockable.this.manager.getColor() );
				if( color != null ){
					colorButton.setIcon( new ColorIcon( 48, 48, color ) );
					ColorDockable.this.manager.setColor( color );
				}
			}
		});
		
		JTabbedPane pane = new JTabbedPane();
		
		for( int b = 0; b < 3; b++ ){
			JPanel buttons = new JPanel( new FlowLayout());
			String title = null;
			switch( b ){
				case 0: title = "Dark"; break;
				case 1: title = "Even"; break;
				case 2: title = "Bright"; break;
			}
			pane.add( title, buttons );
			for( int h = 0; h < 20; h++ ){
				Color color = Color.getHSBColor( h / 20f, 1f, (b+1f)/3f );
				buttons.add( createButton( color ) );
			}
		}
		
		Container content = getContentPane();
		content.setLayout( new LayoutManager(){
			public void addLayoutComponent( String name, Component comp ){
				// ignore
			}

			public void layoutContainer( Container parent ){
				if( parent.getComponentCount() == 2 ){
					Component left = parent.getComponent( 0 );
					Component right = parent.getComponent( 1 );
					
					Dimension preferred = left.getPreferredSize();
					left.setBounds( 0, 0, preferred.width, parent.getHeight() );
					right.setBounds( preferred.width, 0, 
							Math.max( 1, parent.getWidth() - preferred.width ),
							parent.getHeight() );
				}
			}

			public Dimension minimumLayoutSize( Container parent ){
				return new Dimension( 0, 0 );
			}

			public Dimension preferredLayoutSize( Container parent ){
				return new Dimension( 0, 0 );
			}

			public void removeLayoutComponent( Component comp ){
				// ignore
			}
			
		});
		
		content.add( colorButton );
		content.add( pane );
	}
	
	/**
	 * Creates a button which will change the color of the main-button of
	 * this dockable, and the color used to paint new {@link bibliothek.paint.model.Shape}s
	 * to <code>color</code>.
	 * @param color the color this button represents
	 * @return a new button
	 */
	private JButton createButton( final Color color ){
		JButton button = new JButton( new ColorIcon( 32, 32, color ) );
		button.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				colorButton.setIcon( new ColorIcon( 48, 48, color ) );
				manager.setColor( color );
			}
		});
		return button;
	}
	
	/**
	 * An icon which is only filled by a rectangle of one color.
	 * @author Benjamin Sigg
	 *
	 */
	private class ColorIcon implements Icon{
	    /** the width of this icon in pixel */
		private int width;
		/** the height of this icon in pixel */
		private int height;
		/** the color used to paint this icon */
		private Color color;
		
		/**
		 * Creates a new icon.
		 * @param width the width of this icon
		 * @param height the height of this icon
		 * @param color the color used to fill this icon
		 */
		public ColorIcon( int width, int height, Color color ){
			super();
			this.width = width;
			this.height = height;
			this.color = color;
		}
		
		public int getIconHeight(){
			return height;
		}
		public int getIconWidth(){
			return width;
		}
		public void paintIcon( Component c, Graphics g, int x, int y ){
			g.setColor( color );
			g.fillRect( x, y, width, height );
		}
	}
}
