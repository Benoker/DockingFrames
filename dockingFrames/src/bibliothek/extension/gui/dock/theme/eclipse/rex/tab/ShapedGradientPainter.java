package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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
public class ShapedGradientPainter extends JComponent implements TabPainter {
	/*	private static class DefaultCloseIcon implements Icon {

		 private Color c;

		 public DefaultCloseIcon() {
			 this(Color.WHITE);
		 }

		 public DefaultCloseIcon(Color c) {
			 this.c = c;
		 }

		 public void paintIcon(Component c, Graphics g, int x, int y) {
			 Graphics2D g2d = (Graphics2D) g;
			 g2d.setColor(this.c);
			 g2d.setStroke(new BasicStroke(2f));
			 g2d.drawLine(x, y, x + 12, y + 12);
			 g2d.drawLine(x, y + 12, x + 12, y);
		 }

		 public int getIconWidth() {
			 return 12;
		 }

		 public int getIconHeight() {
			 return 12;
		 }
	 }

	 private MatteBorder areaBorder = BorderFactory
			 .createMatteBorder(2, 2, 2, 2, RexSystemColor.getActiveTitleColorGradient());
	 private Dimension chevronRect = new Dimension(27, 18);
 */

	private boolean paintIconWhenInactive = false;

	private boolean hasFocus;
	private boolean isSelected;
	private RexTabbedComponent comp;
	private Tab tab;
	private int tabIndex;
	private MatteBorder contentBorder = new MatteBorder(2, 2, 2, 2, Color.BLACK);

	public JComponent getTabComponent(RexTabbedComponent component, Tab tab, int index, boolean isSelected,
									  boolean hasFocus) {
		this.hasFocus = hasFocus;
		this.isSelected = isSelected;
		this.comp = component;
		this.tab = tab;
		this.tabIndex = index;
		int iconx = getPreferredSize().width - 21;
		//	putClientProperty(RexTabbedComponent.CLOSE_ICON_LOCATION_PROPERTY, isSelected ? iconx : null);
		return this;
	}

	public Border getContentBorder() {
		return contentBorder;
	}

	@Override
	public Dimension getPreferredSize() {
		FontRenderContext frc = new FontRenderContext(null, false, false);
		Rectangle2D bounds = UIManager.getFont("Label.font").getStringBounds(tab.getTitle(), frc);
		int width = 5 + (int) bounds.getWidth() + 5;
		int height = 23;
		if ((paintIconWhenInactive || isSelected) && tab.getIcon() != null)
			width += tab.getIcon().getIconWidth() + 5;
		//	if (isSelected)
		//		width += 5;
		return new Dimension(width, height);
	}


	@Override
	public Insets getInsets() {
		return new Insets(0, isSelected ? 10 : 0, 0, isSelected ? 25 : 0);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());//getBounds();
		int x = 0, y = 0;
		int w = bounds.width, h = bounds.height;
		Graphics2D g2d = (Graphics2D) g;
		Color lineColor = SystemColor.controlShadow;

//		GradientPaint selectedGradient = new GradientPaint(x, y, SystemColor.activeCaption, x, y + height,
//				SystemColor.activeCaption.darker());
		// Gradient for selected tab
		Color color1 = RexSystemColor.getActiveTitleColor(), color2 = RexSystemColor.getActiveTitleColorGradient();
		boolean focusTemporarilyLost = KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.getActiveWindow() != SwingUtilities.getWindowAncestor(comp);
		if (hasFocus && focusTemporarilyLost) {
			color2 = RexSystemColor.getInactiveTitleColor();
			color1 = color2;
		} else if (!hasFocus) {
			color1 = Color.WHITE;
			color2 = UIManager.getColor("Panel.background");
		}
		GradientPaint selectedGradient = new GradientPaint(x, y, color1, x, y + h, color2);

//		g.setColor(selectedGradient.getColor2());
//		g.fillRect(0, h - 2, comp.getWidth(), 2);

