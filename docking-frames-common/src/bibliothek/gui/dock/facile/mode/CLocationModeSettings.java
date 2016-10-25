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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.dock.common.mode.CMaximizedMode;
import bibliothek.gui.dock.support.mode.ModeSettings;
import bibliothek.gui.dock.support.mode.ModeSettingsConverter;
import bibliothek.util.FrameworkOnly;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * This {@link ModeSettings} provides operations to load settings that were
 * stored with version 1.0.7, no additional settings were added.
 * 
 * @author Benjamin Sigg
 * @param <B> the intermediate format used for properties by the {@link ModeSettingsConverter}
 */
@FrameworkOnly
public class CLocationModeSettings<B> extends ModeSettings<Location,B>{
	/** rescued setting from old version */
	private Map<String, B> lastMaximizedLocation;
	/** rescued setting from old version */
	private Map<String, String> lastMaximizedMode;

	/** key for the minimized mode */
    private static final String MINIMIZED = "mini";

    /** key for the maximized mode */
    private static final String MAXIMIZED = "maxi";

    /** key for the normalized mode */
    private static final String NORMALIZED = "normal";

    /** key for the externalized mode */
    private static final String EXTERNALIZED = "extern";
	
	/**
	 * Creates a new setting.
	 * @param converter conversion tool for meta data
	 */
	public CLocationModeSettings( ModeSettingsConverter<Location, B> converter ){
		super( converter );
	}
	
	/**
	 * If there were settings rescued from an older version, then
	 * these settings are transferred to <code>maximizedMode</code>.
	 * @param maximizedMode the mode to store settings in
	 */
	public void rescue( CMaximizedMode maximizedMode ){
		if( lastMaximizedLocation != null ){
			MaximizedModeSetting setting = new MaximizedModeSetting();
			
			Map<String, Path> translatedLastMaximizedMode = new HashMap<String, Path>();
			for( Map.Entry<String, String> pair : lastMaximizedMode.entrySet() ){
				String mode = pair.getValue();
				Path result = resuceMode( mode );
				
				if( result != null ){
					translatedLastMaximizedMode.put( pair.getKey(), result );
				}
			}
			
			Map<String, Location> translatedLastMaximizedLocation = new HashMap<String, Location>();
			for( Map.Entry<String, B> pair : lastMaximizedLocation.entrySet() ){
				Location location = getConverter().convertToWorld( pair.getValue() );
				translatedLastMaximizedLocation.put( pair.getKey(), location );
			}
			
			// ensure keys are used in both maps or not at all
			translatedLastMaximizedLocation.keySet().retainAll( translatedLastMaximizedMode.keySet() );
			translatedLastMaximizedMode.keySet().retainAll( translatedLastMaximizedLocation.keySet() );
			
			// set settings
			setting.setLastMaximizedLocation( translatedLastMaximizedLocation );
			setting.setLastMaximizedMode( translatedLastMaximizedMode );
			
			maximizedMode.readSetting( setting );
		}
	}

	@Override
	protected Path resuceMode( String mode ){
		if( MINIMIZED.equals( mode )){
			return MinimizedMode.IDENTIFIER;
		}
		else if( NORMALIZED.equals( mode )){
			return NormalMode.IDENTIFIER;
		}
		else if( EXTERNALIZED.equals( mode )){
			return ExternalizedMode.IDENTIFIER;
		}
		else if( MAXIMIZED.equals( mode )){
			return MaximizedMode.IDENTIFIER;
		}
		return null;
	}
	
	@Override
	protected void rescueSettings( DataInputStream in, Version version ) throws IOException{
        if( version.compareTo( Version.VERSION_1_0_7 ) < 0 ){
        	// ignore these old settings
            if( in.readBoolean() ){
                in.readUTF();
            }

            if( in.readBoolean() ){
                getConverter().readProperty( in );
            }
        }
        else if( version.compareTo( Version.VERSION_1_0_7 ) == 0 ){
            // read settings from version 1.0.7 and translate them into
        	// the modes that are now used
        	
        	lastMaximizedLocation = new HashMap<String, B>();
        	lastMaximizedMode = new HashMap<String, String>();
        	
        	int count = in.readInt();
            for( int i = 0; i < count; i++ ){
                String key = in.readUTF();
                String value = in.readUTF();
                lastMaximizedMode.put( key, value );
            }

            count = in.readInt();
            for( int i = 0; i < count; i++ ){
                String key = in.readUTF();
                B value = getConverter().readProperty( in );
                lastMaximizedLocation.put( key, value );
            }
        }
	}
	
	@Override
	protected void rescueSettings( XElement element ){
        XElement states = element.getElement( "states" );
        if( states != null ){
            lastMaximizedLocation = new HashMap<String, B>();
            lastMaximizedMode = new HashMap<String, String>();

            XElement xmaximized = element.getElement( "maximized" );

            if( xmaximized != null ){
                for( XElement xitem : xmaximized.getElements( "item" )){
                    String key = xitem.getString( "id" );

                    XElement xmode = xitem.getElement( "mode" );
                    if( xmode != null ){
                        lastMaximizedMode.put( key, xmode.getString() );
                    }

                    XElement xlocation = xitem.getElement( "location" );
                    if( xlocation != null ){
                    	XElement xcopy = xlocation.copy();
                    	xcopy.addElement( "mode" ).setString( MaximizedMode.IDENTIFIER.toString() );
                    	lastMaximizedLocation.put( key, getConverter().readPropertyXML( xcopy ) );
                    }
                }
            }
        }
    }
}
