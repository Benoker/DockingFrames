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
package bibliothek.gui.dock.extension.css.intern;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssSelector;
import bibliothek.gui.dock.extension.css.CssDeclarationValue;

/**
 * The {@link CssParser} takes some text and creates {@link CssRule}s from
 * that text.
 * @author Benjamin Sigg
 */
public class CssParser {
	public List<CssRule> parse( String text ) throws IOException{
		return parse( new StringReader( text ) );
	}
	
	public List<CssRule> parse( Reader text ) throws IOException{
		Collector collector = new Collector();
		parse( new CommentReader( text ), collector );
		return collector.getRules();
	}
	
	private void parse( Reader text, Collector collector ) throws IOException{
		StringBuilder builder = new StringBuilder();
		
		int read;
		
		boolean inRule = false;
		boolean inString = false;
		boolean inCharacter = false;
		
		int line = 1;
		
		while( (read = text.read()) != -1 ){
			char c = (char)read;
			
			if( c == '\n'){
				line++;
			}
			
			boolean storeCharacter = true;
			
			switch( c ){
				case '{':
					if( !inString && !inCharacter ){
						if( inRule ){
							throw new IOException( "Line " + line + ": found { inside a rule" );
						}
						inRule = true;
						collector.selectorRead( line, builder.toString().trim() );
						builder.setLength( 0 );
						storeCharacter = false;
					}
					break;
				case '}':
					if( !inString && !inCharacter ){
						if( !inRule ){
							throw new IOException( "Line " + line + ": found } not ending a rule" );
						}
						inRule = false;
						parseProperty( line, builder.toString(), collector );
						builder.setLength( 0 );
						storeCharacter = false;
					}
					break;
				case ';':
					if( !inString && !inCharacter ){
						if( inRule ){
							parseProperty( line, builder.toString(), collector );
							builder.setLength( 0 );
							storeCharacter = false;
						}
					}
					break;
				case '\'':
					if( !inString ){
						inCharacter = !inCharacter;
					}
					break;
				case '"':
					if( !inCharacter ){
						inString = !inString;
					}
					break;
			}
			
			if( storeCharacter ){
				builder.append( c );
			}
		}
	}
	
	private void parseProperty( int line, String property, Collector collector ) throws IOException{
		property = property.trim();
		if( property.length() > 0 ){
			int assignment = property.indexOf( ':' );
			if( assignment >= 0 ){
				String key = property.substring( 0, assignment ).trim();
				String value = property.substring( assignment+1 ).trim();
				if( (value.startsWith( "'" ) && value.endsWith( "'" )) || (value.startsWith( "\"" ) && value.endsWith( "\"" ))){
					value = value.substring( 1, value.length()-1 );
				}
				collector.propertyRead( key, value );
			}
			else{
				throw new IOException( "Line " + line + ": cannot read property '" + property + "'" );
			}
		}
	}
	
	private CssSelector[] toSelectors( int line, String selector ) throws IOException{
		List<CssSelector> result = new ArrayList<CssSelector>();
		
		boolean inString = false;
		boolean inCharacter = false;
		boolean inAttribute = false;
		
		int offset = 0;
		
		for( int i = 0, n = selector.length(); i<n; i++ ){
			char c = selector.charAt( i );
			switch( c ){
				case '\'':
					if( !inString ){
						inCharacter = !inCharacter;
					}
					break;
				case '"':
					if( !inCharacter ){
						inString = !inString;
					}
					break;
				case '[':
					if( !inString && !inCharacter ){
						if( inAttribute ){
							throw new IOException( "Line " + line + ": found [ in attribute" );
						}
						inAttribute = true;
					}
					break;
				case ']':
					if( !inString && !inCharacter ){
						if( !inAttribute ){
							throw new IOException( "Line " + line + ": found ] outside attribute" );
						}
						inAttribute = false;
					}
					break;
				case ',':
					if( !inString && !inCharacter ){
						result.add( toSelector( line, selector.substring( offset, i ).trim() ));
						offset = i+1;
					}
					break;
			}
		}
		
		String remainder = selector.substring( offset ).trim();
		if( remainder.length() > 0 ){
			result.add( toSelector( line, remainder ) );
		}
		return result.toArray( new CssSelector[ result.size() ] );
	}
	
	private CssSelector toSelector( int line, String selector ){
		SelectorParser parser = new SelectorParser();
		
		for( int i = 0, n = selector.length(); i<n; i++ ){
			char c = selector.charAt( i );
			parser.push( line, c );
		}
		
		if( parser.string.length() > 0 ){
			parser.endCurrent( line );
		}
		
		return parser.builder.build();
	}
	
	private class SelectorParser{
		private DefaultCssSelector.Builder builder = DefaultCssSelector.selector();
		private StringBuilder string = new StringBuilder();
		
		private boolean inString = false;
		private boolean inCharacter = false;
		private boolean inAttribute = false;
		
		private boolean nextIsPseudoClass = false;
		private boolean nextIsClass = false;
		private boolean nextIsIdentifier = false;
		private boolean nextIsChild = false;
		private boolean nextIsSibling = false;

		private boolean first = true;
		
