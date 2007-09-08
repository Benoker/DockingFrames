package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Component;
import java.awt.Point;

import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A DockTitle that shows only the buttons of the 
 * {@link bibliothek.gui.dock.action.DockAction}
 * @author Benjamin Sigg
 */
public class ActionDockTitle implements DockTitle {
	/**
	 * A factory creating instances of {@link ActionDockTitle}.
	 */
	public static final DockTitleFactory FACTORY = new DockTitleFactory(){
		public DockTitle createDockableTitle( Dockable dockable, DockTitleVersion version ){
			return new ActionDockTitle( dockable, version );
		}
		
		public <D extends Dockable & DockStation> DockTitle createStationTitle( D dockable, DockTitleVersion version ){
			return new ActionDockTitle( dockable, version );
		}
	};
	
	/** the owner */
	private Dockable dockable;
	/** the buttons */
	private ButtonPanel panel = new ButtonPanel();
	
	/** the creator of this title */
	private DockTitleVersion origin;
	
	/**
	 * Creates a new title
	 * @param dockable the owner
	 * @param origin the version of this title
	 */
	public ActionDockTitle( Dockable dockable, DockTitleVersion origin ){
		this.dockable = dockable;
		this.origin = origin;
		panel.setOpaque( false );
	}
	
	public void addMouseInputListener( MouseInputListener listener ){
		// ignore
	}

	public void bind(){
		panel.set( dockable, createSource( dockable ));
	}
	
	/**
	 * Creates the list of actions which should be displayed.
	 * @param dockable the element for which the actions are shown
	 * @return the list of actions, must not be <code>null</code>
	 */
	protected DockActionSource createSource( Dockable dockable ){
		return dockable.getController().listOffers( dockable );
	}

	public void changed( DockTitleEvent event ){
		// ignore
	}

	public Component getComponent(){
		return panel;
	}

	public Dockable getDockable(){
		return dockable;
	}

	public Orientation getOrientation(){
		return panel.getOrientation();
	}

	public DockTitleVersion getOrigin(){
		return origin;
	}

	public Point getPopupLocation( Point click ){
		return null;
	}

	public boolean isActive(){
		return false;
	}

	public void removeMouseInputListener( MouseInputListener listener ){
		// ignore
	}

	public void setOrientation( Orientation orientation ){
		panel.setOrientation( orientation );
	}

	public void unbind(){
		panel.set( null );
	}
}
