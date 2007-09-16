package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

/**
 * @author Janni Kovacs
 */
public class EclipseDockableDisplayer extends EclipseStackDockComponent implements DockableDisplayer {
	private DockStation station;
	
	private Dockable dockable;
	private DockController controller;
	private DockTitle title;
	private Location location;
	
	public EclipseDockableDisplayer(EclipseTheme theme, DockStation station, Dockable dockable) {
		super(theme, station);
		this.station = station;
		setDockable(dockable);
	}

	@Override
	public Dimension getMinimumSize(){
		if( dockable == null )
			return new Dimension( 10, 10 );
		else
			return dockable.getComponent().getMinimumSize();
	}
	
	@Override
	public Dimension getPreferredSize(){
		if( dockable == null )
			return new Dimension( 10, 10 );
		else
			return dockable.getComponent().getMinimumSize();
	}
	
	public void setDockable(Dockable dockable) {
		if (getDockable() != null) {
			removeAll();
		}
		this.dockable = dockable;
		if (dockable != null)
			addTab(dockable.getTitleText(), dockable.getTitleIcon(), dockable.getComponent(), dockable);
		revalidate();
	}

	public boolean titleContains( int x, int y ){
		Point point = new Point( x, y );
		point = SwingUtilities.convertPoint( this, point, this );
		for( int i = 0, n = getTabCount(); i<n; i++ ){
			Rectangle bounds = getBoundsAt( i );
			if( bounds.contains( point ))
				return true;
		}
		return false;
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

	public void setStation( DockStation station ){
		this.station = station;
	}

	public void setTitle( DockTitle title ){
		this.title = title;
	}

	public void setTitleLocation( Location location ){
		this.location = location;
	}
}
