package bibliothek.gui.dock.toolbar.location;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerProperty;
import bibliothek.gui.dock.toolbar.CToolbarArea;

/**
 * A {@link CLocation} pointing to a {@link CToolbarArea}.
 * @author Benjamin Sigg
 */
public class CToolbarAreaLocation extends CLocation{
	private CToolbarArea root;
	
	/**
	 * Creates a new location.
	 * @param root the area to which this location points
	 */
	public CToolbarAreaLocation( CToolbarArea root ){
		this.root = root;
	}
	
	/**
	 * Gets a location that points to a specific group of toolbars on
	 * a {@link CToolbarArea}.
	 * @param group the index of the group, a value of <code>-1</code> points to the 
	 * not yet existing group at the beginning of the area
	 * @return the location pointing to <code>group</code>
	 */
	public CToolbarGroupLocation group( int group ){
		return new CToolbarGroupLocation( this, group );
	}
	
	@Override
	public CLocation getParent(){
		return null;
	}

	@Override
	public String findRoot(){
		return root.getUniqueId();
	}

	@Override
	public ExtendedMode findMode(){
		return CToolbarMode.TOOLBAR;
	}

	@Override
	public DockableProperty findProperty( DockableProperty successor ){
		if( successor == null ){
			return new ToolbarContainerProperty( 0, null );
		}
		else{
			return successor;
		}
	}

	@Override
	public CLocation aside(){
		return this;
	}
	
	@Override
	public String toString(){
		return "[toolbar-area " + root.getUniqueId() + "]";
	}
}
