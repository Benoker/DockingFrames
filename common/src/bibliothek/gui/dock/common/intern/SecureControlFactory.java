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

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.security.SecureDockController;
import bibliothek.gui.dock.security.SecureFlapDockStation;
import bibliothek.gui.dock.security.SecureScreenDockStation;
import bibliothek.gui.dock.security.SecureSplitDockStation;

/**
 * A factory used in restricted environment, where no global events can
 * be observed.
 * @author Benjamin Sigg
 *
 */
public class SecureControlFactory implements CControlFactory {
    public DockController createController( final CControl owner ) {
        return new SecureDockController(){
            @Override
            public void setFocusedDockable( Dockable focusedDockable, boolean force, boolean ensureFocusSet ) {
                if( focusedDockable != null ){
                    CStateManager states = owner.getStateManager();
                    if( states != null ){
                        states.ensureNotHidden( focusedDockable );
                    }
                }
                super.setFocusedDockable( focusedDockable, force, ensureFocusSet );
            }
        };
    }

    public FlapDockStation createFlapDockStation( final Component expansion ) {
        return new SecureFlapDockStation(){
            @Override
            public Rectangle getExpansionBounds() {
                Point point = new Point( 0, 0 );
                SwingUtilities.convertPoint( this.getComponent(), point, expansion );
                return new Rectangle( -point.x, -point.y, expansion.getWidth(), expansion.getHeight() );
            }
        };
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
                if( !isFullScreen() ){
                    super.setFrontDockable( dockable );
                }
            }
        };
    }
    
    public CWorkingArea createWorkingArea( String id ) {
        return new CWorkingArea( id, true );
    }
}
