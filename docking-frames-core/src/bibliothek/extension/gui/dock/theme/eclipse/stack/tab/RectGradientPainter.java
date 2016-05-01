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
package bibliothek.extension.gui.dock.theme.eclipse.stack.tab;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Window;

import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import bibliothek.extension.gui.dock.theme.eclipse.OwnedRectEclipseBorder;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTabPane;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * This {@link TabComponent} draws a compact tab.
 * @author Janni Kovacs
 */
@ColorCodes({
	"stack.tab.border", 
	"stack.tab.border.selected", 
	"stack.tab.border.selected.focused", 
	"stack.tab.border.selected.focuslost",
	"stack.tab.border.disabled",
	
    "stack.tab.top", 
    "stack.tab.top.selected", 
    "stack.tab.top.selected.focused",
    "stack.tab.top.selected.focuslost",
    "stack.tab.top.disabled", 
    
    "stack.tab.bottom", 
    "stack.tab.bottom.selected", 
    "stack.tab.bottom.selected.focused", 
    "stack.tab.bottom.selected.focuslost",
    "stack.tab.bottom.disabled",
    
    "stack.tab.text", 
    "stack.tab.text.selected", 
    "stack.tab.text.selected.focused", 
    "stack.tab.text.selected.focuslost",
    "stack.tab.text.disabled", 
    
    "stack.border" })
public class RectGradientPainter extends BaseTabComponent {
	public static final TabPainter FACTORY = new TabPainter(){
		public TabComponent createTabComponent( EclipseTabPane pane, Dockable dockable ){
			return new RectGradientPainter( pane, dockable );
		}
		
		public TabPanePainter createDecorationPainter( EclipseTabPane pane ){
		    return new LinePainter( pane );
		}
		
		public InvisibleTab createInvisibleTab( InvisibleTabPane pane, Dockable dockable ){
			return new DefaultInvisibleTab( pane, dockable );
		}
		
		public Border getFullBorder( BorderedComponent owner, DockController controller, Dockable dockable ){
            return new OwnedRectEclipseBorder( owner, controller, true );
        }
	};
	
	private MatteBorder contentBorder = new MatteBorder(2, 2, 2, 2, Color.BLACK);

	
	public RectGradientPainter( EclipseTabPane pane, Dockable dockable ){
	    super( pane, dockable );
	    
		setOpaque( false );
		
        update();
        updateBorder();
	}
		
	public Insets getOverlap( TabComponent other ){
		return new Insets( 0, 0, 0, 0 );
	}
	
	@Override
	public void updateFocus(){
		update();
		updateBorder();
		updateFont();
	}
	
	@Override
	protected void updateOrientation(){
		update();
	}
	
	@Override
	protected void updateSelected(){
		update();
		updateBorder();
		updateFont();
	}
	
	@Override
	protected void updateColors(){
		update();
	}
	
	@Override
	protected void updateEnabled(){
		updateBorder();
		update();
	}
	
