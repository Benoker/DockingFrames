/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.station.screen.window;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.border.Border;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.util.color.AbstractDockColor;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.util.Colors;

/**
 * This border can be used by {@link ScreenDockWindow}s to paint a border. The border itself offers methods
 * to paint indications whether the user currently moves or presses the mouse over it. The states however have to
 * be set by window itself.
 * @author Benjamin Sigg
 */
@ColorCodes({"station.screen.border",
	"station.screen.border.line",
	"station.screen.border.line.highlight.inner",
	"station.screen.border.line.highlight.outer",
	"station.screen.border.line.shadow.inner",
	"station.screen.border.line.shadow.outer",
	"station.screen.border.highlight.inner",
	"station.screen.border.highlight.outer",
	"station.screen.border.shadow.inner",
	"station.screen.border.shadow.outer",
	"station.screen.border.hover",
	"station.screen.border.highlight.inner.hover",
	"station.screen.border.highlight.outer.hover",
	"station.screen.border.shadow.inner.hover",
	"station.screen.border.shadow.outer.hover",
	"station.screen.border.selected",
	"station.screen.border.highlight.inner.selected",
	"station.screen.border.highlight.outer.selected",
	"station.screen.border.shadow.inner.selected",
	"station.screen.border.shadow.outer.selected" })
public class ScreenDockWindowBorder implements Border{
	/** various positions where the user can grab this border */
    public static enum Position{ N, E, S, W, NE, SW, NW, SE, MOVE, NOTHING };
	
	/** size of the corners in pixel */
	private int cornerSize = 0;
	/** size of the "title" at the top in pixels */
	private int moveSize = 0;
	
	/** where the mouse is currently hovering */
	private Position mouseOver = Position.NOTHING;
	/** where the mouse is currently pressed */
	private Position mousePressed = Position.NOTHING;
	
	/** whether to draw dividing lines */
	private boolean drawDividers = true;
	
	/** the component whose border this is */
	private JComponent target;
	
	/** the window for which this border is used */
	private ScreenDockWindow window;
	
	/** the controller which is monitored for colors */
	private DockController controller;
	
	protected final BorderColor colorLine = new BorderColor( "line" );
	protected final BorderColor colorLineHighlightInner = new BorderColor( "line.highlight.inner" );
	protected final BorderColor colorLineHighlightOuter = new BorderColor( "line.highlight.outer" );
	protected final BorderColor colorLineShadowInner = new BorderColor( "line.shadow.inner" );
	protected final BorderColor colorLineShadowOuter = new BorderColor( "line.shadow.outer" );
	
	protected final BorderColor color = new BorderColor();
	protected final BorderColor colorHighlightInner = new BorderColor( "highlight.inner" );
	protected final BorderColor colorHighlightOuter = new BorderColor( "highlight.outer" );
	protected final BorderColor colorShadowInner = new BorderColor( "shadow.inner" );
	protected final BorderColor colorShadowOuter = new BorderColor( "shadow.outer" );
	
	protected final BorderColor colorHover = new BorderColor( "hover" );
	protected final BorderColor colorHighlightInnerHover = new BorderColor( "highlight.inner.hover" );
	protected final BorderColor colorHighlightOuterHover = new BorderColor( "highlight.outer.hover" );
	protected final BorderColor colorShadowInnerHover = new BorderColor( "shadow.inner.hover" );
	protected final BorderColor colorShadowOuterHover = new BorderColor( "shadow.outer.hover" );
	
	protected final BorderColor colorSelected = new BorderColor( "selected" );
	protected final BorderColor colorHighlightInnerSelected = new BorderColor( "highlight.inner.selected" );
	protected final BorderColor colorHighlightOuterSelected = new BorderColor( "highlight.outer.selected" );
	protected final BorderColor colorShadowInnerSelected = new BorderColor( "shadow.inner.selected" );
	protected final BorderColor colorShadowOuterSelected = new BorderColor( "shadow.outer.selected" );
	
	private BorderColor[] colors;
	
