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
 * This most simple formatter just prints out the entire documentation tree onto the console.
 * @author Benjamin Sigg
 */
public class TextFormatter implements DocFormatter{
	private StringBuilder builder;
	
	@Override
	public void format( DocRoot root ){
		builder = new StringBuilder();
		build( root );
		System.out.println( builder );
	}
	
	private void build( DocRoot root ){
		for( DocPackage pack : root ){
			append( 0, "package: " + pack.getName() );
			for( DocClass clazz : pack ){
				append( 1, "class: " + clazz.getName() );
				
				for( DocProperty property : clazz ){
					append( 2, "property: " + property.getName() );
					append( 3, property.getDescription().getText() );
					append( 3, "type", property.getType() );
					append( 3, "path..." );
					
					DocPath path = property.getPath();
					while( path != null ){
						append( 4, "[" + path.getDescription().getText() + "]");
						
						for( DocNode node : path.getNodes() ){
							build( " -> ", node );
						}
						for( DocNode node : path.getUnordered() ){
							build( " -? ", node );
						}
						
						path = path.getParent();
					}
				}
			}
		}
	}
	
	private void build( String prefix,  DocNode node ){
		append( 4, prefix + node.getDescription().getTextOrEmpty() );
		build( "    name", node.getName() );
		String identifier = node.getIdentifier().getText();
		if( identifier != null ){
			append( 4, "    identifier", identifier );
		}
		for( DocKey key : node.getClasses() ){
			build( "    class", key );
		}
		for( DocKey key : node.getPseudoClasses() ){
			build( "    pseudo-class", key );
		}
		for( DocKey key : node.getProperties() ){
			build( "    property", key );
		}
	}
	
	private void build( String key, DocKey value ){
		if( value != null ){
			append( 4, key + "=" + value.getKey() + ", " + value.getDescription().getTextOrEmpty());
		}
	}
	
	private void append( int tabs, String key, Object value ){
		append( tabs, key + ": " + String.valueOf( value ));
	}
	
	private void append( int tabs, String line ){
		if( line != null ){
			indent( tabs );
			builder.append( line );
			builder.append( "\n" );
		}
	}
	
	private void indent( int tabs ){
		for( int i = 0; i < tabs; i++ ){
			builder.append( "  " );
		}
	}
}
