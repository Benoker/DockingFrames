package bibliothek.test.inspect;

import java.lang.reflect.Field;

import bibliothek.gui.dock.station.split.Node;
import bibliothek.gui.dock.station.split.SplitNode;
import bibliothek.test.Inspect;
import bibliothek.test.Inspectable;
import bibliothek.test.InspectionGraph;

public class SplitNodeInspectable implements Inspectable{
	private Node node;
	private Field placeholders;
	
	public SplitNodeInspectable( Node node ){
		this.node = node;
		try{
			placeholders = SplitNode.class.getDeclaredField( "placeholders" );
			placeholders.setAccessible( true );
		}
		catch( Exception e ){
			throw new IllegalStateException( e );
		}
	}
	
	public Inspect inspect( InspectionGraph graph ){
		return new DefaultInspect( graph ){
			@Override
			public boolean update(){
				try{
					setName( "Node" );
					setValue( node );
					
					put( "left", node.getLeft() );
					put( "right", node.getRight() );
					put( "placeholder-set", placeholders.get( node ));
					put( "placeholder-map", node.getPlaceholderMap());
					
					return true;
				}
				catch( Exception e ){
					throw new IllegalStateException( e );
				}
			}
		};
	}
}
