package bibliothek.test.inspect;

import bibliothek.test.Inspect;
import bibliothek.test.Inspectable;
import bibliothek.test.InspectionGraph;

public class ObjectInspectable implements Inspectable{
	private Object object;
	
	public ObjectInspectable( Object object ){
		this.object = object;
	}
	
	@Override
	public String toString(){
		return String.valueOf( object );
	}
	
	public Inspect inspect( InspectionGraph graph ){
		DefaultInspect inspect = new DefaultInspect( graph );
		inspect.setName( object.getClass().getName() );
		inspect.setValue( object );
		return inspect;
	}
}
