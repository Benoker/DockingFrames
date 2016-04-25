package bibliothek.test;

import java.lang.reflect.Constructor;

public class ReflectionAdapter<S, D> implements Adapter<S, D>{
	private Constructor<? extends D> constructor;
	
	public ReflectionAdapter( Class<S> s, Class<? extends D> d ){
		try {
			constructor = d.getConstructor( s );
		}
		catch( NoSuchMethodException e ) {
			throw new RuntimeException( e );
		}
	}
	
	public D adapt( S value ){
		try {
			return constructor.newInstance( value );
		}
		catch( Exception e ) {
			throw new RuntimeException( e );
		}
	}
}
