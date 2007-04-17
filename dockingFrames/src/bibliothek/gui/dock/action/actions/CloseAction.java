/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.action.actions;

import javax.swing.Icon;

import bibliothek.gui.DockUI;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;

/**
 * This action shows an icon for "close". When the action is trigged, 
 * the {@link #close(Dockable) close}-method is invoked with the
 * {@link Dockable} to close. This method will then remove the Dockable
 * from it's parent.
 * @author Benjamin Sigg
 */
public class CloseAction extends SimpleButtonAction {
    /**
     * Defaultconstructor, sets the icon and the text of this action.
     */
    public CloseAction(){
        Icon icon = createIcon();
        setIcon( icon );
        setText( DockUI.getDefaultDockUI().getString( "close" ));
        setTooltipText( DockUI.getDefaultDockUI().getString( "close.tooltip" ));
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
     * Creates the {@link Icon} that will be used for this action.
     * @return The Icon to display, may be <code>null</code>
     */
    protected Icon createIcon(){
        return DockUI.getDefaultDockUI().getIcon( "close" );
    }
}
