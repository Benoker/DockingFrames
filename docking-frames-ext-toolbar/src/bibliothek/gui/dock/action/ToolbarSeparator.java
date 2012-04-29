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

package bibliothek.gui.dock.action;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * A {@link ToolbarSeparator} draws a very thin line.
 * @author Benjamin Sigg
 */
public class ToolbarSeparator extends JComponent implements BasicTitleViewItem<JComponent> {
	private SeparatorAction action;
	private Color color;
	private Orientation orientation = Orientation.NORTH_SIDED;

	public ToolbarSeparator( SeparatorAction action, Color color ){
		this.action = action;
		this.color = color;
		setOpaque( false );
		setFocusable( false );
	}

	@Override
	public void bind(){
		// ignore
	}

	@Override
	public void unbind(){
		// ignore
	}

	@Override
	public JComponent getItem(){
		return this;
	}

	@Override
	public DockAction getAction(){
		return action;
	}

	@Override
	public void setOrientation( Orientation orientation ){
		this.orientation = orientation;
	}

	public Dimension getPreferredSize(){
		if( isPreferredSizeSet() ) {
			return super.getPreferredSize();
		}
		return new Dimension( 1, 1 );
	}

	@Override
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}

	@Override
	protected void paintComponent( Graphics g ){
		g.setColor( color );
		if( orientation.isHorizontal() ) {
			g.drawLine( 0, 3, 0, getHeight() - 4 );
		}
		else {
			g.drawLine( 3, 0, getWidth() - 4, 0 );
		}
	}
}
