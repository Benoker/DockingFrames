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

import bibliothek.gui.dock.extension.css.theme.CssDockTitle;


/**
 * An application able to generate documentation of all the available css elements reading some annotations
 * directly from the class files.
 * @author Benjamin Sigg
 */
public class DocGenerator {
	public static void main( String[] args ){
		DocGenerator generator = new DocGenerator();
		generator.add( CssDockTitle.class );
		generator.format();
	}
	
	private DocRoot root = new DocRoot();
	
	private DocFormatter formatter = new TextFormatter();
	
	/**
	 * Adds an additional class to the set of classes that need documentation.
	 * @param classToDocument the new class
	 */
	public void add( Class<?> classToDocument ){
		root.add( classToDocument );
	}
	
	/**
	 * Sets the algorithm that should be used to format the documentation.
	 * @param formatter the formating algorithm
	 */
	public void setFormatter( DocFormatter formatter ){
		this.formatter = formatter;
	}
	
	/**
	 * Reads all the classes that need documentation, and calls the 
	 * {@link DocFormatter} with a new {@link DocRoot}.
	 */
	public void format(){
		formatter.format( root );
	}
}
