package bibliothek.test.inspect;

import java.lang.reflect.Field;

import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderMap.Key;
import bibliothek.test.Inspect;
import bibliothek.test.Inspectable;
import bibliothek.test.InspectionGraph;

public class KeyInspectable implements Inspectable{
	private Key key;
	private PlaceholderMap map;
	
	public KeyInspectable( Key key ){
		this.key = key;
		try{
			Class<?> clazz = Class.forName( PlaceholderMap.class.getName() + "$PlaceholderKey" );
			Field this0 = clazz.getDeclaredField( "this$0" );
			this0.setAccessible( true );
			map = (PlaceholderMap)this0.get( key );
		}
		catch( Exception e ){
			throw new IllegalStateException( e );
		}
	}
	
	public Inspect inspect( InspectionGraph graph ){
		return new DefaultInspect( graph ){
			private int size = 0;
			
			@Override
			public boolean update(){
				setName( "Key" );
				setValue( key ); 
				
				String[] names = map.getKeys( key );
				
				int count = 0;
				for( String name : names ){
					Object value = map.get( key, name );
					put( String.valueOf( count++ ), name, value );
				}
				
				while( size > count ){
					remove( String.valueOf( --size ));
				}
				
				size = count;
				
				return true;
			}
		};
	}
}
