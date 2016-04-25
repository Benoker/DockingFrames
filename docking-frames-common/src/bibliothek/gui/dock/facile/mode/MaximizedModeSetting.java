/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.support.mode.ModeSetting;
import bibliothek.gui.dock.support.mode.ModeSettingFactory;
import bibliothek.gui.dock.support.mode.ModeSettingsConverter;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * Settings associated with a {@link MaximizedMode}.
 * @author Benjamin Sigg
 *
 */
public class MaximizedModeSetting implements ModeSetting<Location>{
	/** factory creating new {@link MaximizedModeSetting}s */
	public static ModeSettingFactory<Location> FACTORY = new ModeSettingFactory<Location>(){
		public ModeSetting<Location> create(){
			return new MaximizedModeSetting();
		}
		public Path getModeId(){
			return MaximizedMode.IDENTIFIER;
		}
	};
	
	/** the mode in which some dockable with id=key was before maximizing */
	private HashMap<String, Path> lastMaximizedMode = new HashMap<String, Path>();
	
	/** the location some dockable had before maximizing */
	private HashMap<String, Location> lastMaximizedLocation = new HashMap<String, Location>();
	
	public Path getModeId(){
		return MaximizedMode.IDENTIFIER;
	}

	/**
	 * Sets the location of {@link Dockable}s that are maximized. This method makes a copy
	 * of the map.
	 * @param lastMaximizedLocation the map that is going to be copied 
	 */
	public void setLastMaximizedLocation( Map<String, Location> lastMaximizedLocation ){
		this.lastMaximizedLocation = new HashMap<String, Location>( lastMaximizedLocation );
	}
	
	/**
	 * Sets the mode of {@link Dockable}s that are maximized. This method makes a copy
	 * of the map.
	 * @param lastMaximizedMode the map that is going to be copied
	 */
	public void setLastMaximizedMode( Map<String, Path> lastMaximizedMode ){
		this.lastMaximizedMode = new HashMap<String, Path>( lastMaximizedMode );
	}
	
	/**
	 * Gets the location of {@link Dockable}s that are currently maximized.
	 * @return an unmodifiable map
	 */
	public Map<String, Location> getLastMaximizedLocation(){
		return Collections.unmodifiableMap( lastMaximizedLocation );
	}
	
	/**
	 * Gets the modes of {@link Dockable}s that are currently maximized.
	 * @return an unmodifiable map
	 */
	public Map<String, Path> getLastMaximizedMode(){
		return Collections.unmodifiableMap( lastMaximizedMode );
	}
	
	public <B> void write( DataOutputStream out, ModeSettingsConverter<Location, B> converter ) throws IOException{
        Version.write( out, Version.VERSION_1_0_8 );
        if( lastMaximizedMode == null ){
            out.writeInt( 0 );
        }
        else{
            int count = 0;
            for( Path check : lastMaximizedMode.values() ){
                if( check != null ){
                    count++;
                }
            }

            out.writeInt( count );
            for( Map.Entry<String, Path> entry : lastMaximizedMode.entrySet() ){
                if( entry.getValue() != null ){
                    out.writeUTF( entry.getKey() );
                    out.writeUTF( entry.getValue().toString() );
                }
            }
        }

        if( lastMaximizedLocation == null ){
            out.writeInt( 0 );
        }
        else{
            int count = 0;
            for( Location location : lastMaximizedLocation.values() ){
                if( location != null ){
                    count++;
                }
            }

            out.writeInt( count );
            for( Map.Entry<String, Location> entry : lastMaximizedLocation.entrySet() ){
                if( entry.getValue() != null ){
                    out.writeUTF( entry.getKey() );
                    converter.writeProperty( converter.convertToSetting( entry.getValue() ), out );
                }
            }
        }
	}

    public <B> void read( DataInputStream in, ModeSettingsConverter<Location, B> converter ) throws IOException {
        Version version = Version.read( in );
        version.checkCurrent();

        lastMaximizedLocation = new HashMap<String, Location>();
        lastMaximizedMode = new HashMap<String, Path>();

        int count = in.readInt();
        for( int i = 0; i < count; i++ ){
        	String key = in.readUTF();
        	String value = in.readUTF();
        	lastMaximizedMode.put( key, new Path( value ));
        }

        count = in.readInt();
        for( int i = 0; i < count; i++ ){
        	String key = in.readUTF();
        	Location location = converter.convertToWorld( converter.readProperty( in ) );
        	lastMaximizedLocation.put( key, location );
        }
    }

    public <B> void write( XElement element, ModeSettingsConverter<Location, B> converter ) {
        Set<String> keys = new HashSet<String>();
        if( lastMaximizedLocation != null ){
            keys.addAll( lastMaximizedLocation.keySet() );
        }
        if( lastMaximizedMode != null ){
            keys.addAll( lastMaximizedMode.keySet() );
        }

        if( !keys.isEmpty() ){
            XElement xmaximized = element.addElement( "maximized" );

            for( String key : keys ){
                Path mode = lastMaximizedMode.get( key );
                Location location = lastMaximizedLocation.get( key );

                if( mode != null || location != null ){
                    XElement xitem = xmaximized.addElement( "item" );
                    xitem.addString( "id", key );
                    if( mode != null ){
                        xitem.addElement( "mode" ).setString( mode.toString() );
                    }
                    if( location != null ){
                    	converter.writePropertyXML( converter.convertToSetting( location ), xitem.addElement( "location" ) );
                    }
                }
            }
        }
    }

    public <B> void read( XElement element, ModeSettingsConverter<Location, B> converter ) {
    	lastMaximizedLocation = new HashMap<String, Location>();
    	lastMaximizedMode = new HashMap<String, Path>();

    	XElement xmaximized = element.getElement( "maximized" );

    	if( xmaximized != null ){
    		for( XElement xitem : xmaximized.getElements( "item" )){
    			String key = xitem.getString( "id" );

    			XElement xmode = xitem.getElement( "mode" );
    			if( xmode != null ){
    				lastMaximizedMode.put( key, new Path( xmode.getString() ));
    			}

    			XElement xlocation = xitem.getElement( "location" );
    			if( xlocation != null ){
    				lastMaximizedLocation.put( key, converter.convertToWorld( converter.readPropertyXML( xlocation ) ) );
    			}
    		}
    	}
    }
}
