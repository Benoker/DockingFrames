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

import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabPanePainter;
import bibliothek.gui.dock.station.stack.CombinedStackDockContentPane;
import bibliothek.gui.dock.util.BackgroundPaint;

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
	public void paint( Graphics g ){
		BackgroundPaint background = getBackgroundPaint();
		TabPanePainter painter = pane.getPainter();
		
		boolean done = false;
		
		if( background != null ){
			done = background.paint( getBackgroundComponent(), this, g );
		}
		if( !done && painter != null ){
			painter.paintBackground( g );
		}
		
		super.paint( g );
	
		if( painter != null ){
			painter.paintForeground( g );
		}
		
		paintBorder( g );
	}
}
