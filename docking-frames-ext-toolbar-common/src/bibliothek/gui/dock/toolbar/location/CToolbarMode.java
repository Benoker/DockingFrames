package bibliothek.gui.dock.toolbar.location;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.mode.CLocationMode;
import bibliothek.gui.dock.common.perspective.mode.LocationModePerspective;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.util.Todo;

/**
 * This {@link CLocationMode} describes the areas that are part of a toolbar.
 * @author Benjamin Sigg
 */
public class CToolbarMode extends ToolbarMode<CToolbarModeArea> implements CLocationMode{
	/**
	 * Creates a new mode
	 * @param control the control in whose realm this mode is used
	 */
	public CToolbarMode( CControl control ){
		super( control.getController() );
	}

	public CLocation getCLocation( Dockable dockable ){
		CToolbarModeArea area = get( dockable );
		if( area == null )
			return null;
			
		return area.getCLocation( dockable );
	}
	
	public CLocation getCLocation( Dockable dockable, Location location ){
		CToolbarModeArea area = get( location.getRoot() );
		if( area == null )
			return null;
			
		return area.getCLocation( dockable, location );
	}

	@Override
	public boolean isBasicMode(){
		return false;
	}

	@Override
	public boolean respectWorkingAreas( DockStation station ){
		return true;
	}

	@Override
	@Todo
	public LocationModePerspective createPerspective(){
		return null; // TODO
	}
}
