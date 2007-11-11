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
package bibliothek.gui.dock.control;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.SingleParentRemover;
import bibliothek.gui.dock.action.ActionOffer;
import bibliothek.gui.dock.action.DefaultActionOffer;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.event.DockRegisterListener;

/**
 * A very simple implementation of {@link DockControllerFactory}, creating
 * only the default-versions of every element.
 * @author Benjamin Sigg
 */
public class DefaultDockControllerFactory implements DockControllerFactory {
    public DockRegisterListener createActionBinder( DockController controller ) {
        return new ActionBinder();
    }

    public ActionViewConverter createActionViewConverter(
            DockController controller ) {
        
        return new ActionViewConverter();
    }

    public ActionOffer createDefaultActionOffer( DockController controller ) {
        return new DefaultActionOffer();
    }

    public DoubleClickController createDoubleClickController(
            DockController controller ) {
        
        return new DoubleClickController( controller );
    }

    public DockRegisterListener createFocusController( DockController controller ) {
        return new FocusController( controller );
    }

    public KeyboardController createKeyboardController(
            DockController controller ) {
        
        return new DefaultkeyBoardController( controller );
    }

    public MouseFocusObserver createMouseFocusObserver(
            DockController controller ) {
        
        return new DefaultMouseFocusObserver( controller );
    }

    public DockRegisterListener createPopupController( DockController controller ) {
        return new PopupController( controller );
    }

    public DockRegister createRegister( DockController controller ) {
        return new DockRegister( controller );
    }

    public DockRelocator createRelocator( DockController controller ) {
        return new DefaultDockRelocator( controller );
    }

    public SingleParentRemover createSingleParentRemover(
            DockController controller ) {
        
        return new SingleParentRemover();
    }
}
