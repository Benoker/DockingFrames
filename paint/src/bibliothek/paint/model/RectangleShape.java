package bibliothek.paint.model;

import java.awt.Graphics;
import java.awt.Point;

public class RectangleShape extends Shape{
    public static final ShapeFactory<RectangleShape> FACTORY = 
        new ShapeFactory<RectangleShape>(){
            public RectangleShape create() {
                return new RectangleShape();
            }
            public String getName() {
                return "Rectangle";
            }
        };
    
    @Override
    public void paint( Graphics g ) {
        g.setColor( getColor() );
        Point a = getPointA();
        Point b = getPointB();
        g.drawRect(
                Math.min( a.x, b.x ),
                Math.min( a.y, b.y ),
                Math.abs( a.x - b.x ),
                Math.abs( a.y - b.y ));
    }
}
