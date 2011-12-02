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
package bibliothek.extension.gui.dock.theme.bubble;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * A view for a {@link SeparatorAction}.
 * @author Benjamin Sigg
 */
public class BubbleSeparator extends JComponent implements BasicTitleViewItem<JComponent>{
	/** the action shown on this view */
	private SeparatorAction action;
	/** whether the layout of the title on which this view is, is vertically or horizontally */
	private Orientation orientation = Orientation.FREE_HORIZONTAL;
	
	/**
	 * Creates a new view
	 * @param action the action to show on this view
	 */
	public BubbleSeparator( SeparatorAction action ){
		this.action = action;
		setOpaque( false );
		setFocusable( false );
	}
	
	@Override
	public Dimension getPreferredSize(){
		if( isPreferredSizeSet() )
			return super.getPreferredSize();
		
		return new Dimension( 2, 2 );
	}
	
	@Override
	protected void paintComponent( Graphics g ){
		Graphics2D g2 = (Graphics2D)g.create();
		g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.4f ) );
		g2.setColor( Color.BLACK );
		if( orientation.isHorizontal() )
			g2.fillRect( 0, 1, getWidth(), getHeight()-2 );
		else
			g2.fillRect( 1, 0, getWidth()-2, getHeight() );
			
		g2.dispose();
	}
	
	public void setOrientation( Orientation orientation ){
		this.orientation = orientation;
		repaint();
	}

	public void bind(){
		// ignore
	}

	public DockAction getAction(){
		return action;
	}

	public JComponent getItem(){
		return this;
	}

	public void unbind(){
		// ignore
	}
}
