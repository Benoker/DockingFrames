/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.facile.mode;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;

/**
 * The default implementation of {@link ExternalizedMode}.
 * @author Benjamin Sigg
 */
public class DefaultExternalizedModeBehavior implements ExternalizedModeBehavior{
	/** the minimum size of new windows */
	private Dimension minSize = new Dimension( 300, 200 );
	
	public ScreenDockProperty findLocation( ExternalizedModeArea target, Dockable dockable ){
	    Component component = dockable.getComponent();
        component.invalidate();

        Component parent = component;
        while( parent.getParent() != null )
            parent = parent.getParent();
        parent.validate();
        
        
        Dimension size = component.getSize();
        Dimension preferred = component.getPreferredSize();
        
        size.width = Math.max( Math.max( size.width, preferred.width ), minSize.width );
        size.height = Math.max( Math.max( size.height, preferred.height ), minSize.height );

        Point corner;
        if( dockable.getComponent().isDisplayable() ){
        	corner = new Point();
            SwingUtilities.convertPointToScreen( corner, dockable.getComponent() );	
        }
        else{
        	DockController controller = dockable.getController();
        	Window root = null;
        	if( controller != null ){
        		root = controller.getRootWindowProvider().searchWindow();
        	}
        	if( root != null ){
        		corner = new Point( root.getX() + (root.getWidth() - size.width) / 2, root.getY() + (root.getHeight() - size.height) / 2);
        	}
        	else{
        		corner = new Point( 0, 0 );
        	}
        }
        

        return new ScreenDockProperty( corner.x, corner.y, size.width, size.height, null, false );
	}
}
