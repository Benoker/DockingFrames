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

package bibliothek.gui.dock.station.split;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.split.SplitDockStationLayout.Entry;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

/**
 * A factory that creates {@link SplitDockStation SplitDockStations}.
 * @author Benjamin Sigg
 */
public class SplitDockStationFactory implements DockFactory<SplitDockStation, SplitDockStationLayout> {
	/** The id which is normally used for this type of factory*/
    public static final String ID = "SplitDockStationFactory";
    
    /** optional strategy used as filter for finding out which placeholders should be stored */
    private PlaceholderStrategy placeholders = null;
    
    /**
     * Creates a new factory
     */
    public SplitDockStationFactory(){
    	// nothing
    }
    
    /**
     * Creates a new factory
     * @param placeholders filter for finding out which placeholders to store and which not
     */
    public SplitDockStationFactory( PlaceholderStrategy placeholders ){
    	this.placeholders = placeholders;
    }
    
    public String getID() {
        return ID;
    }
    
    public SplitDockStationLayout getLayout( SplitDockStation station, final Map<Dockable, Integer> children ) {
        
        Entry root =
            station.visit( new SplitTreeFactory<Entry>(){
            	public Entry leaf( Dockable dockable, long id, Path[] placeholders ){
                    Integer childId = children.get( dockable );
                    if( childId != null ){
                        return new SplitDockStationLayout.Leaf( childId, placeholders, id );
                    }
                    else if( placeholders != null && placeholders.length > 0 ){
                    	   return new SplitDockStationLayout.Leaf( -1, placeholders, id );
                    }
                    else{
                        return null;
                    }
                }
            	
            	public Entry placeholder( long id, Path[] placeholders ){
	            	if( placeholders != null && placeholders.length > 0 ){
	            		return new SplitDockStationLayout.Leaf( -1, placeholders, id );
	            	}
	            	else{
	            		return null;
	            	}
            	}

                public Entry root( Entry root, long id ) {
                    return root;
                }
                
                public Entry horizontal( Entry left, Entry right, double divider, long id, Path[] placeholders, boolean visible ){
                    if( left == null )
                        return right;
                    
                    if( right == null )
                        return left;
                    
                    return new SplitDockStationLayout.Node( Orientation.HORIZONTAL, divider, left, right, placeholders, id );
                }

                public Entry vertical( Entry top, Entry bottom, double divider, long id, Path[] placeholders, boolean visible ){
                    if( top == null )
                        return bottom;
                    
                    if( bottom == null )
                        return top;
                    
                    return new SplitDockStationLayout.Node( Orientation.VERTICAL, divider, top, bottom, placeholders, id );
                }
            });
        
        Dockable fullscreenDockable = station.getFullScreen();
        Integer fullscreen = null;
        if( fullscreenDockable != null )
            fullscreen = children.get( fullscreenDockable );
        
        if( fullscreen == null )
            return new SplitDockStationLayout( root, -1 );
        else
            return new SplitDockStationLayout( root, fullscreen );
    }
    
    public void setLayout( SplitDockStation station, SplitDockStationLayout layout, Map<Integer, Dockable> children ) {
        SplitDockTree tree = new SplitDockTree();
        SplitDockTree.Key root = null;
        if( layout.getRoot() != null ){
            root = handleEntry( layout.getRoot(), tree, children );
        }
        if( root != null ){
            tree.root( root );
        }
        station.dropTree( tree, false );
        
        Dockable fullscreen = children.get( layout.getFullscreen() );
        station.setFullScreen( fullscreen );
    }
    
    /**
     * Transforms an entry of a {@link SplitDockStationLayout} into a key
     * of a {@link SplitDockTree}.
     * @param entry the element to transform
     * @param tree the tree into which to add new keys
     * @param children the set of known children
     * @return the key or <code>null</code>
     */
    private SplitDockTree.Key handleEntry( SplitDockStationLayout.Entry entry, SplitDockTree tree, Map<Integer, Dockable> children ){
        if( entry.asLeaf() != null )
            return handleLeaf( entry.asLeaf(), tree, children );
        else
            return handleNode( entry.asNode(), tree, children );
    }
    
