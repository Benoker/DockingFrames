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

package bibliothek.gui.dock.layout;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.*;
import bibliothek.gui.dock.dockable.DefaultDockableFactory;
import bibliothek.gui.dock.security.SecureFlapDockStationFactory;
import bibliothek.gui.dock.security.SecureSplitDockStationFactory;
import bibliothek.gui.dock.security.SecureStackDockStationFactory;
import bibliothek.gui.dock.station.flap.FlapDockStationFactory;
import bibliothek.gui.dock.station.split.SplitDockStationFactory;
import bibliothek.gui.dock.station.stack.StackDockStationFactory;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

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
    private Map<String, DockFactory<?,?>> factories = new HashMap<String, DockFactory<?,?>>();
    
    /** the factory used when no {@link DockFactory} is available */
    private MissingDockFactory missingFactory;
    
    /** a set of additional factories for the {@link DockElement}s */
    private Map<String, AdjacentDockFactory<?>> adjacent = new HashMap<String, AdjacentDockFactory<?>>();
    
    /** the factory used when no {@link AdjacentDockFactory} is available */
    private MissingDockFactory missingAdjacent;
    
    /** a filter for elements which should be ignored */
    private DockSituationIgnore ignore;
    
    /**
     * Constructs a new DockSituation and sets some factories which are
     * used to create new {@link DockElement DockElements}
     * @param factories the factories
     */
    public DockSituation( DockFactory<?,?>...factories ){
    	for( DockFactory<?,?> factory : factories )
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
                new SecureSplitDockStationFactory(),
                new StackDockStationFactory(),
                new SecureStackDockStationFactory(),
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
    public void add( DockFactory<?,?> factory ){
        factories.put( getID( factory ), factory );
    }
    
    /**
     * Adds an adjacent factory
     * @param factory the new factory
     */
    public void addAdjacent( AdjacentDockFactory<?> factory ){
        adjacent.put( getAdjacentID( factory ), factory );
    }
    
    /**
     * Sets a factory which is used whenever no ordinary {@link DockFactory}
     * can be found to read something. The <code>missingFactory</code> can convert
     * the input to any {@link Object} it likes, but if a missing factory
     * is later added to this situation, then that object needs to be casted
     * into the object used by the original factory. So when working with
     * a {@link MissingDockFactory} handling different types of layout-data
     * needs to be done very carefully. Note that this factory cannot be
     * used to convert {@link DockLayoutComposition}s {@link #convert(DockLayoutComposition) to} 
     * or {@link #convert(DockElement) from} {@link DockElement}s.
     * So using a {@link MissingDockFactory} without looking at a {@link DockLayoutComposition}
     * is pointless.
     * @param missingFactory the factory, can be <code>null</code>
     */
    public void setMissingFactory( MissingDockFactory missingFactory ) {
        this.missingFactory = missingFactory;
    }
    
    /**
     * Gets the factory which is used when another factory is missing.
     * @return the factory replacing missing factories, can be <code>null</code>
     * @see #setMissingFactory(MissingDockFactory)
     */
    public MissingDockFactory getMissingFactory() {
        return missingFactory;
    }
    
    /**
     * Sets a factory which is used when a {@link AdjacentDockFactory} is missing.
     * There are the same issures with this factory than with the one used
     * by {@link #setMissingFactory(MissingDockFactory)}.
     * @param missingAdjacent the new factory, can be <code>null</code>
     * @see #setMissingFactory(MissingDockFactory)
     */
    public void setMissingAdjacentFactory( MissingDockFactory missingAdjacent ) {
        this.missingAdjacent = missingAdjacent;
    }
    
    /**
     * Gets the factory which is used when another {@link AdjacentDockFactory}
     * is missing.
     * @return the factory, can be <code>null</code>
     * @see #setMissingAdjacentFactory(MissingDockFactory)
     */
    public MissingDockFactory getMissingAdjacentFactory() {
        return missingAdjacent;
    }
    
    /**
     * Converts the layout of <code>element</code> and all its children into a 
     * {@link DockLayoutComposition}.
     * @param element the element to convert
     * @return the composition or <code>null</code> if the element is ignored
     * @throws IllegalArgumentException if one element has an unknown id of
     * a {@link DockFactory}.
     * @throws ClassCastException if an element does not specify the correct
     * {@link DockFactory}.
     */
    @SuppressWarnings("unchecked")
    public DockLayoutComposition convert( DockElement element ){
        if( ignoreElement( element ))
            return null;
        
        String id = getID( element );
        DockFactory<DockElement,Object> factory = (DockFactory<DockElement, Object>)getFactory( id );
        if( factory == null )
            throw new IllegalArgumentException( "Unknown factory-id: " + element.getFactoryID() );
        
        DockStation station = element.asDockStation();
        Map<Dockable, Integer> ids = new HashMap<Dockable, Integer>();
        List<DockLayoutComposition> children = new ArrayList<DockLayoutComposition>();
        
        boolean ignore = false;
        
        if( station != null ){
            ignore = ignoreChildren( station );
            if( !ignore ){
                int index = 0;
                for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
                    Dockable dockable = station.getDockable( i );
                    DockLayoutComposition composition = convert( dockable );
                    if( composition != null ){
                        children.add( composition );
                        ids.put( dockable, index++ );
                    }
                }
            }
        }
        
        Object data = factory.getLayout( element, ids );
        DockLayout<Object> layout = new DockLayout<Object>( id, data );
        
        List<DockLayout<?>> adjacent = null;
        for( AdjacentDockFactory<?> adjacentFactory : this.adjacent.values() ){
            if( adjacentFactory.interested( element )){
                Object adjacentData = adjacentFactory.getLayout( element, ids );
                if( adjacent == null ){
                    adjacent = new ArrayList<DockLayout<?>>();
                }
                adjacent.add( new DockLayout<Object>( getAdjacentID( adjacentFactory ), adjacentData ) );
            }
        }
        
        return new DockLayoutComposition( new DockLayoutInfo( layout ), adjacent, children, ignore );
    }
    
    /**
     * Reads the contents of <code>composition</code> and tries to create a
     * {@link DockElement} that matches the composition.
     * @param composition the composition to analyze
     * @return the new element, can be something else then a {@link DockElement}
     * if the factory for <code>composition</code> was not found
     */
    @SuppressWarnings("unchecked")
    public DockElement convert( DockLayoutComposition composition ){
    	DockLayoutInfo info = composition.getLayout();
    	if( info == null )
    		return null;
    	
        DockLayout<?> layout = info.getDataLayout();
        if( layout == null )
            return null;
        
        DockFactory<DockElement, Object> factory = (DockFactory<DockElement, Object>)getFactory( layout.getFactoryID() );
        if( factory == null )
            return null;
        
        DockElement result = null;
        Map<Integer, Dockable> children = null;
        
        if( composition.isIgnoreChildren() ){
            for( DockLayoutComposition childComposition : composition.getChildren() ){
                convert( childComposition );
            }
            
            result = factory.layout( layout.getData() );
        }
        else{
            children = new HashMap<Integer, Dockable>();
            int index = 0;
            
            for( DockLayoutComposition childComposition : composition.getChildren() ){
                DockElement child = convert( childComposition );
                if( child != null ){
                	Dockable dockable = child.asDockable();
                	if( dockable != null ){
                		children.put( index, dockable );
                	}
                }
                
                index++;
            }
            
            result = factory.layout( layout.getData(), children );
        }
        
        if( result != null ){
            List<DockLayout<?>> adjacent = composition.getAdjacent();
            if( adjacent != null ){
                for( DockLayout<?> adjacentLayout : adjacent ){
                    AdjacentDockFactory<Object> adjacentFactory = (AdjacentDockFactory<Object>)getAdjacentFactory( adjacentLayout.getFactoryID() );
                    if( adjacentFactory != null ){
                        if( children == null ){
                            adjacentFactory.setLayout( result, adjacentLayout.getData() );
                        }
                        else{
                            adjacentFactory.setLayout( result, adjacentLayout.getData(), children );
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Writes the contents of <code>composition</code> and all its children
     * to <code>out</code>.
     * @param composition the composition to write, should be created by
     * <code>this</code> {@link DockSituation} or a <code>DockSituation</code> with
     * similar properties.
     * @param out the stream to write into
     * @throws IOException if an I/O-error occurs
     */
    @SuppressWarnings("unchecked")
    public void writeComposition( DockLayoutComposition composition, DataOutputStream out ) throws IOException{
        Version.write( out, Version.VERSION_1_0_7 );
        writeCompositionStream( composition, out );
    }
    
    /**
     * Writes the contents of <code>composition</code> and all its children
     * to <code>out</code>.
     * @param composition the composition to write, should be created by
     * <code>this</code> {@link DockSituation} or a <code>DockSituation</code> with
     * similar properties.
     * @param out the stream to write into
     * @throws IOException if an I/O-error occurs
     */
    @SuppressWarnings("unchecked")
    private void writeCompositionStream( DockLayoutComposition composition, DataOutputStream out ) throws IOException{
    	DockLayoutInfo info = composition.getLayout();
    	if( info.getKind() == DockLayoutInfo.Data.BYTE ){
    		out.write( info.getDataByte() );
    	}
    	else if( info.getKind() == DockLayoutInfo.Data.DOCK_LAYOUT ){
    		DockLayout<?> layout = info.getDataLayout();
    		DockFactory<DockElement, Object> factory = (DockFactory<DockElement, Object>)getFactory( layout.getFactoryID() );
    		if( factory == null )
    			throw new IOException( "Missing factory: " + layout.getFactoryID() );

    		// factory
    		out.writeUTF( getID( factory ) );

    		// contents
    		ByteArrayOutputStream bout = new ByteArrayOutputStream();
    		DataOutputStream dout = new DataOutputStream( bout );
    		factory.write( layout.getData(), dout );
    		dout.close();

    		out.writeInt( bout.size() );
    		bout.writeTo( out );
    	}
    	else{
        	// there is nothing to write...
        	throw new IllegalArgumentException( "Cannot store layout in stream: it was never translated and its raw format is not a byte stream" );
    	}

        // adjacent
        List<DockLayout<?>> adjacentLayouts = composition.getAdjacent();
        if( adjacentLayouts == null ){
            out.writeInt( 0 );
        }
        else{
            out.writeInt( adjacentLayouts.size() );
            for( DockLayout<?> adjacentLayout : adjacentLayouts ){
                AdjacentDockFactory<Object> adjacentFactory = (AdjacentDockFactory<Object>)getAdjacentFactory( adjacentLayout.getFactoryID() );
                if( adjacentFactory == null )
                    throw new IOException( "Missing adjacent factory: " + adjacentLayout.getFactoryID() );
                
                ByteArrayOutputStream adjacentBOut = new ByteArrayOutputStream();
                DataOutputStream adjacentOut = new DataOutputStream( adjacentBOut );
                adjacentFactory.write( adjacentLayout.getData(), adjacentOut );
                adjacentOut.close();
                
                out.writeUTF( getAdjacentID( adjacentFactory ) );
                out.writeInt( adjacentBOut.size() );
                adjacentBOut.writeTo( out );
            }
        }
        
        // ignore
        out.writeBoolean( composition.isIgnoreChildren() );
        
        // children
        List<DockLayoutComposition> children = composition.getChildren();
        out.writeInt( children.size() );
        for( DockLayoutComposition child : children ){
            writeCompositionStream( child, out );
        }
    }
    
    /**
     * Reads one {@link DockLayoutComposition} and all its children.
     * @param in the stream to read from
     * @return the new composition or <code>null</code> if the factory was missing
     * @throws IOException if an I/O-error occurs
     */
    public DockLayoutComposition readComposition( DataInputStream in ) throws IOException{
        Version version = Version.read( in );
        version.checkCurrent();
        return readCompositionStream( in, version );
    }
    
    /**
     * Reads one {@link DockLayoutComposition} and all its children.
     * @param in the stream to read from
     * @param version the format of <code>in</code>
     * @return the new composition or <code>null</code> if the factory was missing
     * @throws IOException if an I/O-error occurs
     */
    @SuppressWarnings("unchecked")
    private DockLayoutComposition readCompositionStream( DataInputStream in, Version version ) throws IOException{
        // factory
        String factoryId = in.readUTF();
        DockFactory<DockElement, Object> factory = (DockFactory<DockElement, Object>)getFactory( factoryId );
        
        // contents
        DockLayoutInfo info;
        int count = in.readInt();
        
        if( factory == null ){
        	// copy contents to store byte[]
        	ByteArrayOutputStream byteOutput = new ByteArrayOutputStream( count + 4 + factoryId.length() * 4 );
        	DataOutputStream byteDataOutput = new DataOutputStream( byteOutput );
        	
        	byteDataOutput.writeUTF( factoryId );
        	byteDataOutput.writeInt( count );
        	for( int i = 0; i < count; i++ ){
        		int read = in.read();
        		if( read == -1 )
        			throw new EOFException();
        		byteDataOutput.write( read );
        	}
        	byteDataOutput.close();
        	byte[] dataArray = byteOutput.toByteArray();
        	
        	// try read
        	info = null;
        	
        	if( missingFactory != null ){
        		ByteArrayInputStream byteInput = new ByteArrayInputStream( dataArray );
        		DataInputStream byteDataInput = new DataInputStream( byteInput );
        		byteDataInput.readUTF();
        		byteDataInput.readInt();
        		
        		Object data = missingFactory.read( getFactoryID( factoryId ), byteDataInput, count );
        		byteDataInput.close();
        		
        		if( data != null ){
        			info = new DockLayoutInfo( new DockLayout<Object>( factoryId, data ));
        		}
        	}
        	
        	if( info == null ){
        		info = new DockLayoutInfo( dataArray );
        	}
        }
        else{
            DataInputStream din = readBuffer( in, count );
            Object data = factory.read( din );
            if( data == null )
                info = null;
            else
                info = new DockLayoutInfo( new DockLayout<Object>( factoryId, data ) );
            
            din.close();
        }
        
        List<DockLayout<?>> adjacentLayouts = null;
        if( Version.VERSION_1_0_7.compareTo( version ) <= 0 ){
            // adjacent
            int layoutCount = in.readInt();
            if( layoutCount > 0 ){
                adjacentLayouts = new ArrayList<DockLayout<?>>( layoutCount );
                for( int i = 0; i < layoutCount; i++ ){
                    String adjacentFactoryId = in.readUTF();
                    int adjacentCount = in.readInt();
                    AdjacentDockFactory<Object> adjacentFactory = (AdjacentDockFactory<Object>)getAdjacentFactory( adjacentFactoryId );
                    if( adjacentFactory == null ){
                        if( missingAdjacent == null ){
                            // skip
                            while( adjacentCount > 0 ){
                                int skipped = (int)in.skip( adjacentCount );
                                if( skipped <= 0 )
                                    throw new EOFException();
                                adjacentCount -= skipped;
                            }
                        }
                        else{
                            DataInputStream din = readBuffer( in, adjacentCount );
                            Object data = missingAdjacent.read( getAdjacentFactoryID( adjacentFactoryId ), din, count );
                            if( data != null ){
                                adjacentLayouts.add( new DockLayout<Object>( adjacentFactoryId, data ) );
                            }
                            din.close();
                        }
                    }
                    else{
                        DataInputStream din = readBuffer( in, adjacentCount );
                        Object data = adjacentFactory.read( din );
                        if( data != null ){
                            adjacentLayouts.add( new DockLayout<Object>( adjacentFactoryId, data ) );
                        }
                        
                        din.close();
                    }
                }
            }
        }
        
        // ignore
        boolean ignore = in.readBoolean();
        
        // children
        List<DockLayoutComposition> children = new ArrayList<DockLayoutComposition>();
        count = in.readInt();
        for( int i = 0; i < count; i++ ){
            children.add( readCompositionStream( in, version ) );
        }
        
        // result
        return new DockLayoutComposition( info, adjacentLayouts, children, ignore );
    }
    
    private DataInputStream readBuffer( DataInputStream in, int count ) throws IOException{
        byte[] buffer = new byte[ count ];
        int read = 0;
        while( read < count ){
            int input = in.read( buffer, read, count-read );
            if( input < 0 )
                throw new EOFException();
            read += input;
        }
        
        ByteArrayInputStream bin = new ByteArrayInputStream( buffer );
        DataInputStream din = new DataInputStream( bin );
        return din;
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
        Version.write( out, Version.VERSION_1_0_4 );
        
        out.writeInt( stations.size() );
        for( Map.Entry<String, DockStation> entry : stations.entrySet() ){
            DockLayoutComposition composition = convert( entry.getValue() );
            if( composition != null ){
                out.writeUTF( entry.getKey() );
                writeComposition( composition, out );
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
        Version version = Version.read( in );
        version.checkCurrent();
        
        int count = in.readInt();
        Map<String, DockStation> result = new HashMap<String, DockStation>();
        for( int i = 0; i < count; i++ ){
            String key = in.readUTF();
            DockLayoutComposition composition = readComposition( in );
            DockElement element = composition == null ? null : convert( composition );
            DockStation station = element == null ? null : element.asDockStation();
            if( station != null ){
                result.put( key, station );
            }
        }
        return result;
    }
    
    /**
     * Writes the contents of <code>composition</code> into <code>element</code> without
     * changing the attributes of <code>element</code>.
     * @param composition the composition to write
     * @param element the element to write into
     * @throws IllegalArgumentException if a factory is missing
     */
    @SuppressWarnings("unchecked")
    public void writeCompositionXML( DockLayoutComposition composition, XElement element ){
        DockLayoutInfo info = composition.getLayout();
        if( info.getKind() == DockLayoutInfo.Data.XML ){
        	element.addElement( info.getDataXML() );
        }
        else if( info.getKind() == DockLayoutInfo.Data.DOCK_LAYOUT ){
        	DockLayout<?> layout = info.getDataLayout();
        	
        	DockFactory<DockElement, Object> factory = (DockFactory<DockElement, Object>)getFactory( layout.getFactoryID() );
        	if( factory == null )
        		throw new IllegalArgumentException( "Missing factory: " + layout.getFactoryID() );

        	XElement xfactory = element.addElement( "layout" );
        	xfactory.addString( "factory", getID( factory ) );
        	factory.write( layout.getData(), xfactory );
        }
        else{
        	// there is nothing to write...
        	throw new IllegalArgumentException( "Cannot store layout as XML: it was never translated and its raw format is not XML" );
        }
        
        List<DockLayout<?>> adjacentLayouts = composition.getAdjacent();
        if( adjacentLayouts != null ){
            XElement xadjacent = element.addElement( "adjacent" );
            
            for( DockLayout<?> adjacentLayout : adjacentLayouts ){
                AdjacentDockFactory<Object> adjacentFactory = (AdjacentDockFactory<Object>)getAdjacentFactory( adjacentLayout.getFactoryID() );
                if( adjacentFactory == null )
                    throw new IllegalArgumentException( "Missing adjacent factory: " + adjacentLayout.getFactoryID() );
                
                XElement xlayout = xadjacent.addElement( "layout" );
                xlayout.addString( "factory", getAdjacentID( adjacentFactory ) );
                adjacentFactory.write( adjacentLayout.getData(), xlayout );
            }
        }
        
        XElement xchildren = element.addElement( "children" );
        xchildren.addBoolean( "ignore", composition.isIgnoreChildren() );
        
        for( DockLayoutComposition child : composition.getChildren() ){
            XElement xchild = xchildren.addElement( "child" );
            writeCompositionXML( child, xchild );
        }
    }
    
    /**
     * Reads a {@link DockLayoutComposition} from an xml element.
     * @param element the element to read
     * @return the composition that was read
     * @throws XException if something is missing or malformed in <code>element</code>
     */
    @SuppressWarnings("unchecked")
    public DockLayoutComposition readCompositionXML( XElement element ){
        XElement xfactory = element.getElement( "layout" );
        DockLayoutInfo layout = null;
        if( xfactory != null ){
            String factoryId = xfactory.getString( "factory" );
            DockFactory<DockElement, Object> factory = (DockFactory<DockElement, Object>)getFactory( factoryId );
            if( factory != null ){
                Object data = factory.read( xfactory );
                if( data != null ){
                    layout = new DockLayoutInfo( new DockLayout<Object>( factoryId, data ) );
                }
            }
            else{
            	layout = null;
            	
            	if( missingFactory != null ){
            		Object data = missingFactory.readXML( getFactoryID( factoryId ), xfactory );
            		if( data != null ){
            			layout = new DockLayoutInfo( new DockLayout<Object>( factoryId, data ) );
            		}
            	}
            	
            	if( layout == null ){
            		layout = new DockLayoutInfo( xfactory.copy() );
            	}
            }
        }
        
        XElement xadjacent = element.getElement( "adjacent" );
        List<DockLayout<?>> adjacentLayouts = null;
        if( xadjacent != null ){
            adjacentLayouts = new ArrayList<DockLayout<?>>();
            
            for( XElement xlayout : xadjacent.getElements( "layout" )){
                String factoryId = xlayout.getString( "factory" );
                AdjacentDockFactory<Object> adjacentFactory = (AdjacentDockFactory<Object>)getAdjacentFactory( factoryId );
                if( adjacentFactory != null ){
                    Object data = adjacentFactory.read( xlayout );
                    if( data != null ){
                        adjacentLayouts.add( new DockLayout<Object>( factoryId, data ));
                    }
                }
                else if( missingAdjacent != null ){
                    Object data = missingAdjacent.readXML( getAdjacentFactoryID( factoryId ), xlayout );
                    if( data != null ){
                        adjacentLayouts.add( new DockLayout<Object>( factoryId, data ) );
                    }
                }
            }
        }
        
        XElement xchildren = element.getElement( "children" );
        boolean ignore = true;
        List<DockLayoutComposition> children = new ArrayList<DockLayoutComposition>();
        
        if( xchildren != null ){
            ignore = xchildren.getBoolean( "ignore" );
            for( XElement xchild : xchildren.getElements( "child" )){
                children.add( readCompositionXML( xchild ));
            }
        }
        
        return new DockLayoutComposition( layout, adjacentLayouts, children, ignore );
    }
    
    /**
     * Writes all locations and relationships of the {@link DockStation}s
     * <code>stations</code> and their children as xml.
     * @param stations The stations to store, only the roots are needed.
     * @param element the element to write into, attributes of <code>element</code> will
     * not be changed
     * @throws IOException if an I/O-error occurs
     */
    public void writeXML( Map<String, DockStation> stations, XElement element ) throws IOException{
        for( Map.Entry<String, DockStation> entry : stations.entrySet() ){
            DockLayoutComposition composition = convert( entry.getValue() );
            if( composition != null ){
                XElement xchild = element.addElement( "element" );
                xchild.addString( "name", entry.getKey() );
                writeCompositionXML( composition, xchild );
            }
        }
    }
    
    /**
     * Reads a set of {@link DockStation}s that were stored earlier.
     * @param root the xml element from which to read
     * @return the set of station
     */
    public Map<String, DockStation> readXML( XElement root ){
        Map<String, DockStation> result = new HashMap<String, DockStation>();
        for( XElement xelement : root.getElements( "element" )){
            String name = xelement.getString( "name" );
            DockLayoutComposition composition = readCompositionXML( xelement );
            DockElement element = composition == null ? null : convert( composition );
            DockStation station = element == null ? null : element.asDockStation();
            if( station != null )
                result.put( name, station );
        }
        return result;
    }
    
    /**
     * Using the factories currently known to this {@link DockSituation}, this
     * method tries to fill gaps in <code>composition</code>. It checks
     * all the {@link DockLayoutInfo}s, if an info contains a byte array or
     * an {@link XElement}, then this method tries to use a factory to
     * read the element. It a {@link #setMissingFactory(MissingDockFactory) missing factory}
     * is present, then this factory is used as well.
     * @param composition the composition to read
     * @return either <code>composition</code> or a new composition if this
     * method changed something
     * @throws IOException if some stream was opened but cannot be read
     * @throws XException if some xml element was found but cannot be read
     */
    public DockLayoutComposition fillMissing( DockLayoutComposition composition ) throws IOException, XException{
    	DockLayoutInfo info = composition.getLayout();
    	if( info.getKind() == DockLayoutInfo.Data.BYTE ){
    		DataInputStream in = new DataInputStream( new ByteArrayInputStream( info.getDataByte() ));
    		String factoryId = in.readUTF();
    		int count = in.readInt();
    		
    		
    	}
    	else if( info.getKind() == DockLayoutInfo.Data.XML ){
    		
    	}
    	
    	// TODO continue here
    	throw new IOException( "Method incomplete" );
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
    protected String getID( DockFactory<?,?> factory ){
        return factory.getID();
    }
    
    /**
     * Transforms an id read from a stream to the id of the factory which
     * would be used. This method must fulfill one contract:
     * <code>DockFactory factory = ...
     * factory.getID().equals( getFactoryID( getID( factory )));</code>
     * @param id the id read from a stream
     * @return the id of the original factory
     */
    protected String getFactoryID( String id ){
        return id;
    }

    /**
     * Gets the id of <code>factory</code>. The default behavior is just to
     * return {@link DockFactory#getID()}. Note that this method should be
     * a bijection to {@link #getAdjacentFactory(String)}.
     * @param factory the factory whose id is needed
     * @return the id of the factory
     */
    protected String getAdjacentID( AdjacentDockFactory<?> factory ){
        return factory.getID();
    }
    
    /**
     * Transforms an id read from a stream to the id of the adjacent factory which
     * would be used. This method must fulfill one contract:
     * <code>AdjacentDockFactory factory = ...
     * factory.getID().equals( getFactoryID( getAdjacentID( factory )));</code>
     * @param id the id read from a stream
     * @return the id of the original factory
     */
    protected String getAdjacentFactoryID( String id ){
        return id;
    }

    
    /**
     * Gets the factory which has the given <code>id</code>. Note that this
     * method should be a bijection to {@link #getID(DockFactory)}. The 
     * default behavior compares <code>id</code> with the 
     * {@link #getID(DockFactory)}.
     * @param id the name of the factory
     * @return the factory or <code>null</code> if no factory has this id
     */
    protected DockFactory<? extends DockElement,?> getFactory( String id ){
    	return factories.get( id );
    }
    
    /**
     * Gets the adjacent factory which has the given <code>id</code>. Note that this
     * method should be a bijection to {@link #getID(DockFactory)}. The 
     * default behavior compares <code>id</code> with the 
     * {@link #getID(DockFactory)}.
     * @param id the name of the factory
     * @return the factory or <code>null</code> if no factory has this id
     */
    protected AdjacentDockFactory<?> getAdjacentFactory( String id ){
        return adjacent.get( id );
    }
}
