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

package bibliothek.gui.dock.station.stack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.layout.AbstractDockableProperty;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * A StackDockProperty stores the location of a {@link Dockable}
 * on a {@link StackDockStation}. The <code>Dockables</code> on
 * a <code>StackDockStation</code> are ordered in some way, and
 * the location is used to tell which <code>Dockable</code> is
 * in front of another <code>Dockable</code>.
 * @author Benjamin Sigg
 */
public class StackDockProperty extends AbstractDockableProperty {
    /** The first location on a {@link StackDockStation} */
    public static final StackDockProperty FRONT = new StackDockProperty( 0 );
    
    /** The last location on a {@link StackDockStation} */
    public static final StackDockProperty BACK = new StackDockProperty( Integer.MAX_VALUE );
    
    private int index;
    
    private Path placeholder;

    /**
     * Constructs a property.
     * @param index The location
     */
    public StackDockProperty( int index ){
        setIndex( index );
    }
    

    /**
     * Constructs a property.
     * @param index The location
     * @param placeholder a name for this location
     */
    public StackDockProperty( int index, Path placeholder ){
        setIndex( index );
        setPlaceholder( placeholder );
    }
    
    /**
     * Constructs a property with a location equal to 0.
     */
    public StackDockProperty(){
    	// do nothing
    }
    
    @Override
    public String toString(){
	    return getClass().getSimpleName() + "[index=" + index + ", placeholder=" + placeholder + ", successor=" + getSuccessor() + "]";
    }

    public DockableProperty copy() {
        StackDockProperty copy = new StackDockProperty( index );
        copy( copy );
        return copy;
    }
    
    /**
     * Sets the location which is determined by this property. The smallest
     * location is 0.
     * @param index the location
     */
    public void setIndex( int index ) {
        this.index = index;
    }
    
    /**
     * Gets the location of this property.
     * @return the location
     * @see #setIndex(int)
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * Sets the placeholder name this location.
     * @param placeholder the placeholder, can be <code>null</code>
     */
    public void setPlaceholder( Path placeholder ){
		this.placeholder = placeholder;
	}
    
    /**
     * Gets the placeholder naming this location.
     * @return the placeholder, can be <code>null</code>
     */
    public Path getPlaceholder(){
		return placeholder;
	}
    
    public String getFactoryID() {
        return StackDockPropertyFactory.ID;
    }

    public void store( DataOutputStream out ) throws IOException {
        Version.write( out, Version.VERSION_1_0_8 );
        out.writeInt( index );
        if( placeholder == null ){
        	out.writeBoolean( false );
        }
        else{
        	out.writeBoolean( true );
        	out.writeUTF( placeholder.toString() );
        }
    }
    
    public void store( XElement element ) {
    	if( index >= 0 ){
    		element.addElement( "index" ).setInt( index );
    	}
    	
        if( placeholder != null ){
        	element.addElement( "placeholder" ).setString( placeholder.toString() );
        }
    }

    public void load( DataInputStream in ) throws IOException {    	
        Version version = Version.read( in );
        version.checkCurrent();
        boolean version8 = Version.VERSION_1_0_8.compareTo( version ) <= 0;
        index = in.readInt();
        placeholder = null;
        
        if( version8 ){
        	if( in.readBoolean() ){
        		placeholder = new Path( in.readUTF() );
        	}
        }
    }
    
    public void load( XElement element ) {
    	index = -1;
    	placeholder = null;
    	
    	XElement xindex = element.getElement( "index" );
    	XElement xplaceholder = element.getElement( "placeholder" );
    	
    	if( xindex == null && xplaceholder == null ){
    		if( element.getValue() != null && element.getValue().length() > 0 ){
    			index = element.getInt();
    		}
    	}
    	else{
    		if( xindex != null ){
    			index = xindex.getInt();
    		}
    		if( xplaceholder != null ){
    			placeholder = new Path( xplaceholder.getString() );
    		}
    	}
    }

	@Override
	public int hashCode(){
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals( Object obj ){
        if( this == obj ) {
            return true;
        }

        if( obj == null ) {
            return false;
        }

        if( !super.equals( obj ) ) {
            return false;
        }

		if( obj.getClass() == this.getClass() ) {
            StackDockProperty other = (StackDockProperty)obj;
            if( index != other.index )
                return false;
            return true;
        }
        return false;
	}
}
