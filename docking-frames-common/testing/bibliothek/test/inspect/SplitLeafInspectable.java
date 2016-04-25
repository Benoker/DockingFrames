package bibliothek.test.inspect;

import java.lang.reflect.Field;

import bibliothek.gui.dock.station.split.Leaf;
import bibliothek.gui.dock.station.split.SplitNode;
import bibliothek.test.Inspect;
import bibliothek.test.Inspectable;
import bibliothek.test.InspectionGraph;

public class SplitLeafInspectable implements Inspectable{
	private Leaf leaf;
	private Field placeholders;
	
	public SplitLeafInspectable( Leaf leaf ){
		this.leaf = leaf;
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
					setName( "Leaf" );
					setValue( leaf );
					
					put( "dockable", leaf.getDockable() );
					put( "placeholder-set", placeholders.get( leaf ) );
					put( "placeholder-map", leaf.getPlaceholderMap() );
					
					return true;
				}
				catch( Exception e ){
					throw new IllegalStateException( e );
				}
			}
		};
	}
}