    /**
     * Transforms a leaf of a {@link SplitDockStationLayout} into a key
     * of a {@link SplitDockTree}.
     * @param leaf the element to transform
     * @param tree the tree into which to add new keys
     * @param children the set of known children
     * @return the key or <code>null</code>
     */
    private SplitDockTree.Key handleLeaf( SplitDockStationLayout.Leaf leaf, SplitDockTree tree, Map<Integer, Dockable> children ){
    	Dockable dockable = children.get( leaf.getId() );
        if( dockable != null ){
        	return tree.put( dockable, leaf.getNodeId() );
        }
        return null;
    }
    
    /**
     * Transforms a node of a {@link SplitDockStationLayout} into a key
     * of a {@link SplitDockTree}.
     * @param node the element to transform
     * @param tree the tree into which to add new keys
     * @param children the set of known children
     * @return the key or <code>null</code>
     */
    private SplitDockTree.Key handleNode( SplitDockStationLayout.Node node, SplitDockTree tree,Map<Integer, Dockable> children ){
        SplitDockTree.Key a = handleEntry( node.getChildA(), tree, children );
        SplitDockTree.Key b = handleEntry( node.getChildB(), tree, children );
        
        if( a == null )
            return b;
        
        if( b == null )
            return a;
        
        switch( node.getOrientation() ){
            case HORIZONTAL:
                return tree.horizontal( a, b, node.getDivider(), node.getNodeId() );
            case VERTICAL:
                return tree.vertical( a, b, node.getDivider(), node.getNodeId() );
        }
        
        return null;
    }
    
    public void estimateLocations( SplitDockStationLayout layout, Map<Integer, DockLayoutInfo> children ) {
    	estimateLocations( layout.getRoot(), children );
    }
    
    /**
     * Estimates the {@link DockableProperty location} of all leafs in the subtree
     * beginning with <code>entry</code>.
     * @param entry the root of the subtree to check
     * @param children the children of the station
     */
    private void estimateLocations( SplitDockStationLayout.Entry entry, Map<Integer, DockLayoutInfo> children ){
    	if( entry == null )
    		return;
    	
    	SplitDockStationLayout.Leaf leaf = entry.asLeaf();
    	if( leaf != null ){
    		DockLayoutInfo info = children.get( leaf.getId() );
    		if( info != null ){
    		 	SplitDockPathProperty property = leaf.createPathProperty();
    		 	info.setLocation( property );
    		}
    	}
    	
    	SplitDockStationLayout.Node node = entry.asNode();
    	if( node != null ){
    		estimateLocations( node.getChildA(), children );
    		estimateLocations( node.getChildB(), children );
    	}
    }
    
    public void setLayout( SplitDockStation element,
            SplitDockStationLayout layout ) {
        
        // nothing to do
    }
    
    public SplitDockStation layout( SplitDockStationLayout layout ) {
        SplitDockStation station = createStation();
        setLayout( station, layout );
        return station;
    }
    
    public SplitDockStation layout( SplitDockStationLayout layout,
            Map<Integer, Dockable> children ) {
        
        SplitDockStation station = createStation();
        setLayout( station, layout, children );
        return station;
    }
    
    public void write( SplitDockStationLayout layout, DataOutputStream out )
            throws IOException {

        Version.write( out, Version.VERSION_1_0_8 );
        
        SplitDockStationLayout.Entry root = layout.getRoot();
        if( root == null ){
            out.writeBoolean( false );
        }
        else{
            out.writeBoolean( true );
            writeEntry( root, out );
        }
        
        out.writeInt( layout.getFullscreen() );
    }
    
