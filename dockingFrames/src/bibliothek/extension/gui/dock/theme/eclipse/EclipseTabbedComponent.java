package bibliothek.extension.gui.dock.theme.eclipse;

import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.eclipse.rex.RexTabbedComponent;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.ShapedGradientPainter;

/**
 * @author Janni Kovacs
 */
public class EclipseTabbedComponent extends RexTabbedComponent {
	private EclipseStackDockComponent eclipseStackDockComponent;

	public EclipseTabbedComponent(EclipseStackDockComponent eclipseStackDockComponent) {
		this.eclipseStackDockComponent = eclipseStackDockComponent;
		setBorder(new EclipseBorder());
		ShapedGradientPainter painter = new ShapedGradientPainter();
//		painter.setPaintIconWhenInactive(true);
		setTabPainter(painter);
	}

	public JComponent getTabStrip() {
		return tabStrip;
	}
}
