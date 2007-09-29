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
package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;

import sun.security.jca.GetInstance;

import bibliothek.extension.gui.dock.theme.eclipse.rex.RexTabbedComponent;

/**
 * @author Janni Kovacs
 */
public class SmallTabPainter extends JComponent implements TabComponent {
	public static final TabPainter FACTORY = new TabPainter(){
		public TabComponent createTabComponent( RexTabbedComponent component, Tab tab, int index ){
			return new SmallTabPainter( component, tab, index );
		}
		
		public void paintTabStrip( RexTabbedComponent tabbedComponent, Component tabStrip, Graphics g ){	
			int y = 0;
			
			if (tabbedComponent.getTabPlacement() == RexTabbedComponent.TOP) {
				y = tabStrip.getHeight() - 1;
			}
			g.setColor(UIManager.getColor("Separator.foreground"));
			g.drawLine(0, y, tabStrip.getWidth(), y);
		}
	};
	
	private Tab tab;

	private RexTabbedComponent comp;
	private int index;
	private boolean selected;

	public SmallTabPainter( RexTabbedComponent comp, Tab tab, int index ) {
		this.comp = comp;
		this.index = index;
		this.tab = tab;
		setOpaque( false );
		
		setLayout( null );
		
		if( tab.getTabComponent() != null )
			add( tab.getTabComponent() );
	}
	
	public Component getComponent(){
		return this;
	}
	
	public void setSelected( boolean selected ){
		this.selected = selected;
		revalidate();
		repaint();
	}
	
	public void setIndex( int index ){
		this.index = index;
	}

	public Border getContentBorder() {
		return null;
	}

	public void setFocused( boolean focused ){
		// ignore
	}
	
	public void setPaintIconWhenInactive( boolean paint ){
		// ignore
	}
	
	public void update(){
		revalidate();
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize() {
		FontRenderContext frc = new FontRenderContext(null, false, false);
		TextLayout layout = new TextLayout( tab.getTitle(), getFont(), frc );
		Rectangle2D bounds = layout.getBounds();
		//Rectangle2D bounds = getFont().getStringBounds(tab.getTitle(), frc);
		
		Dimension result = new Dimension((int) bounds.getWidth() + 15, (int) bounds.getHeight() + 6);
		
		if( tab.getTabComponent() != null ){
			Dimension preferred = tab.getTabComponent().getPreferredSize();
			result.width += preferred.width;
			result.height = Math.max( result.height, preferred.height );
		}
		
		Insets insets = getInsets();
		
		result.width += insets.left + insets.right;
		result.height += insets.top + insets.bottom;
		
		return result;
	}
	
	@Override
	public void doLayout(){
		if( tab.getTabComponent() != null ){
			FontRenderContext frc = new FontRenderContext(null, false, false);
			TextLayout layout = new TextLayout( tab.getTitle(), getFont(), frc );
			Rectangle2D bounds = layout.getBounds();
			//Rectangle2D bounds = getFont().getStringBounds(tab.getTitle(), frc);
			
			int x = (int)bounds.getWidth();
			x = Math.min( x-5, getWidth() );
			
			Insets insets = getInsets();
			
			tab.getTabComponent().setBounds( x + insets.left, insets.top, 
					getWidth()-x-insets.left-insets.right, 
					getHeight()-insets.top-insets.bottom );
		}
	}

	@Override
	public void paintComponent(Graphics g) {		
		Insets insets = getInsets();
		
		int x = insets.left;
		int y = insets.top;
		int width = getWidth() - insets.left - insets.right;
		int height = getHeight() - insets.top - insets.bottom;
		int textWidth = width;
		
		if( tab.getTabComponent() != null ){
			textWidth -= tab.getTabComponent().getWidth()/2;
		}
		
		g.translate( x, y );
		
		g.setColor(Color.BLACK);
		g.drawString(tab.getTitle(), (textWidth - g.getFontMetrics().stringWidth(tab.getTitle())) / 2 - 1,
				height / 2 + g.getFontMetrics().getAscent() / 2 - 1);
		g.setColor(UIManager.getColor("Separator.foreground"));
		if (index != 0 && !selected && index != comp.indexOf(comp.getSelectedTab()) + 1) {
			Color background = UIManager.getColor("Label.background");
			Color c1 = g.getColor(), c2 = background;
			if (comp.getTabPlacement() == RexTabbedComponent.BOTTOM) {
				c2 = c1;
				c1 = background;
			}
			GradientPaint gp = new GradientPaint(0, 1, c1, 0, height, c2);
			((Graphics2D) g).setPaint(gp);
			g.drawLine(0, 2, 0, height);
		}
		if (selected) {
			if (comp.getTabPlacement() == RexTabbedComponent.BOTTOM) {
				if (index == 0)
					g.translate(-1, 0);
				g.setColor(UIManager.getColor("Separator.foreground"));
				g.drawRoundRect(0, -1, width - 1, height, 2, 2);
				g.setColor(UIManager.getColor("Separator.background"));
				g.drawRoundRect(1, 0, width - 3, height - 1, 2, 2);
				if (index == 0) {
					g.drawLine(1, 1, 1, height);
				}
				g.drawLine(1, 1, width - 2, 1);
				g.setColor(SystemColor.controlShadow);
				g.drawLine(0, 0, comp.getWidth(), 0);
			} else {
				g.setColor(UIManager.getColor("Separator.foreground"));
				g.drawRoundRect(0, 1, width - 1, height + 1, 2, 2);
				g.setColor(UIManager.getColor("Separator.background"));
				g.drawRoundRect(1, 1, width - 3, height - 1, 2, 2);
				g.drawLine(1, height - 1, width - 2, height - 1);
				if (index == 0)
					g.drawLine(1, 0, 1, height);
			}
		}
		g.translate( -x, -y );
	}
}
