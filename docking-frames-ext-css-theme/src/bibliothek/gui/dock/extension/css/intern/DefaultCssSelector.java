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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bibliothek.gui.dock.extension.css.CssPath;
import bibliothek.gui.dock.extension.css.CssSelector;
import bibliothek.gui.dock.extension.css.CssSpecificity;

/**
 * The default implementation of a {@link CssSelector} just works like the standard CSS selector
 * is supposed to work.
 * @author Benjamin Sigg
 */
public class DefaultCssSelector implements CssSelector{
	private enum Specificity{
		STYLE, ID, ATTRIBUTE, ELEMENT
	}
	
	/**
	 * Creates a new {@link Builder} for creating a {@link DefaultCssSelector}.
	 * @return the new builder, not <code>null</code>
	 */
	public static Builder selector(){
		return new Builder();
	}
	
	/** the different patterns to match */
	private Step[] steps;
	
	private CssSpecificity specificity;
	
	private DefaultCssSelector( Step[] steps ){
		this.steps = steps;
		
		int countStyle = 0;
		int countId = 0;
		int countAttribute = 0;
		int countElement = 0;
		
		for( Step step : steps ){
			Specificity next = step.getSpecificity();
			if( next != null ){
				switch( next ){
					case ATTRIBUTE:
						countAttribute++;
						break;
					case ELEMENT:
						countElement++;
						break;
					case ID:
						countId++;
						break;
					case STYLE:
						countStyle++;
						break;
				}
			}
		}
		
		specificity = new CssSpecificity( countStyle, countId, countAttribute, countElement );
	}
	
	@Override
	public boolean matches( CssPath path ){
		return matches( path, -1, 0 );
	}
	
