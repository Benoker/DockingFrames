/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.util;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * This {@link JPanel} implements {@link PaintableComponent} and 
 * can use a {@link BackgroundAlgorithm} to paint its background.
 * @author Benjamin Sigg
 */
public class BackgroundPanel extends JPanel implements PaintableComponent{
	private BackgroundAlgorithm background;
	
	/**
	 * Sets the background algorithm that should be used by this panel.
	 * @param background the background algorithm
	 */
	public void setBackground( BackgroundAlgorithm background ){
		this.background = background;
	}
	
	/**
	 * Gets the algorithm that paints the background of this panel.
	 * @return the algorithm, can be <code>null</code>
	 */
	public BackgroundAlgorithm getBackgroundAlgorithm(){
		return background;
	}

	protected void paintComponent( Graphics g ){
		if( background == null ){
			paintBackground( g );
			paintForeground( g );
		}
		else{
			background.paint( this, g );
		}
	}
	
	public Component getComponent(){
		return this;
	}
	
	public void paintBackground( Graphics g ){
		super.paintComponent( g );
	}
	
	public void paintForeground( Graphics g ){
		// ignore	
	}
}
