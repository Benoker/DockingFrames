package bibliothek.gui.dock.extension.css.intern;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.DefaultCssItem;
import bibliothek.gui.dock.extension.css.animation.ColorAnimationProperty;
import bibliothek.gui.dock.extension.css.path.DefaultCssNode;
import bibliothek.gui.dock.extension.css.path.DefaultCssPath;
import bibliothek.gui.dock.extension.css.property.ColorCssProperty;

public class AnimationsTest {
	@Test
	public void rulesWithoutAnimationShouldApplyDirectly(){
		TestCssScheme scheme = TestCssRules.getNoAnimationScheme();
		TestItem item = new TestItem( scheme );
		item.addColorProperty();
		item.toBlack();
		Assert.assertNull( item.getColor() );
		scheme.add( item );
		Assert.assertEquals( Color.BLACK, item.getColor() );
		item.toWhite();
		Assert.assertEquals( Color.WHITE, item.getColor() );
	}
	
	@Test
	public void animateColorProperty(){
		TestCssScheme scheme = TestCssRules.getAnimatedColorScheme();
		TestItem item = new TestItem( scheme );
		item.addAnimatedColorProperty();
		item.toBlack();
		Assert.assertNull( item.getColor() );
		scheme.add( item );
		Assert.assertEquals( Color.BLACK, item.getColor() );
		item.toWhite();
		scheme.runAnimations( 500 );
		Assert.assertFalse( Color.BLACK.equals( item.getColor() ) );
		Assert.assertFalse( Color.WHITE.equals( item.getColor() ) );
		scheme.runAnimations( 500 );
		Assert.assertEquals( Color.WHITE, item.getColor() );
	}
	
	private class TestItem extends DefaultCssItem{
		private Map<String, Object> values = new HashMap<String, Object>();
		private CssScheme scheme;
		
		public TestItem( CssScheme scheme ){
			super( new DefaultCssPath( new DefaultCssNode( "base" ) ) );
			this.scheme = scheme;
		} 
		
		public void toWhite(){
			DefaultCssNode node =  new DefaultCssNode( "base" );
			node.setIdentifier( "white" );
			setPath( new DefaultCssPath( node ) );
		}
		
		public void toBlack(){
			DefaultCssNode node =  new DefaultCssNode( "base" );
			node.setIdentifier( "black" );
			setPath( new DefaultCssPath( node ) );
		}
		
		public Color getColor(){
			return (Color)values.get( "color" );
		}
		
		public void addAnimatedColorProperty(){
			putProperty( "color", new ColorAnimationProperty( scheme, this, "color" ){
				@Override
				public void set( Color value ){
					System.out.println( "color: " + value.getRed() + " " + value.getGreen() + " " + value.getBlue());
					values.put( "color", value );
				}
			});
		}
		
		public void addColorProperty(){
			putProperty( "color", new ColorCssProperty(){
				@Override
				public void set( Color value ){
					values.put( "color", value );
				}
			} );
		}
	}
}
