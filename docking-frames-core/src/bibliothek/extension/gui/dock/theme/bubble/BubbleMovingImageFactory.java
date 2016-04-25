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

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.dockable.MovingImage;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.TitleMovingImage;

/**
 * A factory that creates images using the {@link ReducedBubbleTitleFactory}.
 * @author Benjamin Sigg
 */
public class BubbleMovingImageFactory implements DockableMovingImageFactory {
    private ReducedBubbleTitleFactory reduced;
    
    public BubbleMovingImageFactory(){
        reduced = new ReducedBubbleTitleFactory();
    }
    
    public MovingImage create( DockController controller, DockTitle snatched ) {
        return new TitleMovingImage( snatched.getDockable(), reduced.createTitle( snatched.getDockable() ));
    }

    public MovingImage create( DockController controller, Dockable dockable ) {
        return new TitleMovingImage( dockable, reduced.createTitle( dockable ));
    }
}
