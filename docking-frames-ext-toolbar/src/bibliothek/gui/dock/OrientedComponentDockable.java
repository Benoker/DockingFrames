package bibliothek.gui.dock;

import java.awt.Component;

import javax.swing.Icon;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.station.OrientedDockStation;
import bibliothek.gui.dock.station.OrientingDockStationListener;

/**
 * This kind of {@link ComponentDockable} is useful for components with an
 * orientation. It allows to set automatically the orientation of the component
 * with regards to the orientation of its parent {@link DockStation}, if this
 * parent is an {@link OrientedDockStation}.
 * 
 * @author Herve Guillaume
 * 
 */
public abstract class OrientedComponentDockable extends ComponentDockable implements OrientingDockStationListener {

	public OrientedComponentDockable(){
		super();
	}

	public OrientedComponentDockable( Component component, Icon icon ){
		super( component, icon );
	}

	public OrientedComponentDockable( Component component, String title, Icon icon ){
		super( component, title, icon );
	}

	public OrientedComponentDockable( Component component, String title ){
		super( component, title );
	}

	public OrientedComponentDockable( Component component ){
		super( component );
	}

	public OrientedComponentDockable( Icon icon ){
		super( icon );
	}

	public OrientedComponentDockable( String title ){
		super( title );
	}

	@Override
	public void setDockParent( DockStation station ){
		super.setDockParent( station );
		if( station instanceof OrientedDockStation ) {
			final OrientedDockStation orientedStation = (OrientedDockStation) station;
			orientedStation.addOrientingDockStationListener( this );
		}
	}

}