		// set border around tab content
		if (!color2.equals(contentBorder.getMatteColor())) {
			contentBorder = new MatteBorder(2, 2, 2, 2, color2);
		}

//		Component contentArea = comp.getContentArea();
//		if (contentArea instanceof JComponent) {
//			JComponent area = (JComponent) contentArea;
//			if (area.getBorder() != areaBorder) {
//				area.setBorder(areaBorder);
//			}
//		}

		// draw tab if selected
		Paint old = g2d.getPaint();
		if (isSelected) {
			// draw line at the bottom
			g.setColor(lineColor);
			//	g.drawLine(0, h - 1, w, h - 1);
			//	g.drawLine(x + w, h - 1, comp.getWidth(), h - 1);
			// draw tab gradient und so
			//		int[] xpoints = {0, 0, 1, 1, 2, 3, 4, 5, w - 27, w - 24, w - 21, w - 15, w, 	w + 12};
			//		int[] ypoints = {h, 5, 4, 3, 2, 1, 1, 0, 0, 1, 2, 5, 					h - 6, h - 1};
			//		int[] xpoints = {0, 0, 1, 1, 2, 3, 4, 5, w - 20, w - 10, w + 5, w + 15};
			//		int[] ypoints = {h, 5, 4, 3, 2, 1, 1, 0, 0, 5, h - 5, h};
			g.setColor(lineColor);
			//	Polygon outer = extendPolygon(xpoints, ypoints, 5);
			//		Polygon inner = new Polygon(xpoints, ypoints, xpoints.length);
			final int[] TOP_LEFT_CORNER = new int[]{0, 6, 1, 5, 1, 4, 4, 1, 5, 1, 6, 0};
			int tabHeight = 24;
			int d = tabHeight - 12;
			int[] curve = new int[]{0, 0, 0, 1, 2, 1, 3, 2, 5, 2, 6, 3, 7, 3, 9, 5, 10, 5,
					11, 6, 11 + d, 6 + d,
					12 + d, 7 + d, 13 + d, 7 + d, 15 + d, 9 + d, 16 + d, 9 + d, 17 + d, 10 + d, 19 + d, 10 + d,
					20 + d,
					11 + d, 22 + d, 11 + d, 23 + d, 12 + d};
			int rightEdge = Math.min(x + w - 20, comp.getWidth()); // can be replaced by: x + w - 20
			int curveWidth = 26 + d;
			int curveIndent = curveWidth / 3;
			int[] left = TOP_LEFT_CORNER;
			int[] right = curve;
			int[] shape = new int[left.length + right.length + 8];
			int index = 0;
			int height = 23;
			shape[index++] = x; // first point repeated here because below we reuse shape to draw outline
			shape[index++] = y + height + 1;
			shape[index++] = x;
			shape[index++] = y + height + 1;
			for (int i = 0; i < left.length / 2; i++) {
				shape[index++] = x + left[2 * i];
				shape[index++] = y + left[2 * i + 1];
			}
			for (int i = 0; i < right.length / 2; i++) {
				shape[index++] = rightEdge - curveIndent + right[2 * i];
				shape[index++] = y + right[2 * i + 1];
			}
			shape[index++] = rightEdge + curveWidth - curveIndent;
			shape[index++] = y + height + 1;
			shape[index++] = rightEdge + curveWidth - curveIndent;
			shape[index++] = y + height + 1;
			Polygon inner = makePolygon(shape);
			Polygon outer = copyPolygon(inner);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// draw outline from 0/0 or -1/0 resp.
			if (tabIndex == 0)
				outer.translate(-1, 0);
			g.fillPolygon(outer);
			// draw outline from 2/0
			outer.translate(2, 0);
			g.fillPolygon(outer);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2d.setPaint(selectedGradient);
			// draw inner gradient from 1/0 or 0/0 resp.
			if (tabIndex != 0)
				inner.translate(1, 0);
			g.fillPolygon(inner);
		}
		g2d.setPaint(old);

		// draw icon
		int iconOffset = 0;
		if (isSelected || paintIconWhenInactive) {
			Icon i = tab.getIcon();
			if (i != null) {
				i.paintIcon(comp, g, 5, 4);
				iconOffset = i.getIconWidth() + 5;
			}
		}

