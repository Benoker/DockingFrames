package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

/**
 * @author Janni Kovacs
 */
public class EclipseDockableDisplayer extends DockableDisplayer {

	private EclipseStackDockComponent tabs;
	private EclipseTheme theme;
	private DockStation station;

	public EclipseDockableDisplayer(EclipseTheme theme, DockStation station, Dockable dockable) {
		super(null, null);
		this.theme = theme;
		this.station = station;
		setDockable(dockable);
	}

	@Override
	protected void addDockable(Component component) {
		ensureTabComponent();
	}

	@Override
	protected void removeDockable(Component component) {
		ensureTabComponent();
	}

	@Override
	protected Component getComponent(Dockable dockable) {
		ensureTabComponent();
		return tabs;
	}

	private void ensureTabComponent() {
		if (tabs == null) {
			tabs = new EclipseStackDockComponent(theme, station);
			add(tabs);
		}
	}

	@Override
	public void setController(DockController controller) {
		super.setController(controller);
		tabs.setController(controller);
	}

	@Override
	protected Component getComponent(DockTitle title) {
		JPanel p = new JPanel(null);
		p.setBounds(0, 0, 0, 0);
		return p;
	}

	@Override
	public void setDockable(Dockable dockable) {
		if (dockable != null)
			ensureTabComponent();
		if (getDockable() != null) {
			tabs.removeAll();
		}
		super.setDockable(dockable);
		if (dockable != null)
			tabs.addTab(dockable.getTitleText(), dockable.getTitleIcon(), dockable.getComponent(), dockable);
		invalidate();
	}
	
	@Override
	public boolean titleContains( int x, int y ){
		Point point = new Point( x, y );
		point = SwingUtilities.convertPoint( this, point, tabs );
		for( int i = 0, n = tabs.getTabCount(); i<n; i++ ){
			Rectangle bounds = tabs.getBoundsAt( i );
			if( bounds.contains( point ))
				return true;
		}
		return false;
	}
}
