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
package bibliothek.gui.dock.facile.mode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.DockablePropertyFactory;
import bibliothek.gui.dock.layout.PropertyTransformer;
import bibliothek.gui.dock.support.mode.ModeSettingsConverter;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A {@link ModeSettingsConverter} for the {@link LocationModeManager}.
 * @author Benjamin Sigg
 */
public class LocationSettingConverter implements ModeSettingsConverter<Location, Location>{
    /** transformer to read or write single {@link DockableProperty}s */
    private PropertyTransformer transformer = new PropertyTransformer();

    /**
     * Adds an additional factory to this converter, needed to read and write
     * {@link DockableProperty}s.
     * @param factory the additional factory
     */
    public void addFactory( DockablePropertyFactory factory ){
    	transformer.addFactory( factory );
    }
    
    public Location convertToSetting( Location a ) {
        return a;
    }
    public Location convertToWorld( Location b ) {
        return b;
    }

    public void writeProperty( Location element, DataOutputStream out ) throws IOException {
        Version.write( out, Version.VERSION_1_0_8 );
        out.writeUTF( element.getMode().toString() );
        out.writeUTF( element.getRoot() );
        transformer.write( element.getLocation(), out );
    }

    public Location readProperty( DataInputStream in ) throws IOException {
        Version version = Version.read( in );
        version.checkCurrent();
        Path mode = new Path( in.readUTF() );
        String root = in.readUTF();
        DockableProperty location = transformer.read( in );
        return new Location( mode, root, location );
    }

    public void writePropertyXML( Location b, XElement element ) {
    	element.addElement( "mode" ).setString( b.getMode().toString() );
        element.addElement( "root" ).setString( b.getRoot() );
        transformer.writeXML( b.getLocation(), element.addElement( "location" ) );
    }

    public Location readPropertyXML( XElement element ) {
        return new Location(
        		new Path( element.getElement( "mode" ).getString() ),
                element.getElement( "root" ).getString(),
                transformer.readXML( element.getElement( "location" ) ));
    }
}
