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

import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.frontend.Setting;
import bibliothek.gui.dock.layout.DockSituation;
import bibliothek.gui.dock.layout.PropertyTransformer;
import bibliothek.gui.dock.support.mode.ModeSettings;
import bibliothek.util.FrameworkOnly;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A {@link Setting} that stores also the contents of a {@link CLocationModeManager}.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class CSetting extends Setting{
    /** a set of modes */
    private ModeSettings<Location, Location> modes;
    
    /**
     * Sets the set of modes.
     * @param modes the modes
     */
    public void setModes( ModeSettings<Location, Location> modes ) {
        this.modes = modes;
    }
    
    /**
     * Gets the set of modes.
     * @return the modes
     */
    public ModeSettings<Location, Location> getModes() {
        return modes;
    }
    
    @Override
    public void write( DockSituation situation, PropertyTransformer transformer, boolean entry, DataOutputStream out ) throws IOException {
        Version.write( out, Version.VERSION_1_1_1 );
        super.write( situation, transformer, entry, out );
        modes.write( out );
    }
    
    @Override
    public void writeXML( DockSituation situation, PropertyTransformer transformer, boolean entry, XElement element ) {
        super.writeXML( situation, transformer, entry, element.addElement( "base" ) );
        modes.writeXML( element.addElement( "modes" ) );
    }
    
    @Override
    public void read( DockSituation situation, PropertyTransformer transformer, boolean entry, DataInputStream in ) throws IOException {
        
        Version version = Version.read( in );
        version.checkCurrent();
        
        boolean version7 = version.compareTo( Version.VERSION_1_0_7 ) >= 0;
        boolean version11 = version.compareTo( Version.VERSION_1_1_1 ) >= 0;
        
        super.read( situation, transformer, entry, in );
        
        // old settings will be converted automatically
        modes.read( in );
        
        if( version7 && !version11 ){
        	for( int i = 0, n = in.readInt(); i<n; i++ ){
        		in.readUTF();
                for( int j = 0, m = in.readInt(); j<m; j++ ){
                    in.readUTF();
                }
            }
        }
    }
    
    @Override
    public void readXML( DockSituation situation, PropertyTransformer transformer, boolean entry, XElement element ) {
        super.readXML( situation, transformer, entry, element.getElement( "base" ) );
        modes.readXML( element.getElement( "modes" ) );
    }
}
