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


package bibliothek.gui.dock.station.flap;

import bibliothek.gui.dock.DockableProperty;
import bibliothek.gui.dock.DockablePropertyFactory;

/**
 * Creates instances of {@link FlapDockProperty}.
 * @author Benjamin Sigg
 */
public class FlapDockPropertyFactory implements DockablePropertyFactory {
	/** The default-id of this type of factory */
    public static final String ID = "flap dock";
    
    /** An instance of this factory which can be used at any location */
    public static final FlapDockPropertyFactory FACTORY = new FlapDockPropertyFactory();
    
    public String getID() {
        return ID;
    }

    public DockableProperty createProperty() {
        return new FlapDockProperty();
    }
}
