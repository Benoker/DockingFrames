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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.dock.security.SecureFlapDockStationFactory;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.station.StackDockStation;
import bibliothek.gui.dock.station.flap.FlapDockStationFactory;
import bibliothek.gui.dock.station.split.SplitDockStationFactory;
import bibliothek.gui.dock.station.stack.StackDockStationFactory;

/**
 * A DockSituation is a converter: the relationship of 
 * {@link DockStation DockStations} and {@link Dockable Dockables},
 * the position of Dockables and other information are converted into a 
 * stream of bytes. The other direction, read a stream
 * and create Dockables and DockStations, is also possible.
 * @author Benjamin Sigg
 */
public class DockSituation {
    /** the factories used to create new {@link DockElement elements}*/
    private Map<String, DockFactory<?>> factories = new HashMap<String, DockFactory<?>>();
    
    /** a filter for elements which should be ignored */
    private DockSituationIgnore ignore;
    
    /**
     * Constructs a new DockSituation and sets some factories which are
     * used to create new {@link DockElement DockElements}
     * @param factories the factories
     */
    public DockSituation( DockFactory<?>...factories ){
    	for( DockFactory<?> factory : factories )
            this.factories.put( getID( factory ), factory );
    }
    
    /**
     * Constructs a new DockSituation. Factories for {@link DefaultDockable},
     * {@link SplitDockStation}, {@link StackDockStation} and
     * {@link FlapDockStation} will be preinstalled.
     */
    public DockSituation(){
        this( 
                new DefaultDockableFactory(),
                new SplitDockStationFactory(),
                new StackDockStationFactory(),
                new FlapDockStationFactory(),
                new SecureFlapDockStationFactory());
    }
    
    /**
     * Sets a filter which decides, which elements (stations and dockables)
     * are stored.
     * @param ignore the filter or <code>null</code>
     */
    public void setIgnore( DockSituationIgnore ignore ) {
        this.ignore = ignore;
    }
    
    /**
     * Gets the filter which decides, which elements are stored.
     * @return the filter or <code>null</code>
     */
    public DockSituationIgnore getIgnore() {
        return ignore;
    }
    
    /**
     * Adds a factory
     * @param factory the additional factory
     */
    public void add( DockFactory<?> factory ){
        factories.put( getID( factory ), factory );
    }
    
    /**
     * Writes all locations and relationships of the {@link DockStation DockStations}
     * <code>stations</code> and their children into an array of bytes.
     * @param stations The stations to store, a call to {@link #read(byte[])}
     * would return the same map. Only the roots are needed.
     * @return the information as an array of bytes
     * @throws IOException if the information can't be written
     */
    public byte[] write( Map<String, DockStation> stations ) throws IOException{
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( bytes );
        write( stations, out );
        out.close();
        return bytes.toByteArray();
    }
    
    /**
     * Writes all locations and relationships of the {@link DockStation DockStations}
     * <code>stations</code> and their children into the stream <code>out</code>.
     * @param stations The stations to store, only the roots are needed.
     * @param out the stream to write in
     * @throws IOException if the stream throws an exception
     */
    public void write( Map<String, DockStation> stations, DataOutputStream out ) throws IOException{
        out.writeInt( stations.size() );
        for( Map.Entry<String, DockStation> entry : stations.entrySet() ){
            out.writeUTF( entry.getKey() );
            writeElement( entry.getValue(), out );
        }
    }
    
    /**
     * Writes the children of <code>station</code> and returns a map which
     * contains all children and a unique id for each child.
     * @param station the station whose children are written
     * @param ignoreChildren <code>true</code> if no information about the
     * children should be saved.
     * @param out the stream to write into
     * @return a map containing all children of <code>station</code>
     * @throws IOException if the stream throws an exception
     */
    private Map<Dockable, Integer> writeStation( DockStation station, boolean ignoreChildren, DataOutputStream out ) throws IOException{
        Map<Dockable, Integer> result = new HashMap<Dockable, Integer>();
        int count = station.getDockableCount();
        
        if( ignoreChildren ){
            out.writeInt( 0 );
            for( int i = 0; i < count; i++ ){
                Dockable dockable = station.getDockable( i );
                result.put( dockable, -1 );
            }
        }
        else{
            out.writeInt( count );
            
            for( int i = 0; i<count; i++ ){
                Dockable dockable = station.getDockable( i );
                result.put( dockable, i );
                out.writeInt( i );
                writeElement( dockable, out );
            }
        }
        
        return result;
    }
    
    /**
     * Writes the contents of <code>element</code> into <code>out</code>.
     * @param element the element to store
     * @param out the stream to write into
     * @throws IOException if the stream throws an exception or the 
     * <code>element</code> can't be converted
     */
    private void writeElement( DockElement element, DataOutputStream out ) throws IOException{
        if( ignoreElement( element ))
            out.writeBoolean( false );
        else{
            out.writeBoolean( true );
            
            DockStation station = element.asDockStation();
            Map<Dockable, Integer> children = null;
            out.writeBoolean( station != null );
            boolean ignore = false;
            if( station != null ){
                ignore = ignoreChildren( station );
                out.writeBoolean( ignore );
                children = writeStation( station, ignore, out );
            }
            
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream datas = new DataOutputStream( bytes );
            
            try{
                @SuppressWarnings( "unchecked" )
                DockFactory<DockElement> factory = (DockFactory<DockElement>)getFactory( getID( element ) );
                
                if( factory == null )
                    throw new IOException( "Unknown factory-id: " + element.getFactoryID() );
                
                factory.write( element, children, datas );
                
                datas.close();
                
                byte[] written = bytes.toByteArray();
                out.writeUTF( getID( factory ));
                
                Dockable front = station == null ? null : station.getFrontDockable();
                out.writeBoolean( front != null );
                if( front != null )
                    out.writeInt( children.get( front ));
                
                out.writeInt( written.length );
                out.write( written );
            }
            catch( ClassCastException ex ){
                throw new IOException( "Factory " + 
                        getFactory( getID( element )) + " does not handle " + element );
            }
        }
    }
    
