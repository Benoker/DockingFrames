package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JPanel;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

/**
 * @author Janni Kovacs
 */
public class NoTitleDisplayer extends DockableDisplayer {

	private JPanel panel;

	public NoTitleDisplayer(DockStation station, Dockable dockable) {
		super(dockable, null);
	}

	private void ensurePanelVisible() {
		if (panel == null) {
			panel = new JPanel(new BorderLayout()) {

				@Override
				public void paint(Graphics g) {
					super.paint(g);
					paintBorder(g);
				}
			};
			panel.setOpaque(false);
			panel.setBorder(new EclipseBorder());
			add(panel);
		}
	}

	@Override
	protected void addDockable(Component component) {
		ensurePanelVisible();
	}

	@Override
	protected void removeDockable(Component component) {
		ensurePanelVisible();
	}

	@Override
	protected Component getComponent(DockTitle title) {
		JPanel p = new JPanel(null);
		p.setBounds(0, 0, 0, 0);
		return p;
	}

	@Override
	protected Component getComponent(Dockable dockable) {
		ensurePanelVisible();
		return panel;
	}

	@Override
	public void setDockable(Dockable dockable) {
		ensurePanelVisible();
		if (getDockable() != null) {
			panel.removeAll();
		}
		super.setDockable(dockable);
		if (dockable != null)
			panel.add(dockable.getComponent());
		invalidate();
	}
}
