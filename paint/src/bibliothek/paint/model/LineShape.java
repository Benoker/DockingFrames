package bibliothek.paint.model;

import java.awt.Graphics;
import java.awt.Point;

public class LineShape extends Shape {
    public static final ShapeFactory FACTORY = 
        new ShapeFactory(){
            public Shape create() {
                return new LineShape();
            }
            public String getName() {
                return "Line";
            }
        };
    
    @Override
    public void paint( Graphics g ) {
        g.setColor( getColor() );
        Point a = getPointA();
        Point b = getPointB();
        g.drawLine( a.x, a.y, b.x, b.y );
    }
}
