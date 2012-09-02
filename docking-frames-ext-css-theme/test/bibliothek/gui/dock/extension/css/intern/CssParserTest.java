package bibliothek.gui.dock.extension.css.intern;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;

import bibliothek.gui.dock.extension.css.CssRule;
import bibliothek.gui.dock.extension.css.CssSelector;

public class CssParserTest {
	private CssParser parser;
	
	@Before
	public void setup(){
		parser = new CssParser();
	}
	
	@Test
	public void simpleRule() throws IOException{
		List<CssRule> rules = parser.parse( "a {x: y}" );
		assertEquals( 1, rules.size() );
		
		CssRule rule = rules.get( 0 );
		CssSelector selector = rule.getSelector();
		
		assertEquals( DefaultCssSelector.selector().element( "a" ).build(), selector );
		assertEquals( "y", rule.getProperty( "x" ) );
	}
	
	@Test
	public void identifier() throws IOException{
		List<CssRule> rules = parser.parse( "a#id {x: y}" );
		assertEquals( 1, rules.size() );
		
		CssRule rule = rules.get( 0 );
		CssSelector selector = rule.getSelector();
		
		assertEquals( DefaultCssSelector.selector().element( "a" ).identifier( "id" ).build(), selector );
		assertEquals( "y", rule.getProperty( "x" ) );		
	}
	
	@Test
	public void clazz() throws IOException{
		List<CssRule> rules = parser.parse( "a.id {x: y}" );
		assertEquals( 1, rules.size() );
		
		CssRule rule = rules.get( 0 );
		CssSelector selector = rule.getSelector();
		
		assertEquals( DefaultCssSelector.selector().element( "a" ).clazz( "id" ).build(), selector );
		assertEquals( "y", rule.getProperty( "x" ) );
	}
	
	@Test
	public void pseudoClass() throws IOException{
		List<CssRule> rules = parser.parse( "a:id {x: y}" );
		assertEquals( 1, rules.size() );
		
		CssRule rule = rules.get( 0 );
		CssSelector selector = rule.getSelector();
		
		assertEquals( DefaultCssSelector.selector().element( "a" ).pseudo( "id" ).build(), selector );
		assertEquals( "y", rule.getProperty( "x" ) );
	}
	
	@Test
	public void attributeExists() throws IOException{
		List<CssRule> rules = parser.parse( "a[b] {x: y}" );
		assertEquals( 1, rules.size() );
		
		CssRule rule = rules.get( 0 );
		CssSelector selector = rule.getSelector();
		
		assertEquals( DefaultCssSelector.selector().element( "a" ).attribute( "b" ).build(), selector );
		assertEquals( "y", rule.getProperty( "x" ) );
	}
	
	@Test
	public void attributeEquals() throws IOException{
		List<CssRule> rules = parser.parse( "a[b='c'] {x: y}" );
		assertEquals( 1, rules.size() );
		
		CssRule rule = rules.get( 0 );
		CssSelector selector = rule.getSelector();
		
		assertEquals( DefaultCssSelector.selector().element( "a" ).attribute( "b", "c" ).build(), selector );
		assertEquals( "y", rule.getProperty( "x" ) );
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
		assertEquals( "y", rule.getProperty( "x" ) );
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
		assertEquals( "b", rule.getProperty( "a" ) );
		assertEquals( "d", rule.getProperty( "c" ) );
		assertEquals( "f", rule.getProperty( "e" ) );
	}
}