	/**
	 * Creates a new border
	 * @param window the window for which this border is used
	 * @param target the component which is painted by this
	 */
	public ScreenDockWindowBorder( ScreenDockWindow window, JComponent target ){
		if( window == null ){
			throw new IllegalArgumentException( "window must not be null" );
		}
		if( target == null ){
			throw new IllegalArgumentException( "target must not be null" );
		}
		this.window = window;
		this.target = target;
		
		colors = new BorderColor[]{
			colorLine,
			colorLineHighlightInner,
			colorLineHighlightOuter,
			colorLineShadowInner,
			colorLineShadowOuter,
			
			color,
			colorHighlightInner,
			colorHighlightOuter,
			colorShadowInner,
			colorShadowOuter,
			
			colorHover,
			colorHighlightInnerHover,
			colorHighlightOuterHover,
			colorShadowInnerHover,
			colorShadowOuterHover,
			
			colorSelected,
			colorHighlightInnerSelected,
			colorHighlightOuterSelected,
			colorShadowInnerSelected,
			colorShadowOuterSelected
		};
	}
	
	/**
	 * Sets the {@link DockController} which should be monitored for receiving colors.
	 * @param controller the new controller, can be <code>null</code>
	 */
	public void setController( DockController controller ){
		this.controller = controller;
		
		ColorManager manager;
		if( controller == null ){
			manager = null;
		}
		else{
			manager = controller.getColors();
		}
		for( BorderColor color : colors ){
			color.setManager( manager );
		}
	}
	
	/**
	 * Gets the {@link DockController} which is currently associated with this border
	 * @return the controller, may be <code>null</code>
	 */
	public DockController getController(){
		return controller;
	}
	
	/**
	 * Sets the size of the corners in pixels.
	 * @param cornerSize the size in pixels, at least 0
	 */
	public void setCornerSize( int cornerSize ){
		if( this.cornerSize != cornerSize ){
			this.cornerSize = cornerSize;
			target.repaint();
		}
	}
	
	/**
	 * Gets the size of the corners in pixels.
	 * @return the size in pixels
	 */
	public int getCornerSize(){
		return cornerSize;
	}

	/**
	 * Sets the size of the area at the top that can be grabbed and used
	 * to move the window. 
	 * @param moveSize the size, at least 0
	 */
	public void setMoveSize( int moveSize ){
		if( this.moveSize != moveSize ){
			this.moveSize = moveSize;
			target.repaint();
		}
	}
	
	/**
	 * Gets the size of the area at the top that can be grabbed in pixels.
	 * @return the size in pixels
	 */
	public int getMoveSize(){
		return moveSize;
	}
	
	/**
	 * Sets whether dividing lines are to be painted or not 
	 * @param drawDividers <code>true</code> if lines should be painted
	 */
	public void setDrawDividers( boolean drawDividers ){
		if( this.drawDividers != drawDividers ){
			this.drawDividers = drawDividers;
			target.repaint();
		}
	}
	
	/**
	 * Tells whether dividing lines are painted
	 * @return <code>true</code> if lines are painted
	 */
	public boolean isDrawDividers(){
		return drawDividers;
	}

	/**
	 * Sets where the mouse is currently hovering.
	 * @param mouseOver the position, <code>null</code> equals {@link Position#NOTHING}
	 */
	public void setMouseOver( Position mouseOver ){
		if( mouseOver == null ){
			mouseOver = Position.NOTHING;
		}
		if( this.mouseOver != mouseOver ){
			this.mouseOver = mouseOver;
			target.repaint();
		}
	}
	
	/**
	 * Sets where the mouse is currently pressed.
	 * @param mousePressed the position, <code>null</code> equals {@link Position#NOTHING}
	 */
	public void setMousePressed( Position mousePressed ){
		if( mousePressed == null ){
			mousePressed = Position.NOTHING;
		}
		if( this.mousePressed != mousePressed ){
			this.mousePressed = mousePressed;
			target.repaint();
		}
	}
	
	public Insets getBorderInsets( Component c ){
		return new Insets( 5, 4, 4, 4 );
//		return new Insets( 15, 9, 9, 9 );
	}

