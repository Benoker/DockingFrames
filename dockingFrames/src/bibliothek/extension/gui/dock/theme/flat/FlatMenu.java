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
package bibliothek.extension.gui.dock.theme.flat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.menu.AbstractCombinedMenu;
import bibliothek.gui.dock.themes.color.MenuColor;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * A menu that contains a list of {@link Dockable}s to select.
 * @author Benjamin Sigg
 */
@ColorCodes({
	"stack.menu.edge",
	"stack.menu.middle",
	"stack.menu.edge.selected",
	"stack.menu.middle.selected"
})
public class FlatMenu extends AbstractCombinedMenu{
	private FlatTabPane pane;
	private Component component;
	
	private FlatMenuColor buttonEdge = new FlatMenuColor( "stack.menu.edge" );
	private FlatMenuColor buttonMiddle = new FlatMenuColor( "stack.menu.middle" );
	private FlatMenuColor buttonEdgeSelected = new FlatMenuColor( "stack.menu.edge.selected" );
	private FlatMenuColor buttonMiddleSelected = new FlatMenuColor( "stack.menu.middle.selected" );
	
	/**
	 * Creates a new {@link FlatMenu}.
	 * @param parent the panel for which this menu is used
	 */
	public FlatMenu( FlatTabPane parent ){
		super( parent, parent.getMenuVisibilityHandler() );
		this.pane = parent;
	}
	
	@Override
	protected Component createComponent(){
		component = new Button();
		return component;
	}
	
	@Override
	protected void selected( Dockable dockable ){
		pane.setSelectedDockable( dockable );
	}
	
	@Override
	public void setController( DockController controller ){
		super.setController( controller );
		buttonEdge.connect( controller );
		buttonMiddle.connect( controller );
		buttonEdgeSelected.connect( controller );
		buttonMiddleSelected.connect( controller );
	}
	
	
	
	/**
	 * The button shown for this menu.
	 * @author Benjamin Sigg
	 */
	private class Button extends JPanel{
		/** whether the mouse is currently over this button */
		private boolean mouseInsideButton = false;
		
		/**
		 * Creates a new button.
		 */
		public Button(){
			addMouseListener( new MouseAdapter(){
				@Override
				public void mouseEntered( MouseEvent e ){
					mouseInsideButton = true;
					repaint();
				}
				@Override
				public void mouseExited( MouseEvent e ){
					mouseInsideButton = false;
					repaint();
				}
				@Override
				public void mouseClicked( MouseEvent e ){
					open();
				}
			});
			
			setPreferredSize( new Dimension( 20, 20 ));
		}
		
		@Override
		protected void paintComponent( Graphics g ){
			super.paintComponent( g );
			
			Color edge = null;
			Color middle = null;
			
			if( mouseInsideButton ){
				edge = buttonEdgeSelected.color();
				middle = buttonMiddleSelected.color();
			}
			
			if( edge == null )
				edge = buttonEdge.color();
			if( middle == null )
				middle = buttonMiddleSelected.color();
			
			if( edge == null )
				edge = getBackground();
			if( middle == null )
				middle = getBackground();
			
			Graphics2D g2 = (Graphics2D)g;
			
			int width = getWidth();
			int height = getHeight();
			
			g2.setPaint( new GradientPaint( 0, 0, edge, (width+1)/2, 0, middle ));
			g2.drawLine( 0, 0, width, 0 );
			g2.drawLine( 0, height-1, width, height-1 );
			
			g2.setPaint( new GradientPaint( 0, 0, edge, 0, (height+1)/2, middle ));
			g2.drawLine( 0, 0, 0, height );
			g2.drawLine( width-1, 0, width-1, height );
		}
	}

	/**
	 * A color used on this menu.
	 * @author Benjamin Sigg
	 */
	private class FlatMenuColor extends MenuColor{
		/**
		 * Creates a new color.
		 * @param id a unique identifier
		 */
		public FlatMenuColor( String id ){
			super( id, pane.getStation(), FlatMenu.this, null );
		}
		
		@Override
		protected void changed( Color oldValue, Color newValue ){
			if( component != null ){
				component.repaint();
			}
		}
	}
}
