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


package bibliothek.gui.dock.security;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.flap.ButtonPane;
import bibliothek.gui.dock.station.flap.FlapWindow;

/**
 * A {@link FlapDockStation} that uses {@link SecureFlapWindow} instead
 * of {@link FlapWindow}.
 * @author Benjamin Sigg
 */
public class SecureFlapDockStation extends FlapDockStation {
    @Override
    protected SecureFlapWindow createFlapWindow( Window owner, ButtonPane buttonPane ) {
        if( owner instanceof Dialog )
            return new SecureFlapWindow( this, buttonPane, (Dialog)owner );
        else if( owner instanceof Frame )
            return new SecureFlapWindow( this, buttonPane, (Frame)owner );
        else
            return null;
    }
    
    @Override
    public String getFactoryID() {
        return SecureFlapDockStationFactory.ID;
    }
}
