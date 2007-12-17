package bibliothek.paint.view;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.dock.facile.FMultipleDockable;
import bibliothek.gui.dock.facile.FMultipleDockableFactory;
import bibliothek.paint.model.ShapeUtils;

public class PageFactory implements FMultipleDockableFactory{
    public static final PageFactory FACTORY = new PageFactory();
    
    private PageFactory(){
        // empty
    }
    
    public FMultipleDockable read( DataInputStream in ) throws IOException {
        PageDockable page = new PageDockable();
        page.getPage().setShapes( ShapeUtils.read( in ) );
        return page;
    }

    public void write( FMultipleDockable dockable, DataOutputStream out ) throws IOException {
        PageDockable page = (PageDockable)dockable;
        ShapeUtils.write( page.getPage().getShapes(), out );
    }
}
