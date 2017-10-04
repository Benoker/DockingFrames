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
package bibliothek.gui.dock.support.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * Manages a set of {@link ApplicationResource}s, can load and store the
 * resources at any time.<br>
 * The {@link ApplicationResource}s are organized in a {@link Map}. Each
 * resource is associated with a unique key. This key is used to determine, which
 * stream of bytes belongs to which resource. If data is loaded, the byte-streams
 * for missing resources will be stored in a buffer that is read as soon as
 * a missing resource is registered. Additional resources are ignored.<br>
 * Note that there is no order how the resources are stored in the file. 
 * @author Benjamin Sigg
 *
 */
public class ApplicationResourceManager {
    /** the map of all resources known to this manager */
    private Map<String, ApplicationResource> resources = new HashMap<String, ApplicationResource>();
    
    /** buffer for streams which are not yet read */
    private Map<String, Object> buffer = new HashMap<String, Object>();
    
    /**
     * Stores a resource that might be read or written at any time. If a stream
     * was already read by this manager, and if there was an entry in that stream
     * that equals <code>name</code>, then <code>resource</code> will immediately
     * be asked to read the stream.
     * @param name the unique identifier of the resource
     * @param resource the new resource
     * @throws NullPointerException if <code>name</code> of <code>resource</code>
     * is <code>null</code>
     * @throws IOException if the buffered stream can't be read. The resource
     * will be stored in this manager even if an exception occurs
     */
    public void put( String name, ApplicationResource resource ) throws IOException{
        if( name == null )
            throw new NullPointerException( "name must not be null" );
        if( resource == null )
            throw new NullPointerException( "resource must not be null" );
        
        resources.put( name, resource );
        Object buffered = buffer.get( name );
        if( buffered != null ){
            if( buffered instanceof byte[] ){
                ByteArrayInputStream in = new ByteArrayInputStream( (byte[])buffered );
                DataInputStream data = new DataInputStream( in );
                resource.read( data );
                data.close();
            }
            if( buffered instanceof XElement ){
                resource.readXML( (XElement)buffered );
            }
        }
    }
    
    /**
     * Removes a resources that was earlier added to this manager.
     * @param name the name of the resource to remove
     */
    public void remove( String name ){
        resources.remove( name );
    }
    
    /**
     * Writes all currently known {@link ApplicationResource}s into
     * <code>out</code>.
     * @param out the stream to write into
     * @throws IOException if the operation can't be completed
     */
    public void writeStream( DataOutputStream out ) throws IOException{
        // version
        Version.write( out, Version.VERSION_1_0_4 );
        
        // number of elements
        out.writeInt( resources.size() );
        
        // elements
        for( Map.Entry<String, ApplicationResource> resource : resources.entrySet() ){
            out.writeUTF( resource.getKey() );
            
            ByteArrayOutputStream array = new ByteArrayOutputStream();
            DataOutputStream data = new DataOutputStream( array );
            resource.getValue().write( data );
            data.close();
            
            // write out the array
            out.writeInt( array.size() );
            array.writeTo( out );
        }
        
        for( Map.Entry<String, Object> unknownResource : buffer.entrySet() ){
        	Object value = unknownResource.getValue();
        	if( value instanceof byte[] ){
        		byte[] array = (byte[])value;
        		
        		out.writeUTF( unknownResource.getKey() );
        		out.writeInt( array.length );
        		out.write( array );
        	}
        }
    }
    
    /**
     * Lets all {@link ApplicationResource}s read from <code>in</code>.
     * @param in the stream to read from
     * @throws IOException if the operation can't be completed
     */
    public void readStream( DataInputStream in ) throws IOException{
        Version version = Version.read( in );
        version.checkCurrent();
        
        int size = in.readInt();
        for( int i = 0; i < size; i++ ){
            String key = in.readUTF();
            
            int length = in.readInt();
            byte[] input = new byte[ length ];
            in.readFully( input );
            ApplicationResource resource = resources.get( key );
            if( resource != null ){
                ByteArrayInputStream array = new ByteArrayInputStream( input );
                DataInputStream data = new DataInputStream( array );
                resource.read( data );
                data.close();
            }
            else
                buffer.put( key, input );
        }
    }
    
    /**
     * Writes the content of this manager in xml format.
     * @param element the element to write into, the attributes of this
     * element will not be changed.
     */
    public void writeXML( XElement element ){
        for( Map.Entry<String, ApplicationResource> resource : resources.entrySet() ){
            XElement xresource = element.addElement( "resource" );
            xresource.addString( "name", resource.getKey() );
            resource.getValue().writeXML( xresource );
        }
        
        for( Map.Entry<String, Object> unknownResource : buffer.entrySet() ){
        	Object value = unknownResource.getValue();
        	if( value instanceof XElement ){
        		XElement xvalue = (XElement)value;
        		element.addElement( xvalue );
        	}
        }
    }
    
    /**
     * Reads the contents of this manager from a xml element.
     * @param element the element to read
     */
    public void readXML( XElement element ){
        for( XElement xresource : element.getElements( "resource" )){
            String name = xresource.getString( "name" );
            ApplicationResource resource = resources.get( name );
            if( resource != null ){
                resource.readXML( xresource );
            }
            else
                buffer.put( name, xresource );
        }
    }
    
    /**
     * Writes the contents of this manager into <code>file</code>.
     * @param file the file to write into
     * @throws IOException if the operation can't be completed
     */
    public void writeFile( File file ) throws IOException{
        DataOutputStream out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream( file )));
        try{
            writeStream( out );
        }
        finally{
            out.close();
        }
    }
    
    /**
     * Reads the contents of this manager from <code>file</code>.
     * @param file the file to read
     * @throws IOException if the operation can't be completed
     */
    public void readFile( File file ) throws IOException{
        DataInputStream in = new DataInputStream( new BufferedInputStream( new FileInputStream( file )));
        try{
            readStream( in );
        }
        finally{
            in.close();
        }
    }
    
    /**
     * Writes the contents of this manager into an array of bytes.
     * @return the contents as stream of bytes
     * @throws IOException if the operation can't be completed
     */
    public byte[] writeArray() throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream( out );
        writeStream( data );
        data.close();
        return out.toByteArray();
    }
    
    /**
     * Reads the contents of this manager from an array of bytes.
     * @param array the content as stream of bytes
     * @throws IOException if the operation can't be completed
     */
    public void readArray( byte[] array ) throws IOException{
        ByteArrayInputStream in = new ByteArrayInputStream( array );
        DataInputStream data = new DataInputStream( in );
        readStream( data );
        data.close();
    }
    
    /**
     * Writes the contents of this manager into the {@link Preferences} which
     * represent the package of {@link ApplicationResourceManager}.
     * @throws IOException if the operation can't be completed
     */
    public void writePreferences() throws IOException{
        Preferences preference = Preferences.userNodeForPackage( ApplicationResourceManager.class );
        preference.putByteArray( "content", writeArray() );
    }
    
    /**
     * Reads the content of this manager from the {@link Preferences} that
     * represent the package of {@link ApplicationResourceManager}.
     * @throws IOException if the operation can't be completed
     */
    public void readPreferences() throws IOException{
        Preferences preference = Preferences.userNodeForPackage( ApplicationResourceManager.class );
        byte[] array = preference.getByteArray( "content", null );
        if( array != null )
            readArray( array );
    }
}




















