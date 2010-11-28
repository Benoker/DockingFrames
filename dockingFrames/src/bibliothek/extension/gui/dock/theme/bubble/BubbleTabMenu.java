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
package bibliothek.extension.gui.dock.theme.bubble;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.CombinedHandler;
import bibliothek.gui.dock.station.stack.CombinedMenu;
import bibliothek.gui.dock.station.stack.StackDockComponentParent;
import bibliothek.gui.dock.station.stack.menu.AbstractCombinedMenu;
import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.themes.color.MenuColor;
import bibliothek.gui.dock.util.BackgroundPanel;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * A round button with an icon that opens a menu when clicked.
 * @author Benjamin Sigg
 */
@ColorCodes({ 
    "stack.menu.background.top.mouse",
    "stack.menu.background.bottom.mouse",
    "stack.menu.border.mouse",
    
    "stack.menu.background.top",
    "stack.menu.background.bottom",
    "stack.menu.border"
})
public class BubbleTabMenu extends AbstractCombinedMenu{
	private StackDockComponentParent station;
	
	private BubbleMenuColor colorTopMouse;
	private BubbleMenuColor colorBottomMouse;
	private BubbleMenuColor colorBorderMouse;

	private BubbleMenuColor colorTop;
	private BubbleMenuColor colorBottom;
	private BubbleMenuColor colorBorder;
	
	private BubbleColorAnimation animation;
	
	private boolean mouseOver;
	
	private int borderSize = 3;
	
	/**
	 * Creates a new tab menu.
	 * @param station the owner of this menu
	 * @param parent the pane on which the menu is shown
	 * @param visibility handles the visibility of this menu
	 */
	public BubbleTabMenu( StackDockComponentParent station, TabPane parent, CombinedHandler<CombinedMenu> visibility ){
		super( parent, visibility );
		this.station = station;
		
		colorTopMouse = new BubbleMenuColor( "stack.menu.background.top.mouse", Color.RED.brighter() );
		colorBottomMouse = new BubbleMenuColor( "stack.menu.background.bottom.mouse", Color.RED.darker() );
		colorBorderMouse = new BubbleMenuColor( "stack.menu.border.mouse", Color.RED.darker().darker() );
		
		colorTop = new BubbleMenuColor( "stack.menu.background.top", Color.RED.brighter() );
		colorBottom = new BubbleMenuColor( "stack.menu.background.bottom", Color.RED.darker() );
		colorBorder = new BubbleMenuColor( "stack.menu.border", Color.RED.darker().darker() );
		
		animation = new BubbleColorAnimation();
	
		animate();
		animation.kick();
	}
	
	@Override
	public void setController( DockController controller ){
		super.setController( controller );
		
		colorTopMouse.connect( controller );
		colorBottomMouse.connect( controller );
		colorBorderMouse.connect( controller );
		
		colorTop.connect( controller );
		colorBottom.connect( controller );
		colorBorder.connect( controller );
		
		animate();
		animation.kick();
	}
	
	/**
	 * Stops any animation of this menu.
	 */
	public void stopAnimation(){
		animation.stop();
	}
	
	/**
	 * Tells whether the mouse is currently over this menus button.
	 * @return <code>true</code> if the mouse is over the button
	 */
	public boolean isMouseOver(){
		return mouseOver;
	}
	
	/**
	 * Ensures that the animation uses the correct set of colors.
	 */
	private void animate(){
		if( isMouseOver() ){
			animation.putColor( "top", colorTopMouse.color() );
			animation.putColor( "bottom", colorBottomMouse.color() );
			animation.putColor( "border", colorBorderMouse.color() );
		}
		else{
			animation.putColor( "top", colorTop.color() );
			animation.putColor( "bottom", colorBottom.color() );
			animation.putColor( "border", colorBorder.color() );
		}
	}
	
	@Override
	protected Component createComponent(){
		return new Button();
	}

	@Override
	protected void selected( Dockable dockable ){
		// ignore
	}
	
	/**
	 * A round button, when clicked calls {@link AbstractCombinedMenu#open()}.
	 * @author Benjamin Sigg
	 */
	private class Button extends BackgroundPanel implements Runnable{
		/**
		 * Creates a new button.
		 */
		public Button(){
			setOpaque( false );
			setBackground( BubbleTabMenu.this.getBackground() );
			animation.addTask( this );
			
			setPreferredSize( new Dimension( 20, 20 ) );
			
			addMouseListener( new MouseAdapter(){
				@Override
				public void mouseEntered( MouseEvent e ){
					mouseOver = true;
					animate();
				}
				
				@Override
				public void mouseExited( MouseEvent e ){
					mouseOver = false;
					animate();
				}
				
				@Override
				public void mouseClicked( MouseEvent e ){
					open();
				}
			});
		}
		
		public void run(){
			repaint();
		}
		
		@Override
		public void paintBackground( Graphics g ){
			Color top = animation.getColor( "top" );
			Color bottom = animation.getColor( "bottom" );
			Color border = animation.getColor( "border" );
			
			int w = getWidth();
			int h = getHeight();

	        Graphics2D g2 = (Graphics2D)g.create();
	        
	        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
	                    
	        // draw border
			g2.setColor( border );
			g2.fillOval( 0, 0, w, h );
	        
	        // draw background
			if( w > 2*borderSize && h > 2*borderSize ){
				g2.setPaint( new GradientPaint( 0, 0, top, 0, h-borderSize, bottom ) );
				g2.fillOval( borderSize, borderSize, w-2*borderSize, h-2*borderSize );
			}
	        
	        // draw horizon
			g2.setPaint( new GradientPaint( 0, 0, new Color( 150, 150, 150 ), 0, h/2, Color.WHITE ));
			g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.4f ) );
			
			g2.fillArc( 0, 0, w, h, 0, 180 );
			
			g2.dispose();
		}
	}

	/**
	 * Link to a color that is used to paint this button.
	 * @author Benjamin Sigg
	 */
	protected class BubbleMenuColor extends MenuColor{
		/**
		 * Creates a new link.
		 * @param id the unique identifier of the target
		 * @param backup the backup color used when the target is not found
		 */
		public BubbleMenuColor( String id, Color backup ){
			super( id, station.getStation(), BubbleTabMenu.this, backup );
		}
		
		@Override
		protected void changed( Color oldValue, Color newValue ){
			animate();
		}
	}
}
