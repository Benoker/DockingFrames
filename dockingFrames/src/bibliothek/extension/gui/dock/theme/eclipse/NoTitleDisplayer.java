package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

/**
 * @author Janni Kovacs
 */
public class NoTitleDisplayer extends JPanel implements DockableDisplayer {
	private Dockable dockable;
	private DockController controller;
	private DockStation station;
	private DockTitle title;
	private Location location;
	
	public NoTitleDisplayer( DockStation station, Dockable dockable ){
		setBorder( new EclipseBorder() );
		setLayout( new GridLayout( 1, 1, 0, 0 ) );
		setOpaque( false );
		
		setStation( station );
		setDockable( dockable );
	}

	public Component getComponent(){
		return this;
	}

	public DockController getController(){
		return controller;
	}

	public Dockable getDockable(){
		return dockable;
	}

	public DockStation getStation(){
		return station;
	}

	public DockTitle getTitle(){
		return title;
	}

	public Location getTitleLocation(){
		return location;
	}

	public void setController( DockController controller ){
		this.controller = controller;
	}

	public void setDockable( Dockable dockable ){
		this.dockable = dockable;
		removeAll();
		if( dockable != null )
			add( dockable.getComponent() );
	}

	public void setStation( DockStation station ){
		this.station = station;
	}

	public void setTitle( DockTitle title ){
		this.title = title;
	}

	public void setTitleLocation( Location location ){
		this.location = location;
	}

	public boolean titleContains( int x, int y ){
		return false;
	}
}
