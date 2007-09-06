package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.eclipse.rex.RexTabbedComponent;

/**
 * @author Janni Kovacs
 */
public interface TabPainter {

/*	public static final int NORMAL = 0;
	public static final int SELECTED = 1;
	public static final int HOT = 2;

	public Dimension getPreferredSize(RexTabbedComponent component, Tab tab, int index, boolean isSelected);

	public void paintTab(Graphics g, Rectangle bounds, RexTabbedComponent component, Tab tab, int index,
						 boolean isSelected, boolean hasFocus, boolean focusTemporarilyLost);

	public Dimension getChevronSize(RexTabbedComponent component);

	public void paintChevron(Graphics g, Rectangle bounds, RexTabbedComponent component, int state);*/

	public JComponent getTabComponent(RexTabbedComponent component, Tab tab, int index, boolean isSelected,
									  boolean hasFocus);


	public Border getContentBorder();

	public void paintTabStrip(RexTabbedComponent component, Graphics g);
}
