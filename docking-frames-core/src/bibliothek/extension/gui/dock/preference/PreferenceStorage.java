/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.extension.gui.dock.preference;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

/**
 * A preference storage is a container storing the values of artificial preferences of one or more {@link PreferenceModel}s.
 * Clients can use {@link #store(PreferenceModel)} to transfer values from model to storage, or 
 * {@link #load(PreferenceModel, boolean)} to transfer values from storage to model. Further mode the entire storage
 * can be persistently written to a file using one of the {@link #writeXML(XElement) write-methods}. It can later be loaded
 * using one of the {@link #readXML(XElement) read-methods}.<br>
 * The static {@link #readXML(PreferenceModel, XElement) read-methods} and the static {@link #writeXML(PreferenceModel, XElement) write-methods}
 * can be used to write and read the contents of a {@link PreferenceModel} directly. 
 * @author Benjamin Sigg
 */
public class PreferenceStorage {
	/**
	 * Writes the current preferences of <code>model</code> into <code>out</code>.
	 * @param model the model to store
	 * @param out the stream to write into
	 * @throws IOException if the stream is not writeable
	 */
	public static void write( PreferenceModel model, DataOutputStream out ) throws IOException{
		PreferenceStorage storage = new PreferenceStorage();
		storage.store( model );
		storage.write( out );
	}
	
	/**
	 * Reads preferences from <code>in</code> and transfers them into <code>model</code>. 
	 * Missing preferences will be replaced by <code>null</code>.
	 * @param model the model to write into.
	 * @param in the stream to read from
	 * @throws IOException if the stream cannot be read
	 */
	public static void read( PreferenceModel model, DataInputStream in ) throws IOException{
		PreferenceStorage storage = new PreferenceStorage();
		storage.read( in );
		storage.load( model, true );
	}
	
	/**
	 * Writes the preferences of <code>model</code> into <code>element</code>. This
	 * method will add new children to <code>element</code> but not change
	 * its attributes.
	 * @param model the model to store
	 * @param element the element to write into
	 */
	public static void writeXML( PreferenceModel model, XElement element ) {
		PreferenceStorage storage = new PreferenceStorage();
		storage.store( model );
		storage.writeXML( element );
	}
	
	/**
	 * Reads some preferences from <code>element</code> and stores them in
	 * <code>model</code>.
	 * @param model the model to write into
	 * @param element the element to read
	 * @throws XException if <code>element</code> is incorrect
	 */
	public static void readXML( PreferenceModel model, XElement element ){
		PreferenceStorage storage = new PreferenceStorage();
		storage.readXML( element );
		storage.load( model, true );
	}
	
    /** the available factories */
    private Map<Path, PreferenceFactory<?>> factories = new HashMap<Path, PreferenceFactory<?>>();
    
    /** the root of all nodes */
    private Node root = new Node( null );
    
    /**
     * Creates a new preference storage with some default factories set.
     */
    public PreferenceStorage(){
        addFactory( Path.TYPE_INT_PATH, PreferenceFactory.FACTORY_INT );
        addFactory( Path.TYPE_STRING_PATH, PreferenceFactory.FACTORY_STRING );
        addFactory( Path.TYPE_BOOLEAN_PATH, PreferenceFactory.FACTORY_BOOLEAN );
        addFactory( Path.TYPE_KEYSTROKE_PATH, PreferenceFactory.FACTORY_KEYSTROKE );
        addFactory( Path.TYPE_MODIFIER_MASK_PATH, PreferenceFactory.FACTORY_MODIFIER_MASK );
        
        addFactory( Path.TYPE_STRING_CHOICE_PATH, PreferenceFactory.FACTORY_STRING );
    }
    
    /**
     * Adds a new factory to this storage, the factory will be responsible to
     * write or read some kind of preferences. If there is already a factory
     * for <code>type</code>, then the old factory will be replaced by
     * <code>factory</code>
     * @param type the type of values <code>factory</code> handles, this path
     * is most times just the name of some class. Node: there is a set of
     * standard paths defined in {@link Path}
     * @param factory the new factory
     */
    public void addFactory( Path type, PreferenceFactory<?> factory ){
       if( type == null )
           throw new IllegalArgumentException( "type must not be null" );
       
       if( factory == null )
           throw new IllegalArgumentException( "factory must not be null" );
       
       if( type.getSegmentCount() == 0 )
           throw new IllegalArgumentException( "the root path is not a valid path for this metho" );
       
       factories.put( type, factory );
    }
    
