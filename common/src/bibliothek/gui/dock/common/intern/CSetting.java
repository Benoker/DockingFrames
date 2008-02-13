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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.dock.facile.action.StateManager;
import bibliothek.gui.dock.frontend.Setting;
import bibliothek.gui.dock.layout.DockSituation;
import bibliothek.gui.dock.layout.PropertyTransformer;
import bibliothek.gui.dock.support.action.ModeTransitionSetting;
import bibliothek.util.xml.XElement;

/**
 * A {@link Setting} that stores also the contents of a {@link CStateManager}.
 * @author Benjamin Sigg
 */
public class CSetting extends Setting{
    /** a set of modes */
    private ModeTransitionSetting<StateManager.Location, ?> modes;
    
    /**
     * Sets the set of modes.
     * @param modes the modes
     */
    public void setModes( ModeTransitionSetting<StateManager.Location, ?> modes ) {
        this.modes = modes;
    }
    
    /**
     * Gets the set of modes.
     * @return the modes
     */
    public ModeTransitionSetting<StateManager.Location, ?> getModes() {
        return modes;
    }
    
    @Override
    public void write( DockSituation situation,
            PropertyTransformer transformer, boolean entry, DataOutputStream out )
            throws IOException {
        
        super.write( situation, transformer, entry, out );
        modes.write( out );
    }
    
    @Override
    public void writeXML( DockSituation situation,
            PropertyTransformer transformer, boolean entry, XElement element ) {
        
        super.writeXML( situation, transformer, entry, element.addElement( "base" ) );
        modes.writeXML( element.addElement( "modes" ) );
    }
    
    @Override
    public void read( DockSituation situation, PropertyTransformer transformer,
            boolean entry, DataInputStream in ) throws IOException {
        
        super.read( situation, transformer, entry, in );
        modes.read( in );
    }
    
    @Override
    public void readXML( DockSituation situation,
            PropertyTransformer transformer, boolean entry, XElement element ) {
        super.readXML( situation, transformer, entry, element.getElement( "base" ) );
        modes.readXML( element.getElement( "modes" ) );
    }
}
