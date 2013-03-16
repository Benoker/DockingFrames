/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.doc;

import bibliothek.gui.dock.extension.css.CssPropertyContainer;

/**
 * Represents a single {@link CssDocProperty} that was found
 * in a {@link Class}.
 * @author Benjamin Sigg
 */
public class DocProperty {
	private DocClass clazz;
	private CssDocProperty property;
	private Class<?> type;
	private DocPath path;
	
	/**
	 * Creates a new property.
	 * @param clazz the class in which the property was found
	 * @param property the property which is represented by <code>this</code>
	 * @param onType the type of the annotated field
	 */
	public DocProperty( DocClass clazz, CssDocProperty property, Class<?> onType ){
		this.clazz = clazz;
		this.property = property;
		
		if( CssPropertyContainer.class.isAssignableFrom( property.type() )){
			type = property.type();
		}
		else if( CssPropertyContainer.class.isAssignableFrom( onType ) ){
			type = onType;
		}
	}
	
	/**
	 * Gets the annotation that is represented by <code>this</code>
	 * @return the annotation
	 */
	public CssDocProperty getAnnotation(){
		return property;
	}
	
	/**
	 * Gets the class in which <code>this</code> was declared.
	 * @return the owner
	 */
	public DocClass getClazz(){
		return clazz;
	}
	
	/**
	 * Gets the root documentation.
	 * @return the root documentation
	 */
	public DocRoot getRoot(){
		return clazz.getRoot();
	}
	
	/**
	 * Gets the name of this property.
	 * @return the name of the property
	 */
	public String getName(){
		return property.property().key();
	}
	
	/**
	 * Gets a description of this property.
	 * @return the description
	 */
	public DocText getDescription(){
		return new DocText( getRoot(), property.description() );
	}
	
	/**
	 * Gets the type of the property.
	 * @return the type, may be <code>null</code>
	 */
	public Class<?> getType(){
		return type;
	}
	
	/**
	 * Gets the path to <code>this</code>.
	 * @return the path
	 */
	public DocPath getPath(){
		if( path == null ){
			path = new DocPath( this, property.path() );
		}
		return path;
	}
}
