package bibliothek.paint.model;

import java.awt.Graphics;
import java.awt.Point;

public class OvalShape extends Shape {
    public static final ShapeFactory FACTORY = 
        new ShapeFactory(){
            public Shape create() {
                return new OvalShape();
            }
            public String getName() {
                return "Oval";
            }
        };   
    
    @Override
    public void paint( Graphics g, double stretch ) {
        g.setColor( getColor() );
        Point a = getPointA();
        Point b = getPointB();
        g.drawOval(
        		stretch( Math.min( a.x, b.x ), stretch ),
                stretch( Math.min( a.y, b.y ), stretch ),
                stretch( Math.abs( a.x - b.x ), stretch ),
                stretch( Math.abs( a.y - b.y ), stretch ));
    }
}
