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

import bibliothek.gui.DockController;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.util.WindowProvider;

/**
 * A factory creating various elements that are needed in a {@link CControl}
 * and its associated components.
 * @author Benjamin Sigg
 */
public interface CControlFactory {
    /**
     * Creates or gets the {@link DockController}.
     * @param owner the control for which the result of this method will be used
     * @return the controller, always the same object
     */
    public DockController createController( CControl owner );
    
    /**
     * Creates a new {@link FlapDockStation}.
     * @param expansion a {@link Component} which will be some parent of
     * the result, the {@link FlapDockStation#getExpansionBounds()} should 
     * act as if <code>expansion</code> were the whole station
     * @return the new station
     */
    public FlapDockStation createFlapDockStation( Component expansion );
    
    /**
     * Creates a new {@link ScreenDockStation}.
     * @param owner the owner of the dialogs of the station
     * @return the new station
     */
    public ScreenDockStation createScreenDockStation( WindowProvider owner );
    
    /**
     * Creates a new {@link SplitDockStation}.
     * @return the new station
     */
    public SplitDockStation createSplitDockStation();
    
    /**
     * Creates a new {@link CWorkingArea}.
     * @param id the unique if of the area
     * @return the new area
     */
    public CWorkingArea createWorkingArea( String id );
}
