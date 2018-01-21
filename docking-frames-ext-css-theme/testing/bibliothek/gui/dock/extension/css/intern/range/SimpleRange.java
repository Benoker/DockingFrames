package bibliothek.gui.dock.extension.css.intern.range;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainerListener;

public class SimpleRange implements Range{
	private String name;
	private int min;
	private int max;
	
	public SimpleRange( String name ){
		this.name = name;
	}
	
	public SimpleRange( String name, int min, int max ){
		this.name = name;
		this.min = min;
		this.max = max;
	}

	@Override
	public String getName(){
		return name;
	}
	
	public void setMin( int min ){
		this.min = min;
	}
	
	@Override
	public int getMin(){
		return min;
	}
	
	public void setMax( int max ){
		this.max = max;
	}
	
	@Override
	public int getMax(){
		return max;
	}
	
	@Override
	public String toString(){
		return getClass().getSimpleName() + ": " + name + " " + min + " " + max;
	}
	
	@Override
	public String[] getPropertyKeys(){
		return new String[]{};
	}
	
	@Override
	public CssProperty<?> getProperty( String key ){
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
		SimpleRange other = (SimpleRange) obj;
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
}