	public boolean isBorderOpaque(){
		return true;
	}

	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ){
		Color oldColor = g.getColor();
		g.translate( x, y );
		
		Insets insets = getBorderInsets( c );
		
		int hWidth = width/2;
		int titleLeft = x+hWidth-moveSize/2;
		int titleRight = x+hWidth+moveSize/2;
		
		int corner = Math.max( cornerSize, Math.max( Math.max( insets.top, insets.bottom ), Math.max( insets.left, insets.right ) ) );
		
		// title
		if( moveSize > 0 ){
			if( insets.top > 2 ){
				g.setColor( getColor( c, Position.MOVE ) );
				g.fillRect( titleLeft, 0, titleRight-titleLeft, insets.top-2 );
			}			
			
			g.setColor( getColor( c, false, false, Position.MOVE ) );
			g.drawLine( titleLeft, insets.top-2, titleRight-1, insets.top-2 );
			
			g.setColor( getColor( c, true, false, Position.MOVE ) );
			g.drawLine( titleLeft, insets.top-1, titleRight-1, insets.top-1 );
		}
		
		// top
		if( insets.top > 2 ){
			g.setColor( getColor( c, Position.N ) );
			g.fillRect( corner, 0, titleLeft - corner, insets.top-2 );
			g.fillRect( titleRight, 0, width - corner - titleRight, insets.top-2 );
		}
		g.setColor( getColor( c, false, false, Position.N ) );
		g.drawLine( corner, insets.top-2, titleLeft-1, insets.top-2 );
		g.drawLine( titleRight, insets.top-2, width-corner-1, insets.top-2 );
		
		g.setColor( getColor( c, true, false, Position.N ) );
		g.drawLine( corner, insets.top-1, titleLeft-1, insets.top-1 );
		g.drawLine( titleRight, insets.top-1, width-corner-1, insets.top-1 );
		
		// top left
		if( insets.top > 2 ){
			g.setColor( getColor( c, Position.NW ) );
			g.fillRect( 0, 0, corner, insets.top-2 );
		}
		if( insets.left > 2 ){
			g.setColor( getColor( c, Position.NW ) );
			g.fillRect( 0, 0, insets.left-2, corner );
		}
		g.setColor( getColor( c, false, false, Position.NW ) );
		g.drawLine( insets.left-2, insets.top-2, corner-1, insets.top-2 );
		g.drawLine( insets.left-2, insets.top-2, insets.left-2, corner-1 );
		
		g.setColor( getColor( c, true, false, Position.NW ) );
		g.drawLine( insets.left-1, insets.top-1, corner-1, insets.top-1 );
		g.drawLine( insets.left-1, insets.top-1, insets.left-1, corner-1 );
		
		// top right
		if( insets.top > 2 ){
			g.setColor( getColor( c, Position.NE ) );
			g.fillRect( width-corner, 0, corner, insets.top-2 );
		}
		if( insets.right > 2 ){
			g.setColor( getColor( c, Position.NE ) );
			g.fillRect( width-insets.right+2, 0, insets.right-2, corner );
		}
		g.setColor( getColor( c, false, false, Position.NE ) );
		g.drawLine( width-corner, insets.top-2, width-insets.right, insets.top-2 );
		g.setColor( getColor( c, false, true, Position.NE ) );
		g.drawLine( width-insets.right+1, insets.top-2, width-insets.right+1, corner-1 );
		
		g.setColor( getColor( c, true, false, Position.NE ) );
		g.drawLine( width-corner, insets.top-1, width-insets.right-1, insets.top-1 );
		g.setColor( getColor( c, true, true, Position.NE ) );
		g.drawLine( width-insets.right, insets.top-1, width-insets.right, corner-1 );
		
		// left
		if( insets.left > 2 ){
			g.setColor( getColor( c, Position.W ) );
			g.fillRect( 0, corner, insets.left-2, height-2*corner );
		}
		g.setColor( getColor( c, false, false, Position.W ) );
		g.drawLine( insets.left-2, corner, insets.left-2, height-corner-1 );
		
		g.setColor( getColor( c, true, false, Position.W ) );
		g.drawLine( insets.left-1, corner, insets.left-1, height-corner-1 );
		
		// right
		if( insets.right > 2 ){
			g.setColor( getColor( c, Position.E ) );
			g.fillRect( width-insets.right+2, corner, insets.right-2, height-2*corner );
		}
		g.setColor( getColor( c, false, true, Position.E ) );
		g.drawLine( width-insets.right+1, corner, width-insets.right+1, height-corner-1 );
		
		g.setColor( getColor( c, true, true, Position.E ) );
		g.drawLine( width-insets.right, corner, width-insets.right, height-corner-1 );
		
		// bottom
		if( insets.bottom > 2 ){
			g.setColor( getColor( c, Position.S ) );
			g.fillRect( corner, height-insets.bottom+2, width-2*corner, insets.bottom-2 );
		}
		
		g.setColor( getColor( c, true, true, Position.S ) );
		g.drawLine( corner, height-insets.bottom, width-corner-1, height-insets.bottom );
		
		g.setColor( getColor( c, false, true, Position.S ) );
		g.drawLine( corner, height-insets.bottom+1, width-corner-1, height-insets.bottom+1 );
		
		// bottom left
		if( insets.left > 2 ){
			g.setColor( getColor( c, Position.SW ) );
			g.fillRect( 0, height-corner, insets.left-2, corner );
		}
		if( insets.bottom > 2 ){
			g.setColor( getColor( c, Position.SW ) );
			g.fillRect( 0, height-insets.bottom+2, corner, insets.bottom-2 );
		}
		g.setColor( getColor( c, false, false, Position.SW ) );
		g.drawLine( insets.left-2, height-corner, insets.left-2, height-insets.bottom );
		g.setColor( getColor( c, true, false, Position.SW ) );
		g.drawLine( insets.left-1, height-corner, insets.left-1, height-insets.bottom-1 );
		
		g.setColor( getColor( c, true, true, Position.SW ) );
		g.drawLine( insets.left-1, height-insets.bottom, corner-1, height-insets.bottom );
		g.setColor( getColor( c, false, true, Position.SW ) );
		g.drawLine( insets.left-2, height-insets.bottom+1, corner-1, height-insets.bottom+1 );
		
		// bottom right
		if( insets.right > 2 ){
			g.setColor( getColor( c, Position.SE) );
			g.fillRect( width-insets.right+2, height-corner, insets.right-2, corner );
		}
		if( insets.bottom > 2 ){
			g.setColor( getColor( c, Position.SE ) );
			g.fillRect( width-corner, height-insets.bottom+2, corner, insets.bottom-2 );
		}
		g.setColor( getColor( c, true, true, Position.SE ) );
		g.drawLine( width-corner, height-insets.bottom, width-insets.right-1, height-insets.bottom );
		g.drawLine( width-insets.right, height-corner, width-insets.right, height-insets.bottom );
		g.setColor( getColor( c, false, true, Position.SE ) );
		g.drawLine( width-corner, height-insets.bottom+1, width-insets.right, height-insets.bottom+1 );
		g.drawLine( width-insets.right+1, height-corner, width-insets.right+1, height-insets.bottom+1 );
		
		// dividing lines
		if( drawDividers ){
			// top
			if( insets.top > 2 ){
				g.setColor( getLine( c ) );
				g.drawLine( corner, 0, corner, insets.top-3 );
				g.drawLine( titleLeft, 0, titleLeft, insets.top-3 );
				g.drawLine( titleRight-1, 0, titleRight-1, insets.top-3 );
				g.drawLine( width-corner-1, 0, width-corner-1, insets.top-3 );
			}
			g.setColor( getLine( c, true, false ) );
			g.drawLine( corner, insets.top-1, corner, insets.top-1 );
			g.drawLine( titleLeft, insets.top-1, titleLeft, insets.top-1 );
			g.drawLine( titleRight-1, insets.top-1, titleRight-1, insets.top-1 );
			g.drawLine( width-corner-1, insets.top-1, width-corner-1, insets.top-1 );
			
			g.setColor( getLine( c, false, false ) );
			g.drawLine( corner, insets.top-2, corner, insets.top-2 );
			g.drawLine( titleLeft, insets.top-2, titleLeft, insets.top-2 );
			g.drawLine( titleRight-1, insets.top-21, titleRight-1, insets.top-2 );
			g.drawLine( width-corner-1, insets.top-2, width-corner-1, insets.top-2 );
			
			
			// bottom
			if( insets.bottom > 2 ){
				g.setColor( getLine( c ) );
				g.drawLine( corner, height-insets.bottom+2, corner, height-1 );
				g.drawLine( width-corner-1, height-insets.bottom+2, width-corner-1, height-1 );
			}
			g.setColor( getLine( c, true, false ) );
			g.drawLine( corner, height-insets.bottom, corner, height-insets.bottom );
			g.drawLine( width-corner-1, height-insets.bottom, width-corner-1, height-insets.bottom );
			
			g.setColor( getLine( c, false, false ) );
			g.drawLine( corner, height-insets.bottom+1, corner, height-insets.bottom+1 );
			g.drawLine( width-corner-1, height-insets.bottom+1, width-corner-1, height-insets.bottom+1 );
			
			// left
			if( insets.left > 2 ){
				g.setColor( getLine( c ) );
				g.drawLine( 0, corner, insets.left-3, corner );
				g.drawLine( 0, height-corner-1, insets.left-3, height-corner-1 );
			}
			
			g.setColor( getLine( c, true, false ) );
			g.drawLine( insets.left-1, corner, insets.left-1, corner );
			g.drawLine( insets.left-1, height-corner-1, insets.left-1, height-corner-1 );
			
			g.setColor( getLine( c, false, false ) );
			g.drawLine( insets.left-2, corner, insets.left-2, corner );
			g.drawLine( insets.left-2, height-corner-1, insets.left-2, height-corner-1 );
			
			// right
			if( insets.right > 2 ){
				g.setColor( getLine( c ) );
				g.drawLine( width-insets.right+2, corner, width-1, corner );
				g.drawLine( width-insets.right+2, height-corner-1, width-1, height-corner-1 );
			}
			
			g.setColor( getLine( c, true, false ) );
			g.drawLine( width-insets.right, corner, width-insets.right, corner );
			g.drawLine( width-insets.right, height-corner-1, width-insets.right, height-corner-1 );
			
			g.setColor( getLine( c, false, false ) );
			g.drawLine( width-insets.right+1, corner, width-insets.right+1, corner );
			g.drawLine( width-insets.right+1, height-corner-1, width-insets.right+1, height-corner-1 );
		}
		
		g.translate( -x, -y );
		g.setColor( oldColor );
	}
	
	/**
	 * Gets the color that should be used for painting a line.
	 * @param c the component for which the line is painted
	 * @return the color, not <code>null</code>
	 */
	protected Color getLine( Component c ){
		Color color = colorLine.value();
		if( color == null ){
			return c.getForeground();
		}
		return color;
	}
	
	/**
	 * Gets the color that should be used for painting a line.
	 * @param c the component for which the line is painted
	 * @param inner whether the inner or the outer rectangle is painted
	 * @param shadow whether the shadow or the highlight part is painted
	 * @return the color, not <code>null</code>
	 */
	protected Color getLine( Component c, boolean inner, boolean shadow ){
		Color color;
		if( inner && shadow ){
			color = colorLineShadowInner.value();
		}
		else if( inner && !shadow ){
			color = colorLineHighlightInner.value();
		}
		else if( !inner && shadow ){
			color = colorLineShadowOuter.value();
		}
		else{
			color = colorLineHighlightOuter.value();
		}
		
		if( color != null ){
			return color;
		}
		
		return defaultModify( getLine( c ), inner, shadow );
	}
	
	/**
	 * Gets a color to paint some part of this border.
	 * @param c the component for which the border is painted
	 * @param position which part is painted, not <code>null</code>
	 * @return the color, not <code>null</code>
	 */
	private Color getColor( Component c, Position position ){
		if( position == mousePressed ){
			return getSelectedColor( c );
		}
		if( position == mouseOver ){
			return getHoverColor( c );
		}
		return getNormalColor( c );
	}
	
	/**
	 * Gets a color to paint some part of this border.
	 * @param c the component for which the border is painted
	 * @param inner whether the inner or the outer rectangle is painted
	 * @param shadow whether the shadow or the highlight part is painted
	 * @param position which part is painted, not <code>null</code>
	 * @return the color, not <code>null</code>
	 */
	private Color getColor( Component c, boolean inner, boolean shadow, Position position ){
		if( position == mousePressed ){
			return getSelectedColor( c, inner, shadow );
		}
		if( position == mouseOver ){
			return getHoverColor( c, inner, shadow );
		}
		return getNormalColor( c, inner, shadow );
	}
	
	/**
	 * Gets the base color used for painting a region under the mouse
	 * @param c the component for which the border is painted
	 * @return the color, not <code>null</code>
	 */
	protected Color getHoverColor( Component c ){
		Color color = colorHover.value();
		if( color == null ){
			return Color.YELLOW;
		}
		return color;
	}
	
	/**
	 * Gets a color to paint some part of this border that is under the mouse.
	 * @param c the component for which the border is painted
	 * @param inner whether the inner or the outer rectangle is painted
	 * @param shadow whether the shadow or the highlight part is painted
	 * @return the color, not <code>null</code>
	 */
	protected Color getHoverColor( Component c, boolean inner, boolean shadow ){
		Color color;
		if( inner && shadow ){
			color = colorShadowInnerHover.value();
		}
		else if( inner && !shadow ){
			color = colorHighlightInnerHover.value();
		}
		else if( !inner && shadow ){
			color = colorShadowOuterHover.value();
		}
		else{
			color = colorHighlightOuterHover.value();
		}
		
		if( color != null ){
			return color;
		}
		
		return defaultModify( getHoverColor( c ), inner, shadow );
	}
	
	/**
	 * Gets a color to paint some part of this border that is under the pressed mouse.
	 * @param c the component for which the border is painted
	 * @return the color, not <code>null</code>
	 */
	protected Color getSelectedColor( Component c ){
		Color color = colorSelected.value();
		if( color == null ){
			return Color.RED;
		}
		return color;
	}
	
	/**
	 * Gets a color to paint some part of this border that is under the pressed mouse.
	 * @param c the component for which the border is painted
	 * @param inner whether the inner or the outer rectangle is painted
	 * @param shadow whether the shadow or the highlight part is painted
	 * @return the color, not <code>null</code>
	 */
	protected Color getSelectedColor( Component c, boolean inner, boolean shadow ){
		Color color;
		if( inner && shadow ){
			color = colorShadowInnerSelected.value();
		}
		else if( inner && !shadow ){
			color = colorHighlightInnerSelected.value();
		}
		else if( !inner && shadow ){
			color = colorShadowOuterSelected.value();
		}
		else{
			color = colorHighlightOuterSelected.value();
		}
		
		if( color != null ){
			return color;
		}
		
		return defaultModify( getSelectedColor( c ), inner, shadow );
	}

	/**
	 * Gets a color to paint some part of this border.
	 * @param c the component for which the border is painted
	 * @return the color, not <code>null</code>
	 */
	protected Color getNormalColor( Component c ){
		Color color = this.color.value();
		if( color == null ){
			return c.getBackground();
		}
		return color;
	}
	
	/**
	 * Gets a color to paint some part of this.
	 * @param c the component for which the border is painted
	 * @param inner whether the inner or the outer rectangle is painted
	 * @param shadow whether the shadow or the highlight part is painted
	 * @return the color, not <code>null</code>
	 */
	protected Color getNormalColor( Component c, boolean inner, boolean shadow ){
		Color color;
		if( inner && shadow ){
			color = colorShadowInner.value();
		}
		else if( inner && !shadow ){
			color = colorHighlightInner.value();
		}
		else if( !inner && shadow ){
			color = colorShadowOuter.value();
		}
		else{
			color = colorHighlightOuter.value();
		}
		
		if( color != null ){
			return color;
		}
		
		return defaultModify( getNormalColor( c ), inner, shadow );
	}
	
	/**
	 * Creates colors for parts of this border based on some basic color.
	 * @param color the basic color
	 * @param inner whether the inner or the outer rectangle is painted
	 * @param shadow whether the shadow or the highlight part is painted
	 * @return the modified color
	 */
	protected Color defaultModify( Color color, boolean inner, boolean shadow ){		
		if( inner && shadow ){
			return Colors.darker( color, 0.2 );
		}
		else if( !inner && shadow ){
			return Colors.darker( color, 0.4 );
		}
		else if( inner && !shadow ){
			return Colors.brighter( color, 0.2 );
		}
		else {
			return Colors.brighter( color, 0.4 ); 
		}
	}
	
	/**
	 * A {@link DockColor} representing a color used by this border.
	 * @author Benjamin Sigg
	 */
	protected class BorderColor extends AbstractDockColor implements ScreenDockWindowColor{
		public BorderColor( String id ){
			super( "station.screen.border." + id, KIND_SCREEN_WINDOW_COLOR );
		}
		
		public BorderColor(){
			super( "station.screen.border", KIND_SCREEN_WINDOW_COLOR );
		}
		
		@Override
		protected void changed( Color oldValue, Color newValue ){
			target.repaint();
		}

		public ScreenDockWindow getWindow(){
			return window;
		}
	}
}
