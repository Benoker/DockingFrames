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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A {@link XElement} is an entry in a xml-file. It has a name, can have children
 * and attributes, and might have a value.
 * @author Benjamin Sigg
 */
public class XElement extends XContainer implements Iterable<XElement>{
    /** the attributes of this entry */
    private List<XAttribute> attributes = new ArrayList<XAttribute>();
    /** the children of this entry */
    private List<XElement> children = new ArrayList<XElement>();
    
    /** the name of this attribute */
    private String name;

    /**
     * Creates a new entry with given name.
     * @param name the name of this entry
     */
    public XElement( String name ){
        setName( name );
    }
    
    @Override
    public XElement copy() {
    	XElement copy = new XElement( name );
    	copy.copy( this );
    	return copy;
    }
    
    /**
     * Makes a copy of all the elements of <code>original</code>
     * and stores them in this container.
     * @param original the element to copy
     */
    public void copy( XElement original ){
    	super.copy( original );
    	
    	attributes.clear();
    	for( XAttribute attr : original.attributes )
    		attributes.add( attr.copy() );
    	
    	children.clear();
    	for( XElement child : original.children )
    		children.add( child.copy() );
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
    
    public Iterator<XElement> iterator() {
        return children.iterator();
    }
    
    /**
     * Gets all attributes of this entry.
     * @return the attributes
     */
    public XAttribute[] attributes(){
        return attributes.toArray( new XAttribute[ attributes.size() ] );
    }
    
    /**
     * Gets all children of this entry.
     * @return the children
     */
    public XElement[] children(){
        return children.toArray( new XElement[ children.size() ] );
    }
    
    /**
     * Adds a new attribute to this entry.
     * @param attribute the new attribute
     * @return <code>this</code>
     */
    public XElement addAttribute( XAttribute attribute ){
        if( attribute == null )
            throw new NullPointerException( "attribute must not be null" );
        
        if( getAttribute( attribute.getName() ) != null ){
        	throw new IllegalArgumentException( "attribute '" + attribute.getName() + "' has already been added to this element" );
        }
        
        attributes.add( attribute );
        return this;
    }
    
    /**
     * Removes the attribute with name <code>name</code> from this {@link XElement}.
     * @param name the name of the attribute to remove
     * @return the attribute that was removed, or <code>null</code> if not found
     */
    public XAttribute removeAttribute( String name ){
    	for( int i = 0, n = attributes.size(); i<n; i++ ){
    		if( attributes.get( i ).getName().equals( name ) ){
    			return attributes.remove( i );
    		}
    	}
    	return null;
    }
    
    /**
     * Removes the attribute <code>attribute</code> from this {@link XElement}.
     * @param attribute the attribute to remove
     * @return <code>true</code> if <code>attribute</code> was removed
     */
    public boolean removeAttribute( XAttribute attribute ){
    	return attributes.remove( attribute );
    }
    
    /**
     * Adds a new attribute to this entry.
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @return <code>this</code>
     */
    public XElement addByte( String name, byte value ){
        XAttribute attribute = new XAttribute( name );
        attribute.setByte( value );
        addAttribute( attribute );
        return this;
    }
    
    /**
     * Adds a new attribute to this entry.
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @return <code>this</code>
     */
    public XElement addShort( String name, short value ){
        XAttribute attribute = new XAttribute( name );
        attribute.setShort( value );
        addAttribute( attribute );
        return this;
    }
    
    /**
     * Adds a new attribute to this entry.
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @return <code>this</code>
     */
    public XElement addInt( String name, int value ){
        XAttribute attribute = new XAttribute( name );
        attribute.setInt( value );
        addAttribute( attribute );
        return this;
    }
    
    /**
     * Adds a new attribute to this entry.
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @return <code>this</code>
     */
    public XElement addLong( String name, long value ){
        XAttribute attribute = new XAttribute( name );
        attribute.setLong( value );
        addAttribute( attribute );
        return this;
    }
    
    /**
     * Adds a new attribute to this entry.
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @return <code>this</code>
     */
    public XElement addFloat( String name, float value ){
        XAttribute attribute = new XAttribute( name );
        attribute.setFloat( value );
        addAttribute( attribute );
        return this;
    }
    
    /**
     * Adds a new attribute to this entry.
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @return <code>this</code>
     */
    public XElement addDouble( String name, double value ){
        XAttribute attribute = new XAttribute( name );
        attribute.setDouble( value );
        addAttribute( attribute );
        return this;
    }
    
    /**
     * Adds a new attribute to this entry.
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @return <code>this</code>
     */
    public XElement addChar( String name, char value ){
        XAttribute attribute = new XAttribute( name );
        attribute.setChar( value );
        addAttribute( attribute );
        return this;
    }
    
    /**
     * Adds a new attribute to this entry.
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @return <code>this</code>
     */
    public XElement addString( String name, String value ){
        XAttribute attribute = new XAttribute( name );
        attribute.setString( value );
        addAttribute( attribute );
        return this;
    }
    
    /**
     * Adds a new attribute to this entry.
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @return <code>this</code>
     */
    public XElement addBoolean( String name, boolean value ){
        XAttribute attribute = new XAttribute( name );
        attribute.setBoolean( value );
        addAttribute( attribute );
        return this;
    }
    
    /**
     * Adds a new attribute to this entry.
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @return <code>this</code>
     */
    public XElement addByteArray( String name, byte[] value ){
        XAttribute attribute = new XAttribute( name );
        attribute.setByteArray( value );
        addAttribute( attribute );
        return this;
    }
    
    /**
     * Tells whether the attribute <code>name</code> exists.
     * @param name the name to search
     * @return <code>true</code> if such an attribute exists
     */
    public boolean attributeExists( String name ){
        return getAttribute( name ) != null;
    }
    
    /**
     * Searches an attribute with the given name.
     * @param name the name of the attribute
     * @return the attribute
     */
    public XAttribute getAttribute( String name ){
        for( XAttribute check : attributes ){
            if( check.getName().equals( name ))
                return check;
        }
        return null;
    }
    
    /**
     * Gets the value of an attribute.
     * @param name the name of the attribute
     * @return the value of the attribute
     * @throws XException if the attribute does not exist or if the value
     * is in the wrong format
     * @see #getAttribute(String)
     */
    public byte getByte( String name ){
        XAttribute attribute = getAttribute( name );
        if( attribute == null )
            throw new XException( "no attribute known with name: " + name );
        return attribute.getByte();
    }
    
    /**
     * Gets the value of an attribute.
     * @param name the name of the attribute
     * @return the value of the attribute
     * @throws XException if the attribute does not exist or if the value
     * is in the wrong format
     * @see #getAttribute(String)
     */
    public short getShort( String name ){
        XAttribute attribute = getAttribute( name );
        if( attribute == null )
            throw new XException( "no attribute known with name: " + name );
        return attribute.getShort();
    }
    
    /**
     * Gets the value of an attribute.
     * @param name the name of the attribute
     * @return the value of the attribute
     * @throws XException if the attribute does not exist or if the value
     * is in the wrong format
     * @see #getAttribute(String)
     */
    public int getInt( String name ){
        XAttribute attribute = getAttribute( name );
        if( attribute == null )
            throw new XException( "no attribute known with name: " + name );
        return attribute.getInt();
    }
    
    /**
     * Gets the value of an attribute.
     * @param name the name of the attribute
     * @return the value of the attribute
     * @throws XException if the attribute does not exist or if the value
     * is in the wrong format
     * @see #getAttribute(String)
     */
    public long getLong( String name ){
        XAttribute attribute = getAttribute( name );
        if( attribute == null )
            throw new XException( "no attribute known with name: " + name );
        return attribute.getLong();
    }
    
    /**
     * Gets the value of an attribute.
     * @param name the name of the attribute
     * @return the value of the attribute
     * @throws XException if the attribute does not exist or if the value
     * is in the wrong format
     * @see #getAttribute(String)
     */
    public float getFloat( String name ){
        XAttribute attribute = getAttribute( name );
        if( attribute == null )
            throw new XException( "no attribute known with name: " + name );
        return attribute.getFloat();
    }
    
    /**
     * Gets the value of an attribute.
     * @param name the name of the attribute
     * @return the value of the attribute
     * @throws XException if the attribute does not exist or if the value
     * is in the wrong format
     * @see #getAttribute(String)
     */
    public double getDouble( String name ){
        XAttribute attribute = getAttribute( name );
        if( attribute == null )
            throw new XException( "no attribute known with name: " + name );
        return attribute.getDouble();
    }
    
    /**
     * Gets the value of an attribute.
     * @param name the name of the attribute
     * @return the value of the attribute
     * @throws XException if the attribute does not exist or if the value
     * is in the wrong format
     * @see #getAttribute(String)
     */
    public char getChar( String name ){
        XAttribute attribute = getAttribute( name );
        if( attribute == null )
            throw new XException( "no attribute known with name: " + name );
        return attribute.getChar();
    }
    
    /**
     * Gets the value of an attribute.
     * @param name the name of the attribute
     * @return the value of the attribute
     * @throws XException if the attribute does not exist or if the value
     * is in the wrong format
     * @see #getAttribute(String)
     */
    public String getString( String name ){
        XAttribute attribute = getAttribute( name );
        if( attribute == null )
            throw new XException( "no attribute known with name: " + name );
        return attribute.getString();
    }
    
    /**
     * Gets the value of an attribute.
     * @param name the name of the attribute
     * @return the value of the attribute
     * @throws XException if the attribute does not exist or if the value
     * is in the wrong format
     * @see #getAttribute(String)
     */
    public boolean getBoolean( String name ){
        XAttribute attribute = getAttribute( name );
        if( attribute == null )
            throw new XException( "no attribute known with name: " + name );
        return attribute.getBoolean();
    }
    
    /**
     * Gets the value of an attribute.
     * @param name the name of the attribute
     * @return the value of the attribute
     * @throws XException if the attribute does not exist or if the value
     * is in the wrong format
     * @see #getAttribute(String)
     */
    public byte[] getByteArray( String name ){
        XAttribute attribute = getAttribute( name );
        if( attribute == null )
            throw new XException( "no attribute known with name: " + name );
        return attribute.getByteArray();
    }
    
    /**
     * Removes the child <code>element</code> from this {@link XElement}.
     * @param element the element to remove
     * @return <code>true</code> if <code>element</code> was removed, <code>false</code> otherwise
     */
    public boolean removeElement( XElement element ){
    	return children.remove( element );
    }
    
    /**
     * Adds a new element to this element.
     * @param element the new child
     */
    public void addElement( XElement element ){
        if( element == null )
            throw new NullPointerException( "element must not be null" );
        children.add( element );
    }
   
    /**
     * Creates and adds a new element.
     * @param name the name of the new element
     * @return the new element
     */
    public XElement addElement( String name ){
        XElement element = new XElement( name );
        addElement( element );
        return element;
    }
    
    /**
     * Gets the first element with the given name.
     * @param name the name of the element
     * @return the element or <code>null</code>
     */
    public XElement getElement( String name ){
        for( XElement element : children ){
            if( element.getName().equals( name ))
                return element;
        }
        
        return null;
    }
    
    /**
     * Gets the number of children this element has.
     * @return the number of children
     */
    public int getElementCount(){
        return children.size();
    }
    
    /**
     * Gets the index'th child of this element.
     * @param index the index of the child
     * @return the child
     */
    public XElement getElement( int index ){
        return children.get( index );
    }
    
    /**
     * Gets all children with a given name.
     * @param name the name each child must have
     * @return the array of children, might be empty
     */
    public XElement[] getElements( String name ){
        List<XElement> elements = new LinkedList<XElement>();
        for( XElement element : children ){
            if( element.getName().equals( name ))
                elements.add( element );
        }
        return elements.toArray( new XElement[ elements.size() ] );
    }
    
    /**
     * Searches all children which have one of the name <code>names</code>.
     * @param names the names to search
     * @return the array of children, ordered by their occurence within this element, might be empty
     */
    public XElement[] getElements( String... names ){
    	List<XElement> elements = new LinkedList<XElement>();
        for( XElement element : children ){
        	for( String name : names ){
        		if( element.getName().equals( name )){
        			elements.add( element );
        			break;
        		}
        	}
        }
        return elements.toArray( new XElement[ elements.size() ] );
    }
    
    @Override
    public void setString( String s ) {
        if( s.length() == 0 )
            s = "[]";
        else{
            if( Character.isWhitespace( s.charAt( 0 ) ) || Character.isWhitespace( s.charAt( s.length()-1 ) )){
                s = "[" + s + "]";
            }
            else if( s.charAt( 0 ) == '[' && s.charAt( s.length()-1 ) == ']' ){
                s = "[" + s + "]";
            }
        }
        
        super.setString( s );
    }
    
    @Override
    public String getString() {
        String s = super.getString();
        if( s.startsWith( "[" ) && s.endsWith( "]" ))
            return s.substring( 1, s.length()-1 );
        return s;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        try {
            XIO.write( this, builder );
        }
        catch( IOException e ) {
            throw new XException( e );
        }
        return builder.toString();
    }
}
