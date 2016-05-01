package bibliothek.test.inspect;

import bibliothek.gui.dock.common.CControl;
import bibliothek.test.Inspect;
import bibliothek.test.Inspectable;
import bibliothek.test.InspectionGraph;

public class CControlInspectable implements Inspectable{
	private CControl control;
	
	public CControlInspectable( CControl control ){
		this.control = control;
	}
	
	public Inspect inspect( InspectionGraph graph ){
		DefaultInspect result = new DefaultInspect( graph );
		result.setName( "CControl" );
		result.setValue( control );
				
		result.put( "dockables", new CDockablesListInspectable( control ));
		result.put( "stations", new CControlDockStationsInspectable( control ));
		return result;
	}
}