    /**
     * Stores all the preferences of <code>model</code> in this storage. This
     * storage uses the {@link PreferenceModel#getPath(int) paths} of the 
     * preferences to store each value individually. If there is already a value
     * for some path, then that value gets replaced.
     * @param model the model to read out
     */
    public void store( PreferenceModel model ){
        for( int i = 0, n = model.getSize(); i<n; i++ ){
            if( !model.isNatural( i )){
                Node node = root.getNode( model.getPath( i ), true );
                node.put( model.getTypePath( i ), model.getValue( i ) );
            }
        }
    }
    
    /**
     * Gets through all the preferences of <code>model</code> and changes
     * their values according to the values stored in this storage. 
     * @param missingToNull whether missing values should be set to <code>null</code>. If
     * not set, then missing values remain just unchanged.
     * @param model the model to write into
     */
    public void load( PreferenceModel model, boolean missingToNull ){
        for( int i = 0, n = model.getSize(); i<n; i++ ){
            if( !model.isNatural( i )){
                Node node = root.getNode( model.getPath( i ), false );
                if( node == null ){
                    if( missingToNull ){
                        model.setValue( i, null );
                    }
                }
                else{
                    model.setValue( i, node.value );
                }
            }
            else{
                model.setValueNatural( i );
            }
        }
    }
    
    /**
     * Writes all values currently stored in this storage to <code>out</code>.
     * @param out the stream to write into
     * @throws IOException if the stream is not writable or if there is a
     * factory missing for some type
     */
    public void write( DataOutputStream out ) throws IOException{
        Version.write( out, Version.CURRENT );
        write( root, out );
    }
    
    @SuppressWarnings("unchecked")
    private void write( Node node, DataOutputStream out ) throws IOException{
        Path type = node.getType();
        
        if( type == null ){
            out.writeBoolean( false );
        }
        else{
            out.writeBoolean( true );
            out.writeUTF( type.toString() );
            
            Object value = node.getValue();
            if( value == null ){
                out.writeBoolean( false );
            }
            else{
                out.writeBoolean( true );
                PreferenceFactory factory = factories.get( type );
                if( factory == null )
                    throw new IOException( "unknown path for a type: " + type );
                factory.write( value, out );
            }
        }
        
        int size = node.getChildrenCount();
        out.writeInt( size );
        for( int i = 0; i < size; i++ ){
            Node child = node.getChild( i );
            out.writeUTF( child.getName() );
            write( child, out );
        }
    }

    /**
     * Reads the contents of this storage from a stream. Note that this method does
     * not clear the storage, if there are values in this storage that are not
     * in the stream, then these values remain.
     * @param in the stream to read from
     * @throws IOException if <code>in</code> can't be read
     */
    public void read( DataInputStream in ) throws IOException{
        Version version = Version.read( in );
        version.checkCurrent();
        
        read( root, in );
    }
    
    @SuppressWarnings("unchecked")
    private void read( Node node, DataInputStream in ) throws IOException{
        Path type = null;
        Object value = null;
        
        if( in.readBoolean() ){
            type = new Path( in.readUTF() );
            if( in.readBoolean() ){
                PreferenceFactory factory = factories.get( type );
                if( factory == null )
                    throw new IOException( "don't know how to read objects of type " + type );
                value = factory.read( in );
            }
        }
        
        node.put( type, value );
        
        int size = in.readInt();
        for( int i = 0; i < size; i++ ){
            String name = in.readUTF();
            Node child = node.getNode( new Path( name ), true );
            read( child, in );
        }
    }
    
    /**
     * Writes the contents of this storage into <code>element</code>, adds
     * new {@link XElement elements} to <code>element</code> but does
     * not change the attributes. If a factory for some element is missing,
     * then this element will not be stored.
     * @param element the element to write into 
     */
    public void writeXML( XElement element ){
        writeXML( root, element );
    }
    
