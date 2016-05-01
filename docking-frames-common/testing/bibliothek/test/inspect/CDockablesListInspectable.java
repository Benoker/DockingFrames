package bibliothek.test.inspect;

import bibliothek.gui.dock.common.CControl;
import bibliothek.test.Inspect;
import bibliothek.test.Inspectable;
import bibliothek.test.InspectionGraph;

public class CDockablesListInspectable implements Inspectable{
	private CControl control;
	
	public CDockablesListInspectable( CControl control ){
		this.control = control;
	}
	
	public Inspect inspect( InspectionGraph graph ){
		return new DefaultInspect( graph ) {
			private int size = 0;
			
			public boolean update(){
				setName( "Dockables" );
				setValue( control.getRegister() );
				
				int index = 0;
				int count = 0;
				for( int i = 0, n = control.getCDockableCount(); i<n; i++ ){
					put( String.valueOf( index++ ), null, control.getCDockable( i ) );
					count++;
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
