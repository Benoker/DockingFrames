/**
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

package bibliothek.gui.dock.facile.action;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.event.IconManagerListener;
import bibliothek.gui.dock.util.IconManager;

/**
 * This action shows an icon for "close". When the action is trigged, 
 * the {@link #close(Dockable) close}-method is invoked with the
 * {@link Dockable} to close. This method will then remove the Dockable
 * from it's parent.
 * @author Benjamin Sigg
 */
public class CloseAction extends SimpleButtonAction implements ListeningDockAction{
    private DockController controller;
    private Listener listener = new Listener();
    
    /**
     * Sets the icon and the text of this action.
     * @param controller The controller from which this action should read
     * properties, might be <code>null</code> and can be changed by the
     * method {@link #setController(DockController) setController}
     */
    public CloseAction( DockController controller ){
        setText( DockUI.getDefaultDockUI().getString( "close" ));
        setTooltip( DockUI.getDefaultDockUI().getString( "close.tooltip" ));
        setController( controller );
    }
    
    public void setController( DockController controller ) {
        if( controller != this.controller ){
            if( this.controller != null )
                this.controller.getIcons().remove( "close", listener );
            
            this.controller = controller;
            
            if( controller != null ){
                IconManager icons = controller.getIcons();
                icons.add( "close", listener );
                setIcon( icons.getIcon( "close" ));
            }
        }
    }
    
    @Override
    public void action( Dockable dockable ) {
        close( dockable );
    }
    
    /**
     * Invoked when the <code>dockable</code> has to be closed. The
     * default-behaviour of this method is to remove the {@link Dockable}
     * from it's parent, if there is a parent.
     * @param dockable The {@link Dockable} to close
     */
    protected void close( Dockable dockable ){
        DockStation parent = dockable.getDockParent();
        if( parent != null )
            parent.drag( dockable );
    }
    
    /**
     * A listener changing the icon of this action
     * @author Benjamin Sigg
     */
    private class Listener implements IconManagerListener{
        public void iconChanged( String key, Icon icon ) {
            setIcon( icon );
        }
    }
}
