package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import bibliothek.extension.gui.dock.theme.eclipse.rex.RexSystemColor;
import bibliothek.extension.gui.dock.theme.eclipse.rex.RexTabbedComponent;

/**
 * @author Janni Kovacs
 */
public class RectGradientPainter extends JComponent implements TabPainter {
	private boolean paintIconWhenInactive = false;

//	private EditableMatteBorder contentBorder = new EditableMatteBorder(2, 2, 2, 2);
	private MatteBorder contentBorder = new MatteBorder(2, 2, 2, 2, Color.BLACK);

	private boolean hasFocus;
	private boolean isSelected;
	private RexTabbedComponent comp;
	private Tab tab;
	private int tabIndex;

	public JComponent getTabComponent(RexTabbedComponent component, Tab tab, int index, boolean isSelected,
									  boolean hasFocus) {
		this.hasFocus = hasFocus;
		this.isSelected = isSelected;
		this.comp = component;
		this.tab = tab;
		this.tabIndex = index;
		return this;
	}


	@Override
	public Dimension getPreferredSize() {
		FontRenderContext frc = new FontRenderContext(null, false, false);
		Rectangle2D bounds = UIManager.getFont("Label.font").getStringBounds(tab.getTitle(), frc);
		int width = 5 + (int) bounds.getWidth() + 5;
		int height = 23;
		if ((paintIconWhenInactive || isSelected) && tab.getIcon() != null)
			width += tab.getIcon().getIconWidth() + 5;
		return new Dimension(width, height);
	}

	public Border getContentBorder() {
		return contentBorder;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int height = getHeight(), width = getWidth();
		Graphics2D g2d = (Graphics2D) g;
		Color lineColor = SystemColor.controlShadow;
		Color color1 = RexSystemColor.getActiveTitleColor(),
			  color2 = RexSystemColor.getActiveTitleColorGradient();
		boolean focusTemporarilyLost = KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.getActiveWindow() != SwingUtilities.getWindowAncestor(comp);
		if (hasFocus && focusTemporarilyLost) {
			color2 = RexSystemColor.getInactiveTitleColor();
			color1 = color2;
		} else if (!hasFocus) {
			color1 = Color.WHITE;
			color2 = UIManager.getColor("Panel.background");
		}
		GradientPaint selectedGradient = new GradientPaint(0, 0, color1, 0, height, color2);
		if (!color2.equals(contentBorder.getMatteColor())) {
			contentBorder = new MatteBorder(2, 2, 2, 2, color2);
		//	contentBorder.setColor(color2);
		}
		g.setColor(lineColor);
		if (isSelected) {
			Paint old = g2d.getPaint();
			g2d.setPaint(selectedGradient);
			g.fillRect(0, 0, width - 1, height);
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
			g.setColor(getBackground());
			g.drawLine(0, 0, 0, 0);
			g.drawLine(width, 0, width, 0);
		}

		// draw icon
		int iconOffset = 0;
		if (isSelected || paintIconWhenInactive) {
			Icon i = tab.getIcon();
			if (i != null) {
				i.paintIcon(comp, g, 5, 4);
				iconOffset = i.getIconWidth() + 5;
			}
		}

		// draw separator lines
		if (!isSelected && tabIndex != comp.indexOf(comp.getSelectedTab()) - 1) {
			g.setColor(lineColor);
			g.drawLine(width - 1, 0, width - 1, height);
		}

		// draw text
		g.setColor(isSelected && hasFocus ? SystemColor.activeCaptionText : SystemColor.controlText);
		g.drawString(tab.getTitle(), 5 + iconOffset, height / 2 + g.getFontMetrics().getHeight() / 2 - 2);
	}

	public void paintTabStrip(RexTabbedComponent component, Graphics g) {
		Rectangle bounds = g.getClipBounds();
		int h = bounds.height;
		int selectedIndex = component.getSelectedIndex();
		if (selectedIndex != -1) {
			Rectangle selectedBounds = component.getBoundsAt(selectedIndex);
			int to = selectedBounds.x;
			int from = selectedBounds.x + selectedBounds.width;
			int end = bounds.width;
			Color lineColor = SystemColor.controlShadow;
			g.setColor(lineColor);
			int y = h - 1;
			g.drawLine(-1, y, to - 1, y);
			g.drawLine(from, y, end, y);
		}
	}
}
