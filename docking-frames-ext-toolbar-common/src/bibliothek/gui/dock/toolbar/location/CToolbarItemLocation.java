package bibliothek.gui.dock.toolbar.location;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarProperty;

/**
 * A location pointing to one item of a toolbar.
 * @author Benjamin Sigg
 */
public class CToolbarItemLocation extends CLocation{
	private CLocation parent;
	private int index;
	
	/**
	 * Creates a new location.
	 * @param parent a pointer to the toolbar
	 * @param index the index of the item to which this location points
	 */
	public CToolbarItemLocation( CLocation parent, int index ){
		if( parent == null ){
			throw new IllegalArgumentException( "parent must not be null" );
		}
		this.parent = parent;
		this.index = index;
	}

	@Override
	public CLocation getParent(){
		return parent;
	}

	@Override
	public String findRoot(){
		return parent.findRoot();
	}

	@Override
	public ExtendedMode findMode(){
		return parent.findMode();
	}

	@Override
	public DockableProperty findProperty( DockableProperty successor ){
		ToolbarProperty property = new ToolbarProperty( index, null );
		property.setSuccessor( successor );
		return parent.findProperty( property );
	}

	@Override
	public CLocation aside(){
		return new CToolbarItemLocation( parent, index+1 );
	}
}