    /**
     * Writes an entry to <code>out</code>.
     * @param entry the entry to store
     * @param out the stream to write into
     * @throws IOException if an I/O-error occurs
     */
    private void writeEntry( SplitDockStationLayout.Entry entry, DataOutputStream out ) throws IOException{
    	out.writeLong( entry.getNodeId() );
        if( entry.asLeaf() != null ){
        	Path[] placeholders = entry.getPlaceholders();
        	if( placeholders == null || placeholders.length == 0 ){
        		out.writeByte( 0 );
        		out.writeInt( entry.asLeaf().getId() );
        	}
        	else{
        		out.writeByte( 2 );
        		out.writeInt( entry.asLeaf().getId() );
        		out.writeInt( placeholders.length );
        		for( Path placeholder : placeholders ){
        			out.writeUTF( placeholder.toString() );
        		}
        	}
        }
        else{
            SplitDockStationLayout.Node node = entry.asNode();
            Path[] placeholders = entry.getPlaceholders();
        	if( placeholders == null || placeholders.length == 0 ){
        		out.writeByte( 1 );
        		out.writeInt( node.getOrientation().ordinal() );
        		out.writeDouble( node.getDivider() );
        		writeEntry( node.getChildA(), out );
        		writeEntry( node.getChildB(), out );
        	}
        	else{
        		out.writeByte( 3 );
        		out.writeInt( node.getOrientation().ordinal() );
        		out.writeDouble( node.getDivider() );
        		out.writeInt( placeholders.length );
        		for( Path placehoder : placeholders ){
        			out.writeUTF( placehoder.toString() );
        		}
        		writeEntry( node.getChildA(), out );
        		writeEntry( node.getChildB(), out );
        	}
        }
    }
    
    public SplitDockStationLayout read( DataInputStream in ) throws IOException {
        Version version = Version.read( in );
        version.checkCurrent();
        
        boolean version8 = Version.VERSION_1_0_8.compareTo( version ) <= 0;
        
        SplitDockStationLayout.Entry root = null;
        if( in.readBoolean() ){
            root = readEntry( in, version8 );
        }
        int fullscreen = in.readInt();
        return new SplitDockStationLayout( root, fullscreen );
    }
    
    /**
     * Reads an entry from the stream.
     * @param in the stream to read
     * @param version8 version of file is at least 8
     * @return the new entry
     * @throws IOException if an I/O-error occurs
     */
    private SplitDockStationLayout.Entry readEntry( DataInputStream in, boolean version8 ) throws IOException{
    	long id = -1;
    	if( version8 ){
    		id = in.readLong();
    	}
        byte kind = in.readByte();
        if( kind == 0 ){
            return new SplitDockStationLayout.Leaf( in.readInt(), null, id );
        }
        if( kind == 1 ){
            Orientation orientation = Orientation.values()[ in.readInt() ];
            double divider = in.readDouble();
            SplitDockStationLayout.Entry childA = readEntry( in, version8 );
            SplitDockStationLayout.Entry childB = readEntry( in, version8 );
            return new SplitDockStationLayout.Node( orientation, divider, childA, childB, null, id );
        }
        if( kind == 2 ){
        	int childId = in.readInt();
        	Path[] placeholders = readPlaceholders( in );
        	return new SplitDockStationLayout.Leaf( childId, placeholders, id );
        }
        if( kind == 3 ){
        	Orientation orientation = Orientation.values()[ in.readInt() ];
            double divider = in.readDouble();
            Path[] placeholders = readPlaceholders( in );
            SplitDockStationLayout.Entry childA = readEntry( in, version8 );
            SplitDockStationLayout.Entry childB = readEntry( in, version8 );
            return new SplitDockStationLayout.Node( orientation, divider, childA, childB, placeholders, id );
        }
        throw new IOException( "unknown kind: " + kind );
    }
    
    private Path[] readPlaceholders( DataInputStream in ) throws IOException{
    	int length = in.readInt();
    	List<Path> result = new ArrayList<Path>( length );
    	for( int i = 0; i < length; i++ ){
    		Path placeholder = new Path( in.readUTF() );
    		if( placeholders == null || placeholders.isValidPlaceholder( placeholder )){
    			result.add( placeholder );
    		}
    	}
    	return result.toArray( new Path[ result.size() ] );
    }
    
