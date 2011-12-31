package bibliothek.gui.dock.toolbar.location;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.location.CLocationExpandStrategy;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarProperty;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupProperty;

/**
 * A {@link CLocationExpandStrategy} that handles properties related to toolbars.
 * @author Benjamin Sigg
 */
public class ToolbarExpandStrategy implements CLocationExpandStrategy {

	@Override
	public CLocation expand( CLocation location, DockableProperty property ){
		if( property instanceof ToolbarProperty ) {
			return expand( location, (ToolbarProperty) property );
		}
		if( property instanceof ToolbarGroupProperty ) {
			return expand( location, (ToolbarGroupProperty) property );
		}
		if( property instanceof ToolbarContainerProperty ) {
			return expand( location, (ToolbarContainerProperty) property );
		}
		return null;
	}

	/**
	 * Creates a new location by creating the child location of <code>location</code> using
	 * <code>property</code> for that step.
	 * @param location the location to expand
	 * @param property the property that is the source of the next location
	 * @return the new location or <code>null</code> if no conversion is possible
	 */
	protected CLocation expand( CLocation location, ToolbarProperty property ){
		return new CToolbarItemLocation( location, property.getIndex() );
	}

	/**
	 * Creates a new location by creating the child location of <code>location</code> using
	 * <code>property</code> for that step.
	 * @param location the location to expand
	 * @param property the property that is the source of the next location
	 * @return the new location or <code>null</code> if no conversion is possible
	 */
	protected CLocation expand( CLocation location, ToolbarGroupProperty property ){
		return new CToolbarLocation( location, property.getColumn(), property.getLine() );
	}

	/**
	 * Creates a new location by creating the child location of <code>location</code> using
	 * <code>property</code> for that step.
	 * @param location the location to expand
	 * @param property the property that is the source of the next location
	 * @return the new location or <code>null</code> if no conversion is possible
	 */
	protected CLocation expand( CLocation location, ToolbarContainerProperty property ){
		return new CToolbarGroupLocation( location, property.getIndex() );
	}
}
