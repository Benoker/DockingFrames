package bibliothek.gui.dock.extension.css.intern.range;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.property.IntegerCssProperty;

public class DefaultRange extends SimpleRange implements Range{
	private IntegerCssProperty minProperty = new IntegerCssProperty(){
		@Override
		public void set( Integer value ){
			if( value != null ){
				setMin( value );
			}
		}
	};
	
	private IntegerCssProperty maxProperty = new IntegerCssProperty(){
		@Override
		public void set( Integer value ){
			if( value != null ){
				setMax( value );
			}
		}
	};
	
	public DefaultRange( String name ){
		super( name );
	}
	
	@Override
	public String[] getPropertyKeys(){
		return new String[]{"min", "max"};
	}
	@Override
	public CssProperty<?> getProperty( String key ){
		if( "min".equals( key )){
			return minProperty;
		}
		if( "max".equals( key )){
			return maxProperty;
		}
		return null;
	}
}
