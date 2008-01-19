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
package bibliothek.gui.dock.common.intern;

import javax.swing.JFrame;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.common.FWorkingArea;
import bibliothek.gui.dock.control.SingleParentRemover;
import bibliothek.gui.dock.security.*;

/**
 * A factory used in restricted environment, where no global events can
 * be observed.
 * @author Benjamin Sigg
 *
 */
public class SecureControlFactory implements FControlFactory {
    public DockController createController() {
        DockController controller = new SecureDockController( new SecureDockControllerFactory(){
            @Override
            public SingleParentRemover createSingleParentRemover( DockController controller ) {
                return new FSingleParentRemover();
            }
        });
        controller.setSingleParentRemove( true );
        return controller;
    }

    public FlapDockStation createFlapDockStation() {
        return new SecureFlapDockStation();
    }

    public ScreenDockStation createScreenDockStation( JFrame owner ) {
        return new SecureScreenDockStation( owner );
    }

    public SplitDockStation createSplitDockStation(){
        return new SecureSplitDockStation(){
            @Override
            protected ListeningDockAction createFullScreenAction() {
                return null;
            }
            @Override
            public void setFrontDockable( Dockable dockable ) {
                // ignore
            }
        };
    }
    
    public FWorkingArea createWorkingArea( String id ) {
        return new FWorkingArea( id, true );
    }
}
