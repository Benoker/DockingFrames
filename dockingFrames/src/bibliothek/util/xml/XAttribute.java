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
package bibliothek.util.xml;

/**
 * An attribute is an element in a XML-file that has a name and perhaps a value.
 * @author Benjamin Sigg
 */
public class XAttribute extends XContainer{
    /** the name of this attribute */
    private String name;

    /**
     * Creates a new attribute.
     * @param name the name of the attribute
     */
    public XAttribute( String name ){
        setName( name );
    }
    
    @Override
    public XAttribute copy() {
    	XAttribute copy = new XAttribute( name );
    	copy.copy( this );
    	return copy;
    }
    
    /**
     * Sets the name of this attribute.
     * @param name the new name
     */
    public void setName( String name ) {
        if( name == null )
            throw new IllegalArgumentException( "name must not be null" );
        
        this.name = name;
    }
    
    /**
     * Gets the name of this attribute.
     * @return the name
     */
    public String getName() {
        return name;
    }
}
