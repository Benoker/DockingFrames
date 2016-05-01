package bibliothek.test.inspect;

import bibliothek.test.Inspect;
import bibliothek.test.Inspectable;
import bibliothek.test.InspectionGraph;

public class NullInspectable implements Inspectable{
	public static final NullInspectable INSTANCE = new NullInspectable();
	
	public Inspect inspect( InspectionGraph graph ){
		DefaultInspect inspect = new DefaultInspect( graph );
		inspect.setName( "null" );
		return inspect;
	}
}
