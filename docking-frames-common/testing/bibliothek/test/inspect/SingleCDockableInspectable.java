package bibliothek.test.inspect;

import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.test.Inspect;
import bibliothek.test.Inspectable;
import bibliothek.test.InspectionGraph;

public class SingleCDockableInspectable implements Inspectable{
	private SingleCDockable dockable;
	
	public SingleCDockableInspectable( SingleCDockable dockable ){
		this.dockable = dockable;
	}
	
	public Inspect inspect( InspectionGraph graph ){
		return new DefaultInspect( graph ){
			@Override
			public boolean update(){
				setName( dockable.intern().getTitleText() );
				setValue( dockable );
				
				put( "id", dockable.getUniqueId() );
				
				return true;
			}
		};
	}
}
