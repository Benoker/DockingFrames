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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.action.CRadioGroup;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.paint.model.Picture;
import bibliothek.paint.model.PictureListener;
import bibliothek.paint.model.ShapeFactory;
import bibliothek.paint.model.ShapeUtils;
import bibliothek.paint.util.Resources;
import bibliothek.paint.view.action.EraseLastShape;
import bibliothek.paint.view.action.ShapeSelection;
import bibliothek.paint.view.action.ZoomIn;
import bibliothek.paint.view.action.ZoomOut;

/**
 * A {@link MultipleCDockable} showing one {@link Picture}, using a 
 * {@link Page} to do so.
 * @author Benjamin Sigg
 *
 */
public class PictureDockable extends DefaultMultipleCDockable {
	/** the page painting the picture */
    private Page page;

    /** the current picture */
    private Picture picture;
    
    /** an action erasing elements of the picture */
    private EraseLastShape eraser;
    
    /**
     * A listener to the picture, changing some properties when the picture changes.
     */
    private PictureListener listener = new PictureListener(){
    	public void pictureChanged(){
    		eraser.setEnabled( !getPicture().isEmpty() );
    	}
    };
    
    /**
     * Creates a new Dockable.
     * @param factory the factory which creates this kind of Dockable
     */
    public PictureDockable( MultipleCDockableFactory<PictureDockable,?> factory ){
        super( factory );
        
        setTitleText( "Page" );
        setCloseable( true );
        setMinimizable( true );
        setMaximizable( true );
        setExternalizable( false );
        setRemoveOnClose( true );
        
        addCDockableStateListener( new CDockableAdapter(){
        	@Override
        	public void visibilityChanged( CDockable dockable ){
        		Picture picture = getPicture();
        		if( picture != null ){
	        		if( isVisible() ){
	        			page.setPicture( picture );
	        			picture.addListener( listener );
	        			listener.pictureChanged();
	        		}
	        		else{
	        			page.setPicture( null );
	        			picture.removeListener( listener );
	        		}
        		}
        	}
        });
        
        setTitleIcon( Resources.getIcon( "dockable.picture" ) );
        
        page = new Page();
        getContentPane().setLayout( new GridLayout( 1, 1 ) );
        JPanel background = new JPanel( new GridBagLayout() );
        background.add( page, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, 
        		GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
        getContentPane().add( new JScrollPane( background ));
        
        // add buttons to this dockable
        addAction( new ZoomIn( page ) );
        addAction( new ZoomOut( page ) );
        addSeparator();
        
        CRadioGroup group = new CRadioGroup();
        boolean first = true;
        
        for( ShapeFactory shapeFactory : ShapeUtils.getFactories() ){
            ShapeSelection button = new ShapeSelection( page, shapeFactory );
            group.add( button );
            addAction( button );
            
            // ensure that at least one button is selected
            if( first ){
                first = false;
                button.setSelected( true );
            }
        }
        eraser = new EraseLastShape( page );
        addAction( eraser );
        addSeparator();
    }
    
    /**
     * Sets the picture which will be painted on this PageDockable.
     * @param picture the new picture
     */
    public void setPicture( Picture picture ){
    	if( isVisible() && getPicture() != null )
    		getPicture().removeListener( listener );
    	
    	this.picture = picture;
    	
    	if( isVisible() ){
    		page.setPicture( picture );
    	}
        setTitleText( picture == null ? "" : picture.getName() );
        eraser.setEnabled( picture != null && !picture.isEmpty() );
        
        if( isVisible() && picture != null )
        	picture.addListener( listener );
    }
    
    /**
     * Gets the picture which is painted on this Dockable.
     * @return the picture
     */
    public Picture getPicture(){
        return picture;
    }
    
    /**
     * Gets the page which paints the {@link Picture} of this PageDockable.
     * @return the page
     */
    public Page getPage() {
        return page;
    }
}
