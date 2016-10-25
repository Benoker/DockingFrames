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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import bibliothek.gui.DockController;

/**
 * This is the default component to be used by a {@link GroupedCustomizationMenuContent} as title for one group.
 * @author Benjamin Sigg
 */
public class GroupedCustomizationMenuTitle implements CustomizationMenuContent{
	private JPanel titlePanel;
	private String title;
	private Insets textInsets = new Insets( 1, 10, 3, 5 );
	private Insets insets = new Insets( 1, 0, 0, 1 );
	
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
	
	/**
	 * Sets the area around the text that should remain empty.
	 * @param insets the area to remain empty
	 */
	public void setTextInsets( Insets insets ){
		if( insets == null ){
			throw new IllegalArgumentException( "insets must not be null" );
		}
		this.textInsets = insets;
	}
	
	/**
	 * Gets the area around the text that should remain empty.
	 * @return the area, not <code>null</code>
	 */
	public Insets getTextInsets(){
		return textInsets;
	}
	
	/**
	 * Sets the area around the entire title that should remain empty.
	 * @param insets the area to remain empty
	 */
	public void setInsets( Insets insets ){
		if( insets == null ){
			throw new IllegalArgumentException( "insets must not be null" );
		}
		this.insets = insets;
	}
	
	/**
	 * Gets the area around the entire title that remains empty.
	 * @return the area that remains empty
	 */
	public Insets getInsets(){
		return insets;
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
		titlePanel = new JPanel();
		final JSeparator line = new JSeparator( SwingConstants.HORIZONTAL );
		final JLabel text = new JLabel( title );
		text.setOpaque( true );
		text.setBorder( BorderFactory.createEmptyBorder( 1, 2, 1, 2 ) );
		titlePanel.add( text );
		titlePanel.add( line );
		
		titlePanel.setLayout( new LayoutManager(){
			@Override
			public Dimension preferredLayoutSize( Container parent ){
				Dimension textDimension = text.getPreferredSize();
				Dimension lineDimension = line.getPreferredSize();
				
				int width = Math.max( textDimension.width + textInsets.left + textInsets.right, lineDimension.width ) + insets.left + insets.right;
				int height = Math.max( textDimension.height + textInsets.top + textInsets.bottom, lineDimension.height ) + insets.top + insets.bottom;
				return new Dimension( width, height );
			}
			
			@Override
			public Dimension minimumLayoutSize( Container parent ){
				Dimension textDimension = text.getMinimumSize();
				Dimension lineDimension = line.getMinimumSize();
				
				int width = Math.max( textDimension.width + textInsets.left + textInsets.right, lineDimension.width ) + insets.left + insets.right;
				int height = Math.max( textDimension.height + textInsets.top + textInsets.bottom, lineDimension.height ) + insets.top + insets.bottom;
				return new Dimension( width, height );
			}
			
			@Override
			public void layoutContainer( Container parent ){
				Dimension size = line.getPreferredSize();
				int maxHeight = parent.getHeight() - insets.top - insets.bottom;
				int height = Math.min( size.height, maxHeight );
				int y = insets.top + maxHeight/2 - height/2;
				line.setBounds( insets.left, y, parent.getWidth()-insets.left-insets.right, height );
				
				size = text.getPreferredSize();
				int width = Math.min( size.width, parent.getWidth()-insets.left-insets.right-textInsets.left-textInsets.right );
				maxHeight = parent.getHeight()-insets.top-insets.bottom-textInsets.top-textInsets.bottom;
				height = Math.min( size.height, maxHeight );
				y = insets.top + textInsets.top + maxHeight/2 - height/2;
				text.setBounds( insets.left+textInsets.left, y, width, height );
			}
			
			@Override
			public void addLayoutComponent( String name, Component comp ){
				// ignore
			}
			
			@Override
			public void removeLayoutComponent( Component comp ){
				// ignore
			}
		});
	}

	@Override
	public void unbind(){
		titlePanel = null;
	}
}
