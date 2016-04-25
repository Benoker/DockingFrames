package bibliothek.gui.dock.station.split;

import java.awt.Dimension;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.layout.location.AsideAnswer;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.util.Path;

/**
 * A placeholder is a set of {@link Path}-keys, each key stands for a 
 * {@link Dockable} that is currently not visible. Placeholders are not
 * visible to the user (they have no graphical representation), they
 * are only used to place {@link Dockable}s at their former position.
 * @author Benjamin Sigg
 */
public class Placeholder extends SplitNode {
	
	/**
	 * Creates a new placeholder
	 * @param access access to the {@link SplitDockStation}
	 * @param id the unique id of this placeholder
	 */
	public Placeholder( SplitDockAccess access, long id ){
		super( access, id );
	}
	
	@Override
	public void evolve( SplitDockTree<Dockable>.Key key, boolean checkValidity, Map<Leaf, Dockable> linksToSet ){
		setPlaceholders( key.getTree().getPlaceholders( key ) );
	}

	@Override
	public int getChildLocation( SplitNode child ){
		return -1;
	}

	@Override
	public Node getDividerNode( int x, int y ){
		return null;
	}

	@Override
	public Leaf getLeaf( Dockable dockable ){
		return null;
	}

	@Override
	public Dimension getMinimumSize(){
		return null;
	}
	
	@Override
	public Dimension getPreferredSize(){
		return null;
	}

	@Override
	public PutInfo getPut( int x, int y, double factorW, double factorH, Dockable drop ){
		return null;
	}

	@Override
	public boolean aside( AsideRequest request ){
		if( request.getPlaceholder() != null ){	
			addPlaceholder( request.getPlaceholder() );
			AsideAnswer answer = request.forward( getStation().getCombiner(), getPlaceholderMap() );
	    	if( answer.isCanceled() ){
	    		return false;
	    	}
	    	setPlaceholderMap( answer.getLayout() );
		}
    	return true;
	}
	
	@Override
	public boolean aside( SplitDockPathProperty property, int index, AsideRequest request ){
		if( request.getPlaceholder() != null ){
			if( index < property.size() ){
				Placeholder placeholder = createPlaceholder( property.getLeafId() );
				split( property, index, placeholder );
				placeholder.aside( request );
			}
			else{
				aside( request );
			}
		}
		return true;
	}
	
	@Override
	public boolean insert( SplitDockPlaceholderProperty property, Dockable dockable ){
		Path placeholder = property.getPlaceholder();
		if( hasPlaceholder( placeholder )){
			return replace( placeholder, dockable );
		}
		return false;
	}
	
	@Override
	public boolean insert( SplitDockPathProperty property, int depth, Dockable dockable ){
		if( property.getLeafId() == getId() ){
			return replace( null, dockable );
		}
		else if( depth < property.size() ){
			Leaf leaf = create( dockable, property.getLeafId() );
			if( leaf == null ){
				return false;
			}
			split( property, depth, leaf );
			leaf.setDockable( dockable, null );
			return true;
		}
		else{
			return false;
		}
	}
	
	private boolean replace( Path placeholder, Dockable dockable ){
		// replace this placeholder with a leaf
		Leaf leaf = create( dockable, getId() );
		if( leaf == null )
			return false;
		ensureOnlyOnThisNode( placeholder );
		ensureOnlyOnThisNode( getAccess().getOwner().getPlaceholderStrategy().getPlaceholderFor( dockable ) );
		
		leaf.setPlaceholders( getPlaceholders() );
		replace( leaf );
		leaf.setPlaceholderMap( getPlaceholderMap() );
		leaf.setDockable( dockable, null );
		return true;	
	}
	
	private void ensureOnlyOnThisNode( Path placeholder ){
		if( placeholder != null ){
			getAccess().getPlaceholderSet().set( this, placeholder, this );
		}
	}

	@Override
	public boolean isInOverrideZone( int x, int y, double factorW, double factorH ){
		return false;
	}

	@Override
	public void setChild( SplitNode child, int location ){
		throw new IllegalArgumentException();
	}
	
	@Override
	public int getMaxChildrenCount(){
		return 0;
	}
	
	@Override
	public SplitNode getChild( int location ){
		return null;
	}

	@Override
	public <N> N submit( SplitTreeFactory<N> factory ){
		return factory.placeholder( getId(), getPlaceholders(), getPlaceholderMap() );
	}
	
	@Override
	public boolean isVisible(){
		return false;
	}
	
	@Override
	public SplitNode getVisible(){
		return null;
	}
	
	@Override
	public boolean isOfUse(){
		if( !getAccess().isTreeAutoCleanupEnabled() ){
			return true;
		}
		return hasPlaceholders();
	}

	@Override
	public void visit( SplitNodeVisitor visitor ){
		visitor.handlePlaceholder( this );
	}

	@Override
	public void toString( int tabs, StringBuilder out ){
		out.append( "Placeholder: " );
		boolean first = true;
		for( Path key : getPlaceholders() ){
			if( first ){
				first = false;
			}
			else{
				out.append( ", " );
			}
			out.append( key );
		}
	}
}
