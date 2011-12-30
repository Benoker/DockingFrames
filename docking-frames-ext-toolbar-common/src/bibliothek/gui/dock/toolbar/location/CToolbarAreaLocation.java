package bibliothek.gui.dock.toolbar.location;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerProperty;
import bibliothek.gui.dock.toolbar.CToolbarArea;
import bibliothek.util.Todo;

/**
 * A {@link CLocation} pointing to a {@link CToolbarArea}.
 * @author Benjamin Sigg
 */
public class CToolbarAreaLocation extends CLocation{
	private String root;
	
	/**
	 * Creates a new location.
	 * @param root the name of the station to which this location points
	 */
	public CToolbarAreaLocation( String root ){
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
		return root;
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
	@Todo
	public CLocation aside(){
		// TODO implement
		return this;
	}
	
}
