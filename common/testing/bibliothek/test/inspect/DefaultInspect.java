package bibliothek.test.inspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import bibliothek.test.Inspect;
import bibliothek.test.InspectionGraph;

public class DefaultInspect implements Inspect{
	private String name;
	private Object value;
	
	private List<String> order = new ArrayList<String>();
	private Map<String, Object> children = new HashMap<String, Object>();
	private Map<Object, Object> named = new IdentityHashMap<Object, Object>();
	
	protected final InspectionGraph graph;
	
	public DefaultInspect( InspectionGraph graph ){
		this.graph = graph;
	}
	
	public boolean update(){
		return false;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName( String name ){
		this.name = name;
	}
	
	public Object getValue(){
		return value;
	}
	
	public void setValue( Object value ){
		this.value = value;
	}
	
	public Object[] getChildren(){
		Object[] result = new Object[ order.size() ];
		for( int i = 0; i < result.length; i++ ){
			result[i] = named.get( children.get( order.get( i ) ) );
		}
		return result;
	}
	
	public void clear(){
		order.clear();
		children.clear();
		named.clear();
	}
	
	public void remove( String name ){
		order.remove( name );
		Object value = children.remove( name );
		if( value != null ){
			named.remove( value );
		}
	}
	
	public void put( String name, Object value ){
		put( name, name, value );
	}
	
	public void put( String name, String rename, Object value ){
		if( !children.containsKey( name )){
			order.add( name );
		}
		Object old = children.put( name, value );
		if( old != value ){
			named.remove( old );
			if( rename != null ){
				named.put( value, new NamedInspectable( rename, value, graph ));
			}
			else{
				named.put( value, value );
			}
		}
	}
}
