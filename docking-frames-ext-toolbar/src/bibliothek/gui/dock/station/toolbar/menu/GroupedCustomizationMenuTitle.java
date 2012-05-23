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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import bibliothek.gui.DockController;

/**
 * This is the default component to be used by a {@link GroupedCustomizationMenuContent} as title for one group.
 * @author Benjmain Sigg
 */
public class GroupedCustomizationMenuTitle implements CustomizationMenuContent{
	private JPanel titlePanel;
	private String title;
	
	/**
	 * Creates a new title.
	 * @param title the text of the title
	 */
	public GroupedCustomizationMenuTitle( String title ){
		this.title = title;
	}
	
	/**
	 * Sets the title text.
	 * @param title the new title
	 */
	public void setTitle( String title ){
		this.title = title;
	}
	
	/**
	 * Gets the current title text.
	 * @return the current text
	 */
	public String getTitle(){
		return title;
	}
	
	@Override
	public Component getView(){
		return titlePanel;
	}

	@Override
	public void setController( DockController controller ){
		// ignore
	}

	@Override
	public void bind( CustomizationMenuCallback callback ){
		titlePanel = new JPanel(){
			@Override
			public Dimension getMinimumSize(){
				return getPreferredSize();
			}
			
			@Override
			public Dimension getPreferredSize(){
				Insets insets = getInsets();
				return new Dimension( insets.left + insets.right, insets.top + insets.bottom );
			}
		};
		titlePanel.setBorder( new TopBorder( BorderFactory.createTitledBorder( title ) ) );
	}

	@Override
	public void unbind(){
		titlePanel = null;
	}
	
	private class TopBorder implements Border{
		private Border border;
		
		public TopBorder( Border border ){
			this.border = border;
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ){
			Insets insets = border.getBorderInsets( c );
			border.paintBorder( c, g, x-insets.left, y, width+insets.left+insets.right, height+insets.bottom );
		}

		@Override
		public Insets getBorderInsets( Component c ){
			Insets insets = border.getBorderInsets( c );
			return new Insets( insets.top, 0, 0, 0 );
		}

		@Override
		public boolean isBorderOpaque(){
			return border.isBorderOpaque();
		}
	}
}
