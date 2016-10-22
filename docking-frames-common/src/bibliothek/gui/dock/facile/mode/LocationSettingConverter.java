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

import bibliothek.gui.DockController;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.DockablePropertyFactory;
import bibliothek.gui.dock.layout.PropertyTransformer;
import bibliothek.gui.dock.station.flap.FlapDockProperty;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.support.mode.ModeSettingsConverter;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A {@link ModeSettingsConverter} for the {@link LocationModeManager}.
 * @author Benjamin Sigg
 */
public class LocationSettingConverter implements ModeSettingsConverter<Location, Location>{
    /** transformer to read or write single {@link DockableProperty}s */
    private PropertyTransformer transformer;

    /**
     * Creates a new converter.
     * @param controller the controller in whose realm settings need to be converted
     */
    public LocationSettingConverter( DockController controller ){
    	transformer = new PropertyTransformer( controller );
    }
    
    /**
     * Creates a new converter.
     * @param transformer transformer used to read {@link DockableProperty}s.
     */
    public LocationSettingConverter( PropertyTransformer transformer ){
    	this.transformer = transformer;
    }
    
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
        Version.write( out, Version.VERSION_1_1_2 );
        out.writeUTF( element.getMode().toString() );
        out.writeUTF( element.getRoot() );
        out.writeBoolean( element.isApplicationDefined() );
        transformer.write( element.getLocation(), out );
    }

    public Location readProperty( DataInputStream in ) throws IOException {
        Version version = Version.read( in );
        version.checkCurrent();
        boolean version108 = Version.VERSION_1_0_8.compareTo( version ) <= 0;
        boolean version112 = version.equals( Version.VERSION_1_1_2 );
        Path mode = null;
        if( version108 ){
        	mode = new Path( in.readUTF() );
        }
        String root = in.readUTF();
        boolean applicationDefined = false;
        if( version112 ){
        	applicationDefined = in.readBoolean();
        }
        DockableProperty location = transformer.read( in );
        if( !version108 ){
        	mode = guessMode( location );
        }
        return new Location( mode, root, location, applicationDefined );
    }
    
    private Path guessMode( DockableProperty location ){
    	if( location instanceof FlapDockProperty ){
    		return MinimizedMode.IDENTIFIER;
    	}
    	else if( location instanceof ScreenDockProperty ){
    		return ExternalizedMode.IDENTIFIER; 
    	}
    	else{
    		return NormalMode.IDENTIFIER;
    	}
    }

    public void writePropertyXML( Location b, XElement element ) {
    	element.addElement( "mode" ).setString( b.getMode().toString() );
        element.addElement( "root" ).setString( b.getRoot() );
        element.addElement( "applicationDefined" ).setBoolean( b.isApplicationDefined() );
        transformer.writeXML( b.getLocation(), element.addElement( "location" ) );
    }

    public Location readPropertyXML( XElement element ) {
    	XElement xmode = element.getElement( "mode" );
    	Path mode = null;
    	if( xmode != null ){
    		mode = new Path( xmode.getString() );
    	}
    	String root = element.getElement( "root" ).getString();
    	DockableProperty location = transformer.readXML( element.getElement( "location" ) );
    	if( mode == null ){
    		mode = guessMode( location );
    	}
    	
    	boolean applicationDefined = false;
    	XElement xapplicationDefined = element.getElement( "applicationDefined" );
    	if( xapplicationDefined != null ){
    		applicationDefined = xapplicationDefined.getBoolean();
    	}
    	
        return new Location( mode, root, location, applicationDefined );
    }
}
