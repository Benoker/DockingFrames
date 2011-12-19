package bibliothek.gui.dock;

import java.awt.Component;

import javax.swing.Icon;

import bibliothek.gui.DockStation;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.station.OrientationObserver;
import bibliothek.gui.dock.station.OrientedDockStation;

/**
 * This kind of {@link ComponentDockable} is useful for components with an
 * orientation. It allows to set automatically the orientation of the component
 * with regards to the orientation of its parent {@link DockStation}, if this
 * parent is an {@link OrientedDockStation}.
 * 
 * @author Herve Guillaume
 * 
 */
public abstract class OrientedComponentDockable extends ComponentDockable {

	public OrientedComponentDockable(){
		super();
		init();
	}

	public OrientedComponentDockable( Component component, Icon icon ){
		super( component, icon );
		init();
	}

	public OrientedComponentDockable( Component component, String title, Icon icon ){
		super( component, title, icon );
		init();
	}

	public OrientedComponentDockable( Component component, String title ){
		super( component, title );
		init();
	}

	public OrientedComponentDockable( Component component ){
		super( component );
		init();
	}

	public OrientedComponentDockable( Icon icon ){
		super( icon );
		init();
	}

	public OrientedComponentDockable( String title ){
		super( title );
		init();
	}

	private void init(){
		new OrientationObserver( this ){
			@Override
			protected void orientationChanged( Orientation current ){
				if( current != null ){
					setOrientation( current );
				}
			}
		};
	}
	
	/**
	 * Sets the new {@link Orientation} of this dockable.
	 * @param orientation the new orientation, not <code>null</code>
	 */
	protected abstract void setOrientation( Orientation orientation );
}
