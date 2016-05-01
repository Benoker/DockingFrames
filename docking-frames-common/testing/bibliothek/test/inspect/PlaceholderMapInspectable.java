package bibliothek.test.inspect;

import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderMap.Key;
import bibliothek.test.Inspect;
import bibliothek.test.Inspectable;
import bibliothek.test.InspectionGraph;

public class PlaceholderMapInspectable implements Inspectable{
	private PlaceholderMap map;
	
	public PlaceholderMapInspectable( PlaceholderMap map ){
		this.map = map;
	}
	
	public Inspect inspect( InspectionGraph graph ){
		return new DefaultInspect( graph ){
			private int size = 0;
			
			@Override
			public boolean update(){
				setName( "PlaceholderMap" );
				setValue( map );
				
				put( "strategy", map.getPlaceholderStrategy() );
				
				int count = 0;
				
				for( Key key : map.getPlaceholders() ){
					put( String.valueOf( count++ ), key.toString(), key );
				}
				
				while( size > count ){
					remove( String.valueOf( --size ) );
				}
				
				size = count;
				
				return true;
			}
		};
	}
}
