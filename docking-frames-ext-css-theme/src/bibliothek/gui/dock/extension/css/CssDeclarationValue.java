/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to call the method {@link CssType#convert(String)}, this class represents a simple {@link String}
 * as it can be found in the "value" part of a css-declaration. At the same time this class offers some
 * methods to split that string into multiple parts.
 * @author Benjamin Sigg
 */
public class CssDeclarationValue {
	/** the actual value found in the declaration */
	private String value;
	
	/** cache for the parts */
	private String[] parts;
	
	/**
	 * Creates a new argument
	 * @param value the actual value found in the declaration
	 */
	public CssDeclarationValue( String value ){
		this.value = value;
	}
	
	/**
	 * Gets the unmodified, original value.
	 * @return the unmodified value
	 */
	public String getValue(){
		return value;
	}
	
	/**
	 * Assuming that the value has only one part, gets that one part (or the first part if the assumption was incorrect).
	 * @return the first part of the value
	 */
	public String getSingleValue(){
		String[] values = getValues();
		return values[0];
	}
	
	/**
	 * Gets all the parts of this value, parts are split when finding a comma.
	 * @return all the parts
	 */
	public String[] getValues(){
		if( parts != null ){
			return parts;
		}
		
		List<String> items = new ArrayList<String>();
		StringBuilder item = new StringBuilder();
		
		boolean inString = false;
		boolean escape = false;
		boolean pending = true;
		
		for( int i = 0, n = value.length(); i<n; i++ ){
			pending = false;
			char c = value.charAt( i );
			
			if( escape ){
				item.append( c );
				escape = false;
			}
			else if( c == '\\'){
				escape = true;
			}
			else if( c == '"' ){
				inString = !inString;
			}
			else if( c == ',' ){
				if( inString ){
					item.append( c );
				}
				else{
					items.add( item.toString().trim() );
					item.setLength( 0 );
					pending = true;
				}
			}
			else{
				item.append( c );
			}
		}
		
		if( pending || item.length() > 0 ){
			items.add( item.toString().trim() );
		}
		
		parts = items.toArray( new String[ items.size() ] );
		return parts;
	}
	
	@Override
	public String toString(){
		return getValue();
	}
}
