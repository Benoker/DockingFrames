package bibliothek.paint.view;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import bibliothek.gui.dock.facile.FControl;
import bibliothek.gui.dock.facile.FMultipleDockable;
import bibliothek.gui.dock.facile.FMultipleDockableFactory;
import bibliothek.paint.model.Picture;

public class ViewManager {
    private FControl control;
    
    private PictureListDockable list;
    private List<PageDockable> pages = new LinkedList<PageDockable>();
    
    private PageFactory pageFactory;
    
    public ViewManager( FControl control ){
        this.control = control;
        pageFactory = new PageFactory();
        control.add( "page", pageFactory );
        
        list = new PictureListDockable( this );
        control.add( list );
        list.setVisible( true );
    }
    
    public Picture getPicture( String name ){
        return list.getPicture( name );
    }
    
    public void open( Picture picture ){
        PageDockable page = new PageDockable( pageFactory );
        pages.add( page );
        page.setPicture( picture );
        control.add( page );
        page.setVisible( true );
    }
    
    public void closeAll( Picture picture ){
        ListIterator<PageDockable> pageIterator = pages.listIterator();
        while( pageIterator.hasNext() ){
            PageDockable page = pageIterator.next();
            if( page.getPicture() == picture ){
                page.setVisible( false );
                control.remove( page );
                pageIterator.remove();
            }
        }
    }
    
    public class PageFactory implements FMultipleDockableFactory{
        public FMultipleDockable read( DataInputStream in ) throws IOException {
            String name = in.readUTF();
            Picture picture = getPicture( name );
            if( picture == null )
                return null;
            PageDockable page = new PageDockable( this );
            pages.add( page );
            page.setPicture( picture );
            return page;
        }

        public void write( FMultipleDockable dockable, DataOutputStream out ) throws IOException {
            PageDockable page = (PageDockable)dockable;
            out.writeUTF( page.getPicture().getName() );
        }
    }
}
