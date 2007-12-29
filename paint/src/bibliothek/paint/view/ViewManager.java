/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.paint.view;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import bibliothek.gui.dock.facile.FControl;
import bibliothek.gui.dock.facile.FDockable;
import bibliothek.gui.dock.facile.FLocation;
import bibliothek.gui.dock.facile.FMultipleDockable;
import bibliothek.gui.dock.facile.FMultipleDockableFactory;
import bibliothek.paint.model.Picture;
import bibliothek.paint.model.PictureRepository;
import bibliothek.paint.model.PictureRepositoryListener;
import bibliothek.paint.model.Shape;

/**
 * The <code>ViewManager</code> is responsible to connect all the {@link FDockable}s
 * used in this application. The <code>ViewManager</code> also provides 
 * methods to transfer data from one <code>FDockable</code> to another.
 * @author Benjamin Sigg
 *
 */
public class ViewManager {
    /** the controller of the whole framework */
    private FControl control;
    
    /** shows a list of all available {@link Picture}s */
    private PictureRepositoryDockable repositoryDockable;
    /** shows a selection of {@link Color}s, the <code>Color</code>s are used when adding a new {@link Shape} */
    private ColorDockable colorDockable;
    /** the {@link FDockable}s showing some {@link Picture}s */
    private List<PictureDockable> pages = new LinkedList<PictureDockable>();
    
    /** the factory which creates new {@link PictureDockable}s */
    private PictureFactory pageFactory;
    
    /** a set of {@link Picture}s */
    private PictureRepository pictures;
    /** the currently used color to paint new {@link bibliothek.paint.model.Shape}s */
    private Color color = Color.BLACK;
    
    /**
     * Creates a new manager.
     * @param control the center of the Docking-Framework
     * @param pictures a set of pictures which might be shown
     */
    public ViewManager( FControl control, PictureRepository pictures ){
        this.control = control;
        this.pictures = pictures;
        
        pageFactory = new PictureFactory();
        control.add( "page", pageFactory );
        
        repositoryDockable = new PictureRepositoryDockable( this );
        control.add( repositoryDockable );
        repositoryDockable.setLocation( FLocation.base().normalRectangle( 0, 0, 1, 1 ) );
        repositoryDockable.setVisible( true );
        
        colorDockable = new ColorDockable( this );
        control.add( colorDockable );
        colorDockable.setLocation( FLocation.base().normalSouth( 0.25 ) );
        colorDockable.setVisible( true );
        
        pictures.addListener( new PictureRepositoryListener(){
        	public void pictureAdded( Picture picture ){
        		open( picture );
        	}
        	public void pictureRemoved( Picture picture ){
        		closeAll( picture );
        	}
        });
    }
    
    /**
     * Gets the set of available {@link Picture}s.
     * @return the set of pictures
     */
    public PictureRepository getPictures(){
		return pictures;
	}
    
    /**
     * Gets the central control of the Docking-framework.
     * @return the central control mechanism
     */
    public FControl getControl() {
        return control;
    }
    
    /**
     * Opens a view which shows <code>picture</code>.
     * @param picture the picture to show
     */
    public void open( Picture picture ){
        PictureDockable page = new PictureDockable( pageFactory );
        page.setLocation( FLocation.base().normalNorth( 0.75 ).stack() );
        pages.add( page );
        page.setPicture( picture );
        page.getPage().setColor( color );
        control.add( page );
        page.setVisible( true );
    }
    
    /**
     * Ensures that no view shows <code>picture</code> anymore.
     * @param picture the picture which should not be painted anywhere
     */
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
    
    /**
     * A factory which creates {@link PictureDockable}s.
     * @author Benjamin Sigg
     */
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
