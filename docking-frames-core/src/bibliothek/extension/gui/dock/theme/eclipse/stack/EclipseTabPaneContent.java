/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse.stack;

import java.awt.Graphics;

import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabPanePainter;
import bibliothek.gui.dock.station.stack.CombinedStackDockContentPane;

/**
 * The panel painting the background of a {@link EclipseTabPane}.
 * @author Benjamin Sigg
 */
public class EclipseTabPaneContent extends CombinedStackDockContentPane{
	private EclipseTabPane pane;
	
	public EclipseTabPaneContent( EclipseTabPane pane ){
		super( pane );
		this.pane = pane;
		setPaintBackground( false );
	}
	
	/**
	 * Gets the parent of this panel.
	 * @return the parent
	 */
	public EclipseTabPane getPane(){
		return pane;
	}
	
	@Override
	public void paintBackground( Graphics g ){
		getPane().getPainter().paintBackground( g );
	}
	
	@Override
	public void paintBorder( Graphics g ){
		// ignore
	}

	@Override
	public void paintOverlay( Graphics g ){
		TabPanePainter painter = getPane().getPainter();
		painter.paintForeground( g );
		
		Border border = getBorder();
		if( border != null ){
			border.paintBorder( this, g, 0, 0, getWidth(), getHeight() );
		}
	}
}
