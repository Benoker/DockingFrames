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
package bibliothek.gui.dock.support.mode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.Dockable;
import bibliothek.util.Version;
import bibliothek.util.xml.XAttribute;
import bibliothek.util.xml.XElement;

/**
 * A set of properties extracted from a {@link ModeManager} and its {@link Mode}s. The properties
 * can be stored persistently and do not depend on any existing object (like for example the {@link Mode}s).
 * 
 * @author Benjamin Sigg
 * @param <A> the objects used by {@link ModeManager} to store information
 * @param <B> the independent objects used by this {@link ModeSettings} to store information
 */
public class ModeSettings<A,B> {
    /** the list of known {@link Dockable}s */
    private List<DockableEntry> dockables = new ArrayList<DockableEntry>();
    
    /** the list of mode information to store */
    private Map<Path, ModeSetting<A>> modes = new HashMap<Path, ModeSetting<A>>();
    
    /** a converter that converts properties from outside to inside */
    private ModeSettingsConverter<A, B> converter;
    
    /** factories for creating new {@link ModeSetting}s */
    private Map<Path, ModeSettingFactory<A>> factories = new HashMap<Path, ModeSettingFactory<A>>();
    
    /**
     * Creates a new setting
     * @param converter the converter to read and write properties
     */
    public ModeSettings( ModeSettingsConverter<A, B> converter ){
        if( converter == null )
            throw new IllegalArgumentException( "converter must not be null" );
        
        this.converter = converter;
    }
    
    /**
     * Gets the converter that is used to transform internal and external
     * properties.
     * @return the converter, never <code>null</code>
     */
    public ModeSettingsConverter<A, B> getConverter(){
        return converter;
    }
    
    /**
     * Adds a factory to this setting. The factory will be used to create 
     * new {@link ModeSetting}s.
     * @param factory the new factory, not <code>null</code>
     */
    public void addFactory( ModeSettingFactory<A> factory ){
    	factories.put( factory.getModeId(), factory );
    }
    
    /**
     * Adds a new set of properties to this setting.
     * @param id the unique identifier of this set of properties
     * @param current the current mode of the set, can be <code>null</code>
     * @param properties the properties, will be copied by this method
     * @param history older modes of the setting, will be copied by this method
     */
    public void add( String id, Path current, Map<Path, A> properties, Collection<Path> history ){
        if( id == null )
            throw new NullPointerException( "id" );
        if( properties == null )
            throw new NullPointerException( "properties" );
        if( history == null )
            throw new NullPointerException( "history" );
        
        DockableEntry entry = new DockableEntry();
        entry.id = id;
        entry.current = current;
        entry.history = history.toArray( new Path[ history.size() ] );
        entry.properties = new HashMap<Path, B>();
        for( Map.Entry<Path, A> next : properties.entrySet() ){
            entry.properties.put( next.getKey(), converter.convertToSetting( next.getValue() ) );
        }
        dockables.add( entry );
    }
    
    /**
     * Adds the settings of <code>mode</code> to this.
     * @param mode the mode whose settings are to be stored
     */
    public void add( Mode<A> mode ){
    	ModeSettingFactory<A> factory = factories.get( mode.getUniqueIdentifier() );
    	if( factory == null )
    		throw new IllegalArgumentException( "no factory present for '" + mode.getUniqueIdentifier() + "'" );
    	
    	ModeSetting<A> setting = factory.create();
    	if( setting != null ){
    		mode.writeSetting( setting );
    	
    		modes.put( setting.getModeId(), setting );
    	}
    }
    
    /**
     * Gets the number of sets this setting stores.
     * @return the number of sets
     */
    public int size(){
        return dockables.size();
    }
    
    /**
     * Gets the unique id of the index'th set.
     * @param index the index of the set
     * @return the unique id
     */
    public String getId( int index ){
        return dockables.get( index ).id;
    }
    
    /**
     * Gets the current mode of the index'th set.
     * @param index the index of the set
     * @return the current mode
     */
    public Path getCurrent( int index ){
        return dockables.get( index ).current;
    }
    
