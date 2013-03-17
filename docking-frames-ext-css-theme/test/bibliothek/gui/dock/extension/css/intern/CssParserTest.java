package bibliothek.gui.dock.extension.css.intern;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;

import bibliothek.gui.dock.extension.css.CssPropertyKey;
import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssSelector;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.CssDeclarationValue;
import bibliothek.gui.dock.extension.css.transition.TransitionalCssProperty;

public class CssParserTest {
	private CssParser parser;
	
	@Before
	public void setup(){
		parser = new CssParser();
	}
	
	private CssType<String> string(){
		return new CssType<String>(){
			@Override
			public String convert( CssDeclarationValue value ){
				return value.getValue();
			}

			@Override
			public TransitionalCssProperty<String> createTransition(){
				return null;
			}
		};
	}
	
	@Test
	public void simpleRule() throws IOException{
		List<CssRule> rules = parser.parse( "a {x: y}" );
		assertEquals( 1, rules.size() );
		
		CssRule rule = rules.get( 0 );
		CssSelector selector = rule.getSelector();
		
		assertEquals( DefaultCssSelector.selector().element( "a" ).build(), selector );
		assertEquals( "y", rule.getContent().getProperty( string(), key("x") ) );
	}
	
	@Test
	public void identifier() throws IOException{
		List<CssRule> rules = parser.parse( "a#id {x: y}" );
		assertEquals( 1, rules.size() );
		
		CssRule rule = rules.get( 0 );
		CssSelector selector = rule.getSelector();
		
		assertEquals( DefaultCssSelector.selector().element( "a" ).identifier( "id" ).build(), selector );
		assertEquals( "y", rule.getContent().getProperty( string(), key( "x" ) ) );		
	}
	
	@Test
	public void clazz() throws IOException{
		List<CssRule> rules = parser.parse( "a.id {x: y}" );
		assertEquals( 1, rules.size() );
		
		CssRule rule = rules.get( 0 );
		CssSelector selector = rule.getSelector();
		
		assertEquals( DefaultCssSelector.selector().element( "a" ).clazz( "id" ).build(), selector );
		assertEquals( "y", rule.getContent().getProperty( string(), key( "x" ) ) );
	}
	
	@Test
	public void pseudoClass() throws IOException{
		List<CssRule> rules = parser.parse( "a:id {x: y}" );
		assertEquals( 1, rules.size() );
		
		CssRule rule = rules.get( 0 );
		CssSelector selector = rule.getSelector();
		
		assertEquals( DefaultCssSelector.selector().element( "a" ).pseudo( "id" ).build(), selector );
		assertEquals( "y", rule.getContent().getProperty( string(), key( "x" ) ) );
	}
	
	@Test
	public void attributeExists() throws IOException{
		List<CssRule> rules = parser.parse( "a[b] {x: y}" );
		assertEquals( 1, rules.size() );
		
		CssRule rule = rules.get( 0 );
		CssSelector selector = rule.getSelector();
		
		assertEquals( DefaultCssSelector.selector().element( "a" ).attribute( "b" ).build(), selector );
		assertEquals( "y", rule.getContent().getProperty( string(), key( "x" ) ) );
	}
	
	@Test
	public void attributeEquals() throws IOException{
		List<CssRule> rules = parser.parse( "a[b='c'] {x: y}" );
		assertEquals( 1, rules.size() );
		
		CssRule rule = rules.get( 0 );
		CssSelector selector = rule.getSelector();
		
		assertEquals( DefaultCssSelector.selector().element( "a" ).attribute( "b", "c" ).build(), selector );
		assertEquals( "y", rule.getContent().getProperty( string(), key( "x" ) ) );
	}
	
	@Test
	public void complexCase() throws IOException{
		List<CssRule> rules = parser.parse( "* . something : hover > a [ b = 'c' ] d # bla {x: y}" );
		assertEquals( 1, rules.size() );
		
		CssRule rule = rules.get( 0 );
		CssSelector selector = rule.getSelector();
		
		assertEquals( DefaultCssSelector.selector()
				.any()
				.clazz( "something" )
				.pseudo( "hover" )
				.child( "a" )
				.attribute( "b", "c" )
				.element( "d" )
				.identifier( "bla" )
				.build(), selector );
		assertEquals( "y", rule.getContent().getProperty( string(), key( "x" ) ) );
	}
	
	@Test
	public void multiSelector() throws IOException{
		List<CssRule> rules = parser.parse( "a[b='bi,ba'], d {x: y}" );
		assertEquals( 2, rules.size() );
		
		assertEquals( DefaultCssSelector.selector().element( "a" ).attribute( "b", "bi,ba" ).build(), rules.get( 0 ).getSelector() );
		assertEquals( DefaultCssSelector.selector().element( "d" ).build(), rules.get( 1 ).getSelector() );
	}
	
	@Test
	public void multiRule() throws IOException{
		List<CssRule> rules = parser.parse( "a {a:b; c : d ;e:'f'}" );
		assertEquals( 1, rules.size() );
		
		CssRule rule = rules.get( 0 );
		CssSelector selector = rule.getSelector();
		
		assertEquals( DefaultCssSelector.selector().element( "a" ).build(), selector );
		assertEquals( "b", rule.getContent().getProperty( string(), key( "a" ) ) );
		assertEquals( "d", rule.getContent().getProperty( string(), key( "c" ) ) );
		assertEquals( "f", rule.getContent().getProperty( string(), key( "e" ) ) );
	}
	
	private CssPropertyKey key( String key ){
		return new CssPropertyKey( key );
	}
}
