package bibliothek.sizeAndColor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class ColorButton {
    private Color color = Color.RED;
    
    private class ColorIcon implements Icon{
        public int getIconHeight() {
            return 20;
        }

        public int getIconWidth() {
            return 20;
        }

        public void paintIcon( Component c, Graphics g, int x, int y ) {
            g.setColor( color );
            g.fillRect( x, y, 20, 20 );
        }
    }
}
