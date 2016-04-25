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
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.LocationEstimationMap;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.split.SplitDockPerspective.Root;
import bibliothek.gui.dock.station.split.SplitDockStationLayout.Entry;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

/**
 * A factory that creates {@link SplitDockStation SplitDockStations}.
 * @author Benjamin Sigg
 */
public class SplitDockStationFactory implements DockFactory<SplitDockStation, SplitDockPerspective, SplitDockStationLayout> {
	/** The id which is normally used for this type of factory*/
    public static final String ID = "SplitDockStationFactory";

    /**
     * Creates a new factory
     */
    public SplitDockStationFactory(){
    	// nothing
    }
    
    public String getID() {
        return ID;
    }
    
    /**
     * Creates a new layout for <code>station</code>. The default implementation just calls {@link #createLayout(SplitDockStationLayout.Entry, int, boolean)}.
     * @param station the station for which the layout is requested
     * @param root the contents of the layout
     * @param fullscreen the index of the child that is maximized
     * @param hasFullscreenAction whether a fullscreen action is shown or not
     * @return the new layout
     */
    protected SplitDockStationLayout createLayout( SplitDockStation station, Entry root, int fullscreen, boolean hasFullscreenAction ){
    	return createLayout( root, fullscreen, hasFullscreenAction );
    }
    
    /**
     * Creates a new layout for <code>station</code>. The default implementation just calls {@link #createLayout(SplitDockStationLayout.Entry, int, boolean)}.
     * @param station the station for which the layout is requested
     * @param root the contents of the layout
     * @param fullscreen the index of the child that is maximized
     * @param hasFullscreenAction whether a fullscreen action is shown or not
     * @return the new layout
     */
    protected SplitDockStationLayout createLayout( SplitDockPerspective station, Entry root, int fullscreen, boolean hasFullscreenAction ){
    	return createLayout( root, fullscreen, hasFullscreenAction );
    }
    
    /**
     * Creates a new layout for <code>station</code>.
     * @param root the contents of the layout
     * @param fullscreen the index of the child that is maximized
     * @param hasFullscreenAction whether a fullscreen action is shown or not
     * @return the new layout
     */
    protected SplitDockStationLayout createLayout( Entry root, int fullscreen, boolean hasFullscreenAction ){
    	return new SplitDockStationLayout( root, fullscreen, hasFullscreenAction );
    }
    
    public SplitDockStationLayout getLayout( final SplitDockStation station, final Map<Dockable, Integer> children ) {
        Entry root =
            station.visit( new SplitTreeFactory<Entry>(){
            	private PlaceholderStrategy strategy = station.getPlaceholderStrategy();
            	
            	public Entry leaf( Dockable dockable, long id, Path[] placeholders, PlaceholderMap placeholderMap ){
            		Integer childId = children.get( dockable );
            		placeholders = DockUtilities.mergePlaceholders( placeholders, dockable, strategy );
                    if( childId != null ){
                        return new SplitDockStationLayout.Leaf( childId, placeholders, placeholderMap, id );
                    }
                    else if( placeholders != null && placeholders.length > 0 ){
                    	return new SplitDockStationLayout.Leaf( -1, placeholders, placeholderMap, id );
                    }
                    else{
                        return null;
                    }
                }
            	
            	public Entry placeholder( long id, Path[] placeholders, PlaceholderMap placeholderMap ){
	            	if( placeholders != null && placeholders.length > 0 ){
	            		return new SplitDockStationLayout.Leaf( -1, placeholders, placeholderMap, id );
	            	}
	            	else{
	            		return null;
	            	}
            	}

                public Entry root( Entry root, long id ) {
                    return root;
                }
                
                public Entry horizontal( Entry left, Entry right, double divider, long id, Path[] placeholders, PlaceholderMap placeholderMap, boolean visible ){
                    if( left == null )
                        return right;
                    
                    if( right == null )
                        return left;
                    
                    return new SplitDockStationLayout.Node( Orientation.HORIZONTAL, divider, left, right, placeholders, placeholderMap, id );
                }

                public Entry vertical( Entry top, Entry bottom, double divider, long id, Path[] placeholders, PlaceholderMap placeholderMap, boolean visible ){
                    if( top == null )
                        return bottom;
                    
                    if( bottom == null )
                        return top;
                    
                    return new SplitDockStationLayout.Node( Orientation.VERTICAL, divider, top, bottom, placeholders, placeholderMap, id );
                }
            });
        
        Dockable fullscreenDockable = station.getFullScreen();
        Integer fullscreen = null;
        if( fullscreenDockable != null )
            fullscreen = children.get( fullscreenDockable );
        
        if( fullscreen == null )
            return createLayout( station, root, -1, station.hasFullScreenAction() );
        else
            return createLayout( station, root, fullscreen, station.hasFullScreenAction() );
    }
    
