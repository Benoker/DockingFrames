/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.title;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;

import javax.swing.JPanel;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.dockable.MovingImage;
import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * This {@link MovingImage} shows a {@link DockTitle}, the title
 * can be automatically exchanged.
 * @author Benjamin Sigg
 *
 */
public class UpdatingTitleMovingImage implements MovingImage {
    /** the element which is represented by this image */
    private Dockable dockable;

    /** the items to show */
    private JPanel content;
    
    /** the kind of title to show */
    private DockTitleVersion version;
    
    /** the current title */
    private DockTitleRequest request;

    /** how to show the title */
    private Orientation orientation;
    
    /**
     * Creates a new image.
     * @param dockable the element which is represented by this image
     * @param version the contents of this image
     * @param orientation how to align the title
     */
    public UpdatingTitleMovingImage( Dockable dockable, DockTitleVersion version, Orientation orientation ){
        if( dockable == null )
            throw new IllegalArgumentException( "ockable must not be null" );
        
        if( version == null )
            throw new IllegalArgumentException( "version must not be null" );
        
        this.dockable = dockable;
        content = new JPanel( new BorderLayout() );
        content.setOpaque( false );
        this.version = version;
        this.orientation = orientation;
    }
    
    public Point getOffset( Point pressPoint ){
    	return null;
    }
    
    public void bind( boolean transparency ) {
        request = new DockTitleRequest( null, dockable, version ) {
			@Override
			protected void answer( DockTitle previous, DockTitle title ){
				if( previous != null ){	
					dockable.unbind( previous );
				}
				content.removeAll();
				if( title != null ){
					title.setOrientation( orientation );
					dockable.bind( title );
					content.add( title.getComponent(), BorderLayout.CENTER );
				}
			}
		};
		request.install();
		request.request();
    }

    public Component getComponent() {
        return content;
    }

    public void unbind() {
    	DockTitle title = request.getAnswer();
    	if( title != null ){
    		dockable.unbind( title );
    		content.removeAll();
    	}
        request.uninstall();
        request = null;
    }
}