		// draw close label

		// draw separator lines
		if (!isSelected && tabIndex != comp.indexOf(comp.getSelectedTab()) - 1) {
			g.setColor(lineColor);
			g.drawLine(w - 1, 0, w - 1, h);
		}

		// draw text
		g.setColor(isSelected && hasFocus ? SystemColor.activeCaptionText : SystemColor.controlText);
		g.drawString(tab.getTitle(), x + 5 + iconOffset, h / 2 + g.getFontMetrics().getHeight() / 2 - 2);
// Debug:
//		g.setColor(Color.GREEN);
//		g.drawLine(x, y, x, y + getHeight());
//		g.drawRect(x, y, width, height - 1);
//		g.drawLine(x, 5, width, 5);
//		g.drawLine(x, height - 5, width, height - 5);

	}

	private Polygon copyPolygon(Polygon p) {
		int[] xpoints = new int[p.npoints];
		int[] ypoints = new int[p.npoints];
		System.arraycopy(p.xpoints, 0, xpoints, 0, xpoints.length);
		System.arraycopy(p.ypoints, 0, ypoints, 0, ypoints.length);
		return new Polygon(xpoints, ypoints, xpoints.length);
	}

	private Polygon makePolygon(int[] shape) {
		int[] xpoints = new int[shape.length / 2];
		int[] ypoints = new int[shape.length / 2];
		for (int i = 0, j = 0; i < shape.length - 1; i += 2, j++) {
			int x = shape[i];
			int y = shape[i + 1];
			xpoints[j] = x;
			ypoints[j] = y;
		}
		return new Polygon(xpoints, ypoints, xpoints.length);
	}

	public void paintTabStrip(RexTabbedComponent component, Graphics g) {
//		Color noFocusBorderColor = UIManager.getColor("Panel.background");
//		if (noFocusBorderColor.equals(contentBorder.getMatteColor())) {
		Rectangle bounds = g.getClipBounds();
		int h = bounds.height;
		int selectedIndex = component.getSelectedIndex();
		if (selectedIndex != -1) {
			Rectangle selectedBounds = component.getBoundsAt(selectedIndex);
			int to = selectedBounds.x;
			int from = selectedBounds.x + selectedBounds.width + 8;
			int end = bounds.width;
			Color lineColor = SystemColor.controlShadow;
			g.setColor(lineColor);
			int y = h - 1;
			if (to != 0)
				g.drawLine(0, y, to, y);
			g.drawLine(from, y, end, y);
		}
//		}
	}