    /**
     * Reads <code>data</code> as stream and returns the roots of the
     * {@link DockElement DockElements} which were found. 
     * @param data the array to read
     * @return the root stations which were found
     * @throws IOException if <code>data</code> can't be read
     */
    public Map<String, DockStation> read( byte[] data ) throws IOException{
        DataInputStream in = new DataInputStream( new ByteArrayInputStream( data ));
        Map<String, DockStation> result = read( in );
        in.close();
        return result;
    }
    
    /**
     * Reads <code>in</code> and returns the roots of the
     * {@link DockElement DockElements} which were found. 
     * @param in the stream to read
     * @return the roots of all elements that were found
     * @throws IOException if the stream can't be read
     */
    public Map<String, DockStation> read( DataInputStream in ) throws IOException{
        int count = in.readInt();
        Map<String, DockStation> result = new HashMap<String, DockStation>();
        for( int i = 0; i < count; i++ ){
            String key = in.readUTF();
            DockElement element = readElement( in );
            DockStation station = element == null ? null : element.asDockStation();
            
            if( station != null )
                result.put( key, station );
        }
        return result;
    }
    
    /**
     * Reads the contents of one {@link DockElement}. Note if there
     * is a {@link DockFactory} missing, <code>null</code> will be returned.  
     * @param in the stream to read
     * @return the read element or <code>null</code>
     * @throws IOException if the element can't be read because of a 
     * malfunction of <code>in</code>
     */
    private DockElement readElement( DataInputStream in ) throws IOException{
        if( !in.readBoolean() )
            return null;
        
        boolean isStation = in.readBoolean();
        boolean ignoreChildren = false;
        Map<Integer, Dockable> children = null;
        if( isStation ){
            ignoreChildren = in.readBoolean();
            children = readDockables( in );
        }
        
        String id = in.readUTF();
        Dockable front = null;
        if( in.readBoolean() )
            front = children.get( in.readInt() );
            
        int length = in.readInt();
        byte[] written = new byte[ length ];
        
        int index = 0;
        while( index < length ){
            int read = in.read( written, index, length-index );
            if( read == -1 )
                throw new EOFException();
            index += read;
        }
        
        DockFactory<?> factory = getFactory( id );
        if( factory == null )
            return null;
        
        DataInputStream datas = new DataInputStream( new ByteArrayInputStream( written ));
        DockElement element = factory.read( children, ignoreChildren, datas );
        
        DockStation station = element == null ? null : element.asDockStation();
        if( station != null )
            station.setFrontDockable( front );
        
        datas.close();
        return element;
    }
    
    /**
     * Reads the next list of {@link Dockable Dockables}.
     * @param in the stream to read
     * @return a map containing some instances of {@link Dockable}
     * @throws IOException if the stream throws an exception
     */
    private Map<Integer, Dockable> readDockables( DataInputStream in ) throws IOException{
        int count = in.readInt();
        
        Map<Integer, Dockable> result = new HashMap<Integer, Dockable>();
        
        for( int i = 0; i < count; i++ ){
            int id = in.readInt();
            DockElement element = readElement( in );
            Dockable dockable = element == null ? null : element.asDockable();
            if( dockable != null )
                result.put( id, dockable );
        }
        
        return result;
    }
    
    /**
     * Tells whether to ignore this element when saving. If an element is ignored, no 
     * factory is needed for it. This implementation forwards
     * the call to the {@link DockSituationIgnore} of this situation.
     * @param element the element which might not be saved
     * @return <code>true</code> if the element should not be saved
     */
    protected boolean ignoreElement( DockElement element ){
        if( ignore == null )
            return false;
        
        return ignore.ignoreElement( element );
    }
    
    /**
     * Tells whether to ignore the children of the station when saving or not. If the children
     * are ignored, no factories are needed for them. This implementation forwards
     * the call to the {@link DockSituationIgnore} of this situation.
     * @param station the station whose children might be ignored
     * @return <code>true</code> if the station is saved as having no children
     */
    protected boolean ignoreChildren( DockStation station ){
        if( ignore == null )
            return false;
        
        return ignore.ignoreChildren( station );
    }
    
    /**
     * Gets the id of the factory which is needed to write (and later
     * read) the element <code>dockable</code>.
     * @param dockable the dockable to write
     * @return the id of the factory
     * @see #getID(DockFactory)
     * @see #getFactory(String)
     */
    protected String getID( DockElement dockable ){
        return dockable.getFactoryID();
    }
    
    /**
     * Gets the id of <code>factory</code>. The default behavior is just to
     * return {@link DockFactory#getID()}. Note that this method should be
     * a bijection to {@link #getFactory(String)}.
     * @param factory the factory whose id is needed
     * @return the id of the factory
     */
    protected String getID( DockFactory<?> factory ){
        return factory.getID();
    }
    
    /**
     * Gets the factory which has the given <code>id</code>. Note that this
     * method should be a bijection to {@link #getID(DockFactory)}. The 
     * default behavior compares <code>id</code> with the 
     * {@link #getID(DockFactory)}.
     * @param id the name of the factory
     * @return the factory or <code>null</code> if no factory has this id
     */
    protected DockFactory<? extends DockElement> getFactory( String id ){
    	return factories.get( id );
    }
}