	private boolean matches( CssPath path, int pathOffset, int stepOffset ){
		int[] next = steps[ stepOffset ].matches( path, pathOffset );
		if( next.length == 0 ){
			return false;
		}
		if( stepOffset+1 == steps.length ){
			return next.length > 0;
		}
		for( int offset : next ){
			if( matches( path, offset, stepOffset+1 )){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public CssSpecificity getSpecificity(){
		return specificity;
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		for( int i = 0; i < steps.length; i++ ){
			if( i > 0 ){
				builder.append( " " );
			}
			builder.append( steps[i] );
		}
		return builder.toString();
	}
	
	@Override
	public int hashCode(){
		return Arrays.hashCode( steps );
	}

	@Override
	public boolean equals( Object obj ){
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		DefaultCssSelector other = (DefaultCssSelector) obj;
		if( !Arrays.equals( steps, other.steps ) )
			return false;
		return true;
	}

	private static interface Step{
		/**
		 * Tells whether this {@link Step} matches <code>item</code>.
		 * @param path the item to match
		 * @param offset the offset of the {@link CssPathNode} that was matched by the previous
		 * step. For the first step this is <code>-1</code>.
		 * @return the offset of all the {@link CssPathNode} that are matched
		 */
		public int[] matches( CssPath path, int offset );
		
		public Specificity getSpecificity();
	}
	
	private static class Any implements Step{
		@Override
		public int[] matches( CssPath path, int offset ){
			int size = path.getSize();
			if( offset+1 >= size ){
				return new int[]{};
			}
			int[] result = new int[ size - 1 - offset ];
			for( int i = 0; i < result.length; i++ ){
				result[i] = offset + i + 1;
			}
			return result;
		}
		
		@Override
		public Specificity getSpecificity(){
			return null;
		}
		
		@Override
		public int hashCode(){
			return 0;
		}
		
		@Override
		public boolean equals( Object obj ){
			if( obj == null ){
				return false;
			}
			return obj.getClass() == getClass();
		}
		
		@Override
		public String toString(){
			return "*";
		}
	}
	
	private static class Element implements Step{
		private String name;
		
		public Element( String name ){
			this.name = name;
		}
		
		@Override
		public int[] matches( CssPath path, int offset ){
			int[] temp = new int[ path.getSize() - offset ];
			int count = 0;
			
			for( int i = offset+1, n = path.getSize(); i<n; i++ ){
				if( path.getNode( i ).getName().equals( name )){
					temp[ count++ ] = i;
				}
			}
			
			int[] result = new int[ count ];
			System.arraycopy( temp, 0, result, 0, count );
			return result;
		}
		
		@Override
		public Specificity getSpecificity(){
			return Specificity.ELEMENT;
		}
		
		@Override
		public String toString(){
			return name;
		}
		
		@Override
		public int hashCode(){
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals( Object obj ){
			if( this == obj )
				return true;
			if( obj == null )
				return false;
			if( getClass() != obj.getClass() )
				return false;
			Element other = (Element) obj;
			if( name == null ) {
				if( other.name != null )
					return false;
			}
			else if( !name.equals( other.name ) )
				return false;
			return true;
		}

	}
	
	private static class Child implements Step{
		private String name;
		
		public Child( String name ){
			this.name = name;
		}
		
		@Override
		public int[] matches( CssPath path, int offset ){
			if( offset+1 < path.getSize() ){
				if( path.getNode( offset+1 ).getName().equals( name )){
					return new int[]{ offset+1 };
				}
			}
			return new int[]{};
		}
		
		@Override
		public Specificity getSpecificity(){
			return Specificity.ELEMENT;
		}
		
		@Override
		public String toString(){
			return " > " + name;
		}
		
		@Override
		public int hashCode(){
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals( Object obj ){
			if( this == obj )
				return true;
			if( obj == null )
				return false;
			if( getClass() != obj.getClass() )
				return false;
			Child other = (Child) obj;
			if( name == null ) {
				if( other.name != null )
					return false;
			}
			else if( !name.equals( other.name ) )
				return false;
			return true;
		}
	}
	
	private static class PseudoClass implements Step{
		private String name;
		
		public PseudoClass( String name ){
			this.name = name;
		}
		
		@Override
		public int[] matches( CssPath path, int offset ){
			if( path.getNode( offset ).hasPseudoClass( name )){
				return new int[]{ offset };
			}
			return new int[]{};
		}
		
		@Override
		public Specificity getSpecificity(){
			return Specificity.ATTRIBUTE;
		}
		
		@Override
		public String toString(){
			return ":" + name;
		}
		
		@Override
		public int hashCode(){
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals( Object obj ){
			if( this == obj )
				return true;
			if( obj == null )
				return false;
			if( getClass() != obj.getClass() )
				return false;
			PseudoClass other = (PseudoClass) obj;
			if( name == null ) {
				if( other.name != null )
					return false;
			}
			else if( !name.equals( other.name ) )
				return false;
			return true;
		}
	}
	
	private static class Attribute implements Step{
		private String key;
		private String value;
		
		public Attribute( String key, String value ){
			this.key = key;
			this.value = value;
		}
		
		@Override
		public int[] matches( CssPath path, int offset ){
			if( value == null ){
				if( path.getNode( offset ).getProperty( key ) != null ){
					return new int[]{ offset };
				}
			}
			else{
				if( value.equals( path.getNode( offset ).getProperty( key ))){
					return new int[]{ offset };
				}
			}
			return new int[]{};
		}
		
		@Override
		public Specificity getSpecificity(){
			return Specificity.ATTRIBUTE;
		}
		
		@Override
		public String toString(){
			if( value == null ){
				return "[" + key + "]";
			}
			else{
				return "[" + key + "=\"" + value + "\"]";
			}
		}
		
		@Override
		public int hashCode(){
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals( Object obj ){
			if( this == obj )
				return true;
			if( obj == null )
				return false;
			if( getClass() != obj.getClass() )
				return false;
			Attribute other = (Attribute) obj;
			if( key == null ) {
				if( other.key != null )
					return false;
			}
			else if( !key.equals( other.key ) )
				return false;
			if( value == null ) {
				if( other.value != null )
					return false;
			}
			else if( !value.equals( other.value ) )
				return false;
			return true;
		}
	}
	
	private static class ItemClass implements Step{
		private String name;
		
		public ItemClass( String name ){
			this.name = name;
		}
		
		@Override
		public int[] matches( CssPath path, int offset ){
			if( path.getNode( offset ).hasClass( name )){
				return new int[]{ offset };
			}
			return new int[]{};
		}
		
		@Override
		public Specificity getSpecificity(){
			return Specificity.ATTRIBUTE;
		}
		
		@Override
		public String toString(){
			return "." + name;
		}
		
		@Override
		public int hashCode(){
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals( Object obj ){
			if( this == obj )
				return true;
			if( obj == null )
				return false;
			if( getClass() != obj.getClass() )
				return false;
			ItemClass other = (ItemClass) obj;
			if( name == null ) {
				if( other.name != null )
					return false;
			}
			else if( !name.equals( other.name ) )
				return false;
			return true;
		}
	}
	
	private static class Identifier implements Step{
		private String name;
		
		public Identifier( String name ){
			this.name = name;
		}
		
		@Override
		public int[] matches( CssPath path, int offset ){
			if( name.equals( path.getNode( offset ).getIdentifier() )){
				return new int[]{ offset };
			}
			return new int[]{};
		}
		
		@Override
		public Specificity getSpecificity(){
			return Specificity.ID;
		}
		
		@Override
		public String toString(){
			return "#" + name;
		}
		
		@Override
		public int hashCode(){
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals( Object obj ){
			if( this == obj )
				return true;
			if( obj == null )
				return false;
			if( getClass() != obj.getClass() )
				return false;
			Identifier other = (Identifier) obj;
			if( name == null ) {
				if( other.name != null )
					return false;
			}
			else if( !name.equals( other.name ) )
				return false;
			return true;
		}
	}
	
	/**
	 * A builder for creating new {@link DefaultCssSelector}s.
	 * @author Benjamin Sigg
	 */
	public static class Builder{
		private List<Step> steps = new ArrayList<Step>();
		
		private Builder(){
			// ignore
		}
		
		private Builder push( Step step ){
			steps.add( step );
			return this;
		}
		
		/**
		 * Adds the any pattern "*" which matches to any element.
		 * @return <code>this</code>
		 */
		public Builder any(){
			return push( new Any() );
		}
		
		/**
		 * Adds a pattern to match an element <code>name</code>.
		 * @param name the name of the element
		 * @return <code>this</code>
		 */
		public Builder element( String name ){
			return push( new Element( name ));
		}
		
		/**
		 * Adds a pattern to match a child element of the current element. In 
		 * CSS this would be a pattern like "E &gt; F".
		 * @param name the name of the child element
		 * @return <code>this</code>
		 */
		public Builder child( String name ){
			return push( new Child( name ));
		}
		
		/**
		 * Adds a pattern to match a pseudo class of the current element. In
		 * CSS this would be a pattern like "E:hover".
		 * @param pseudoClass the name of the pseudo class
		 * @return <code>this</code>
		 */
		public Builder pseudo( String pseudoClass ){
			return push( new PseudoClass( pseudoClass ));
		}
		
		/**
		 * Adds a pattern to match the existence of an attribute of the current element. In
		 * CSS this would be a pattern like "E[foo]"
		 * @param name the name of the attribute
		 * @return <code>this</code>
		 */
		public Builder attribute( String name ){
			return push( new Attribute( name, null ));
		}
		
		/**
		 * Adds a pattern to match the value of an attribute of the current element. In
		 * CSS this would be a pattern like "E[foo=bar]"
		 * @param name the name of the attribute
		 * @param value the value of the attribute
		 * @return <code>this</code>
		 */
		public Builder attribute( String name, String value ){
			return push( new Attribute( name, value ));
		}
		
		/**
		 * Adds a pattern to match the class of the current element. In CSS
		 * this would be a pattern like "E.warning".
		 * @param className the name of the class
		 * @return <code>this</code>
		 */
		public Builder clazz( String className ){
			return push( new ItemClass( className ));
		}
		
		/**
		 * Adds a pattern to match the identifier of the current element. In
		 * CSS this would be a pattern like "E#id".
		 * @param name the name of the identifier
		 * @return <code>this</code>
		 */
		public Builder identifier( String name ){
			return push( new Identifier( name ));
		}
		
		/**
		 * Creates a new {@link DefaultCssSelector} using the configuration that
		 * has been made on this {@link Builder}.
		 * @return the new selector
		 */
		public DefaultCssSelector build(){
			return new DefaultCssSelector( steps.toArray( new Step[ steps.size() ] ) );
		}
	}
}
