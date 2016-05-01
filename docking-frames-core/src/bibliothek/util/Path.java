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
package bibliothek.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.KeyStroke;

import bibliothek.extension.gui.dock.preference.preferences.KeyStrokeValidator;
import bibliothek.extension.gui.dock.preference.preferences.choice.Choice;
import bibliothek.gui.dock.control.ModifierMask;

/**
 * A path is a description of the position of some resource. A path consists
 * of segments where a segment can be any kind of string (preferably a segment is a 
 * valid java-identifier). A path can be converted into a string, the segments will
 * be {@link #encodeSegment(String) encoded} when doing that.
 * @author Benjamin Sigg
 */
public final class Path {
    /** standard path for {@link Integer}  */
    public static final Path TYPE_INT_PATH = new Path( "java.lang.Integer" );
    
    /** standard path for {@link String} */
    public static final Path TYPE_STRING_PATH = new Path( "java.lang.String" );
    
    /** standard path for {@link Boolean} */
    public static final Path TYPE_BOOLEAN_PATH = new Path( "java.lang.Boolean" );
    
    /** standard path for {@link KeyStroke}, can use {@link KeyStrokeValidator} as information */
    public static final Path TYPE_KEYSTROKE_PATH = new Path( "javax.swing.KeyStroke" );
    
    /** standard path for {@link ModifierMask} */
    public static final Path TYPE_MODIFIER_MASK_PATH = new Path( "dock.modifier_mask" );
    
    /** standard path for a choice using a {@link String} as value and a {@link Choice} as information */
    public static final Path TYPE_STRING_CHOICE_PATH = new Path( "dock.choice" );
    
    /** standard path for a label, a label is not shown in an enabled editor */
    public static final Path TYPE_LABEL = new Path( "dock.label" );
    
    /**
     * Puts an escape character before any illegal character of <code>segment</code>, thus
     * creating a valid segment.
     * @param segment the segment to encode
     * @return the valid segment
     */
    public static String encodeSegment( String segment ){
    	StringBuilder builder = new StringBuilder( segment.length() );
    	
    	for( int i = 0, n = segment.length(); i<n; i++ ){
    		char c = segment.charAt( i );
    		boolean escape = false;
	        if( c == '.' ){
	            escape = true;
	        }
	        else if( c == '\\' ){
	        	escape = true;
	        }
	        else if( i == 0 ){
	            if( !Character.isJavaIdentifierStart( c )){
	            	escape = true;
	            }
	        }
	        else{
	            if( !Character.isJavaIdentifierPart( c )){
	            	escape = true;
	            }
	        }
	        if( escape ){
	        	builder.append( '\\' );
	        }
	        builder.append( c );
    	}
    	
    	return builder.toString();
    }
    
    /**
     * The opposite of {@link #encodeSegment(String)}.
     * @param segment some segment with escape characters
     * @return the original form of the segment
     */
    public static String decodeSegment( String segment ){
    	StringBuilder builder = new StringBuilder( segment.length() );
    	boolean escape = false;
    	
    	for( int i = 0, n = segment.length(); i<n; i++ ){
    		char c = segment.charAt( i );
    		if( escape ){
    			escape = false;
    			builder.append( c );
    		}
    		else if( c == '\\'){
    			escape = true;
    		}
    		else{
    			builder.append( c );
    		}
    	}
    	
    	return builder.toString();
    }
    
    /** the segments of this path */
    private String[] segments;
    
    /**
     * Tells whether <code>path</code> is a valid path or not
     * @param path the path to test
     * @return <code>true</code> if the segment is valid 
     */
    public static boolean isValidPath( String path ){
        try{
            new Path( path );
            return true;
        }
        catch( IllegalArgumentException ex ){
            return false;
        }
    }
    

    /**
     * Creates a new path with the given segments.
     * @param segments the path
     */
    public Path( String... segments ){
    	this.segments = new String[ segments.length ];
    	System.arraycopy( segments, 0, this.segments, 0, segments.length );
    	
    	for( String check : this.segments ){
    		if( check == null ){
    			throw new IllegalArgumentException( "null segments are not allowed" );
    		}
    	}
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
     * "_". Clients may use {@link #encodeSegment(String)} to use any character
     * within a single segment.
     */
    public Path( String path ){
        if( path == null )
            throw new IllegalArgumentException( "path must not be null" );
        
        List<String> list = new ArrayList<String>();
        int lastDot = -1;
        boolean escape = false;
        
        for( int i = 0, n = path.length(); i <= n; i++ ){
            char c;
            if( i == n ){
            	escape = false;
                c = '.';
            }
            else{
                c = path.charAt( i );
            }
            
            if( escape ){
            	escape = false;
            }
            else{
	            if( c == '.' ){
	                if( lastDot+1 == i )
	                    throw new IllegalArgumentException( "not a path: empty segment" );
	                
	                list.add( decodeSegment( path.substring( lastDot+1, i ) ) );
	                lastDot = i;
	            }
	            else if( c == '\\' ){
	            	escape = true;
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
    
    /**
     * Tells whether the first segments of this {@link Path} matches
     * the segments of <code>path</code>. 
     * @param path some other path
     * @return <code>true</code> if this path is either equal to <code>path</code> or
     * if this path starts with <code>path</code>
     */
    public boolean startsWith( Path path ){
    	if( path.getSegmentCount() > getSegmentCount() ){
    		return false;
    	}
    	for( int i = 0, n = path.getSegmentCount(); i<n; i++ ){
    		if( !path.getSegment( i ).equals( getSegment( i ) )){
    			return false;
    		}
    	}
    	return true;
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
            
            builder.append( encodeSegment( segments[i] ) );
        }
        
        return builder.toString();
    }
}
