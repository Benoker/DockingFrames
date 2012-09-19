package bibliothek.gui.dock.extension.css.intern;

import bibliothek.gui.dock.extension.css.CssSelector;

public class TestCssRules {
	public static TestCssScheme getNoAnimationScheme(){
		TestCssScheme scheme = new TestCssScheme();
		scheme.addRule( getColor( "white" ) );
		scheme.addRule( getColor( "black" ) );
		return scheme;
	}
	
	public static TestCssScheme getAnimatedColorScheme(){
		TestCssScheme scheme = new TestCssScheme();
		scheme.addRule( getAnimatedColor( "white" ) );
		scheme.addRule( getAnimatedColor( "black" ) );
		return scheme;
	}
	
	private static CssSelector selector( String identifier ){
		return DefaultCssSelector.selector().any().identifier( identifier ).build();
	}
	
	private static DefaultCssRule getColor( String color ){
		DefaultCssRule rule = new DefaultCssRule( selector( color ) );
		rule.setProperty( "color", color );
		return rule;
	}
	
	private static DefaultCssRule getAnimatedColor( String color ){
		DefaultCssRule rule = new DefaultCssRule( selector( color ) );
		rule.setProperty( "color", color );
		rule.setProperty( "color-animation", "linear" );
		return rule;
	}
}