		private void endCurrent( int line ){
			String next = string.toString().trim();
			string.setLength( 0 );
			if( next.length() > 0 ){
				if( nextIsChild ){
					if( first ){
						builder.any();
						first = false;
					}
					if( !nextIsPseudoClass && !nextIsIdentifier && !nextIsClass ){
						builder.child( next );
					}
					else{
						builder.any();
					}
				}
				if( nextIsSibling ){
					throw new IllegalArgumentException( "Line " + line + ": siblings are not supported" );
				}
				if( nextIsPseudoClass ){
					if( first ){
						builder.any();
					}
					builder.pseudo( next );
				}
				if( nextIsClass ){
					if( first ){
						builder.any();
					}
					builder.clazz( next );
				}
				if( nextIsIdentifier ){
					if( first ){
						builder.any();
					}
					builder.identifier( next );
				}
				
				if( !nextIsPseudoClass && !nextIsClass && !nextIsSibling && !nextIsChild && !nextIsSibling && !nextIsIdentifier ){
					if( "*".equals( next )){
						builder.any();
					}
					else{
						builder.element( next );
					}
				}
				
				first = false;
				
				nextIsPseudoClass = false;
				nextIsIdentifier = false;
				nextIsChild = false;
				nextIsSibling = false;
				nextIsClass = false;
			}
		}
		
		public void push( int line, char c ){
			switch( c ){
				case '"':
					if( !inCharacter ){
						inString = !inString;
					}
					else{
						string.append( c );
					}
					break;
				case '\'':
					if( !inString ){
						inCharacter = !inCharacter;
					}
					else{
						string.append( c );
					}
					break;
				case '[':
					if( !inCharacter && !inString ){
						endCurrent( line );
						if( inAttribute ){
							throw new IllegalArgumentException( "Line " + line + ": found [ in attribute" ); 
						}
						inAttribute = true;
					}
					else{
						string.append( c );
					}
					break;
				case ']':
					if( !inCharacter && !inString ){
						if( !inAttribute ){
							throw new IllegalArgumentException( "Line " + line + ": found ] without attribute" );
						}
						inAttribute = false;
						int assignment = string.indexOf( "=" );
						if( assignment == -1 ){
							builder.attribute( string.toString().trim() );
						}
						else{
							String key = string.substring( 0, assignment ).trim();
							String value = string.substring( assignment+1 ).trim();
							builder.attribute( key, value );
						}
						string.setLength( 0 );
					}
					else{
						string.append( c );
					}
					break;
				case ':':
					if( !inCharacter && !inString && !inAttribute ){
						endCurrent( line );
						nextIsPseudoClass = true;
					}
					else{
						string.append( c );
					}
					break;
				case '#':
					if( !inCharacter && !inString && !inAttribute ){
						endCurrent( line );
						nextIsIdentifier = true;
					}
					else{
						string.append( c );
					}
					break;
				case '.':
					if( !inCharacter && !inString && !inAttribute ){
						endCurrent( line );
						nextIsClass = true;
					}
					else{
						string.append( c );
					}
					break;
				case '>':
					if( !inCharacter && !inString && !inAttribute ){
						endCurrent( line );
						nextIsChild = true;
					}
					else{
						string.append( c );
					}
					break;
				case '+':
					if( !inCharacter && !inString && !inAttribute ){
						endCurrent( line );
						nextIsSibling = true;
					}
					else{
						string.append( c );
					}
					break;
				default:
					if( !inCharacter && !inString && !inAttribute && Character.isWhitespace( c )){
						if( string.length() > 0 ){
							endCurrent( line );
						}
					}
					else{
						string.append( c );
					}
			}
		}
	}
	
	private class Collector{
		private List<CssRule> rules = new ArrayList<CssRule>();
		
		private DefaultCssRule[] currentRules = new DefaultCssRule[]{};
		
		public void selectorRead( int line, String selector ) throws IOException{
			CssSelector[] selectors = toSelectors( line, selector );
			if( currentRules.length != selectors.length ){
				currentRules = new DefaultCssRule[ selectors.length ];
			}
			for( int i = 0; i < selectors.length; i++ ){
				DefaultCssRule rule = new DefaultCssRule( selectors[i] );
				currentRules[i] = rule;
				rules.add( rule );
			}
		}
		
		public void propertyRead( String key, String value ){
			for( DefaultCssRule rule : currentRules ){
				if( "null".equals( value ) || value == null ){
					rule.setProperty( CssPropertyKey.parse( key ), null );
				}
				else{
					rule.setProperty( CssPropertyKey.parse( key ), new CssDeclarationValue( value ));
				}
			}
		}
		
		public List<CssRule> getRules(){
			return rules;
		}
	}
	
	private static class CommentReader extends Reader{
		private Reader reader;
		private boolean inComment = false;
		private int previous = -1;
		private int next = -1;
		
		public CommentReader( Reader reader ){
			this.reader = reader;
		}
		
		@Override
		public int read( char[] cbuf, int off, int len ) throws IOException{
			int start = off;
			
			while( off < len ){
				if( inComment ){
					int read = previous;
					previous = -1;
					if( read == -1 ){
						read = reader.read();
					}
					if( read == '*' ){
						read = reader.read();
						if( read == '/' ){
							inComment = false;
						}
						else{
							previous = read;
						}
					}
					if( read == -1 ){
						break;
					}
				}
				else if( previous != -1 ){
					cbuf[off++] = (char)previous;
					previous = -1;
				}
				else if( next != -1 ){
					cbuf[off++] = (char)next;
					next = -1;
				}
				else{
					int read = reader.read();
					if( read == '/' ){
						previous = read;
						read = reader.read();
						if( read == '*' ){
							inComment = true;
							previous = -1;
						}
						else{
							next = read;
						}
					}
					else{
						if( read == -1 ){
							break;
						}
						cbuf[off++] = (char)read;
					}
				}
			}
			
			if( start == off ){
				return -1;
			}
			else{
				return off - start;
			}
		}

		@Override
		public void close() throws IOException{
			reader.close();
		}
	}
}
