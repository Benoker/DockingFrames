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

package bibliothek.gui.dock.action;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.basic.action.menu.MenuMenuHandler;

/**
 * A mouse-listener that may be added to any component. When
 * the popup-trigger is pressed, a popupmenu will appear. This menu
 * shows a list of {@link DockAction DockActions}.
 * @author Benjamin Sigg
 */
public abstract class ActionPopup extends MouseInputAdapter{
    /** Whether to check the {@link ActionPopupSuppressor} or not */
    private boolean suppressable;
    
    /**
     * Constructs a new ActionPopup
     * @param suppressable whether to check the {@link ActionPopupSuppressor} 
     * before popping up, or not. The suppressor can tell the popup, that it
     * should not be made visible.
     */
    public ActionPopup( boolean suppressable ){
        this.suppressable = suppressable;
    }
    
    /**
     * Whether this ActionPopup can be suppressed or not.
     * @return <code>true</code> if this can be suppressed
     */
    public boolean isSuppressable() {
        return suppressable;
    }
    
    /**
     * Sets whether to ask the {@link ActionPopupSuppressor} if this menu
     * is allowed to popup or not.
     * @param suppressable <code>true</code> if the suppressor is allowed to
     * suppress this popup
     */
    public void setSuppressable( boolean suppressable ) {
        this.suppressable = suppressable;
    }
    
    @Override
    public void mousePressed( MouseEvent e ) {
        if( e.isPopupTrigger() ){
            popup( e );
        }
    }
    
    @Override
    public void mouseReleased( MouseEvent e ) {
        if( e.isPopupTrigger() ){
            popup( e );
        }        
    }
    
    /**
     * Tells, whether a popup can be displayed, or not.
     * @return <code>true</code> if a popup can be displayed, <code>false</code>
     * otherwise.
     */
    protected abstract boolean isEnabled();
    
    /**
     * Gets the Dockable to which the actions are linked.
     * @return The Dockable
     */
    protected abstract Dockable getDockable();
    
    /**
     * Gets the actions, that will be displayed
     * @return The actions
     */
    protected abstract DockActionSource getSource();
    
    /**
     * Shows the popup of this ActionPopup. This method is normally 
     * invoked by the {@link #mousePressed(MouseEvent) mousePressed}
     * or the {@link #mouseReleased(MouseEvent) mouseReleased}-method
     * @param e The {@link MouseEvent} that triggers the popup. The event must not
     * {@link MouseEvent#isConsumed() consumed}
     */
    protected void popup( MouseEvent e ){
        if( e.isConsumed() )
            return;
        
        if( isEnabled() ){
            popup( e.getComponent(), e.getX(), e.getY() );
        }
    }
    
    /**
     * Pops up this menu.
     * @param owner the owner of the menu
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void popup( Component owner, int x, int y ){
        final Dockable dockable = getDockable();
        if( dockable.getController() == null )
            return;
        
        if( dockable.getController().isOnMove() )
            return;
                    
        DockActionSource source = getSource();
        
        if( source.getDockActionCount() == 0 )
            return;
        
        if( isSuppressable() && dockable.getController().getPopupSuppressor().suppress( dockable, source ))
            return;
        
        JPopupMenu menu = new JPopupMenu();
        final MenuMenuHandler handler = new MenuMenuHandler( source, dockable, menu );
        handler.bind();
        
        menu.addPopupMenuListener( new PopupMenuListener(){
            public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
            	// do nothing
            }
            public void popupMenuCanceled( PopupMenuEvent e ) {
                handler.unbind();
            }
            public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
            	// do nothing
            }
        });
        
        menu.show( owner, x, y );
    }
}