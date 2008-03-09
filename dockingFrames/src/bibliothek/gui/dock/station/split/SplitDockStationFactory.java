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
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.station.split.SplitDockStationLayout.Entry;
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
    
    public String getID() {
        return ID;
    }
    
    public SplitDockStationLayout getLayout( SplitDockStation station,
            final Map<Dockable, Integer> children ) {
        
        Entry root =
            station.visit( new SplitTreeFactory<Entry>(){
                public Entry leaf( Dockable dockable ) {
                    Integer id = children.get( dockable );
                    if( id != null ){
                        return new SplitDockStationLayout.Leaf( id );
                    }
                    else{
                        return null;
                    }
                }

                public Entry root( Entry root ) {
                    return root;
                }

                public Entry horizontal( Entry left, Entry right, double divider ) {
                    if( left == null )
                        return right;
                    
                    if( right == null )
                        return left;
                    
                    return new SplitDockStationLayout.Node( Orientation.HORIZONTAL,
                            divider, left, right );
                }

                public Entry vertical( Entry top, Entry bottom, double divider ) {
                    if( top == null )
                        return bottom;
                    
                    if( bottom == null )
                        return top;
                    
                    return new SplitDockStationLayout.Node( Orientation.VERTICAL,
                            divider, top, bottom );
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
    
    public void setLayout( SplitDockStation station,
            SplitDockStationLayout layout, Map<Integer, Dockable> children ) {
        
        SplitDockTree tree = new SplitDockTree();
        SplitDockTree.Key root = null;
        if( layout.getRoot() != null ){
            root = handleEntry( layout.getRoot(), tree, children );
        }
        if( root != null ){
            tree.root( root );
        }
        station.dropTree( tree, false );
        
        station.setFullScreen( children.get( layout.getFullscreen() ) );
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
        if( dockable == null )
            return null;
        return tree.put( dockable );
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
                return tree.horizontal( a, b, node.getDivider() );
            case VERTICAL:
                return tree.vertical( a, b, node.getDivider() );
        }
        
        return null;
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

        Version.write( out, Version.VERSION_1_0_4 );
        
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
        if( entry.asLeaf() != null ){
            out.writeByte( 0 );
            out.writeInt( entry.asLeaf().getId() );            
        }
        else{
            SplitDockStationLayout.Node node = entry.asNode();
            out.writeByte( 1 );
            out.writeInt( node.getOrientation().ordinal() );
            out.writeDouble( node.getDivider() );
            writeEntry( node.getChildA(), out );
            writeEntry( node.getChildB(), out );
        }
    }
    
    public SplitDockStationLayout read( DataInputStream in ) throws IOException {
        Version version = Version.read( in );
        version.checkCurrent();
        
        SplitDockStationLayout.Entry root = null;
        if( in.readBoolean() ){
            root = readEntry( in );
        }
        int fullscreen = in.readInt();
        return new SplitDockStationLayout( root, fullscreen );
    }
    
    /**
     * Reads an entry from the stream.
     * @param in the stream to read
     * @return the new entry
     * @throws IOException if an I/O-error occurs
     */
    private SplitDockStationLayout.Entry readEntry( DataInputStream in ) throws IOException{
        byte kind = in.readByte();
        if( kind == 0 ){
            return new SplitDockStationLayout.Leaf( in.readInt() );
        }
        if( kind == 1 ){
            Orientation orientation = Orientation.values()[ in.readInt() ];
            double divider = in.readDouble();
            SplitDockStationLayout.Entry childA = readEntry( in );
            SplitDockStationLayout.Entry childB = readEntry( in );
            return new SplitDockStationLayout.Node( orientation, divider, childA, childB );
        }
        throw new IOException( "unknown kind: " + kind );
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
        if( entry.asLeaf() != null ){
            parent.addElement( "leaf" ).addInt( "id", entry.asLeaf().getId() );
        }
        else{
            XElement xnode = parent.addElement( "node" );
            xnode.addString( "orientation", entry.asNode().getOrientation().name() );
            xnode.addDouble( "divider", entry.asNode().getDivider() );
            writeEntry( entry.asNode().getChildA(), xnode );
            writeEntry( entry.asNode().getChildB(), xnode );
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
        if( "leaf".equals( element.getName() )){
            return new SplitDockStationLayout.Leaf( element.getInt( "id" ));
        }
        if( "node".equals( element.getName() )){
            if( element.getElementCount() != 2 )
                throw new XException( "node element must have exactly two children: " + element );
            
            return new SplitDockStationLayout.Node( 
                    Orientation.valueOf( element.getString( "orientation" ) ),
                    element.getDouble( "divider" ),
                    readEntry( element.getElement( 0 ) ),
                    readEntry( element.getElement( 1 ) ));
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
