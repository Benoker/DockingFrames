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

package bibliothek.extension.gui.dock.theme.flat;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.themes.basic.BasicButtonDockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * This title is used by the {@link FlatTheme} to replace the
 * default-DockTitle of the {@link FlapDockStation}. This title
 * does not show any {@link DockAction actions}, and if there is
 * an icon, the text of the title isn't shown either.
 * @author Benjamin Sigg
 */
public class FlatButtonTitle extends BasicButtonDockTitle {
    /** 
     * Current state of the mouse, is <code>true</code> when the
     * mouse is over this title.
     */
    private boolean mouseover = false;
    
    /**
     * Constructs a new title
     * @param dockable the owner of the title
     * @param origin the version which was used to create this title
     */
    public FlatButtonTitle( Dockable dockable, DockTitleVersion origin ) {
    	super( dockable, origin );
    	
        Listener listener = new Listener();
        listener.added( getComponent() );
        
        setBorder( null );
    }
    
    /**
     * Tells whether the mouse is currently over this button or not.
     * @return <code>true</code> if the mouse is over this button
     */
    public boolean isMouseover() {
		return mouseover;
	}
    
    /**
     * Exchanges the border of this title according to the state of
     * <code>selected</code> and of <code>mouseover</code>.
     * @param selected <code>true</code> if this title is selected
     * @param mouseover <code>true</code> if the mouse is currently
     * over this title
     */
    @Override
    protected void changeBorder(){
    	boolean selected = isSelected();
    	boolean mouseover = isMouseover();
    	boolean mousePressed = isMousePressed();
    	
        if( selected ^ mousePressed ){
            setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ));
        }
        else{
            if( mouseover || mousePressed )
                setBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ));
            else
                setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ));
        }
    }
    
    /**
     * A listener added to this title. The listener is triggered
     * when the mouse is moved over this title. This listener will
     * then invoke {@link FlatButtonTitle#changeBorder(boolean, boolean) changeBorder}.
     * @author Benjamin Sigg
     */
    private class Listener extends MouseAdapter implements ContainerListener{
        @Override
        public void mouseEntered( MouseEvent e ) {
        	mouseover = true;
        	changeBorder();
        }
        @Override
        public void mouseExited( MouseEvent e ) {
        	mouseover = false;
        	changeBorder();
        }
        
        public void componentAdded( ContainerEvent e ){
        	added( e.getChild() );
        }
        
        public void added( Component component ){
        	component.addMouseListener( this );
        	if( component instanceof Container ){
        		Container container = (Container)component;
        		container.addContainerListener( this );
        		for( int i = 0, n = container.getComponentCount(); i<n; i++ ){
        			added( container.getComponent( i ));
        		}
        	}
        }
        
        public void componentRemoved( ContainerEvent e ){
        	removed( e.getChild() );
        }
        
        public void removed( Component component ){
        	component.removeMouseListener( this );
        	if( component instanceof Container ){
        		Container container = (Container)component;
        		container.removeContainerListener( this );
        		for( int i = 0, n = container.getComponentCount(); i<n; i++ ){
        			removed( container.getComponent( i ));
        		}
        	}
        }
    }
}