    @SuppressWarnings("unchecked")
    private void writeXML( Node node, XElement element ){
        Path type = node.getType();
        Object value = node.getValue();
        if( type != null && value != null ){
            PreferenceFactory factory = factories.get( type );
            if( factory != null ){
                XElement xvalue = element.addElement( "value" );
                xvalue.addString( "type", type.toString() );
                factory.writeXML( value, xvalue );
            }
        }
        
        int size = node.getChildrenCount();
        for( int i = 0; i < size; i++ ){
            Node child = node.getChild( i );
            XElement xchild = element.addElement( "child" );
            
            xchild.addString( "name", child.getName() );
            writeXML( child, xchild );
        }
    }
    
    /**
     * Reads the contents of this storage from <code>element</code>.  Note that this 
     * method does not clear the storage, if there are values in this storage that are not
     * in <code>element</code>, then these values remain. If there is a 
     * {@link PreferenceFactory} missing for some type, then this value will silently
     * be left out.
     * @param element the element to read from
     * @throws XException if <code>element</code> is not correct
     */
    public void readXML( XElement element ){
        readXML( root, element );
    }
    
    @SuppressWarnings("unchecked")
    private void readXML( Node node, XElement element ){
        Path type = null;
        Object value = null;
        
        XElement xvalue = element.getElement( "value" );
        if( xvalue != null ){
            String typeName = xvalue.getString( "type" );
            type = new Path( typeName );
            PreferenceFactory factory = factories.get( type );
            if( factory != null ){
                value = factory.readXML( xvalue );
            }
        }
        
        node.put( type, value );
        
        XElement[] xchildren = element.getElements( "child" );
        for( XElement xchild : xchildren ){
            String name = xchild.getString( "name" );
            Node child = node.getNode( new Path( name ), true );
            readXML( child, xchild );
        }
    }
    
    /**
     * Removes all preferences from this storage
     */
    public void clear(){
        root = new Node( null );
    }
    
    /**
     * Represents a single resource in a {@link PreferenceStorage}.
     * @author Benjamin Sigg
     */
    private static class Node{
        /** the name of this node */
        private String name;
        
        /** the value of the preference stored in this node, may be <code>null</code> */
        private Object value;
        
        /**
         * the kind of value stored in this node, may be <code>null</code> to indicate
         * that this node does not represent a preference 
         */
        private Path type;
        
        /** additional nodes that are nested into this node */
        private List<Node> children;
        
        /**
         * Creates a new node
         * @param name the name of this node
         */
        public Node( String name ){
            this.name = name;
        }
        
        /**
         * Sets type and value of this node
         * @param type new type
         * @param value new value, should be <code>null</code> or a subclass
         * of <code>type</code>
         */
        public void put( Path type, Object value ){
            this.type = type;
            this.value = value;
        }
        
        /**
         * Gets the type of this node, might be <code>null</code>
         * @return the type
         */
        public Path getType() {
            return type;
        }
        
        /**
         * Gets the value of this node, might be <code>null</code>. Is
         * surely <code>null</code> if {@link #getType()} returns <code>null</code>
         * @return the value
         */
        public Object getValue() {
            return value;
        }
        
        /**
         * Gets the number of children of this node.
         * @return the number of children
         */
        public int getChildrenCount(){
            return children == null ? 0 : children.size();
        }
        
        /**
         * Gets the index'th child of this node
         * @param index the location of the child
         * @return the child
         */
        public Node getChild( int index ){
            return children.get( index );
        }
        
        /**
         * Gets the name of this node.
         * @return the name
         */
        public String getName() {
            return name;
        }
        
        /**
         * Searches or creates a node for <code>path</code>.
         * @param path the path of some node
         * @param create whether to create the node if it does not exist
         * @return the node at the end of <code>path</code>
         */
        public Node getNode( Path path, boolean create ){
            return getNode( path, create, 0 );
        }
        
        private Node getNode( Path path, boolean create, int segment ){
            if( path.getSegmentCount() == segment )
                return this;
            
            if( children == null ){
                children = new ArrayList<Node>();
            }
            
            String name = path.getSegment( segment );
            for( Node child : children ){
                if( name.equals( child.getName() )){
                    return child.getNode( path, create, segment+1 );
                }
            }
            
            if( !create )
                return null;
            
            Node result = new Node( name );
            children.add( result );
            return result.getNode( path, create, segment+1 );
        }
    }
}
