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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.layout.AbstractDockableProperty;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A {@link DockableProperty} used by the {@link SplitDockStation} to describe
 * the location of a {@link Dockable} in the tree of all children of the station.
 * @author Benjamin Sigg
 */
public class SplitDockPathProperty extends AbstractDockableProperty implements Iterable<SplitDockPathProperty.Node>{
    /** The direction which the path takes */
    public static enum Location{
        /** the tree splits horizontally and the path is to the left */
        LEFT, 
        /** the tree splits horizontally and the path is to the right */
        RIGHT,
        /** the tree splits vertically and the path is to the top */
        TOP,
        /** the tree splits vertically and the path is to the bottom */
        BOTTOM
    }
    
    /** the path, where the first entry is the node nearest to the root */
    private List<Node> nodes = new LinkedList<Node>();
    
    /**
     * Creates a new, empty path
     */
    public SplitDockPathProperty(){
        // do nothing
    }
    

    public DockableProperty copy() {
        SplitDockPathProperty copy = new SplitDockPathProperty();
        for( Node node : nodes ){
            copy.add( node.getLocation(), node.getSize() );
        }
        copy( copy );
        return copy;
    }
    
    public Iterator<SplitDockPathProperty.Node> iterator() {
        return nodes.iterator();
    }
    
    /**
     * Gets the number of nodes stores in this property.
     * @return the number of nodes
     */
    public int size(){
        return nodes.size();
    }
    
    /**
     * Gets the index'th node, where the node 0 is the node nearest to the
     * root.
     * @param index the index of the node
     * @return the node
     */
    public SplitDockPathProperty.Node getNode( int index ){
        return nodes.get( index );
    }
    
    /**
     * Calculates which bounds the element accessed through the given path would
     * have.
     * @return the bounds
     */
    public SplitDockProperty toLocation(){
        double x = 0, y = 0, w = 1, h = 1;
        for( Node node : nodes ){
            switch( node.getLocation() ){
                case LEFT:
                    w = w * node.getSize();
                    break;
                case RIGHT:
                    x = x + w - w * node.getSize();
                    w = w * node.getSize();
                    break;
                case TOP:
                    h = h * node.getSize();
                    break;
                case BOTTOM:
                    y = y + h - h * node.getSize();
                    h = h * node.getSize();
                    break;
            }
        }
        
        SplitDockProperty property = new SplitDockProperty( x, y, w, h );
        property.setSuccessor( getSuccessor() );
        return property;
    }
    
    /**
     * Adds a new element to the end of the path. Every element describes which turn the
     * path takes in a node.
     * @param location the direction into which the path goes
     * @param size the relative size of the path, a value in the range 0.0
     * to 1.0
     */
    public void add( Location location, double size ){
        insert( location, size, nodes.size() );
    }
    
    /**
     * Adds a new element to the path. Every element describes which turn the
     * path takes in a node.
     * @param location the direction into which the path goes
     * @param size the relative size of the path, a value in the range 0.0
     * to 1.0
     * @param index where to add the new element
     */
    public void insert( Location location, double size, int index ){
        if( location == null )
            throw new NullPointerException( "location must not be null" );
        
        if( size < 0 || size > 1.0 )
            throw new IllegalArgumentException( "size must be in the range 0.0 to 1.0");
        
        nodes.add( index, new Node( location, size ) );
    }
    
    public String getFactoryID() {
        return SplitDockPathPropertyFactory.ID;
    }

    public void store( DataOutputStream out ) throws IOException {
        Version.write( out, Version.VERSION_1_0_4 );
        out.writeInt( nodes.size() );
        for( Node node : nodes ){
            switch( node.getLocation() ){
                case LEFT:
                    out.writeByte( 0 );
                    break;
                case RIGHT:
                    out.writeByte( 1 );
                    break;
                case TOP:
                    out.writeByte( 2 );
                    break;
                case BOTTOM:
                    out.writeByte( 3 );
                    break;
            }
            out.writeDouble( node.getSize() );
        }
    }
    
    public void store( XElement element ) {
        for( Node node : nodes ){
            XElement xnode = element.addElement( "node" );
            xnode.addString( "location", node.getLocation().name() );
            xnode.addDouble( "size", node.getSize() );
        }
    }
    
    public void load( DataInputStream in ) throws IOException {
        Version version = Version.read( in );
        version.checkCurrent();
        nodes.clear();
        int count = in.readInt();
        while( count > 0 ){
            count--;
            Location location = null;
            switch( in.readByte() ){
                case 0:
                    location = Location.LEFT;
                    break;
                case 1:
                    location = Location.RIGHT;
                    break;
                case 2:
                    location = Location.TOP;
                    break;
                case 3:
                    location = Location.BOTTOM;
                    break;
            }
            double size = in.readDouble();
            nodes.add( new Node( location, size ) );
        }
    }
    
    public void load( XElement element ) {
        nodes.clear();
        for( XElement xnode : element.getElements( "node" )){
            nodes.add( new Node( Location.valueOf( xnode.getString( "location" )), xnode.getDouble( "size" ) ) );
        }
    }
    
    @Override
    public String toString() {
        return getClass().getName() + "[nodes="+ nodes +"]";
    }
    
    @Override
	public int hashCode(){
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		return result;
	}


	@Override
	public boolean equals( Object obj ){
		if( this == obj )
			return true;
		if( !super.equals( obj ) )
			return false;
		if( !(obj instanceof SplitDockPathProperty) )
			return false;
		SplitDockPathProperty other = (SplitDockPathProperty)obj;
		if( nodes == null ){
			if( other.nodes != null )
				return false;
		}else if( !nodes.equals( other.nodes ) )
			return false;
		return true;
	}



	/**
     * Describes one turn of the path.
     * @author Benjamin Sigg
     */
    public static class Node{
        /** the amount of space the path gets in this turn */
        private double size;
        /** the direction into which the path goes */
        private Location location;
        
        /**
         * Creates a new turn.
         * @param location the direction into which the path goes
         * @param size the amount of space the path gets in this turn
         */
        public Node( Location location, double size ){
            this.location = location;
            this.size = size;
        }
        
        /**
         * Gets the amount of space the path gets in this turn
         * @return the amount of space, a value in the range 0.0 to 1.0.
         */
        public double getSize() {
            return size;
        }
        
        /**
         * Gets the direction into which the path goes
         * @return the direction
         */
        public Location getLocation() {
            return location;
        }
        

        @Override
        public String toString() {
            return getClass().getName() + "[size=" + size + ",location=" + location + "]";
        }

		@Override
		public int hashCode(){
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((location == null) ? 0 : location.hashCode());
			long temp;
			temp = Double.doubleToLongBits( size );
			result = prime * result + (int)(temp ^ (temp >>> 32));
			return result;
		}

		@Override
		public boolean equals( Object obj ){
			if( this == obj )
				return true;
			if( obj == null )
				return false;
			if( !(obj instanceof Node) )
				return false;
			Node other = (Node)obj;
			if( location == null ){
				if( other.location != null )
					return false;
			}else if( !location.equals( other.location ) )
				return false;
			if( Double.doubleToLongBits( size ) != Double
					.doubleToLongBits( other.size ) )
				return false;
			return true;
		}
    }
}