/*	private boolean paintIconWhenInactive = false;
   private boolean showCloseIconWhenInactive = false;

   private JLabel closeLabel = new JLabel();
   private Icon closeIcon;
   private Icon closeHoverIcon;

   public ShapedGradientPainter() {
	   setCloseIcon(new DefaultCloseIcon());
   }

   public Dimension getPreferredSize(RexTabbedComponent component, Tab tab, int index, boolean isSelected) {
	   FontRenderContext frc = new FontRenderContext(null, false, false);
	   Rectangle2D bounds = UIManager.getFont("Label.font").getStringBounds(tab.getTitle(), frc);
	   int width = 5 + (int) bounds.getWidth() + 5;
	   int height = 23;
	   if ((paintIconWhenInactive || isSelected) && tab.getIcon() != null)
		   width += tab.getIcon().getIconWidth() + 5;
	   if (isSelected)
		   width += 30;
	   return new Dimension(width, height);
   }

   public Dimension getChevronSize(RexTabbedComponent component) {
	   return chevronRect;
   }

   public void paintTab(Graphics g, Rectangle bounds, RexTabbedComponent comp, Tab tab, int tabIndex,
						boolean isSelected, boolean hasFocus, boolean focusTemporarilyLost) {
	   int x = bounds.x, y = bounds.y, w = bounds.width, h = bounds.height;
	   Graphics2D g2d = (Graphics2D) g;
	   Color lineColor = SystemColor.controlShadow;

//		GradientPaint selectedGradient = new GradientPaint(x, y, SystemColor.activeCaption, x, y + height,
//				SystemColor.activeCaption.darker());
	   // Gradient for selected tab
	   Color color1 = RexSystemColor.getActiveTitleColor(), color2 = RexSystemColor.getActiveTitleColorGradient();
	   if (hasFocus && focusTemporarilyLost) {
		   color1 = color2;
	   } else if (!hasFocus) {
		   color1 = Color.WHITE;
		   color2 = UIManager.getColor("Panel.background");
	   }
	   GradientPaint selectedGradient = new GradientPaint(x, y, color1, x, y + h, color2);

//		g.setColor(selectedGradient.getColor2());
//		g.fillRect(0, h - 2, comp.getWidth(), 2);

	   // set border around tab content
	   if (!areaBorder.getMatteColor().equals(color2)) {
		   areaBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, color2);
	   }

	   Component contentArea = comp.getContentArea();
	   if (contentArea instanceof JComponent) {
		   JComponent area = (JComponent) contentArea;
		   if (area.getBorder() != areaBorder) {
			   area.setBorder(areaBorder);
		   }
	   }

	   // draw tab if selected
	   Paint old = g2d.getPaint();
	   if (isSelected) {
		   // draw line at the bottom
		   g.setColor(lineColor);
		   g.drawLine(0, h - 1, x, h - 1);
		   g.drawLine(x + w, h - 1, comp.getWidth(), h - 1);
		   // draw tab gradient und so
		   //		int[] xpoints = {0, 0, 1, 1, 2, 3, 4, 5, w - 27, w - 24, w - 21, w - 15, w, 	w + 12};
		   //		int[] ypoints = {h, 5, 4, 3, 2, 1, 1, 0, 0, 1, 2, 5, 					h - 6, h - 1};
		   //		int[] xpoints = {0, 0, 1, 1, 2, 3, 4, 5, w - 20, w - 10, w + 5, w + 15};
		   //		int[] ypoints = {h, 5, 4, 3, 2, 1, 1, 0, 0, 5, h - 5, h};
		   g.setColor(lineColor);
		   //	Polygon outer = extendPolygon(xpoints, ypoints, 5);
		   //		Polygon inner = new Polygon(xpoints, ypoints, xpoints.length);
		   final int[] TOP_LEFT_CORNER = new int[]{0, 6, 1, 5, 1, 4, 4, 1, 5, 1, 6, 0};
		   int tabHeight = 24;
		   int d = tabHeight - 12;
		   int[] curve = new int[]{0, 0, 0, 1, 2, 1, 3, 2, 5, 2, 6, 3, 7, 3, 9, 5, 10, 5,
				   11, 6, 11 + d, 6 + d,
				   12 + d, 7 + d, 13 + d, 7 + d, 15 + d, 9 + d, 16 + d, 9 + d, 17 + d, 10 + d, 19 + d, 10 + d,
				   20 + d,
				   11 + d, 22 + d, 11 + d, 23 + d, 12 + d};
		   int rightEdge = Math.min(x + w - 10, comp.getWidth());
		   int curveWidth = 26 + d;
		   int curveIndent = curveWidth / 3;
		   int[] left = TOP_LEFT_CORNER;
		   int[] right = curve;
		   int[] shape = new int[left.length + right.length + 8];
		   int index = 0;
		   int height = 23;
		   shape[index++] = x; // first point repeated here because below we reuse shape to draw outline
		   shape[index++] = y + height + 1;
		   shape[index++] = x;
		   shape[index++] = y + height + 1;
		   for (int i = 0; i < left.length / 2; i++) {
			   shape[index++] = x + left[2 * i];
			   shape[index++] = y + left[2 * i + 1];
		   }
		   for (int i = 0; i < right.length / 2; i++) {
			   shape[index++] = rightEdge - curveIndent + right[2 * i];
			   shape[index++] = y + right[2 * i + 1];
		   }
		   shape[index++] = rightEdge + curveWidth - curveIndent;
		   shape[index++] = y + height + 1;
		   shape[index++] = rightEdge + curveWidth - curveIndent;
		   shape[index++] = y + height + 1;
		   Polygon inner = makePolygon(shape);
		   Polygon outer = copyPolygon(inner);
		   g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		   outer.translate(-1, 0);
		   g.fillPolygon(outer);
		   outer.translate(2, 0);
		   g.fillPolygon(outer);
		   g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		   g2d.setPaint(selectedGradient);
		   //	inner.translate(x, y);
		   g.fillPolygon(inner);
	   }
	   g2d.setPaint(old);

	   // draw icon
	   int iconOffset = 0;
	   if (isSelected || paintIconWhenInactive) {
		   Icon i = tab.getIcon();
		   if (i != null) {
			   i.paintIcon(comp, g, x + 5, y + 4);
			   iconOffset = i.getIconWidth() + 5;
		   }
	   }

	   // draw close label
	   if (tab.isClosable() && (isSelected || showCloseIconWhenInactive)) {
		   SwingUtilities.paintComponent(g, closeLabel, comp, x + w - 25, 3, 20, 20);
	   }

	   // draw separator lines
	   if (!isSelected && tabIndex != comp.indexOf(comp.getSelectedTab()) - 1) {
		   g.setColor(lineColor);
		   g.drawLine(x + w, y, x + w, h);
	   }

	   // draw text
	   g.setColor(isSelected && hasFocus ? SystemColor.activeCaptionText : SystemColor.controlText);
	   g.drawString(tab.getTitle(), x + 5 + iconOffset, h / 2 + g.getFontMetrics().getHeight() / 2 - 2);
// Debug:
//		g.setColor(Color.GREEN);
//		g.drawRect(x, y, width, height - 1);
//		g.drawLine(x, 5, width, 5);
//		g.drawLine(x, height - 5, width, height - 5);
   }

   public void paintChevron(Graphics gc, Rectangle bounds, RexTabbedComponent component, int state) {
	   int x = bounds.x + 3;
	   int y = bounds.y + 10;
	   int count = component.getTabCount();
	   String chevronString = count > 99 ? "99+" : String.valueOf(count);
	   switch (state) {
		   case NORMAL: {
			   Color chevronBorder = UIManager.getColor("Button.foreground");
			   gc.setColor(chevronBorder);
			   gc.drawLine(x, y, x + 2, y + 2);
			   gc.drawLine(x + 2, y + 2, x, y + 4);
			   gc.drawLine(x + 1, y, x + 3, y + 2);
			   gc.drawLine(x + 3, y + 2, x + 1, y + 4);
			   gc.drawLine(x + 4, y, x + 6, y + 2);
			   gc.drawLine(x + 6, y + 2, x + 5, y + 4);
			   gc.drawLine(x + 5, y, x + 7, y + 2);
			   gc.drawLine(x + 7, y + 2, x + 4, y + 4);
			   gc.drawString(chevronString, x + 7, y + 3);
			   break;
		   }
		   case HOT: {
			   gc.setColor(UIManager.getColor("Button.highlight"));
			   gc.fillRoundRect(x, 1, chevronRect.width, chevronRect.height, 6, 6);
			   gc.setColor(UIManager.getColor("Button.foreground"));
			   gc.drawRoundRect(x, 1, chevronRect.width - 1, chevronRect.height - 1, 6, 6);
			   gc.drawLine(x, y, x + 2, y + 2);
			   gc.drawLine(x + 2, y + 2, x, y + 4);
			   gc.drawLine(x + 1, y, x + 3, y + 2);
			   gc.drawLine(x + 3, y + 2, x + 1, y + 4);
			   gc.drawLine(x + 4, y, x + 6, y + 2);
			   gc.drawLine(x + 6, y + 2, x + 5, y + 4);
			   gc.drawLine(x + 5, y, x + 7, y + 2);
			   gc.drawLine(x + 7, y + 2, x + 4, y + 4);
			   gc.drawString(chevronString, x + 7, y + 3);
			   break;
		   }
		   case SELECTED: {
			   gc.setColor(UIManager.getColor("Button.highlight"));
			   gc.fillRoundRect(x, 1, chevronRect.width, chevronRect.height, 6, 6);
			   gc.setColor(UIManager.getColor("Button.foreground"));
			   gc.drawRoundRect(x, 1, chevronRect.width - 1, chevronRect.height - 1, 6, 6);
			   gc.drawLine(x + 1, y + 1, x + 3, y + 3);
			   gc.drawLine(x + 3, y + 3, x + 1, y + 5);
			   gc.drawLine(x + 2, y + 1, x + 4, y + 3);
			   gc.drawLine(x + 4, y + 3, x + 2, y + 5);
			   gc.drawLine(x + 5, y + 1, x + 7, y + 3);
			   gc.drawLine(x + 7, y + 3, x + 6, y + 5);
			   gc.drawLine(x + 6, y + 1, x + 8, y + 3);
			   gc.drawLine(x + 8, y + 3, x + 5, y + 5);
			   gc.drawString(chevronString, x + 8, y + 4);
			   break;
		   }
	   }
   }

   private Polygon copyPolygon(Polygon p) {
	   int[] xpoints = new int[p.npoints];
	   int[] ypoints = new int[p.npoints];
	   System.arraycopy(p.xpoints, 0, xpoints, 0, xpoints.length);
	   System.arraycopy(p.ypoints, 0, ypoints, 0, ypoints.length);
	   return new Polygon(xpoints, ypoints, xpoints.length);
   }

   private Polygon makePolygon(int[] shape) {
	   int[] xpoints = new int[shape.length / 2];
	   int[] ypoints = new int[shape.length / 2];
	   for (int i = 0, j = 0; i < shape.length - 1; i += 2, j++) {
		   int x = shape[i];
		   int y = shape[i + 1];
		   xpoints[j] = x;
		   ypoints[j] = y;
	   }
	   return new Polygon(xpoints, ypoints, xpoints.length);
   }

   private Polygon extendPolygon(int[] xpoints, int[] ypoints, int numPixels) {
	   Polygon p = new Polygon(xpoints, ypoints, xpoints.length);
	   Rectangle bounds = p.getBounds();
	   int[] newXpoints = new int[xpoints.length];
	   int[] newYpoints = new int[ypoints.length];
	   System.arraycopy(xpoints, 0, newXpoints, 0, xpoints.length);
	   System.arraycopy(ypoints, 0, newYpoints, 0, ypoints.length);
	   for (int i = 0; i < newXpoints.length; i++) {
		   int xpoint = newXpoints[i];
		   int ypoint = newYpoints[i];
		   if (xpoint < bounds.width / 2) {
			   newXpoints[i] = xpoint - numPixels;
		   } else if (xpoint > bounds.width / 2) {
			   newXpoints[i] = xpoint + numPixels;
		   }
   //		if (ypoint < bounds.height / 2) {
   //			newYpoints[i] = ypoint - numPixels;
   //		} else if (ypoint > bounds.height / 2) {
   //			newYpoints[i] = ypoint + numPixels;
   //		}
	   }
	   return new Polygon(newXpoints, newYpoints, newXpoints.length);
   }
*/
   public boolean doPaintIconWhenInactive() {
	   return paintIconWhenInactive;
   }

   public void setPaintIconWhenInactive(boolean paintIconWhenInactive) {
	   this.paintIconWhenInactive = paintIconWhenInactive;
   }
/*
   public boolean doShowCloseIconWhenInactive() {
	   return showCloseIconWhenInactive;
   }

   public void setShowCloseIconWhenInactive(boolean showCloseIconWhenInactive) {
	   this.showCloseIconWhenInactive = showCloseIconWhenInactive;
   }

   public void setCloseIcon(Icon i) {
	   closeIcon = i;
	   closeLabel.setIcon(i);
   }

   public void setCloseHoverIcon(Icon i) {
	   closeHoverIcon = i;
   }
   */
}
