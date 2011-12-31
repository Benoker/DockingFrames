package bibliothek.gui.dock.toolbar.location;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerProperty;

/**
 * This location points to a group of toolbars.
 * @author Benjamin Sigg
 */
public class CToolbarGroupLocation extends CLocation{
	private CLocation parent;
	private int group;
	
	/**
	 * Creates a new location.
	 * @param parent the location definied the root station, must not be <code>null</code>
	 * @param group the index of the group, at least 0
	 */
	public CToolbarGroupLocation( CLocation parent, int group ){
		if( parent == null ){
			throw new IllegalArgumentException( "parent must not be null" );
		}
		this.parent = parent;
		this.group = group;
	}
	
	/**
	 * Creates a new location pointing to one toolbar of this group of toolbars.
	 * @param column the column in which to find the toolbar, a value of <code>-1</code> is pointing to a new, 
	 * not yet existing column at the beginning of the group
	 * @param line the line in <code>column</code> where the toolbar is to be found, a value of <code>-1</code>
	 * is pointing to a new, not yet existing line at the beginning of the column
	 * @return the new location
	 */
	public CToolbarLocation toolbar( int column, int line ){
		return new CToolbarLocation( this, column, line );
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
		ToolbarContainerProperty location = new ToolbarContainerProperty( group, null );
		location.setSuccessor( successor );
		return parent.findProperty( location );
	}

	@Override
	public CLocation aside(){
		return new CToolbarGroupLocation( parent, group+1 );
	}
	
	@Override
	public String toString(){
		return String.valueOf( parent ) + " [group " + group + "]";
	}
}