    /**
     * Gets the history of the index'th set.
     * @param index the index of the set
     * @return the history
     */
    public Path[] getHistory( int index ){
        return dockables.get( index ).history;
    }
    
    /**
     * Gets the converted properties of the index'th set.
     * @param index the index of the set
     * @return a new map of freshly converted properties
     */
    public Map<Path, A> getProperties( int index ){
        Map<Path, A> result = new HashMap<Path, A>();
        for( Map.Entry<Path, B> entry : dockables.get( index ).properties.entrySet() ){
            result.put( entry.getKey(), converter.convertToWorld( entry.getValue() ) );
        }
        return result;
    }
    
    /**
     * Gets the settings which belong to a {@link Mode} with unique
     * identifier <code>modeId</code>.
     * @param modeId the unique identifier of some mode
     * @return the settings or <code>null</code> if not found
     */
    public ModeSetting<A> getSettings( Path modeId ){
    	return modes.get( modeId );
    }
    
    /**
     * Writes all properties of this setting into <code>out</code>.
     * @param out the stream to write into
     * @throws IOException if an I/O-error occurs
     */
    public void write( DataOutputStream out ) throws IOException{
        Version.write( out, Version.VERSION_1_0_8 );
        
        out.writeInt( dockables.size() );
        for( DockableEntry entry : dockables ){
            out.writeUTF( entry.id );
            
            if( entry.current == null ){
                out.writeBoolean( false );
            }
            else{
                out.writeBoolean( true );
                out.writeUTF( entry.current.toString() );
            }
            
            out.writeInt( entry.history.length );
            for( Path history : entry.history )
                out.writeUTF( history.toString() );
            
            out.writeInt( entry.properties.size() );
            for( Map.Entry<Path, B> next : entry.properties.entrySet() ){
                out.writeUTF( next.getKey().toString() );
                converter.writeProperty( next.getValue(), out );
            }
        }
        
        out.writeInt( modes.size() );
        for( ModeSetting<A> mode : modes.values() ){
        	// storing id - byte count - bytes
        	out.writeUTF( mode.getModeId().toString() );
        	
        	ByteArrayOutputStream bout = new ByteArrayOutputStream();
        	DataOutputStream dout = new DataOutputStream( bout );
        	mode.write( dout, converter );
        	
        	out.writeInt( bout.size() );
        	bout.writeTo( out );
        	
        	dout.close();
        }
    }
    
    /**
     * Clears all properties of this setting and then reads new properties
     * from <code>in</code>.
     * @param in the stream to read from
     * @throws IOException if an I/O-error occurs
     */
    public void read( DataInputStream in ) throws IOException{
        Version version = Version.read( in );
        version.checkCurrent();
        
        dockables.clear();
        for( int i = 0, n = in.readInt(); i<n; i++ ){
            DockableEntry entry = new DockableEntry();
            dockables.add( entry );
            entry.id = in.readUTF();
            if( in.readBoolean() )
                entry.current = new Path( in.readUTF() );
            
            entry.history = new Path[ in.readInt() ];
            for( int j = 0; j < entry.history.length; j++ )
                entry.history[j] = new Path( in.readUTF() );
            
            entry.properties = new HashMap<Path, B>();
            for( int j = 0, m = in.readInt(); j<m; j++ ){
            	Path key = new Path( in.readUTF() );
                B property = converter.readProperty( in );
                entry.properties.put( key, property );
            }
        }
        
        modes.clear();
        for( int i = 0, n = in.readInt(); i<n; i++ ){
        	Path id = new Path( in.readUTF() );
        	
        	int count = in.readInt();
        	byte[] content = new byte[ count ];
        	
        	int offset = 0;
        	int length = count;
        	int read;
        	while( (length > 0) && ((read = in.read( content, offset, length )) > 0) ){
        		offset += read;
        		length -= read;
        	}
        	
        	ByteArrayInputStream bin = new ByteArrayInputStream( content );
        	DataInputStream din = new DataInputStream( bin );
        	
        	ModeSettingFactory<A> factory = factories.get( id );
        	if( factory != null ){
        		ModeSetting<A> setting = factory.create();
        		setting.read( din, converter );
        		din.close();
        		
        		modes.put( setting.getModeId(), setting );
        	}
        }
    }
    
