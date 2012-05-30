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
package bibliothek.gui.dock.station.toolbar.menu;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupHeader;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupHeaderFactory;

/**
 * A factory for a toggle button that will open the {@link CustomizationMenu}.
 * @author Benjamin Sigg
 */
public class CustomizationButton implements ToolbarGroupHeaderFactory{
	/** the controller in whose realm this factory is used */
	private DockController controller;
	
	/** the menu to show */
	private CustomizationMenu menu;
	
	/** the content of the menu */
	private CustomizationMenuContent content;
	
	/**
	 * Creates a new factory.
	 * @param controller the controller in whose realm this button will be used
	 */
	public CustomizationButton( DockController controller ){
		this.controller = controller;
	}

	@Override
	public ToolbarGroupHeader create( ToolbarGroupDockStation station ){
		return new Button( station );
	}
	
	/**
	 * Sets the menu which should be used by the buttons to show the content.
	 * @param menu the menu to use, can be <code>null</code>
	 */
	public void setMenu( CustomizationMenu menu ){
		if( this.menu != null ){
			this.menu.close();
			this.menu.setController( null );
		}
		this.menu = menu;
		if( menu != null ){
			menu.setController( controller );
			menu.setContent( content );
		}
	}
	
	/**
	 * Sets the contents of the menu. 
	 * @param content the contents, can be <code>null</code>
	 */
	public void setContent( CustomizationMenuContent content ){
		if( menu != null ){
			menu.setContent( content );
		}
		this.content = content;
	}
	
	/**
	 * A toggle button that will open the {@link CustomizationMenu}.
	 * @author Benjamin Sigg
	 */
	protected class Button implements ToolbarGroupHeader, CustomizationMenuCallback{
		private JToggleButton toggle;
		private Orientation orientation;
		private boolean open = false;
		private ToolbarGroupDockStation station;
		
		/**
		 * Creates a new button
		 * @param station the station on which this button will be shown
		 */
		public Button( ToolbarGroupDockStation station ){
			this.station = station;
			ImageIcon icon = null;
			try{
				final InputStream in = getClass().getResourceAsStream(
						"/data/bibliothek/gui/toolbar/tool.png");
				if (in == null){
					throw new FileNotFoundException("cannot find file 'tool.png'");
				}
				icon = new ImageIcon(ImageIO.read(in));
				in.close();
			} catch (final IOException e){
				e.printStackTrace();
				icon = null;
			}
			if (icon != null) {
				toggle = new JToggleButton( icon );
			} else {
				toggle = new JToggleButton( ">" );
			}
			
			toggle.addActionListener( new ActionListener(){
				@Override
				public void actionPerformed( ActionEvent e ){
					if( toggle.isSelected() ){
						showMenu();
					}
					else{
						if( open ){
							menu.close();
						}
					}
				}
			} );
		}
		
		private void showMenu(){
			if( menu == null ){
				throw new IllegalStateException( "there is no menu set" );
			}
			Point location = new Point( 0, 0 );
			SwingUtilities.convertPointToScreen( location, toggle );
			if( orientation == Orientation.HORIZONTAL ){
				location.y += toggle.getHeight();
			}
			else{
				location.x += toggle.getWidth();
			}
			menu.open( location.x, location.y, this );
		}
		
		@Override
		public Component getComponent(){
			return toggle;
		}

		@Override
		public void setOrientation( Orientation orientation ){
			this.orientation = orientation;
		}

		@Override
		public Rectangle getButton(){
			Point location = new Point( 0, 0 );
			SwingUtilities.convertPointToScreen( location, toggle );
			return new Rectangle( location, toggle.getSize() );
		}

		@Override
		public Component getParent(){
			return toggle;
		}

		@Override
		public DockStation getOwner(){
			return station;
		}

		@Override
		public void append( Dockable dockable ){
			DockStation parent = station;
			while( true ){
				if( parent.getDockableCount() == 0 ){
					parent.drop( dockable );
					return;
				}
				Dockable child = parent.getDockable( parent.getDockableCount()-1 );
				if( child.asDockStation() != null ){
					parent = child.asDockStation();
				}
				else{
					parent.drop( dockable );
					return;
				}
			}
		}

		@Override
		public void closed(){
			open = false;
			toggle.setSelected( false );
		}
	}
}
