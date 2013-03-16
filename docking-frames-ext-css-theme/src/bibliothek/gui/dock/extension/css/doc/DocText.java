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


/**
 * Represents a {@link CssDocText}.
 * @author Benjamin Sigg
 */
public class DocText {
	/** the root of the documentation */
	private DocRoot root;
	
	/** the actual description */
	private CssDocText description;

	/**
	 * Creates a new text. 
	 * @param root the root of the documentation
	 * @param description the actual description
	 */
	public DocText( DocRoot root, CssDocText description ){
		this.root = root;
		this.description = description;
	}
	
	/**
	 * Gets {@link #getText()}, but replaces a result of <code>null</code> with <code>""</code>.
	 * @return the text, or an empty string
	 */
	public String getTextOrEmpty(){
		String text = getText();
		if( text == null ){
			return "";
		}
		return text;
	}

	/**
	 * Gets the text that is actually described by this {@link DocText}. 
	 * @return the actual text
	 */
	public String getText(){
		String format;
		
		if( !description.id().isEmpty() ){
			format = root.getString( description.id() );
		}
		else{
			format = description.format();
		}
		
		String text;
		
		if( format == null || format.isEmpty() ){
			text = description.text();
			if( text.isEmpty() ){
				text = null;
			}
		}
		else{
			text = String.format( format, (Object[])description.arguments() );
		}
		return text;
	}
	
	@Override
	public String toString(){
		return getText();
	}
}
