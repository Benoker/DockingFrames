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
package bibliothek.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A version represents a version of this library.
 * @author Benjamin Sigg
 */
public final class Version implements Comparable<Version>{
    /** the version 1.0.4 */
    public static final Version VERSION_1_0_4 = new Version( 1, 0, 4, null );
    
    /** the version 1.0.5 */
    public static final Version VERSION_1_0_5 = new Version( 1, 0, 5, null );
    
    /** the version 1.0.6 */
    public static final Version VERSION_1_0_6 = new Version( 1, 0, 6, null );
    
    /** the version 1.0.7 */
    public static final Version VERSION_1_0_7 = new Version( 1, 0, 7, null );
    
    /** the version 1.0.8 */
    public static final Version VERSION_1_0_8 = new Version( 1, 0, 8, null );
    
    /** the version 1.0.8 (since preview 4) */
    public static final Version VERSION_1_0_8a = new Version( 1, 0, 8, "a" );
    
    /** the version 1.1.0 */ 
    public static final Version VERSION_1_1_0 = new Version( 1, 1, 0, null );
    
    /** the version 1.1.0 (since preview 5)*/ 
    public static final Version VERSION_1_1_0a = new Version( 1, 1, 0, "a" );
    
    /** the version 1.1.1 */
    public static final Version VERSION_1_1_1 = new Version( 1, 1, 1, null );
    
    /** the version 1.1.1 (since preview 5c) */
    public static final Version VERSION_1_1_1a = new Version( 1, 1, 1, "a" );
    
    /** version 1.1.2 */
    public static final Version VERSION_1_1_2 = new Version( 1, 1, 2, null );
    
    /** The current version of this library. This constant will be changed for every release. */
    public static final Version CURRENT = VERSION_1_1_2;
    
    private int major;
    private int minor;
    private int mikro;
    private String add;
    
    /**
     * Creates a new version
     * @param major the major version
     * @param minor the minor version
     * @param mikro the mikro version
     * @param add additional comment, <code>null</code> will be replaced by the empty string
     */
    public Version( int major, int minor, int mikro, String add ){
        if( major < 0 )
            throw new IllegalArgumentException( "major must not be smaller than 0" );
        if( minor < 0 )
            throw new IllegalArgumentException( "minor must not be smaller than 0" );
        if( mikro < 0 )
            throw new IllegalArgumentException( "mikro must not be smaller than 0" );
        if( add == null )
            add = "";
        
        this.major = major;
        this.mikro = mikro;
        this.minor = minor;
        this.add = add;
    }
    
    /**
     * Writes the contents of a version.
     * @param out the stream to write into
     * @param version the version to write
     * @throws IOException if an error occurs
     */
    public static void write( DataOutputStream out, Version version ) throws IOException{
        out.writeByte( 1 );
        out.writeInt( version.major );
        out.writeInt( version.minor );
        out.writeInt( version.mikro );
        out.writeUTF( version.add );
    }
    
    /**
     * Reads a version that was stored earlier.
     * @param in the stream to read from
     * @return the new version
     * @throws IOException if an error occurs or the format is unknown
     */
    public static Version read( DataInputStream in ) throws IOException{
        byte version = in.readByte();
        if( version != 1 )
            throw new IOException( "Unknown format for version" );
        int major = in.readInt();
        int minor = in.readInt();
        int mikro = in.readInt();
        String add = in.readUTF();
        return new Version( major, minor, mikro, add );
    }
    
    /**
     * Checks whether this version is greater than the current version and
     * throws an exception if so. This method is intended to be called from code
     * that is loading some file, but other usages are possible as well.
     * @throws IOException thrown if the current version is smaller than <code>this</code>.
     */
    public void checkCurrent() throws IOException{
        if( this.compareTo( CURRENT ) > 0 )
            throw new IOException( "Trying to read something from the future: " + this + " is greater than the current version " + CURRENT );
    }
    
    /**
     * Gets the additional information
     * @return the additional information, can be an empty string
     */
    public String getAdd() {
        return add;
    }
    
    /**
     * Gets the major number.
     * @return the major
     */
    public int getMajor() {
        return major;
    }
    
    /**
     * Gets the mikro number
     * @return the mikro
     */
    public int getMikro() {
        return mikro;
    }
    
    /**
     * Gets the minor number
     * @return the minor
     */
    public int getMinor() {
        return minor;
    }
    
    public int compareTo( Version o ) {
        if( major > o.major )
            return 1;
        if( major < o.major ) 
            return -1;
        if( minor > o.minor )
            return 1;
        if( minor < o.minor )
            return -1;
        if( mikro > o.mikro )
            return 1;
        if( mikro < o.mikro )
            return -1;
        
        return add.compareTo( o.add );
    }
    
    @Override
    public String toString() {
        return major + "." + minor + "." + mikro + add;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( add == null ) ? 0 : add.hashCode() );
        result = prime * result + major;
        result = prime * result + mikro;
        result = prime * result + minor;
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if( this == obj )
            return true;
        if( obj == null )
            return false;
        if( getClass() != obj.getClass() )
            return false;
        final Version other = (Version)obj;
        if( add == null ) {
            if( other.add != null )
                return false;
        } else if( !add.equals( other.add ) )
            return false;
        if( major != other.major )
            return false;
        if( mikro != other.mikro )
            return false;
        if( minor != other.minor )
            return false;
        return true;
    }
    
    
}
