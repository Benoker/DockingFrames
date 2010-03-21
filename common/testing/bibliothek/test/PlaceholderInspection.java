package bibliothek.test;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.test.inspect.CControlInspectable;
import bibliothek.test.inspect.SingleCDockableInspectable;

public class PlaceholderInspection extends InspectionGraph{
	public PlaceholderInspection(){
		putInspectableAdapter( CControl.class, CControlInspectable.class );
		putInspectableAdapter( SingleCDockable.class, SingleCDockableInspectable.class );
	}
}