    public SplitDockStationLayout getPerspectiveLayout( SplitDockPerspective element, Map<PerspectiveDockable, Integer> children ){
    	if( children == null ){
    		return createLayout( null, -1, element.hasFullscreenAction() );
    	}
    	Entry root = convert( element.getRoot(), children );
         
        PerspectiveDockable fullscreenDockable = element.getFullscreen();
        Integer fullscreen = null;
        if( fullscreenDockable != null )
             fullscreen = children.get( fullscreenDockable );
         
        if( fullscreen == null )
            return createLayout( element, root, -1, element.hasFullscreenAction() );
        else
            return createLayout( element, root, fullscreen, element.hasFullscreenAction() );
    }

    private Entry convert( SplitDockPerspective.Entry entry, Map<PerspectiveDockable, Integer> children ){
    	if( entry == null ){
    		return null;
    	}
    	
    	if( entry.asNode() != null ){
    		SplitDockPerspective.Node node = entry.asNode();
    		Entry childA = convert( node.getChildA(), children );
    		Entry childB = convert( node.getChildB(), children );
    		
    		if( childA == null ){
    			return childB;
    		}
    		if( childB == null ){
    			return childA;
    		}
    		
    		return new SplitDockStationLayout.Node( node.getOrientation(), node.getDivider(), childA, childB, toArray( node.getPlaceholders() ), node.getPlaceholderMap(), node.getNodeId() );
    	} else if( entry.asLeaf() != null ){
    		SplitDockPerspective.Leaf leaf = entry.asLeaf();
    		Integer id = children.get( leaf.getDockable() );
    		return new SplitDockStationLayout.Leaf( id == null ? -1 : id.intValue(), toArray( leaf.getPlaceholders() ), leaf.getPlaceholderMap(), leaf.getNodeId() );
    	}
    	else{
    		return convert( ((Root)entry).getChild(), children );
    	}
    }
    
    private Path[] toArray( Set<Path> placeholders ){
    	if( placeholders == null ){
    		return null;
    	}
    	return placeholders.toArray( new Path[ placeholders.size() ] );
    }
    
    public void setLayout( SplitDockStation station, SplitDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ) {
        DockableSplitDockTree tree = new DockableSplitDockTree();
        DockableSplitDockTree.Key root = null;
        if( layout.getRoot() != null ){
            root = handleEntry( layout.getRoot(), tree, children );
        }
        if( root != null ){
            tree.root( root );
        }
        
        station.dropTree( tree, false );
        
        PlaceholderStrategy oldStrategy = station.getPlaceholderStrategy().getStrategy();
        if( placeholders != oldStrategy && placeholders != null ){
	        try{
	        	station.setPlaceholderStrategy( placeholders );
	        }
	        finally{
	        	station.setPlaceholderStrategy( oldStrategy );
	        }
        }
        
        Dockable fullscreen = children.get( layout.getFullscreen() );
        station.setFullScreen( fullscreen );
    }
    
    public SplitDockPerspective layoutPerspective( SplitDockStationLayout layout, Map<Integer, PerspectiveDockable> children ){
    	SplitDockPerspective result = new SplitDockPerspective();
    	layoutPerspective( result, layout, children );
    	return result;
    }
    
    public void layoutPerspective( SplitDockPerspective perspective, SplitDockStationLayout layout, Map<Integer,PerspectiveDockable> children ){
    	if( children != null ){
		    PerspectiveSplitDockTree tree = new PerspectiveSplitDockTree();
		    PerspectiveSplitDockTree.Key root = null;
		    if( layout.getRoot() != null ){
		    	root = handleEntry( layout.getRoot(), tree, children );
		    }
		    if( root != null ){
		    	tree.root( root );
		    }
		    perspective.read( tree, children.get( layout.getFullscreen() ) );
    	}
	    perspective.setHasFullscreenAction( layout.hasFullscreenAction() );
    }
    
