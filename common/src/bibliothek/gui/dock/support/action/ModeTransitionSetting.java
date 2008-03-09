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
package bibliothek.gui.dock.support.action;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

import bibliothek.gui.Dockable;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A storage device used to store a specific set of modes from a 
 * {@link ModeTransitionManager}. This setting can be used later to load
 * the set of modes again.
 * @author Benjamin Sigg
 */
public class ModeTransitionSetting<A, B> {
    /** the list of known {@link Dockable}s */
    private List<Entry> entries = new ArrayList<Entry>();
    
    /** a converter that converts properties from outside to inside */
    private ModeTransitionConverter<A, B> converter;
    
    /**
     * Creates a new setting
     * @param converter the converter to read and write properties
     */
    public ModeTransitionSetting( ModeTransitionConverter<A, B> converter ){
        if( converter == null )
            throw new IllegalArgumentException( "converter must not be null" );
        
        this.converter = converter;
    }
    
    /**
     * Gets the converter that is used to transform internal and external
     * properties.
     * @return the converter, never <code>null</code>
     */
    public ModeTransitionConverter<A, B> getConverter(){
        return converter;
    }
    
    /**
     * Adds a new set of properties to this setting.
     * @param id the unique identifier of this set of properties
     * @param current the current mode of the set
     * @param properties the properties, will be copied by this method
     * @param history older modes of the setting, will be copied by this method
     */
    public void add( String id, String current, Map<String, A> properties, Collection<String> history ){
        if( id == null )
            throw new NullPointerException( "id" );
        if( current == null )
            throw new NullPointerException( "current" );
        if( properties == null )
            throw new NullPointerException( "properties" );
        if( history == null )
            throw new NullPointerException( "history" );
        
        Entry entry = new Entry();
        entry.id = id;
        entry.current = current;
        entry.history = history.toArray( new String[ history.size() ] );
        entry.properties = new HashMap<String, B>();
        for( Map.Entry<String, A> next : properties.entrySet() ){
            entry.properties.put( next.getKey(), converter.convertToSetting( next.getValue() ) );
        }
        entries.add( entry );
    }
    
    /**
     * Gets the number of sets this setting stores.
     * @return the number of sets
     */
    public int size(){
        return entries.size();
    }
    
    /**
     * Gets the unique id of the index'th set.
     * @param index the index of the set
     * @return the unique id
     */
    public String getId( int index ){
        return entries.get( index ).id;
    }
    
    /**
     * Gets the current mode of the index'th set.
     * @param index the index of the set
     * @return the current mode
     */
    public String getCurrent( int index ){
        return entries.get( index ).current;
    }
    
    /**
     * Gets the history of the index'th set.
     * @param index the index of the set
     * @return the history
     */
    public String[] getHistory( int index ){
        return entries.get( index ).history;
    }
    
    /**
     * Gets the converted properties of the index'th set.
     * @param index the index of the set
     * @return a new map of freshly converted properties
     */
    public Map<String, A> getProperties( int index ){
        Map<String, A> result = new HashMap<String, A>();
        for( Map.Entry<String, B> entry : entries.get( index ).properties.entrySet() ){
            result.put( entry.getKey(), converter.convertToWorld( entry.getValue() ) );
        }
        return result;
    }
    
    /**
     * Writes all properties of this setting into <code>out</code>.
     * @param out the stream to write into
     * @throws IOException if an I/O-error occurs
     */
    public void write( DataOutputStream out ) throws IOException{
        Version.write( out, Version.VERSION_1_0_4 );
        
        out.writeInt( entries.size() );
        for( Entry entry : entries ){
            out.writeUTF( entry.id );
            out.writeUTF( entry.current );
            
            out.writeInt( entry.history.length );
            for( String history : entry.history )
                out.writeUTF( history );
            
            out.writeInt( entry.properties.size() );
            for( Map.Entry<String, B> next : entry.properties.entrySet() ){
                out.writeUTF( next.getKey() );
                converter.writeProperty( next.getValue(), out );
            }
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
        
        entries.clear();
        for( int i = 0, n = in.readInt(); i<n; i++ ){
            Entry entry = new Entry();
            entry.id = in.readUTF();
            entry.current = in.readUTF();
            
            entry.history = new String[ in.readInt() ];
            for( int j = 0; j < entry.history.length; j++ )
                entry.history[j] = in.readUTF();
            
            entry.properties = new HashMap<String, B>();
            for( int j = 0, m = in.readInt(); j<m; j++ ){
                String key = in.readUTF();
                B property = converter.readProperty( in );
                entry.properties.put( key, property );
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
        for( Entry entry : entries ){
            XElement xentry = element.addElement( "entry" );
            xentry.addString( "id", entry.id );
            xentry.addString( "current", entry.current );
            
            XElement xhistory = xentry.addElement( "history" );
            for( String history : entry.history ){
                xhistory.addElement( "mode" ).setString( history );
            }
            
            XElement xproperties = xentry.addElement( "properties" );
            for( Map.Entry<String, B> next : entry.properties.entrySet() ){
                XElement xproperty = xproperties.addElement( "property" );
                xproperty.addString( "id", next.getKey() );
                converter.writePropertyXML( next.getValue(), xproperty );
            }
        }
    }
    
    /**
     * Clears all properties of this setting and then reads new properties
     * from <code>element</code>.
     * @param element the element from which the properties should be read
     * @see #writeXML(XElement)
     */
    public void readXML( XElement element ){
        entries.clear();
        
        for( XElement xentry : element.getElements( "entry" )){
            Entry entry = new Entry();
            entry.id = xentry.getString( "id" );
            entry.current = xentry.getString( "current" );
            
            XElement xhistory = xentry.getElement( "history" );
            if( xhistory == null )
                entry.history = new String[]{};
            else{
                XElement[] xmodes = xhistory.getElements( "mode" );
                entry.history = new String[ xmodes.length ];
                for( int i = 0; i < xmodes.length; i++ )
                    entry.history[i] = xmodes[i].getString();
            }
            
            XElement xproperties = xentry.getElement( "properties" );
            entry.properties = new HashMap<String, B>();
            if( xproperties != null ){
                for( XElement xproperty : xproperties.getElements( "property" )){
                    entry.properties.put( xproperty.getString( "id" ), converter.readPropertyXML( xproperty ) );
                }
            }
        }
    }
    
    /**
     * The properties of one {@link Dockable}
     * @author Benjamin Sigg
     */
    private class Entry{
        /** the unique id of this entry */
        public String id;
        
        /** the current mode of this entry */
        public String current;
        
        /** a set of properties that has been built by the client */
        public Map<String, B> properties;

        /** The modes this entry already visited. No mode is more than once in this list. */
        public String[] history;
    }
}