    /**
     * Writes the contents of this setting in xml format.
     * @param element the elemnt to write into, the attributes of
     * element will not be changed.
     * @see #readXML(XElement)
     */
    public void writeXML( XElement element ){
    	XElement delement = element.addElement( "dockables" );
    	for( DockableEntry entry : dockables ){
            XElement xentry = delement.addElement( "entry" );
            xentry.addString( "id", entry.id );
            if( entry.current != null )
                xentry.addString( "current", entry.current.toString() );
            
            XElement xhistory = xentry.addElement( "history" );
            for( Path history : entry.history ){
                xhistory.addElement( "mode" ).setString( history.toString() );
            }
            
            XElement xproperties = xentry.addElement( "properties" );
            for( Map.Entry<Path, B> next : entry.properties.entrySet() ){
                XElement xproperty = xproperties.addElement( "property" );
                xproperty.addString( "id", next.getKey().toString() );
                converter.writePropertyXML( next.getValue(), xproperty );
            }
        }
    	
    	XElement melement = element.addElement( "modes" );
    	for( ModeSetting<A> mode : modes.values() ){
    		XElement xmode = melement.addElement( "entry" );
    		xmode.addString( "id", mode.getModeId().toString() );
    		mode.write( xmode, converter );
    	}
    }
    
    /**
     * Clears all properties of this setting and then reads new properties
     * from <code>element</code>.
     * @param element the element from which the properties should be read
     * @see #writeXML(XElement)
     */
    public void readXML( XElement element ){
        dockables.clear();
        XElement delement = element.getElement( "dockables" );
        if( delement != null ){
        	for( XElement xentry : delement.getElements( "entry" )){
        		DockableEntry entry = new DockableEntry();
        		dockables.add( entry );
        		entry.id = xentry.getString( "id" );
        		XAttribute current = xentry.getAttribute( "current" );
        		if( current != null )
        			entry.current = new Path( current.getString() );

        		XElement xhistory = xentry.getElement( "history" );
        		if( xhistory == null )
        			entry.history = new Path[]{};
        		else{
        			XElement[] xmodes = xhistory.getElements( "mode" );
        			entry.history = new Path[ xmodes.length ];
        			for( int i = 0; i < xmodes.length; i++ )
        				entry.history[i] = new Path( xmodes[i].getString() );
        		}

        		XElement xproperties = xentry.getElement( "properties" );
        		entry.properties = new HashMap<Path, B>();
        		if( xproperties != null ){
        			for( XElement xproperty : xproperties.getElements( "property" )){
        				entry.properties.put( new Path( xproperty.getString( "id" )), converter.readPropertyXML( xproperty ) );
        			}
        		}
        	}
        }
        
        modes.clear();
        XElement melement = element.getElement( "modes" );
        if( melement != null ){
        	for( XElement xmode : melement.getElements( "entry" )){
        		Path id = new Path( xmode.getString( "id" ));
        		ModeSettingFactory<A> factory = factories.get( id );
        		if( factory != null ){
        			ModeSetting<A> setting = factory.create();
        			setting.read( xmode, converter );
        			modes.put( setting.getModeId(), setting );
        		}
        	}
        }
    }
    
    /**
     * The properties of one {@link Dockable}
     * @author Benjamin Sigg
     */
    private class DockableEntry{
        /** the unique id of this entry */
        public String id;
        
        /** the current mode of this entry */
        public Path current;
        
        /** a set of properties that has been built by the client */
        public Map<Path, B> properties;

        /** The modes this entry already visited. No mode is more than once in this list. */
        public Path[] history;
    }
}
