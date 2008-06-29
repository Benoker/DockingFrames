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
package bibliothek.extension.gui.dock.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.KeyStroke;

/**
 * A path is a description of the position of some resource. A path consists
 * of segments, segments are separated by a dot. A segment must be a valid
 * java-identifier.
 * @author Benjamin Sigg
 */
public final class Path {
    /** standard path for the integer type */
    public static final Path TYPE_INT_PATH = new Path( "dock.int" );
    
    /** standard path for {@link KeyStroke} */
    public static final Path TYPE_KEYSTROKE_PATH = new Path( "javax.swing.KeyStroke" );
    
    /** the segments of this path */
    private String[] segments;
    
    /**
     * Creates a new path with the given segments.
     * @param segments the path
     */
    private Path( String[] segments ){
        this.segments = segments;
    }
    
    /**
     * Creates a new root path.
     */
    public Path(){
        segments = new String[]{};
    }
    
    /**
     * Creates a new path. 
     * @param path the dot-separated segments of this path, each segment
     * must be a valid Java-identifier. Note that no segment should start with
     * "_"
     */
    public Path( String path ){
        if( path == null )
            throw new IllegalArgumentException( "path must not be null" );
        
        List<String> list = new ArrayList<String>();
        int lastDot = -1;
        
        for( int i = 0, n = path.length(); i <= n; i++ ){
           
            char c;
            if( i == n )
                c = '.';
            else
                c = path.charAt( i );
            
            if( c == '.' ){
                if( lastDot+1 == i )
                    throw new IllegalArgumentException( "not a path: empty segment" );
                
                list.add( path.substring( lastDot+1, i ) );
                lastDot = i;
            }
            else if( lastDot+1 == i ){
                if( !Character.isJavaIdentifierStart( c ))
                    throw new IllegalArgumentException( "not a valid start of a segment: '" + c + "'" );                
            }
            else{
                if( !Character.isJavaIdentifierPart( c ))
                    throw new IllegalArgumentException( "not a valid character of a segment: '" + c + "'" );
            }
        }
        
        segments = list.toArray( new String[ list.size()] );
    }

    /**
     * Gets the number of segments of this path.
     * @return the number of segments
     */
    public int getSegmentCount(){
        return segments.length;
    }
    
    /**
     * Gets the <code>index</code>'th segment of this path.
     * @param index the location of the segment
     * @return the segment
     */
    public String getSegment( int index ){
        return segments[index];
    }
    
    /**
     * Gets the last segment of this path or <code>null</code> if this is
     * the root path.
     * @return the last segment or <code>null</code>
     */
    public String getLastSegment(){
        if( segments.length == 0 )
            return null;
        
        return segments[ segments.length-1 ];
    }
    
    /**
     * Creates a new path that is a subset of this path.
     * @param offset the begin of the new path
     * @param length the length of the new path, at least 1
     * @return the new path
     */
    public Path subPath( int offset, int length ){
        if( length < 1 )
            throw new IllegalArgumentException( "length must be at least 1: " + length );
        
        String[] result = new String[ length ];
        System.arraycopy( segments, offset, result, 0, length );
        return new Path( result );
    }
    
    /**
     * Creates a new path which is a combination of <code>this</code> and <code>path</code>.
     * @param path the path to add
     * @return the new path
     */
    public Path append( Path path ){
        String[] segments = new String[ this.segments.length + path.segments.length ];
        System.arraycopy( this.segments, 0, segments, 0, this.segments.length );
        System.arraycopy( path.segments, 0, segments, this.segments.length, path.segments.length );
        return new Path( segments );
    }
    
    /**
     * Creates a new path which is not only a combination of <code>this</code>
     * and <code>path</code>, but is also unique in the way that
     * <code>x+y.z</code> would not yield the same as <code>x.y+z</code>. This
     * implies also that <code>(x+y)+z</code> would result in another path 
     * than <code>x+(y+z)</code>. Note that the result of this method differs 
     * from {@link #append(Path)}. Note also that the new path has a different
     * prefix than <code>this</code>.
     * @param path the additional path
     * @return the new path
     */
    public Path uniqueAppend( Path path ){
        String[] segments = new String[ this.segments.length + 2 + path.segments.length ];
        segments[0] = "_f" + this.segments.length;
        System.arraycopy( this.segments, 0, segments, 1, this.segments.length );
        segments[ this.segments.length+1 ] = "_s" + path.segments.length;
        System.arraycopy( path.segments, 0, segments, this.segments.length+2, path.segments.length );
        return new Path( segments );
    }
    
    /**
     * Creates a new path appending <code>segments</code> to this path.
     * @param segments the additional segments
     * @return the new path
     */
    public Path append( String segments ){
        return append( new Path( segments ));
    }
    
    /**
     * Returns the parent of this path.
     * @return the parent or <code>null</code> if this is the root
     */
    public Path getParent(){
        if( segments.length == 0 )
            return null;
        
        String[] result = new String[ segments.length-1 ];
        System.arraycopy( segments, 0, result, 0, result.length );
        return new Path( result );
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode( segments );
    }

    @Override
    public boolean equals( Object obj ) {
        if( this == obj )
            return true;
        if( obj == null )
            return false;
        if( getClass() != obj.getClass() )
            return false;
        final Path other = (Path)obj;
        if( !Arrays.equals( segments, other.segments ) )
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for( int i = 0, n = segments.length; i<n; i++ ){
            if( i > 0 )
                builder.append( "." );
            
            builder.append( segments[i] );
        }
        
        return builder.toString();
    }
}
