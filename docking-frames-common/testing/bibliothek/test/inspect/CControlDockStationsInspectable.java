package bibliothek.test.inspect;

import java.util.List;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CStation;
import bibliothek.test.Inspect;
import bibliothek.test.Inspectable;
import bibliothek.test.InspectionGraph;

public class CControlDockStationsInspectable implements Inspectable{
	private CControl control;
	
	public CControlDockStationsInspectable( CControl control ){
		this.control = control;
	}
	
	public Inspect inspect( InspectionGraph graph ){
		return new DefaultInspect( graph ) {
			private int size = 0;
			
			public boolean update(){
				setName( "Stations" );
				setValue( control.getRegister() );
				
				int count = 0;
				List<CStation<?>> stations = control.getStations();
				for( CStation<?> station : stations ){
					put( String.valueOf( count++ ), station.getUniqueId(), station.getStation() );
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
