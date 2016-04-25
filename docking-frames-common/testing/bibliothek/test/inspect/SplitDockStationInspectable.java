package bibliothek.test.inspect;

import bibliothek.gui.dock.SplitDockStation;
import bibliothek.test.Inspect;
import bibliothek.test.Inspectable;
import bibliothek.test.InspectionGraph;

public class SplitDockStationInspectable implements Inspectable{
	private SplitDockStation station;
	
	public SplitDockStationInspectable( SplitDockStation station ){
		this.station = station;
	}
	
	public Inspect inspect( InspectionGraph graph ){
		return new DefaultInspect( graph ){
			@Override
			public boolean update(){
				setName( "SplitDockStation" );
				setValue( station );
				put( "root", station.getRoot().getChild() );
					
				return true;
			}
		};
	}
}
