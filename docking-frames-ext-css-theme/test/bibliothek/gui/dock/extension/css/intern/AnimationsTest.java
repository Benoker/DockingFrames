package bibliothek.gui.dock.extension.css.intern;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.DefaultCssItem;
import bibliothek.gui.dock.extension.css.animation.ColorAnimationProperty;
import bibliothek.gui.dock.extension.css.intern.range.Range;
import bibliothek.gui.dock.extension.css.intern.range.RangeAnimationProperty;
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
		scheme.runAnimations( 5000 );
		Assert.assertFalse( Color.BLACK.equals( item.getColor() ) );
		Assert.assertFalse( Color.WHITE.equals( item.getColor() ) );
		scheme.runAnimations( 5050 );
		Assert.assertEquals( Color.WHITE, item.getColor() );
	}
	
	@Test
	public void overlappingAnimations(){
		TestCssScheme scheme = TestCssRules.getAnimatedColorScheme();
		TestItem item = new TestItem( scheme );
		item.addAnimatedColorProperty();
		item.toRed();
		Assert.assertNull( item.getColor() );
		scheme.add( item );
		Assert.assertEquals( Color.RED, item.getColor() );
		
		item.toGreen();
		scheme.runAnimations( 3000 );
		
		assertBetween( 150, 200, item.getColor().getRed() );
		assertBetween( 50, 150, item.getColor().getGreen() );
		Assert.assertEquals( 0, item.getColor().getBlue() );
		
		item.toBlue();
		scheme.runAnimations( 3000 );
		
		assertBetween( 50, 200, item.getColor().getRed() );
		assertBetween( 50, 200, item.getColor().getGreen() );
		assertBetween( 50, 150, item.getColor().getBlue() );
		
		scheme.runAnimations( 4000 );
		
		Assert.assertEquals( 0, item.getColor().getRed() );
		assertBetween( 50, 150, item.getColor().getGreen() );
		assertBetween( 100, 200, item.getColor().getBlue() );
		
		scheme.runAnimations( 3100 );
		
		Assert.assertEquals( Color.BLUE, item.getColor() );
	}
	
	@Test
	public void dependingProperties(){
		TestCssScheme scheme = TestCssRules.getAnimatedRangeScheme();
		TestItem item = new TestItem( scheme );
		item.addAnimatedRangeProperty();
		item.to( "alpha" );
		Assert.assertNull( item.getRange() );
		scheme.add( item );
		Assert.assertEquals( new Range( "alpha", 0, 0 ), item.getRange() );
		
		item.to( "delta" );
		scheme.runAnimations( 5000 );
		assertBetween( 450, 550, item.getRange().getMin() );
		assertBetween( 450, 550, item.getRange().getMax() );
		
		scheme.runAnimations( 5050 );
		Assert.assertEquals( new Range( "delta", 100, 100 ), item.getRange() );
	}
	
	private void assertBetween( int min, int max, int actual ){
		Assert.assertTrue( min + " <= " + actual,  min <= actual );
		Assert.assertTrue( max + " >= " + actual, max >= actual );
	}
	
	private class TestItem extends DefaultCssItem{
		private Map<String, Object> values = new HashMap<String, Object>();
		private CssScheme scheme;
		
		public TestItem( CssScheme scheme ){
			super( new DefaultCssPath( new DefaultCssNode( "base" ) ) );
			this.scheme = scheme;
		} 
		
		public void toWhite(){
			to( "white" );
		}
		
		public void toBlack(){
			to( "black" );
		}
		
		public void toRed(){
			to( "red" );
		}
		
		public void toGreen(){
			to( "green" );
		}
		
		public void toBlue(){
			to( "blue" );
		}
		
		public void to( String color ){
			DefaultCssNode node =  new DefaultCssNode( "base" );
			node.setIdentifier( color );
			setPath( new DefaultCssPath( node ) );
		}
		
		public Color getColor(){
			return (Color)values.get( "color" );
		}
		
		public Range getRange(){
			return (Range)values.get( "range" );
		}
		
		public void addAnimatedColorProperty(){
			putProperty( "color", new ColorAnimationProperty( scheme, this ){
				@Override
				public void set( Color value ){
					System.out.println( "color: " + value.getRed() + " " + value.getGreen() + " " + value.getBlue());
					values.put( "color", value );
				}
			});
		}
		
		public void addAnimatedRangeProperty(){
			putProperty( "range", new RangeAnimationProperty( scheme, this ){
				@Override
				public void set( Range value ){
					System.out.println( "range: " + value.getMin() + " " + value.getMax() );
					values.put( "range", value );
				}
			} );
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
