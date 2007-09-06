package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.eclipse.rex.RexTabbedComponent;

/**
 * @author Janni Kovacs
 */
public class SmallTabPainter extends JComponent implements TabPainter {

	private Tab tab;

	private RexTabbedComponent comp;
	private int index;
	private boolean selected;

	public SmallTabPainter() {
	}

	public JComponent getTabComponent(RexTabbedComponent component, Tab tab, int index, boolean isSelected,
									  boolean hasFocus) {
		this.comp = component;
		this.tab = tab;
		this.index = index;
		this.selected = isSelected;
		return this;
	}

	public Border getContentBorder() {
		return null;
	}

	public void paintTabStrip(RexTabbedComponent component, Graphics g) {
		int y = 0;
		Rectangle rectangle = g.getClipBounds();
		if (component.getTabPlacement() == RexTabbedComponent.TOP) {
			y = rectangle.height - 1;
		}
		g.setColor(UIManager.getColor("Separator.foreground"));
		g.drawLine(0, y, rectangle.width, y);
	}

	@Override
	public Dimension getPreferredSize() {
		FontRenderContext frc = new FontRenderContext(null, false, false);
		Rectangle2D bounds = UIManager.getFont("Label.font").getStringBounds(tab.getTitle(), frc);
		return new Dimension((int) bounds.getWidth() + 15, (int) bounds.getHeight() + 6);		
	}

	@Override
	public void paint(Graphics g) {
		Rectangle bounds = getBounds();
		g.setColor(Color.BLACK);
		g.drawString(tab.getTitle(), bounds.width / 2 - g.getFontMetrics().stringWidth(tab.getTitle()) / 2 - 1,
				bounds.height / 2 + g.getFontMetrics().getAscent() / 2 - 1);
		g.setColor(UIManager.getColor("Separator.foreground"));
		if (index != 0 && !selected && index != comp.indexOf(comp.getSelectedTab()) + 1) {
			Color background = UIManager.getColor("Label.background");
			Color c1 = g.getColor(), c2 = background;
			if (comp.getTabPlacement() == RexTabbedComponent.BOTTOM) {
				c2 = c1;
				c1 = background;
			}
			GradientPaint gp = new GradientPaint(0, 1, c1, 0, bounds.height, c2);
			((Graphics2D) g).setPaint(gp);
			g.drawLine(0, 2, 0, bounds.height);
		}
		if (selected) {
			if (comp.getTabPlacement() == RexTabbedComponent.BOTTOM) {
				if (index == 0)
					g.translate(-1, 0);
				g.setColor(UIManager.getColor("Separator.foreground"));
				g.drawRoundRect(0, -1, bounds.width - 1, bounds.height, 2, 2);
				g.setColor(UIManager.getColor("Separator.background"));
				g.drawRoundRect(1, 0, bounds.width - 3, bounds.height - 1, 2, 2);
				if (index == 0) {
					g.drawLine(1, 1, 1, bounds.height);
				}
				g.drawLine(1, 1, bounds.width - 2, 1);
				g.setColor(SystemColor.controlShadow);
				g.drawLine(0, bounds.y, comp.getWidth(), bounds.y);
			} else {
				g.setColor(UIManager.getColor("Separator.foreground"));
				g.drawRoundRect(0, 1, bounds.width - 1, bounds.height + 1, 2, 2);
				g.setColor(UIManager.getColor("Separator.background"));
				g.drawRoundRect(1, 1, bounds.width - 3, bounds.height - 1, 2, 2);
				g.drawLine(1, bounds.height - 1, bounds.width - 2, bounds.height - 1);
				if (index == 0)
					g.drawLine(1, 0, 1, bounds.height);
			}
		}
	}
}
