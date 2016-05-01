package bibliothek.test.inspect;

import bibliothek.test.Inspect;
import bibliothek.test.Inspectable;
import bibliothek.test.InspectionGraph;

public class NamedInspectable implements Inspectable{
	private String name;
	private Inspectable delegate;
	
	public NamedInspectable( String name, Object value, InspectionGraph graph ){
		this.name = name;
		this.delegate = graph.getInspectable( value );
	}
	
	@Override
	public String toString(){
		return name + ": " + delegate;
	}
	
	public Inspect inspect( final InspectionGraph graph ){
		return new Inspect() {
			private Inspect value = delegate.inspect( graph );
			
			public boolean update(){
				return value.update();
			}
			
			public Object getValue(){
				return value.getValue();
			}
			
			public String getName(){
				return name;
			}
			
			public Object[] getChildren(){
				return value.getChildren();
			}
		};
	}
}
