package bibliothek.test.inspect;

import java.lang.reflect.Field;

import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.test.Inspect;
import bibliothek.test.Inspectable;
import bibliothek.test.InspectionGraph;

public class FlapDockStationInspectable implements Inspectable{
	private FlapDockStation station;
	private Field handles;
	
	public FlapDockStationInspectable( FlapDockStation station ){
		this.station = station;
		
		try {
			handles = FlapDockStation.class.getDeclaredField( "handles" );
			handles.setAccessible( true );
		}
		catch( Exception e ) {
			throw new IllegalStateException( e );
		}	
	}
	
	public Inspect inspect( InspectionGraph graph ){
		return new DefaultInspect( graph ){
			private int size = 0;
			
			@Override
			public boolean update(){
				try{
					PlaceholderList<?,?,?> list = (PlaceholderList<?,?,?>)handles.get( station );
					
					setName( "FlapDockStation" );
					setValue( station );
					
					int count = 0;
					for( PlaceholderList<?,?,?>.Item item : list.list() ){
						put( String.valueOf( count++ ), item );
					}
					
					while( size > count ){
						remove( String.valueOf( --size ));
					}
					
					size = count;
					
					return true;
				}
				catch( Exception e ){
					throw new IllegalStateException( e );
				}
			}
		};
	}
}
