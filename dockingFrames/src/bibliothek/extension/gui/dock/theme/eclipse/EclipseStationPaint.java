package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.DockStation;

/**
 * @author Janni Kovacs
 */
public class EclipseStationPaint implements StationPaint {
	
	public void drawInsertionLine(Graphics g, DockStation station, int x1, int y1, int x2, int y2) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(2f));
		g.drawLine(x1, y1, x2, y2);
	}

	public void drawDivider(Graphics g, DockStation station, Rectangle bounds) {
		if (station instanceof SplitDockStation && !((SplitDockStation) station).isContinousDisplay()) {
			g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}

	public void drawInsertion(Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(2f));
		g.drawRect(dockableBounds.x, dockableBounds.y, dockableBounds.width, dockableBounds.height);
	}
}
