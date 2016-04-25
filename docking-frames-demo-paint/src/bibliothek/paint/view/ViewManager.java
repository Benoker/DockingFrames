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

import bibliothek.gui.dock.common.*;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.paint.model.Picture;
import bibliothek.paint.model.PictureRepository;
import bibliothek.paint.model.PictureRepositoryListener;
import bibliothek.paint.model.Shape;
import bibliothek.util.xml.XElement;

/**
 * The <code>ViewManager</code> is responsible to connect all the {@link CDockable}s
 * used in this application. The <code>ViewManager</code> also provides 
 * methods to transfer data from one <code>CDockable</code> to another.
 * @author Benjamin Sigg
 *
 */
public class ViewManager {
    /** the controller of the whole framework */
    private CControl control;
    
    /** shows a list of all available {@link Picture}s */
    private PictureRepositoryDockable repositoryDockable;
    /** shows a selection of {@link Color}s, the <code>Color</code>s are used when adding a new {@link Shape} */
    private ColorDockable colorDockable;
    /** the {@link CDockable}s showing some {@link Picture}s */
    private List<PictureDockable> pages = new LinkedList<PictureDockable>();
    
    /** the factory which creates new {@link PictureDockable}s */
    private PictureFactory pageFactory;
    
    /** a set of {@link Picture}s */
    private PictureRepository pictures;
    /** the currently used color to paint new {@link bibliothek.paint.model.Shape}s */
    private Color color = Color.BLACK;
    
    /** the area on which the {@link PictureDockable}s are shown */
    private CWorkingArea workingArea;
    
    /**
     * Creates a new manager.
     * @param control the center of the Docking-Framework
     * @param pictures a set of pictures which might be shown
     */
    public ViewManager( CControl control, PictureRepository pictures ){
        this.control = control;
        this.pictures = pictures;
        
        pageFactory = new PictureFactory();
        control.addMultipleDockableFactory( "page", pageFactory );
        
        workingArea = control.createWorkingArea( "picture area" );
        workingArea.setLocation( CLocation.base().normalRectangle( 0, 0, 1, 1 ) );
        workingArea.setVisible( true );
        
        repositoryDockable = new PictureRepositoryDockable( this );
        control.addDockable( repositoryDockable );
        repositoryDockable.setLocation( CLocation.base().normalWest( 0.2 ) );
        repositoryDockable.setVisible( true );
        
        colorDockable = new ColorDockable( this );
        control.addDockable( colorDockable );
        colorDockable.setLocation( CLocation.base().normalSouth( 0.25 ) );
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
    public CControl getControl() {
        return control;
    }
    
    /**
     * Gets the area on which the pictures are shown.
     * @return the area
     */
    public CWorkingArea getWorkingArea() {
        return workingArea;
    }
    
    /**
     * Opens a view which shows <code>picture</code>.
     * @param picture the picture to show
     */
    public void open( Picture picture ){
        final PictureDockable page = new PictureDockable( pageFactory );
        page.addCDockableStateListener( new CDockableAdapter(){
            @Override
            public void visibilityChanged( CDockable dockable ) {
                if( dockable.isVisible() ){
                    pages.add( page );
                }
                else{
                    pages.remove( page );
                }
            }
        });
        
        page.setPicture( picture );
        page.getPage().setColor( color );
        
        page.setLocation( CLocation.working( workingArea ).rectangle( 0, 0, 1, 1 ) );
        workingArea.add( page );
        page.setVisible( true );
    }
    
    /**
     * Ensures that no view shows <code>picture</code> anymore.
     * @param picture the picture which should not be painted anywhere
     */
    public void closeAll( Picture picture ){
        for( PictureDockable page : pages.toArray( new PictureDockable[ pages.size() ] )){
            if( page.getPicture()  == picture ){
                page.setVisible( false );
                control.removeDockable( page );
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
    private class PictureFactory implements MultipleCDockableFactory<PictureDockable, PictureLayout>{
        public PictureLayout create() {
            return new PictureLayout();
        }

        public PictureDockable read( PictureLayout layout ) {
            String name = layout.getName();
            Picture picture = pictures.getPicture( name );
            if( picture == null )
                return null;
            final PictureDockable page = new PictureDockable( this );
            page.addCDockableStateListener( new CDockableAdapter(){
                @Override
                public void visibilityChanged( CDockable dockable ) {
                    if( dockable.isVisible() ){
                        pages.add( page );
                    }
                    else{
                        pages.remove( page );
                    }
                }
            });
            page.getPage().setColor( color );
            page.setPicture( picture );
            return page;            
        }

        public PictureLayout write( PictureDockable dockable ) {
            PictureLayout layout = new PictureLayout();
            layout.setName( dockable.getPicture().getName() );
            return layout;
        }
        
        public boolean match( PictureDockable dockable, PictureLayout layout ){
        	String name = dockable.getPicture().getName();
        	return name.equals( layout.getName() );
        }
    }
    
    /**
     * Describes the layout of one {@link PictureDockable}
     * @author Benjamin Sigg
     */
    private static class PictureLayout implements MultipleCDockableLayout{
        /** the name of the picture */
        private String name;
        
        /**
         * Sets the name of the picture that is shown.
         * @param name the name of the picture
         */
        public void setName( String name ) {
            this.name = name;
        }
        
        /**
         * Gets the name of the picture that is shown.
         * @return the name
         */
        public String getName() {
            return name;
        }
        
        public void readStream( DataInputStream in ) throws IOException {
            name = in.readUTF();
        }

        public void readXML( XElement element ) {
            name = element.getString();
        }

        public void writeStream( DataOutputStream out ) throws IOException {
            out.writeUTF( name );
        }

        public void writeXML( XElement element ) {
            element.setString( name );
        }
    }
}
