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
package bibliothek.extension.gui.dock.theme.bubble;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.view.ConnectingViewItem;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * A {@link BasicTitleViewItem} that connects a {@link RoundButton} with
 * the {@link DockController} when bound and when a controller is available.
 * @author Benjamin Sigg
 *
 */
public class RoundButtonViewItem  extends ConnectingViewItem<JComponent> implements BasicTitleViewItem<JComponent>{
    private BasicTitleViewItem<JComponent> delegate;
    private RoundButtonConnectable button;
    
    /**
     * Creates a new view item.
     * @param dockable the element to observe to get a {@link DockController}
     * @param delegate used to do all the other tasks
     * @param button the button which should be connected
     */
    public RoundButtonViewItem( Dockable dockable, BasicTitleViewItem<JComponent> delegate, RoundButtonConnectable button ) {
        super( dockable, delegate );
        this.delegate = delegate;
        this.button = button;
    }
    
    public void setBackground( Color background ) {
        Component item = getItem();
        if( item != null )
            item.setBackground( background );
    }
    
    public void setForeground( Color foreground ) {
        Component item = getItem();
        if( item != null )
            item.setForeground( foreground );
    }

    @Override
    protected void changed( DockController oldController, DockController newController ) {
        button.setController( newController );
    }

    public void setOrientation( Orientation orientation ) {
        delegate.setOrientation( orientation );
    }
}
