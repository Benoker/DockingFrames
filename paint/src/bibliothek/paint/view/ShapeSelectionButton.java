package bibliothek.paint.view;

import java.awt.*;

import javax.swing.Icon;

import bibliothek.gui.dock.facile.action.FRadioButton;
import bibliothek.paint.model.Shape;
import bibliothek.paint.model.ShapeFactory;

public class ShapeSelectionButton extends FRadioButton {
    private Page page;
    private ShapeFactory factory;
    
    public ShapeSelectionButton( Page page, ShapeFactory factory ){
        this.page = page;
        this.factory = factory;
        setText( factory.getName() );
        setIcon( new ShapeIcon() );
    }
    
    @Override
    protected void changed() {
        if( isSelected() ){
            page.setFactory( factory );
        }
    }
    
    private class ShapeIcon implements Icon{
        private Shape shape;
        
        public ShapeIcon(){
            shape = factory.create();
            shape.setColor( Color.BLACK );
        }
        
        public int getIconHeight() {
            return 16;
        }
        
        public int getIconWidth() {
            return 16;
        }
        
        public void paintIcon( Component c, Graphics g, int x, int y ) {
            shape.setPointA( new Point( x+3, y+3 ) );
            shape.setPointB( new Point( x+13, y+13 ) );
            shape.paint( g );
        }
    }
}