    public void write( SplitDockStationLayout layout, XElement element ) {
        if( layout.getFullscreen() != -1 ){
            element.addElement( "fullscreen" ).addInt( "id", layout.getFullscreen() );
        }
        
        if( layout.getRoot() != null ){
            writeEntry( layout.getRoot(), element );
        }
    }
    
    /**
     * Writes an entry in xml format.
     * @param entry the entry to write
     * @param parent the parent node of the entry
     */
    private void writeEntry( SplitDockStationLayout.Entry entry, XElement parent ){
    	XElement xchild;
    	
        if( entry.asLeaf() != null ){
        	xchild = parent.addElement( "leaf" );
            xchild.addInt( "id", entry.asLeaf().getId() ).addLong( "nodeId", entry.getNodeId() );
        }
        else{
            xchild = parent.addElement( "node" );
            xchild.addLong( "nodeId", entry.getNodeId() );
            xchild.addString( "orientation", entry.asNode().getOrientation().name() );
            xchild.addDouble( "divider", entry.asNode().getDivider() );
            writeEntry( entry.asNode().getChildA(), xchild );
            writeEntry( entry.asNode().getChildB(), xchild );
        }
        
        Path[] placeholders = entry.getPlaceholders();
        if( placeholders != null && placeholders.length > 0 ){
        	XElement xplaceholders = xchild.addElement( "placeholders" );
        	for( Path placeholder : placeholders ){
        		xplaceholders.addElement( "placeholder" ).setString( placeholder.toString() );
        	}
        }
    }
    
    public SplitDockStationLayout read( XElement element ) {
        SplitDockStationLayout.Entry root = null;
        XElement xroot = element.getElement( "node" );
        if( xroot == null )
            xroot = element.getElement( "leaf" );
        if( xroot != null )
            root = readEntry( xroot );
        
        int fullscreen = -1;
        XElement xfullscreen = element.getElement( "fullscreen" );
        if( xfullscreen != null )
            fullscreen = xfullscreen.getInt( "id" );
        
        return new SplitDockStationLayout( root, fullscreen );
    }
    
    /**
     * Transforms an xml-element into an entry.
     * @param element the element that should be converted, of type "node" or
     * "leaf".
     * @return the new entry
     */
    private SplitDockStationLayout.Entry readEntry( XElement element ){
    	long nodeId = -1;
    	if( element.attributeExists( "nodeId" ) ){
    		nodeId = element.getLong( "nodeId" );
    	}
    	
    	Path[] placeholders = null;
    	XElement xplaceholders = element.getElement( "placeholders" );
    	if( xplaceholders != null ){
    		XElement[] xchildren = xplaceholders.getElements( "placeholder" );
    		if( xchildren.length > 0 ){
    			List<Path> collection = new ArrayList<Path>( xchildren.length );
    			for( int i = 0; i < xchildren.length; i++ ){
    				Path placeholder = new Path( xchildren[i].getString() );
    				if( this.placeholders == null || this.placeholders.isValidPlaceholder( placeholder )){
    					collection.add( placeholder );
    				}
    			}
    			placeholders = collection.toArray( new Path[ collection.size() ]);
    		}
    	}
    	
        if( "leaf".equals( element.getName() )){
            return new SplitDockStationLayout.Leaf( element.getInt( "id" ), placeholders, nodeId );
        }
        if( "node".equals( element.getName() )){
        	XElement[] xchildren = element.getElements( "leaf", "node" );
        	
        	if( xchildren.length != 2 )
                throw new XException( "node element must have exactly least two children: " + element );
           
            
            
            return new SplitDockStationLayout.Node( 
                    Orientation.valueOf( element.getString( "orientation" ) ),
                    element.getDouble( "divider" ),
                    readEntry( xchildren[0] ),
                    readEntry( xchildren[1] ),
                    placeholders,
                    nodeId );
        }
        throw new XException( "element neither leaf nor node: " + element );
    }
    
    /**
     * Creates new objects of {@link SplitDockStation}
     * @return the new instance
     */
    protected SplitDockStation createStation(){
        return new SplitDockStation();
    }
}
