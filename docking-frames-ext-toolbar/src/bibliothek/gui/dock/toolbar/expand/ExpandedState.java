package bibliothek.gui.dock.toolbar.expand;

import bibliothek.gui.Dockable;

/**
 * Describes the state of a {@link Dockable} as seen by the
 * {@link ExpandableToolbarItemStrategyListener} and by
 * {@link ExpandableToolbarItem}.
 * 
 * @author Benjamin Sigg
 */
public enum ExpandedState{
	/** an expanded {@link Dockable} has the largest possible size */
	EXPANDED,
	/** a stretched {@link Dockable} has a medium size */
	STRETCHED,
	/** a shrunk {@link Dockable} has the smallest possible size */
	SHRUNK;
	
	/**
	 * Orders the {@link ExpandedState}s according to their size, and returns the
	 * <code>size</code>'th state.
	 * @param size the size of the state, where 0 is the smallest state.
	 * @return the state, not <code>null</code>
	 * @throws IllegalArgumentException if <code>size</code> is smaller than 0 or bigger
	 * than the number of available states
	 */
	public static ExpandedState getOrdered( int size ){
		switch( size ){
			case 0: return SHRUNK;
			case 1: return STRETCHED;
			case 2: return EXPANDED;
			default: throw new IllegalArgumentException( "size out of bounds: " + size );
		}
	}
	
	/**
	 * Gets the order of this state. The order tells how big this state is compared to the
	 * other states, 0 is the smallest state. This formula always holds true:
	 * <code>x == ExpandedState.getOrdered( x.getOrder() );</code>
	 * @return the order of this state
	 */
	public int getOrder(){
		switch( this ){
			case SHRUNK: return 0;
			case STRETCHED: return 1;
			case EXPANDED: return 2;
			default: throw new IllegalStateException( "never happens" ); 
		}
	}
	
	/**
	 * Gets the next smaller state.
	 * @return the next smaller state or <code>this</code> if there is no smaller state
	 */
	public ExpandedState smaller(){
		int order = getOrder()-1;
		if( order < 0 ){
			return this;
		}
		return getOrdered( order );
	}
	
	/**
	 * Gets the next larger state.
	 * @return the next larger state or <code>null</code> if there is no larger state
	 */
	public ExpandedState larger(){
		int order = getOrder()+1;
		if( order >= values().length ){
			return this;
		}
		return getOrdered( order );
	}
}
