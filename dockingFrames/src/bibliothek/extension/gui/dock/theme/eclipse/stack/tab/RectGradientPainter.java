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
import java.awt.KeyboardFocusManager;
import java.awt.Paint;
import java.awt.Window;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import bibliothek.extension.gui.dock.theme.eclipse.RectEclipseBorder;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTabPane;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * @author Janni Kovacs
 */
@ColorCodes({"stack.tab.border", "stack.tab.border.selected", "stack.tab.border.selected.focused", "stack.tab.border.selected.focuslost",
    "stack.tab.top", "stack.tab.top.selected", "stack.tab.top.selected.focused","stack.tab.top.selected.focuslost",
    "stack.tab.bottom", "stack.tab.bottom.selected", "stack.tab.bottom.selected.focused", "stack.tab.bottom.selected.focuslost",
    "stack.tab.text", "stack.tab.text.selected", "stack.tab.text.selected.focused", "stack.tab.text.selected.focuslost",
    "stack.border "})
public class RectGradientPainter extends BaseTabComponent {
	public static final TabPainter FACTORY = new TabPainter(){
		public TabComponent createTabComponent( EclipseTabPane pane, Dockable dockable ){
			return new RectGradientPainter( pane, dockable );
		}
		
		public TabPanePainter createDecorationPainter( EclipseTabPane pane ){
		    return new LinePainter( pane );
		}
		
        public Border getFullBorder( DockController controller, Dockable dockable ) {
            return new RectEclipseBorder( controller, true );
        }
	};
	
	private MatteBorder contentBorder = new MatteBorder(2, 2, 2, 2, Color.BLACK);

	
	public RectGradientPainter( EclipseTabPane pane, Dockable dockable ){
	    super( pane, dockable );
	    
		setLayout( null );
		setOpaque( false );
		
        add( getButtons() );
	}
		
	public Insets getOverlap( TabComponent other ){
		return new Insets( 0, 0, 0, 0 );
	}
	
	public void update(){
		updateBorder();
		revalidate();
	}

	@Override
	public Dimension getPreferredSize() {
	    Dockable dockable = getDockable();
	    boolean isSelected = isSelected();
	    ButtonPanel buttons = getButtons();
	    
		FontRenderContext frc = new FontRenderContext(null, false, false);
		Rectangle2D bounds = getFont().getStringBounds(dockable.getTitleText(), frc);
		int width = 5 + (int) bounds.getWidth() + 5;
		int height = 6 + (int) bounds.getHeight();
		if (( doPaintIconWhenInactive() || isSelected) && dockable.getTitleIcon() != null)
			width += dockable.getTitleIcon().getIconWidth() + 5;
		
		if( isSelected )
		    width += 5;
		
		if( buttons != null ){
			Dimension preferred = buttons.getPreferredSize();
			width += preferred.width+1;
			height = Math.max( height, preferred.height+1 );
		}
		
		return new Dimension(width, height);
	}
	
	@Override
	public void doLayout(){
	    ButtonPanel buttons = getButtons();
	    
		if( buttons != null ){
		    Dockable dockable = getDockable();
		    boolean isSelected = isSelected();
		    
			FontRenderContext frc = new FontRenderContext(null, false, false);
			Rectangle2D bounds = getFont().getStringBounds(dockable.getTitleText(), frc);
			int x = 5 + (int) bounds.getWidth() + 5;
			if (( doPaintIconWhenInactive() || isSelected) && dockable.getTitleIcon() != null)
				x += dockable.getTitleIcon().getIconWidth() + 5;
			
			if( isSelected )
				x += 5;
			
			Dimension preferred = buttons.getPreferredSize();
			int width = Math.min( preferred.width, getWidth()-x );
			int height = Math.min( getHeight()-1, preferred.height );
            
            buttons.setBounds( x, getHeight()-1-height, width-1, height );
		}
	}

	public Border getContentBorder() {
		return contentBorder;
	}
	
	@Override
	protected void updateBorder(){
		Color color2;
		
		Window window = SwingUtilities.getWindowAncestor( getComponent() );
		boolean focusTemporarilyLost = false;
		
		if( window != null ){
			focusTemporarilyLost = !window.isActive();
		}
		
		if( isSelected() ){
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
		/*
        if( getPane() != null )
            getPane(). .updateContentBorder();*/
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int height = getHeight(), width = getWidth();
		Graphics2D g2d = (Graphics2D) g;
		Color lineColor = colorStackBorder.value();
		Color color1, color2, colorText;
		boolean focusTemporarilyLost = KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.getActiveWindow() != SwingUtilities.getWindowAncestor( getComponent() );
		
        if( isFocused() && !focusTemporarilyLost ){
            color1 = colorStackTabTopSelectedFocused.value();
            color2 = colorStackTabBottomSelectedFocused.value();
            colorText = colorStackTabTextSelectedFocused.value();
        }
        else if (isFocused() && focusTemporarilyLost) {
            color1 = colorStackTabTopSelectedFocusLost.value();
            color2 = colorStackTabBottomSelectedFocusLost.value();
            colorText = colorStackTabTextSelectedFocusLost.value();
        }
        else if( isSelected() ){
            color1 = colorStackTabTopSelected.value();
            color2 = colorStackTabBottomSelected.value();
            colorText = colorStackTabTextSelected.value();
        }
        else{
            color1 = colorStackTabTop.value();
            color2 = colorStackTabBottom.value();
            colorText = colorStackTabText.value();
        }
		
		GradientPaint gradient = color1.equals( color2 ) ? null : new GradientPaint(0, 0, color1, 0, height, color2);
		boolean isSelected = isSelected();
		Dockable dockable = getDockable();
		int tabIndex = getTabIndex();
		
		Paint old = g2d.getPaint();
        if( gradient != null )
            g2d.setPaint(gradient);
        else
            g2d.setPaint( color1 );
		
		if (isSelected) {
			g.fillRect(1, 0, width - 2, height);
			g.drawLine( 0, 1, 0, height );
			g2d.setPaint(old);
			// left
			if (tabIndex != 0) {
				g.drawLine(1, 0, 1, 0);
				g.drawLine(0, 1, 0, height);
			}
			// right
			g.drawLine(width - 2, 0, width - 2, 0);
			g.drawLine(width - 1, 1, width - 1, height);
			// overwrite gradient pixels
		}
		else{
		    g.fillRect( 0, 0, getWidth(), getHeight()-1 );
		    
		    g2d.setPaint(old);
		}

		// draw icon
		int iconOffset = 0;
		if (isSelected || doPaintIconWhenInactive()) {
			Icon i = dockable.getTitleIcon();
			if (i != null) {
				i.paintIcon( getComponent(), g, 5, 4);
				iconOffset = i.getIconWidth() + 5;
			}
		}

		// draw separator lines
		if (!isSelected && isNextTabSelected() ) {
			g.setColor(lineColor);
			g.drawLine(width - 1, 0, width - 1, height);
		}

		// draw text
		g.setColor(colorText);
		g.setFont( getFont() );
		g.drawString( dockable.getTitleText(), 5 + iconOffset, height / 2 + g.getFontMetrics().getHeight() / 2 - 2);
	}
}
