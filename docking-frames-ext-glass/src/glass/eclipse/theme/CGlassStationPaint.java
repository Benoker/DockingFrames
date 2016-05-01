package glass.eclipse.theme;

import java.awt.*;

import bibliothek.gui.*;
import bibliothek.gui.dock.*;
import bibliothek.gui.dock.station.*;
import bibliothek.gui.dock.themes.color.*;
import bibliothek.gui.dock.util.color.*;


@ColorCodes( {"glass.paint.divider", "glass.paint.insertion", "glass.paint.line"})
public class CGlassStationPaint implements StationPaint {
	private final StationPaintColor color = new StationPaintColor("glass.paint", this, Color.GRAY) {
		@Override
		protected void changed (Color oldColor, Color newColor) {
			// ignore
		}
	};

	public void drawDivider (Graphics g, DockStation station, Rectangle bounds) {
		if (station instanceof SplitDockStation && !((SplitDockStation)station).isContinousDisplay()) {
			color.setId( "glass.paint.divider" );
			color.connect( station.getController() );
			
			Graphics2D g2 = (Graphics2D)g.create();
			g2.setColor(color.value());
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.8f));
			g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			g2.dispose();
			
			color.connect( null );
		}
	}

	public void drawInsertion (Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds) {
		color.setId("glass.paint.insertion");
		color.connect(station.getController());

		Graphics2D g2 = (Graphics2D)g.create();
		g2.setColor(color.value());
		//      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(3f));

		int x = dockableBounds.x + 1;
		int y = dockableBounds.y + 1;
		int w = dockableBounds.width - 3;
		int h = dockableBounds.height - 3;

		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f));
		g2.drawRect(x, y, w, h);
		g2.fillRect(x, y, w + 1, h + 1);

		g2.dispose();

		color.connect(null);
	}

	public void drawInsertionLine (Graphics g, DockStation station, int x1, int y1, int x2, int y2) {
		color.setId("glass.paint.line");
		color.connect(station.getController());

		Graphics2D g2d = (Graphics2D)g.create();
		g2d.setStroke(new BasicStroke(3f));
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.6f));
		g2d.setColor(color.value());
		g2d.drawLine(x1, y1, x2, y2);
		g2d.dispose();

		color.connect(null);
	}
	
	public void drawRemoval( Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds ){
		// ignore
	}
}
