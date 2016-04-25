package bibliothek.test.inspect;

import java.lang.reflect.Field;

import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.test.Inspect;
import bibliothek.test.Inspectable;
import bibliothek.test.InspectionGraph;

public class StackDockStationInspectable implements Inspectable {
	private StackDockStation station;
	private Field handles;
	
	public StackDockStationInspectable( StackDockStation station ){
		this.station = station;
		
		try {
			handles = StackDockStation.class.getDeclaredField( "dockables" );
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
					
					setName( "StackDockStation" );
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
