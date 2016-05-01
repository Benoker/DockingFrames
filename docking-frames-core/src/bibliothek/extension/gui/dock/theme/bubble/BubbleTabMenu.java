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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.station.stack.menu.ButtonCombinedMenu;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;
import bibliothek.gui.dock.themes.color.MenuColor;
import bibliothek.gui.dock.util.color.AbstractDockColor;
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
public class BubbleTabMenu extends ButtonCombinedMenu<RoundButton>{
	private BubbleStackDockComponent pane;
	private int borderSize = 3;
	
	public BubbleTabMenu( BubbleStackDockComponent pane ){
		super( pane, pane.getMenuHandler() );
		this.pane = pane;
	}

	@Override
	protected RoundButton createButton( BasicTrigger trigger ){
		return new Button( trigger );
	}
	
	@Override
	protected BasicButtonModel getModel( RoundButton button ){
		return button.getModel();
	}
	
	@Override
	public void setController( DockController controller ){
		super.setController( controller );
		RoundButton button = getButton();
		if( button != null ){
			button.setController( controller );
		}
	}

	private class Button extends RoundButton{
		public Button( BasicTrigger trigger ){
			super( trigger, null, null, null );
			setController( BubbleTabMenu.this.getController() );
			setPaintFocusBorder( false );
		}

		@Override
		protected AbstractDockColor[] createColors( Dockable dockable, DockAction action ){
			return new AbstractDockColor[]{
					createColor( "stack.menu.background.top.mouse", dockable, action, Color.WHITE ),
					createColor( "stack.menu.background.bottom.mouse", dockable, action, Color.WHITE ),
					createColor( "stack.menu.border.mouse", dockable, action, Color.BLACK ),
					
					createColor( "stack.menu.background.top", dockable, action, Color.WHITE ),
					createColor( "stack.menu.background.bottom", dockable, action, Color.WHITE ),
					createColor( "stack.menu.border", dockable, action, Color.BLACK )
			};
		}
		
		@Override
		protected AbstractDockColor createColor( String key, Dockable dockable, DockAction action, Color backup ){
			return new BubbleMenuColor( key, backup );
		}
		
		@Override
		protected void updateColors(){
			if( getModel().isMouseInside() || getModel().isMousePressed() ){
				animate( "top", "stack.menu.background.top.mouse" );
				animate( "bottom", "stack.menu.background.bottom.mouse" );
				animate( "border", "stack.menu.border.mouse" );
			}
			else{
				animate( "top", "stack.menu.background.top" );
				animate( "bottom", "stack.menu.background.bottom" );
				animate( "border", "stack.menu.border" );
			}
		}
		
		@Override
		protected void doPaintBackground( Graphics g ){
			BubbleColorAnimation animation = getAnimation();
			
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
				super( id, pane.getStation(), BubbleTabMenu.this, backup );
			}
			
			@Override
			protected void changed( Color oldValue, Color newValue ){
				updateColors();
			}
		}
	}
}
