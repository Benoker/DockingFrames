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

package bibliothek.gui.dock.station.flap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.layout.AbstractDockableProperty;
import bibliothek.util.Path;
import bibliothek.util.Version;
import bibliothek.util.xml.XElement;

/**
 * FlapDockProperties are used on the {@link FlapDockStation} to determine
 * the location of a {@link Dockable}.
 * @author Benjamin Sigg
 *
 */
public class FlapDockProperty extends AbstractDockableProperty {
	/**
	 * The location of the first {@link Dockable}.
	 */
    public static final FlapDockProperty FIRST = new FlapDockProperty( 0 );
    
    /**
     * The location of the last {@link Dockable}.
     */
    public static final FlapDockProperty LAST = new FlapDockProperty( Integer.MAX_VALUE );
    
    /** the location of the element */
    private int index;

    /** whether the element is pinned */
    private boolean holding = false;

    /** the size of the window, -1 if unknown */
    private int size = -1;
    
    /** placeholder name of this location */
    private Path placeholder;
    
    /**
     * Constructs a FlapDockProperty
     */
    public FlapDockProperty(){
    	// do nothing
    }
    
    /**
     * Constructs a FlapDockProperty
     * @param index the location of the {@link Dockable}
     * @see #setIndex(int)
     */
    public FlapDockProperty( int index ){
        setIndex( index );
    }
    

    /**
     * Constructs a FlapDockProperty
     * @param index the location of the {@link Dockable}
     * @param holding whether the <code>Dockable</code> is pinned down or not
     * @param size the size of the window, -1 if unknown
     * @see #setIndex(int)
     * @see #setHolding(boolean)
     * @see #setSize(int)
     */
    public FlapDockProperty( int index, boolean holding, int size ){
        this( index, holding, size, null );
    }
    
    /**
     * Constructs a FlapDockProperty
     * @param index the location of the {@link Dockable}
     * @param holding whether the <code>Dockable</code> is pinned down or not
     * @param size the size of the window, -1 if unknown
     * @param placeholder the name of this location
     * @see #setIndex(int)
     * @see #setHolding(boolean)
     * @see #setSize(int)
     * @see #setPlaceholder(Path)
     */
    public FlapDockProperty( int index, boolean holding, int size, Path placeholder ){
    	setIndex( index );
        setHolding( holding );
        setSize( size );
        setPlaceholder( placeholder );
    }

    public FlapDockProperty copy() {
        FlapDockProperty copy = new FlapDockProperty( index, holding, size, placeholder );
        copy( copy );
        return copy;
    }
    
    /**
     * Sets the location of the {@link Dockable} on its {@link FlapDockStation}.
     * @param index the location
     */
    public void setIndex( int index ){
        if( index < 0 )
            throw new IllegalArgumentException( "Index must be >= 0: " + index );
        
        this.index = index;
    }
    
    /**
     * Gets the location of the {@link Dockable} on its {@link FlapDockStation}.
     * @return the location
     * @see #setIndex(int)
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * Whether the element should be pinned down or not.
     * @param holding <code>true</code> if it should be pinned, <code>false</code>
     * if not
     */
    public void setHolding(boolean holding) {
		this.holding = holding;
	}
    
    /**
     * Tells whether an element was pinned down or not.
     * @return <code>true</code> if pinned down, <code>false</code> otherwise
     */
    public boolean isHolding() {
		return holding;
	}
    
    /**
     * Sets the size the window has in which the <code>Dockable</code> will
     * be presented.
     * @param size the size, -1 if unknown
     */
    public void setSize( int size ){
		this.size = size;
	}
    
    /**
     * Gets the size the window has in which the <code>Dockable</code> will
     * be presented.
     * @return the size or -1 if unknown
     */
    public int getSize() {
		return size;
	}
    
    /**
     * Sets the name of this location.
     * @param placeholder the placeholder, can be <code>null</code>
     */
    public void setPlaceholder( Path placeholder ){
		this.placeholder = placeholder;
	}
    
    /**
     * Gets the name of this location.
     * @return the name, can be <code>null</code>
     */
    public Path getPlaceholder(){
		return placeholder;
	}
    
    public String getFactoryID() {
        return FlapDockPropertyFactory.ID;
    }

    public void store( DataOutputStream out ) throws IOException {
        Version.write( out, Version.VERSION_1_0_8 );
        out.writeInt( index );
        out.writeBoolean( holding );
        out.writeInt( size );
        if( placeholder == null ){
        	out.writeBoolean( false );
        }
        else{
        	out.writeBoolean( true );
            out.writeUTF( placeholder.toString() );
        }
    }
    
    public void store( XElement element ) {
        // element.setInt( index );
    	element.addElement( "index" ).setInt( index );
    	element.addElement( "holding" ).setBoolean( holding );
    	if( size >= 0 ){
    		element.addElement( "size" ).setInt( size );
    	}
    	if( placeholder != null ){
    		element.addElement( "placeholder" ).setString( placeholder.toString() );
    	}
    }

    public void load( DataInputStream in ) throws IOException {
        Version version = Version.read( in );
        version.checkCurrent();
        
        setIndex( in.readInt() );
        if( version.compareTo( Version.VERSION_1_0_8 ) >= 0 ){
        	holding = in.readBoolean();
        	size = in.readInt();
        	if( in.readBoolean() ){
        		placeholder = new Path( in.readUTF() );
        	}
        }
        else if( version.compareTo( Version.VERSION_1_0_7 ) >= 0 ){
        	holding = in.readBoolean();
        	size = in.readInt();
        }
        else{
        	holding = false;
        	size = -1;
        }
    }
    
    public void load( XElement element ) {
    	XElement xindex = element.getElement( "index" );
    	XElement xholding = element.getElement( "holding" );
    	XElement xsize = element.getElement( "size" );
    	XElement xplaceholder = element.getElement( "placeholder" );
    	
    	if( xindex == null && xholding == null ){
    		index = element.getInt();
    	}
    	else{
    		if( xindex != null )
    			index = xindex.getInt();
    		
    		if( xholding != null )
    			holding = xholding.getBoolean();
    		
    		if( xsize == null )
    			size = -1;
    		else
    			size = xsize.getInt();
    	}
    	
    	if( xplaceholder != null ){
    		placeholder = new Path( xplaceholder.getString() );
    	}
    }
    
    @Override
    public String toString(){
	    return getClass().getName() + "[index=" + index + ", holding=" + holding + ", size=" + size + ", placeholder=" + placeholder + "]";
    }

	@Override
	public int hashCode(){
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (holding ? 1231 : 1237);
		result = prime * result + index;
		result = prime * result
				+ ((placeholder == null) ? 0 : placeholder.hashCode());
		result = prime * result + size;
		return result;
	}

	@Override
	public boolean equals( Object obj ){
		if (this == obj)
			return true;
		if (!super.equals( obj ))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FlapDockProperty other = (FlapDockProperty) obj;
		if (holding != other.holding)
			return false;
		if (index != other.index)
			return false;
		if (placeholder == null) {
			if (other.placeholder != null)
				return false;
		} else if (!placeholder.equals( other.placeholder ))
			return false;
		if (size != other.size)
			return false;
		return true;
	}
}
