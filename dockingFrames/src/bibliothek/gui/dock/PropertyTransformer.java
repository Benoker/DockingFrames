/**
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

package bibliothek.gui.dock;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.dock.station.flap.FlapDockProperty;
import bibliothek.gui.dock.station.flap.FlapDockPropertyFactory;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.station.split.SplitDockPropertyFactory;
import bibliothek.gui.dock.station.stack.StackDockProperty;
import bibliothek.gui.dock.station.stack.StackDockPropertyFactory;

/**
 * A PropertTransformer can read and write instances of {@link DockableProperty},
 * assuming that a factory is installed for the property.
 * @author Benjamin Sigg
 *
 */
public class PropertyTransformer {
    private Map<String, DockablePropertyFactory> factories = 
        new HashMap<String, DockablePropertyFactory>();
    
    /**
     * Creates a new transformer, the factories for {@link SplitDockProperty},
     * {@link StackDockProperty} and {@link FlapDockProperty} are
     * installed.
     */
    public PropertyTransformer(){
        this(
                SplitDockPropertyFactory.FACTORY,
                StackDockPropertyFactory.FACTORY,
                FlapDockPropertyFactory.FACTORY );
    }
    
    /**
     * Creates a new transformer and installs <code>factories</code>.
     * @param factories a list of factories to install
     */
    public PropertyTransformer( DockablePropertyFactory...factories ){
        for( DockablePropertyFactory factory : factories )
            this.factories.put( factory.getID(), factory );
    }
    
    /**
     * Installs a factory
     * @param factory the new factory
     */
    public void addFactory( DockablePropertyFactory factory ){
        factories.put( factory.getID(), factory );
    }
    
    
    /**
     * Writes <code>property</code> and all its successors into <code>out</code>.
     * @param property the property to write
     * @param out a stream to write into
     * @throws IOException if the stream throws an exception
     */
    public void write( DockableProperty property, DataOutputStream out ) throws IOException{
        int count = 0;
        DockableProperty successor = property;
        while( successor != null ){
            count++;
            successor = successor.getSuccessor();
        }
        
        out.writeInt( count );
        while( property != null ){
            out.writeUTF( property.getFactoryID() );
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream datas = new DataOutputStream( bytes );
            property.store( datas );
            datas.close();
            byte[] written = bytes.toByteArray();
            out.writeInt( written.length );
            out.write( written );
            
            property = property.getSuccessor();
        }
    }
    
    /**
     * Reads a property which was earlier stored. If the property had
     * any successors, then they are read as well.
     * @param in a stream to read from
     * @return the properties
     * @throws IOException if the property can't be read
     */
    public DockableProperty read( DataInputStream in ) throws IOException{
        int count = in.readInt();
        
        DockableProperty property = null;
        DockableProperty base = null;
        
        for( int i = 0; i < count; i++ ){
            String id = in.readUTF();
            DockablePropertyFactory factory = factories.get( id );
            if( factory == null )
                throw new IOException( "Unknown factory-id: " + id );
            
            DockableProperty temp = factory.createProperty();
            
            int length = in.readInt();
            byte[] data = new byte[ length ];
            int index = 0;
            
            while( index < length ){
                int read = in.read( data, index, length-index );
                if( read < 0 )
                    throw new EOFException();
                index += read;
            }
            
            DataInputStream datas = new DataInputStream( new ByteArrayInputStream( data ));
            temp.load( datas );
            datas.close();
            
            if( base == null ){
                base = temp;
                property = temp;
            }
            else{
                property.setSuccessor( temp );
                property = temp;
            }
        }
        
        return base;
    }
}
