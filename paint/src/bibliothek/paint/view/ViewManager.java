package bibliothek.paint.view;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import bibliothek.gui.dock.facile.FControl;
import bibliothek.gui.dock.facile.FMultipleDockable;
import bibliothek.gui.dock.facile.FMultipleDockableFactory;
import bibliothek.gui.dock.facile.FDockable.ExtendedMode;
import bibliothek.paint.model.Picture;
import bibliothek.paint.model.PictureRepository;
import bibliothek.paint.model.PictureRepositoryListener;

public class ViewManager {
    private FControl control;
    
    private PictureRepositoryDockable repositoryDockable;
    private ColorDockable colorDockable;
    private List<PictureDockable> pages = new LinkedList<PictureDockable>();
    
    private PictureFactory pageFactory;
    
    private PictureRepository pictures;
    /** the currently used color to paint new {@link bibliothek.paint.model.Shape}s */
    private Color color = Color.BLACK;
    
    public ViewManager( FControl control, PictureRepository pictures ){
        this.control = control;
        this.pictures = pictures;
        
        pageFactory = new PictureFactory();
        control.add( "page", pageFactory );
        
        repositoryDockable = new PictureRepositoryDockable( this );
        control.add( repositoryDockable );
        repositoryDockable.setVisible( true );
        
        colorDockable = new ColorDockable( this );
        control.add( colorDockable );
        colorDockable.setVisible( true );
        colorDockable.setExtendedMode( ExtendedMode.MINIMIZED );
        
        pictures.addListener( new PictureRepositoryListener(){
        	public void pictureAdded( Picture picture ){
        		open( picture );
        	}
        	public void pictureRemoved( Picture picture ){
        		closeAll( picture );
        	}
        });
    }
    
    public PictureRepository getPictures(){
		return pictures;
	}
    
    public void open( Picture picture ){
        PictureDockable page = new PictureDockable( pageFactory );
        pages.add( page );
        page.setPicture( picture );
        page.getPage().setColor( color );
        control.add( page );
        page.setVisible( true );
    }
    
    public void closeAll( Picture picture ){
        ListIterator<PictureDockable> pageIterator = pages.listIterator();
        while( pageIterator.hasNext() ){
            PictureDockable page = pageIterator.next();
            if( page.getPicture() == picture ){
                page.setVisible( false );
                control.remove( page );
                pageIterator.remove();
            }
        }
    }
    
    /**
     * Ensures that all new {@link bibliothek.paint.model.Shape}s will be painted
     * with the {@link Color} <code>color</code>.
     * @param color the color of new Shapes.
     */
    public void setColor( Color color ){
    	this.color = color;
    	for( PictureDockable picture : pages ){
    		picture.getPage().setColor( color );
    	}
    }
    
    /**
     * Gets the currently used color.
     * @return the currently used color
     */
    public Color getColor(){
    	return color;
    }
    
    public class PictureFactory implements FMultipleDockableFactory{
        public FMultipleDockable read( DataInputStream in ) throws IOException {
            String name = in.readUTF();
            Picture picture = pictures.getPicture( name );
            if( picture == null )
                return null;
            PictureDockable page = new PictureDockable( this );
            pages.add( page );
            page.getPage().setColor( color );
            page.setPicture( picture );
            return page;
        }

        public void write( FMultipleDockable dockable, DataOutputStream out ) throws IOException {
            PictureDockable page = (PictureDockable)dockable;
            out.writeUTF( page.getPicture().getName() );
        }
    }
}
