package bibliothek.gui.dock.extension.css.intern.range;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainer;
import bibliothek.gui.dock.extension.css.CssPropertyContainerListener;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.animation.AnimatedCssProperty;
import bibliothek.gui.dock.extension.css.animation.types.AbstractAnimatedCssProperty;
import bibliothek.gui.dock.extension.css.property.IntegerCssProperty;

public class Range implements CssPropertyContainer{
	public static CssType<Range> TYPE = new CssType<Range>(){
		@Override
		public AnimatedCssProperty<Range> createAnimation(){
			return new AnimatedRangedInteger();
		}
		
		@Override
		public Range convert( String value ){
			return new Range( value );
		}
	};
	
	private String name;
	private int min;
	private int max;
	
	private IntegerCssProperty minProperty = new IntegerCssProperty(){
		@Override
		public void set( Integer value ){
			if( value != null ){
				min = value;
			}
		}
	};
	
	private IntegerCssProperty maxProperty = new IntegerCssProperty(){
		@Override
		public void set( Integer value ){
			if( value != null ){
				max = value;
			}
		}
	};
	
	public Range( String name ){
		this.name = name;
	}
	
	public Range( String name, int min, int max ){
		this.name = name;
		this.min = min;
		this.max = max;
	}
	
	public String getName(){
		return name;
	}
	
	public int getMin(){
		return min;
	}
	
	public int getMax(){
		return max;
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
	
	@Override
	public void addPropertyContainerListener( CssPropertyContainerListener listener ){
		// ignore
	}
	
	@Override
	public void removePropertyContainerListener( CssPropertyContainerListener listener ){
		// ignore
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + max;
		result = prime * result + min;
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
		Range other = (Range) obj;
		if( max != other.max )
			return false;
		if( min != other.min )
			return false;
		if( name == null ) {
			if( other.name != null )
				return false;
		}
		else if( !name.equals( other.name ) )
			return false;
		return true;
	}



	private static class AnimatedRangedInteger extends AbstractAnimatedCssProperty<Range>{
		@Override
		protected void update(){
			Range source = getSource();
			Range target = getTarget();
			if( source == null && target == null ){
				getCallback().set( null );
			}
			else{
				String name;
				if( source == null ){
					name = target.getName();
				}
				else{
					name = source.getName();
				}
				
				int smin = 0;
				int smax = 0;
				int tmin = 0;
				int tmax = 0;
				
				if( source != null ){
					smin = source.getMin();
					smax = source.getMax();
				}
				if( target != null ){
					tmin = target.getMin();
					tmax = target.getMax();
				}
				
				double t = getTransition();
				
				int min = (int)( smin * (1-t) + tmin * t );
				int max = (int)( smax * (1-t) + tmax * t );
				getCallback().set( new Range( name, min, max ) );
			}
		}		
	}
}
