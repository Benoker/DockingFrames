package bibliothek.paint.model;

import java.awt.Graphics;
import java.awt.Point;

public class OvalShape extends Shape {
    public static final ShapeFactory<OvalShape> FACTORY = 
        new ShapeFactory<OvalShape>(){
            public OvalShape create() {
                return new OvalShape();
            }
            public String getName() {
                return "Oval";
            }
        };   
    
    @Override
    public void paint( Graphics g ) {
        g.setColor( getColor() );
        Point a = getPointA();
        Point b = getPointB();
        g.drawOval(
                Math.min( a.x, b.x ),
                Math.min( a.y, b.y ),
                Math.abs( a.x - b.x ),
                Math.abs( a.y - b.y ));
    }
}