	@Override
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}
	
	/**
	 * Updates the insets, colors and icon of this tab depending on orientation,
	 * selection and focus.
	 */
	public void update(){
		Insets labelInsets = null;
		Insets buttonInsets = null;
		
		switch( getOrientation() ){
			case TOP_OF_DOCKABLE:
			case BOTTOM_OF_DOCKABLE:
				labelInsets = new Insets( 3, 5, 3, 2 );
				buttonInsets = new Insets( 1, 2, 1, 5 );
				break;
			case LEFT_OF_DOCKABLE:
			case RIGHT_OF_DOCKABLE:
				labelInsets = new Insets( 5, 3, 2, 3 );
				buttonInsets = new Insets( 2, 1, 5, 1 );
				break;
		}
		
		getLabel().setForeground( getTextColor() );
		setLabelInsets( labelInsets );
		setButtonInsets( buttonInsets );
		
		revalidate();
		repaint();
	}
	
	private Color getTextColor(){
		boolean focusTemporarilyLost = isFocusTemporarilyLost();
		
		if( !isEnabled() ){
			return colorStackTabTextDisabled.value();
		}
		else  if( isFocused() && !focusTemporarilyLost ){
            return colorStackTabTextSelectedFocused.value();
        }
        else if (isFocused() && focusTemporarilyLost) {
        	return colorStackTabTextSelectedFocusLost.value();
        }
        else if( isSelected() ){
        	return colorStackTabTextSelected.value();
        }
        else{
        	return colorStackTabText.value();
        }
	}
	
	@Override
	public void updateBorder(){
		Color color2;
		
		Window window = SwingUtilities.getWindowAncestor( getComponent() );
		boolean focusTemporarilyLost = false;
		
		if( window != null ){
			focusTemporarilyLost = !window.isActive();
		}
		
		if( !isEnabled() ){
			color2 = colorStackTabBorderDisabled.value();
		}
		else if( isSelected() ){
            if( isFocused() ){
                if( focusTemporarilyLost )
                    color2 = colorStackTabBorderSelectedFocusLost.value();
                else
                    color2 = colorStackTabBorderSelectedFocused.value();
            }
            else
                color2 = colorStackTabBorderSelected.value();
        }
        else
            color2 = colorStackTabBorder.value();
		
		// set border around tab content
		if (!color2.equals(contentBorder.getMatteColor())) {
			contentBorder = new MatteBorder(2, 2, 2, 2, color2);
		}
		
	    EclipseTabPane pane = getPane();
	    if( pane != null ){
	    	int index = getDockableIndex();
	    	if( index >= 0 ){
	    		pane.setContentBorderAt( index, contentBorder );
	    	}
	    }
	}
	
	@Override
	public void paintBackground( Graphics g ){
		int height = getHeight(), width = getWidth();
		Graphics2D g2d = (Graphics2D) g;
		Color lineColor = colorStackBorder.value();
		Color color1, color2;
		boolean focusTemporarilyLost = isFocusTemporarilyLost();
		boolean isSelected = isSelected();
		
		TabPlacement orientation = getOrientation();
		
		if( !isEnabled() ){
			color1 = colorStackTabTopDisabled.value();
			color2 = colorStackTabBottomDisabled.value();
		}
		else if( isFocused() && !focusTemporarilyLost ){
            color1 = colorStackTabTopSelectedFocused.value();
            color2 = colorStackTabBottomSelectedFocused.value();
        }
        else if (isFocused() && focusTemporarilyLost) {
            color1 = colorStackTabTopSelectedFocusLost.value();
            color2 = colorStackTabBottomSelectedFocusLost.value();
        }
        else if( isSelected ){
            color1 = colorStackTabTopSelected.value();
            color2 = colorStackTabBottomSelected.value();
        }
        else{
            color1 = colorStackTabTop.value();
            color2 = colorStackTabBottom.value();
        }
        
        if( orientation == TabPlacement.BOTTOM_OF_DOCKABLE || orientation == TabPlacement.RIGHT_OF_DOCKABLE ){
        	Color temp = color1;
        	color1 = color2;
        	color2 = temp;
        }
		
        GradientPaint gradient = null;
        if( !color1.equals( color2 )){
        	if( orientation.isHorizontal() ){
        		gradient = new GradientPaint( 0, 0, color1, 0, height, color2 );		
        	}
        	else{
        		gradient = new GradientPaint( 0, 0, color1, width, 0, color2 );
        	}
        }
        
		int tabIndex = getTabIndex();
		
		g2d.setColor( lineColor );
		Paint old = g2d.getPaint();
        if( gradient != null )
            g2d.setPaint(gradient);
        else
            g2d.setPaint( color1 );
		
		if (isSelected) {
			paintSelected( g2d, tabIndex, old );
		}
		else{
			if( getTransparency() != Transparency.TRANSPARENT ){
				switch( orientation ){
					case TOP_OF_DOCKABLE:
						g.fillRect( 0, 0, getWidth(), getHeight()-1 );
						break;
					case BOTTOM_OF_DOCKABLE:
						g.fillRect( 0, 1, getWidth(), getHeight()-1 );
						break;
					case LEFT_OF_DOCKABLE:
						g.fillRect( 0, 0, getWidth()-1, getHeight() );
						break;
					case RIGHT_OF_DOCKABLE:
						g.fillRect( 1, 0, getWidth()-1, getHeight() );
						break;
				}
			}
		}

	    g2d.setPaint(old);
	}
	
	@Override
	public void paintForeground( Graphics g ){
		// draw separator lines
		if (!isSelected() && !isNextTabSelected() ) {
			Color lineColor = colorStackBorder.value();
			TabPlacement orientation = getOrientation();
			int width = getWidth();
			int height = getHeight();
			
			g.setColor(lineColor);
			switch( orientation ){
				case TOP_OF_DOCKABLE:
				case BOTTOM_OF_DOCKABLE:
					g.drawLine(width - 1, 0, width - 1, height);
					break;
				case LEFT_OF_DOCKABLE:
				case RIGHT_OF_DOCKABLE:
					
					break;
			}
		}
	}
	
	private void paintSelected( Graphics2D g2d, int tabIndex, Paint normalBackground ){
		TabPlacement orientation = getOrientation();
		int width = getWidth();
		int height = getHeight();
		
		switch( orientation ){
			case TOP_OF_DOCKABLE:
				if( getTransparency() != Transparency.TRANSPARENT ){
					g2d.fillRect( 1, 0, width - 2, height );
				}
				g2d.drawLine( 0, 1, 0, height );
				g2d.setPaint( normalBackground );
				// left
				if (tabIndex != 0 || getX() > 1) {
					g2d.drawLine(1, 0, 1, 0);
					g2d.drawLine(0, 1, 0, height);
				}
				// right
				g2d.drawLine( width - 2, 0, width - 2, 0 );
				g2d.drawLine( width - 1, 1, width - 1, height );
				break;
			case BOTTOM_OF_DOCKABLE:
				if( getTransparency() != Transparency.TRANSPARENT ){
					g2d.fillRect( 1, 0, width - 2, height );
				}
				g2d.drawLine( 0, height-2, 0, 0 );
				g2d.setPaint( normalBackground );
				// left
				if (tabIndex != 0 || getX() > 1) {
					g2d.drawLine(1, height-1, 1, height-1 );
					g2d.drawLine(0, height-2, 0, 0 );
				}
				// right
				g2d.drawLine( width - 2, height-1, width - 2, height-1 );
				g2d.drawLine( width - 1, height-2, width - 1, 0 );
				break;
			case LEFT_OF_DOCKABLE:
				if( getTransparency() != Transparency.TRANSPARENT ){
					g2d.fillRect( 0, 1, width, height-2 );
				}
				g2d.drawLine( 1, 0, width, 0 );
				g2d.setPaint( normalBackground );
				// left
				if (tabIndex != 0 || getY() > 1) {
					g2d.drawLine( 0, 1, 0, 1 );
					g2d.drawLine( 1, 0, width, 0 );
				}
				// right
				g2d.drawLine( 0, height-2, 0, height-2 );
				g2d.drawLine( 1, height-1, width, height-1 );
				break;
			case RIGHT_OF_DOCKABLE:
				if( getTransparency() != Transparency.TRANSPARENT ){
					g2d.fillRect( 0, 1, width, height-2 );
				}
				g2d.drawLine( 0, 0, width-1, 0 );
				g2d.setPaint( normalBackground );
				// left
				if (tabIndex != 0 || getY() > 1) {
					g2d.drawLine( width-1, 1, width-1, 1 );
					g2d.drawLine( 0, 0, width-2, 0 );
				}
				// right
				g2d.drawLine( width-1, height-2, width-1, height-2 );
				g2d.drawLine( 0, height-1, width-2, height-1 );
				break;
		}
	}
}