    /**
     * Transforms an entry of a {@link SplitDockStationLayout} into a key
     * of a {@link SplitDockTree}.
     * @param entry the element to transform
     * @param tree the tree into which to add new keys
     * @param children the set of known children
     * @return the key or <code>null</code>
     */
    private <D> SplitDockTree<D>.Key handleEntry( SplitDockStationLayout.Entry entry, SplitDockTree<D> tree, Map<Integer, D> children ){
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
    private <D> SplitDockTree<D>.Key handleLeaf( SplitDockStationLayout.Leaf leaf, SplitDockTree<D> tree, Map<Integer, D> children ){
    	D dockable = children.get( leaf.getId() );
    	
    	Path[] placeholders = leaf.getPlaceholders();
    	PlaceholderMap placeholderMap = leaf.getPlaceholderMap();
    	
        if( dockable != null ){
        	return tree.put( tree.array( dockable ), null, placeholders, placeholderMap, leaf.getNodeId() );
        }
        else if( placeholders != null && placeholders.length > 0 ){
        	return tree.put( tree.array( 0 ), null, placeholders, placeholderMap, leaf.getNodeId() );
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
    private <D> SplitDockTree<D>.Key handleNode( SplitDockStationLayout.Node node, SplitDockTree<D> tree,Map<Integer, D> children ){
        SplitDockTree<D>.Key a = handleEntry( node.getChildA(), tree, children );
        SplitDockTree<D>.Key b = handleEntry( node.getChildB(), tree, children );
        
        if( a == null )
            return b;
        
        if( b == null )
            return a;
        
        switch( node.getOrientation() ){
            case HORIZONTAL:
                return tree.horizontal( a, b, node.getDivider(), node.getPlaceholders(), node.getPlaceholderMap(), node.getNodeId() );
            case VERTICAL:
                return tree.vertical( a, b, node.getDivider(), node.getPlaceholders(), node.getPlaceholderMap(), node.getNodeId() );
        }
        
        return null;
    }
    
    public void estimateLocations( SplitDockStationLayout layout, LocationEstimationMap children ) {
    	estimateLocations( layout.getRoot(), children );
    }
    
    /**
     * Estimates the {@link DockableProperty location} of all leafs in the subtree
     * beginning with <code>entry</code>.
     * @param entry the root of the subtree to check
     * @param children the children of the station
     */
    private void estimateLocations( SplitDockStationLayout.Entry entry, LocationEstimationMap children ){
    	if( entry == null )
    		return;
    	
    	SplitDockStationLayout.Leaf leaf = entry.asLeaf();
    	if( leaf != null ){
    		DockLayoutInfo info = children.getChild( leaf.getId() );
    		if( info != null ){
    			// should put the placeholder here
    		 	SplitDockPathProperty property = leaf.createPathProperty();
    		 	Path placeholder = info.getPlaceholder();
    		 	if( placeholder != null ){
    		 		info.setLocation( new SplitDockPlaceholderProperty( placeholder, property ) );
    		 	}
    		 	else{
    		 		info.setLocation( property );
    		 	}
    		 	
    		 	for( int i = 0, n = children.getSubChildCount( leaf.getId() ); i<n; i++ ){
    		 		DockLayoutInfo subInfo = children.getSubChild( leaf.getId(), i );
    		 		placeholder = subInfo.getPlaceholder();
    		 		if( placeholder != null ){
    		 			subInfo.setLocation( new SplitDockPlaceholderProperty( placeholder, property ) );
    		 		}
    		 	}
    		}
    	}
    	
    	SplitDockStationLayout.Node node = entry.asNode();
    	if( node != null ){
    		estimateLocations( node.getChildA(), children );
    		estimateLocations( node.getChildB(), children );
    	}
    }
    
    public void setLayout( SplitDockStation element, SplitDockStationLayout layout, PlaceholderStrategy placeholders ) {
        // nothing to do
    }
    
    public SplitDockStation layout( SplitDockStationLayout layout, PlaceholderStrategy placeholders ) {
        SplitDockStation station = createStation( layout.hasFullscreenAction() );
        setLayout( station, layout, placeholders );
        return station;
    }
    
    public SplitDockStation layout( SplitDockStationLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ) {
        SplitDockStation station = createStation( layout.hasFullscreenAction() );
        setLayout( station, layout, children, placeholders );
        return station;
    }
    
    public void write( SplitDockStationLayout layout, DataOutputStream out ) throws IOException {
        Version.write( out, Version.VERSION_1_1_0 );
        
        SplitDockStationLayout.Entry root = layout.getRoot();
        if( root == null ){
            out.writeBoolean( false );
        }
        else{
            out.writeBoolean( true );
            writeEntry( root, out );
        }
        
        out.writeInt( layout.getFullscreen() );
        out.writeBoolean( layout.hasFullscreenAction() );
    }
    
    /**
     * Writes an entry to <code>out</code>.
     * @param entry the entry to store
     * @param out the stream to write into
     * @throws IOException if an I/O-error occurs
     */
    private void writeEntry( SplitDockStationLayout.Entry entry, DataOutputStream out ) throws IOException{
    	out.writeLong( entry.getNodeId() );
    	
    	Path[] placeholders = entry.getPlaceholders();
    	PlaceholderMap placeholderMap = entry.getPlaceholderMap();
    	
    	int flag = 0;
    	if( entry.asNode() != null ){
    		flag |= 1;
    	}
    	if( placeholders != null && placeholders.length > 0 ){
    		flag |= 2;
    	}
    	if( placeholderMap != null ){
    		flag |= 4;
    	}
    	
    	out.writeByte( flag );

        if( placeholders != null && placeholders.length > 0 ){
    		out.writeInt( placeholders.length );
    		for( Path placeholder : placeholders ){
    			out.writeUTF( placeholder.toString() );
    		}
    	}
        if( placeholderMap != null ){
        	placeholderMap.write( out );
        }
        
        if( entry.asLeaf() != null ){
        	out.writeInt( entry.asLeaf().getId() );
        }
        else{
            SplitDockStationLayout.Node node = entry.asNode();
    		out.writeInt( node.getOrientation().ordinal() );
    		out.writeDouble( node.getDivider() );
    		writeEntry( node.getChildA(), out );
    		writeEntry( node.getChildB(), out );
        }
    }
    
    public SplitDockStationLayout read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException {
        Version version = Version.read( in );
        version.checkCurrent();
        
        boolean version8 = Version.VERSION_1_0_8.compareTo( version ) <= 0;
        boolean version8a = Version.VERSION_1_0_8a.compareTo( version ) <= 0;
        boolean version110 = Version.VERSION_1_1_0.compareTo( version ) <= 0;
        
        SplitDockStationLayout.Entry root = null;
        if( in.readBoolean() ){
            root = readEntry( in, version8, version8a, placeholders );
        }
        int fullscreen = in.readInt();
        boolean fullscreenAction = true;
        if( version110 ){
        	fullscreenAction = in.readBoolean();
        }
        return createLayout( root, fullscreen, fullscreenAction );
    }
    
    /**
     * Reads an entry from the stream.
     * @param in the stream to read
     * @param version8 version of file is at least 8
     * @param version8a version of file is at least 8a
     * @param strategy tells which placeholders are invalid
     * @return the new entry
     * @throws IOException if an I/O-error occurs
     */
    private SplitDockStationLayout.Entry readEntry( DataInputStream in, boolean version8, boolean version8a, PlaceholderStrategy strategy ) throws IOException{
    	long id = -1;
    	if( version8 ){
    		id = in.readLong();
    	}
    	if( version8a ){
    		byte flag = in.readByte();
    		boolean node = (flag & 1) != 0;
    		boolean hasPlaceholders = (flag & 2) != 0;
    		boolean hasMap = (flag & 4) != 0;
    		
    		Path[] placeholders = null;
    		if( hasPlaceholders ){
    			placeholders = new Path[ in.readInt() ];
    			for( int i = 0; i < placeholders.length; i++ ){
    				placeholders[i] = new Path( in.readUTF() );
    			}
    		}
    		
    		PlaceholderMap placeholderMap = null;
    		if( hasMap ){
    			placeholderMap = new PlaceholderMap( in, strategy );
    			placeholderMap.setPlaceholderStrategy( null );
    		}
    		
    		if( node ){
    			Orientation orientation = Orientation.values()[ in.readInt() ];
	            double divider = in.readDouble();
	            SplitDockStationLayout.Entry childA = readEntry( in, version8, version8a, strategy );
	            SplitDockStationLayout.Entry childB = readEntry( in, version8, version8a, strategy );
	            return new SplitDockStationLayout.Node( orientation, divider, childA, childB, placeholders, placeholderMap, id );
    		}
    		else{
    			return new SplitDockStationLayout.Leaf( in.readInt(), placeholders, placeholderMap, id );
    		}
    	}
    	else{
	        byte kind = in.readByte();
	        if( kind == 0 ){
	            return new SplitDockStationLayout.Leaf( in.readInt(), null, null, id );
	        }
	        if( kind == 1 ){
	            Orientation orientation = Orientation.values()[ in.readInt() ];
	            double divider = in.readDouble();
	            SplitDockStationLayout.Entry childA = readEntry( in, version8, version8a, strategy );
	            SplitDockStationLayout.Entry childB = readEntry( in, version8, version8a, strategy );
	            return new SplitDockStationLayout.Node( orientation, divider, childA, childB, null, null, id );
	        }
	        if( kind == 2 ){
	        	int childId = in.readInt();
	        	Path[] placeholders = readPlaceholders( in, strategy );
	        	return new SplitDockStationLayout.Leaf( childId, placeholders, null, id );
	        }
	        if( kind == 3 ){
	        	Orientation orientation = Orientation.values()[ in.readInt() ];
	            double divider = in.readDouble();
	            Path[] placeholders = readPlaceholders( in, strategy );
	            SplitDockStationLayout.Entry childA = readEntry( in, version8, version8a, strategy );
	            SplitDockStationLayout.Entry childB = readEntry( in, version8, version8a, strategy );
	            return new SplitDockStationLayout.Node( orientation, divider, childA, childB, placeholders, null, id );
	        }
	        throw new IOException( "unknown kind: " + kind );
    	}
    }
    
    private Path[] readPlaceholders( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException{
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
        
        element.addElement( "fullscreen-action" ).setBoolean( layout.hasFullscreenAction() );
        
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
        
        PlaceholderMap map = entry.getPlaceholderMap();
        if( map != null ){
        	XElement xmap = xchild.addElement( "placeholder-map" );
        	map.write( xmap );
        }
    }
    
    public SplitDockStationLayout read( XElement element, PlaceholderStrategy placeholders ) {
        SplitDockStationLayout.Entry root = null;
        XElement xroot = element.getElement( "node" );
        if( xroot == null )
            xroot = element.getElement( "leaf" );
        if( xroot != null )
            root = readEntry( xroot, placeholders );
        
        int fullscreen = -1;
        XElement xfullscreen = element.getElement( "fullscreen" );
        if( xfullscreen != null )
            fullscreen = xfullscreen.getInt( "id" );
        
        XElement xfullscreenAction = element.getElement( "fullscreen-action" );
        boolean fullscreenAction = true;
        if( xfullscreenAction != null ){
        	fullscreenAction = xfullscreenAction.getBoolean();
        }
        
        return createLayout( root, fullscreen, fullscreenAction );
    }
    
    /**
     * Transforms an xml-element into an entry.
     * @param element the element that should be converted, of type "node" or
     * "leaf".
     * @param strategy strategy used for removing invalid placeholders
     * @return the new entry
     */
    private SplitDockStationLayout.Entry readEntry( XElement element, PlaceholderStrategy strategy ){
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
    				if( strategy == null || strategy.isValidPlaceholder( placeholder )){
    					collection.add( placeholder );
    				}
    			}
    			placeholders = collection.toArray( new Path[ collection.size() ]);
    		}
    	}
    	
    	PlaceholderMap map = null;
    	XElement xmap = element.getElement( "placeholder-map" );
    	if( xmap != null ){
    		map = new PlaceholderMap( xmap, strategy );
    		map.setPlaceholderStrategy( null );
    	}
    	
        if( "leaf".equals( element.getName() )){
            return new SplitDockStationLayout.Leaf( element.getInt( "id" ), placeholders, map, nodeId );
        }
        if( "node".equals( element.getName() )){
        	XElement[] xchildren = element.getElements( "leaf", "node" );
        	
        	if( xchildren.length != 2 )
                throw new XException( "node element must have exactly least two children: " + element );
           
            
            
            return new SplitDockStationLayout.Node( 
                    Orientation.valueOf( element.getString( "orientation" ) ),
                    element.getDouble( "divider" ),
                    readEntry( xchildren[0], strategy ),
                    readEntry( xchildren[1], strategy ),
                    placeholders,
                    map,
                    nodeId );
        }
        throw new XException( "element neither leaf nor node: " + element );
    }
    
    /**
     * Creates new objects of {@link SplitDockStation}
     * @param hasFullscreenAction whether the station did have a fullscreen-action
     * @return the new instance
     */
    protected SplitDockStation createStation( boolean hasFullscreenAction ){
        return new SplitDockStation( hasFullscreenAction );
    }
}
