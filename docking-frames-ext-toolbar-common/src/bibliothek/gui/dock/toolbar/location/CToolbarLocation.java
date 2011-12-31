package bibliothek.gui.dock.toolbar.location;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupProperty;

/**
 * A location pointing to a toolbar that is part of a group of toolbars. 
 * @author Benjamin Sigg
 */
public class CToolbarLocation extends CLocation{
	private int column;
	private int line;
	private CLocation parent;
	
	/**
	 * Creates a new location.
	 * @param parent the location defining the group of toolbars
	 * @param column the column to which this location is pointing
	 * @param line the line in <code>column</code> to which this location is pointing
	 */
	public CToolbarLocation( CLocation parent, int column, int line ){
		if( parent == null ){
			throw new IllegalArgumentException( "parent must not be null" );
		}
		this.parent = parent;
		this.column = column;
		this.line = line;
	}

	/**
	 * Gets the location of an item of this toolbar.
	 * @param index the index of the item
	 * @return the new location
	 */
	public CToolbarItemLocation item( int index ){
		return new CToolbarItemLocation( this, index );
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
		ToolbarGroupProperty property = new ToolbarGroupProperty( column, line, null );
		property.setSuccessor( successor );
		return parent.findProperty( property );
	}

	@Override
	public CLocation aside(){
		return new CToolbarLocation( parent, column, line+1 );
	}
	
	@Override
	public String toString(){
		return String.valueOf( parent ) + " [column " + column + ", line " + line + "]";
	}
}
