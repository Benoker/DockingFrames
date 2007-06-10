package bibliothek.extension.gui.dock.theme.bubble;

import java.awt.*;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockStation;
import bibliothek.gui.dock.station.StationPaint;

public class BubbleStationPaint implements StationPaint {
    private BubbleTheme theme;
    
    public BubbleStationPaint( BubbleTheme theme ){
        this.theme = theme;
    }
    
    public void drawDivider( Graphics g, DockStation station, Rectangle bounds ) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setColor( theme.getColor( "paint" ) );
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.4f ) );
        
        g2.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );
        
        g2.dispose();
    }

    public void drawInsertion( Graphics g, DockStation station, Rectangle stationBounds, Rectangle dockableBounds ) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setColor( theme.getColor( "paint" ) );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setStroke( new BasicStroke( 3f ));
        
        int x = dockableBounds.x;
        int y = dockableBounds.y;
        int w = dockableBounds.width;
        int h = dockableBounds.height;
        
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP, 0.4f ) );
        g2.drawRoundRect( x, y, w, h, 50, 50 );
        g2.fillRoundRect( x, y, w, h, 50, 50 );
        
        g2.dispose();
    }

    public void drawInsertionLine( Graphics g, DockStation station, int x1, int y1, int x2, int y2 ) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setColor( theme.getColor( "paint" ) );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setStroke( new BasicStroke( 3f ));
        g2.drawLine( x1, y1, x2, y2 );
        g2.dispose();
    }
}
